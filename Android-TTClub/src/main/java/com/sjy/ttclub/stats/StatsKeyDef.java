/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.stats;

/**
 * Created by zhxu on 2015/10/29.
 * Email: 357599859@ta2she.com
 * 注意：每个event对应的key不能超过10个！！如果超过了，请使用新的event id
 */
public class StatsKeyDef {

    public static final String VALUE_MAN = "man";
    public static final String VALUE_WOMAN = "woman";
    //分享统计
    public static final String SHARE = "share_button";
    //分享统计
    public static final String SHARE_QQ = "qq_share";
    //分享统计
    public static final String SHARE_QZONE = "qqzone_share";
    //分享统计
    public static final String SHARE_WECHAT_FRIENDS = "wechat_share";
    //分享统计
    public static final String SHARE_WECHAT_TIMELINE = "wechat_zone_share";
    //
    public static final String SHARE_WEIBO = "weibo_share";

    //登录引导统计
    public static final String LOGIN_INDUCTION_1 = "login_induction_1";
    public static final String LOGIN_INDUCTION_2 = "login_induction_2";
    public static final String LOGIN_INDUCTION_3 = "login_induction_3";
    public static final String LOGIN_INDUCTION_REGISTER = "login_induction_register";
    public static final String LOGIN_INDUCTION_LOGIN = "login_induction_login";
    //展示新手引导
    public static final String GUIDE_VIEW = "guide_view";
    public static final String GUIDE_FINISH = "guide_finish";
    public static final String GUIDE_MERRIED_KEY = "marriage";
    public static final String GUIDE_MERRIED_VALUE_SINGLE = "single";
    public static final String GUIDE_MERRIED_VALUE_MARRIED = "married";
    public static final String GUIDE_MERRIED_VALUE_INVOLVED = "in_loved";

    //首页 页面展示
    public static final String HOMEPAGE_VIEW = "index_view";
    //首页 记录
    public static final String HOMEPAGE_RECORD = "index_record";
    //点击涨姿势
    public static final String HOMEPAGE_GESTURE = "index_gesture";
    //点击TA问TA答
    public static final String HOMEPAGE_QUESTION = "index_question";
    //点击测一测
    public static final String HOMEPAGE_TEST = "index_test";
    //首页banner
    public static final String HOMEPAGE_BANNER = "index_banner";
    //点击任一信息流文章
    public static final String HOMEPAGE_ARTICLE = "index_article";
    //首页点击个人信息
    public static final String HOMEPAGE_PROFILE = "index_profile";
    //首页点赞
    public static final String HOMEPAGE_LIKE = "index_like";
    //首页加载更多
    public static final String HOMEPAGE_LOADMORE = "index_content_more";
    //下拉刷新
    public static final String HOMEPAGE_REFRESH = "index_content_upate";
    //信息流文章（包括图片、测试、导购、图文）总展示
    public static final String CONTENT_VIEW = "content_view";
    public static final String CONTENT_BACK = "content_back";
    public static final String CONTENT_COMMENT = "content_comment";
    public static final String CONTENT_PRAISE = "content_praise";
    public static final String CONTENT_BUY = "content_buy";
    public static final String CONTENT_SHARE = "content_share";
    public static final String CONTENT_COLLECT = "content_collection";

    public static final String CONTENT_TEST_PREVIOUS = "content_test_last_subject";
    public static final String CONTENT_TEST_AGAIN = "content_test_again";
    public static final String CONTENT_TEST_VIEW = "test_view";
    public static final String CONTENT_TEST_FINISH = "test_finish";

    public static final String RECORD_VIEW = "record_view";
    public static final String RECORD_VIEW_LEAVE = "record_view_leave";
    public static final String RECORD_PAPA = "record_papa";
    public static final String RECORD_PAPA_SUBMIT = "record_papa_submit";
    public static final String RECORD_ZIHAI = "record_zihai";
    public static final String RECORD_ZIHAI_SUBMIT = "record_zihai_submit";
    public static final String RECORD_BUZUO = "record_buzuo";
    public static final String RECORD_BUZUO_SUBMIT = "record_buzuo_submit";

