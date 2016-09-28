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
public abstract class RetrievePasswordRequest {
    private Context mContext;

    public RetrievePasswordRequest(Context context) {
        mContext = context;
    }

    /**
     * 获取个人资料
     *
     */
    public void getPersonalData(String phone, String code, String password) {
        if (phone.length() != 11) {
            ToastHelper.showToast(mContext, R.string.account_login_check_phone, Toast.LENGTH_SHORT);
            return;
        } else if (code.length() != 4) {
            ToastHelper.showToast(mContext, R.string.account_retrieve_password_code_hint, Toast.LENGTH_SHORT);
            return;
        } else if (password.length() < 6 || password.length() > 18) {
            ToastHelper.showToast(mContext, R.string.account_login_check_password, Toast.LENGTH_SHORT);
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "ufp");
        httpManager.addParams("phoneNumber", phone);
        httpManager.addParams("newPassword", Md5Utils.getMD5(password));
        httpManager.addParams("VICode", code);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new MyIHttpClassBack());
    }

    public abstract void success();

    private class MyIHttpClassBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            ToastHelper.showToast(mContext, R.string.account_retrieve_password_success, Toast.LENGTH_SHORT);
            success();
        }

        @Override
        public void onFail(String errorStr, int code) {
            switch (code) {
                case HttpCode.ERR_VICODE:
                    ToastHelper.showToast(mContext, R.string.account_request_error_401, Toast.LENGTH_SHORT);
                    break;
                case HttpCode.ERR_NOT_REGISTER:
                    ToastHelper.showToast(mContext, R.string.account_request_error_403, Toast.LENGTH_SHORT);
                    break;
                default:
                    ToastHelper.showToast(mContext, R.string.account_retrieve_password_error, Toast.LENGTH_SHORT);
                    break;
            }
        }
    }

}
