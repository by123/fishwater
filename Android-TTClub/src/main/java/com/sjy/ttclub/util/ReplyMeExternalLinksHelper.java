package com.sjy.ttclub.util;

import android.os.Message;

import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.ExternalLinksHelperParam;
import com.sjy.ttclub.comment.CommentListInfo;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;

/**
 * Created by gangqing on 2015/12/7.
 * Email:denggangqing@ta2she.com
 */
public class ReplyMeExternalLinksHelper {
    public void goExternalLinks(ExternalLinksHelperParam param) {
        //评论类型 -> 帖子:1；问答:2；文章:3；帖子评论:4；问答评论:5；文章评论:6；测试文章:7；导购文章:8；
        switch (param.getType()) {
            case Constant.EXTERNAL_POST:  //帖子
                goToPost(param);
                break;
            case Constant.EXTERNAL_CIRCLE:  //问答
                goToPost(param);
                break;
            case Constant.EXTERNAL_ARTICLE:  //文章
                goToHomePage(param, HomepageConst.FEED_TYPE_ARTICLE);
                break;
            case Constant.EXTERNAL_GOOD:  //帖子评论
                goToComment(param, CommunityConstant.COMMENTS_TYPE_POST);
                break;
            case Constant.EXTERNAL_COMMODITY_PROJECT:  //问答评论
                goToComment(param, CommunityConstant.COMMENTS_TYPE_POST);
                break;
            case Constant.EXTERNAL_LINKS:  //文章评论
                goToComment(param, CommunityConstant.COMMENTS_TYPE_ARTICLE);
                break;
            case Constant.EXTERNAL_POSTURE:  //测试文章
                goToHomePage(param, HomepageConst.FEED_TYPE_TEST);
                break;
            case Constant.EXTERNAL_TEST:  //导购文章
                goToHomePage(param, HomepageConst.FEED_TYPE_PRODUCT);
                break;
        }
    }

    private void goToComment(ExternalLinksHelperParam param, int type) {
        if (param.getMsgObj() == null) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(param.getId());
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void goToArticleComment(ExternalLinksHelperParam param) {
        CommentListInfo info = new CommentListInfo();
        info.commentType = CommunityConstant.COMMENTS_TYPE_ARTICLE;
        info.postId = StringUtils.parseInt(param.getId());
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_COMMENT_LIST_WINDOW;
        msg.obj = info;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    /**
     * 帖子
     *
     * @param param
     */
    private void goToPost(ExternalLinksHelperParam param) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(param.getId());
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 问答圈子
     *
     * @param param
     */
    private void goToCircle(ExternalLinksHelperParam param, int type) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(param.getId());
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 文章、测试、导购
     *
     * @param param
     */
    private void goToHomePage(ExternalLinksHelperParam param, int type) {
        HomepageFeedDetailInfo detailInfo = new HomepageFeedDetailInfo();
        detailInfo.articleId = param.getId();
        detailInfo.type = type;
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
        msg.obj = detailInfo;
        MsgDispatcher.getInstance().sendMessage(msg);
    }
}