    public static final String WELFARE_VIEW = "welfare_view";
    public static final String WELFARE_SHOPPING_CART = "welfare_shopping_cart";
    public static final String WELFARE_CLASSIFY = "welfare_classify";
    public static final String WELFARE_PANIC_BUYING_LINE = "welfare_panic_buying_line";
    public static final String WELFARE_PANIC_BUYING_BUTTON = "welfare_panic_buying_button";

    public static final String GOODS_DETAIL_VIEW = "goods_detail_view";
    public static final String GOODS_DETAIL_ADD_CART = "goods_detail_add_cart";
    public static final String GOODS_DETAIL_BUY_NOW = "goods_detail_buy_now";
    public static final String GOODS_DETAIL_COLLECT = "goods_detail_collect";
    //社区/*new*/
    public static final String GENDER_KEY = "gender";
    public static final String SPEC_KEY = "spec";
    public static final String COMMUNITY_TAB = "community_tab"; //已测
    public static final String CONMMUNITY_DISCOVER_CLICK = "conmmunity_discover_click";//已测
    public static final String CONMMUNITY_BANNER = "conmmunity_banner"; //已测
    public static final String CONMMUNITY_GROUP_REC = "conmmunity_group_rec";   //已测
    public static final String CONMMUNITY_GROUP_MORE = "conmmunity_group_more"; //已测
    public static final String CONMMUNITY_QUESITON_REC = "conmmunity_quesiton_rec"; //已测
    public static final String CONMMUNITY_QUESITON_MORE = "conmmunity_quesiton_more";   //已测
    /*热门*/
    public static final String COMMNITY_HOT_TAB = "commnity_hot_tab";   //已测
    public static final String COMMNITY_HOT_PULL = "commnity_hot_pull"; //已测
    public static final String COMMNITY_HOT_VIEW = "commnity_hot_view"; //已测
    public static final String COMMNITY_HOT_POST = "commnity_hot_post"; //已测
    /*圈子*/
    public static final String GROUP_VIEW = "group_view";   //已测
    public static final String GROUP_POST = "group_post";   //已测
    public static final String GROUP_WRITE_POST = "group_write_post";   //已测
    public static final String GROUP_DELIEVER_POST = "group_deliever_post"; //已测
    public static final String GROUPLIST_CLICK = "grouplist_click"; //已测
    public static final String QUESTION_VIEW = "question_view"; //已测
    public static final String GROUP_INTRODUCTION = "group_introduction";   //已测
    public static final String GROUP_TAB_ESSENCE = "group_tab_essence"; //已测
    public static final String GROUP_TAB_NEW = "group_tab_new"; //已测
    public static final String GROUP_POST_NEW = "group_post_new";   //已测
    public static final String GROUP_POST_HOT = "group_post_hot";   //已测
    public static final String GROUP_POST_ESSENCE = "group_post_essence";   //已测
    /*帖子*/
    public static final String POST_VIEW = "post_view"; //已测
    public static final String WRITE_REPLY = "write_reply"; //已测
    //社区 /*old*/
    public static final String COMMUNITY_DISCOVERY_CARE = "community_discovery_care";
    public static final String COMMUNITY_DISCOVERY_LEFT_SLIP = "community_discovery_left_slip";
    public static final String COMMUNITY_DISCOVERY_LOAD = "community_discovery_load";
    public static final String COMMUNITY_DISCOVERY_CIRCLE = "community_discovery_circle";
    public static final String COMMUNITY_DISCOVERY_CIRCLE_ALL = "community_discovery_circle_all";
    public static final String COMMUNITY_DISCOVERY_QA_MORE = "community_ask_answer_more";
    public static final String COMMUNITY_DISCOVERY_QA_RECOMMEND = "community_recommend_answer";
    public static final String COMMUNITY_SHARE_CHANNEL = "community_share_channel";
    public static final String COMMUNITY_CARE_VIEW = "community_care_view";
    public static final String COMMUNITY_CARE_DISCOVERY = "community_care_discovery";   //已测
    public static final String COMMUNITY_CARE_USER = "community_care_user"; //已测
    public static final String COMMUNITY_CARE_ADD = "community_care_add";
    public static final String COMMUNITY_CARE_PRAISE = "community_care_praise"; //已测
    public static final String COMMUNITY_CARE_CIRCLE_NAME = "community_care_circle_name";
    public static final String COMMUNITY_CARE_REFRESH = "community_care_refresh";
    public static final String COMMUNITY_CARE_LOAD = "community_care_load";
    public static final String COMMUNITY_CARE_LEFT_SLIP = "community_care_left_slip";
    public static final String POST_LIST_VIEW = "post_list_view";   //已测
    public static final String POST_LIST_USER = "post_list_user";   //已测
    public static final String POST_LIST_PRAISE = "post_list_praise";   //已测
    public static final String POST_LIST_REFRESH = "post_list_refresh"; //已测
    public static final String POST_LIST_LOAD = "post_list_load";   //已测
    public static final String POST_SEND_VIEW = "post_send_view";   //已测
    public static final String POST_SEND_CANCEL = "post_send_cancel";   //已测
    public static final String POST_SEND_RELEASE = "post_send_release"; //已测
    public static final String POST_SEND_TITLE = "post_send_title"; //已测
    public static final String POST_SEND_NOTE = "post_send_note";   //已测
    public static final String POST_SEND_IMAGE = "post_send_image"; //已测
    public static final String POST_USER_MASTER = "post_user_master";   //已测
    public static final String POST_CARE = "post_care";
    public static final String POST_CIRCLE_NAME = "post_circle_name";
    public static final String POST_PRAISE_MASTER = "post_praise_master";   //已测
    public static final String POST_USER_PRAISE = "post_user_praise";   //已测
    public static final String POST_COMMENT_MASTER = "post_comment_master"; //已测
    public static final String POST_COMMENT_HOT = "post_comment_hot";
    public static final String POST_COMMENT_DESC = "post_comment_desc"; //已测
    public static final String POST_USER_REPLY = "post_user_reply"; //已测
    public static final String POST_LOAD = "post_load"; //已测
    public static final String POST_REPLY = "post_reply";   //已测

