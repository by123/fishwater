package com.sjy.ttclub.praise;

import android.content.Context;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by Administrator on 2015/12/2.
 */
public class PraiseRequest {
    private Context mContext;
    private boolean mIsRequesting;
    private IHttpManager mHttpManager;
    public PraiseRequest(Context context) {
        this.mContext = context;
        mHttpManager = HttpManager.getBusinessHttpManger();
    }

    /**
     * 帖子点赞
     *
     * @param postId
     */
    public void addCardPraise(int postId) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_POST, CommunityConstant.PRAISE_ADD);
    }

    public void addCardPraise(int postId, RequestPraiseResultCallback callback) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_POST, CommunityConstant.PRAISE_ADD, callback);
    }

    /**
     * 帖子取消点赞
     *
     * @param postId
     */
    public void cancelCardPraise(int postId) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_POST, CommunityConstant.PRAISE_CANCEL);
    }

    public void cancelCardPraise(int postId, RequestPraiseResultCallback callback) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_POST, CommunityConstant.PRAISE_CANCEL, callback);
    }

    /**
     * 文章点赞
     *
     * @param postId
     */
    public void addArticlePraise(int postId) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_ADD);
    }

    public void addArticlePraise(int postId, RequestPraiseResultCallback callback) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_ADD, callback);
    }

    /**
     * 文章取消点赞
     *
     * @param postId
     */
    public void cancelArticlePraise(int postId) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_CANCEL);
    }

    public void cancelArticlePraise(int postId, RequestPraiseResultCallback callback) {
        requestPraise("praisePost", "postId", postId, "postType", CommunityConstant.PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_CANCEL, callback);
    }

    /**
     * 帖子评论点赞
     *
     * @param commentId
     */
    public void addCardCommentPraise(int commentId) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENT_PRAISE_TYPE_POST, CommunityConstant.PRAISE_ADD);
    }

    public void addCardCommentPraise(int commentId, RequestPraiseResultCallback callback) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENT_PRAISE_TYPE_POST, CommunityConstant.PRAISE_ADD, callback);
    }

    /**
     * 帖子评论取消点赞
     *
     * @param commentId
     */
    public void cancelCardCommentPraise(int commentId) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENT_PRAISE_TYPE_POST, CommunityConstant.PRAISE_CANCEL);
    }

    public void cancelCardCommentPraise(int commentId, RequestPraiseResultCallback callback) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENT_PRAISE_TYPE_POST, CommunityConstant.PRAISE_CANCEL, callback);
    }

    /**
     * 文章评论点赞
     *
     * @param commentId
     */
    public void addArticleCommentPraise(int commentId) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENTS_PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_ADD);
    }

    public void addArticleCommentPraise(int commentId, RequestPraiseResultCallback callback) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENTS_PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_ADD, callback);
    }

    /**
     * 文章评论取消点赞
     *
     * @param commentId
     */
    public void cancelArticleCommentPraise(int commentId) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENTS_PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_CANCEL);
    }

    public void cancelArticleCommentPraise(int commentId, RequestPraiseResultCallback callback) {
        requestPraise("praiseComment", "commentId", commentId, "cmtType", CommunityConstant.COMMENTS_PRAISE_TYPE_ARTICLE, CommunityConstant.PRAISE_CANCEL, callback);
    }

    /**
     * 发送请求
     *
     * @param praiseKey
     * @param id
     * @param type
     * @param typeValue
     * @param action
     * @param isPraise
     */
    private void requestPraise(String action, String praiseKey, int id, String type, int typeValue,
                               int isPraise) {
        mHttpManager.addParams("a", action);
        mHttpManager.addParams(praiseKey, String.valueOf(id));
        mHttpManager.addParams(type, String.valueOf(typeValue));
        mHttpManager.addParams("praiseFlag", String.valueOf(isPraise));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, BaseBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                BaseBean bean = (BaseBean) obj;
                if (HttpCode.SUCCESS_CODE == bean.getStatus()) {
                    //点赞成功
                }
            }

            @Override
            public void onError(String errorStr, int code) {
            }
        });
    }

    private void requestPraise(String action, String praiseKey, int id, String type, int typeValue,
                               int isPraise, final RequestPraiseResultCallback callback) {
        if (callback == null) {
            return;
        }

        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mHttpManager.addParams("a", action);
        mHttpManager.addParams(praiseKey, String.valueOf(id));
        mHttpManager.addParams(type, String.valueOf(typeValue));
        mHttpManager.addParams("praiseFlag", String.valueOf(isPraise));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, BaseBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                BaseBean bean = (BaseBean) obj;
                handleRequestResultSuccess(bean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleRequestResultFail(CommunityConstant.ERROR_TYPE_DATA, callback);
            }
        });
    }

    private void handleRequestResultFail(int errorCode, RequestPraiseResultCallback callback) {
        callback.onResultFail(errorCode);
        mIsRequesting = false;

    }

    private void handleRequestResultSuccess(BaseBean bean, RequestPraiseResultCallback callback) {
        mIsRequesting = false;

            if (HttpCode.SUCCESS_CODE == bean.getStatus()) {
                //点赞成功
                callback.onResultSuccess();
            } else {
                handleRequestResultFail(bean.getStatus(), callback);
            }
    }

    public interface RequestPraiseResultCallback {
        void onResultFail(int errorCode);

        void onResultSuccess();
    }
}
