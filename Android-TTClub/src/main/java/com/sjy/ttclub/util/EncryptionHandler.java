/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.util;

import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.util.file.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;


public class EncryptionHandler {

    public static final int MAX_FILE_SIZE = 1024 * 1024 * 8; // 最大512K

    public final static int[] LOCAL_KEY = { 126, 147, 115, 241, 101, 198, 215,
            134 };
    public final static int[] COMMON_KEY = { 238, 185, 233, 179, 129, 142, 151,
            167 };

    public static String decodeStrFromInputStream(InputStream is) {
        try {
            byte[] buffer = new byte[is.available()];

            is.read(buffer);
            is.close();

            byte[] decBuf = decode(buffer, LOCAL_KEY);

            if (decBuf != null && decBuf.length >= 1) {
                int len = decBuf.length;
                // strip last "\n" byte
                return ((byte) decBuf[len - 1] == 10 ? new String(decBuf, 0,
                        len - 1, "UTF-8") : new String(decBuf, "UTF-8"));
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeStrFromFilePath(String strFn) {
        File file = new File(strFn);

        if (!file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];

            fis.read(buffer);
            fis.close();

            byte[] decBuf = decode(buffer, LOCAL_KEY);

            if (decBuf != null && decBuf.length >= 1) {
                int len = decBuf.length;
                // strip last "\n" byte
                return ((byte) decBuf[len - 1] == 10 ? new String(decBuf, 0,
                        len - 1) : new String(decBuf));
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.safeClose(fis);
        }
    }

    public static boolean decodeSmallFile(final String filePath,
            final String newFilePathName, boolean zipFlag) {
        if (filePath == null || filePath == "" || newFilePathName == null
                || newFilePathName == "")
            return false;

        File file = new File(filePath);
        if (!file.exists())
            return false;
        if (file.length() > MAX_FILE_SIZE)
            return false;

        boolean needReplaceFile = false;
        String tmpPath = newFilePathName;
        if (tmpPath.equals(filePath)) {
            tmpPath = newFilePathName + ".tmp";
            needReplaceFile = true;
        }

        File tmpFile = new File(tmpPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[(int) file.length()];

            fis.read(buffer);
            fis.close();

            byte[] decodedBuf = decode(buffer, LOCAL_KEY);
            if (decodedBuf == null || decodedBuf.length < 0) {
                fos.close();
                return false;
            }
            byte[] writeBuffer = decodedBuf;
            if (zipFlag) {
                writeBuffer = FileUtils.unZipData(decodedBuf);
            }

            fos.write(writeBuffer);
            fos.flush();
            fos.close();

            // rename file
            if (needReplaceFile) {
                file.delete();
                tmpFile.renameTo(file);
            }

        } catch (Exception e) {
            return false;
        } finally {
            IOUtil.safeClose(fis);
            IOUtil.safeClose(fos);
        }
        return true;
    }

    public static boolean encodeSmallFile(final String filePath,
            final String zipFilePath, boolean zipFlag) {
        if (filePath == null || filePath == "" || zipFilePath == null
                || zipFilePath == "")
            return false;

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        if (file.length() > MAX_FILE_SIZE) {
            return false;
        }

        boolean needReplaceFile = false;
        String tmpPath = zipFilePath;
        if (tmpPath.equals(filePath)) {
            tmpPath = zipFilePath + ".tmp";
            needReplaceFile = true;
        }

        File tmpFile = new File(tmpPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[(int) file.length()];

            fis.read(buffer);
            fis.close();

            byte[] encodedBuf = buffer;

            if (zipFlag) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(baos);
                gos.write(buffer);

                baos.flush();
                baos.close();
                gos.close();

                encodedBuf = baos.toByteArray();
                if (encodedBuf == null || encodedBuf.length <= 0) {
                    fos.close();
                    return false;
                }
            }

            byte[] writeBuffer = encode(encodedBuf, LOCAL_KEY);
            if (writeBuffer == null || writeBuffer.length < 0) {
                fos.close();
                return false;
            }

            fos.write(writeBuffer);
            fos.flush();
            fos.close();

            // rename file
            if (needReplaceFile) {
                file.delete();
                tmpFile.renameTo(file);
            }

        } catch (Exception e) {
            return false;
        } finally {
            IOUtil.safeClose(fis);
            IOUtil.safeClose(fos);
        }
        return true;
    }

    public static String decode(byte[] aEncodeData, final int[] aKey,
            String aCharsetName, int offset) {
        byte[] decodeBytes = decode(aEncodeData, offset, aKey);
        if (decodeBytes != null) {
            String decodedStr = null;
            try {
                decodedStr = new String(decodeBytes, aCharsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return decodedStr;
        }

        return null;
    }

    public static byte[] decode(byte[] srcArray, final int[] maskArray) {
        return decode(srcArray, 0, maskArray);
    }

    public static byte[] decode(byte[] srcArray, int offset,
            final int[] maskArray) {
        if (offset < 0 || srcArray == null || srcArray.length - offset < 2
                || maskArray == null || maskArray.length != 8)
            return null;

        byte mask = 0;
        byte a = 0;
        byte b = 0;

        int srcLen = srcArray.length - 2 - offset;
        byte[] desBuffer = null;

        try {
            desBuffer = new byte[srcLen];
        } catch (Exception e) {
            return null;
        }

        for (int i = 0; i < srcLen; i++) {
            a = srcArray[i + offset];
            b = (byte) (a ^ maskArray[i % 8]);
            desBuffer[i] = (byte) b;
            mask = (byte) (mask ^ b);
        }

        if (srcArray[srcLen + offset] == (byte) (0xFF & (mask ^ maskArray[0]))
                && srcArray[srcLen + 1 + offset] == (byte) (0xFF & (mask ^ maskArray[1]))) {
            return desBuffer;
        } else
            return null;
    }

    public static byte[] encode(byte[] srcArray, final int[] maskArray) {

        if (srcArray == null || maskArray == null || maskArray.length != 8)
            return null;

        byte mask = 0;
        byte a = 0;
        byte b = 0;

        int srcLen = srcArray.length;
        byte[] desBuffer = null;

        try {
            desBuffer = new byte[srcLen + 2];
        } catch (Exception e) {
            return null;
        }

        for (int i = 0; i < srcLen; i++) {
            a = srcArray[i];
            b = (byte) (a ^ maskArray[i % 8]);
            desBuffer[i] = (byte) b;
            mask = (byte) (mask ^ a);
        }

        desBuffer[srcLen] = (byte) (mask ^ maskArray[0]);
        desBuffer[srcLen + 1] = (byte) (mask ^ maskArray[1]);

        return desBuffer;
    }

    public static String decode(byte[] aEncodeData, final int[] aKey,
            String aCharsetName) {
        return decode(aEncodeData, aKey, aCharsetName, 0);
    }

}
