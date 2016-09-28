package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataCircleChartView extends RecordDataChartView {

    public RecordDataCircleChartView(Context context) {
        super(context, null);
    }

    public RecordDataCircleChartView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public RecordDataCircleChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setupChartView(ArrayList<RecordChartViewInfo> list) {
        super.setupChartView(list);
        setOrientation(LinearLayout.VERTICAL);
        removeAllViewsInLayout();
        for (RecordChartViewInfo viewInfo : list) {
            addCircleChartView(viewInfo);
        }
    }

    private void addCircleChartView(RecordChartViewInfo viewInfo) {
        int size = ResourceHelper.getDimen(R.dimen.space_182);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        CircleChartView chartView = new CircleChartView(getContext());
        chartView.setIcon(viewInfo.iconResId);
        for (BaseChartInfo info : viewInfo.chartInfoList) {
            chartView.addChartInfo(info);
        }
        this.addView(chartView,lp);
    }
}
