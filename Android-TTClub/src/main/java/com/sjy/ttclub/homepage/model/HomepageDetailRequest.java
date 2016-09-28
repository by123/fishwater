package com.sjy.ttclub.homepage.model;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.homepage.ArticleComments;
import com.sjy.ttclub.bean.homepage.ArticleDetail;
import com.sjy.ttclub.bean.homepage.ArticleRecommends;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;


/**
 * Created by linhz on 2015/11/1.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageDetailRequest {

    private Context mContext;
    private int mFeedType;
    private String mSourceType;

    public HomepageDetailRequest(Context context, int feedType, String sourceType) {
        mContext = context;
        mFeedType = feedType;
        mSourceType = sourceType;
    }


    public void startCommentRequest(final String articleId, final CommentsRequestCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            return;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "topNCommentList");
        httpManager.addParams("postId", articleId);
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
                    return;
                }
                try {
                    Gson gson = new Gson();
                    ArticleComments comments = gson.fromJson(result, ArticleComments.class);
                    callback.onResultSuccess(comments);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            }
        });

    }

    public void startDetailRequest(String articleId, final DetailRequestCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            return;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "articleDetail");
        httpManager.addParams("type", String.valueOf(mFeedType));
        httpManager.addParams("aid", articleId);
        httpManager.addParams("source", mSourceType);
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isNotEmpty(result)) {
                    handleGetDetailInfoSuccess(result, callback);
                } else {
                    callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            }
        });

    }


    private void handleGetDetailInfoSuccess(String result, DetailRequestCallback callback) {
        if (StringUtils.isEmpty(result)) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
            return;
        }
        try {
            Gson gson = new Gson();
            ArticleDetail articleDetail = gson.fromJson(result, ArticleDetail.class);
            callback.onResultSuccess(articleDetail);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
        }
    }

    public void startRecommendRequest(String articleId, final RecommendRequestCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            return;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "recommendArticle");
        httpManager.addParams("aid", articleId);
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
                } else {
                    try {
                        Gson gson = new Gson();
                        ArticleRecommends comments = gson.fromJson(result, ArticleRecommends.class);
                        callback.onResultSuccess(comments);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onResultFail(HomepageConst.ERROR_TYPE_DATA);
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                callback.onResultFail(HomepageConst.ERROR_TYPE_NETWORK);
            }
        });

    }

    public interface CommentsRequestCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArticleComments comments);
    }

    public interface RecommendRequestCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArticleRecommends recommends);
    }

    public interface DetailRequestCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArticleDetail articleDetail);
    }

}
