package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.BlacklistBean;
import com.sjy.ttclub.bean.account.DialogIDBean;
import com.sjy.ttclub.bean.account.MessageDialogDetails;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class MessageChatDetailsRequest {
    private static final int pageSize = 20;

    /**
     * 获取会话详情
     *
     * @param dialogId 会话id
     * @param startId  开始id
     * @param endId    结束id
     * @param type     上拉获取新数据：1；下拉获取旧数据：2；（首次进入会话详情时传1）
     */
    public void personalChatRequest(String dialogId, int startId, int endId, int type, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "letterList");
        httpManager.addParams("dialogId", dialogId);
        httpManager.addParams("startId", String.valueOf(startId));
        httpManager.addParams("endId", String.valueOf(endId));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.addParams("pageDirection", String.valueOf(type));
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, MessageDialogDetails.class, callBack);
    }

    /**
     * 获取官方消息详情
     *
     * @param endId    获取分页ID （首次传0）
     * @param page     页数
     * @param callBack
     */
    public void officialNewsChatRequest(int endId, int page, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "getOfficialMsg");
        httpManager.addParams("endId", String.valueOf(endId));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.addParams("page", String.valueOf(page));
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, MessageDialogDetails.class, callBack);
    }

    /**
     * 获取会话ID
     *
     * @param toUserId 私信对象ID（与她他小秘私信时传0，由后端随机分配返回）
     * @param type     私信对象类型（1:普通会员 2:她他小秘）
     * @param callBack
     */
    public void dialogIdRequest(String toUserId, String type, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "getDialog");
        httpManager.addParams("toUserId", toUserId);
        httpManager.addParams("toUserType", type);
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, DialogIDBean.class, callBack);
    }

    /**
     * 发送
     *
     * @param dialogId
     * @param toUserId
     * @param content
     * @param linkType
     * @param linkContent
     * @param linkUrl
     * @param callBack
     */
    public void sendChatRequest(String dialogId, String toUserId, String content, String linkType, String linkContent, String linkUrl, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "sendLetter");
        httpManager.addParams("dialogId", dialogId);
        httpManager.addParams("toUserId", toUserId);
        httpManager.addParams("content", content);
        httpManager.addParams("linkType", linkType);
        httpManager.addParams("linkContent", linkContent);
        httpManager.addParams("linkUrl", linkUrl);
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, null, callBack);
    }

    /**
     * 黑名单列表
     *
     * @param endId
     * @param page
     * @param pageSize
     * @param callBack
     */
    public void blacklistRequest(int endId, int page, int pageSize, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "blacklist");
        httpManager.addParams("endId", String.valueOf(endId));
        httpManager.addParams("page", String.valueOf(page));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, BlacklistBean.class, callBack);
    }

    /**
     * 拉黑
     *
     * @param toUserId
     * @param callBack
     * @param flag     1：拉黑，0：取消拉黑
     */
    public void shieldingRequest(String toUserId, String flag, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "editBlacklist");
        httpManager.addParams("blUserId", toUserId);
        httpManager.addParams("flag", flag);
        httpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, null, callBack);
    }
}
