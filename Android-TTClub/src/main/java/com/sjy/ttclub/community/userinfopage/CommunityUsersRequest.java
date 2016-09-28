package com.sjy.ttclub.community.userinfopage;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.CommunityPostJsonBean;
import com.sjy.ttclub.bean.community.CommunityUserInfo;
import com.sjy.ttclub.bean.community.UserInfoBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import java.util.ArrayList;

/**
 * Created by zwl on 2015/11/16.
 * Email: 1501448275@qq.com
 */
public class CommunityUsersRequest {
    private int mPageIndex = CommunityConstant.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
    private int mEndId = CommunityConstant.START_END_ID;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private boolean mIsRequesting = false;
    private Context mContext;
    private int mUserId = 0;
    private ArrayList<CommunityListItemInfo> mCardItems = new ArrayList<>();

    public CommunityUsersRequest(Context context, int userId) {
        mContext = context;
        mUserId = userId;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startRequest(boolean loadMore, int type, RequestResultCallback callback) {
        startRequestUserCards(loadMore, mUserId, mPageSize, type, callback);
    }


    public void startRequestUserInfo(final RequestGetUserInfoResultCallback callback) {
        if (callback == null) {
            return;
        }
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "ugoi");
        mHttpManager.addParams("userid", String.valueOf(mUserId));
        mHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, UserInfoBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                UserInfoBean resultBean = (UserInfoBean) obj;
                handleGetUserInfoSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetUserInfoFail(code, callback);
            }
        });
    }

    private void handleGetUserInfoFail(int errorType, RequestGetUserInfoResultCallback callback) {
        callback.onResultFail(errorType);
    }

    private void handleGetUserInfoSuccess(UserInfoBean userInfoBean, RequestGetUserInfoResultCallback callback) {
        if (HttpCode.SUCCESS_CODE == userInfoBean.getStatus()) {
            CommunityUserInfo user = userInfoBean.getData();
            callback.onResultSuccess(user);
        } else {
            handleGetUserInfoFail(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    private void startRequestUserCards(boolean loadMore, int mUserId, final int pageSize, int type, final RequestResultCallback
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
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "postListOfTa_2_8_patch");
        mHttpManager.addParams("userId", String.valueOf(mUserId));
        mHttpManager.addParams("endId", String.valueOf(mEndId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(mPageSize));
        mHttpManager.addParams("type", String.valueOf(type));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityPostJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityPostJsonBean resultBean = (CommunityPostJsonBean) obj;
                handleGetPostsSuccess(resultBean, callback);
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

    private void handleGetPostsSuccess(CommunityPostJsonBean result, RequestResultCallback callback) {
        mIsRequesting = false;

        if (HttpCode.SUCCESS_CODE == result.getStatus()) {

            //获取数据列表
            ArrayList<CommunityListItemInfo> newList = new ArrayList<>();
            ArrayList<CommunityPostBean> posts = new ArrayList<>();
            posts.addAll(result.getData().getPosts());
            for (int i = 0; i < posts.size(); i++) {
                CommunityListItemInfo item = new CommunityListItemInfo();
                CommunityPostBean card = posts.get(i);
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
                mEndId = result.getData().getEndId();
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

    public interface RequestGetUserInfoResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(CommunityUserInfo userInfo);
    }
}
