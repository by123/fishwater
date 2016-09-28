package com.sjy.ttclub.account.model;

/**
 * Created by gangqing on 2015/12/3.
 * Email:denggangqing@ta2she.com
 */
public enum LoginMedia {
    QQ("1"), WECHAT("2"), SINA("3");

    private String mType;

    LoginMedia(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
