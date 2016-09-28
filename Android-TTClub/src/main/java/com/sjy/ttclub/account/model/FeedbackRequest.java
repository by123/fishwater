package com.sjy.ttclub.account.model;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.account.FeedbackBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/25.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackRequest {
    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
    private int mEndId = CommonConst.START_END_ID;
    private static final int PAGE_SIZE = 20;

    private boolean mHasMore = false;

    private boolean mIsRequesting = false;
    private FeedbackContentCallBackOnSuccess mFeedbackContentCallBackOnSuccess;


    private Context mContext;

    private ArrayList<FeedbackBean.FeedbackInfo> mFeedbackList = new ArrayList<>();

    public FeedbackRequest(Context context) {
        mContext = context;
    }

    public void commitWantFeedback(String content, ArrayList<String> photoList, final IHttpCallBack
            callBack) {
        final IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "feedbackPublish");
        httpManager.addParams("content", content);
        if (photoList != null && !photoList.isEmpty()) {
            BitmapUtil.compressBitmaps(photoList, new BitmapUtil.CompressBitmapCallback() {
                @Override
                public void onCompressFinished(ArrayList<String> desList) {
                    if (desList != null && !desList.isEmpty()) {
                        for (int i = 0; i < desList.size(); i++) {
                            String fileName = "feedback" + i;
                            httpManager.addParams(fileName, new File(desList.get(i)), null);
                        }
                    }
                    httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
                }
            });
        } else {
            httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
        }
    }

    public void startRecordRequest(boolean isLoadMore, final FeedbackRecordCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onRequestFailed(CommonConst.ERROR_TYPE_NETWORK);
            return;
        }
        if (mIsRequesting) {
            callback.onRequestFailed(CommonConst.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        if (mFeedbackList.isEmpty() || !isLoadMore) {
            mEndId = CommonConst.START_END_ID;
            mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "feedbackList");
        httpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        httpManager.addParams("pageSize", String.valueOf(PAGE_SIZE));

        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleGetRecordFailed(CommonConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                handleGetRecordSuccess(result, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetRecordFailed(CommonConst.ERROR_TYPE_NETWORK, callback);
            }
        });
    }

    private void handleGetRecordSuccess(String result, FeedbackRecordCallback callback) {
        mIsRequesting = false;
        try {
            Gson gson = new Gson();
            FeedbackBean feedbackBean = gson.fromJson(result, FeedbackBean.class);
            List<FeedbackBean.FeedbackInfo> feedbackList = null;
            if (feedbackBean.data != null && feedbackBean.data.messageArray != null) {
                feedbackList = feedbackBean.data.messageArray;
            }
            if (feedbackList == null || feedbackList.isEmpty()) {
                mHasMore = false;
            } else {
                boolean firstPage = mPageLoadingIndex == CommonConst.START_PAGE_INDEX;
                if (firstPage) {
                    mFeedbackList.clear();
                }
                mPageIndex = mPageLoadingIndex;
                int size = feedbackList.size();
                if (size < PAGE_SIZE) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                mFeedbackList.addAll(feedbackList);
                mPageIndex = mPageLoadingIndex;
            }
            callback.onRequestSuccess(mFeedbackList);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onRequestFailed(CommonConst.ERROR_TYPE_DATA);
        }
    }

    public void feedbackContentRequest(String messageId) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "feedbackDetail");
        httpManager.addParams("messageId", messageId);
        httpManager.addParams("page", String.valueOf(mPageIndex));
        httpManager.addParams("pageSize", String.valueOf(PAGE_SIZE));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, FeedbackBean.class, new FeedbackContentCallBack());
    }

    private class FeedbackContentCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof FeedbackBean) {
                FeedbackBean bean = (FeedbackBean) obj;
                if (bean.data.messageArray.size() < PAGE_SIZE) {
                    mIsRequesting = false;
                } else {
                    mIsRequesting = true;
                }
                mPageIndex++;
                mFeedbackContentCallBackOnSuccess.onRequestSuccess(bean.data.messageArray, mIsRequesting);
            }
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    public void sendFeedbackRequest(String messageId, String content) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "feedbackReply");
        httpManager.addParams("messageId", messageId);
        httpManager.addParams("content", content);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, null, new SendFeedbackCallBack());
    }

    private class SendFeedbackCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            mFeedbackContentCallBackOnSuccess.onSendSuccess();
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    private void handleGetRecordFailed(int errorType, FeedbackRecordCallback callback) {
        mIsRequesting = false;
        callback.onRequestFailed(errorType);
    }

    public interface FeedbackRecordCallback {
        void onRequestSuccess(ArrayList<FeedbackBean.FeedbackInfo> list);

        void onRequestFailed(int errorType);
    }

    public interface FeedbackContentCallBackOnSuccess {
        void onRequestSuccess(List<FeedbackBean.FeedbackInfo> feedbackInfoList, boolean isRequesting);

        void onSendSuccess();
    }

    public void setFeedbackContentCallBackOnSuccess(FeedbackContentCallBackOnSuccess callback) {
        this.mFeedbackContentCallBackOnSuccess = callback;
    }

}
