/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.util;

import com.sjy.ttclub.util.file.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class Md5Utils {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    private static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];

        int i = 0;
        for (int j = 0; i < l; i++) {
            out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = toDigits[(0xF & data[i])];
        }
        return out;
    }

    private static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        String str = null;
        MessageDigest messageDigest = null;
        if (file != null) {
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[8192];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        messageDigest.update(bArr, 0, read);
                    } else {
                        str = encodeHexString(messageDigest.digest());
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e2) {
                            }
                        }
                        return str;
                    }
                }
            } catch (FileNotFoundException e3) {
                IOUtil.safeClose(fileInputStream);
                fileInputStream = null;
            } catch (IOException e4) {
                IOUtil.safeClose(fileInputStream);
                fileInputStream = null;
            } catch (Throwable th) {
                IOUtil.safeClose(fileInputStream);
                fileInputStream = null;
            }
        }

        return str;
    }

    public static String getMD5(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes();
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bytes);
            return encodeHexString(instance.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] getMD5(InputStream inputStream) {
        byte[] bArr = null;
        if (inputStream != null) {
            MessageDigest instance;
            try {
                instance = MessageDigest.getInstance("MD5");
                if (instance != null) {
                    byte[] bArr2 = new byte[8192];
                    while (true) {
                        int read = inputStream.read(bArr2);
                        if (read != -1) {
                            instance.update(bArr2, 0, read);
                        } else {
                            bArr = instance.digest();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return bArr;
    }

    public static byte[] getMD5ByteArray(byte[] bArr) {
        try {
            if (bArr == null) {
                return null;
            }
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.reset();
            instance.update(bArr);
            return instance.digest();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMD5(byte[] bytes) {
        byte[] result = getMD5ByteArray(bytes);
        if (result == null) {
            return null;
        }

        return encodeHexString(result);
    }
}