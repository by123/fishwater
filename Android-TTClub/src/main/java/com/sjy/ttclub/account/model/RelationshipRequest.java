package com.sjy.ttclub.account.model;

import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.RelationshipBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2015/12/23.
 * Email:denggangqing@ta2she.com
 */
public class RelationshipRequest {
    private boolean mAccountDataChange = false;

    /**
     * 获取关注或粉丝列表
     *
     * @param type
     * @param endTimestamp
     * @param callBack
     */
    public void startRelationshipRequest(String type, String endTimestamp, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "userRelationList");
        httpManager.addParams("relation", type);
        if (StringUtils.isNotEmpty(endTimestamp)) {
            httpManager.addParams("endTimestamp", endTimestamp);
        }
        httpManager.addParams("pageSize", String.valueOf(Constant.RELATIONSHIP_PAGER_SIZE));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, RelationshipBean.class, callBack);
    }

    /**
     * @param followId 用户id
     * @param ornot    关注与否 (关注:1; 取消关注:0;)
     */
    public void startCancelOrFollow(String followId, String ornot) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "urf");
        httpManager.addParams("followId", followId);
        httpManager.addParams("ornot", ornot);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, null, new AccountDataChangeCallBack());
    }

    private class AccountDataChangeCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            if (!mAccountDataChange) {
                mAccountDataChange = true;
            }
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_RELATIONSHIP_ACCOUNT_DATA_CHANGE);
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }
}
