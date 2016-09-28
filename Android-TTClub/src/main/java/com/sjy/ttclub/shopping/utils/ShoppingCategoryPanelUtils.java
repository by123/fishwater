package com.sjy.ttclub.shopping.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhxu on 2015/12/30.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryPanelUtils implements View.OnClickListener {

    private LinearLayout mShoppingCategoryPanel;

    private FrameLayout mFloatView;

    private ICategoryPanelListener mCategoryPanelListener;

    public ShoppingCategoryPanelUtils(LinearLayout categoryPanel, FrameLayout floatView, int titleBarHeight) {

        mShoppingCategoryPanel = categoryPanel;
        mFloatView = floatView;

        final int floatViewTopMargin = titleBarHeight + ResourceHelper.getDimen(R.dimen.space_52);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = floatViewTopMargin;
        mFloatView.setLayoutParams(layoutParams);
        mFloatView.setAlpha(0);

        mFloatView.setOnClickListener(this);
    }

    public boolean isShow() {
        return mShoppingCategoryPanel.getLayoutParams().height != 0;
    }

    public void animator(ICategoryPanelListener categoryPanelListener) {
        mCategoryPanelListener = categoryPanelListener;
        final int maxHeight = ResourceHelper.getDimen(R.dimen.space_200);
        ValueAnimator anim;
        final boolean isShow = mShoppingCategoryPanel.getLayoutParams().height != 0;
        if (isShow) {
            anim = ValueAnimator.ofFloat(1f, 0);
        } else {
            anim = ValueAnimator.ofFloat(0, 1f);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mFloatView.setAlpha(value);
                mShoppingCategoryPanel.getLayoutParams().height = (int) (value * maxHeight);
                mShoppingCategoryPanel.requestLayout();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFloatView.setVisibility(!isShow ? View.VISIBLE : View.GONE);
                if (isShow) {
                    mCategoryPanelListener.hide();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mFloatView.setVisibility(View.VISIBLE);
            }
        });
        anim.setDuration(300);
        anim.start();
    }

    @Override
    public void onClick(View v) {
        if (mFloatView.getAlpha() != 0) {
            animator(mCategoryPanelListener);
        }
    }

    public interface ICategoryPanelListener {
        void hide();
    }
}
