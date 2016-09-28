package com.sjy.ttclub.bean.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/8.
 * Email:357599859@qq.com
 */
public class RecordData implements Serializable {
    private List<RecordDataMonth> dataMonth;
    private List<RecordDataDay> dataDay;

    public List<RecordDataMonth> getDataMonth() {
        return dataMonth;
    }

    public void setDataMonth(List<RecordDataMonth> dataMonth) {
        this.dataMonth = dataMonth;
    }

    public List<RecordDataDay> getDataDay() {
        return dataDay;
    }

    public void setDataDay(List<RecordDataDay> dataDay) {
        this.dataDay = dataDay;
    }
}
