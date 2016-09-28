/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.framework.AppEnv;
import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.util.file.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class BitmapUtil {

    public static final int THUMBNAIL_SIZE = 150;
    private static final int GET_IMAGE_TIMEOUT = 5000;

    private static final int LIMIT_MAX_WIDTH = 720;
    private static final int LIMIT_MAX_HEIGHT = 1280;

    private static InputStream getInputStream(Context context, String name) {
        try {
            return context.getAssets().open(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int[] getBitmapSize(String filePath) {
        boolean isAsset = FileUtils.isAssetsFile(filePath);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        if (isAsset) {
            InputStream is = null;
            try {
                is = getInputStream(AppEnv.getApplicationContext(), filePath);
                BitmapFactory.decodeStream(is, null, opts);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    IOUtil.safeClose(is);
                }
            }
        } else {
            try {
                BitmapFactory.decodeFile(filePath, opts);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        int[] size = new int[2];
        size[0] = opts.outWidth;
        size[1] = opts.outHeight;
        return size;
    }

    public static Bitmap createBitmapThumbnail(String filePath) {
        int width = Math.min(720, HardwareUtil.screenWidth);
        int height = Math.min(1280, HardwareUtil.screenHeight);
        return createBitmapThumbnail(filePath, width, height);
    }

    public static Bitmap createBitmapThumbnail(String filePath, int width, int height) {
        return createBitmapThumbnail(filePath, width, height, false);
    }

    public static Bitmap createBitmapThumbnail(String filePath, int width, int height, boolean scale) {
        return createBitmapThumbnail(filePath, width, height, false, scale);
    }

    public static Bitmap createBitmapThumbnail(String filePath, int width, int height, boolean limitSize,
                                               boolean scale) {
        if(filePath == null){
            return null;
        }
        Bitmap bitmap = null;
        InputStream is = null;
        boolean isAsset = FileUtils.isAssetsFile(filePath);
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            if (!isAsset) {
                is = new FileInputStream(filePath);
            } else {
                is = getInputStream(AppEnv.getApplicationContext(), filePath);
            }

            BitmapFactory.decodeStream(is, null, opts);

            IOUtil.safeClose(is);
            // 这里不能使用is.reset，因为部分手机有兼容问题
            if (!isAsset) {
                is = new FileInputStream(filePath);
            } else {
                is = getInputStream(AppEnv.getApplicationContext(), filePath);
            }
            if (limitSize) {
                int[] size = correctLimitSize(width, height);
                width = size[0];
                height = size[1];
            }
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(is, null, opts);

            if (bitmap != null && scale) {
                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                        height, true);
                bitmap.recycle();
                bitmap = newBitmap;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(is);
        }
        return bitmap;
    }

    public static Bitmap createBitmapThumbnail(int resId,
                                               int width, int height) {
        return createBitmapThumbnail(resId, width, height, false);
    }

    public static Bitmap createBitmapThumbnail(int resId,
                                               int width, int height, boolean limitSize) {
        if (resId == 0) {
            return null;
        }
        Context context = AppEnv.getApplicationContext();
        Resources res = context.getResources();
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, opts);

            if (limitSize) {
                int[] size = correctLimitSize(width, height);
                width = size[0];
                height = size[1];
            }
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(res, resId, opts);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static int[] correctLimitSize(int desWidth, int desHeight) {
        int[] result = new int[2];
        result[0] = desWidth;
        result[1] = desHeight;
        float radio = 1.0f * desHeight / desWidth;
        if (radio >= 1) {
            if (desWidth > LIMIT_MAX_WIDTH) {
                result[0] = LIMIT_MAX_WIDTH;
                result[1] = (int) (result[0] * radio);
            }
        } else {
            if (desHeight > LIMIT_MAX_WIDTH) {
                result[1] = LIMIT_MAX_WIDTH;
                result[0] = (int) (result[1] / radio);
            }
        }
        return result;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 获取圆形图片
     *
     * @param bitmap
     * @param size   圆的直径
     * @return
     */
    public static Bitmap getCroppedRoundBitmap(Bitmap bitmap, int size) {
        if (bitmap == null || bitmap.isRecycled() || size < 0) {
            return null;
        }

        Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        int x = 0, y = 0;
        if (bmpHeight > bmpWidth) {
            // 高大于宽
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
        } else if (bmpHeight < bmpWidth) {
            // 宽大于高
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
        }

        Paint paint = new Paint();
        Rect srcRect = new Rect(x, y, bmpWidth, bmpHeight);
        Rect desRect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, desRect, paint);

        return output;
    }


    public static void compressBitmaps(final ArrayList<String> srcList, final CompressBitmapCallback callback) {
        if (callback == null) {
            return;
        }
        if (srcList.isEmpty()) {
            callback.onCompressFinished(null);
        }
        final ArrayList<String> resultList = new ArrayList<String>();
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                for (String path : srcList) {
                    String resultPath = compressImage(path);
                    if (resultPath != null) {
                        resultList.add(resultPath);
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                callback.onCompressFinished(resultList);
            }
        });
    }

    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 720;

    private static String compressImage(String srcPath) {
        String path = PathManager.getInstance().getExtTempPhotoPath() + System.currentTimeMillis() + ".jpg";
        int width = Math.min(HardwareUtil.screenWidth, MAX_WIDTH);
        int height = Math.min(HardwareUtil.screenHeight, MAX_HEIGHT);
        boolean success = false;
        try {
            Bitmap bitmap = BitmapUtil.createBitmapThumbnail(srcPath, width, height);
            if (bitmap != null) {
                File file = new File(path);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                    out.flush();
                    out.close();
                    success = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!success) {
            path = null;
        }
        return path;
    }

    public static void deleteTempImages() {
        String path = PathManager.getInstance().getExtTempPhotoPath();
        if (path == null) {
            return;
        }
        FileUtils.delete(new File(path), false);
    }


    public interface CompressBitmapCallback {
        void onCompressFinished(ArrayList<String> desList);
    }

}
