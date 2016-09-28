/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.network;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.system.SystemHelper;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class HttpUrls {

    //主接口配置
    public final static String SERVER_URL = BuildConfig.SERVER_URL;
    //审核通过接口
    public static final String AUDITED_URL = SERVER_URL + "/android_audit_new.html";
    //闪屏接口
    public final static String URL_INDEX = SERVER_URL + "/index.php";
    //商城首页接口
    public final static String URL_SHOP = SERVER_URL + "/mall.php";
    //个人资料接口
    public static final String USER_URL = SERVER_URL + "/user.php";
    //消息资料接口
    public static final String LETTER_URL = SERVER_URL + "/letter.php";
    //首页接口
    public static final String HOMEPAGE_URL = SERVER_URL + "/home.php";
    //社区接口
    public static final String COMMUNITY_URL = SERVER_URL + "/bbs.php";
    //帮助中心接口
    public static final String HELP_URL = SERVER_URL + "/h5.php?a=help";
    //关于她他社
    public static final String ABOUT_URL = SERVER_URL + "/h5.php?a=aboutUs&version=";
    //关于她他社
    public static final String LOGISTICS_URL = SERVER_URL + "/h5.php?a=kuaidi2&logisticsId=";
    //等级说明接口
    public static final String LEVEL_EXPLAIN = SERVER_URL + "/h5.php?a=howToPlay";
    //应用评分接口
    public static final String APPLICATION_GRADE = "market://details?id=" + SystemHelper.getAppInfo().packageName;
    //设置接口
    public static final String USERCONF_URL = SERVER_URL + "/userconf.php";

    public static final String H5_URL = BuildConfig.SERVER_URL + "/h5.php";
    //分享帖子链接
    public static final String SHARE_CARD_URL = BuildConfig.SERVER_URL + "/h5.php?a=share&apiver=" + CommonConst.API_VERSION + "&post_id=";
    //圈子分享
    public static final String SHARE_CIRCLE_URL = BuildConfig.SERVER_URL + "/h5.php?a=circleShare&apiver=" + CommonConst.API_VERSION + "&id=";
    //分享文章
    public static final String SHARE_ARTICLE_URL = BuildConfig.SERVER_URL + "/h5.php?a=articleShare&apiver=" + CommonConst.API_VERSION + "&id=";
    //商品详情webview连接
    public static final String PRODUCT_DETAIL = BuildConfig.SERVER_URL + "/h5.php?a=goodsDetail&id=";
    //圈子介绍
    public static final String CIRCLE_DETAIL_URL = BuildConfig.SERVER_URL + "/h5.php?a=circleDetail&id=";
    //微信支付
    public static final String WECHAT_PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //社区圈子统计接口
    public static final String COMMUNITY_CIRCLE_STATS_URL = "http://s.api.ta2she.com/t.html";
}
