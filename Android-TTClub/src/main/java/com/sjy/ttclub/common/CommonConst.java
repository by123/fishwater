package com.sjy.ttclub.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class CommonConst {

    public static final String API_VERSION = "5";
    //SINA app id
    public static final String APPID_SINA = "1402622580";
    public static final String APPSECRECT_SINA = "e7a27f455036506019b8848fc8cf7d94";
    public static final String REDIRECT_URL_SINA = "http://sns.whalecloud.com";
    // qq appid
    public static final String APPID_QQ = "1104666174";
    public static final String APPSECRET_QQ = "v54Cxvn5vJ9VQ9Gk";
    // wechat appid
    public static final String APPSECRET_WECHAT = "2172dd9a89beb52bd22d3c0bbd54e3df";

    //支付参数
    public static String PREFERENCE_NAME = "wxali";
    public static String APPID_WECHAT = "wx683c2dc7c163644a";
    public static String TYPE_APPID_WECHAT = "appid_wechat";
    public static String MCH_ID_WECHAT = "1246188601";
    public static String TYPE_MCH_ID_WECHAT = "mch_id_wechat";
    public static String API_KEY_WECHAT = "18f4f7c11ac70b0e4ea6a9c02bbf1078";
    public static String TYPE_API_KEY_WECHAT = "api_key_wechat";
    public static String SPBILL_CREATE_IP_WECHAT = "127.0.0.1";

    public static String PARTNER_ALIPAY = "2088911792631157";
    public static String TYPE_PARTNER_ALIPAY = "partner_alipay";
    public static String SELLER_ALIPAY = "2088911792631157";
    public static String TYPE_SELLER_ALIPAY = "seller_alipay";

    public static String RSA_PRIVATE_ALIPAY = "";
    public static String TYPE_RSA_PRIVATE_ALIPAY = "rsa_private_alipay";
    public static String RSA_PUBLIC_ALIPAY = "";
    public static String TYPE_RSA_PUBLIC_ALIPAY = "rsa_public_alipay";

    public static String GOODS_TITLE = "她他社订单";
    public static String TYPE_GOODS_TITLE = "goods_title";
    public static String GOODS_DESCRIPTION = "她他社订单";
    public static String TYPE_GOODS_DESCRIPTION = "goods_description";

    public static String URL_ALIPAY = "http://apittclub.61s-corp.com/TTClub/php/pay/zfb/notify_url.php";
    public static String TYPE_URL_ALIPAY = "url_alipay";
    public static String URL_WECHAT = "http://apittclub.61s-corp.com/TTClub/php/pay/wx/notify_url.php";
    public static String TYPE_URL_WECHAT = "url_wechat";

    public static double TOTALPRICE = 0;
    public static String ORDERNUM = "";
    public static String ORDERID = "";

    public static final String ISFIRST_LOGIN = "isFirstLogin";

    public static final String HOMEPAGE_FIRST_SHOW = "homepageIsFirstShow";

    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;
    public static final int DEFAULT_SEX = SEX_MAN;

    public static final int UPDATE_SEX_COMPLETE = 1;
    public static final int UPDATE_SEX_NO_COMPLETE = 0;

    public static final int MARRIAGE_STATE_SINGLE = 1;
    public static final int MARRIAGE_STATE_INLOVE = 2;
    public static final int MARRIAGE_STATE_MARRIAGED = 4;
    public static final int DEFAULT_MARRIAGE_STATE = MARRIAGE_STATE_SINGLE;

    public static final int SEX_SKILL_NONE = 1;
    public static final int SEX_SKILL_LITTLE = 2;
    public static final int SEX_SKILL_NORMAL = 4;
    public static final int SEX_SKILL_EXCELLENT = 8;
    public static final int DEFAULT_SEX_SKILL = SEX_SKILL_NORMAL;

    public static final int START_PAGE_INDEX = 1;
    public static final int START_END_ID = 0;

    public static final int COLLECT_TYPE_POST = 1;
    public static final int COLLECT_TYPE_ARTICLE = 2;
    public static final int COLLECT_TYPE_PRODUCT = 3;

    public static final int COLLECT_ADD = 1;
    public static final int COLLECT_CANCEL = 0;

    public static final int PRAISE_TYPE_POST = 1;//帖子
    public static final int PRAISE_TYPE_ARTICLE = 2;//文章
    public static final int PRAISE_TYPE_POST_COMMENTS = 3;//帖子评论
    public static final int PRAISE_TYPE_ARTICLE_COMMENTS = 4;//文章评论
    public static final int PRAISE_ADD = 1;//添加点赞
    public static final int PRAISE_CANCEL = 0;//取消点赞

    //評論
    public static final int COMMENT_ONLY_HOST = 1;//只看楼主
    public static final int COMMENT_ALL = 0;//看全部评论

    //1.图片 已经不再使用
    public static final int BANNER_TYPE_IMAGE = 1;
    //2.IOS应用推荐 已经不再使用
    public static final int BANNER_TYPE_APP_ISO = 2;
    //3.产品分类 已经不再使用
    public static final int BANNER_TYPE_PRODUCT_ZHUTI = 3;
    //4.社区热帖
    public static final int BANNER_TYPE_POST = 4;
    //5.商品详情
    public static final int BANNER_TYPE_PRODUCT_DETAIL = 5;
    //6.网页
    public static final int BANNER_TYPE_WEBVIEW = 6;
    //7，android应用推荐
    public static final int BANNER_TYPE_APP_ANDROID = 7;
    //8.评测文章 已经不再使用
    public static final int BANNER_TYPE_TEST = 8;
    //9.广告分类(商品列表，通过调用3.6的广告分类列表获取，列表显示形式和专题栏目点击进的列表一致)
    // 已经不再使用
    public static final int BANNER_TYPE_PRODUCT_ZHUTI_AD = 9;
    //10.女神导购文章 已经不再使用
    public static final int BANNER_TYPE_NVSHEN_SHOPPING = 10;
    //11.信息流：普通文章
    public static final int BANNER_TYPE_FEED_ARTICLE = 11;
    //12.信息流：导购
    public static final int BANNER_TYPE_FEED_PRODUCT = 12;
    //13.信息流：测试
    public static final int BANNER_TYPE_FEED_TEST = 13;
    //14.信息流：涨姿势
    public static final int BANNER_TYPE_FEED_KNOWLEDGE = 14;
    //15 文章----图片流
    public static final int BANNER_TYPE_FEED_PIC = 15;
    //16.商品商品专题
    public static final int BANNER_TYPE_PRODUCT_TOPIC = 16;
    //17.普通圈子
    public static final int BANNER_TYPE_GENERAL_POST = 17;
    //18.问答圈子
    public static final int BANNER_TYPE_QA_POST = 18;

    public static final String[] WEEK_STRINGS = new String[]{
            "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static final int ERROR_TYPE_DATA = 1;
    public static final int ERROR_TYPE_NETWORK = -1000;
    public static final int ERROR_TYPE_REQUESTING = 3;
    public static final int ERROR_TYPE_URL_FAULT = 4;

    public static String getWechatAPPID(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_APPID_WECHAT);
        if (!StringUtils.isEmpty(oldVal)) {
            APPID_WECHAT = oldVal;
        }
        return APPID_WECHAT;
    }

    public static void setWechatAPPID(Context context, String val) {
        storeStringValue(context, TYPE_APPID_WECHAT, val);
    }

    public static String getWechatMCHID(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_MCH_ID_WECHAT);
        if (!StringUtils.isEmpty(oldVal)) {
            MCH_ID_WECHAT = oldVal;
        }
        return MCH_ID_WECHAT;
    }

    public static void setWechatMCHID(Context context, String val) {
        storeStringValue(context, TYPE_MCH_ID_WECHAT, val);
    }

    public static String getWechatKey(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_API_KEY_WECHAT);
        if (!StringUtils.isEmpty(oldVal)) {
            API_KEY_WECHAT = oldVal;
        }
        return API_KEY_WECHAT;
    }

    public static void setWechatKey(Context context, String val) {
        storeStringValue(context, TYPE_API_KEY_WECHAT, val);
    }

    public static String getAliPayPartner(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_PARTNER_ALIPAY);
        if (!StringUtils.isEmpty(oldVal)) {
            PARTNER_ALIPAY = oldVal;
        }
        return PARTNER_ALIPAY;
    }

    public static void setAliPayPartner(Context context, String val) {
        storeStringValue(context, TYPE_PARTNER_ALIPAY, val);
    }

    public static String getAliPaySeller(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_SELLER_ALIPAY);
        if (!StringUtils.isEmpty(oldVal)) {
            SELLER_ALIPAY = oldVal;
        }
        return SELLER_ALIPAY;
    }

    public static void setAliPaySeller(Context context, String val) {
        storeStringValue(context, TYPE_SELLER_ALIPAY, val);
    }

    public static String getRSAPrivate(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_RSA_PRIVATE_ALIPAY);
        if (!StringUtils.isEmpty(oldVal)) {
            RSA_PRIVATE_ALIPAY = oldVal;
        }
        return RSA_PRIVATE_ALIPAY;
    }

    public static void setRSAPrivate(Context context, String val) {
        storeStringValue(context, TYPE_RSA_PRIVATE_ALIPAY, val);
    }

    public static String getRSAPublic(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_RSA_PUBLIC_ALIPAY);
        if (!StringUtils.isEmpty(oldVal)) {
            RSA_PUBLIC_ALIPAY = oldVal;
        }
        return RSA_PUBLIC_ALIPAY;
    }

    public static void setRSAPublic(Context context, String val) {
        storeStringValue(context, TYPE_RSA_PUBLIC_ALIPAY, val);
    }

    public static String getGoodsTitle(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_GOODS_TITLE);
        if (!StringUtils.isEmpty(oldVal)) {
            GOODS_TITLE = oldVal;
        }
        return GOODS_TITLE;
    }

    public static void setGoodsTitle(Context context, String val) {
        storeStringValue(context, TYPE_GOODS_TITLE, val);
    }

    public static String getGoodsDescription(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_GOODS_DESCRIPTION);
        if (!StringUtils.isEmpty(oldVal)) {
            GOODS_DESCRIPTION = oldVal;
        }
        return GOODS_DESCRIPTION;
    }

    public static void setGoodsDescription(Context context, String val) {
        storeStringValue(context, TYPE_GOODS_DESCRIPTION, val);
    }

    public static String getAliPayUrl(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_URL_ALIPAY);
        if (!StringUtils.isEmpty(oldVal)) {
            URL_ALIPAY = oldVal;
        }
        return URL_ALIPAY;
    }

    public static void setAliPayUrl(Context context, String val) {
        storeStringValue(context, TYPE_URL_ALIPAY, val);
    }

    public static String getWeChatUrl(Context context) {
        String oldVal = getStoreStringValue(context, TYPE_URL_WECHAT);
        if (!StringUtils.isEmpty(oldVal)) {
            URL_WECHAT = oldVal;
        }
        return URL_WECHAT;
    }

    public static void setWeChatUrl(Context context, String val) {
        storeStringValue(context, TYPE_URL_WECHAT, val);
    }

    public static String getStoreStringValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    private static void storeStringValue(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
}
