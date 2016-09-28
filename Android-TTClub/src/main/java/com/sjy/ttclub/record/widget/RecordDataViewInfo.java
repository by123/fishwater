package com.sjy.ttclub.record.widget;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/9.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataViewInfo {
    public static final int TYPE_BAR = 0;
    public static final int TYPE_CIRCLE = 1;
    public static final int TYPE_POSITION = 2;
    public int type = TYPE_BAR;
    public String title;
    public ArrayList<RecordChartViewInfo> chartViewInfoList = new ArrayList<RecordChartViewInfo>();
}