    //个人资料  页面总展示
    public static final String PERSONAL_PROFILE_VIEW = "personal_profile_view";
    //个人资料  关注
    public static final String PERSONAL_PROFILE_CARE = "personal_profile_care";
    //个人资料  取消关注
    public static final String PERSONAL_PROFILE_CARE_CANCEL = "personal_profile_care_cancel";
    //个人主页发私信
    public static final String PERSONAL_PROFILE_MESSAGE = "personal_profile_message";
    //个人主页关注
    public static final String PERSONAL_PROFILE_FOLLOW = "personal_profile_follow";

    //我 页面总展示
    public static final String MYSELF_VIEW = "myself_view";
    //我 收藏
    public static final String MYSELF_COLLECT = "myself_collect";
    //我 头像
    public static final String MYSELF_USER_AVATAR = "myself_user_avatar";
    //我 设置
    public static final String MYSELF_SETTING = "myself_setting";
    //我 全部订单
    public static final String MYSELF_ORDERS = "myself_orders";
    //我 我的发言
    public static final String MYSELF_POST = "myself_post";
    //我 回复我的
    public static final String MYSELF_REPLY = "myself_reply";
    //我 隐私保护
    public static final String MYSELF_PRIVACY = "myself_privacy";
    //点击消息
    public static final String ME_MESSAGE = "me_message";
    //点击粉丝
    public static final String ME_FANS = "me_fans";
    //点击关注
    public static final String ME_FOLLOW = "me_follow";
    //点击情趣指数
    public static final String ME_SEXY_INDEX = "me_sexy_index";
    //展示情趣面板
    public static final String SEXY_INDEX_VIEW = "sexy_index_view";

