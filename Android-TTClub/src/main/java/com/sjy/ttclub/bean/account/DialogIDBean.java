package com.sjy.ttclub.bean.account;

import java.io.Serializable;

/**
 * Created by gangqing on 2015/12/7.
 * Email:denggangqing@ta2she.com
 */
public class DialogIDBean {
    public Data data;
    public String msg;
    public int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data implements Serializable {
        public String dialogId;
        public String pullBlackFlag;
        public String toUserId;
    }
}
