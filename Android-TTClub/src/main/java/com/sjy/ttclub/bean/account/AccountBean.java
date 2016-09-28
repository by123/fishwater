package com.sjy.ttclub.bean.account;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by gangqing on 2015/11/27.
 * Email:denggangqing@ta2she.com
 */
public class AccountBean implements Serializable {
    private Data data;
    private String msg;
    private int status;

    public class Data {
        public String age;
        public String currentProgress;
        public String followersCount;
        public String followingCount;
        public String imageUrl;
        public String isMaxLevel;
        public String level;
        public String marriage;
        public String nickname;
        public String progressBar;
        public String sex;
        public String sexIfUpdate;
        public String sexyLife;
        public String token;
        public String userId;
        public String userRoleId;

        public String ifBindPhone;
        public String ranking;
        public String recordCount;
        public String sexCount;
        public String sexPoint;
        public String userExpers;
        public String ifHasUploadSex;
    }

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
}
