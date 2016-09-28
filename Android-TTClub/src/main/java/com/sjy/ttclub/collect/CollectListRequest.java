/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.collect;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.collect.CollectArticleBean;
import com.sjy.ttclub.bean.collect.CollectPostBean;
import com.sjy.ttclub.bean.collect.CollectProductBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectListRequest {

    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
    private int mEndId = CommonConst.START_END_ID;
    private int mPageSize = 20;

    private int mType = CommonConst.COLLECT_TYPE_ARTICLE;

    private boolean mHasMore = false;

    private boolean mIsRequesting = false;

    private boolean mIsSelectAll = false;

    private Context mContext;
    private ArrayList<CollectItemInfo> mCollectList = new ArrayList<CollectItemInfo>();


    public CollectListRequest(Context context, int type) {
        mContext = context;
        mType = type;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestFeedByType(loadMore, mType, mPageSize, callback);
    }

    private void startRequestFeedByType(boolean loadMore, int type, final int pageSize,
                                        final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(CommonConst.ERROR_TYPE_NETWORK);
            return;
        }
        if (isRequesting()) {
            callback.onResultFail(CommonConst.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mPageSize = pageSize;
        if (mCollectList.isEmpty() || !loadMore) {
            mEndId = CommonConst.START_END_ID;
            mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "collectPostList");
        httpManager.addParams("listType", String.valueOf(type));
        httpManager.addParams("pageSize", String.valueOf(pageSize));
        httpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleGetCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                handleGetCollectSuccess(result, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCollectFailed(CommonConst.ERROR_TYPE_NETWORK, callback);
                super.onError(errorStr, code);
            }
        });

    }


    private void handleGetCollectFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private ArrayList<CollectItemInfo> parseArticleCollectData(String result) {
        ArrayList<CollectItemInfo> newList = null;
        try {
            Gson gson = new Gson();
            CollectArticleBean articleData = gson.fromJson(result, CollectArticleBean.class);

            if (articleData == null || articleData.getData() == null) {
                return null;
            }
            newList = new ArrayList<CollectItemInfo>();
            List<CollectArticleBean.ArticleInfo> articleList = articleData.getData().getArticles();
            mEndId = articleData.getData().getEndId();
            if (articleList == null) {
                return newList;
            }
            CollectItemInfo itemInfo;
            for (CollectArticleBean.ArticleInfo articleInfo : articleList) {
                itemInfo = new CollectItemInfo();
                itemInfo.data = articleInfo;
                itemInfo.isSelected = mIsSelectAll;
                newList.add(itemInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }

    private ArrayList<CollectItemInfo> parsePostCollectData(String result) {
        ArrayList<CollectItemInfo> newList = null;
        try {
            Gson gson = new Gson();
            CollectPostBean postData = gson.fromJson(result, CollectPostBean.class);

            if (postData == null || postData.getData() == null) {
                return null;
            }
            newList = new ArrayList<CollectItemInfo>();
            List<CollectPostBean.PostInfo> postList = postData.getData().getPosts();
            mEndId = postData.getData().getEndId();
            if (postList == null) {
                return newList;
            }
            CollectItemInfo itemInfo;
            for (CollectPostBean.PostInfo postInfo : postList) {
                itemInfo = new CollectItemInfo();
                itemInfo.data = postInfo;
                itemInfo.isSelected = mIsSelectAll;
                newList.add(itemInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }

    private ArrayList<CollectItemInfo> parseGoodsCollectData(String result) {
        ArrayList<CollectItemInfo> newList = null;
        try {
            Gson gson = new Gson();
            CollectProductBean productData = gson.fromJson(result, CollectProductBean.class);

            if (productData == null || productData.getData() == null) {
                return null;
            }
            newList = new ArrayList<CollectItemInfo>();
            List<CollectProductBean.ProductInfo> goodList = productData.getData().getGoods();
            mEndId = productData.getData().getEndId();
            if (goodList == null) {
                return newList;
            }
            CollectItemInfo itemInfo;
            for (CollectProductBean.ProductInfo productInfo : goodList) {
                itemInfo = new CollectItemInfo();
                itemInfo.data = productInfo;
                itemInfo.isSelected = mIsSelectAll;
                newList.add(itemInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }

    private void handleGetCollectSuccess(String result, RequestResultCallback callback) {
        if (StringUtils.isEmpty(result)) {
            handleGetCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
            return;
        }
        mIsRequesting = false;
        try {
            ArrayList<CollectItemInfo> newList = null;
            if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
                newList = parseArticleCollectData(result);
            } else if (mType == CommonConst.COLLECT_TYPE_POST) {
                newList = parsePostCollectData(result);
            } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
                newList = parseGoodsCollectData(result);
            }
            if (newList == null) {
                handleGetCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
            } else {
                mPageIndex = mPageLoadingIndex;
                mCollectList.addAll(newList);
                if (newList.size() < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                callback.onResultSuccess(mCollectList, newList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleGetCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
        }
    }

    public void delSelectedCollects(final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        final ArrayList<CollectItemInfo> selectedList = new ArrayList<CollectItemInfo>();
        boolean isFirstItem = true;
        String id = null;
        for (CollectItemInfo info : mCollectList) {
            if (info.isSelected) {
                selectedList.add(info);
                if (mType == CommonConst.COLLECT_TYPE_POST) {
                    id = ((CollectPostBean.PostInfo) info.data).getId();
                } else if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
                    id = ((CollectArticleBean.ArticleInfo) info.data).getId();
                } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
                    id = ((CollectProductBean.ProductInfo) info.data).getId();
                }
                if (StringUtils.isNotEmpty(id)) {
                    if (!isFirstItem) {
                        builder.append(",").append(id);
                    } else {
                        builder.append(id);
                        isFirstItem = false;
                    }
                }
            }
        }

        String delIds = builder.toString();
        if (StringUtils.isEmpty(delIds)) {
            callback.onResultFail(CommonConst.ERROR_TYPE_DATA);
            return;
        }

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "collectPost");
        httpManager.addParams("postId", delIds);
        httpManager.addParams("collectType", String.valueOf(mType));
        httpManager.addParams("collectFlag", String.valueOf(CommonConst.COLLECT_CANCEL));
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    callback.onResultFail(CommonConst.ERROR_TYPE_DATA);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("status");
                    if (HttpCode.SUCCESS_CODE == code) {
                        handleCancelCollectSuccess(selectedList, callback);
                    } else {
                        handleCancelCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
                    }
                } catch (JSONException e) {
                    handleCancelCollectFailed(CommonConst.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                handleCancelCollectFailed(CommonConst.ERROR_TYPE_NETWORK, callback);
            }
        });

    }

    private void handleCancelCollectSuccess(ArrayList<CollectItemInfo> selectedList, RequestResultCallback callback) {
        for (CollectItemInfo info : selectedList) {
            mCollectList.remove(info);
        }
        callback.onResultSuccess(mCollectList, null);
    }

    private void handleCancelCollectFailed(int errorType, RequestResultCallback callback) {
        callback.onResultFail(errorType);
    }

    public int getSelectedCount() {
        int count = 0;
        for (CollectItemInfo info : mCollectList) {
            if (info.isSelected) {
                count += 1;
            }
        }
        return count;
    }

    public void setSelectAll(boolean selectAll) {
        mIsSelectAll = selectAll;
        for (CollectItemInfo info : mCollectList) {
            info.isSelected = selectAll;
        }
    }

    public ArrayList<CollectItemInfo> getCollectList() {
        return mCollectList;
    }

    public void reset() {
        mCollectList.clear();
        mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
        mPageIndex = CommonConst.START_PAGE_INDEX;
        mEndId = CommonConst.START_END_ID;
    }


    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public boolean isEmpty() {
        return mCollectList.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<CollectItemInfo> allList, ArrayList<CollectItemInfo> newList);
    }


    public static class CollectItemInfo {
        public Object data;
        public boolean isSelected = false;
    }
}
