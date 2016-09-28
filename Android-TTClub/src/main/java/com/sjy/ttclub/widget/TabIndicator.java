
package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class TabIndicator extends View {

    private static final String TAG = "TabIndicator";

    private static final int NO_SCROLL = 0;
    private static final int SCROLL_TO_LEFT_TAB = 1;
    private static final int SCROLL_TO_RIGHT_TAB = 2;

    private int mTabCount;
    private int mCurrentIndex = -1;
    private float mOffsetPercent = 0;
    private int mScrollState = NO_SCROLL;

    private int mCurrentDotWidth = 25;
    private int mNormalDotWidth = 4;
    private int mNormalDotHeight = 4;
    private int mDotSpace = 4;

    private int mDotXRadius = 2;
    private int mDotYRadius = 2;

    private Paint mPaint;
    private RectF[] mRects = null;

    public TabIndicator(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
    }

    /**
     * 设置页数。 注意：这个方法会将最后 一个页面设置为当前页。
     */
    public void setTabCount(int tabCount) {
        if (tabCount < 0 || tabCount == mTabCount) {
            return;
        }

        mTabCount = tabCount;
        if (mTabCount == 0) {
            mCurrentIndex = -1;
        } else {
            mCurrentIndex = mTabCount - 1;
        }

        mRects = new RectF[mTabCount];
        for (int i = 0; i < mTabCount; ++i) {
            mRects[i] = new RectF();
        }

        checkLayoutWidth();
        invalidate();
    }

    /**
     * @param index
     *            current tab index, must be a valid index between [0,
     *            tabCount).
     */
    public void setCurrentTab(int index) {
        if (index < 0 || index >= mTabCount) {
            return;
        }

        mScrollState = NO_SCROLL;
        mCurrentIndex = index;
        checkLayoutWidth();
        invalidate();
    }

    public int getCurrentTab() {
        return mCurrentIndex;
    }

    /**
     * 显示滑动到左边页面时的效果。
     * 
     * @param percent
     *            滑动的像素占控件宽度的比例
     */
    public void scrollToLeftTab(float percent) {
        scrollTab(SCROLL_TO_LEFT_TAB, percent);
    }

    /**
     * 显示滑动到右边页面时的效果。
     * 
     * @param percent
     *            滑动的像素占控件宽度的比例
     */
    public void scrollToRightTab(float percent) {
        scrollTab(SCROLL_TO_RIGHT_TAB, percent);
    }

    public void setDotColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setDotWidth(int dotWidth) {
        if (dotWidth < 0) {
            return;
        }

        mNormalDotWidth = dotWidth;
        mDotXRadius = dotWidth / 2;
        checkLayoutWidth();
        invalidate();
    }

    public void setDotHeight(int dotHeight) {
        if (dotHeight < 0) {
            return;
        }

        mNormalDotHeight = dotHeight;
        mDotYRadius = dotHeight / 2;
        checkLayoutHeight();
        invalidate();
    }

    public void setDotSpace(int dotSpace) {
        if (dotSpace < 0) {
            return;
        }

        mDotSpace = dotSpace;
        checkLayoutWidth();
        invalidate();
    }

    public void setCurrentDotWidth(int width) {
        if (width < 0) {
            return;
        }

        mCurrentDotWidth = width;
        checkLayoutWidth();
        invalidate();
    }

    public void setDotRadius(int xRadius, int yRadius) {
        if (mDotXRadius == xRadius && mDotYRadius == yRadius) {
            return;
        }

        mDotXRadius = xRadius;
        mDotYRadius = yRadius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mTabCount; ++i) {
            canvas.drawRoundRect(mRects[i], mDotXRadius, mDotYRadius, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        computeDotRect();
    }

    /**
     * 重新实现onMeasure()，使重写的getDefaultSize()静态方法生效。
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        int result = super.getSuggestedMinimumWidth();
        if (0 != mTabCount) {
            int suggest = getPaddingLeft() + getPaddingRight()
                    + getNeededMinimumWidth();
            result = Math.max(result, suggest);
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        int result = super.getSuggestedMinimumHeight();
        if (0 != mTabCount) {
            int suggest = getPaddingTop() + getPaddingBottom()
                    + getNeededMinimumHeight();
            result = Math.max(result, suggest);
        }
        return result;
    }

    /**
     * View.getDefaultSize()在未指定确切的大小时会直接使用剩余的所有空间。
     * 当其他控件设置了layout_weight而本控件未设置时，由于需要先计算本控件空间， 默认的设置会将剩余空间填满而导致其他控件无法显示。
     * 这里重写了View的静态方法，如果没有明确指定控件大小，则返回所需的最小空间。
     */
    public static int getDefaultSize(int size, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        }
        return size;
    }

    private int getNeededMinimumWidth() {
        if (mTabCount <= 0) {
            return 0;
        }
        return mCurrentDotWidth + (mNormalDotWidth + mDotSpace)
                * (mTabCount - 1);
    }

    private int getNeededMinimumHeight() {
        return mNormalDotHeight;
    }

    /**
     * Inner implementation for scrollToLeftTab() and scrollToRightTab().
     * 
     * @param state
     *            can be NO_SCROLL, SCROLL_TO_LEFT_TAB, or SCROLL_TO_RIGHT_TAB.
     */
    private void scrollTab(int state, float percent) {
        mOffsetPercent = percent;
        mScrollState = state;
        computeDotRect();
        invalidate();
    }

    /**
     * 如果宽度不足，则请求重新布局。否则直接计算点的RectF数组。
     */
    private void checkLayoutWidth() {
        if (getWidth() < getSuggestedMinimumWidth()) {
            requestLayout();
        } else {
            computeDotRect();
        }
    }

    /**
     * 如果高度不足，则请求重新布局。否则直接计算点的RectF数组。
     */
    private void checkLayoutHeight() {
        if (getHeight() < getSuggestedMinimumHeight()) {
            requestLayout();
        } else {
            computeDotRect();
        }
    }

    /**
     * 计算并保存每一个指示点的大小和位置到mRects，用于onDraw()。 当控件大小或指示点的大小等状态改变时需要调用此方法重新计算。
     */
    private void computeDotRect() {
        if (mRects == null) { // no tab
            return;
        }

        float startX = (getWidth() - getNeededMinimumWidth()) / 2.0f;
        float startY = (getHeight() - getNeededMinimumHeight()) / 2.0f;

        float itemOffset = (mCurrentDotWidth - mNormalDotWidth)
                * mOffsetPercent; // 当前点减少的像素
        float itemWidth = 0;
        for (int i = 0; i < mTabCount; ++i) {
            if (i == mCurrentIndex) { // current tab
                itemWidth = (mScrollState == NO_SCROLL) ? mCurrentDotWidth
                        : mCurrentDotWidth - itemOffset;
            } else if (i == (mCurrentIndex - 1)) { // the first tab that left to
                                                   // current tab
                itemWidth = (mScrollState == SCROLL_TO_LEFT_TAB) ? mNormalDotWidth
                        + itemOffset
                        : mNormalDotWidth;
            } else if (i == (mCurrentIndex + 1)) { // the first tab that right
                                                   // to current tab
                itemWidth = (mScrollState == SCROLL_TO_RIGHT_TAB) ? mNormalDotWidth
                        + itemOffset
                        : mNormalDotWidth;
            } else { // other tab
                itemWidth = mNormalDotWidth;
            }

            mRects[i].set(startX, startY, startX + itemWidth, startY
                    + mNormalDotHeight);
            startX += itemWidth + mDotSpace;
        }

        if (mOffsetPercent == 1.0) { // effect on next compute
            mScrollState = NO_SCROLL;
        }
    }

}
