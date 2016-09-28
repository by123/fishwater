/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.ui.BaseLayerLayout;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.util.ResourceHelper;

public class AbstractWindow extends FrameLayout implements INotify {
    public static final String TAG = "AbstractWindow";
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_DRAW = false;
    public static final boolean DEBUG_MEASURE_LAYOUT_EFFICIENCY = false;

    public static final int LAUNCH_MODE_STANDER = 0;
    public static final int LAUNCH_MODE_SINGLE_TOP = 1;
    public static final int LAUNCH_MODE_SINGLE_INSTANCE = 2;

    /**
     * before push in，在窗口进入动画进行前，回调
     */
    public static final byte STATE_BEFORE_PUSH_IN = 0;
    /**
     * after push in，在窗口进入动画结束后，回调
     */
    public static final byte STATE_AFTER_PUSH_IN = 1;
    /**
     * 在window重新回到栈顶时（即当前显示），回调（注意，第一次pushWindow，不会回调）
     */
    public static final byte STATE_ON_SHOW = 2;
    /**
     * before pop out，在窗口退出动画进行前，回调
     */
    public static final byte STATE_BEFORE_POP_OUT = 3;
    /**
     * after pop out，在窗口动画结束后，回调
     */
    public static final byte STATE_AFTER_POP_OUT = 4;
    /**
     * 当window不再栈顶时（即不是当前显示），回调
     */
    public static final byte STATE_ON_HIDE = 5;
    /**
     * 在窗口已经添加root view时，回调
     */
    public static final byte STATE_ON_ATTACH = 12;
    /**
     * 在窗口已经做root view删除时，回调
     */
    public static final byte STATE_ON_DETACH = 13;

    public static final byte STATE_BEFORE_SWITCH_IN = 6; // reserved for later
    // use
    public static final byte STATE_AFTER_SWITCH_IN = 7; // reserved for later
    // use
    public static final byte STATE_ON_SWITCH_IN = 8;
    public static final byte STATE_BEFORE_SWITCH_OUT = 9;// reserved for later
    // use
    public static final byte STATE_AFTER_SWITCH_OUT = 10;// reserved for later
    // use
    public static final byte STATE_ON_SWITCH_OUT = 11;

    public static final byte STATE_ON_WIN_STACK_CREATE = 14;
    public static final byte STATE_ON_WIN_STACK_DESTROY = 15;

    public static final int WINDOW_TYPE_NOTSPECIFIED = -1;

    public static final LayoutParams WINDOW_LP = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    public static enum WindowLayerType {
        ONLY_USE_BASE_LAYER, USE_BASE_AND_BAR_LAYER, USE_ALL_LAYER,
    }

    ;

    /*package*/ static boolean isHaveKeyDownEvent = false;

    private ViewGroup mBaseLayer;
    private RelativeLayout mBtnLayer;
    private RelativeLayout mExtLayer;
    private RelativeLayout mBarLayer;
    protected View mStatusBarView;

    private WindowSwipeHelper mSwipeHelper;

    protected UICallBacks mCallBacks;
    protected Rect mWindowRect;

    protected AbstractWindowInfo mWindowInfo = new AbstractWindowInfo();

    protected boolean mIsFullScreen;

    public AbstractWindow(Context context, UICallBacks callBacks) {
        this(context, callBacks, WindowLayerType.ONLY_USE_BASE_LAYER);
    }

    public AbstractWindow(Context context, UICallBacks callBacks, boolean fullScreen) {
        this(context, callBacks, WindowLayerType.ONLY_USE_BASE_LAYER, fullScreen);
    }

    public AbstractWindow(Context context, UICallBacks callBacks,
                          WindowLayerType useLayerType) {
        this(context, callBacks, useLayerType, false);
    }

    public AbstractWindow(Context context, UICallBacks callBacks,
                          WindowLayerType useLayerType, boolean fullScreen) {
        super(context);
        mIsFullScreen = fullScreen;
        mCallBacks = callBacks;
        mWindowRect = new Rect();
        mWindowInfo.setUseLayerType(useLayerType);
        mSwipeHelper = new WindowSwipeHelper(this, callBacks);
        setWillNotDraw(false);
        initLayer();
    }

    public ViewGroup getBaseLayer() {
        return mBaseLayer;
    }

    public RelativeLayout getBtnLayer() {
        return mBtnLayer;
    }

    public RelativeLayout getExtLayer() {
        return mExtLayer;
    }

    public RelativeLayout getBarLayer() {
        return mBarLayer;
    }

