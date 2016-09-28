/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

public class TitleBarActionItem extends FrameLayout {

    private static final float DEFAULT_ALPHA = 1.0f;
    private static final float DISABLED_ALPHA = 0.4f;
    private static final float PR_ALPHA = 0.6f;

    private TextView mTextView;
    private RedTipImageView mImageView;
    private int mItemId;
    private Drawable mDrawable;

    private boolean mTouchFeedbackEnabled = true;
    private boolean mIsShowRedTip = true; //

    public TitleBarActionItem(Context context) {
        this(context, null, 0);
    }

    public TitleBarActionItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarActionItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initResource();
    }

    public void setTouchFeedbackEnabled(boolean touchFeedbackEnabled) {
        mTouchFeedbackEnabled = touchFeedbackEnabled;
        refreshDrawableState();
    }

    private void init() {
        int padding = ResourceHelper
                .getDimen(R.dimen.titlebar_action_item_padding);
        this.setPadding(0, 0, padding, 0);
    }

    private void refreshTouchFeedbackDrawable() {
        if (mTouchFeedbackEnabled) {
            if (mTextView != null) {
                mTextView
                        .setTextColor(new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_pressed},
                                        new int[]{android.R.attr.state_enabled},
                                        new int[]{}},
                                new int[]{
                                        ResourceHelper
                                                .getColor(R.color.titlebar_item_pressed_color),
                                        ResourceHelper
                                                .getColor(R.color.titlebar_item_text_enable_color),
                                        ResourceHelper
                                                .getColor(R.color.titlebar_item_text_disable_color)}));
            }
        } else {
            if (mTextView != null) {
                mTextView
                        .setTextColor(new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_enabled},
                                        new int[]{}},
                                new int[]{
                                        ResourceHelper
                                                .getColor(R.color.titlebar_item_text_enable_color),
                                        ResourceHelper
                                                .getColor(R.color.titlebar_item_text_disable_color)}));
            }
        }
    }

    private void initResource() {
        if (mTextView != null) {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper
                    .getDimen(R.dimen.defaultwindow_title_right_size));
        }

        refreshImageDrawable();
        refreshTouchFeedbackDrawable();
    }

    private void refreshImageDrawable() {
        if (mImageView != null) {
            mImageView.setImageDrawable(mDrawable);
        }
    }

    public void setText(String text) {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        initResource();
        mTextView.setText(text);
    }

    public void setTextSize(int textSize) {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        refreshImageDrawable();
        refreshTouchFeedbackDrawable();
    }

    public void setTextBackground(Drawable drawable) {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        initResource();
        mTextView.setBackground(drawable);
    }

    public void setTextColor(ColorStateList resorceId) {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        mTextView.setTextColor(resorceId);
        refreshImageDrawable();
        refreshTouchFeedbackDrawable();
    }
    public void setTextColor(int resorceId) {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        mTextView.setTextColor(resorceId);
        refreshImageDrawable();
        refreshTouchFeedbackDrawable();

    }
    public TextView getTextView() {
        if (mTextView == null) {
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            this.addView(mTextView);
        }
        return mTextView;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setItemId(int id) {
        mItemId = id;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        if (mImageView == null) {
            mImageView = new RedTipImageView(getContext());
            mImageView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            int padding = ResourceHelper.getDimen(R.dimen.space_5);
            mImageView.setPadding(padding, padding, 0, padding);
            this.addView(mImageView);
        }
        refreshImageDrawable();
    }

    public void setRedTipVisibility(boolean isShow) {
        if (mImageView != null) {
            if (isShow) {
                mImageView.setTipVisibility(View.VISIBLE);
                mIsShowRedTip = true;
            } else {
                mImageView.setTipVisibility(View.INVISIBLE);
                mIsShowRedTip = false;
            }
        }
    }

    public void setRedTipText(String text) {
        if (mImageView != null) {
            mImageView.setTipText(text);
        }
    }

    public boolean getRedTipVisibility() {
        return mIsShowRedTip;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mImageView != null) {
            if (enabled) {
                mImageView.setAlpha(DEFAULT_ALPHA);

            } else {
                mImageView.setAlpha(DISABLED_ALPHA);
            }
        }

        if (mTextView != null) {
            mTextView.setEnabled(enabled);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        final boolean rt = super.onTouchEvent(event);
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    changeBackgroundDrawableState(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    /**
                     * post the change requestWithLoading to avoid the down and up drawable
                     * state changing happen at the same frame
                     */
                    post(new Runnable() {

                        @Override
                        public void run() {
                            changeBackgroundDrawableState(false);
                        }

                    });
                    break;
            }
        }
        return rt;
    }

    private void changeBackgroundDrawableState(boolean needBgDrawable) {
        if (mTouchFeedbackEnabled && mImageView != null) {
            if (needBgDrawable) {
                mImageView.setAlpha(PR_ALPHA);
            } else {
                mImageView.setAlpha(DEFAULT_ALPHA);
            }
        }
    }

    private static class RedTipImageView extends FrameLayout {

        private ImageView mImageView;
        private TextView mTipsView;

        public RedTipImageView(Context context) {
            super(context);
            mImageView = new ImageView(context);
            FrameLayout.LayoutParams lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            int padding = ResourceHelper.getDimen(R.dimen.space_5);
            mImageView.setPadding(padding, padding, padding, padding);
            this.addView(mImageView, lp);

            mTipsView = new TextView(context);
            mTipsView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_10));
            mTipsView.setTextColor(ResourceHelper.getColor(R.color.white));
            mTipsView.setBackgroundResource(R.drawable.round_radius_red);
            mTipsView.setVisibility(View.INVISIBLE);
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT | Gravity.TOP;
            this.addView(mTipsView, lp);
        }

        public void setImageDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

        public void setTipText(String text) {
            mTipsView.setText(text);
        }

        public void setTipVisibility(int visibility) {
            mTipsView.setVisibility(visibility);
        }


    }

}