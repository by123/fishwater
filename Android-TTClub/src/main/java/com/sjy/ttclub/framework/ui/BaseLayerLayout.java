/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.util.SystemUtil;

public class BaseLayerLayout extends ViewGroup {

    /**
     * If type is unspecified, we treat it like width and height are all
     * match_parent.
     */
    public static final int TYPE_UNSPECIFIED = 0;
    public static final int TYPE_CONTENT_VIEW = 1;
    public static final int TYPE_TITLE_BAR = 2;
    public static final int TYPE_TOOL_BAR = 3;
    public static final int TYPE_STATUS_BAR = 4;
    /**
     * content view的位置会从title bar开始算
     */
    public static final int TYPE_CONTENT_VIEW_EXTENDS_TITLE_BAR = 5;

    /**
     * Fields below only be meaningful in onMeasure() and onLayout().
     */
    private List<View> mTitleBars = new ArrayList<View>();
    private List<View> mToolBars = new ArrayList<View>();
    private List<View> mContentViews = new ArrayList<View>();
    private List<View> mUnspecifiedViews = new ArrayList<View>();
    private List<View> mStatusBarViews = new ArrayList<View>();
    private int mTitleBarMaxHeight = 0;
    private int mToolBarMaxHeight = 0;
    private int mStatusBarHeight = 0;

    private List<Integer> mContentTypeList = new ArrayList<Integer>();

    public BaseLayerLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTitleBars.clear();
        mToolBars.clear();
        mContentViews.clear();
        mContentTypeList.clear();
        mUnspecifiedViews.clear();

