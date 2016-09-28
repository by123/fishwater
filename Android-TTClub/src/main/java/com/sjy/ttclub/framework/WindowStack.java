package com.sjy.ttclub.framework;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.HardwareUtil;

import java.util.ArrayList;
import java.util.Stack;

public class WindowStack extends FrameLayout implements INotify {

    public static final String TAG = "WindowStack";
    public static final boolean DEBUG = false;

    private static final int ANIMATOR_DURATION = 300;

    private AbstractWindow mRootWindow;
    private Stack<AbstractWindow> mViewsStack = new Stack<AbstractWindow>();


    private ArrayList<Runnable> mRunnables = new ArrayList<Runnable>();
    /**
     * 当正在处于子View绘制期间的标志位
     */
    private boolean mIsDispatchDrawing = false;

    public WindowStack(Context context) {
        super(context);
    }

    public WindowStack(Context context, AbstractWindow rootWindow) {
        super(context);
        mRootWindow = rootWindow;
        addView(rootWindow);
        mViewsStack.push(mRootWindow);
    }

    AbstractWindow getRootWindow() {
        return mRootWindow;
    }

    void replaceRootWindow(AbstractWindow newRootWindow) {
        removeView(mRootWindow);
        mRootWindow = newRootWindow;
        mRootWindow.onWindowStateChange(AbstractWindow.STATE_ON_SHOW);
        mViewsStack.set(0, mRootWindow);
        addView(newRootWindow, 0);
    }

    AbstractWindow getStackTopWindow() {
        return mViewsStack.peek();
    }

    public boolean removeStackView(AbstractWindow window) {
        return mViewsStack.remove(window);
    }

    AbstractWindow getWindowBehind(AbstractWindow currentWindow) {
        final int stackSize = mViewsStack.size();
        for (int i = stackSize - 1; i > 0; i--) {
            if (mViewsStack.get(i) == currentWindow) {
                return mViewsStack.get(i - 1);
            }
        }
        return null;
    }

