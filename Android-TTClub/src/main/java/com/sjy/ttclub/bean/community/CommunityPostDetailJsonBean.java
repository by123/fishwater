package com.sjy.ttclub.bean.community;

/**
 * Created by zwl on 2015/11/17.
 * Email: 1501448275@qq.com
 */
public class CommunityPostDetailJsonBean {

    private int status;
    private String msg;
    private CommunityPostBean data;

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

    public CommunityPostBean getData() {
        return data;
    }

    public void setData(CommunityPostBean data) {
        this.data = data;
    }
}
