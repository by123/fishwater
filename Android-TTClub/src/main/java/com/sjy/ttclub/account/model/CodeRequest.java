/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.model;

import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by gangqing on 2015/11/30.
 * Email:denggangqing@ta2she.com
 */
public class CodeRequest {
    public CodeRequest() {
    }

    /**
     * 获取验证码
     */
    public void getCode(String phone) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "uvi");
        httpManager.addParams("phoneNumber", phone);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, null, null);
    }
}
