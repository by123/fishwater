package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.RemindSetting;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by gangqing on 2016/1/12.
 * Email:denggangqing@ta2she.com
 */
public class SettingRequest {

    public void updateRemindSettingRequest(String message, String card, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "updateRemindSetting");
        httpManager.addParams("privateMsgReply", message);
        httpManager.addParams("record", card);
        httpManager.request(HttpUrls.USERCONF_URL, HttpMethod.POST, callBack);
    }

    public void getRemindSettingRequest(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "getRemindSetting");
        httpManager.request(HttpUrls.USERCONF_URL, HttpMethod.POST, RemindSetting.class, callBack);
    }
}
