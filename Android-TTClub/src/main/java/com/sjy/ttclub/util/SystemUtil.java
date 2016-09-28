/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SystemUtil {

    public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000;
    public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;
    private static boolean sEnableTransparentStatusBar = false;

    private static boolean sHasCheckTransparentStatusBar = false;

    private static int sStatusBarHeight;
    private static boolean sHasCheckStatusBarHeight;

    private static int sNaviBarHeight = -1;
    private static boolean sHasCheckNaviBarHeight;

    private static int sVerticalNaviBarHeight = -1;
    private static boolean sHasCheckVerticalNaviBarHeight;

    public static boolean isHaveKeyDownEvent = false;

    public static int systemStatusBarHeight;

    private static boolean sHasCheckedMeizuMXSeries = false;
    private static boolean sIsMeizuMXSeries = false;
    private static boolean sHasCheckedMeizuFlyemeVersion = false;
    private static boolean sIsMeizuFlyme2 = false;
    private static boolean sIsMeizuFlyme3 = false;
    private static boolean sIsMeizuFlyme4 = false;
    private static boolean sHasCheckedIfMIUISystemV5OrAbove = false;
    private static boolean sIsMIUISystemV5OrAbove = false;
    private static boolean sIsMIUISystemV5 = false;
    private static boolean sIsMIUISytemV6OrAbove = false;

    private static final int SDK_INT_MEIZU_FLYME_2 = 16;
    private static final int SDK_INT_MEIZU_FLYME_3 = 17;
    private static final int SDK_INT_MEIZU_FLYME_4 = 19;
    private static final String[] MEIZU_SMARTBAR_DEVICE_LIST = {"M040",
            "M045",};

    public static boolean isResolutionHigherThanQHD(int width, int height) {
        return Math.max(width, height) >= 960 && Math.min(width, height) >= 540;
    }

    public static boolean isResolutionHigherThanWVGA(int width, int height) {
        return Math.max(width, height) >= 800 && Math.min(width, height) >= 480;
    }

    /**
     * @return true
     */
    public static boolean checkTransparentStatusBar(Context context) {
        if (sHasCheckTransparentStatusBar) {
            return sEnableTransparentStatusBar;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            boolean isResolutionHigherThanWVGA = isResolutionHigherThanWVGA(
                    screenWidth, screenHeight);
            if (isResolutionHigherThanWVGA) {
                sEnableTransparentStatusBar = true;
            }
        } else {
            sEnableTransparentStatusBar = false;
        }
        sHasCheckTransparentStatusBar = true;
        return sEnableTransparentStatusBar;
    }

    public static boolean isTransparentStatusBarEnable() {
        return sEnableTransparentStatusBar;
    }

    public static void configTransparentStatusBar(Window window) {
        if (window != null) {
            window.setFlags(0x04000000, 0x04000000);
            if (isMIUISystemV6OrAbove()) {
                configMIUIStatusBar(window);
            }
        }
    }

    private static void configMIUIStatusBar(Window window) {
        Class clazz = window.getClass();
        try {
            int type = 1;
            int tranceFlag;
            int darkModeFlag;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);
            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (type == 0) {
                extraFlagField.invoke(window, tranceFlag, tranceFlag);//只需要状态栏透明
            } else if (type == 1) {
                extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            }
        } catch (Exception e) {

        }
    }

    public static void configTransparentStatusBar(WindowManager.LayoutParams lp) {
        if (lp != null) {
            int flags = 0x04000000;
            int mask = 0x04000000;
            lp.flags = (lp.flags & ~mask) | (flags & mask);
        }
    }

    public static int getStatusBarHeight(Context context) {
        if (sHasCheckStatusBarHeight) {
            return sStatusBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
            sHasCheckStatusBarHeight = true;
        } catch (Exception e) {
            sStatusBarHeight = guessStatusBarHeight(context);
            sHasCheckStatusBarHeight = true;
            e.printStackTrace();
        }
        return sStatusBarHeight;
    }

    private static int guessStatusBarHeight(Context context) {
        try {
            if (context != null) {
                final int statusBarHeightDP = 25;
                float density = context.getResources().getDisplayMetrics().density;
                return Math.round(density * statusBarHeightDP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNaviBarHeight(Context context) {
        if (sHasCheckNaviBarHeight) {
            return sNaviBarHeight;
        }

        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int x = (Integer) field.get(o);
            sNaviBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            final int dp = 48;
            float density = context.getResources().getDisplayMetrics().density;
            sNaviBarHeight = Math.round(density * dp);
            e.printStackTrace();
        }

        sHasCheckNaviBarHeight = true;
        return sNaviBarHeight;
    }

    public static int getVerticalNaviBarHeight(Context context) {
        if (sHasCheckVerticalNaviBarHeight) {
            return sVerticalNaviBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("navigation_bar_width");
            int x = (Integer) field.get(o);
            sVerticalNaviBarHeight = context.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e) {
            final int dp = 42;
            float density = context.getResources().getDisplayMetrics().density;
            sVerticalNaviBarHeight = Math.round(density * dp);
            e.printStackTrace();
        }
        sHasCheckVerticalNaviBarHeight = true;
        return sVerticalNaviBarHeight;
    }

    public static boolean isMeizuMXSeries() {
        if (sHasCheckedMeizuMXSeries) {
            return sIsMeizuMXSeries;
        }

        if (Build.DISPLAY.contains("Flyme")) {
            final String model = Build.MODEL;
            for (String knownModel : MEIZU_SMARTBAR_DEVICE_LIST) {
                if (knownModel.equals(model)) {
                    sIsMeizuMXSeries = true;
                    break;
                }
            }

            if (!sIsMeizuMXSeries) {
                try {
                    Class<?> buildClass = Build.class;
                    Method hasSmartBarMethod = buildClass
                            .getMethod("hasSmartBar");
                    sIsMeizuMXSeries = ((Boolean) hasSmartBarMethod
                            .invoke(null)).booleanValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        sHasCheckedMeizuMXSeries = true;
        return sIsMeizuMXSeries;
    }

    private static void checkMeizuFlymeVersion() {
        if (Build.DISPLAY.contains("Flyme")) {
            if (Build.VERSION.SDK_INT == SDK_INT_MEIZU_FLYME_2) {
                sIsMeizuFlyme2 = true;
            } else if (Build.VERSION.SDK_INT == SDK_INT_MEIZU_FLYME_3) {
                sIsMeizuFlyme3 = true;
            } else if (Build.VERSION.SDK_INT >= SDK_INT_MEIZU_FLYME_4) {
                sIsMeizuFlyme4 = true;
            }
        }
    }

    static boolean isMeizuFlyme2() {
        if (!sHasCheckedMeizuFlyemeVersion) {
            checkMeizuFlymeVersion();
            sHasCheckedMeizuFlyemeVersion = true;
        }
        return sIsMeizuFlyme2;
    }

    public static boolean isMeizuFlyme3() {
        if (!sHasCheckedMeizuFlyemeVersion) {
            checkMeizuFlymeVersion();
            sHasCheckedMeizuFlyemeVersion = true;
        }
        return sIsMeizuFlyme3;
    }

    public static boolean isMeizuFlyme4() {
        if (!sHasCheckedMeizuFlyemeVersion) {
            checkMeizuFlymeVersion();
            sHasCheckedMeizuFlyemeVersion = true;
        }
        return sIsMeizuFlyme4;
    }

    public static boolean isMIPhone() {
        return Build.MANUFACTURER.contains("Xiaomi");
    }

    public static boolean isMIBrand() {
        return "Xiaomi".equals(Build.BRAND);
    }

    public static boolean isMIUISystemV5OrAbove() {
        if (sHasCheckedIfMIUISystemV5OrAbove) {
            return sIsMIUISystemV5OrAbove;
        }

        String version = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get",
                    new Class<?>[]{String.class});
            version = (String) getMethod.invoke(classType,
                    new Object[]{"ro.miui.ui.version.name"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        sIsMIUISystemV5OrAbove = !TextUtils.isEmpty(version);
        if (StringUtils.isNotEmpty(version)) {
            version = version.trim();
            if (version.contains("V5") || version.contains("v5")
                    || version.contains("V 5") || version.contains("v 5")) {
                sIsMIUISystemV5 = true;
            } else {
                sIsMIUISytemV6OrAbove = true;
            }
        }
        sHasCheckedIfMIUISystemV5OrAbove = true;
        return sIsMIUISystemV5OrAbove;
    }

    public static boolean isMIUISystemV6OrAbove() {
        isMIUISystemV5OrAbove();
        return sIsMIUISytemV6OrAbove;
    }

    public static boolean isMIUISystemV5() {
        isMIUISystemV5OrAbove();
        return sIsMIUISystemV5;
    }

    public static boolean isHardwareAcceleratedJudgeEveryTime(Canvas canvas) {
        boolean ac = false;
        Class<Canvas> clazz = Canvas.class;
        try {
            Method method = clazz.getDeclaredMethod("isHardwareAccelerated");
            ac = (Boolean) method.invoke(canvas, new Object[]{});
        } catch (Exception e) {
            ac = false;
        }
        return ac;
    }

    public static boolean isHardwareAcceleratedJudgeEveryTime(View v) {
        boolean hardwareAcceleration = false;
        if (v != null) {
            // View.isHardwareAccelerated() is supported from version 3.0
            if (Build.VERSION.SDK_INT >= 11) {
                try {
                    if (null != v) {
                        Class<View> c = View.class;
                        Method method = c.getMethod(
                                "isHardwareAccelerated", (Class[]) null);
                        hardwareAcceleration = (Boolean) method.invoke(v,
                                (Object[]) null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hardwareAcceleration = false;
                }
            }
        }
        return hardwareAcceleration;
    }

}
