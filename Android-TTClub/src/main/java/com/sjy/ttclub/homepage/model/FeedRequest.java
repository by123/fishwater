package com.sjy.ttclub.homepage.model;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.homepage.FeedInfo;
import com.sjy.ttclub.bean.homepage.JTBFeedDataInfo;
import com.sjy.ttclub.homepage.HomepageCacheManager;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiawei on 2016/1/9.
 */
public class FeedRequest {
    private int mPageIndex = HomepageConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
    private int mEndId = HomepageConst.START_END_ID;
    private int mPageSize = HomepageConst.PAGE_SIZE_HOMEPAGE;


    private boolean mHasMore = false;

    private boolean mIsRequesting = false;

    private Context mContext;
    private ArrayList<FeedInfo> mFeedList = new ArrayList<FeedInfo>();

    public FeedRequest(Context context) {
        this.mContext = context;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestFeed(loadMore, mPageSize, callback);
    }

    private void startRequestFeed(boolean loadMore, final int pageSize, final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mPageSize = pageSize;
        if (mFeedList.isEmpty() || !loadMore) {
            mEndId = HomepageConst.START_END_ID;
            mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "feedList");
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleGetFeedFailed(HomepageConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                handleGetFeedSuccess(result, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetFeedFailed(HomepageConst.ERROR_TYPE_NETWORK, callback);
            }
        });
    }


    private void handleGetFeedFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetFeedSuccess(String result, RequestResultCallback callback) {
        if (StringUtils.isEmpty(result)) {
            handleGetFeedFailed(HomepageConst.ERROR_TYPE_DATA, callback);
            return;
        }
        mIsRequesting = false;
        try {
            Gson gson = new Gson();
            JTBFeedDataInfo feedDataInfo = gson.fromJson(result, JTBFeedDataInfo.class);
            if (feedDataInfo == null || feedDataInfo.getData() == null) {
                handleGetFeedFailed(HomepageConst.ERROR_TYPE_DATA, callback);
                return;
            }
            ArrayList<FeedInfo> newList = new ArrayList<FeedInfo>();
            List<FeedInfo> feedInfoList = feedDataInfo.getData().getFeed();
            if (feedInfoList == null || feedInfoList.isEmpty()) {
                mHasMore = false;
            } else {
                boolean firstPage = mPageLoadingIndex == HomepageConst.START_PAGE_INDEX;
                if (firstPage) {
                    mFeedList.clear();
                }
                mEndId = StringUtils.parseInt(feedDataInfo.getData().getEndId());
                mPageIndex = mPageLoadingIndex;
                int size = feedInfoList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }

                newList.addAll(feedInfoList);
                mFeedList.addAll(newList);
                HomepageCacheManager.cacheArticleFirstData(mContext, result);
            }
            callback.onResultSuccess(mFeedList, newList);
        } catch (Exception e) {
            e.printStackTrace();
            handleGetFeedFailed(HomepageConst.ERROR_TYPE_DATA, callback);
        }
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<FeedInfo> allList, ArrayList<FeedInfo> newList);
    }

    public void reset() {
        mFeedList.clear();
        mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
        mPageIndex = HomepageConst.START_PAGE_INDEX;
        mEndId = HomepageConst.START_END_ID;
    }

    public ArrayList<FeedInfo> getmFeedList() {
        return mFeedList;
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public boolean isRequesttingFirstPage() {
        return mPageLoadingIndex == HomepageConst.START_PAGE_INDEX;
    }

    public boolean isEmpty() {
        return mFeedList.isEmpty();
    }
}
