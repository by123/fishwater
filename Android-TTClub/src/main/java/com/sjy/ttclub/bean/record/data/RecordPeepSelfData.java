package com.sjy.ttclub.bean.record.data;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepSelfData {

    private RecordMorePapa morePapa = new RecordMorePapa();
    private RecordOnePapa onePapa = new RecordOnePapa();
    private RecordNoPapa noPapa = new RecordNoPapa();

    public RecordNoPapa getNoPapa() {
        return noPapa;
    }

    public RecordMorePapa getMorePapa() {
        return morePapa;
    }

    public RecordOnePapa getOnePapa() {
        return onePapa;
    }
}
