package com.sjy.ttclub.record.widget;

/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class BaseChartInfo {
    public enum ChartType {CIRCLE_AVERAGE, CIRCLE_SELF, BAR_UP, BAR_DOWN, PLACE_AVERAGE, PLACE_SELF}

    public int color;
    public int width;
    public int percent = 50;
    public int textSize = 0;
    public ChartType chartType = ChartType.BAR_UP;

    public BaseChartInfo() {

    }

    public BaseChartInfo(BaseChartInfo info) {
        this.color = info.color;
        this.width = info.width;
        this.percent = info.percent;
        this.textSize = info.textSize;
        this.chartType = info.chartType;
    }
}
