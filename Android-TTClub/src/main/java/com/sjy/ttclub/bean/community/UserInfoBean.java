package com.sjy.ttclub.bean.community;

import java.io.Serializable;

/**
 * Created by zhangwulin on 2015/12/8.
 * email 1501448275@qq.com
 */
public class UserInfoBean implements Serializable {
    private int status;
    private String msg;
    private CommunityUserInfo data;

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

    public CommunityUserInfo getData() {
        return data;
    }

    public void setData(CommunityUserInfo data) {
        this.data = data;
    }


}
