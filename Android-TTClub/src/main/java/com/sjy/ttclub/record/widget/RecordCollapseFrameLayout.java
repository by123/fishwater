package com.sjy.ttclub.record.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.record.datepicker.views.DatePicker;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhxu on 2015/12/17.
 * Email:357599859@qq.com
 */
public class RecordCollapseFrameLayout extends FrameLayout {

    public final static String TAG = "RecordCollapseFrameLayout";

    private int mRecordTopViewMaxHeight, mRecordTopViewMinHeight;

    private int mHeight = 0;

    public RecordCollapseFrameLayout(Context context) {
        this(context, null, 0);
    }

    public RecordCollapseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public int getRecordTopViewMinHeight() {
        return mRecordTopViewMinHeight;
    }

    public int getRecordTopViewMaxHeight() {
        return mRecordTopViewMaxHeight;
    }

    public RecordCollapseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRecordTopViewMaxHeight = ResourceHelper.getDimen(R.dimen.space_345);
        mRecordTopViewMinHeight = ResourceHelper.getDimen(R.dimen.space_122);
        mHeight = (mRecordTopViewMaxHeight - mRecordTopViewMinHeight) / 3; //开始移动的高度
    }

    public void init() {
        getLayoutParams().height = mRecordTopViewMaxHeight;
        setMinimumHeight(mRecordTopViewMinHeight);
    }

    public boolean onAnimate(int changeHeight) {
        if (changeHeight <= mRecordTopViewMinHeight) {
            return false;
        }
        getLayoutParams().height = changeHeight;
        requestLayout();
        return false;
    }

    /**
     * 是否滚回顶部
     *
     * @return
     */
    public boolean isScrollTop() {
        return getLayoutParams().height < mRecordTopViewMaxHeight - mHeight;
    }

    /**
     * @param isShow true 展开 false 收缩
     * @return
     */
    public boolean onAnimate(final boolean isShow) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int changeHeight = isShow ? mRecordTopViewMaxHeight - getLayoutParams().height : -(getLayoutParams().height - mRecordTopViewMinHeight);
                getLayoutParams().height = (int) (getLayoutParams().height + (changeHeight * Float.parseFloat(animation.getAnimatedValue() + "")));
                requestLayout();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                ((DatePicker) getChildAt(0)).onAnimate(isShow);
            }
        });
        anim.start();
        return false;
    }
}
