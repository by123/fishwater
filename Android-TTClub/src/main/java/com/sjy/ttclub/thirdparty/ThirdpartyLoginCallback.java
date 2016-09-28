package com.sjy.ttclub.thirdparty;

import com.sjy.ttclub.account.model.LoginMedia;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public interface ThirdpartyLoginCallback {
    void onLoginSuccess(LoginMedia media,ThirdpartyUserInfo userInfo);
    void onLoginFailed(LoginMedia media);
}
