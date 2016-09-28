package com.sjy.ttclub.util;

import android.os.Message;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.ExternalLinksHelperParam;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.knowledge.KnowledgeDetailInfo;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;
import com.sjy.ttclub.web.WebViewBrowserParams;

/**
 * Created by gangqing on 2015/12/7.
 * Email:denggangqing@ta2she.com
 */
public class LetterExternalLinksHelper {
    public void goExternalLinks(ExternalLinksHelperParam param) {
        //(1帖子;2圈子;3文章;4商品;5商品专题;6外部链接;7涨姿势;8测试;9导购;10图片;)
        switch (param.getType()) {
            case Constant.EXTERNAL_POST:  //帖子
                goToPost(param);
                break;
            case Constant.EXTERNAL_CIRCLE:  //圈子
                goToCircle(param, CommunityConstant.CIRCLE_TYPE_POST);
                break;
            case Constant.EXTERNAL_ARTICLE:  //文章
                goToHomePage(param, HomepageConst.FEED_TYPE_ARTICLE);
                break;
            case Constant.EXTERNAL_GOOD:  //商品
                goToGood(param);
                break;
            case Constant.EXTERNAL_COMMODITY_PROJECT:  //商品专题
                goToCommodityProject(param);
                break;
            case Constant.EXTERNAL_LINKS:  //外部链接
                goToExternalLinks(param);
                break;
            case Constant.EXTERNAL_POSTURE:  //涨姿势
                goToPosture(param);
                break;
            case Constant.EXTERNAL_TEST:  //测试
                goToHomePage(param, HomepageConst.FEED_TYPE_TEST);
                break;
            case Constant.EXTERNAL_SHOPPING_GUIDE:  //导购
                goToHomePage(param, HomepageConst.FEED_TYPE_PRODUCT);
                break;
            case Constant.EXTERNAL_IMAGE:  //图片
                goToHomePage(param, HomepageConst.FEED_TYPE_PIC);
                break;
            case Constant.EXTERNAL_QUESTION_ANSWER:   //圈子-问答详情
                goToCircle(param, CommunityConstant.CIRCLE_TYPE_QA_POST);
                break;
            case Constant.EXTERNAL_ORDER:   //订单详情
                goToOrder(param);
                break;
        }
    }

    /**
     * 1,帖子
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
     * 2,圈子,问答圈子
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
     * 3,文章、测试、导购、图片
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

    /**
     * 4,商品
     *
     * @param param
     */
    private void goToGood(ExternalLinksHelperParam param) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        message.obj = param.getId();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 5,商品专题
     *
     * @param param
     */
    private void goToCommodityProject(ExternalLinksHelperParam param) {
        ShoppingTopicInfo topicInfo = new ShoppingTopicInfo();
        topicInfo.columnId = param.getId();
        topicInfo.title = ResourceHelper.getString(R.string.account_good_subject_title);
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW;
        message.obj = topicInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 6,外部链接
     *
     * @param param
     */
    private void goToExternalLinks(ExternalLinksHelperParam param) {
        Message msg = Message.obtain();
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = "";
        params.url = param.getId();
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    /**
     * 7,涨姿势
     *
     * @param param
     */
    private void goToPosture(ExternalLinksHelperParam param) {
//      HomepageConst.FEED_TYPE_RAISE_POSTTURE
        KnowledgeDetailInfo detailInfo = new KnowledgeDetailInfo();
        detailInfo.articleId = StringUtils.parseInt(param.getId());
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
        msg.obj = detailInfo;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    /**
     * 12,订单详情
     *
     * @param param
     */
    private void goToOrder(ExternalLinksHelperParam param) {
        OrderResultInfo orderResultInfo = new OrderResultInfo();
        orderResultInfo.orderId = param.getId();
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_ORDER_DETAIL_WINDOW;
        message.obj = orderResultInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
