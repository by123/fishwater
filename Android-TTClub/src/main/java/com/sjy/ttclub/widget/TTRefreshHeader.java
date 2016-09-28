/**
 *
 */
package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class TTRefreshHeader extends RelativeLayout implements PtrUIHandler {

    private TextView mRefreshText;
    private ImageView mRefreshImage;
    private ImageView mRefreshingImage;
    private AnimationDrawable mAnimationDrawable;
    private RelativeLayout mRootView;
    private int[] mRefreshImageArray = {
            R.drawable.new_refresh_1,
            R.drawable.new_refresh_2,
            R.drawable.new_refresh_3,
            R.drawable.new_refresh_4,
            R.drawable.new_refresh_5,
            R.drawable.new_refresh_6,
            R.drawable.new_refresh_7,
            R.drawable.new_refresh_8,
            R.drawable.new_refresh_9,
            R.drawable.new_refresh_10,
            R.drawable.new_refresh_11,
            R.drawable.new_refresh_12,
            R.drawable.new_refresh_13,
            R.drawable.new_refresh_14,
            R.drawable.new_refresh_15,
            R.drawable.new_refresh_16,
            R.drawable.new_refresh_17,
            R.drawable.new_refresh_18,
            R.drawable.new_refresh_19,
            R.drawable.new_refresh_20,
            R.drawable.new_refresh_20,
    };

    public TTRefreshHeader(Context context) {
        this(context, null);
    }

    public TTRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TTRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRootView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.tt_refresh_header_layout, this);
        mRefreshText = (TextView) mRootView.findViewById(R.id.refresh_text);
        mRefreshImage = (ImageView) mRootView.findViewById(R.id.refresh_image);
        mRefreshingImage = (ImageView) mRootView.findViewById(R.id.refreshing_image);
        mAnimationDrawable = (AnimationDrawable) mRefreshingImage.getDrawable();
        reset();
    }

    private void startAnim(float percent) {
        mRootView.setVisibility(View.VISIBLE);
        mRefreshImage.setVisibility(View.VISIBLE);
        int index = (int) ((percent * 100) / 5);
        index = index % mRefreshImageArray.length;
        mRefreshImage.setImageResource(mRefreshImageArray[index]);
    }

    private void startRefreshing() {
        mRootView.setVisibility(View.VISIBLE);
        mRefreshingImage.setVisibility(View.VISIBLE);
        if (mAnimationDrawable != null) {
            mRefreshingImage.clearAnimation();
            mAnimationDrawable.start();
        }
    }

    private void stopAnim() {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    private void reset() {
        mRefreshingImage.setVisibility(View.GONE);
        mRefreshImage.setVisibility(View.GONE);
        mRefreshingImage.clearAnimation();
        mRootView.setVisibility(View.GONE);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        reset();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mRootView.setVisibility(View.VISIBLE);
        mRefreshText.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mRefreshText.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            mRefreshText.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mRootView.setVisibility(View.VISIBLE);
        mRefreshText.setVisibility(VISIBLE);
        mRefreshText.setText(R.string.cube_ptr_refreshing);
        mRefreshImage.setVisibility(View.GONE);
        startRefreshing();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mRefreshText.setText(R.string.cube_ptr_refresh_complete);
        mRefreshText.setVisibility(VISIBLE);
        stopAnim();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch,
                                   byte status, PtrIndicator ptrIndicator) {
        float percent = Math.min(1f, ptrIndicator.getCurrentPercent());
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();
        if (currentPos < mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch(frame);
                invalidate();
                startAnim(percent);
            }
        } else if (currentPos > mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch(frame);
            }
        }
    }

    private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
        if (!frame.isPullToRefresh()) {
            mRefreshText.setVisibility(VISIBLE);
            mRefreshText.setText(R.string.cube_ptr_release_to_refresh);
        }
    }

    private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
        mRefreshText.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mRefreshText.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            mRefreshText.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        }
    }

}
