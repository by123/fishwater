package com.sjy.ttclub.bean.community;

/**
 * Created by zwl on 2015/11/17.
 * Email: 1501448275@qq.com
 */
public class CommentJsonBean {
    private int status;
    private String msg;
    private CommentBean data;

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

    public CommentBean getData() {
        return data;
    }

    public void setData(CommentBean data) {
        this.data = data;
    }
}
