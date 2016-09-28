package com.sjy.ttclub.community.model;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityPostJsonBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.TimeUtil;

import java.util.ArrayList;

/**
 * Created by zwl on 2015/11/11.
 * Email: 1501448275@qq.com
 */
public class CommunityRequest {

    private int mPageIndex = CommunityConstant.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
    private int mEndId = CommunityConstant.START_END_ID;
    private int mType = CommunityConstant.INVALIDE_TYPE;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private boolean mIsRequesting = false;
    private Context mContext;
    private int mCircleId = 0;
    private IHttpManager mHttpManager;
    private ArrayList<CommunityListItemInfo> mCardItems = new ArrayList<>();

    public CommunityRequest(Context context) {
        mContext = context;
    }

    public CommunityRequest(Context context, int type, int circleId) {
        mContext = context;
        mType = type;
        mCircleId = circleId;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestCardByType(loadMore, mType, mPageSize, callback);
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
        if (mCardItems.isEmpty() || !loadMore) {
            mEndId = CommunityConstant.START_END_ID;
            mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "postListOfCircle");
        mHttpManager.addParams("category", String.valueOf(type));
        mHttpManager.addParams("endId", String.valueOf(mEndId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(mPageSize));
        mHttpManager.addParams("circleId", String.valueOf(mCircleId));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityPostJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityPostJsonBean cardJsonBean = (CommunityPostJsonBean) obj;
                handleGetCardsSuccess(cardJsonBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCardsFailed(code, callback);
            }
        });
    }

    public void startRequestPostByHot(boolean loadMore, final RequestResultCallback
            callback) {

        if (callback == null) {
            return;
        }

        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mPageSize = CommunityConstant.PAGE_SIZE_CARD;
        if (mCardItems.isEmpty() || !loadMore) {
            mEndId = CommunityConstant.START_END_ID;
            mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "followPostList");
        mHttpManager.addParams("endId", String.valueOf(mEndId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(mPageSize));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityPostJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityPostJsonBean cardJsonBean = (CommunityPostJsonBean) obj;
                handleGetHotCardsSuccess(cardJsonBean, callback);
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

    private void handleGetHotCardsSuccess(CommunityPostJsonBean cardJsonBean, RequestResultCallback callback) {

        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == cardJsonBean.getStatus()) {

            //获取数据列表
            ArrayList<CommunityListItemInfo> newList = new ArrayList<>();
            ArrayList<CommunityPostBean> posts = new ArrayList<>();
            posts.addAll(cardJsonBean.getData().getPosts());
            for (int i = 0; i < posts.size(); i++) {
                CommunityListItemInfo item = new CommunityListItemInfo();
                CommunityPostBean card = posts.get(i);
                processPostCardValue(card);
                if (card.getCircleType() == CommunityConstant.CIRCLE_TYPE_POST) {
                    item.setItemType(CommunityItemType.POST_TYPE_IMAGE);
                    item.setData(card);
                    newList.add(item);
                } else {
                    item.setItemType(CommunityItemType.POST_TYPE_QA);
                    item.setData(card);
                    newList.add(item);
                }
            }

            if (newList == null || newList.isEmpty()) {
                mHasMore = false;
            } else {
                if (mPageLoadingIndex == CommunityConstant.START_PAGE_INDEX) {
                    mCardItems.clear();
                }
                mEndId = cardJsonBean.getData().getEndId();
                mPageIndex = mPageLoadingIndex;
                int size = newList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                mCardItems.addAll(newList);
            }
            callback.onResultSuccess(mCardItems, newList);
        } else {
            handleGetCardsFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    private void handleGetCardsSuccess(CommunityPostJsonBean cardJsonBean, RequestResultCallback callback) {
        mIsRequesting = false;

        if (HttpCode.SUCCESS_CODE == cardJsonBean.getStatus()) {

            //获取数据列表
            ArrayList<CommunityListItemInfo> newList = new ArrayList<>();
            ArrayList<CommunityPostBean> posts = new ArrayList<>();
            ArrayList<CommunityPostBean> topPosts = new ArrayList<>();
            ArrayList<CommunityPostBean> gerneralPosts = new ArrayList<>();
            posts.addAll(cardJsonBean.getData().getPosts());
            for (int i = 0; i < posts.size(); i++) {
                CommunityPostBean card = posts.get(i);
                processPostCardValue(card);
                if (card.getPostTag() == 2) {
                    topPosts.add(card);
                } else {
                    gerneralPosts.add(card);
                }
            }
            updateDataList(newList, topPosts, gerneralPosts);
            if (newList == null || newList.isEmpty()) {
                mHasMore = false;
            } else {
                if (mPageLoadingIndex == CommunityConstant.START_PAGE_INDEX) {
                    mCardItems.clear();
                }
                mEndId = cardJsonBean.getData().getEndId();
                mPageIndex = mPageLoadingIndex;
                int size = newList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                mCardItems.addAll(newList);
            }
            callback.onResultSuccess(mCardItems, newList);
        } else {
            handleGetCardsFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    private CommunityPostBean processPostCardValue(CommunityPostBean postCard) {
        String userName = postCard.getNickname().replace("\n", "");
        String content = postCard.getBriefContent().replace("\n", "");
        String theme = postCard.getPostTitle().replace("\n", "");
        String time = TimeUtil.getCDTime(postCard.getPublishTime());
        postCard.setPublishTime(time);
        postCard.setNickname(userName);
        postCard.setBriefContent(content);
        postCard.setPostTitle(theme);
        return postCard;
    }

    private void updateDataList(ArrayList<CommunityListItemInfo> newList, ArrayList<CommunityPostBean> topPosts, ArrayList<CommunityPostBean> generalPosts) {
        CommunityListItemInfo item;
        if (topPosts.size() > 0) {
            for (CommunityPostBean card : topPosts) {
                item = new CommunityListItemInfo();
                item.setItemType(CommunityItemType.POST_TYPE_TOP);
                item.setData(card);
                newList.add(item);
            }
            item = new CommunityListItemInfo();
            item.setItemType(CommunityItemType.POST_TYPE_DEFAULT_DIVIDER);
            newList.add(item);
        }

        if (generalPosts.size() > 0) {
            for (CommunityPostBean postCard : generalPosts) {
                if (postCard.getCircleType() == CommunityConstant.CIRCLE_TYPE_POST) {
                    item = new CommunityListItemInfo();
                    item.setItemType(CommunityItemType.POST_TYPE_IMAGE);
                    item.setData(postCard);
                    newList.add(item);
                } else {
                    item = new CommunityListItemInfo();
                    item.setItemType(CommunityItemType.POST_TYPE_QA);
                    item.setData(postCard);
                    newList.add(item);
                }
            }
        }
    }

    public void reset() {
        mCardItems.clear();
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
        return mCardItems.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList);
    }
}
