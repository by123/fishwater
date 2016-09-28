/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.model;

import android.content.Context;
import android.widget.Toast;

import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2015/11/30.
 * Email:denggangqing@ta2she.com
 */
public class BindPhoneRequest {
    private Context mContext;

    public BindPhoneRequest(Context context) {
        mContext = context;
    }

    /**
     * 获取验证码
     */
    public void bindPhone(String phone, String code) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "ubp");
        httpManager.addParams("phoneNumber", phone);
        httpManager.addParams("VICode", code);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, null, new MyIHttpCallBack());
    }

    private class MyIHttpCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            ToastHelper.showToast(mContext, "绑定成功", Toast.LENGTH_SHORT);
        }

        @Override
        public void onFail(String errorStr, int code) {
            ToastHelper.showToast(mContext, "绑定失败", Toast.LENGTH_SHORT);
        }
    }
}
