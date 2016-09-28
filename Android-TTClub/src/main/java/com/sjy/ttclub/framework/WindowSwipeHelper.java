/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.framework;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class WindowSwipeHelper {

    public static interface IScrollable {
        public boolean isLeftEdge();
    }

    private final static String TAG = "WindowSwipeHelper";
    private final static boolean DEBUG = false;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int TOUCH_STATE_SETTLLING = 2;

    private final static int DEFAULT_SCROLL_DURATION = 450; // ms
    private final static int MAX_SCROLL_DURATION = 600; // ms
    private final static int MAX_ALPHA = 255;

    private final static int MIN_DISTANCE_FOR_FLING = 25; // dp
    private final static int MIN_FLING_VELOCITY = 400; // dp
    private final static int CLOSE_ENOUGH = 2; // dp

    private final static int COLOR_DIM_MASK = 0x14000000;

    private final static int SCREEN_WIDTH_DIVISION_FACTOR = 2;
    private final static float XDIFF_REDUCE_FACTOR = 0.75f;

    private final static Interpolator INTERPOLATOR = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private static enum SwipeEffect {
        SCROLL_WINDOW, SHOW_INDICATOR
    }

    private View mHost;
    private UICallBacks mHostCallbacks;

    private View mViewBehind;
    private View mPossibleTargetView;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private Handler mHandler;

    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop;
    private int mScrollDuration = DEFAULT_SCROLL_DURATION;
    private int mTotalScrollXHolder;

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mCloseEnough;

    private float mTouchDownX;
    private float mTouchDownY;
    private float mLastTouchX;

    private boolean mIsBeingDragged = false;
    private boolean mIsUnableToDrag = false;

    private boolean mIsTouchingIScrollable = false;
    private boolean mIsIScrollableOnLeftEdge = false;
    private boolean mIsExiting = false;

    private SwipeEffect mSwipeEffect;

    private float mScrollPercentage;
    private Drawable mDimMaskDrawable;

    private float mIndicatorPercentage;

    public WindowSwipeHelper(View host, UICallBacks hostCallbacks) {
        mHost = host;
        mHostCallbacks = hostCallbacks;

        mHandler = new Handler(Looper.getMainLooper());

        final ViewConfiguration configuration = ViewConfiguration.get(host
                .getContext());
        final float density = host.getContext().getResources()
                .getDisplayMetrics().density;
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
        mCloseEnough = (int) (CLOSE_ENOUGH * density);

        mDimMaskDrawable = new ColorDrawable(COLOR_DIM_MASK);

        mScroller = new Scroller(host.getContext(), INTERPOLATOR);
        mSwipeEffect = SwipeEffect.SCROLL_WINDOW;

        if (DEBUG) {
            Log.d(TAG, "===WindowSwipeHelper");
            Log.d(TAG, "mTouchSlop: " + mTouchSlop);
            Log.d(TAG, "mMinimumVelocity: " + mMinimumVelocity);
            Log.d(TAG, "mMaximumVelocity: " + mMaximumVelocity);
            Log.d(TAG, "mFlingDistance: " + mFlingDistance);
            Log.d(TAG, "mCloseEnough: " + mCloseEnough);
            Log.d(TAG, "mSwipeEffect: " + mSwipeEffect);
        }
    }

    public void computeScroll() {
        if (DEBUG) {
            Log.d(TAG, "==computeScroll");
        }
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            if (DEBUG) {
                Log.d(TAG, "currentScrollX: " + mHost.getScrollX());
            }
            if (mScroller.computeScrollOffset()) {
                mHost.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                mHost.postInvalidate();
                if (DEBUG) {
                    Log.d(TAG, "newScrollX: " + mScroller.getCurrX());
                }
            } else if (mTouchState == TOUCH_STATE_SETTLLING) {
                if (DEBUG) {
                    Log.d(TAG, "scroll ends");
                }
                onFinishScroll();
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "do nothing.");
            }
        }
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (DEBUG) {
            Log.d(TAG, "==onScrollChanged");
        }
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            mScrollPercentage = (float) Math.abs(l)
                    / (float) mHost.getMeasuredWidth();
            if (DEBUG) {
                Log.d(TAG, "newScrollX: " + l);
                Log.d(TAG, "mScrollPercentage: " + mScrollPercentage);
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "do nothing.");
            }
        }
    }

    public int getScrollX() {
        return mScroller.getCurrX();
    }

    public int getScrollY() {
        return mScroller.getCurrY();
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (DEBUG) {
            Log.d(TAG, "==onSizeChanged");
        }
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            checkViewBehind(w, h);
        } else {
            if (DEBUG) {
                Log.d(TAG, "do nothing.");
            }
        }
    }

    private void checkViewBehind(int newWidth, int newHeight) {
        if (DEBUG) {
            Log.d(TAG, "==checkViewBehind, newWidth: " + newWidth
                    + " newHeight: " + newHeight);
        }
        if (mViewBehind != null && mViewBehind.getVisibility() == View.GONE) {
            if (newWidth != mViewBehind.getMeasuredWidth()
                    || newHeight != mViewBehind.getMeasuredHeight()) {
                if (DEBUG) {
                    Log.d(TAG,
                            "manually measure, layout and invalidate mViewBehind: "
                                    + mViewBehind);
                    Log.d(TAG,
                            "mViewBehind, oldWidth: "
                                    + mViewBehind.getMeasuredWidth());
                    Log.d(TAG,
                            "mViewBehind, oldHeight: "
                                    + mViewBehind.getMeasuredHeight());
                }
                mViewBehind.measure(MeasureSpec.makeMeasureSpec(newWidth,
                        MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                        newHeight, MeasureSpec.EXACTLY));
                mViewBehind.layout(0, 0, newWidth, newHeight);
                mViewBehind.invalidate();
                if (DEBUG) {
                    Log.d(TAG,
                            "mViewBehind, newWidth: "
                                    + mViewBehind.getMeasuredWidth());
                    Log.d(TAG,
                            "mViewBehind, newHeight: "
                                    + mViewBehind.getMeasuredHeight());
                }
            } else {
                if (DEBUG) {
                    Log.d(TAG,
                            "do nothing, view width and height are the same.");
                }
            }
        } else if (mViewBehind != null) {
            if (DEBUG) {
                Log.d(TAG,
                        "do nothing, mViewBehind visibiliy: "
                                + mViewBehind.getVisibility());
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "do nothing, mViewBehind is NULL.");
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if (DEBUG) {
            Log.d(TAG, "==onInterceptTouchEvent, action: " + action);
        }

        // 不拦截以下动作，使手势完整完成
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            if (DEBUG) {
                Log.d(TAG, "action up or cancel.");
            }
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return false;
        }

        // 如果已经判断为拖动状态或无法拖动，则直接返回
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                if (DEBUG) {
                    Log.d(TAG, "already being dragged.");
                }
                return true;
            }
            if (mIsUnableToDrag) {
                if (DEBUG) {
                    Log.d(TAG, "unable to drag.");
                }
                return false;
            }
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (DEBUG) {
                    Log.d(TAG, "action down.");
                }
                mTouchDownX = x;
                mTouchDownY = y;
                mLastTouchX = x;
                if (mTouchState == TOUCH_STATE_SETTLLING) {
                    mScroller.computeScrollOffset();
                    if (Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
                        if (DEBUG) {
                            Log.d(TAG, "touch state: settlling, turn to scrolling.");
                        }
                        if (!mScroller.isFinished()) {
                            mScroller.abortAnimation();
                            if (DEBUG) {
                                Log.d(TAG, "mScroller is not finished, abort it.1");
                            }
                        }
                        mIsBeingDragged = true;
                        mTouchState = TOUCH_STATE_SCROLLING;
                    } else {
                        if (DEBUG) {
                            Log.d(TAG,
                                    "touch state: settlling, but close enough, just finish it.");
                        }
                        onFinishScroll();
                        return false;
                    }
                } else {
                    mIsBeingDragged = false;
                }
                mIsUnableToDrag = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (DEBUG) {
                    Log.d(TAG, "action move.");
                }
                mLastTouchX = x;
                final float deltaX = x - mTouchDownX;
                final float xDiff = Math.abs(deltaX);
                final float yDiff = Math.abs(y - mTouchDownY);

                if (DEBUG) {
                    Log.d(TAG, "deltaX: " + deltaX);
                    Log.d(TAG, "xDiff: " + xDiff);
                    Log.d(TAG, "yDiff: " + yDiff);
                    Log.d(TAG, "mTouchSlop: " + mTouchSlop);
                    Log.d(TAG, "mIsBeingDragged: " + mIsBeingDragged);
                    Log.d(TAG, "mIsUnableToDrag: " + mIsUnableToDrag);
                }

                if (deltaX > 0) {
                    if (DEBUG) {
                        Log.d(TAG, "deltaX > 0, checkIfCanBeDragged.");
                    }
                    if (checkIfCanBeDragged(mHost, true, (int) x, (int) y)) {
                        if (DEBUG) {
                            Log.d(TAG, "CAN be dragged.");
                        }
                        if (xDiff > mTouchSlop
                                && xDiff * XDIFF_REDUCE_FACTOR > yDiff) {
                            if (DEBUG) {
                                Log.d(TAG, "onBeginDragged.");
                            }
                            onBeginDrag();
                            mIsBeingDragged = true;
                            mTouchState = TOUCH_STATE_SCROLLING;
                        } else if (yDiff > mTouchSlop) {
                            if (DEBUG) {
                                Log.d(TAG, "yDiff > mTouchSlop, unable to drag.");
                            }
                            mIsUnableToDrag = true;
                        }
                    } else {
                        if (DEBUG) {
                            Log.d(TAG, "CANNOT be dragged.");
                        }
                        mIsUnableToDrag = true;
                    }
                } else if (deltaX < 0) {
                    if (DEBUG) {
                        Log.d(TAG, "deltaX < 0, unable to drag.");
                    }
                    mIsUnableToDrag = true;
                }
                break;
            }
        }

        // 通知Child
        if (mIsBeingDragged) {
            notifyChildTouchCanceled();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        // 只拦截拖动状态的事件
        return mIsBeingDragged;
    }

    private boolean checkIfCanBeDragged(View viewToCheck, boolean checkSelf,
                                        int x, int y) {
        if (DEBUG) {
            Log.d(TAG, "==checkIfCanBeDragged, viewToCheck: " + viewToCheck);
        }

        if (mTotalScrollXHolder != 0) {
            mTotalScrollXHolder = 0;
        }

        if (checkSelf) {
            mPossibleTargetView = viewToCheck;

            if (viewToCheck instanceof IScrollable) {
                mIsTouchingIScrollable = true;
                mIsIScrollableOnLeftEdge = ((IScrollable) viewToCheck)
                        .isLeftEdge();
                if (DEBUG) {
                    Log.d(TAG, "checkSelf: " + checkSelf);
                    Log.d(TAG, "mPossibleTargetView: " + mPossibleTargetView);
                    Log.d(TAG, "mIsIScrollableOnLeftEdge: "
                            + mIsIScrollableOnLeftEdge);
                }
                return mIsIScrollableOnLeftEdge;
            } else {
                mIsTouchingIScrollable = false;
                mTotalScrollXHolder += viewToCheck.getScrollX();
                if (DEBUG) {
                    Log.d(TAG, "checkSelf: " + checkSelf);
                    Log.d(TAG, "mPossibleTargetView: " + mPossibleTargetView);
                    Log.d(TAG, "mChildScrollXHolder: " + mTotalScrollXHolder);
                }
                if (mTotalScrollXHolder > 0) {
                    return false;
                }
            }
        }

        if (viewToCheck instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) viewToCheck;
            final int scrollX = viewToCheck.getScrollX();
            final int scrollY = viewToCheck.getScrollY();
            final int realX = x + scrollX;
            final int realY = y + scrollY;
            final int count = group.getChildCount();
            final Rect hitRect = new Rect();

            if (DEBUG) {
                Log.d(TAG, "viewToCheck is a ViewGroup, childCount: " + count);
                Log.d(TAG, "realX: " + realX + " realY:" + realY);
            }

            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                child.getHitRect(hitRect);
                if (DEBUG) {
                    Log.d(TAG, "checking child: " + child + ", hitRect: "
                            + hitRect);
                }
                if (hitRect.contains(realX, realY)) {
                    if (DEBUG) {
                        Log.d(TAG, "hit child: " + child);
                    }
                    checkIfCanBeDragged(child, true, realX - child.getLeft(),
                            realY - child.getTop());

                    if (mIsTouchingIScrollable && !mIsIScrollableOnLeftEdge) {
                        return false;
                    }

                    if (mTotalScrollXHolder > 0) {
                        return false;
                    }
                }
            }

            if (DEBUG) {
                Log.d(TAG, "view group for loop ends, viewToCheck: "
                        + viewToCheck);
            }
            return true;
        }

        return checkSelf && mTotalScrollXHolder <= 0;
    }

    private void notifyChildTouchCanceled() {
        if (DEBUG) {
            Log.d(TAG, "==notifyChildTouchCanceled, mPossibleTargetView: "
                    + mPossibleTargetView);
        }
        if (mPossibleTargetView != null) {
            MotionEvent ev = MotionEvent.obtain(0, 0,
                    MotionEvent.ACTION_CANCEL, 0, 0, 0);
            mPossibleTargetView.dispatchTouchEvent(ev);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        if (DEBUG) {
            Log.d(TAG, "==onTouchEvent, action: " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (DEBUG) {
                    Log.d(TAG, "action down.");
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    if (DEBUG) {
                        Log.d(TAG, "mScroller is not finished, abort it.2");
                    }
                }
                mTouchDownX = x;
                mTouchDownY = y;
                mLastTouchX = x;

                break;
            case MotionEvent.ACTION_MOVE:
                if (DEBUG) {
                    Log.d(TAG, "action move.");
                }
                if (!mIsBeingDragged) {
                    if (DEBUG) {
                        Log.d(TAG, "is not being dragged, judging.");
                    }
                    final float deltaX = x - mTouchDownX;
                    final float xDiff = Math.abs(deltaX);
                    final float yDiff = Math.abs(y - mTouchDownY);
                    if (deltaX > 0 && xDiff > mTouchSlop
                            && xDiff * XDIFF_REDUCE_FACTOR > yDiff) {
                        mLastTouchX = x;
                        mIsBeingDragged = true;
                        mTouchState = TOUCH_STATE_SCROLLING;
                        onBeginDrag();
                    }
                }

                if (mIsBeingDragged) {
                    if (DEBUG) {
                        Log.d(TAG, "is being dragged.");
                    }

                    float deltaX = mLastTouchX - x;
                    mLastTouchX = x;

                    float currentX = mHost.getScrollX();
                    float targetX = currentX + deltaX;
                    float leftLimit = 0;
                    float rightLimit = -mHost.getMeasuredWidth();

                    if (targetX > leftLimit) {
                        if (DEBUG) {
                            Log.d(TAG, "reach left limit.");
                        }
                        deltaX = leftLimit - currentX;
                        targetX = leftLimit;
                    } else if (targetX < rightLimit) {
                        if (DEBUG) {
                            Log.d(TAG, "reach right limit.");
                        }
                        deltaX = rightLimit - currentX;
                        targetX = rightLimit;
                    }
                    onDrag((int) deltaX);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (DEBUG) {
                    Log.d(TAG, "action up.");
                }
                if (mIsBeingDragged) {
                    float deltaX = x - mTouchDownX;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocity = (int) mVelocityTracker.getXVelocity();
                    onFinishDrag(deltaX, velocity, false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (DEBUG) {
                    Log.d(TAG, "action cancel.");
                }
                if (mIsBeingDragged) {
                    float deltaX = x - mTouchDownX;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocity = (int) mVelocityTracker.getXVelocity();
                    onFinishDrag(deltaX, velocity, true);
                }
                break;
        }

        return true;
    }

    private void onBeginDrag() {
        if (DEBUG) {
            Log.d(TAG, "==onBeginDrag");
        }
        mIsExiting = false;
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            mViewBehind = mHostCallbacks.onGetViewBehind(mHost);
            if (mViewBehind == mHost) {
                mViewBehind = null;
            } else {
                checkViewBehind(mHost.getMeasuredWidth(),
                        mHost.getMeasuredHeight());
            }
        } else {
            mIndicatorPercentage = 0;
        }
    }

    private void onDrag(int deltaX) {
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            if (deltaX != 0) {
                mHost.scrollBy(deltaX, 0);
            }
        } else {
            mIndicatorPercentage = (float) Math.abs(mLastTouchX - mTouchDownX)
                    / (float) mHost.getMeasuredWidth();
        }
        mHost.invalidate();
    }

    private void onFinishDrag(float deltaX, int velocity,
                              boolean forceSnapToDefault) {
        if (DEBUG) {
            Log.d(TAG, "==onFinishDrag");
        }

        mPossibleTargetView = null;
        mIsBeingDragged = false;
        mIsUnableToDrag = false;

        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            snapToDestinationForScrollWindow(deltaX, velocity,
                    forceSnapToDefault);
        }
    }

    private void onFinishScroll() {
        if (DEBUG) {
            Log.d(TAG, "==onFinishScroll");
        }
        mTouchState = TOUCH_STATE_REST;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            if (DEBUG) {
                Log.d(TAG, "mScroller is not finished, abort it.3");
            }
        }
        if (mIsExiting) {
            if (DEBUG) {
                Log.d(TAG, "is exiting, post a runnable.");
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (DEBUG) {
                        Log.d(TAG, "call mHostCallbacks.onWindowForceExit.");
                    }
                    mHostCallbacks
                            .onWindowExitEvent((AbstractWindow) mHost, mSwipeEffect == SwipeEffect.SHOW_INDICATOR);
                }
            });
        }
    }

    private void snapToDestinationForScrollWindow(float deltaX, int velocity,
                                                  boolean forceSnapToDefault) {
        if (DEBUG) {
            Log.d(TAG, "==snapToDestinationForScrollWindow");
        }

        boolean snapToDefaultPosition = true;

        if (!forceSnapToDefault) {
            if (Math.abs(deltaX) > mFlingDistance
                    && Math.abs(velocity) > mMinimumVelocity) {
                snapToDefaultPosition = velocity < 0 ? true : false;
            } else if (Math.abs(mHost.getScrollX()) > mHost.getMeasuredWidth()
                    / SCREEN_WIDTH_DIVISION_FACTOR) {
                snapToDefaultPosition = false;
            }
        }

        mIsExiting = !snapToDefaultPosition;

        final int currentScrollX = mHost.getScrollX();
        final int newScrollX = snapToDefaultPosition ? 0 : -mHost
                .getMeasuredWidth();

        if (DEBUG) {
            Log.d(TAG, "deltaX: " + deltaX);
            Log.d(TAG, "velocity: " + velocity);
            Log.d(TAG, "newScrollX: " + newScrollX);
            Log.d(TAG, "mIsExiting: " + mIsExiting);
        }

        if (!mScroller.isFinished()) {
            if (DEBUG) {
                Log.d(TAG, "mScroller is not finished, do nothing");
            }
            return;
        }

        final int deltaScrollX = newScrollX - currentScrollX;
        final int duration = calculateDuration(deltaScrollX);

        if (DEBUG) {
            Log.d(TAG, "deltaScrollX: " + deltaScrollX);
            Log.d(TAG, "duration: " + duration);
        }

        mTouchState = TOUCH_STATE_SETTLLING;
        mScroller.startScroll(currentScrollX, 0, deltaScrollX, 0, duration);
        mHost.invalidate();
    }

    private int calculateDuration(int deltaDistance) {
        if (DEBUG) {
            Log.d(TAG, "==calculateDuration");
        }
        float duration = mScrollDuration;
        float width = mHost.getMeasuredWidth();
        if (width > 0) {
            duration = (Math.abs(deltaDistance) / width + 1) * mScrollDuration
                    / 2;
            duration = Math.min(duration, MAX_SCROLL_DURATION);
        }
        return (int) duration;
    }


    public void draw(Canvas canvas, Rect dst) {
        if (mSwipeEffect == SwipeEffect.SCROLL_WINDOW) {
            drawForScrollWindow(canvas, dst);
        }
    }

    private void drawForScrollWindow(Canvas canvas, Rect dst) {
        if (DEBUG) {
            Log.d(TAG, "==drawForScrollWindow");
        }
        int hostScrollX = mHost.getScrollX();
        if (hostScrollX < 0) {
            int alpha = (int) ((1.0f - mScrollPercentage) * MAX_ALPHA);
            int hostHeight = mHost.getMeasuredHeight();

            if (DEBUG) {
                Log.d(TAG, "alpha: " + alpha + ", hostScrollX: " + hostScrollX);
            }

            if (mViewBehind != null) {
                canvas.save();
                canvas.translate(hostScrollX, 0);
                canvas.clipRect(0, 0, -hostScrollX, hostHeight);
                mViewBehind.draw(canvas);
                mDimMaskDrawable.setAlpha(alpha);
                mDimMaskDrawable.setBounds(0, 0, -hostScrollX, hostHeight);
                mDimMaskDrawable.draw(canvas);
                canvas.restore();
            }
        }
    }

}
