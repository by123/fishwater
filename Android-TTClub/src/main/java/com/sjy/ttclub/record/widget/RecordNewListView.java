package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.RecordDataDay;
import com.sjy.ttclub.record.adapter.RecordListViewAdapter;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2015/12/17.
 * Email:357599859@qq.com
 */
public class RecordNewListView extends ListView {

    public final static String TAG = "RecordNewListView";

    private Context mContext;

    private RecordCollapseFrameLayout mCollapseFrameLayout;

    private RecordListViewAdapter mAdapter;
    private List<RecordDataDay> mListData = new ArrayList<>();

    private float mStartY = 0;

    public RecordNewListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        AbsListView.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper.getDimen(R.dimen.space_100));
        View mFootView = new View(mContext);
        mFootView.setLayoutParams(layoutParams);
        addFooterView(mFootView);
    }

    public RecordNewListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordNewListView(Context context) {
        this(context, null, 0);
    }

    public void stepListView(List<RecordDataDay> recordDataDayList) {
        mAdapter = new RecordListViewAdapter(mContext, recordDataDayList);
        setAdapter(mAdapter);
    }

    public void updateData(List<RecordDataDay> list) {
        mListData = list;
        if (mAdapter == null) {
            mAdapter = new RecordListViewAdapter(mContext, mListData);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setCollapseFrameLayout(RecordCollapseFrameLayout collapseFrameLayout) {
        mCollapseFrameLayout = collapseFrameLayout;
    }

    public int getChildScrollTop() {
        View child = getChildAt(0);
        if (child != null) {
            return child.getTop();
        }
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            mStartY = ev.getY();
        }
        if (action == MotionEvent.ACTION_MOVE) {
            int scrollY = (int) (ev.getY() - mStartY);
            int changeHeight = mCollapseFrameLayout.getHeight() + scrollY;
            boolean isLesserMax = changeHeight <= mCollapseFrameLayout.getRecordTopViewMaxHeight();
            boolean isLesserMin = changeHeight >= mCollapseFrameLayout.getRecordTopViewMinHeight();
            boolean isMin = mCollapseFrameLayout.getHeight() == mCollapseFrameLayout.getRecordTopViewMinHeight();
            if (getChildScrollTop() == 0) {
                if (isLesserMax && isLesserMin) {
                    return mCollapseFrameLayout.onAnimate(changeHeight);
                } else if (scrollY < 0 && isMin) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return false;
                }
            }
        }
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mCollapseFrameLayout.onAnimate(!mCollapseFrameLayout.isScrollTop());
        }
        return super.dispatchTouchEvent(ev);
    }
}
