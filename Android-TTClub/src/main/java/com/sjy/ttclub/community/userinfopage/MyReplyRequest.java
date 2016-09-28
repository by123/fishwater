package com.sjy.ttclub.community.userinfopage;

import android.content.Context;

import com.sjy.ttclub.bean.community.MyCommentBean;
import com.sjy.ttclub.bean.community.MyCommentJsonBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/1.
 */
public class MyReplyRequest {
    private int mPageIndex = CommunityConstant.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
    private int mEndId = CommunityConstant.START_END_ID;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private boolean mIsRequesting = false;
    private Context mContext;
    private ArrayList<MyCommentBean> mReplys = new ArrayList<>();

    public MyReplyRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startRequest(boolean loadMore, int type, RequestResultCallback callback) {
        startRequestCardByType(loadMore, type, mPageSize, callback);
    }

    private void startRequestCardByType(boolean loadMore, int type, final int pageSize, final RequestResultCallback
            callback) {

        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mPageSize = pageSize;
        if (mReplys.isEmpty() || !loadMore) {
            mEndId = CommunityConstant.START_END_ID;
            mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "myReplyList");
        mHttpManager.addParams("endId", String.valueOf(mEndId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(mPageSize));
        mHttpManager.addParams("type", String.valueOf(type));
        mHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, MyCommentJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                MyCommentJsonBean resultBean = (MyCommentJsonBean) obj;
                handleGetCardsSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCardsFailed(code, callback);
            }
        });
    }

    private void handleGetCardsFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }


    private void handleGetCardsSuccess(MyCommentJsonBean result, RequestResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {

            //获取数据列表
            ArrayList<MyCommentBean> newList = new ArrayList<>();
            newList.addAll(result.getData().getMsgArray());
            if (newList == null || newList.isEmpty()) {
                mHasMore = false;
            } else {
                if (mPageLoadingIndex == CommunityConstant.START_PAGE_INDEX) {
                    mReplys.clear();
                }
                mEndId = result.getData().getEndId();
                mPageIndex = mPageLoadingIndex;
                int size = newList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                mReplys.addAll(newList);
            }
            callback.onResultSuccess(mReplys, newList);
        } else {
            handleGetCardsFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }


    public void reset() {
        mReplys.clear();
        mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
        mPageIndex = CommunityConstant.START_PAGE_INDEX;
        mEndId = CommunityConstant.START_END_ID;
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public boolean isRequesttingFirstPage() {
        return mPageLoadingIndex == CommunityConstant.START_PAGE_INDEX;
    }

    public boolean isEmpty() {
        return mReplys.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<MyCommentBean> allList, ArrayList<MyCommentBean> newList);
    }
}
