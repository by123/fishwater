package com.sjy.ttclub.framework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class WindowManager {
    private Context mContext;
    private WindowEnvironment mWinEnvironment;
    private View mTargetView;
    private OnTouchEventInterceptor mOnTouchEventInterceptor;
    private boolean mTouchEventIntercepted;

    private Handler mHandler;

    public WindowManager(Context context) {
        mContext = context;
        setupWindowEnvironment(context);
    }

    private void setupWindowEnvironment(Context context) {
        if (null == mWinEnvironment) {
            mWinEnvironment = new WindowEnvironment(context);
            mWinEnvironment.setFocusable(true);
        }
        ((Activity) context).setContentView(mWinEnvironment);
    }

    public void pushWindow(AbstractWindow window) {
        pushWindow(window, true);
    }

    public void pushWindow(AbstractWindow window, boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().pushWindow(window, animated);
        }
    }

    public void popWindow() {
        popWindow(true);
    }

    public void popWindow(boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().popWindow(animated);
        }
    }

    public boolean removeWindow(AbstractWindow delWindow, boolean onlyCurStack) {
        if (onlyCurStack) {
            WindowStack stack = mWinEnvironment.getCurrentWindowStack();
            if (stack != null) {
                stack.removeView(delWindow);
                return stack.removeStackView(delWindow);
            }
            return false;
        } else {
            boolean exist = false;
            for (int i = 0; i < mWinEnvironment.getWindowStackCount(); i++) {
                WindowStack stack = mWinEnvironment.getWindowStackAt(i);
                if (stack != null) {
                    stack.removeView(delWindow);
                    exist |= stack.removeStackView(delWindow);
                }
            }

            return exist;
        }
    }

    public void popToRootWindow(boolean animated) {
        if (mWinEnvironment.getCurrentWindowStack() != null) {
            mWinEnvironment.getCurrentWindowStack().popToRootWindow(animated);
        }
    }

    public void popToRootWindow(int index, boolean animated) {
        if (mWinEnvironment.getWindowStackAt(index) != null) {
            mWinEnvironment.getWindowStackAt(index).popToRootWindow(animated);
        }
    }

    public AbstractWindow getCurrentWindow() {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return mWinEnvironment.getCurrentWindowStack().getStackTopWindow();
    }

    public AbstractWindow getWindowBehind(AbstractWindow currentWindow) {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return mWinEnvironment.getCurrentWindowStack().getWindowBehind(currentWindow);
    }

    public AbstractWindow getCurrentRootWindow() {
        if (mWinEnvironment.getCurrentWindowStack() == null) {
            return null;
        }
        return mWinEnvironment.getCurrentWindowStack().getRootWindow();
    }

    public int getRootWindowIndex(AbstractWindow rootWindow) {
        final int stackCount = getWindowStackCount();
        AbstractWindow tempRootWin = null;
        for (int i = 0; i < stackCount; i++) {
            tempRootWin = getRootWindowAt(i);
            if (tempRootWin == rootWindow)
                return i;
        }
        return -1;
    }

    public WindowStack getWindowStack(int index) {
        return mWinEnvironment.getWindowStackAt(index);
    }

    public int getWindowStackCount() {
        return mWinEnvironment.getWindowStackCount();
    }

    public int getCurrentWindowStatckIndex() {
        return mWinEnvironment.getCurrentWindowStackIndex();
    }

    public boolean createWindowStack(AbstractWindow rootWindow) {
        return createWindowStack(rootWindow, -1);
    }

    public boolean createWindowStack(AbstractWindow rootWindow, int index) {
        if (rootWindow.getParent() != null) {
            ((ViewGroup) rootWindow.getParent()).removeView(rootWindow);
        }
        if (null == mWinEnvironment.getCurrentWindowStack()) {
            mWinEnvironment.createWindowStack(new WindowStack(mContext, rootWindow), index, true);
        } else {
            mWinEnvironment.createWindowStack(new WindowStack(mContext, rootWindow), index, false);
        }
        return true;
    }

    public boolean destroyWindowStack(int index) {
        return mWinEnvironment.destroyWindowStack(index);
    }


    public AbstractWindow getRootWindowAt(int index) {
        if (mWinEnvironment.getWindowStackAt(index) == null) {
            return null;
        }
        return mWinEnvironment.getWindowStackAt(index).getRootWindow();
    }

    public AbstractWindow getTopWindowAt(int index) {
        if (mWinEnvironment.getWindowStackAt(index) == null) {
            return null;
        }
        return mWinEnvironment.getWindowStackAt(index).getStackTopWindow();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;
        final int action = event.getAction();

        if (!mTouchEventIntercepted && onInterceptTouchEvent(event)) {
            mTouchEventIntercepted = true;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            if (mTargetView != null) {
                mTargetView = null;
            }

            if (!mTouchEventIntercepted && mWinEnvironment.getCurrentWindowStack() != null) {
                final AbstractWindow topWindow = mWinEnvironment.getCurrentWindowStack().getStackTopWindow();
                if (topWindow != null) {
                    final Rect hitRect = new Rect();
                    final int realX = (int) event.getX();
                    final int realY = (int) event.getY();
                    topWindow.getHitRect(hitRect);
                    if (hitRect.contains(realX, realY)) {
                        mTargetView = topWindow;
                    }
                }
            }
        }

        if (mTouchEventIntercepted) {
            if (mTargetView != null) {
                event.setAction(MotionEvent.ACTION_CANCEL);
                mTargetView.dispatchTouchEvent(event);
                mTargetView = null;
                event.setAction(action);
            }
            result = onTouchEvent(event);
        } else if (mTargetView != null) {
            result = mTargetView.dispatchTouchEvent(event);
        } else {
            result = false;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mTargetView = null;
            mTouchEventIntercepted = false;
        }

        return result;
    }

    private boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mOnTouchEventInterceptor != null && mOnTouchEventInterceptor.onInterceptTouchEvent(ev)) {
            return true;
        }
        return false;
    }

    private boolean onTouchEvent(MotionEvent event) {
        if (mOnTouchEventInterceptor != null && mOnTouchEventInterceptor.onTouchEvent(event)) {
            return true;
        }
        return false;
    }


    public void addExtLayerContent(View v) {
        mWinEnvironment.addExtLayerContent(v);
    }

    public void removeExtLayerContent(View v) {
        mWinEnvironment.removeExtLayerContent(v);
    }

    public void showWindowLayer() {
        mWinEnvironment.showWindowLayer();
    }

    public void hideWindowLayer() {
        mWinEnvironment.hideWindowLayer();
    }


    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private void setOnTouchEventInterceptor(OnTouchEventInterceptor interceptor) {
        mOnTouchEventInterceptor = interceptor;
    }
}
