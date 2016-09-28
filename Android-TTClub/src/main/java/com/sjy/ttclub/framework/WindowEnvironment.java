package com.sjy.ttclub.framework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.SystemUtil;

final class WindowEnvironment extends FrameLayout {

    private static final String TAG = "WindowEnvironment";
    private static final String TAG_WINLAYER = "WindowLayer";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_MEASURE_LAYOUT_EFFICIENCY = false;

    private LayoutParams mMatchParentLP;
    private static final DisplayMetrics sDisplayMetrics = new DisplayMetrics();
    private WindowLayer mWindowLayer;
    private FrameLayout mExtendedLayer;

    private WindowStack mCurrentStack;

    public WindowEnvironment(Context context) {
        super(context);
        mMatchParentLP = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mWindowLayer = new WindowLayer(context);
        addView(mWindowLayer, mMatchParentLP);

        mExtendedLayer = new FrameLayout(context);
        mMatchParentLP = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(mExtendedLayer, mMatchParentLP);
    }

    void createWindowStack(WindowStack winStack, int index, boolean switchTo) {
        winStack.getRootWindow().onWindowStateChange(AbstractWindow.STATE_ON_WIN_STACK_CREATE);
        if (switchTo) {
            mWindowLayer.addView(winStack, index);
            switchWindowStack(winStack);
        } else {
            winStack.setVisibility(View.INVISIBLE);
            mWindowLayer.addView(winStack, index);
        }
    }

    private boolean ensureIndexLegal(int index) {
        if (index < 0 || index > mWindowLayer.getChildCount() - 1) {
            Log.d(TAG_WINLAYER, "index illegal " + index);
            return false;
        }
        return true;
    }

    boolean destroyWindowStack(int index) {
        if (ensureIndexLegal(index)) {
            getWindowStackAt(index).popToRootWindow(false);
            if (mWindowLayer.getChildCount() == 1) {
                return false;
            }
            getWindowStackAt(index).getRootWindow()
                    .onWindowStateChange(AbstractWindow.STATE_ON_WIN_STACK_DESTROY);
            if (mCurrentStack == getWindowStackAt(index)) {
                int fallbackCurrentStackIndex = (index > 0) ? index - 1 : index;
                mWindowLayer.removeViewAt(index);
                switchWindowStack(fallbackCurrentStackIndex);
            } else {
                mWindowLayer.removeViewAt(index);
            }
            return true;
        }
        return false;
    }

    int getWindowStackCount() {
        return mWindowLayer.getChildCount();
    }

