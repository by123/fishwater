package com.sjy.ttclub.bean.account;

import com.sjy.ttclub.bean.BaseBean;

/**
 * Created by gangqing on 2016/1/13.
 * Email:denggangqing@ta2she.com
 */
public class RemindSetting extends BaseBean {
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private String privateMsgReply;
        private String record;

        public String getPrivateMsgReply() {
            return privateMsgReply;
        }

        public String getRecord() {
            return record;
        }
    }
}
