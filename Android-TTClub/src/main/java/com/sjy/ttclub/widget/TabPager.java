
package com.sjy.ttclub.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

public class TabPager extends ViewGroup {

    public final static String TAG = "TabPager";

    public final static int OVERSCROLLED_STYLE_NOTHING = 0; // no effect.
    public final static int OVERSCROLLED_STYLE_EDGEGLOW = 1; // edge glow when
    // there are more
    // than 1 tab.
    public final static int OVERSCROLLED_STYLE_EDGEGLOW_ALWAYS = 2; // edge glow
    // even
    // there is
    // only 1
    // tab.
    public final static int OVERSCROLLED_STYLE_EDGEBOUNCE = 3; // edge bounce.

    public final static int INVALID_TAB = -1;
    public final static int DEFAULT_SCROLL_DURATION = 450; // ms
    public final static int MAX_SCROLL_DURATION = 600; // ms

    private final static int OVERSCROLLED_NONE = 0;
    private final static int OVERSCROLLED_LEFT = 1;
    private final static int OVERSCROLLED_RIGHT = 2;

    private final static int OVERSCROLLED_NONE_EVENT = 0;
    private final static int OVERSCROLLED_LEFT_EVENT = 1;
    private final static int OVERSCROLLED_RIGHT_EVENT = 2;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int TOUCH_STATE_SETTLLING = 2;

    private final static boolean DEBUG = false;
    private final static boolean DEBUG_TOUCHEVENT = false;

    private Scroller mScroller;
    private EdgeEffect mLeftEdgeEffect;
    private EdgeEffect mRightEdgeEffect;
    private TabPagerListener mListener;
    private TabPagerOverScrollListener mOverScrollListener;

    private int mChildWidthMeasureSpec;
    private int mChildHeightMeasureSpec;

    protected int mCurrentTabIndex = -1; // -1 indicates not initialized
    private int mNextTab = INVALID_TAB;
    private int mTouchState = TOUCH_STATE_REST;
    private int mOverScrolledStyle = OVERSCROLLED_STYLE_EDGEGLOW;

    private int mTouchSlop;
    protected int mTabMargin = 0;
    private int mEdgeBounceDragger = 1; // 在边界减小滑动距离
    private int mScrollDuration = DEFAULT_SCROLL_DURATION;

    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastPosX;
    private float mDeltaTime;
    private long mLastTime;
    private float mOverScrolledDistance = 0;

    private boolean mSlideToNextPage = false;
    private boolean mFirstLayout = true;

    private boolean mIsDynamicDurationEnabled = true;
    private boolean mIsDrawingCacheEnabled = false;
    private boolean mIsTouchEventLocked = false;
    private boolean mIsForceDrawAllChild = false;
    private boolean mIsBeingDragged = false;
    private boolean mIsUnableToDrag = false;
    private int mOverScrollState = OVERSCROLLED_NONE;
    private int mOverScrollEvent = OVERSCROLLED_NONE_EVENT;

    private List<ScrollableChildView> mScrollableChildViews;
    private View mTargetView;
    private int[] mTargetViewOffset = new int[2];

    private boolean mIgnoreChild = false;

    /**
     * 可滚动Child停止滚动并返回false后TabPager是否继续滚动
     */
    private boolean mScrollWhenChildStopSelfScroll;

    /**
     * 用户手指滑动是否允许.
     */
    private boolean mDragSwitchEnable = true;

    private static final Interpolator mInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public TabPager(Context context) {
        this(context, mInterpolator);
    }

