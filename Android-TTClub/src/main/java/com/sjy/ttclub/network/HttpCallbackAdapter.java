package com.sjy.ttclub.network;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.ContextManager;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by linhz on 2016/1/4.
 * Email: linhaizhong@ta2she.com
 */
public abstract class HttpCallbackAdapter implements IHttpCallBack {

    @Override
    public void onError(String errorStr, int code) {
        if (HttpCode.INVALID_TOKEN_CODE == code) {
            ToastHelper.showToast(ContextManager.getAppContext(), R.string.community_invalided_token);
            AccountManager.getInstance().logout();
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
    }
}
