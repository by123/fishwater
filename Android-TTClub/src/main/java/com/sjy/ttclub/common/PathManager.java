/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.common;

import android.content.Context;
import android.os.Environment;

import com.sjy.ttclub.util.file.StorageHelper;

import java.io.File;

/**
 * Created by linhz on 2015/11/19.
 * Email: linhaizhong@ta2she.com
 */
public class PathManager {
    public static final String EXT_DATA_DIR = "TTClub/";
    public static final String IMAGE_FOLDER = "images/";
    public static final String SPLASH_FOLDER = "/splash/";
    public static final String TEMP_PHOTO_FOLDER = "tempPhoto/";
    private static PathManager sInstance;
    private Context mContext;
    private String mExtCachePath;
    private String mCachePath;
    private String mInnerCachePath;
    private String mDataPath;
    private String mExtDataPath;

    private PathManager(Context context) {
        mContext = context;
        initPath();
    }

    public static void init(Context context) {
        sInstance = new PathManager(context);
    }

    private void initPath() {
        mDataPath = mContext.getApplicationInfo().dataDir;
        mExtDataPath = StorageHelper.getSdcardPath() + EXT_DATA_DIR;

        initCachePath();
    }

    private void initCachePath() {
        File appCacheDir = getExternalCacheDir();
        if (appCacheDir == null) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            File extCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache");
            mExtCachePath = extCacheDir.getAbsolutePath();
        } else {
            mExtCachePath = appCacheDir.getAbsolutePath();
        }

        if (appCacheDir == null) {
            appCacheDir = mContext.getCacheDir();
            if (appCacheDir != null) {
                if (!appCacheDir.exists()) {
                    if (!appCacheDir.mkdirs()) {
                        appCacheDir = null;
                    }
                }
            }
            if (appCacheDir == null) {
                String cacheDirPath = "/data/data/" + mContext.getPackageName() + "/cache/";
                appCacheDir = new File(cacheDirPath);
                if (!appCacheDir.exists()) {
                    appCacheDir.mkdirs();
                }
            }
            mInnerCachePath = appCacheDir.getAbsolutePath();
        }

        mCachePath = appCacheDir.getAbsolutePath();
    }

    private File getExternalCacheDir() {
        File appCacheDir = mContext.getExternalCacheDir();
        if (appCacheDir != null) {
            if (!appCacheDir.exists()) {
                if (!appCacheDir.mkdirs()) {
                    appCacheDir = null;
                }
            }
        }
        if (appCacheDir == null) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            appCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache");
            if (!appCacheDir.exists()) {
                if (!appCacheDir.mkdirs()) {
                    appCacheDir = null;
                }
            }
        }

        return appCacheDir;
    }

    public static PathManager getInstance() {
        return sInstance;
    }

    public String getExtCachePath() {
        return mExtCachePath;
    }

    public String getExtDataPath() {
        return mExtDataPath;
    }

    public String getExtImagePath() {
        return getExtDataPath() + IMAGE_FOLDER;
    }

    public String getSplashPath() {
        return getDataPath() + SPLASH_FOLDER;
    }

    public String getExtTempPhotoPath() {
        return getExtDataPath() + TEMP_PHOTO_FOLDER;
    }

    /**
     * 返回cache目录,可能是external cache,或inner cache
     * 优先级:external cache,如果不存在,使用inner cache
     * 所以,大的数据不要使用此cache路径,使用xt data path
     *
     * @return
     */
    public String getCachePath() {
        return mCachePath;
    }

    public String getInnerCachePath() {
        return mInnerCachePath;
    }

    public String getDataPath() {
        return mDataPath;
    }
}
