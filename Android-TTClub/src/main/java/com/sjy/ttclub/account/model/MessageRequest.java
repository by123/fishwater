package com.sjy.ttclub.account.model;

import android.content.Context;

import com.sjy.ttclub.bean.account.MessageDialog;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by gangqing on 2015/12/25.
 * Email:denggangqing@ta2she.com
 */
public class MessageRequest {

    /**
     * 获取私信列表
     *
     * @param endId
     * @param page
     * @param pageSize
     * @param callBack
     */
    public void personalLetterRequest(int endId, int page, int pageSize, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "letterDialogList");
        httpManager.addParams("endId", String.valueOf(endId));
        httpManager.addParams("page", String.valueOf(page));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, MessageDialog.class, callBack);
    }

    /**
     * 获取官方消息
     *
     * @param callBack
     */
    public void officialNewsRequest(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "getOfficialMsg");
        httpManager.addParams("endId", "0");
        httpManager.addParams("page", "1");
        httpManager.addParams("pageSize", "1");
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, MessageDialog.class, callBack);
    }

    /**
     * 删除会话
     *
     * @param letter
     * @param callBack
     */
    public void deletePersonalLetterRequest(List<MessageDialogs> letter, IHttpCallBack callBack) {
        String dialogIds = "";
        for (MessageDialogs d : letter) {
            dialogIds += "," + d.getDialogId();
        }
        if (StringUtils.isEmpty(dialogIds)) {
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "removeLetterDialog");
        httpManager.addParams("dialogIds", dialogIds.substring(1));
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, null, callBack);
    }
}