    void switchWindowStack(int index) {
        if (index == getStackIndex(mCurrentStack)) {
            return;
        }
        if (ensureIndexLegal(index)) {
            int count = mWindowLayer.getChildCount();
            for (int i = 0; i < count; i++) {
                if (i == index) {

                    mCurrentStack.getRootWindow().clearAnimation();
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_OUT);
                    if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                        mCurrentStack.getStackTopWindow().clearAnimation();
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_OUT);
                    }
                    mCurrentStack = getWindowStackAt(index);
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_IN);
                    if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_IN);
                    }
                    // 必须先更新mCurrentStack，再设置VISIBLE
                    mCurrentStack.setVisibility(View.VISIBLE);
                    mCurrentStack.requestLayout();
                }
            }

            // 将当前Stack隐藏会引发窗口onVisibilityChanged,而该方法内部需要根据新的Stack是否指向WebView以处理壁纸是否需要隐藏的问题。
            // 所以：在变更mCurrentStack之后才能隐藏当前Stack，否则onVisibilityChanged将无法获取正确的Stack，引发GPU overDraw。
            for (int i = 0; i < count; ++i) {
                if (i != index) {
                    mWindowLayer.getChildAt(i).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    void switchWindowStack(WindowStack winStack) {
        int count = mWindowLayer.getChildCount();
        View child = null;
        for (int i = 0; i < count; i++) {
            child = mWindowLayer.getChildAt(i);
            if (winStack == child) {
                if (null != mCurrentStack) {
                    mCurrentStack.getRootWindow().clearAnimation();
                    mCurrentStack.getRootWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_OUT);
                    if (mCurrentStack.getStackTopWindow() != mCurrentStack.getRootWindow()) {
                        mCurrentStack.getStackTopWindow().clearAnimation();
                        mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_OUT);
                    }
                }
                mCurrentStack = winStack;
                mCurrentStack.getRootWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_IN);
                if (mCurrentStack.getRootWindow() != mCurrentStack.getStackTopWindow()) {
                    mCurrentStack.getStackTopWindow().onWindowStateChange(AbstractWindow.STATE_ON_SWITCH_IN);
                }

                // 必须先更新mCurrentStack，再设置VISIBLE
                mCurrentStack.setVisibility(View.VISIBLE);
            }
        }

        for (int i = 0; i < count; ++i) {
            child = mWindowLayer.getChildAt(i);
            if (null != child && winStack != child) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }

    WindowStack getWindowStackAt(int index) {
        if (ensureIndexLegal(index)) {
            return (WindowStack) mWindowLayer.getChildAt(index);
        }
        return null;
    }

    int getStackIndex(WindowStack stack) {
        for (int i = 0; i < getWindowStackCount(); i++) {
            if (stack == getWindowStackAt(i)) {
                return i;
            }
        }
        return -1;
    }

    WindowStack getCurrentWindowStack() {
        return mCurrentStack;
    }

    int getCurrentWindowStackIndex() {
        return getStackIndex(mCurrentStack);
    }

    void showWindowLayer() {
        mWindowLayer.setVisibility(View.VISIBLE);
    }

    void hideWindowLayer() {
        mWindowLayer.setVisibility(View.GONE);
    }

    void addExtLayerContent(View v) {
        if (v != null) {
            if (v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
            mExtendedLayer.addView(v);
        }
    }

    void removeExtLayerContent(View v) {
        if (v != null && v.getParent() != null && v.getParent() == mExtendedLayer) {
            mExtendedLayer.removeView(v);
        }
    }

    void addLayer(View layer) {
        addView(layer, mMatchParentLP);
    }

    void removeLayer(View layer) {
        removeView(layer);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            final Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            HardwareUtil.screenWidth = display.getWidth();
            HardwareUtil.screenHeight = display.getHeight();
            HardwareUtil.windowWidth = right - left;
            HardwareUtil.windowHeight = bottom - top;
            display.getMetrics(sDisplayMetrics);
            HardwareUtil.density = sDisplayMetrics.density;
            // now, the screen and window height are all corrected. 
            // so, it's the time to update the system status bar height. 
            SystemUtil.systemStatusBarHeight = HardwareUtil.screenHeight - HardwareUtil.windowHeight;
        }
    }

    void setBlockWindowLayerDispatchDraw(boolean block) {
        mWindowLayer.setBlockDispatchDraw(block);
    }

    void setOnTouchEventInterceptor(OnTouchEventInterceptor interceptor) {
        mWindowLayer.setOnTouchEventInterceptor(interceptor);
    }

    private static class WindowLayer extends FrameLayout {

        private boolean mBlockDispatchDraw;
        private OnTouchEventInterceptor mTouchInterceptor;

        public WindowLayer(Context context) {
            super(context);
        }

        public void setBlockDispatchDraw(boolean block) {
            if (mBlockDispatchDraw != block) {
                mBlockDispatchDraw = block;
                /**
                 * 进入block状态时请求刷新，结合dispatchDraw的拦截，以达到清空DisplayList的目的.
                 * 退出block状态时请求刷新，以便恢复DisplayList
                 */
                invalidate();
            }
        }

        public void setOnTouchEventInterceptor(OnTouchEventInterceptor interceptor) {
            mTouchInterceptor = interceptor;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (!mBlockDispatchDraw) {
                super.dispatchDraw(canvas);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mTouchInterceptor != null && mTouchInterceptor.onInterceptTouchEvent(ev)) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mTouchInterceptor != null && mTouchInterceptor.onTouchEvent(event)) {
                return true;
            }
            return super.onTouchEvent(event);
        }
    }
}

