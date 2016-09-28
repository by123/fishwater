package com.sjy.ttclub.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.sjy.ttclub.InitEventInfo;
import com.sjy.ttclub.TTClubActivity;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.knowledge.KnowledgeDetailInfo;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.umeng.UmengPushManager;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.web.WebViewBrowserParams;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by linhz on 2015/12/12.
 * Email: linhaizhong@ta2she.com
 */
public class PushManager implements IPushEventListener {
    private Context mContext;
    private static PushManager sInstance;

    private IPushManager mPushImpl;

    private PushManager(Context context) {
        mContext = context;
        mPushImpl = factoryPushManger(context);
        mPushImpl.setPushEventListener(this);
        mPushImpl.initPush();
    }

    public static void init(Context context) {
        sInstance = new PushManager(context);
    }

    public static PushManager getInstance() {
        return sInstance;
    }

    @Override
    public void onPushCustomMessageEvent(Map<String, String> map) {
        startPushActivity(map);
    }

    @Override
    public void onPushNotificationEvent(Map<String, String> map) {
        startPushActivity(map);
    }

    @Override
    public void onPushRegistered(String deviceToken) {
        SystemHelper.getDeviceInfo().devToken = deviceToken;
        boolean enableRecord = isEnableRecordPush();
        if (enableRecord) {
            addRecordTag();
        } else {
            removeRecordTag();
        }
    }

    private void startPushActivity(Map<String, String> map) {
        Intent intent = new Intent();
        intent.setClass(mContext, TTClubActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            bundle.putString(entry.getKey(), entry.getValue());
        }
        bundle.putBoolean(InitEventInfo.KEY_PUSH, true);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public void addTag(String tag) {
        mPushImpl.addTag(tag);
    }

    public void removeTag(String tag) {
        mPushImpl.removeTag(tag);
    }

    public void addRecordTag() {
        addTag(PushConst.TAG_RECORD);
    }

    public void setEnableRecordPush(boolean enable) {
        String key = PushConst.FLAG_RECORD;
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (info != null) {
            key = key +"_"+ info.getUserId();
        }
        SettingFlags.setFlag(key, enable);
    }

    public boolean isEnableRecordPush() {
        String key = PushConst.FLAG_RECORD;
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (info != null) {
            key = key +"_" +info.getUserId();
        }
        return SettingFlags.getBooleanFlag(key, true);
    }


    public void removeRecordTag() {
        removeTag(PushConst.TAG_RECORD);
    }

    public static void handlePushEvent(Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return;
        }
        String type = bundle.getString(PushConst.KEY_TYPE);
        if (StringUtils.isEmpty(type)) {
            return;
        }
        String typeValue = bundle.getString(PushConst.KEY_TYPE_VALUE);
        String title = bundle.getString(PushConst.KEY_TITLE);

        if (PushConst.TYPE_ARTICLE.equals(type)) {
            int feedType = HomepageConst.FEED_TYPE_ARTICLE;
            showFeedDetail(feedType, typeValue);
        } else if (PushConst.TYPE_ARTICLE_PRODUCT.equals(type)) {
            int feedType = HomepageConst.FEED_TYPE_PRODUCT;
            showFeedDetail(feedType, typeValue);
        } else if (PushConst.TYPE_ARTICLE_PICTURE.equals(type)) {
            int feedType = HomepageConst.FEED_TYPE_PIC;
            showFeedDetail(feedType, typeValue);
        } else if (PushConst.TYPE_ARTICLE_TEST.equals(type)) {
            int feedType = HomepageConst.FEED_TYPE_TEST;
            showFeedDetail(feedType, typeValue);
        } else if (PushConst.TYPE_ARTICLE_GESTURE.equals(type)) {
            showKnowledgeDetail(typeValue);
        } else if (PushConst.TYPE_PRUDOCT_DETAILE.equals(type)) {
            showProductDetail(typeValue);
        } else if (PushConst.TYPE_PRUDOCT_TOPIC.equals(type)) {
            showProductTopic(typeValue);
        } else if (PushConst.TYPE_PRUDOCT_CATEGORY.equals(type)) {
            showProductCategory(title, typeValue);
        } else if (PushConst.TYPE_GROUP_DETAIL.equals(type)) {
            showCircleDetail(CommunityConstant.CIRCLE_TYPE_POST, typeValue);
        } else if (PushConst.TYPE_GROUP_QA_DETAIL.equals(type)) {
            showCircleDetail(CommunityConstant.CIRCLE_TYPE_QA_POST, typeValue);
        } else if (PushConst.TYPE_POST_DETAILE.equals(type)) {
            showPostDetail(typeValue);
        } else if (PushConst.TYPE_WEB.equals(type)) {
            showWebDetail(title, typeValue);
        } else if (PushConst.TYPE_RECORD.equals(type)) {
            showRecordWindow();
        } else if (PushConst.TYPE_REPLY_ME.equals(type)) {
            showReplyMeWindow();
        } else if (PushConst.TYPE_PRIVATE_MSG.equals(type)) {
            showPrivateMsgWindow();
        }

    }

    private static void showRecordWindow() {
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_RECORD_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showReplyMeWindow() {
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_REPLY_ME_WINDOW;
        msg.arg1 = Constant.TYPE_REPLY_ME;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showPrivateMsgWindow() {
        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_MESSAGE_WINDOW);
    }

    private static void showWebDetail(String title, String url) {
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = title;
        params.url = url;
        Message msg = Message.obtain();
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showFeedDetail(int feedType, String articleId) {
        HomepageFeedDetailInfo info = new HomepageFeedDetailInfo();
        info.type = feedType;
        info.articleId = articleId;
        Message msg = Message.obtain();
        msg.obj = info;
        msg.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showKnowledgeDetail(String articleId) {
        KnowledgeDetailInfo info = new KnowledgeDetailInfo();
        info.articleId = StringUtils.parseInt(articleId);
        Message msg = Message.obtain();
        msg.obj = info;
        msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showProductCategory(String title, String goodsId) {
        //todo
    }

    private static void showProductDetail(String goodsId) {
        Message msg = Message.obtain();
        msg.obj = goodsId;
        msg.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private static void showProductTopic(String topicId) {
        ShoppingTopicInfo topicInfo = new ShoppingTopicInfo();
        topicInfo.columnId = topicId;
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW;
        message.obj = topicInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private static void showCircleDetail(int type, String groupId) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(groupId);
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private static void showPostDetail(String postId) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(postId);
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private IPushManager factoryPushManger(Context context) {
        return new UmengPushManager(context);
    }


}
