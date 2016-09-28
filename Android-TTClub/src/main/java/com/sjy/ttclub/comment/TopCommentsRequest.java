package com.sjy.ttclub.comment;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommunityCommentDataBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import java.util.ArrayList;

/**
 * Created by zhangwulin on 2015/12/21.
 * email 1501448275@qq.com
 */
public class TopCommentsRequest {

    private static final int MAX_PAGE_SIZE = 20;
    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
    private int mEndId = CommonConst.START_END_ID;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private Context mContext;
    private int mPostId;
    private boolean mIsRequesting = false;
    private IHttpManager mHttpManager;
    private ArrayList<CommentBean> mComments = new ArrayList<>();

    public TopCommentsRequest(Context mContext, int postId) {

        this.mContext = mContext;
        mPostId = postId;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startHotRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestCardByHot(loadMore, mPostId, CommunityConstant.PAGE_SIZE_CARD, callback);
    }

    private void startRequestCardByHot(boolean loadMore, int postId, final int pageSize, final RequestResultCallback
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
        mHttpManager.addParams("a", "hotCommentList");
        mHttpManager.addParams("postId", String.valueOf(postId));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityCommentDataBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityCommentDataBean resultBean = (CommunityCommentDataBean) obj;
                handleGetHotCommentsSuccess(resultBean, callback);
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

    private void handleGetHotCommentsSuccess(CommunityCommentDataBean result, RequestResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {

            //获取数据列表
            ArrayList<CommentBean> newList = new ArrayList<>();
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