    public Bitmap snapShot(Bitmap outBitmap) {
        long begin;
        if (DEBUG)
            begin = SystemClock.currentThreadTimeMillis();

        if (null == outBitmap) {
            outBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
            if (null == outBitmap) {
                return null;
            }
        }

        Canvas snapShotCanvas = new Canvas();
        snapShotCanvas.setBitmap(outBitmap);
        draw(snapShotCanvas);

        if (DEBUG) {
            long end = SystemClock.currentThreadTimeMillis();
            Log.i(TAG, "snap shot cost time: " + String.valueOf(end - begin));
        }

        return outBitmap;
    }

    public void setOptBackWindow(boolean optBackWindow) {
        mWindowInfo.setOptBackWindow(optBackWindow);
    }

    public boolean isOptBackWindow() {
        return mWindowInfo.isOptBackWindow();
    }

    public WindowLayerType getUseLayerType() {
        return mWindowInfo.getUseLayerType();
    }

    public int getWindowType() {
        return mWindowInfo.getWindowType();
    }

    public void setWindowType(int type) {
        mWindowInfo.setWindowType(type);
    }

    public boolean isEnableHardwareAcceleration() {
        return mWindowInfo.isEnableHardwareAcceleration();
    }

    public void setEnableSwipeGesture(boolean enable) {
        mWindowInfo.setEnableSwipeGesture(enable);
    }

    public boolean isEnableSwipeGesture() {
        return mWindowInfo.isEnableSwipeGesture();
    }

    public boolean isAnimating() {
        return mWindowInfo.isAnimating();
    }


    public Animation getPushAnimation() {
        return mWindowInfo.getPushAnimation();
    }

    public Animation getPopAnimation() {
        return mWindowInfo.getPopAnimation();
    }

    public Animation getUnderPushAnimation() {
        return mWindowInfo.getUnderPushAnimation();
    }

    public Animation getUnderPopAnimation() {
        return mWindowInfo.getUnderPopAnimation();
    }

    public void setPushAnimation(int animationStyle) {
        mWindowInfo.setPushAnimation(AnimationUtils.loadAnimation(getContext(),
                animationStyle));
    }

    public void setPopAnimation(int animationStyle) {
        mWindowInfo.setPopAnimation(AnimationUtils.loadAnimation(getContext(),
                animationStyle));
    }

    public void setUnderPushAnimation(int animationStyle) {
        mWindowInfo.setUnderPushAnimation(AnimationUtils.loadAnimation(
                getContext(), animationStyle));
    }

    public void setUnderPopAnimation(int animationStyle) {
        mWindowInfo.setUnderPopAnimation(AnimationUtils.loadAnimation(
                getContext(), animationStyle));
    }

    public void setPopAnimation(Animation animation) {
        mWindowInfo.setPopAnimation(animation);
    }

    public void setLaunchMode(int launchMode) {
        mWindowInfo.setLaunchMode(launchMode);
    }

    public int getLaunchMode() {
        return mWindowInfo.getLaunchMode();
    }

    public void setPopBackWindowAfterPush(boolean popBackWindowAfterPush) {
        mWindowInfo.setPopBackWindow(popBackWindowAfterPush);
    }

    public boolean isPopBackWindowAfterPush() {
        return mWindowInfo.isPopBackWindow();
    }

    protected UICallBacks getUICallbacks() {
        return mCallBacks;
    }

    protected void onWindowStateChange(int stateFlag) {
        if (stateFlag == STATE_BEFORE_PUSH_IN
                || stateFlag == STATE_BEFORE_POP_OUT) {
            mWindowInfo.setIsAnimating(true);
            invalidate();
        }

        if (stateFlag == STATE_BEFORE_PUSH_IN || stateFlag == STATE_ON_SHOW) {
            scrollTo(0, 0);
        }

        if (stateFlag == STATE_AFTER_PUSH_IN
                || stateFlag == STATE_AFTER_POP_OUT) {
            mWindowInfo.setIsAnimating(false);
        }

        mCallBacks.onWindowStateChange(this, stateFlag);
    }

    protected void initLayer() {
        mBaseLayer = onCreateBaseLayer();
        addViewInLayout(mBaseLayer, 0, WINDOW_LP);

        if (WindowLayerType.USE_ALL_LAYER == mWindowInfo.getUseLayerType()) {
            mExtLayer = onCreateExtLayer();
            addViewInLayout(mExtLayer, -1, WINDOW_LP);
            mBtnLayer = onCreateButtonLayer();
            addViewInLayout(mBtnLayer, -1, WINDOW_LP);
            mBarLayer = onCreateBarLayer();
            addViewInLayout(mBarLayer, -1, WINDOW_LP);
        } else if (WindowLayerType.USE_BASE_AND_BAR_LAYER == mWindowInfo
                .getUseLayerType()) {
            mBarLayer = onCreateBarLayer();
            addViewInLayout(mBarLayer, -1, WINDOW_LP);
        }
    }