    public TabPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(mInterpolator);
    }

    public TabPager(Context context, Interpolator interpolator) {
        super(context);
        init(interpolator);
    }

    protected void init(Interpolator interpolator) {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mScroller = new Scroller(context, interpolator);
        mLeftEdgeEffect = new EdgeEffect(ResourceHelper.getDrawable(R.drawable.tab_shadow_left));
        mRightEdgeEffect = new EdgeEffect(ResourceHelper.getDrawable(R.drawable.tab_shadow_left));
        mScrollableChildViews = new ArrayList<ScrollableChildView>();
        mTargetView = null;
        this.setFocusable(true);
        this.setWillNotDraw(false);
    }

    /**
     * 设置用户手指滑动是否允许，默认是true.
     *
     * @param aEnable
     */
    public void setDragSwitchEnable(boolean aEnable) {
        this.mDragSwitchEnable = aEnable;
    }

    @Override
    public void computeScroll() {
        if (DEBUG) {
            Log.d(TAG, "===computeScrollOffset " + this);
        }
        if (mScroller.computeScrollOffset()) {
            if (DEBUG) {
                Log.d(TAG, "CurrX, CurrY: " + mScroller.getCurrX() + ", "
                        + mScroller.getCurrY());
            }
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else if (mNextTab != INVALID_TAB) {
            mTouchState = TOUCH_STATE_REST;
            int oldTabIndex = mCurrentTabIndex;
            mCurrentTabIndex = Math.max(0, Math.min(mNextTab, getChildCount() - 1));
            mNextTab = INVALID_TAB;
            if (DEBUG) {
                Log.d(TAG, "scroll complete.");
                Log.d(TAG, "mCurrentTab: " + mCurrentTabIndex + " mNextTab: "
                        + mNextTab);
                Log.d(TAG, "scrollX: " + getScrollX() + " scrollY: "
                        + getScrollY());
            }

            onTabChanged(mCurrentTabIndex, oldTabIndex);

            if (this.mListener != null) {
                this.mListener.onTabChanged(mCurrentTabIndex, oldTabIndex);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // 这里是为了捕获由于系统在draw子类的时候没有判断子类为空导致的空指针崩溃
        try {
            super.draw(canvas);
        } catch (Throwable t) {
            // ExceptionHandler.processFatalException(t);
        }
        boolean needsInvalidate = false;
        int childCount = getChildCount();
        if (mOverScrolledStyle == OVERSCROLLED_STYLE_EDGEGLOW_ALWAYS
                || (mOverScrolledStyle == OVERSCROLLED_STYLE_EDGEGLOW && childCount > 1)) {
            final int width = getWidth();
            final int height = getHeight();
            if (!mLeftEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                mLeftEdgeEffect.setHeight(height);
                needsInvalidate |= mLeftEdgeEffect.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
            if (!mRightEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                canvas.rotate(180);
                canvas.translate(-(width + mTabMargin) * childCount, -height);
                mRightEdgeEffect.setHeight(height);
                needsInvalidate |= mRightEdgeEffect.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
        } else {
            if (mLeftEdgeEffect != null && mRightEdgeEffect != null) {
                mLeftEdgeEffect.finish();
                mRightEdgeEffect.finish();
            }
        }

        if (needsInvalidate) {
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return; // 为空时直接返回
        }
        if (DEBUG) {
            Log.d(TAG, "===dispatchDraw " + this);
        }
        final long drawingTime = getDrawingTime();

        if (!mIsForceDrawAllChild) {
            boolean fastDraw = mTouchState == TOUCH_STATE_REST
                    && !mIsBeingDragged && mNextTab == INVALID_TAB;
            if (fastDraw) {
                if (DEBUG) {
                    Log.d(TAG, "fastDraw.");
                    Log.d(TAG, "mCurrentTab: " + mCurrentTabIndex + " mNextTab: "
                            + mNextTab);
                    Log.d(TAG, "scrollX: " + getScrollX() + " scrollY: "
                            + getScrollY());
                }
                // 如果当前没有动作，则快速画当前Tab
                drawChild(canvas, getChildAt(mCurrentTabIndex), drawingTime);
                return;
            } else if (mNextTab >= 0 && mNextTab < getChildCount()
                    && Math.abs(mCurrentTabIndex - mNextTab) == 1) {
                if (DEBUG) {
                    Log.d(TAG, "Not fastDraw.");
                    Log.d(TAG, "mCurrentTab: " + mCurrentTabIndex + " mNextTab: "
                            + mNextTab);
                    Log.d(TAG, "scrollX: " + getScrollX() + " scrollY: "
                            + getScrollY());
                }
                // 如果当前是fling模式，则画当前Tab和下一个Tab
                drawChild(canvas, getChildAt(mCurrentTabIndex), drawingTime);
                drawChild(canvas, getChildAt(mNextTab), drawingTime);
                return;
            }
        }
        if (DEBUG) {
            Log.d(TAG, "Force draw all child.");
            Log.d(TAG, "mCurrentTab: " + mCurrentTabIndex + " mNextTab: " + mNextTab);
            Log.d(TAG, "scrollX: " + getScrollX() + " scrollY: " + getScaleY());
        }

        // 如果当前是scrolling或者forceDrawAllChild，则画所有Tab
        for (int i = 0; i < childCount; i++) {
            drawChild(canvas, getChildAt(i), drawingTime);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth()
                - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                MeasureSpec.EXACTLY);

        // Make sure all children have been properly measured.
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }

        if (mFirstLayout) {
            if (mCurrentTabIndex == -1) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        snapToTab(0, false);
                    }
                });
            }
            setChildDrawingCacheEnabled(mIsDrawingCacheEnabled);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {

        final int count = getChildCount();
        final int width = right - left;
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, childTop, childLeft + childWidth,
                        childTop + child.getMeasuredHeight());
                childLeft += width + mTabMargin;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int targetTab = (mNextTab != INVALID_TAB) ? mNextTab
                : mCurrentTabIndex;
        final int targetScrollX = targetTab * (w + mTabMargin);

        if (DEBUG) {
            Log.d(TAG, "===onSizeChanged " + this);
            Log.d(TAG, "mCurrentTab: " + mCurrentTabIndex + " mNextTab: " + mNextTab);
            Log.d(TAG, "targetTab: " + targetTab + " targetScrollX: "
                    + targetScrollX);
            Log.d(TAG, "scrollX: " + getScrollX() + " scrollY: " + getScrollY());
        }

        if (targetScrollX != getScrollX() || mTouchState != TOUCH_STATE_REST) {
            mScroller.abortAnimation();
            scrollTo(targetScrollX, getScrollY());
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mListener != null)
            this.mListener.onTabSliding(l, t);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentTabIndex() > 0) {
                snapToTab(getCurrentTabIndex() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentTabIndex() < getChildCount() - 1) {
                snapToTab(getCurrentTabIndex() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    /**
     * Calculate a targetView's offset of TabPager.
     *
     * @param targetView the view your want to calculate it's offset
     * @param location   an array of two integers for storing offset values
     * @return true if we have successfully calculated it's offset; otherwise
     * false, probably it's not inside <br/>
     * TabPager or it's inside a listView and currently not visible.
     */
    public boolean getTargetViewOffset(View targetView, int[] location) {
        if (targetView == null) {
            throw new IllegalArgumentException("targetView is null");
        }

        if (location == null || location.length < 2) {
            throw new IllegalArgumentException(
                    "location must be an array of two integers");
        }

        location[0] = 0;
        location[1] = 0;

        if (DEBUG_TOUCHEVENT) {
            Log.d(TAG, "getTargetViewOffset, Target: " + targetView);
        }

        if (!(targetView.getParent() instanceof View)) {
            return false;
        }

        View parent = (View) targetView.getParent();

        while (parent != null && parent != this) {
            if (DEBUG_TOUCHEVENT) {
                Log.d(TAG, "getTargetViewOffset, Parent: " + parent);
            }
            location[0] += parent.getScrollX() - parent.getLeft();
            location[1] += parent.getScrollY() - parent.getTop();

            final ViewParent viewParent = parent.getParent();
            if (viewParent != null && viewParent instanceof View) {
                parent = (View) viewParent;
            } else {
                parent = null;
            }
        }

        if (parent == this) {
            location[0] += this.getScrollX();
            location[1] += this.getScrollY();
            return true;
        } else {
            if (DEBUG_TOUCHEVENT) {
                Log.w(TAG,
                        "targetView is not inside TabPager! Probably it's inside a listView and currently not visible" +
                                ".");
            }
            return false;
        }

    }

    public boolean checkIfSpecialChildWantsIt(MotionEvent ev) {
        if (mTargetView != null) {
            mTargetView = null;
        }

        final int x = (int) ev.getX();
        final int y = (int) ev.getY();
        final int currentTab = mCurrentTabIndex;
        Rect hitRect = new Rect();
        for (ScrollableChildView scrollableView : mScrollableChildViews) {
            final View child = (View) scrollableView;
            if (child.getVisibility() == VISIBLE
                    && scrollableView.getTabIndex() == currentTab) {
                int realX = x;
                int realY = y;
                if (!getTargetViewOffset(child, mTargetViewOffset)) {
                    continue;
                }
                realX += mTargetViewOffset[0];
                realY += mTargetViewOffset[1];
                child.getHitRect(hitRect);
                if (hitRect.contains(realX, realY)) {
                    if (scrollableView.determineTouchEventPriority(ev)) {
                        mTargetView = child;
                        if (DEBUG_TOUCHEVENT) {
                            Log.d(TAG, "checkIfSpecialChildWantsIt, Child: "
                                    + child.getClass().toString()
                                    + " wants it.");
                        }
                        return true;
                    }
                }
            }
        }
        if (DEBUG_TOUCHEVENT) {
            Log.d(TAG, "checkIfSpecialChildWantsIt, NO ONE wants it.");
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean result = false;
        if (DEBUG_TOUCHEVENT) {
            Log.d(TAG, "dispatchTouchEvent, Action: " + action);
        }
        if (action == MotionEvent.ACTION_DOWN) {
            if (checkIfSpecialChildWantsIt(ev)) {
                if (DEBUG_TOUCHEVENT) {
                    Log.d(TAG, "dispatchTouchEvent, Action: " + action
                            + ", TabPager is Locked.");
                }
                mIsTouchEventLocked = true;
            }
        }

        if ((null != mTargetView) && (this.mDragSwitchEnable)) {
            final float realX = ev.getX() + mTargetViewOffset[0]
                    - mTargetView.getLeft();
            final float realY = ev.getY() + mTargetViewOffset[1]
                    - mTargetView.getTop();
            ev.setLocation(realX, realY);
            result = mTargetView.dispatchTouchEvent(ev);
            if (shouldScrollWhenChildStopSelfScroll()
                    && !result
                    && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)) {
                resetTargetView();
                MotionEvent fakeEvent = MotionEvent.obtain(ev);
                fakeEvent.setAction(MotionEvent.ACTION_DOWN);
                result = super.dispatchTouchEvent(fakeEvent);
            }
        } else {
            result = super.dispatchTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            resetTargetView();
        }
        return result;
    }


    private boolean shouldScrollWhenChildStopSelfScroll() {
        return mScrollWhenChildStopSelfScroll;
    }

    public void enableScrollWhenChildStopSelfScroll() {
        mScrollWhenChildStopSelfScroll = true;
    }

    public void disableScrollWhenChildStopSelfScroll() {
        mScrollWhenChildStopSelfScroll = false;
    }

    private void resetTargetView() {
        if (mTargetView != null) {
            if (DEBUG_TOUCHEVENT) {
                Log.d(TAG, "dispatchTouchEvent, TabPager is Unlocked.");
            }
            mIsTouchEventLocked = false;
            mTargetView = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (DEBUG_TOUCHEVENT) {
            Log.d(TAG, "onInterceptTouchEvent");
        }
        if (mIsTouchEventLocked) {
            return super.onInterceptTouchEvent(ev);
        }

        final int action = ev.getAction();

        // 不拦截以下动作，使手势完整完成
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            return false;
        }

        // 如果已经判断为拖动状态或无法拖动，则直接返回
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
            if (mIsUnableToDrag) {
                return false;
            }
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = x;
                mLastMotionY = y;
                mLastPosX = x;
                mLastTime = System.currentTimeMillis();
                mIsBeingDragged = false;
                mIsUnableToDrag = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (this.mDragSwitchEnable) {
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float yDiff = Math.abs(y - mLastMotionY);

                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        boolean scrollLeft = x - mLastMotionX > 0;
                        if (checkIfCanBeDragged(TabPager.this, (int) mLastMotionX, (int) mLastMotionY, scrollLeft)) {
                            onBeginDragged();
                            mIsBeingDragged = true;
                            mTouchState = TOUCH_STATE_SCROLLING;
                        }
                    } else if (yDiff > mTouchSlop) {
                        mIsUnableToDrag = true;
                    }
                }
                break;
            }
        }

        // 通知Child
        if (mIsBeingDragged) {
            notifyChildTouchCanceled();
        }
        // 只拦截拖动状态的事件
        return mIsBeingDragged | mIgnoreChild;
    }

    private boolean checkIfCanBeDragged(View viewToCheck, int x, int y, boolean scrollLeft) {
        if (DEBUG) {
            Log.d(TAG, "==checkIfCanBeDragged, viewToCheck: " + viewToCheck);
        }

        if (viewToCheck instanceof IScrollable) {
            boolean canScrolling = ((IScrollable) viewToCheck).canScroll(scrollLeft);
            if (canScrolling) {
                return false;
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
                    boolean canDraged = checkIfCanBeDragged(child, realX
                            - child.getLeft(), realY - child.getTop(), scrollLeft);
                    if (!canDraged) {
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

        return true;
    }

    protected void notifyChildTouchCanceled() {
        MotionEvent ev = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0,
                0, 0);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsTouchEventLocked) {
            return super.onTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong
            // to one of our
            // descendants.
            return false;
        }

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                mLastMotionY = y;
                mLastPosX = x;
                mLastTime = System.currentTimeMillis();

                break;
            case MotionEvent.ACTION_MOVE:
                if (this.mDragSwitchEnable) {

                    if (!mIsBeingDragged) {
                        final float xDiff = Math.abs(x - mLastMotionX);
                        final float yDiff = Math.abs(y - mLastMotionY);
                        if (xDiff > mTouchSlop && xDiff > yDiff) {
                            mLastPosX = x;
                            mIsBeingDragged = true;
                            mTouchState = TOUCH_STATE_SCROLLING;
                            onBeginDragged();
                        }
                    }

                    if (mIsBeingDragged) {
                        float deltaX = mLastPosX - x;
                        mLastPosX = x;

                        float currentX = getScrollX();
                        float targetX = currentX + deltaX;
                        float leftEdge = 0;
                        float rightEdge = (getWidth() + mTabMargin)
                                * (getChildCount() - 1);

                        if (mOverScrollState == OVERSCROLLED_NONE) {
                            if (targetX < leftEdge) {
                                mOverScrollState = OVERSCROLLED_LEFT;
                                mOverScrollEvent = OVERSCROLLED_LEFT_EVENT;
                            } else if (targetX > rightEdge) {
                                mOverScrollState = OVERSCROLLED_RIGHT;
                                mOverScrollEvent = OVERSCROLLED_RIGHT_EVENT;
                            } else {
                                mOverScrollEvent = OVERSCROLLED_NONE_EVENT;
                            }
                        }

                        if (mOverScrollState != OVERSCROLLED_NONE) {
                            mOverScrolledDistance += deltaX;

                            switch (mOverScrolledStyle) {
                                case OVERSCROLLED_STYLE_NOTHING:
                                    deltaX = 0;
                                    mOverScrollState = OVERSCROLLED_NONE;
                                    break;

                                case OVERSCROLLED_STYLE_EDGEGLOW:
                                case OVERSCROLLED_STYLE_EDGEGLOW_ALWAYS:
                                    if (mOverScrollState == OVERSCROLLED_LEFT) {
                                        mLeftEdgeEffect.onPull(deltaX / getWidth());
                                        if (mOverScrolledDistance >= 0) {
                                            mOverScrollState = OVERSCROLLED_NONE;
                                        }
                                    } else if (mOverScrollState == OVERSCROLLED_RIGHT) {
                                        mRightEdgeEffect.onPull(deltaX / getWidth());
                                        if (mOverScrolledDistance <= 0) {
                                            mOverScrollState = OVERSCROLLED_NONE;
                                        }
                                    }
                                    deltaX = 0;
                                    invalidate();
                                    break;

                                case OVERSCROLLED_STYLE_EDGEBOUNCE:
                                    deltaX /= mEdgeBounceDragger;
                                    break;
                            }
                        }

                        if (deltaX != 0) {
                            scrollBy((int) deltaX, 0);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {

                    long nowTime = System.currentTimeMillis();
                    mDeltaTime = nowTime - mLastTime;

                    float deltaX = x - mLastMotionX;
                    mSlideToNextPage = (Math.abs(deltaX) / mDeltaTime) > 0.3f;

                    if (mSlideToNextPage) {
                        snapToNextTab(deltaX < 0.0f);
                    } else {
                        snapToDestination();
                    }
                    mSlideToNextPage = false;
                    if (mOverScrollListener != null) {
                        if (mOverScrollEvent == OVERSCROLLED_LEFT_EVENT) {
                            mOverScrollListener.onLeftOverScroll();
                        } else if (mOverScrollEvent == OVERSCROLLED_RIGHT_EVENT) {
                            mOverScrollListener.onRightOverScroll();
                        }
                    }
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    snapToDestination();
                    endDrag();
                }
                break;
        }

        return true;
    }

    protected void onBeginDragged() {
    }

    protected void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mOverScrollState = OVERSCROLLED_NONE;
        mOverScrollEvent = OVERSCROLLED_NONE_EVENT;
        mOverScrolledDistance = 0;
        if (mLeftEdgeEffect != null && mRightEdgeEffect != null) {
            mLeftEdgeEffect.onRelease();
            mRightEdgeEffect.onRelease();
            boolean needsInvalidate = mLeftEdgeEffect.isFinished()
                    | mRightEdgeEffect.isFinished();
            if (needsInvalidate)
                invalidate();
        }
    }

    public void releaseDragging() {
        snapToDestination(false);
        endDrag();
    }

    public void addScrollableViews(ScrollableChildView view) {
        if (!mScrollableChildViews.contains(view)) {
            mScrollableChildViews.add(view);
        }
    }

    public void deleteScrollableViews(ScrollableChildView view) {
        mScrollableChildViews.remove(view);
    }

    public void clearScrollableViews() {
        mScrollableChildViews.clear();
    }

    /**
     * Force the TabPager to draw all child or not.
     *
     * @param force force or not
     */
    public void forceDrawAllChild(boolean force) {
        mIsForceDrawAllChild = force;
    }

    /**
     * Force the TabPager to be or Not to be in Dragging-Mode.
     *
     * @param force force or not
     */
    public void forceDrag(boolean force) {
        mIsBeingDragged = force;
    }

    public void lock() {
        mIsTouchEventLocked = true;
        if (mTouchState != TOUCH_STATE_REST) {
            releaseDragging();
        }
    }

    public void unlock() {
        mIsTouchEventLocked = false;
    }

    public View getCurrentTabView() {
        return getTabView(mCurrentTabIndex);
    }

    public View getTabView(int index) {
        if (index >= 0 && index < getChildCount()) {
            return getChildAt(index);
        }
        return null;
    }

    public void replaceView(int index, View view) {
        removeViewAt(index);
        ViewParent viewParent = view.getParent();
        if (viewParent != null) {
            ((ViewGroup) viewParent).removeView(view);
        }
        addView(view, index);
    }

    /**
     * Get the index of the current tab, started from 0.
     *
     * @return index of the current tab, started from 0.
     */
    public int getCurrentTabIndex() {
        return mCurrentTabIndex;
    }

    public View getCurrentTab() {
        if (mCurrentTabIndex < getChildCount()) {
            return getChildAt(mCurrentTabIndex);
        }
        return null;
    }

    public int getNextTab() {
        return mNextTab;
    }

    public int getOverScrolledStyle() {
        return mOverScrolledStyle;
    }

    public void setOverScrolledStyle(int style) {
        this.mOverScrolledStyle = style;
    }

    public int getTabMargin() {
        return mTabMargin;
    }

    public void setTabMargin(int margin) {
        this.mTabMargin = margin;
        this.requestLayout();
    }

    public int getScrollDuration() {
        return this.mScrollDuration;
    }

    public void setScrollDuration(int duration) {
        this.mScrollDuration = duration;
    }

    public void setEdgeBouceDragger(int dragger) {
        this.mEdgeBounceDragger = dragger;
    }

    public void setEdgeEffect(EdgeEffect effect, boolean left) {
        if (left) {
            this.mLeftEdgeEffect = effect;
        } else {
            this.mRightEdgeEffect = effect;
        }
    }

    public void setEdgeEffect(Drawable leftDrawable, Drawable rightDrawable) {
        EdgeEffect leftEffect = new EdgeEffect(leftDrawable);
        EdgeEffect rightEffect = new EdgeEffect(rightDrawable);
        this.mLeftEdgeEffect = leftEffect;
        this.mRightEdgeEffect = rightEffect;
    }

    public void setDrawingCacheEnabled(boolean enabled) {
        mIsDrawingCacheEnabled = enabled;
    }

    public void setDynamicDurationEnabled(boolean enabled) {
        mIsDynamicDurationEnabled = enabled;
    }

    private void setChildDrawingCacheEnabled(boolean enabled) {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(enabled);
            }
        }
    }

    protected void snapToDestination() {
        snapToDestination(true);
    }

    protected void snapToDestination(boolean withAnimation) {
        final int tabWidth = getMeasuredWidth() + mTabMargin;
        if (tabWidth == 0) {
            return;
        }
        final int whichTab = (getScrollX() + (tabWidth / 2)) / tabWidth;
        snapToTab(whichTab, withAnimation);
    }

    public void snapToNextTab(boolean forward) {
        if (forward) {
            snapToTab(this.mCurrentTabIndex + 1);
        } else {
            snapToTab(this.mCurrentTabIndex - 1);
        }
    }

    /**
     * Scroll to the tab with animation and tell the listener.
     *
     * @param whichTab index of the tab you wish to scroll to.
     */
    public void snapToTab(int whichTab) {
        snapToTab(whichTab, true);
    }

    /**
     * Scroll to the tab. If withAnimation is false, it will scroll to the
     * position immediately.
     *
     * @param whichTab index of the tab you wish to scroll to.
     */
    public void snapToTab(int whichTab, boolean withAnimation) {

        if (withAnimation) {

            this.resetPrivateFlagDrawn();

            if (!mScroller.isFinished()) {
                return;
            }
            if (whichTab >= getChildCount() && this.mListener != null) {
                if (this.mListener.onTabSlideOut()) {
                    return;
                }
            }

            whichTab = Math.max(0, Math.min(whichTab, getChildCount() - 1));
            mNextTab = whichTab;

            final int currentX = getScrollX();
            final int newX = whichTab * (getMeasuredWidth() + mTabMargin);
            final int delta = newX - currentX;

            if (delta == 0) {
                return;
            }

            final int duration = calculateDuration(delta);
            mTouchState = TOUCH_STATE_SETTLLING;
            mScroller.startScroll(currentX, 0, delta, 0, duration);

            if (this.mListener != null) {
                this.mListener.onTabChangeStart(mNextTab, mCurrentTabIndex);
            }
        } else {
            int oldTabIndex = mCurrentTabIndex;
            mCurrentTabIndex = Math.max(0, Math.min(whichTab, getChildCount() - 1));
            scrollTo(mCurrentTabIndex * (getMeasuredWidth() + mTabMargin), 0);

            onTabChanged(mCurrentTabIndex, oldTabIndex);
            if (this.mListener != null) {
                this.mListener.onTabChanged(mCurrentTabIndex, oldTabIndex);
            }
        }
        this.invalidate();
    }

    /**
     * 当tab正在滑过程中，如果上层View隐藏了(如TabWidget),再次显示后TabPager会因为privateFlags
     * DRAWN为false而不能正常绘制.调用此方法以修复privateFlags设置.
     */
    public void resetPrivateFlagDrawn() {
        this.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
    }

    private int calculateDuration(int deltaDistance) {
        float duration = mScrollDuration;
        if (mIsDynamicDurationEnabled) {
            final float width = getMeasuredWidth() + mTabMargin;
            if (width > 0) {
                duration = (Math.abs(deltaDistance) / width + 1)
                        * mScrollDuration / 2;
                duration = Math.min(duration, MAX_SCROLL_DURATION);
            }
        }
        return (int) duration;
    }

    public void setListener(TabPagerListener listener) {
        this.mListener = listener;
    }

    public void setOverScrollListener(TabPagerOverScrollListener listener) {
        this.mOverScrollListener = listener;
    }

    public boolean isScrolling() {
        return mTouchState != TOUCH_STATE_REST;
    }

    public boolean isAutoScrolling() {
        return mTouchState == TOUCH_STATE_SETTLLING;
    }

    public boolean isDragging() {
        return mTouchState == TOUCH_STATE_SCROLLING;
    }

    public void setIgnoreChild(boolean ignoreChild) {
        mIgnoreChild = ignoreChild;
    }

    protected void onTabChanged(int newTabIndex, int oldTabIndex) {
    }

    public interface ScrollableChildView {
        public boolean determineTouchEventPriority(MotionEvent ev);

        public int getTabIndex();
    }

    public interface IScrollable {
        boolean canScroll(boolean scrollLeft);
    }

    public void forceFinished(boolean b) {
        mScroller.forceFinished(b);
    }

}
