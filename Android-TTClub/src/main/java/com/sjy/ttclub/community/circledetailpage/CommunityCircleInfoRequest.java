package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.bean.community.CommunityCirlceInfoJsonBean;
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
public class CommunityCircleInfoRequest {
    private Context mContext;
    private int mCircleId;
    private boolean mIsRequesting = false;
    private IHttpManager mHttpManager;

    public CommunityCircleInfoRequest(Context context) {

        this.mContext = context;
    }

    public void startRequestById(int circleId, RequestCircleInfoCallback circleInfoCallback) {
        mCircleId = circleId;
        startRequest(circleInfoCallback);
    }

    private void startRequest(final RequestCircleInfoCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "circleInfo");
        mHttpManager.addParams("circleId", String.valueOf(mCircleId));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityCirlceInfoJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityCirlceInfoJsonBean cirlceInfoJsonBean = (CommunityCirlceInfoJsonBean) obj;
                handleGetCircleInfoSuccess(cirlceInfoJsonBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCircleInfoFailed(code, callback);
            }
        });

    }

    private void handleGetCircleInfoFailed(int errorCode, RequestCircleInfoCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorCode);
    }

    private void handleGetCircleInfoSuccess(CommunityCirlceInfoJsonBean result, RequestCircleInfoCallback callback) {
        mIsRequesting = false;
        if (result.getStatus() == HttpCode.SUCCESS_CODE) {
            callback.onResultSuccess(result.getData());
        } else {
            handleGetCircleInfoFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    public boolean ismIsRequesting() {
        return mIsRequesting;
    }

    public interface RequestCircleInfoCallback {
        void onResultFail(int errorCode);

        void onResultSuccess(CommunityCircleBean circle);
    }
}
