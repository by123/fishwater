package com.sjy.ttclub.account.model;

import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;

/**
 * Created by gangqing on 2015/12/23.
 * Email:denggangqing@ta2she.com
 */
public abstract class AccountBaseIHttpCallBack implements IHttpCallBack {

    @Override
    public void onError(String errorStr, int code) {
        if (HttpCode.INVALID_TOKEN_CODE == code) {
            AccountManager.getInstance().logout();
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
        onFail(errorStr, code);
    }

    public abstract void onFail(String errorStr, int code);
}