    protected ViewGroup onCreateBaseLayer() {
        BaseLayerLayout baseLayer = createDefaultBaseLayer();
        baseLayer.setBackgroundColor(getBgColor());

        if (!mIsFullScreen) {
            mStatusBarView = createDefaultStatusBar();
            if (mStatusBarView != null) {
                baseLayer.addView(mStatusBarView);
            }
        }

        return baseLayer;
    }

    protected RelativeLayout onCreateButtonLayer() {
        return createDefaultLayer();
    }

    protected RelativeLayout onCreateBarLayer() {
        return createDefaultLayer();
    }

    protected RelativeLayout onCreateExtLayer() {
        return createDefaultLayer();
    }

    protected RelativeLayout createDefaultLayer() {
        return new RelativeLayout(getContext());
    }

    protected BaseLayerLayout createDefaultBaseLayer() {
        BaseLayerLayout baseLayout = new BaseLayerLayout(getContext());
        return baseLayout;
    }

    protected View createDefaultStatusBar() {
        View view = new View(getContext());
        view.setBackgroundColor(TitleBar.getBgColor());
        BaseLayerLayout.LayoutParams lp = new BaseLayerLayout.LayoutParams(
                BaseLayerLayout.LayoutParams.MATCH_PARENT,
                BaseLayerLayout.LayoutParams.WRAP_CONTENT);
        lp.type = BaseLayerLayout.TYPE_STATUS_BAR;
        view.setLayoutParams(lp);
        return view;
    }

    protected BaseLayerLayout.LayoutParams getBaseLayerLP() {
        BaseLayerLayout.LayoutParams lp = new BaseLayerLayout.LayoutParams(
                BaseLayerLayout.LayoutParams.MATCH_PARENT,
                BaseLayerLayout.LayoutParams.MATCH_PARENT);
        return lp;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        getDrawingRect(mWindowRect);
        mSwipeHelper.draw(canvas, mWindowRect);
    }

    public boolean onWindowBackKeyEvent() {
        return false;
    }

    public boolean onWindowKeyEvent(int keyCode, KeyEvent event) {
        boolean retVal = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (AbstractWindow.isHaveKeyDownEvent) {
                if (onWindowBackKeyEvent()) {
                    retVal = true;
                }
            }
        }
        return retVal;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            isHaveKeyDownEvent = true;
        }

        boolean result = onWindowKeyEvent(event.getKeyCode(), event);
        if (!result) {
            result = mCallBacks.onWindowKeyEvent(this, event
                    .getKeyCode(), event) || super.dispatchKeyEvent(event);
        }

        if (event.getAction() == KeyEvent.ACTION_UP) {
            isHaveKeyDownEvent = false;
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isAnimating() || !isEnableSwipeGesture()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return mSwipeHelper.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isAnimating() || !isEnableSwipeGesture()) {
            return super.onTouchEvent(ev);
        } else {
            return mSwipeHelper.onTouchEvent(ev);
        }
    }

    @Override
    public void computeScroll() {
        if (isAnimating() || !isEnableSwipeGesture()) {
            super.computeScroll();
        } else {
            mSwipeHelper.computeScroll();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (isAnimating() || !isEnableSwipeGesture()) {
            super.onScrollChanged(l, t, oldl, oldt);
        } else {
            mSwipeHelper.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (isAnimating() || !isEnableSwipeGesture()) {
            super.onSizeChanged(w, h, oldw, oldh);
        } else {
            mSwipeHelper.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void addView(View child) {
        throw new UnsupportedOperationException("Cannot add view from outside.");
    }

    @Override
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("Cannot add view from outside.");
    }

    @Override
    public void addView(View child, int width, int height) {
        throw new UnsupportedOperationException("Cannot add view from outside.");
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("Cannot add view from outside.");
    }

    @Override
    public void addView(View child, int index,
                        ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("Cannot add view from outside.");
    }

    @Override
    public void bringChildToFront(View child) {
        throw new UnsupportedOperationException("Cannot adjust layer index.");
    }

    @Override
    public void notify(Notification notification) {

    }

    public void resetWindowBackground() {
        if (mBaseLayer != null) {
            mBaseLayer.setBackgroundColor(getBgColor());
        }

        if (mStatusBarView != null) {
            mStatusBarView.setBackgroundColor(TitleBar.getBgColor());
        }
    }

    public void setWindowBackground(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        if (mBaseLayer != null) {
            mBaseLayer.setBackgroundDrawable(drawable);
        }

        if (mStatusBarView != null) {
            mStatusBarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void setStatusBarBackground(int color) {
        if (mStatusBarView != null) {
            mStatusBarView.setBackgroundColor(color);
        }
    }

    public static int getBgColor() {
        return ResourceHelper.getColor(R.color.defaultwindow_bg_color);
    }

}
