package com.sjy.ttclub.comment;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommentJsonBean;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.bean.community.CommentReplyJsonBean;
import com.sjy.ttclub.community.CommunityConstant;
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
public class CommentReplyRequest {
    private int mPageIndex = CommunityConstant.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommunityConstant.START_PAGE_INDEX;
    private int mEndId = CommunityConstant.START_END_ID;
    private int mPageSize = CommunityConstant.PAGE_SIZE_CARD;
    private boolean mHasMore = false;
    private boolean mIsRequesting = false;
    private Context mContext;
    private int mType = CommunityConstant.COMMENTS_TYPE_POST;
    private int mCommentId = 0;
    private ArrayList<CommentReplyBean> mReplys = new ArrayList<>();
    private IHttpManager mHttpManager;

    public CommentReplyRequest(Context context, int type, int commentId) {
        mContext = context;
        mType = type;
        mCommentId = commentId;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void startRequest(boolean loadMore, RequestResultCallback callback) {
        startRequestReliesData(loadMore, mCommentId, mType, mPageSize, callback);
    }

    public void startRequestById(RequestCommentResultCallback callback) {
        startRequestComment(mCommentId, mType, callback);
    }

    private void startRequestComment(int commentId, int cmtType, final RequestCommentResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "commentDetail");
        mHttpManager.addParams("commentId", String.valueOf(commentId));
        mHttpManager.addParams("cmtType", String.valueOf(cmtType));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommentJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommentJsonBean resultBean = (CommentJsonBean) obj;
                handleGetCommentSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCommentFailed(code, callback);
            }
        });
    }

    private void startRequestReliesData(boolean loadMore, int commentId, int cmtType, final int pageSize, final RequestResultCallback
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
        IHttpManager httpManger = HttpManager.getBusinessHttpManger();
        httpManger.addParams("a", "replyList");
        httpManger.addParams("commentId", String.valueOf(commentId));
        httpManger.addParams("cmtType", String.valueOf(cmtType));
        httpManger.addParams("endId", String.valueOf(mEndId));
        httpManger.addParams("page", String.valueOf(mPageLoadingIndex));
        httpManger.addParams("pageSize", String.valueOf(mPageSize));
        httpManger.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommentReplyJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommentReplyJsonBean resultBean = (CommentReplyJsonBean) obj;
                handleGetCommentRepliesSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCommentRepliesFailed(code, callback);
            }
        });
    }

    private void handleGetCommentFailed(int errorType, RequestCommentResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetCommentSuccess(CommentJsonBean result, RequestCommentResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            //获取数据列表
            callback.onResultSuccess(result.getData());
        } else {
            if (result.getStatus() == CommunityConstant.ERROR_TYPE_COMMENT_NOT_EXIST) {
                handleGetCommentFailed(CommunityConstant.ERROR_TYPE_COMMENT_NOT_EXIST, callback);
            } else {
                handleGetCommentFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
            }
        }

    }

    private void handleGetCommentRepliesFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetCommentRepliesSuccess(CommentReplyJsonBean result, RequestResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            //获取数据列表
            ArrayList<CommentReplyBean> newList = new ArrayList<>();
            newList.addAll(result.getData().getReplys());
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
            handleGetCommentRepliesFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
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

        void onResultSuccess(ArrayList<CommentReplyBean> allList, ArrayList<CommentReplyBean> newList);
    }

    public interface RequestCommentResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(CommentBean commentBean);
    }
}
