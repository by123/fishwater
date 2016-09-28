/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.homepage;

import android.os.Message;

import com.sjy.ttclub.bean.homepage.ArticleInfo;
import com.sjy.ttclub.bean.homepage.FeedInfo;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailWindow;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedListWindow;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.StringUtils;

import java.util.HashMap;

/**
 * Created by linhz on 2015/11/30.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageController extends DefaultWindowController implements HomepageFeedDetailWindow
        .IFeedDetailCallback, HomepageFeedListWindow.IFeedListWindowCallback, HomepageMainView
        .HomepageMainViewCallback {

    private HomepageMainTab mHomepageTab;

    public HomepageController() {
        registerMessage(MsgDef.MSG_GET_HOMEPAGE_TAB);
        registerMessage(MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL);
        registerMessage(MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_LIST);
    }

    private void ensureHomepageTab() {
        if (mHomepageTab == null) {
            mHomepageTab = new HomepageMainTab(mContext, this);
            mHomepageTab.setMainViewCallback(this);
        }
    }

    @Override
    public Object handleMessageSync(Message msg) {
        if (msg.what == MsgDef.MSG_GET_HOMEPAGE_TAB) {
            ensureHomepageTab();
            return mHomepageTab;
        }
        return super.handleMessageSync(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL) {
            if (msg.obj instanceof HomepageFeedDetailInfo) {
                showArticleDetailWindow((HomepageFeedDetailInfo) msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_LIST) {
            if (msg.obj instanceof HomepageFeedDetailInfo) {
                showArticleListWindow((HomepageFeedDetailInfo) msg.obj);
            }
        }
    }

    private void showArticleDetailWindow(HomepageFeedDetailInfo detailInfo) {
        AbstractWindow window = getCurrentWindow();
        HomepageFeedDetailWindow detailWindow;
        if (window instanceof HomepageFeedDetailWindow) {
            detailWindow = (HomepageFeedDetailWindow) window;
        } else {
            detailWindow = new HomepageFeedDetailWindow(mContext, this);
        }
        detailWindow.setFeedDetailCallback(this);
        detailWindow.setupDetailWindow(detailInfo);
        mWindowMgr.pushWindow(detailWindow);
    }

    private void showArticleListWindow(HomepageFeedDetailInfo detailInfo) {
        AbstractWindow window = getCurrentWindow();
        HomepageFeedListWindow listWindow;
        if (window instanceof HomepageFeedListWindow) {
            listWindow = (HomepageFeedListWindow) window;
        } else {
            listWindow = new HomepageFeedListWindow(mContext, this);
        }
        listWindow.setFeedListCallback(this);
        listWindow.setupFeedListWindow(detailInfo);
        mWindowMgr.pushWindow(listWindow);
    }


    @Override
    public void onFeedListItemClick(ArticleInfo info) {
        HomepageFeedDetailInfo detailInfo = new HomepageFeedDetailInfo();
        detailInfo.type = info.getType();
        detailInfo.childType = info.getChildType();
        detailInfo.typeName = info.getTypeName();
        detailInfo.articleId = info.getId();
        detailInfo.sourceType = HomepageConst.SOURCE_TYPE_NORMAL;

        showArticleDetailWindow(detailInfo);
    }

    @Override
    public void onCommentsClick() {

    }

    @Override
    public void onEntranceClick(HomepageTopLayout.EntranceInfo info) {
        if (info.id == HomepageTopLayout.ID_TEST) {
            HomepageFeedDetailInfo detailInfo = new HomepageFeedDetailInfo();
            detailInfo.type = HomepageConst.FEED_TYPE_TEST;
            showArticleListWindow(detailInfo);
            StatsModel.stats(StatsKeyDef.HOMEPAGE_TEST);
        } else if (info.id == HomepageTopLayout.ID_KNOWLEDGE) {
            mDispatcher.sendMessage(MsgDef.MSG_SHOW_KNOWLEDGE_MAIN_WINDOW);
            StatsModel.stats(StatsKeyDef.HOMEPAGE_GESTURE);
        } else if (info.id == HomepageTopLayout.ID_QA) {
            if (info.data instanceof Integer) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
                message.arg1 = (Integer) info.data;
                message.arg2 = CommunityConstant.CIRCLE_TYPE_QA_POST;
                mDispatcher.sendMessage(message);
            }
            StatsModel.stats(StatsKeyDef.HOMEPAGE_QUESTION);
        } else if (info.id == HomepageTopLayout.ID_RECODE) {
            mDispatcher.sendMessage(MsgDef.MSG_SHOW_RECORD_WINDOW);
            StatsModel.stats(StatsKeyDef.HOMEPAGE_RECORD);
        }
    }


    @Override
    public void onFeedClick(FeedInfo info, int turnType) {
        statsArticle(info, turnType);
        if (turnType == HomepageConst.TURN_TYPE_DETAILE) {
            int type = StringUtils.parseInt(info.getAttrType());
            if (type == HomepageConst.FEED_TYPE_POST) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
                message.arg1 = StringUtils.parseInt(info.getAttrValue());
                MsgDispatcher.getInstance().sendMessage(message);
            } else if (type == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
                message.obj = StringUtils.parseInt(info.getAttrValue());
                MsgDispatcher.getInstance().sendMessage(message);
            } else if (type == HomepageConst.FEED_TYPE_ASK_ANSWER) {
                int circleType = CommunityConstant.CIRCLE_TYPE_QA_POST;
                showCircleDetail(circleType, info.getAttrValue());
            } else if (type == HomepageConst.FEED_TYPE_CIRCLE) {
                int circleType = CommunityConstant.CIRCLE_TYPE_POST;
                showCircleDetail(circleType, info.getAttrValue());
            } else if (type == HomepageConst.FEED_TYPE_TOPIC) {
                ShoppingTopicInfo topicInfo = new ShoppingTopicInfo();
                topicInfo.title = info.getTitle();
                topicInfo.columnId = info.getAttrValue();
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW;
                message.obj = topicInfo;
                MsgDispatcher.getInstance().sendMessage(message);
            } else {
                HomepageFeedDetailInfo detailInfo = new HomepageFeedDetailInfo();
                detailInfo.type = StringUtils.parseInt(info.getAttrType());
                detailInfo.articleId = info.getAttrValue();
                detailInfo.sourceType = HomepageConst.SOURCE_TYPE_NORMAL;
                showArticleDetailWindow(detailInfo);
            }
        } else if (turnType == HomepageConst.TURN_TYPE_PERSONAL) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
            message.arg1 = StringUtils.parseInt(info.getUid());
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }


    private void showCircleDetail(int type, String id) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(id);
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void statsArticle(FeedInfo info, int turnType) {
        int type = StringUtils.parseInt(info.getAttrType());
        String title = info.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = info.getFid();
        }
        String typeValue = "article";
        if (type == HomepageConst.FEED_TYPE_TEST) {
            typeValue = "test";
        } else if (type == HomepageConst.FEED_TYPE_PRODUCT) {
            typeValue = "product";
        } else if (type == HomepageConst.FEED_TYPE_ARTICLE) {
            typeValue = "word";
        } else if (type == HomepageConst.FEED_TYPE_PIC) {
            typeValue = "picture";
        } else if (type == HomepageConst.FEED_TYPE_POST) {
            typeValue = "post";
        } else if (type == HomepageConst.FEED_TYPE_CIRCLE ||
                type == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            typeValue = "group";
        } else if (type == HomepageConst.FEED_TYPE_TOPIC) {
            typeValue = "special";
        }
        HashMap<String, String> map = new HashMap<>(2);
        map.put("type", typeValue);
        map.put("spec", title);
        if (turnType == HomepageConst.TURN_TYPE_DETAILE) {
            StatsModel.stats(StatsKeyDef.HOMEPAGE_ARTICLE, map);
        } else {
            StatsModel.stats(StatsKeyDef.HOMEPAGE_PROFILE, map);
        }
    }
}
