package com.sjy.ttclub.homepage.model;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.homepage.ArticleFeed;
import com.sjy.ttclub.bean.homepage.ArticleInfo;
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
 * Created by linhz on 2015/11/1.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageFeedRequest {
    private int mPageIndex = HomepageConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
    private int mEndId = HomepageConst.START_END_ID;
    private int mType = HomepageConst.INVALIDE_TYPE;
    private int mChildType = 0;
    private int mPageSize = HomepageConst.PAGE_SIZE_HOMEPAGE;

    private boolean mHasMore = false;

    private boolean mIsRequesting = false;

    private Context mContext;
    private ArrayList<ArticleInfo> mFeedList = new ArrayList<ArticleInfo>();

    public HomepageFeedRequest(Context context, int type, int childType) {
        mContext = context;
        mType = type;
        mChildType = childType;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestFeedByType(loadMore, mType, mChildType, mPageSize, callback);
    }

    private void startRequestFeedByType(boolean loadMore, int type, int childType, final int pageSize,
                                        final RequestResultCallback callback) {

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
        httpManager.addParams("a", "articleList27");
        httpManager.addParams("type", String.valueOf(type));
        httpManager.addParams("childType", String.valueOf(childType));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleGetArticleFailed(HomepageConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                handleGetArticleSuccess(result, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetArticleFailed(HomepageConst.ERROR_TYPE_NETWORK, callback);
            }
        });
    }


    private void handleGetArticleFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetArticleSuccess(String result, RequestResultCallback callback) {
        if (StringUtils.isEmpty(result)) {
            handleGetArticleFailed(HomepageConst.ERROR_TYPE_DATA, callback);
            return;
        }
        mIsRequesting = false;
        try {
            Gson gson = new Gson();
            ArticleFeed articleFeed = gson.fromJson(result, ArticleFeed.class);
            if (articleFeed == null || articleFeed.getData() == null) {
                handleGetArticleFailed(HomepageConst.ERROR_TYPE_DATA, callback);
                return;
            }
            ArrayList<ArticleInfo> newList = new ArrayList<ArticleInfo>();
            List<ArticleInfo> articleList = articleFeed.getData().getArticles();
            if (articleList == null || articleList.isEmpty()) {
                mHasMore = false;
            } else {
                boolean firstPage = mPageLoadingIndex == HomepageConst.START_PAGE_INDEX;
                if (firstPage) {
                    mFeedList.clear();
                }
                mEndId = StringUtils.parseInt(articleFeed.getData().getEndId());
                mPageIndex = mPageLoadingIndex;
                int size = articleList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }

                newList.addAll(articleList);
                mFeedList.addAll(newList);
                if (mType == HomepageConst.FEED_TYPE_HOMEPAGE && firstPage) {
                    HomepageCacheManager.cacheArticleFirstData(mContext, result);
                }
            }
            callback.onResultSuccess(mFeedList, newList);
        } catch (Exception e) {
            e.printStackTrace();
            handleGetArticleFailed(HomepageConst.ERROR_TYPE_DATA, callback);
        }
    }

    public ArrayList<ArticleInfo> getFeedList() {
        return mFeedList;
    }

    public void reset() {
        mFeedList.clear();
        mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
        mPageIndex = HomepageConst.START_PAGE_INDEX;
        mEndId = HomepageConst.START_END_ID;
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

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<ArticleInfo> allList, ArrayList<ArticleInfo> newList);
    }
}
