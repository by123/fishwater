package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataChartView extends LinearLayout {
    private ArrayList<RecordChartViewInfo> mChartInfoList = new ArrayList<RecordChartViewInfo>();

    public RecordDataChartView(Context context) {
        super(context);
    }

    public RecordDataChartView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public RecordDataChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setupChartView(ArrayList<RecordChartViewInfo> list) {
        mChartInfoList = list;
    }


}
