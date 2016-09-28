package com.sjy.ttclub.bean.community;

/**
 * Created by zhangwulin on 2015/12/29.
 * email 1501448275@qq.com
 */
public class CommunityCirlceInfoJsonBean {
    private int status;
    private String msg;
    private CommunityCircleBean data;

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

    public CommunityCircleBean getData() {
        return data;
    }

    public void setData(CommunityCircleBean data) {
        this.data = data;
    }
}
