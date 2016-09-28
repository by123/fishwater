package com.sjy.ttclub.util;

import android.os.Message;

import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.knowledge.KnowledgeDetailInfo;

/**
 * Created by gangqing on 2016/1/25.
 * Email:denggangqing@ta2she.com
 */
public class UserInfoExternalLinksHelper {
    //跳转类型，测试：1；导购：2；文章：3；涨姿势：4；图片流类型：5；帖子类型：7；ta问ta答：7
    private static final int JUMP_TYPE_TESTING = 1;
    private static final int JUMP_TYPE_SHOPPING = 2;
    private static final int JUMP_TYPE_ATICLE = 3;
    private static final int JUMP_TYPE_KNOW = 4;
    private static final int JUMP_TYPE_PIC = 5;
    private static final int JUMP_TYPE_POST = 6;
    private static final int JUMP_TYPE_QA_POST = 7;

    public void goToExternalLinks(CommunityPostBean postBean) {
        switch (postBean.getJumpType()) {
            case JUMP_TYPE_TESTING:
                tryOpenArticleDetailWindow(HomepageConst.FEED_TYPE_TEST, postBean);
                break;
            case JUMP_TYPE_SHOPPING:
                tryOpenArticleDetailWindow(HomepageConst.FEED_TYPE_PRODUCT, postBean);
                break;
            case JUMP_TYPE_ATICLE:
                tryOpenArticleDetailWindow(HomepageConst.FEED_TYPE_ARTICLE, postBean);
                break;
            case JUMP_TYPE_KNOW:
                tryOpenKnowledgeDetail(postBean);
                break;
            case JUMP_TYPE_PIC:
                tryOpenArticleDetailWindow(HomepageConst.FEED_TYPE_PIC, postBean);
                break;
            case JUMP_TYPE_POST:
                tryOpenPostDetailWindow(postBean);
                break;
            case JUMP_TYPE_QA_POST:
                tryOpenPostDetailWindow(postBean);
                break;
        }
    }

    private static void tryOpenKnowledgeDetail(CommunityPostBean postBean) {
        KnowledgeDetailInfo info = new KnowledgeDetailInfo();
        info.articleId = postBean.getPostId();
        Message msg = Message.obtain();
        msg.obj = info;
        msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void tryOpenArticleDetailWindow(int feedType, CommunityPostBean postBean) {
        HomepageFeedDetailInfo info = new HomepageFeedDetailInfo();
        info.type = feedType;
        info.articleId = String.valueOf(postBean.getPostId());

        Message message = Message.obtain();
        message.obj = info;
        message.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void tryOpenPostDetailWindow(CommunityPostBean postBean) {
        Message messge = Message.obtain();
        messge.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        messge.arg1 = postBean.getPostId();
        MsgDispatcher.getInstance().sendMessage(messge);
    }
}
