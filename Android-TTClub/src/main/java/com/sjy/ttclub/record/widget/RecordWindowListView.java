package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sjy.ttclub.record.RecordConfig;
import com.sjy.ttclub.record.adapter.RecordWindowListViewAdapter;


/**
 * Created by zhxu on 2015/12/7.
 * Email:357599859@qq.com
 */
public class RecordWindowListView extends ListView {

    public final static String TAG = "RecordWindowListView";

    private Context mContext;

    private RecordWindowListViewAdapter mRecordWindowListViewAdapter;

    public RecordWindowListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public RecordWindowListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordWindowListView(Context context) {
        this(context, null, 0);
    }

    public void stepListView() {
        mRecordWindowListViewAdapter = new RecordWindowListViewAdapter(mContext, RecordConfig.RECORD_TYPE_PAPA);
        setAdapter(mRecordWindowListViewAdapter);
        setListViewHeightBasedOnChildren(this);
    }

    public void updateData(int cate) {
        RecordConfig.getInstance().updateCate(cate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
