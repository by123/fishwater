package com.sjy.ttclub.common;

import android.content.Context;
import android.os.Message;

import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.knowledge.KnowledgeDetailInfo;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/12/18.
 * Email: linhaizhong@ta2she.com
 */
public class BannerHelper {

    public static void handleBannerClick(Context context, Banner banner) {
        String value = banner.getAdAttrValue();
        if (StringUtils.isEmpty(value)) {
            return;
        }
        String title = banner.getTitle();

        int type = banner.getAdAttr();
        if (type == CommonConst.BANNER_TYPE_FEED_ARTICLE) {
            int feedType = HomepageConst.FEED_TYPE_ARTICLE;
            showFeedDetail(feedType, banner.getAdAttrValue());
        } else if (type == CommonConst.BANNER_TYPE_FEED_PRODUCT) {
            int feedType = HomepageConst.FEED_TYPE_PRODUCT;
            showFeedDetail(feedType, banner.getAdAttrValue());
        } else if (type == CommonConst.BANNER_TYPE_FEED_TEST) {
            int feedType = HomepageConst.FEED_TYPE_TEST;
            showFeedDetail(feedType, banner.getAdAttrValue());
        } else if (type == CommonConst.BANNER_TYPE_FEED_PIC) {
            int feedType = HomepageConst.FEED_TYPE_PIC;
            showFeedDetail(feedType, banner.getAdAttrValue());
        } else if (type == CommonConst.BANNER_TYPE_FEED_KNOWLEDGE) {
            showKnowledgeDetail(value);
        } else if (type == CommonConst.BANNER_TYPE_POST) {
            showPostDetail(value);
        } else if (type == CommonConst.BANNER_TYPE_WEBVIEW) {
            showWebView(title, value);
        } else if (type == CommonConst.BANNER_TYPE_APP_ANDROID) {
            showApp(title, value);
        } else if (type == CommonConst.BANNER_TYPE_PRODUCT_DETAIL) {
            showProductDetail(value);
        } else if (type == CommonConst.BANNER_TYPE_PRODUCT_TOPIC) {
            showProductTopic(value);
        } else if (type == CommonConst.BANNER_TYPE_QA_POST) {
            int circleType = CommunityConstant.CIRCLE_TYPE_QA_POST;
            showCircleDetail(circleType, value);
        } else if (type == CommonConst.BANNER_TYPE_GENERAL_POST) {
            int circleType = CommunityConstant.CIRCLE_TYPE_POST;
            showCircleDetail(circleType, value);
        }
    }

    private static void showFeedDetail(int feedType, String articleId) {
        HomepageFeedDetailInfo info = new HomepageFeedDetailInfo();
        info.type = feedType;
        info.articleId = articleId;

        Message message = Message.obtain();
        message.obj = info;
        message.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private static void showKnowledgeDetail(String id) {
        KnowledgeDetailInfo info = new KnowledgeDetailInfo();
        info.articleId = StringUtils.parseInt(id);
        Message msg = Message.obtain();
        msg.obj = info;
        msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showPostDetail(String id) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(id);
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private static void showCircleDetail(int type, String id) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(id);
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private static void showWebView(String title, String url) {
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = title;
        params.url = url;
        Message msg = Message.obtain();
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showApp(String title, String url) {
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = title;
        params.url = url;
        Message msg = Message.obtain();
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showProductDetail(String id) {
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        msg.obj = id;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showProductTopic(String id) {
        ShoppingTopicInfo topicInfo = new ShoppingTopicInfo();
        topicInfo.columnId = id;
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW;
        message.obj = topicInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }


}
