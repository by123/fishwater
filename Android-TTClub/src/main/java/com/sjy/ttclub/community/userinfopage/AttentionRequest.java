package com.sjy.ttclub.community.userinfopage;

import android.content.Context;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
/**
 * Created by Administrator on 2015/12/3.
 */
public class AttentionRequest {
    private Context mContext;
    private int mFollowId;
    private boolean mIsRequesting;

    public AttentionRequest(Context context, int followId) {
        this.mContext = context;
        this.mFollowId = followId;
    }

    public void startAddAttentionRequest(AttentionRequestResultCallback callback) {
        startRequestByType(CommunityConstant.ADD_ATTETION, callback);
    }

    public void startCancelAttentionRequest(AttentionRequestResultCallback callback) {
        startRequestByType(CommunityConstant.CANCEL_ATTETION, callback);
    }

    private void startRequestByType(int isAttention, final AttentionRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "urf");
        mHttpManager.addParams("followId", String.valueOf(mFollowId));
        mHttpManager.addParams("ornot", String.valueOf(isAttention));
        mHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, BaseBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                BaseBean resultBean = (BaseBean) obj;
                handleRequestResultSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleRequestResultFail(code, callback);
            }
        });
    }

    private void handleRequestResultFail(int errorCode, AttentionRequestResultCallback callback) {
        callback.onResultFail(errorCode);
        mIsRequesting = false;
    }

    public void handleRequestResultSuccess(BaseBean result, AttentionRequestResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            callback.onResultSuccess();
        } else {
            handleRequestResultFail(result.getStatus(), callback);
        }


    }

    public interface AttentionRequestResultCallback {
        void onResultFail(int errorCode);

        void onResultSuccess();
    }
}
