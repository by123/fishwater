/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.sjy.ttclub.framework.adapter.AppConst;
import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.util.file.IOUtil;
import com.sjy.ttclub.util.file.StorageHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CommonUtils {

    public static Pattern emoji = Pattern.compile(
            "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    private final static String EDGEGLOW_LEFT = "mEdgeGlowLeft";
    private final static String EDGEGLOW_TOP = "mEdgeGlowTop";
    private final static String EDGEGLOW_RIGHT = "mEdgeGlowRight";
    private final static String EDGEGLOW_BOTTOM = "mEdgeGlowBottom";

    public final static Bitmap sEmptyBitmap = Bitmap.createBitmap(1, 1,
            Bitmap.Config.RGB_565);

    public static boolean setEdgeEffectDrawable(ScrollView view,
                                                Drawable edgeDrawable, Drawable glowDrawable) {
        Class<?> viewClass = ScrollView.class;
        boolean result = true;
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_TOP,
                edgeDrawable, glowDrawable);
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_BOTTOM,
                edgeDrawable, glowDrawable);
        return result;
    }

    public static boolean setEdgeEffectDrawable(HorizontalScrollView view,
                                                Drawable edgeDrawable, Drawable glowDrawable) {
        Class<?> viewClass = HorizontalScrollView.class;
        boolean result = true;
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_LEFT,
                edgeDrawable, glowDrawable);
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_RIGHT,
                edgeDrawable, glowDrawable);
        return result;
    }

    public static boolean setEdgeEffectDrawable(AbsListView view,
                                                Drawable edgeDrawable, Drawable glowDrawable) {
        Class<?> viewClass = AbsListView.class;
        boolean result = true;
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_TOP,
                edgeDrawable, glowDrawable);
        result &= setEdgeEffectDrawable(view, viewClass, EDGEGLOW_BOTTOM,
                edgeDrawable, glowDrawable);
        return result;
    }

    private static boolean setEdgeEffectDrawable(View view, Class<?> viewClass,
                                                 String edgeName, Drawable edgeDrawable, Drawable glowDrawable) {
        try {
            Field edgeEffectField = viewClass.getDeclaredField(edgeName);
            edgeEffectField.setAccessible(true);
            Object edgeEffect = edgeEffectField.get(view);

            Class<?> edgeEffectClass = edgeEffect.getClass();

            Field mEdgeField = edgeEffectClass.getDeclaredField("mEdge");
            mEdgeField.setAccessible(true);
            mEdgeField.set(edgeEffect, edgeDrawable);

            Field mGlowField = edgeEffectClass.getDeclaredField("mGlow");
            mGlowField.setAccessible(true);
            mGlowField.set(edgeEffect, glowDrawable);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    private static final int OVER_SCROLL_NEVER = 2;

    public static boolean enableEdgeEffect(View view) {
        return setViewOverScrollMode(view, OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    public static boolean disableEdgeEffect(View view) {
        return setViewOverScrollMode(view, OVER_SCROLL_NEVER);
    }

    private static boolean setViewOverScrollMode(View view, int mode) {
        try {
            Method setOverScrollModeMethod = View.class.getMethod(
                    "setOverScrollMode", int.class);
            setOverScrollModeMethod.invoke(view, mode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int sBaseId = 10;

    public static int generateId() {
        return sBaseId++;
    }

    public static final String SNAPSHOT_PREFIX = "TMPSNAPSHOT";

    public static String getRootFolder() {
        String path = StorageHelper.getSdcardPath() + AppConst.APP_NAME;
        return path;
    }

    public static String getSnapshotFolder() {
        String snapshotFolder = StorageHelper.getSdcardPath()
                + AppConst.APP_NAME + "/Screenshot/tmp/";
        return snapshotFolder;
    }

    public static String getSnapshotPath() {
        final String snapshotFolder = getSnapshotFolder();
        final String snapshotName = SNAPSHOT_PREFIX
                + System.currentTimeMillis() + ".jpg";

        String snapshotPath = snapshotFolder + snapshotName;

        return snapshotPath;
    }

    private static void deleteSnapshotTempPicture(String currentSnapPath) {
        if (currentSnapPath == null) {
            return;
        }
        File file = new File(currentSnapPath);
        final String snapshotName = file.getName();
        final String snapshotFolder = getSnapshotFolder();

        if (!currentSnapPath.startsWith(snapshotFolder)) {
            // 如果不是默认的路径，直接忽略，避免误删除
            return;
        }

        // 为了用户sdcard空间，这里在创建新的图片时，删除旧的，遍历删除
        ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {

            @Override
            public void run() {
                FileFilter filter = new FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        String filename = file.getName();
                        // 注意不包括当前在生成的这个图片
                        if (filename != null
                                && filename.startsWith(SNAPSHOT_PREFIX)
                                && !filename.equalsIgnoreCase(snapshotName)) {
                            return true;
                        }
                        return false;
                    }
                };

                File filedir = new File(snapshotFolder);
                File[] fileList = filedir.listFiles(filter);
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        fileList[i].delete();
                    }
                }
            }
        });
    }

    public static String storePageSnapshot(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        String snapshotPath = getSnapshotPath();
        String snapshotFolder = getSnapshotFolder();

        deleteSnapshotTempPicture(snapshotPath);

        boolean success = false;
        try {
            File folder = new File(snapshotFolder);
            if (!folder.exists()) {
                boolean mkSucc = folder.mkdirs();
            }

            File file = new File(snapshotPath);
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果保存失败，不进行截图
        if (!success) {
            snapshotPath = null;
        }
        return snapshotPath;
    }

    public static String storeTempImage(InputStream in) {
        if (in == null) {
            return null;
        }

        String snapshotPath = getSnapshotPath();
        String snapshotFolder = getSnapshotFolder();

        deleteSnapshotTempPicture(snapshotPath);

        OutputStream out = null;
        boolean success = false;
        try {
            File folder = new File(snapshotFolder);
            if (!folder.exists()) {
                boolean mkSucc = folder.mkdirs();
            }
            out = new FileOutputStream(snapshotPath);
            byte[] buffer = new byte[8096];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(in);
            IOUtil.safeClose(out);
        }

        if (!success) {
            snapshotPath = null;
        }
        return snapshotPath;
    }

    public static void notifyGralleryRefresh(final Context context,
                                             final String fileName) {
        if (context == null) {
            return;
        }
        ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {

            @Override
            public void run() {

                // 最后通知图库更新
                Uri uri = Uri.fromFile(new File(fileName));
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                try {
                    context.sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * get media file absolute path by uri
     *
     * @param context
     * @param uri
     */
    @SuppressLint("NewApi")
    public static String getMediaAbsolutePath(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        StringUtils.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri
                .getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE
                .equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    private static Runnable sGcRunnable = new Runnable() {

        @Override
        public void run() {
            System.gc();
        }
    };

    public static void gc() {
        ThreadManager.removeRunnable(sGcRunnable);
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, sGcRunnable, 1500);
    }

    public static void cancelGc() {
        ThreadManager.removeRunnable(sGcRunnable);
    }

    public static String getPackageName(Context aContext) {
        return aContext.getPackageName();
    }

    public static PackageInfo getPackageInfo(Context context,
                                             String packageName, int flags) {

        try {
            PackageManager packageManager = context.getApplicationContext()
                    .getPackageManager();
            PackageInfo info = packageManager
                    .getPackageInfo(packageName, flags);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    private static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 清除缓存
     */
    public static void clearAllCache(final Context context, final Runnable callback) {
        ThreadManager.execute( new Runnable() {
            @Override
            public void run() {
                deleteCacheFile(context.getCacheDir());
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    deleteCacheFile(context.getExternalCacheDir());
                }
            }
        }, callback);
    }

    private static void deleteCacheFile(File cacheDir) {
        if (cacheDir != null && cacheDir.isDirectory()) {
            try {
                File[] children = cacheDir.listFiles();
                for (int i = 0; i < children.length; i++) {
                    FileUtils.delete(children[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
