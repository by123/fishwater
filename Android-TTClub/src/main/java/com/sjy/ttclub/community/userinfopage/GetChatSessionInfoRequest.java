package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.DialogIDBean;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhangwulin on 2015/12/8.
 * email 1501448275@qq.com
 */
public class GetChatSessionInfoRequest {
    private Context mContext;
    private int mToUserId;
    private boolean mIsRequesting = false;

    public GetChatSessionInfoRequest(Context context) {
        this.mContext = context;
    }

    public void startChatWithConsumer(int toUserId, RequestChatSessionInfoCallback circleInfoCallback) {
        mToUserId = toUserId;
        startRequest(CommunityConstant.TYPE_CHAT_OBJECT_GENERAL, circleInfoCallback);
    }

    public void startChatWithOfficialSecretary(RequestChatSessionInfoCallback circleInfoCallback) {
        mToUserId = CommunityConstant.CHAT_DEFAULT_ID;
        startRequest(CommunityConstant.TYPE_CHAT_OBJECT_OFFICIAL, circleInfoCallback);
    }

    private void startRequest(final int type, final RequestChatSessionInfoCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "getDialog");
        mHttpManager.addParams("toUserId", String.valueOf(mToUserId));   //私信对象ID（与她他小秘私信时传0，由后端随机分配返回）
        mHttpManager.addParams("toUserType", String.valueOf(type)); //私信对象类型（1:普通会员 2:她他小秘）
        mHttpManager.request(HttpUrls.LETTER_URL, HttpMethod.POST, DialogIDBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                DialogIDBean resultBean = (DialogIDBean) obj;
                handleGetChatSessionInfoSuccess(type, resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetChatSessionInfoFailed(code, callback);
            }
        });
    }

    private void handleGetChatSessionInfoFailed(int errorCode, RequestChatSessionInfoCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorCode);
    }

    private void handleGetChatSessionInfoSuccess(int type, DialogIDBean result, RequestChatSessionInfoCallback callback) {
        mIsRequesting = false;
        if (result.status == HttpCode.SUCCESS_CODE) {
            DialogIDBean.Data data = result.data;
            MessageDialogs letter = new MessageDialogs();
            letter.setUserId(data.toUserId);
            letter.setDialogId(data.dialogId);
            letter.setPullBlackFlag(data.pullBlackFlag);
            if (type == CommunityConstant.TYPE_CHAT_OBJECT_GENERAL) {
                letter.setUserRoleId(String.valueOf(CommunityConstant.TYPE_CHAT_OBJECT_GENERAL));
            } else if (type == CommunityConstant.TYPE_CHAT_OBJECT_OFFICIAL) {
                letter.setUserRoleId(Constant.TA_SHE_SECRETARY);
            }
            callback.onResultSuccess(letter);
        } else {
            handleGetChatSessionInfoFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    public boolean ismIsRequesting() {
        return mIsRequesting;
    }

    public interface RequestChatSessionInfoCallback {
        void onResultFail(int errorCode);

        void onResultSuccess(MessageDialogs letter);
    }
}