        final int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                findSpecifiedView(child, lp);
            }
        }

        final int verticalPadding = this.getPaddingTop()
                + this.getPaddingBottom();
        final int horizontalPadding = this.getPaddingLeft()
                + this.getPaddingRight();
        final int maxWidth = MeasureSpec.getSize(widthMeasureSpec)
                - horizontalPadding;
        final int maxHeight = MeasureSpec.getSize(heightMeasureSpec)
                - verticalPadding;

        /**
         * Measure child with type of TITLE_STATUS_BAR.
         */
        final int statusBarCount = mStatusBarViews.size();
        mStatusBarHeight = (statusBarCount > 0 && SystemUtil
                .isTransparentStatusBarEnable()) ? DeviceManager.getInstance()
                .getWindowPaddingTop() : 0;
        for (int i = 0; i < statusBarCount; ++i) {
            final View child = mStatusBarViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = lp.leftMargin + lp.rightMargin;
            int childMaxWidth = maxWidth - childMarginHorizontal;
            child.measure(MeasureSpec.makeMeasureSpec(childMaxWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    mStatusBarHeight, MeasureSpec.EXACTLY));
        }

        /**
         * Measure child with type of TITLE_BAR.
         */
        final int titleBarCount = mTitleBars.size();
        for (int i = 0; i < titleBarCount; ++i) {
            final View child = mTitleBars.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = lp.leftMargin + lp.rightMargin;
            int childMaxWidth = maxWidth - childMarginHorizontal;
            child.measure(MeasureSpec.makeMeasureSpec(childMaxWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    lp.height, MeasureSpec.EXACTLY));

            int childMarginVertical = lp.topMargin + lp.bottomMargin;
            int childHeight = child.getMeasuredHeight() + childMarginVertical;
            if (mTitleBarMaxHeight < childHeight) {
                mTitleBarMaxHeight = childHeight;
            }
        }

        /**
         * Measure child with type of TOOL_BAR.
         */
        final int toolBarCount = mToolBars.size();
        for (int i = 0; i < toolBarCount; ++i) {
            final View child = mToolBars.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = lp.leftMargin + lp.rightMargin;
            int childMaxWidth = maxWidth - childMarginHorizontal;
            child.measure(MeasureSpec.makeMeasureSpec(childMaxWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    lp.height, MeasureSpec.EXACTLY));

            int childMarginVertical = lp.topMargin + lp.bottomMargin;
            int childHeight = child.getMeasuredHeight() + childMarginVertical;
            if (mToolBarMaxHeight < childHeight) {
                mToolBarMaxHeight = childHeight;
            }
        }

        /**
         * Measure child with type of CONTENT_VIEW.
         */
        final int contentCount = mContentViews.size();
        for (int i = 0; i < contentCount; ++i) {
            final View child = mContentViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = lp.leftMargin + lp.rightMargin;
            int childMarginVertical = lp.topMargin + lp.bottomMargin;
            int childMaxWidth = maxWidth - childMarginHorizontal;
            int childMaxHeight = maxHeight - mTitleBarMaxHeight
                    - mStatusBarHeight - mToolBarMaxHeight
                    - childMarginVertical;
            int type = mContentTypeList.get(i);
            if (type == TYPE_CONTENT_VIEW_EXTENDS_TITLE_BAR) {
                childMaxHeight = maxHeight - mToolBarMaxHeight
                        - childMarginVertical;
            }
            child.measure(MeasureSpec.makeMeasureSpec(childMaxWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    childMaxHeight, MeasureSpec.EXACTLY));
        }

        /**
         * Measure child with type of UNSPECIFIED.
         */
        final int unspecifiedCount = mUnspecifiedViews.size();
        for (int i = 0; i < unspecifiedCount; ++i) {
            final View child = mUnspecifiedViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = lp.leftMargin + lp.rightMargin;
            int childMarginVertical = lp.topMargin + lp.bottomMargin;
            int childMaxWidth = maxWidth - childMarginHorizontal;
            int childMaxHeight = maxHeight - childMarginVertical;
            child.measure(MeasureSpec.makeMeasureSpec(childMaxWidth,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    childMaxHeight, MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(
                getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        final int paddingBottom = this.getPaddingBottom();
        final int paddintTopIncludeStatusBar = paddingTop + mStatusBarHeight;

        int contentTop = paddintTopIncludeStatusBar + mTitleBarMaxHeight;
        int contentBottom = b - t - paddingBottom - mToolBarMaxHeight;

        /**
         * Layout child with type of TITLE_STATUS_BAR.
         */
        final int statusBarCount = mStatusBarViews.size();
        for (int i = 0; i < statusBarCount; ++i) {
            final View child = mStatusBarViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childLeft = paddingLeft + lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childTop = paddingTop + lp.topMargin;
            int childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        /**
         * Layout child with type of TITLE_BAR.
         */
        final int titleBarCount = mTitleBars.size();
        for (int i = 0; i < titleBarCount; ++i) {
            final View child = mTitleBars.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childLeft = paddingLeft + lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childTop = paddintTopIncludeStatusBar + lp.topMargin;
            int childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        /**
         * Layout child with type of TOOL_BAR.
         */
        final int toolBarCount = mToolBars.size();
        for (int i = 0; i < toolBarCount; ++i) {
            final View child = mToolBars.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childBottom = b - t - paddingBottom - lp.bottomMargin;
            int childTop = childBottom - child.getMeasuredHeight();
            int childLeft = paddingLeft + lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        /**
         * Layout child with type of CONTENT_VIEW.
         */
        final int contentCount = mContentViews.size();
        for (int i = 0; i < contentCount; ++i) {
            final View child = mContentViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childLeft = paddingLeft + lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childTop = contentTop + lp.topMargin;
            int childBottom = contentBottom - lp.bottomMargin;
            int type = mContentTypeList.get(i);
            if (type == TYPE_CONTENT_VIEW_EXTENDS_TITLE_BAR) {
                childTop = paddingTop;
            }
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        /**
         * Layout child with type of UNSPECIFIED.
         */
        final int unspecifiedCount = mUnspecifiedViews.size();
        for (int i = 0; i < unspecifiedCount; ++i) {
            final View child = mUnspecifiedViews.get(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childLeft = paddingLeft + lp.leftMargin;
            int childTop = paddintTopIncludeStatusBar + lp.topMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = b - t - paddingBottom - lp.bottomMargin;
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    private void findSpecifiedView(View child, LayoutParams lp) {
        switch (lp.type) {
        case TYPE_TITLE_BAR:
            mTitleBars.add(child);
            break;
        case TYPE_TOOL_BAR:
            mToolBars.add(child);
            break;
        case TYPE_CONTENT_VIEW:
        case TYPE_CONTENT_VIEW_EXTENDS_TITLE_BAR:
            mContentTypeList.add(lp.type);
            mContentViews.add(child);
            break;
        case TYPE_STATUS_BAR:
            mStatusBarViews.add(child);
            break;
        case TYPE_UNSPECIFIED:
        default:
            mUnspecifiedViews.add(child);
            break;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int type = BaseLayerLayout.TYPE_UNSPECIFIED;

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int type) {
            super(width, height);
            this.type = type;
        }
    }

}
