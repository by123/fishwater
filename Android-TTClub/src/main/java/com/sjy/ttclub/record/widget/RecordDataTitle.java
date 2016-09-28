package com.sjy.ttclub.record.widget;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by linhz on 2016/1/11.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataTitle implements View.OnClickListener {

    private View mLeftArrow;
    private View mRightArrow;
    private TextView mTitleView;

    private int mMaxIndex = 3;
    private int mCurrentIndex = 0;

    private RecordDataTitleListener mListener;

    private View mRootView;
    private View mTopView;

    public void initTitleView(View view, boolean isPeepTitle) {
        mRootView = view;
        mLeftArrow = view.findViewById(R.id.record_data_title_left_arrow);
        mLeftArrow.setOnClickListener(this);
        mRightArrow = view.findViewById(R.id.record_data_title_right_arrow);
        mRightArrow.setOnClickListener(this);
        mTitleView = (TextView) view.findViewById(R.id.record_data_title_text);
        if (!isPeepTitle) {
            view.setBackgroundColor(ResourceHelper.getColor(R.color.white));
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_18));
            mTitleView.setTextColor(ResourceHelper.getColor(R.color.title_color));
            mTitleView.setBackgroundColor(Color.TRANSPARENT);
            FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) mTitleView.getLayoutParams();
            lp.bottomMargin = 0;
            mTitleView.setLayoutParams(lp);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.record_data_title_left_arrow) {
            if (mListener != null) {
                mListener.onRecordDataPageSelected(mCurrentIndex - 1);
            }
        } else if (id == R.id.record_data_title_right_arrow) {
            if (mListener != null) {
                mListener.onRecordDataPageSelected(mCurrentIndex + 1);
            }
        }
    }

    public void setTopView(View topView) {
        mTopView = topView;
    }

    public void setTitleText(String text) {
        mTitleView.setText(text);
    }

    public void setCurrentIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        if (index >= mMaxIndex) {
            index = mMaxIndex - 1;
        }
        mCurrentIndex = index;
        if (mCurrentIndex == 0) {
            mLeftArrow.setVisibility(View.INVISIBLE);
            mRightArrow.setVisibility(View.VISIBLE);
        } else if (mCurrentIndex == mMaxIndex - 1) {
            mRightArrow.setVisibility(View.INVISIBLE);
            mLeftArrow.setVisibility(View.VISIBLE);
        } else {
            mRightArrow.setVisibility(View.VISIBLE);
            mLeftArrow.setVisibility(View.VISIBLE);
        }
    }

    public View getRootView() {
        return mRootView;
    }

    public int getLocationY() {
        int posY = 0;
        if (mTopView == null) {
            return posY;
        }
        int height = mTopView.getHeight();
        if (height <= 0) {
            return posY;
        }
        ViewGroup.LayoutParams lp = mRootView.getLayoutParams();
        int topMargin = 0;
        if (lp instanceof LinearLayout.LayoutParams) {
            topMargin = ((LinearLayout.LayoutParams) lp).topMargin;
        } else if (lp instanceof RelativeLayout.LayoutParams) {
            topMargin = ((RelativeLayout.LayoutParams) lp).topMargin;
        } else if (lp instanceof FrameLayout.LayoutParams) {
            topMargin = ((FrameLayout.LayoutParams) lp).topMargin;
        }
        posY = mTopView.getHeight() + topMargin;
        return posY;
    }

    public void setMaxIndex(int max) {
        mMaxIndex = max;
    }

    public void setTitleListener(RecordDataTitleListener listener) {
        mListener = listener;
    }

    public void setVisibility(int visibility) {
        mRootView.setVisibility(visibility);
    }

    public interface RecordDataTitleListener {
        void onRecordDataPageSelected(int index);
    }
}
