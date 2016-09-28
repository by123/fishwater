package com.sjy.ttclub.bean.record;

import com.sjy.ttclub.bean.BaseBean;

/**
 * Created by gangqing on 2016/1/7.
 * Email:denggangqing@ta2she.com
 */
public class RecordPeepRuleBean extends BaseBean {
    public Data data;

    public class Data {
        public String holidayName;
        public String userPeepLevel;
        public String userPeepMarriage;
        public String userPeepSex;
        public String userPeepShowMe;
        public String userPeepTime;
    }
}
