package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;

import java.util.ArrayList;

/**
 * Created by gangqing on 2016/1/9.
 * Email:denggangqing@ta2she.com
 */
public class RecordDataWherePapaView extends RecordDataChartView {
    private TextView mHomeOther, mHomeSelf;
    private TextView mHotelOther, mHotelSelf;
    private TextView mCarOther, mCarSelf;
    private TextView mOutdoorsOther, mOutdoorsSelf;
    private View mHomeLine, mHotelLine, mCarLine, mOutdoorsLine;

    public RecordDataWherePapaView(Context context) {
        super(context);
        addBarChartView();
    }

    public RecordDataWherePapaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addBarChartView();
    }

    public RecordDataWherePapaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addBarChartView();
    }

    private void addBarChartView() {
        View view = View.inflate(getContext(), R.layout.record_where_papa_layout, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);

        mHomeOther = (TextView) view.findViewById(R.id.record_where_papa_home_other);
        mHomeSelf = (TextView) view.findViewById(R.id.record_where_papa_home_self);
        mHotelOther = (TextView) view.findViewById(R.id.record_where_papa_hotel_other);
        mHotelSelf = (TextView) view.findViewById(R.id.record_where_papa_hotel_self);
        mCarOther = (TextView) view.findViewById(R.id.record_where_papa_car_other);
        mCarSelf = (TextView) view.findViewById(R.id.record_where_papa_car_self);
        mOutdoorsOther = (TextView) view.findViewById(R.id.record_where_papa_outdoors_other);
        mOutdoorsSelf = (TextView) view.findViewById(R.id.record_where_papa_outdoors_self);
        mHomeLine = view.findViewById(R.id.record_where_papa_home_line);
        mHotelLine = view.findViewById(R.id.record_where_papa_hotel_line);
        mCarLine = view.findViewById(R.id.record_where_papa_car_line);
        mOutdoorsLine = view.findViewById(R.id.record_where_papa_outdoors_line);

        this.addView(view, lp);
    }

    @Override
    public void setupChartView(ArrayList<RecordChartViewInfo> list) {
        super.setupChartView(list);
        int size = list.size();
        if (size >= 4) {
            View lineView;
            TextView selfText;
            TextView othersText;
            RecordChartViewInfo info;
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    lineView = mHomeLine;
                    selfText = mHomeSelf;
                    othersText = mHomeOther;
                } else if (i == 1) {
                    lineView = mHotelLine;
                    selfText = mHotelSelf;
                    othersText = mHotelOther;
                } else if (i == 2) {
                    lineView = mCarLine;
                    selfText = mCarSelf;
                    othersText = mCarOther;
                } else {
                    lineView = mOutdoorsLine;
                    selfText = mOutdoorsSelf;
                    othersText = mOutdoorsOther;
                }
                info = list.get(i);
                if (info.chartInfoList.size() > 1) {
                    lineView.setVisibility(View.VISIBLE);
                    othersText.setVisibility(View.VISIBLE);
                    selfText.setVisibility(View.VISIBLE);
                    othersText.setText(info.chartInfoList.get(0).percent + "%");
                    selfText.setText(info.chartInfoList.get(1).percent + "%");
                } else {
                    lineView.setVisibility(View.GONE);
                    BaseChartInfo chartInfo = info.chartInfoList.get(0);
                    if (chartInfo.chartType == BaseChartInfo.ChartType.PLACE_AVERAGE) {
                        othersText.setVisibility(View.VISIBLE);
                        othersText.setText(chartInfo.percent + "%");
                        selfText.setVisibility(View.GONE);
                    } else {
                        othersText.setVisibility(View.GONE);
                        selfText.setVisibility(View.VISIBLE);
                        selfText.setText(chartInfo.percent + "%");
                    }
                }
            }
        }
    }
}
