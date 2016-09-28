/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util.file;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {


    /**
     * @param aInput
     * @param aReadLength
     * @param aBufferLength
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream aInput, int aReadLength,
                                   int aBufferLength) throws IOException {
        if (aInput == null || aReadLength <= 0)
            return null;

        byte[] sData = new byte[aReadLength];

        // aBufferLength = Math.max(aBufferLength,2048);
        if (aBufferLength <= 0) {
            aBufferLength = 2048;
        }
        int sLength = 0;
        for (int i = 0; i < aReadLength; ) {
            if (aReadLength - i < aBufferLength)
                sLength = aInput.read(sData, i, aReadLength - i);
            else
                sLength = aInput.read(sData, i, aBufferLength);

            if (sLength == -1)
                break;
            i += sLength;
        }

        return sData;
    }

    public static byte[] readByteArrayFromInputStream(InputStream aInputStream) {
        byte[] res = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            int len = 0;
            int size = 1024 * 4;
            byte[] buf = new byte[size];
            while ((len = aInputStream.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            res = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(bos);
        }

        return res;
    }

    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

}
