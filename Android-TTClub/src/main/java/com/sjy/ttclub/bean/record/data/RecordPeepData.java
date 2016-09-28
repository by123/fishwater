package com.sjy.ttclub.bean.record.data;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepData {
    private RecordPeepAverageData peepData = new RecordPeepAverageData();
    private RecordPeepSelfData myData = new RecordPeepSelfData();

    public RecordPeepAverageData getPeepData() {
        return peepData;
    }

    public RecordPeepSelfData getMyData() {
        return myData;
    }
}
