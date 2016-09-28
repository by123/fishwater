/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.umeng;

import android.app.Activity;
import android.content.Context;

import com.lsym.ttclub.BuildConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * Created by linhz on 2015/10/28.
 * Email: linhaizhong@ta2she.com
 */
public class UmengManager {
    private static UmengManager sInstance;
    private Context mContext;
    private Activity mActivity;


    private UmengManager(Context context) {
        mContext = context;
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
            MobclickAgent.setCatchUncaughtExceptions(false);
        }
        UmengUpdateAgent.setUpdateOnlyWifi(false);
    }

    public static void init(Context context) {
        sInstance = new UmengManager(context);
    }

    public static UmengManager getInstance() {
        return sInstance;
    }

    /**
     * 在所有的Activity 的onCreate 函数添加
     */
    public void onAppStart() {
        PushAgent.getInstance(mContext).onAppStart();
    }

    public void configPlatforms(Activity activity) {
        mActivity = activity;
    }


    /**
     * 检测版本
     */
    public void startCheckUpdate(final boolean isManual, final UmengVersionUpdateHelper.CheckVersionUpdateCallBack
            callBack) {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                        callBack.callBackYes(updateInfo);
                        break;
                    case UpdateStatus.NoneWifi:
                        break;
                    case UpdateStatus.Timeout:
                        if (isManual) {
                            callBack.callBackTimeOut();
                        } else {
                            callBack.callBackAuto();
                        }
                        break;
                    case UpdateStatus.No:
                        if (isManual) {
                            callBack.callBackNo();
                        } else {
                            callBack.callBackAuto();
                        }
                        break;
                }
            }
        });
        UmengUpdateAgent.update(mContext);
    }

}