    void pushWindow(AbstractWindow w, boolean animated) {
        int launchMode = w.getLaunchMode();
        if (launchMode == AbstractWindow.LAUNCH_MODE_SINGLE_TOP || launchMode == AbstractWindow
                .LAUNCH_MODE_SINGLE_INSTANCE) {
            AbstractWindow frontWin = w;
            AbstractWindow backWin = mViewsStack.peek();
            if (frontWin.getClass().equals(backWin.getClass())) {
                return;
            }
            if (launchMode == AbstractWindow.LAUNCH_MODE_SINGLE_INSTANCE) {
                for (AbstractWindow window : mViewsStack) {
                    if (window.getClass().equals(w.getClass())) {
                        mViewsStack.remove(window);
                        removeView(window);
                    }
                }
            }
        }
        if (w.getParent() != null) {
            return;
        }
        boolean isPopBackWindow = w.isPopBackWindowAfterPush();

        AbstractWindow frontWin = w;
        AbstractWindow backWin = isPopBackWindow ? mViewsStack.pop() : mViewsStack.peek();

        frontWin.setVisibility(View.VISIBLE);

        addView(w);

        if (animated) {
            frontWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_PUSH_IN);
            mViewsStack.push(frontWin);
            frontWin.onWindowStateChange(AbstractWindow.STATE_ON_ATTACH);
            startPushAnimation(frontWin, backWin);
        } else {
            if (isPopBackWindow) {
                backWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_POP_OUT);
                removeView(backWin);
                backWin.onWindowStateChange(AbstractWindow.STATE_ON_DETACH);
                backWin.onWindowStateChange(AbstractWindow.STATE_AFTER_POP_OUT);
            } else {
                backWin.onWindowStateChange(AbstractWindow.STATE_ON_HIDE);
                backWin.setVisibility(View.INVISIBLE);
                if (frontWin.isOptBackWindow()) {
                    backWin.setVisibility(GONE);
                }
            }
            frontWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_PUSH_IN);
            mViewsStack.push(frontWin);
            frontWin.onWindowStateChange(AbstractWindow.STATE_ON_ATTACH);
            frontWin.onWindowStateChange(AbstractWindow.STATE_AFTER_PUSH_IN);
        }
    }

    void popWindow(boolean animated) {
        if (mViewsStack.size() <= 1) {
            return;
        }

        AbstractWindow frontWin = mViewsStack.pop();
        AbstractWindow backWin = mViewsStack.peek();
        if (frontWin == mRootWindow || frontWin == null) {
            return;
        }

        if (animated) {
            frontWin.invalidate();
        }
        backWin.setVisibility(View.VISIBLE);

        if (animated) {
            frontWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_POP_OUT);
            backWin.onWindowStateChange(AbstractWindow.STATE_ON_SHOW);
            startPopAnimation(frontWin, backWin);
        } else {
            frontWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_POP_OUT);
            backWin.onWindowStateChange(AbstractWindow.STATE_ON_SHOW);

            removeView(frontWin);
            frontWin.onWindowStateChange(AbstractWindow.STATE_ON_DETACH);
            frontWin.onWindowStateChange(AbstractWindow.STATE_AFTER_POP_OUT);

            CommonUtils.gc();
        }
    }


    void popToRootWindow(boolean animated) {
        int count = mViewsStack.size();
        if (count == 1) {
            return;
        }

        AbstractWindow w;
        // remove all the views except the top most view and the bottom view
        for (int i = count - 2; i > 0; i--) {
            w = mViewsStack.remove(i);
            removeView(w);
            w.onWindowStateChange(AbstractWindow.STATE_BEFORE_POP_OUT);
            w.onWindowStateChange(AbstractWindow.STATE_ON_DETACH);
            w.onWindowStateChange(AbstractWindow.STATE_AFTER_POP_OUT);
        }

        popWindow(animated);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        mIsDispatchDrawing = true;
        super.dispatchDraw(canvas);
        mIsDispatchDrawing = false;
    }

    @Override
    public void notify(Notification notification) {
    }

    private void startPushAnimation(final AbstractWindow frontWin, final AbstractWindow backWin) {
        Animation pushAnimation = frontWin.getPushAnimation();

        if (pushAnimation != null) {
            pushAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            dealWithWindowAfterPush(frontWin, backWin);
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }

            });
            frontWin.startAnimation(pushAnimation);
        } else {
            final ViewPropertyAnimator anim = frontWin.animate();
            frontWin.setTranslationX(getWidth() * 0.8f);
            anim.translationX(0);
            anim.setDuration(ANIMATOR_DURATION);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            HardwareUtil.setLayerType(frontWin, HardwareUtil.LAYER_TYPE_NONE);
                            dealWithWindowAfterPush(frontWin, backWin);
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }
            });
            HardwareUtil.setLayerType(frontWin, HardwareUtil.LAYER_TYPE_HARDWARE);
            HardwareUtil.buildLayer(frontWin);
            anim.start();
        }
    }

    private void startPopAnimation(final AbstractWindow frontWin, final AbstractWindow backWin) {
        Animation popAnimation = frontWin.getPopAnimation();

        if (popAnimation != null) {
            popAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            dealWithWindowAfterPop(frontWin, backWin);
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }
            });
            frontWin.startAnimation(popAnimation);
        } else {
            final ViewPropertyAnimator anim = frontWin.animate();
            frontWin.setTranslationX(0);
            anim.translationX(getWidth());
            anim.setDuration(ANIMATOR_DURATION);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            HardwareUtil.setLayerType(frontWin, HardwareUtil.LAYER_TYPE_NONE);
                            dealWithWindowAfterPop(frontWin, backWin);
                            mRunnables.remove(this);
                        }
                    };
                    mRunnables.add(runnable);
                    post(runnable);
                }
            });
            HardwareUtil.setLayerType(frontWin, HardwareUtil.LAYER_TYPE_HARDWARE);
            HardwareUtil.buildLayer(frontWin);
            anim.start();
        }
    }

    /**
     * 切入动画 结束后的处理
     */
    private void dealWithWindowAfterPush(final AbstractWindow frontWin, final AbstractWindow backWin) {
        if (frontWin != null && backWin != null) {
            if (frontWin.isPopBackWindowAfterPush()) {
                backWin.onWindowStateChange(AbstractWindow.STATE_BEFORE_POP_OUT);
                removeView(backWin);
                mViewsStack.remove(backWin);
                backWin.onWindowStateChange(AbstractWindow.STATE_ON_DETACH);
                backWin.onWindowStateChange(AbstractWindow.STATE_AFTER_POP_OUT);
            } else {
                backWin.onWindowStateChange(AbstractWindow.STATE_ON_HIDE);
                backWin.setVisibility(View.INVISIBLE);
                if (frontWin.isOptBackWindow()) {
                    backWin.setVisibility(GONE);
                }
            }
            frontWin.onWindowStateChange(AbstractWindow.STATE_AFTER_PUSH_IN);
        }
    }

    /**
     * 切出动画 结束后的处理，包括更改状态，removeViews等
     */
    private void dealWithWindowAfterPop(final AbstractWindow frontWin, final AbstractWindow backWin) {
        if (frontWin != null && backWin != null) {
            removeView(frontWin);
            frontWin.onWindowStateChange(AbstractWindow.STATE_ON_DETACH);
            frontWin.onWindowStateChange(AbstractWindow.STATE_AFTER_POP_OUT);
        }
        backWin.setVisibility(View.VISIBLE);
        CommonUtils.gc();
    }

}
