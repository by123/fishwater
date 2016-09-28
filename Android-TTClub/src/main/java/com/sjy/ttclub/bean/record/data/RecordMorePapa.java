package com.sjy.ttclub.bean.record.data;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordMorePapa {
    private int userSampleCount;
    private RecordTime time = new RecordTime();
    private RecordFeel feel = new RecordFeel();
    private RecordPosition position = new RecordPosition();
    private RecordPlace place = new RecordPlace();
    private RecordToy toy = new RecordToy();
    private RecordOrgasm orgasm = new RecordOrgasm();

    public RecordTime getTime() {
        return time;
    }

    public RecordFeel getFeel() {
        return feel;
    }

    public RecordPlace getPlace() {
        return place;
    }

    public RecordPosition getPosition() {
        return position;
    }

    public RecordToy getToy() {
        return toy;
    }

    public RecordOrgasm getOrgasm() {
        return orgasm;
    }

    public int getUserSampleCount() {
        return userSampleCount;
    }
}
