package com.sjy.ttclub.bean.record;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/8.
 * Email:357599859@qq.com
 */
public class RecordDay implements Serializable {
    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RecordData getData() {
        return data;
    }

    public void setData(RecordData data) {
        this.data = data;
    }

    private RecordData data;
}
