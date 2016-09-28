package com.sjy.ttclub.community.allcirclespage;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityAllCircleBean;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwl on 2015/11/23.
 * Email: 1501448275@qq.com
 */
public class CommunityAllCircleRequest {
    private Context mContext;
    private boolean mIsRequesting = false;
    private IHttpManager mIHttpManager;
    private List<CommunityCircleBean> mDataList = new ArrayList<>();

    public CommunityAllCircleRequest(Context mContext) {
        this.mContext = mContext;

    }

    public void startRequest(RequestResultCallback callback) {
        startRequestByType(callback);
    }

    private void startRequestByType(final RequestResultCallback callback) {

        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFailed(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mIHttpManager = HttpManager.getBusinessHttpManger();
        mIHttpManager.addParams("a", "allCircleList");
        mIHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityAllCircleBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityAllCircleBean allCircleBean = (CommunityAllCircleBean) obj;
                handleGetCirclesSuccess(allCircleBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCirclesFailed(code, callback);
            }
        });
    }

    private void handleGetCirclesFailed(int errorCode, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFailed(errorCode);
    }

    private void handleGetCirclesSuccess(CommunityAllCircleBean allCircleBean, RequestResultCallback callback) {
        mIsRequesting = false;
        mDataList.addAll(allCircleBean.getData().getCircles());
        callback.onResultSuccess(mDataList);


    }

    public interface RequestResultCallback {
        void onResultFailed(int errorType);

        void onResultSuccess(List<CommunityCircleBean> datas);
    }
}
