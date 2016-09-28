/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ReflectionHelper;
import com.sjy.ttclub.util.SystemUtil;

public class DeviceManager {
    private static final int CLEAR_LAYOUT_NO_LIMITS_FLAGS_DURING = 1500;

    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 0x00000002;
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 0x00000004;
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 0x00000100;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 0x00000200;
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 0x00000400;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 0x00000800;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    private static DeviceManager sDeviceManager;
    private Activity mActivity;

    private DeviceManager(Activity activity) {
        mActivity = activity;
    }

    public static DeviceManager getInstance() {
        if (sDeviceManager == null) {
            throw new IllegalStateException("should init first");
        }
        return sDeviceManager;
    }

    /* package */
    static void init(Activity activity) {
        sDeviceManager = new DeviceManager(activity);
    }

    public void destroy() {

    }

    private static boolean isSysStatusBarShowing(Context context) {
        if (context instanceof Activity) {
            int val = ((Activity) context).getWindow().getAttributes().flags;
            return (val & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            return false;
        }
    }

    public boolean isSysStatusBarShowing() {
        int val = mActivity.getWindow().getAttributes().flags;
        return (val & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public void showSysStatusBar() {
        if (isSysStatusBarShowing()) {
            return;
        }

        HardwareUtil.windowHeight = HardwareUtil.screenHeight
                - SystemUtil.systemStatusBarHeight;
        NotificationCenter.getInstance().notify(
                Notification.obtain(NotificationDef.N_FULL_SCREEN_MODE_CHANGE));

        final Window window = mActivity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        if (hasWindowFlag(window,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) {
            clearLayoutNoLimitsFlag();
        }
    }

    public void hideSysStatusBar() {
        if (!isSysStatusBarShowing()) {
            return;
        }

        NotificationCenter.getInstance().notify(
                Notification.obtain(NotificationDef.N_FULL_SCREEN_MODE_CHANGE));

        final Window window = mActivity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (SystemUtil.systemStatusBarHeight != 0
                || SystemUtil.isTransparentStatusBarEnable()) {
            addLayoutNoLimitsFlagTemporary();
        }
    }

    public void onPause() {
        if (hasWindowFlag(mActivity.getWindow(),
                WindowManager.LayoutParams.FLAG_FULLSCREEN)) {
            addLayoutNoLimitsFlag();
        }
    }

    public void onResume() {
        if (hasWindowFlag(mActivity.getWindow(),
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) {
            clearLayoutNoLimitsFlagDelay();
        }
    }

    private void addLayoutNoLimitsFlagTemporary() {
        addLayoutNoLimitsFlag();
        clearLayoutNoLimitsFlagDelay();
    }

    private void addLayoutNoLimitsFlag() {
        if (!DeviceManager.isDeviceNotCallAddLayoutNoLimitsFlag()) {
            final Window window = mActivity.getWindow();
            if (hasWindowFlag(window,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) {
                window.getDecorView().removeCallbacks(
                        mClearLayoutNoLimitsFlagRunnable);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    private void clearLayoutNoLimitsFlag() {
        clearLayoutNoLimitsFlag(false);
    }

    private void clearLayoutNoLimitsFlagDelay() {
        clearLayoutNoLimitsFlag(true);
    }

    private void clearLayoutNoLimitsFlag(boolean delay) {
        final Window window = mActivity.getWindow();
        final View decorView = window.getDecorView();
        decorView.removeCallbacks(mClearLayoutNoLimitsFlagRunnable);
        if (delay) {
            decorView.postDelayed(mClearLayoutNoLimitsFlagRunnable,
                    CLEAR_LAYOUT_NO_LIMITS_FLAGS_DURING);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void removeClearLayoutNoLimitsFlagRunnable() {
        final Window window = mActivity.getWindow();
        final View decorView = window.getDecorView();
        decorView.removeCallbacks(mClearLayoutNoLimitsFlagRunnable);
    }

    public boolean isSystemFullScreen() {
        if (mActivity == null) {
            return false;
        }
        boolean isSystemFullscreen = false;
        Window window = mActivity.getWindow();
        if (window != null && window.getAttributes() != null) {
            int flag = window.getAttributes().flags;
            if ((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                isSystemFullscreen = true;
            } else if ((flag & WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) == WindowManager.LayoutParams
                    .FLAG_FORCE_NOT_FULLSCREEN) {
                isSystemFullscreen = false;
            }
        }
        return isSystemFullscreen;
    }

    public int getWindowPaddingTop() {
        if (SystemUtil.isTransparentStatusBarEnable()
                && isSysStatusBarShowing()) {
            return SystemUtil.getStatusBarHeight(mActivity);
        } else {
            return SystemUtil.systemStatusBarHeight;
        }
    }

    public int getWindowContentHeight() {
        if (SystemUtil.isTransparentStatusBarEnable()
                && isSysStatusBarShowing()) {
            return HardwareUtil.windowHeight
                    - SystemUtil.getStatusBarHeight(mActivity);
        } else {
            return HardwareUtil.windowHeight;
        }
    }

    private static final boolean hasWindowFlag(Window window, int flag) {
        final WindowManager.LayoutParams lp = window.getAttributes();
        return lp != null && (lp.flags & flag) != 0;
    }

    private final Runnable mClearLayoutNoLimitsFlagRunnable = new Runnable() {
        @Override
        public void run() {
            clearLayoutNoLimitsFlag();
        }
    };

    public void setSysOrientation(int OrientationInfo) {
        // eg. "ActivityInfo.SCREEN_ORIENTATION_PORTRAIT" or
        // "ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE"
        mActivity.setRequestedOrientation(OrientationInfo);
    }

    public void lockScreenOrientation() {
        boolean handled = false;
        try {
            int rotation = mActivity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            if (rotation == 3) {
                int orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                orientation = ActivityInfo.class.getField(
                        "SCREEN_ORIENTATION_REVERSE_LANDSCAPE").getInt(null);
                setSysOrientation(orientation);
                handled = true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            handled = false;
        }

        if (!handled) {
            int ori = mActivity.getResources().getConfiguration().orientation;
            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                setSysOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                setSysOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public void resetSysDefaultOrientation() {
        mActivity
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public boolean isInputMethodShow() {
        InputMethodManager imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showInputMethod(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideInputMethod() {
        hideInputMethod(mActivity.getWindow().getDecorView());
    }

    public void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isColorDepth16Device() {
        return isColorDeth16Device(mActivity);
    }

    public static boolean isColorDeth16Device(Context context) {
        Display screenRes = ((Activity) context).getWindowManager()
                .getDefaultDisplay();
        boolean boolVal = (screenRes.getPixelFormat() == PixelFormat.RGB_565)
                || (screenRes.getPixelFormat() == PixelFormat.RGB_888)
                || (screenRes.getPixelFormat() == PixelFormat.RGBX_8888)
                || (screenRes.getPixelFormat() == PixelFormat.RGBA_5551)
                || (screenRes.getPixelFormat() == PixelFormat.RGBA_4444);

        return boolVal;
    }


    public void setWindowBgTransparent() {
        mActivity.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
    }


    public void setWindowBgNotTransparent() {
        mActivity.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.BLACK));
    }

    public void pushAndroidWindow(View view, ViewGroup.LayoutParams lp) {
        if (view != null && view.getParent() == null) {
            decorateWindowLayoutParams(lp);
            final WindowManager windowManager = mActivity.getWindowManager();
            try {
                windowManager.addView(view, lp);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void popAndroidWindow(View view) {
        if (view != null && view.getParent() != null) {
            final WindowManager windowManager = mActivity.getWindowManager();
            windowManager.removeView(view);
        }
    }

    public void updateAndroidWindowLP(View view, ViewGroup.LayoutParams lp) {
        if (view != null && view.getParent() != null) {
            decorateWindowLayoutParams(lp);
            final WindowManager windowManager = mActivity.getWindowManager();
            windowManager.updateViewLayout(view, lp);
        }
    }

    public void decorateWindowLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof WindowManager.LayoutParams) {
            final WindowManager.LayoutParams winLP = (WindowManager.LayoutParams) lp;
            if (winLP.type >= WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW
                    && winLP.type <= WindowManager.LayoutParams.LAST_APPLICATION_WINDOW) {
                winLP.token = null;
            }

            if (winLP.type == WindowManager.LayoutParams.TYPE_APPLICATION) {
                if (!isSysStatusBarShowing(mActivity)) {
                    winLP.flags &= ~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                    winLP.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                } else {
                    winLP.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    winLP.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                }
            }

            if (SystemUtil.checkTransparentStatusBar(mActivity)) {
                SystemUtil
                        .configTransparentStatusBar((WindowManager.LayoutParams) lp);
            }
        }
    }

    public void hideNavigationBar(boolean immersiveSticky) {
        Window win = mActivity.getWindow();

        if (win == null) {
            return;
        }

        View view = win.getDecorView();
        int visibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
                | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN
                | SYSTEM_UI_FLAG_IMMERSIVE;

        if (immersiveSticky) {
            visibility |= SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        try {
            ReflectionHelper.invokeObjectMethod(view, "setSystemUiVisibility",
                    new Class[]{int.class}, new Object[]{visibility});
        } catch (Exception e) {
        }

    }

    public void hideNavigationBar() {
        hideNavigationBar(false);
    }

    public void showNavigationBar(boolean immerge) {
        Window win = mActivity.getWindow();

        if (win == null) {
            return;
        }

        View view = win.getDecorView();
        int visibility = 0;

        if (immerge) {
            visibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }

        try {
            ReflectionHelper.invokeObjectMethod(view, "setSystemUiVisibility",
                    new Class[]{int.class}, new Object[]{visibility});
        } catch (Exception e) {
        }

    }

    // gHasCheckDeviceNotCallAddLayoutNoLimitsFlagTemporary
    private static boolean gHasCheckDeviceNotCallAddLayoutNoLimitsFlagTemporary = false;
    // gIsDeviceNotCallAddLayoutNoLimitsFlagTemporary
    private static boolean gIsDeviceNotCallAddLayoutNoLimitsFlagTemporary = false;

    private static final String[] gModelListOfDeviceNotCallAddLayoutNoLimitsFlagTemporary = {
            "GT-N7100", "GT-9300", "GT-I9300"};

    // SAFE_STATIC_VAR
    private static boolean gHasCheckDeviceNeedClearBufferBeforeDraw = false;
    // SAFE_STATIC_VAR
    private static boolean gIsDeviceNeedClearBufferBeforeDraw = false;

    private static final String[] gModelListOfDeviceNeedClearBufferBeforeDraw = {"GT-N7100",};

    private static final String[] gBrandListOfDeviceNeedClearBufferBeforeDraw = {
            "Xiaomi", "Meizu"};

    public static boolean isDeviceNeedClearBufferBeforeDraw() {
        if (gHasCheckDeviceNeedClearBufferBeforeDraw)
            return gIsDeviceNeedClearBufferBeforeDraw;

        gHasCheckDeviceNeedClearBufferBeforeDraw = true;
        String model = Build.MODEL;
        if (model != null && model.length() > 0) {
            for (String name : gModelListOfDeviceNeedClearBufferBeforeDraw) {
                if (name != null && name.contains(model)) {
                    gIsDeviceNeedClearBufferBeforeDraw = true;
                    break;
                }
            }
        }

        if (!gIsDeviceNeedClearBufferBeforeDraw) {
            String brand = Build.BRAND;
            if (brand != null && brand.length() > 0) {
                for (String name : gBrandListOfDeviceNeedClearBufferBeforeDraw) {
                    if (name != null && name.contains(brand)) {
                        gIsDeviceNeedClearBufferBeforeDraw = true;
                        break;
                    }
                }
            }
        }

        return gIsDeviceNeedClearBufferBeforeDraw;
    }

    public static boolean isDeviceNotCallAddLayoutNoLimitsFlag() {
        if (gHasCheckDeviceNotCallAddLayoutNoLimitsFlagTemporary) {
            return gIsDeviceNotCallAddLayoutNoLimitsFlagTemporary;
        }

        gHasCheckDeviceNotCallAddLayoutNoLimitsFlagTemporary = true;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        if (version == 16 && model != null && model.length() > 0) {// 16 means
            // android
            // 4.1.x
            for (String name : gModelListOfDeviceNotCallAddLayoutNoLimitsFlagTemporary) {
                if (name != null && name.equalsIgnoreCase(model.trim())) {
                    gIsDeviceNotCallAddLayoutNoLimitsFlagTemporary = true;
                    break;
                }
            }
        }

        return gIsDeviceNotCallAddLayoutNoLimitsFlagTemporary;
    }

}
