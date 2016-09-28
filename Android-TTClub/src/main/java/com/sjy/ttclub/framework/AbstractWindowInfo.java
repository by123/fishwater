/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.view.animation.Animation;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.AbstractWindow.WindowLayerType;

public class AbstractWindowInfo {

    /**
     * if mOptBackWindow is true, when this window is attached, the window behind
     * will be GONE
     */
    private boolean mOptBackWindow = true;

    /**
     * if this flag is true && SDK > 3.0 && resolution > QHD(960*540) all the
     * elements in the window will be rendered using OpenGL.
     */
    private boolean mEnableHardwareAcceleration = false;


    private boolean mEnableSwipeGesture = true;

    private boolean mIsAnimating = false;

    private WindowLayerType mUseLayerType = WindowLayerType.ONLY_USE_BASE_LAYER;

    private int mWindowType = AbstractWindow.WINDOW_TYPE_NOTSPECIFIED;

    private int mLaunchMode = AbstractWindow.LAUNCH_MODE_SINGLE_TOP;

    private boolean mIsPopBackWinAfterPush = false;

    private Animation mPushAnimation;

    private Animation mUnderPushAnimation;

    private Animation mPopAnimation;

    private Animation mUnderPopAnimation;

    public void setOptBackWindow(boolean optBackWindow) {
        mOptBackWindow = optBackWindow;
    }

    public boolean isOptBackWindow() {
        return mOptBackWindow;
    }

    public void setUseLayerType(WindowLayerType type) {
        mUseLayerType = type;
    }

    public WindowLayerType getUseLayerType() {
        return mUseLayerType;
    }

    public int getWindowType() {
        return mWindowType;
    }

    public void setWindowType(int type) {
        mWindowType = type;
    }

    public boolean isEnableHardwareAcceleration() {
        return mEnableHardwareAcceleration;
    }

    public void setEnableHardwareAcceleration(boolean enableHardwareAcceleration) {
        mEnableHardwareAcceleration = enableHardwareAcceleration;
    }

    public void setEnableSwipeGesture(boolean enable) {
        mEnableSwipeGesture = enable;
    }

    public boolean isEnableSwipeGesture() {
        return mEnableSwipeGesture;
    }

    public void setIsAnimating(boolean isAnimating) {
        mIsAnimating = isAnimating;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public Animation getPushAnimation() {
        return mPushAnimation;
    }

    public void setPushAnimation(Animation pushAnimation) {
        mPushAnimation = pushAnimation;
    }

    public Animation getUnderPushAnimation() {
        return mUnderPushAnimation;
    }

    public void setUnderPushAnimation(Animation underPushAnimation) {
        mUnderPushAnimation = underPushAnimation;
    }

    public Animation getPopAnimation() {
        return mPopAnimation;
    }

    public void setPopAnimation(Animation popAnimation) {
        mPopAnimation = popAnimation;
    }

    public Animation getUnderPopAnimation() {
        return mUnderPopAnimation;
    }

    public void setUnderPopAnimation(Animation underPopAnimation) {
        mUnderPopAnimation = underPopAnimation;
    }

    public void setLaunchMode(int launchMode) {
        mLaunchMode = launchMode;
    }

    public int getLaunchMode() {
        return mLaunchMode;
    }

    public boolean isPopBackWindow() {
        return mIsPopBackWinAfterPush;
    }

    public void setPopBackWindow(boolean popBackWindow) {
        mIsPopBackWinAfterPush = popBackWindow;
    }


}
