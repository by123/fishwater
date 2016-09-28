package com.sjy.ttclub.bean.community;

import java.io.Serializable;

/**
 * Created by zwl on 2015/11/12.
 * Email: 1501448275@qq.com
 */
public class CommunityPostJsonBean implements Serializable {

    private int status;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CommunityPostJsonDataBean getData() {
        return data;
    }

    public void setData(CommunityPostJsonDataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private CommunityPostJsonDataBean data;
}
