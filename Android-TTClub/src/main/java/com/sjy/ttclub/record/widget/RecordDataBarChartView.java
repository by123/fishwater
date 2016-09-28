package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataBarChartView extends RecordDataChartView {

    public RecordDataBarChartView(Context context) {
        super(context);
    }

    public RecordDataBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public RecordDataBarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setupChartView(ArrayList<RecordChartViewInfo> list) {
        super.setupChartView(list);
        removeAllViewsInLayout();
        setOrientation(LinearLayout.HORIZONTAL);
        for (RecordChartViewInfo itemInfo : list) {
            addBarChartView(itemInfo);
        }
    }

    private void addBarChartView(RecordChartViewInfo info) {
        if (info.chartInfoList.isEmpty()) {
            return;
        }
        DataBarChartView chartView = new DataBarChartView(getContext());
        chartView.setupView(info);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        this.addView(chartView, lp);
    }

    private class DataBarChartView extends LinearLayout {

        public DataBarChartView(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
        }

        public void setupView(RecordChartViewInfo info) {
            this.removeAllViewsInLayout();
            boolean isUp = info.chartInfoList.get(0).chartType == BaseChartInfo.ChartType.BAR_UP;
            BarChartView chartView = new BarChartView(getContext());
            chartView.addChartInfo(info.chartInfoList);

            TextView iconView = new TextView(getContext());
            iconView.setTextColor(ResourceHelper.getColor(R.color.black));
            iconView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_12));
            if (info.textResId != 0) {
                iconView.setText(info.textResId);
            }
            if (info.iconResId != 0) {
                Drawable iconDrawable = ResourceHelper.getDrawable(info.iconResId);
                int iconWidth = iconDrawable.getIntrinsicWidth();
                int iconHeight = iconDrawable.getIntrinsicHeight();
                iconWidth = Math.min(iconWidth, ResourceHelper.getDimen(R.dimen.space_30));
                iconHeight = Math.min(iconHeight, ResourceHelper.getDimen(R.dimen.space_30));
                iconDrawable.setBounds(0, 0, iconWidth, iconHeight);
                if (isUp) {
                    iconView.setCompoundDrawables(null, iconDrawable, null, null);
                    iconView.setCompoundDrawablePadding(ResourceHelper.getDimen(R.dimen.space_8));
                } else {
                    iconView.setCompoundDrawables(null, null, null, iconDrawable);
                    iconView.setCompoundDrawablePadding(ResourceHelper.getDimen(R.dimen.space_8));
                }
            }
            if (isUp) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        ResourceHelper.getDimen(R.dimen.space_150));
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                this.addView(chartView, lp);

                lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.topMargin = ResourceHelper.getDimen(R.dimen.space_8);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                this.addView(iconView, lp);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                lp.bottomMargin = ResourceHelper.getDimen(R.dimen.space_8);
                this.addView(iconView, lp);

                lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ResourceHelper.getDimen(R.dimen
                        .space_150));
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                this.addView(chartView, lp);
            }
        }
    }
}
