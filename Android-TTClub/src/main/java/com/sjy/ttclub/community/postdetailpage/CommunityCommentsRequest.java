package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommunityCommentDataBean;
import com.sjy.ttclub.bean.community.CommunityPostJsonBean;
import com.sjy.ttclub.comment.CommentView;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;

/**
 * Created by zwl on 2015/11/16.
 * Email: 1501448275@qq.com
 */
public class CommunityCommentsRequest {
    private int mPageIndex = CommunityConstant.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
    private int mEndId = CommunityConstant.START_END_ID;
    private int mType = CommunityConstant.INVALIDE_TYPE;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private boolean mIsRequesting = false;
    private Context mContext;
    private int mCardId = 0;
    private int mUserId = 0;
    private ArrayList<CommentBean> mComments = new ArrayList<>();
    private IHttpManager mHttpManager;

    public CommunityCommentsRequest(Context context, int cardId, int userId) {
        mContext = context;
        mCardId = cardId;
        mUserId = userId;

    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestCardByType(loadMore, mCardId, mUserId, CommunityConstant.NO_ONLY_HOST, CommunityConstant.ASCEND_ORDER, CommunityConstant.PAGE_SIZE_CARD, callback);
    }

    public void startRequestByHost(boolean loadMore, RequestResultCallback callback) {
        startRequestCardByType(loadMore, mCardId, mUserId, CommunityConstant.ONLY_HOST, CommunityConstant.ASCEND_ORDER, CommunityConstant.PAGE_SIZE_CARD, callback);
    }

    public void startRequestByDropOrder(boolean loadMore, RequestResultCallback callback) {
        startRequestCardByType(loadMore, mCardId, mUserId, CommunityConstant.NO_ONLY_HOST, CommunityConstant.DROP_ORDER, CommunityConstant.PAGE_SIZE_CARD, callback);
    }

    private void startRequestCardByType(boolean loadMore, int cardId, int userId, int hostFlag, int orderFlag, final int pageSize, final RequestResultCallback
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
        if (mComments.isEmpty() || !loadMore) {
            mEndId = CommunityConstant.START_END_ID;
            mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "commentList");
        mHttpManager.addParams("postId", String.valueOf(cardId));
        mHttpManager.addParams("cmtType", String.valueOf(CommunityConstant.COMMENTS_TYPE_POST));
        mHttpManager.addParams("isOnlyAuthor", String.valueOf(hostFlag));
        mHttpManager.addParams("listOrder", String.valueOf(orderFlag));
        mHttpManager.addParams("authorId", String.valueOf(userId));
        mHttpManager.addParams("endId", String.valueOf(mEndId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(mPageSize));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityCommentDataBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityCommentDataBean commentDataBean = (CommunityCommentDataBean) obj;
                handleGetCardsSuccess(commentDataBean, callback);
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

    private void handleGetCardsSuccess(CommunityCommentDataBean result, RequestResultCallback callback) {
        mIsRequesting = false;

        if (HttpCode.SUCCESS_CODE == result.getStatus()) {

            //获取数据列表
            ArrayList<CommentBean> newList = new ArrayList<CommentBean>();
            newList.addAll(result.getData().getComments());
            if (newList == null || newList.isEmpty()) {
                mHasMore = false;
            } else {
                if (mPageLoadingIndex == CommunityConstant.START_PAGE_INDEX) {
                    mComments.clear();
                }
                mEndId = result.getData().getEndId();
                mPageIndex = mPageLoadingIndex;
                int size = newList.size();
                if (size < mPageSize) {
                    mHasMore = false;
                } else {
                    mHasMore = true;
                }
                mComments.addAll(newList);
            }
            callback.onResultSuccess(mComments, newList);
        } else {
            handleGetCardsFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }

    }

    public void reset() {
        mComments.clear();
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
        return mComments.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<CommentBean> allList, ArrayList<CommentBean> newList);
    }
}
