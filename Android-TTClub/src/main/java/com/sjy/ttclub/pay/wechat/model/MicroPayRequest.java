package com.sjy.ttclub.pay.wechat.model;

import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhxu on 2016/1/5.
 * Email:357599859@qq.com
 */
public class MicroPayRequest {

    private static MicroPayRequest mMicroPayRequest;

    public MicroPayRequest() {
    }

    public static MicroPayRequest getInstance() {
        if (mMicroPayRequest == null) {
            mMicroPayRequest = new MicroPayRequest();
        }
        return mMicroPayRequest;
    }

    /**
     * 预支付交易会话标识
     */
    public void getPrepayId(String body, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.setBodyContent(body);
        httpManager.request(HttpUrls.WECHAT_PAY_URL, HttpMethod.POST, callBack);
    }
}
