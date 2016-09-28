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
import java.util.List;

/**
 * Created by linhz on 2015/11/1.
 * Email: linhaizhong@ta2she.com
 */
public class CommentsRequest {

    private static final int MAX_PAGE_SIZE = 20;
    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
    private int mEndId = CommonConst.START_END_ID;

    private boolean mHasMore = false;

    private ArrayList<CommentBean> mCommentList = new ArrayList<>();
    private Context mContext;

    private int mCommentType;

    private boolean mIsRequesting = false;

    private IHttpManager mHttpManager;

    public CommentsRequest(Context context, int commentType) {

        mContext = context;
        mCommentType = commentType;
    }

    public void startRequest(final String articleId, boolean isLoadMore, final RequestResultCallback callback) {
        int pageIndex;
        if (mCommentList.isEmpty() && !isLoadMore) {
            mEndId = CommonConst.START_END_ID;
            pageIndex = 1;
        } else {
            pageIndex = mPageIndex + 1;
        }
        startRequest(articleId, pageIndex, mEndId, callback);
    }

    public void startRequest(final String articleId, int pageIndex, int endId, final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
        }
        mIsRequesting = true;
        mPageLoadingIndex = pageIndex;
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "commentList");
        mHttpManager.addParams("postId", articleId);
        mHttpManager.addParams("cmtType", String.valueOf(mCommentType));
        mHttpManager.addParams("isOnlyAuthor", "");
        mHttpManager.addParams("authorId", "");
        mHttpManager.addParams("endId", String.valueOf(endId));
        mHttpManager.addParams("page", String.valueOf(mPageLoadingIndex));
        mHttpManager.addParams("pageSize", String.valueOf(MAX_PAGE_SIZE));
        mHttpManager.addParams("listOrder", "");
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityCommentDataBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityCommentDataBean resultBean = (CommunityCommentDataBean) obj;
                handleGetCommentsSuccess(resultBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCommentsFailed(callback, code);
            }
        });
    }

    private void handleGetCommentsFailed(RequestResultCallback callback, int errorType) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetCommentsSuccess(CommunityCommentDataBean result, RequestResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            if (mPageLoadingIndex == CommonConst.START_PAGE_INDEX) {
                mCommentList.clear();
            }
            List<CommentBean> commentList = result.getData().getComments();
            mEndId = result.getData().getEndId();
            mPageIndex = mPageLoadingIndex;
            if (commentList.size() >= MAX_PAGE_SIZE) {
                mHasMore = true;
            } else {
                mHasMore = false;
            }
            mCommentList.addAll(commentList);
            callback.onResultSuccess(mCommentList, commentList);
        } else {
            handleGetCommentsFailed(callback, CommunityConstant.ERROR_TYPE_DATA);
        }
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(List<CommentBean> allList, List<CommentBean>
                newList);
    }

}
