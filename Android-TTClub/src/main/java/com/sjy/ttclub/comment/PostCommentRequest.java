package com.sjy.ttclub.comment;

import android.content.Context;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunitySendCommentResultBean;
import com.sjy.ttclub.common.RespondCodeHelper;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by Administrator on 2015/12/1.
 */
public class PostCommentRequest {

    private Context mContext;
    private int mPostType = CommunityConstant.POST_TYPE_COMMENT;//发送的类型 评论，回复
    private int mContentType = CommunityConstant.CONTENT_TYPE_CARD;//内容类型 帖子，文章
    private String mPostContent;
    private int mPostId;
    private int mCommentId;
    private int mReplyId;
    private boolean mIsRequesting = false;

    private boolean mAutoToast = true;

    public PostCommentRequest(Context mContext, int mPostType, int mPostId) {
        this.mContext = mContext;
        this.mPostType = mPostType;
        this.mPostId = mPostId;
    }

    public void startCommentReplyRequest(int contentType, int contentId, String content, PostRequestResultCallback
            callback) {
        this.mContentType = contentType;
        this.mPostContent = content;
        statRequestById(contentId, callback);
    }
    public void startCommentRequest(int mPostType,int contentType, int contentId, String content, PostRequestResultCallback
            callback) {
        this.mPostType=mPostType;
        this.mContentType = contentType;
        this.mPostContent = content;
        statRequestById(contentId, callback);
    }
    public void startCommentRequest(int contentType, String content, PostRequestResultCallback callback) {
        this.mContentType = contentType;
        this.mPostContent = content;
        statRequestById(CommunityConstant.POST_COMMENT_NO_COMMENTID, callback);
    }

    public void statRequestById(int contentId, final PostRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFailed(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;

        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "submitComment");
        httpManager.addParams("operType", String.valueOf(mPostType));
        httpManager.addParams("cmtType", String.valueOf(mContentType));
        httpManager.addParams("postId", String.valueOf(mPostId));
        httpManager.addParams("commentId", String.valueOf(contentId));
        httpManager.addParams("content", mPostContent);
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunitySendCommentResultBean.class, new
                IHttpCallBack() {
                    @Override
                    public <T> void onSuccess(T obj, String result) {
                        CommunitySendCommentResultBean resultBean = (CommunitySendCommentResultBean) obj;
                        handleSendSuccess(resultBean, callback);
                    }

                    @Override
                    public void onError(String errorStr, int code) {
                        handleSendFail(code, callback);
                    }
                });
    }

    private void handleSendFail(int errorType, PostRequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFailed(errorType);
        if (mAutoToast) {
            if (errorType == CommunityConstant.ERROR_TYPE_NETWORK) {
                ToastHelper.showToast(mContext, R.string.homepage_network_error);
            } else if (errorType == CommunityConstant.ERROR_TYPE_DATA) {
                ToastHelper.showToast(mContext, R.string.community_post_failed);
            }
        }
    }

    private void handleSendSuccess(CommunitySendCommentResultBean result, PostRequestResultCallback callback) {
        mIsRequesting = false;
        //统计回复成功
        StatsModel.stats(StatsKeyDef.WRITE_REPLY);
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            if (mAutoToast) {
                CommunitySendCommentResultBean.ResultBean resultBean = result.getData();
                if (resultBean.getDayReplyCount() <= resultBean.getMaxRewardTimes()) {
                    if (resultBean.getIsLevelUp() == 0) {
                        ToastHelper.showToast(mContext, "回帖成功！", "经验+" + resultBean.getObtainedExp() + "(" + resultBean
                                .getDayReplyCount() + "/" + resultBean.getMaxRewardTimes() + ")");
                    } else {
                        ToastHelper.showToast(mContext, "回帖成功！", "升到" + resultBean.getCurrentLevel() + "级");
                    }
                }
            }
            callback.onResultSuccess(result.getData());
        } else {
            handleSendFail(result.getStatus(), callback);
            if (mAutoToast) {
                int errorCode = result.getStatus();
                RespondCodeHelper.handlerResultStatus(mContext, errorCode);
            }
        }

    }

    public void setAutoToast(boolean isAuto) {
        this.mAutoToast=isAuto;
    }

    public interface PostRequestResultCallback {
        void onResultFailed(int errorCode);

        void onResultSuccess(CommunitySendCommentResultBean.ResultBean resultBean);
    }
}
