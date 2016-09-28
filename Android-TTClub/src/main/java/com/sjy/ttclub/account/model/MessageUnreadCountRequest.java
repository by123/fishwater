package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.MessageUnreadCountBean;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by gangqing on 2015/12/30.
 * Email:denggangqing@ta2she.com
 */
public class MessageUnreadCountRequest {
    public void messageUnreadCountRequest(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "ubu");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, MessageUnreadCountBean.class, callBack);
    }
}