    //记录-我的记录
    public static final String RECORD_MINE = "record_mine";
    //记录-偷窥
    public static final String RECORD_SNOOP = "record_snoop";
    //展示我的数据页
    public static final String MY_RECORD_VIEW = "my_record_view";
    //切换tab
    public static final String MY_RECORD_SWITCH_TAB = "my_record_switch_tab";
    //展示偷窥页
    public static final String SNOOP_VIEW = "snoop_view";
    //偷窥-切换tab
    public static final String SNOOP_SWITCH_TAB = "snoop_switch_tab";
    //偷窥-切换筛选条件
    public static final String SNOOP_SWITCH_FILTER = "snoop_switch_filter";

    //圈子列表页 页面总展示
    public static final String CIRCLE_LIST_VIEW = "circle_list_view";
    //圈子列表页 每个圈子分类 + index
    public static final String CIRCLE_CLASSIFY = "circle_classify";


    //社区2.6统计
    public static final String COMMUNITY_DISCOVERY_VIEW = "community_discovery_view";
    public static final String CIRCLE_LIST = "circle_list";


    //涨姿势
    public static final String KNOWLEDGE_VIEW = "knowledge_view";
    public static final String KNOWLEDGE_SEX_MAN = "knowledge_category_male";
    public static final String KNOWLEDGE_SEX_WOMAN = "knowledge_category_female";
    public static final String KNOWLEDGE_SEX_MAN_WOMAN = "knowledge_category_couple";
    public static final String KNOWLEDGE_MAN_DETAIL = "knowledge_category_male_list";
    public static final String KNOWLEDGE_WOMAN_DETAIL = "knowledge_category_female_list";
    public static final String KNOWLEDGE_MAN_AND_WOMAN_DETAIL = "knowledge_category_couple_list";
    public static final String KNOWLEDGE_PRAISE = "knowledge_prasie";
    public static final String KNOWLEDGE_COLLECT = "knowledge_collection";
    //涨姿势详情页展示
    public static final String KNOWLEDGE_DETAIL = "pose_view";

    //福利社
    public static final String WELFARE_TAB = "welfare_tab";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_SELL = "product_sell";
    public static final String PRODUCT_COLLECTION = "product_collection";
    public static final String PRODUCT_DETAIL_VIEW = "product_detail_view";
    public static final String PRODUCT_CART = "product_cart";
    public static final String PRODUCT_PURCHASE = "product_purchase";
    public static final String PRODUCT_PAYMENT = "product_payment";

    public static final String PRODUCT_ORDER_CONFIRM = "product_order_confirm";
    public static final String PRODUCT_ORDER_UNPAY = "product_order_unpay";
    public static final String PRODUC_ORDER_CANCEL = "produc_order_cancel";
    public static final String PRODUCT_ORDER_DELIEVERED = "product_order_delievered";
    public static final String PRODUCT_ORDER_RECEIVE = "product_order_receive";
    public static final String PRODUCT_ORDER_UNREVIEWED = "product_order_unreviewed";
    public static final String PRODUCT_ORDER_REVIEW = "product_order_review";
    public static final String PRODUCT_REVIEWLIST_VIEW = "product_reviewlist_view";
    public static final String PRODUCT_REVIEWLIST_REVIEW = "product_reviewlist_review";
    public static final String PRODUCT_REVIEW_POST = "product_review_post";

    //登录页
    public static final String PAGE_LOGIN_VIEW = "page_login_view";
    public static final String PAGE_LOGIN_CELLPHONE = "page_login_cellphone";
    public static final String PAGE_LOGIN_WECHAT = "page_login_wechat";
    public static final String PAGE_LOGIN_WEIBO = "page_login_weibo";
    public static final String PAGE_LOGIN_QQ = "page_login_qq";

    //注册页
    public static final String PAGE_REGISTER_VIEW = "page_register_view";
    public static final String PAGE_REGISTER_CELLPHONE = "page_register_cellphone";
    public static final String PAGE_REGISTER_WEIBO = "page_register_weibo";
    public static final String PAGE_REGISTER_QQ = "page_register_qq";
    public static final String PAGE_REGISTER_WECHAT = "page_register_wechat";
}
