/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.model;

import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.account.AccountBean;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.Md5Utils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2015/11/30.
 * Email:denggangqing@ta2she.com
 */
public abstract class SignupRequest {
    private Context mContext;

    public SignupRequest(Context context) {
        mContext = context;
    }

    /**
     * 注册
     */
    public void getPersonalData(String phone, String password, String nickname, String code) {
        if (code.length() != 4) {
            ToastHelper.showToast(mContext, R.string.account_retrieve_password_code_hint, Toast.LENGTH_SHORT);
            return;
        }
        AccountManager accountManager = AccountManager.getInstance();
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "userRegisterQuick");
        httpManager.addParams("tel", phone);
        httpManager.addParams("pass", Md5Utils.getMD5(password));
        httpManager.addParams("name", nickname);
        httpManager.addParams("VICode", code);
        httpManager.addParams("sex", String.valueOf(accountManager.getSex()));
        httpManager.addParams("marriage", String.valueOf(accountManager.getMarriage()));
        httpManager.addParams("sexyLife", String.valueOf(accountManager.getSexyLife()));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new MyIHttpCallBack());
    }

    public abstract void success();

    private class MyIHttpCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            ToastHelper.showToast(mContext, R.string.account_signup_success, Toast.LENGTH_SHORT);
            success();
        }

        @Override
        public void onFail(String errorStr, int code) {
            switch (code) {
                case HttpCode.ERR_VICODE:
                    ToastHelper.showToast(mContext, R.string.account_request_error_401, Toast.LENGTH_SHORT);
                    break;
                case HttpCode.ERR_HAS_REGISTER:
                    ToastHelper.showToast(mContext, R.string.account_request_error_402, Toast.LENGTH_SHORT);
                    break;
                default:
                    ToastHelper.showToast(mContext, R.string.account_signup_error, Toast.LENGTH_SHORT);
                    break;
            }
        }
    }
}
