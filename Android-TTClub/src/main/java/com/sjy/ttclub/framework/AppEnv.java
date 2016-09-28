/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.framework;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;

import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.SystemUtil;


public class AppEnv {
    private static AppEnv sAppEnv;

    private static Context sApplicationContext;
    private Activity mActivity;
    private WindowManager mWindowManager;
    private boolean mIsForeground = true;

    private AppEnv(Activity activity) {
        mActivity = activity;
        MsgDispatcher.init(activity);
        NotificationCenter.init(activity);
        DeviceManager.init(activity);
        AppSecurityManager.init(activity);
        configActivity(activity);

        Display d = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        HardwareUtil.screenWidth = dm.widthPixels;
        HardwareUtil.screenHeight = dm.heightPixels;
        HardwareUtil.windowWidth = dm.widthPixels;
        HardwareUtil.windowHeight = dm.heightPixels;

        HardwareUtil.density = dm.density;
        HardwareUtil.densityDpi = dm.densityDpi;

        mWindowManager = new WindowManager(activity);

    }

    public static void init(Activity activity) {
        sAppEnv = new AppEnv(activity);
    }

    public void destroy() {
        MsgDispatcher.getInstance().destroy();
        NotificationCenter.getInstance().destroy();
        DeviceManager.getInstance().destroy();
        sAppEnv = null;
    }

    public static AppEnv getInstance() {
        return sAppEnv;
    }

    private void configActivity(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = activity.getWindow();
        final int FLAG_HARDWARE_ACCELERATED = 0x01000000;
        window.setFlags(FLAG_HARDWARE_ACCELERATED, FLAG_HARDWARE_ACCELERATED);
        window.setFormat(PixelFormat.RGBA_8888);

        if (SystemUtil.checkTransparentStatusBar(activity)) {
            SystemUtil.configTransparentStatusBar(window);
        }

        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);

    }

    /* package */WindowManager getWindowManager() {
        return mWindowManager;
    }

    public Activity getMainActivity() {
        return mActivity;
    }

    public Context getMainContext() {
        return mActivity;
    }

    public static void setApplicationContext(Context context){
        sApplicationContext = context;
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    public boolean isForeground() {
        return mIsForeground;
    }

    /* package */void setForeground(boolean isForeground) {
        mIsForeground = isForeground;
    }

}
