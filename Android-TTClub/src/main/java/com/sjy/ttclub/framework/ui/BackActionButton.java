/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

public class BackActionButton extends LinearLayout {

    private static final int DEFAULT_ALPHA = 255;
    private static final int DISABLED_ALPHA = 90;
    private static final int PR_ALPHA = 128;

    private static final float DEFAULT_ALPHA_FLOAT = 1.0f;
    private static final float DISABLED_ALPHA_FLOAT = 0.4f;
    private static final float PR_ALPHA_FLOAT = 0.6f;

    private ImageView mImageView;
    private TextView mTitleTextView;

    public BackActionButton(Context context) {
        super(context);
        initComponent();
    }

    private void initComponent() {

        mImageView = new ImageView(getContext());
        int padding = ResourceHelper
                .getDimen(R.dimen.titlebar_action_item_padding);
        mImageView.setPadding(padding, 0, padding, 0);
        mImageView.setImageDrawable(ResourceHelper
                .getDrawable(R.drawable.title_back_gray));

        mTitleTextView = new TextView(getContext());
        mTitleTextView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                ResourceHelper.getDimen(R.dimen.defaultwindow_title_text_size));
        int titleTextPadding = ResourceHelper
                .getDimen(R.dimen.titlebar_title_text_padding);
        mTitleTextView.setPadding(0, 0, titleTextPadding, 0);
        mTitleTextView.setGravity(Gravity.CENTER);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TruncateAt.END);
        mTitleTextView.setVisibility(View.GONE);
        mTitleTextView.setTextColor(ResourceHelper
                .getColor(R.color.title_text_color));
        this.addView(mImageView);
        this.addView(mTitleTextView);

    }

    public void setBackActionIcon(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setTextColor(int color) {
        mTitleTextView.setTextColor(color);
    }

    public TextView getTextView() {
        return mTitleTextView;
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
        if (mImageView != null) {
            if (needBgDrawable) {
                mImageView.setAlpha(PR_ALPHA);
            } else {
                mImageView.setAlpha(DEFAULT_ALPHA);
            }
        }

        if (mTitleTextView != null) {
            if (needBgDrawable) {
                mTitleTextView.setAlpha(PR_ALPHA_FLOAT);
            } else {
                mTitleTextView.setAlpha(DEFAULT_ALPHA_FLOAT);
            }
        }
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

        if (mTitleTextView != null) {
            if (enabled) {
                mTitleTextView.setAlpha(DEFAULT_ALPHA_FLOAT);
            } else {
                mTitleTextView.setAlpha(DISABLED_ALPHA_FLOAT);
            }

        }

    }
}
