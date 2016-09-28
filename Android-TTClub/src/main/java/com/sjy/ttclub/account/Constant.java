package com.sjy.ttclub.account;

import com.sjy.ttclub.common.CommonConst;

/**
 * Created by gangqing on 2015/11/26.
 * Email:denggangqing@ta2she.com
 */
public class Constant {
    public static final String PHONE = "phone";
    public static final String NICKNAME = "nickname";
    public static final String PASSWORD = "password";
    public static final String DEFAULT_SEX = String.valueOf(CommonConst.SEX_MAN);
    public static final String DEFAULT_MARRIAGE = String.valueOf(CommonConst.MARRIAGE_STATE_SINGLE);
    public static final String DEFAULT_SEX_SKILL = String.valueOf(CommonConst.SEX_SKILL_NONE);
    public static final String WEB_VIEW_TITLE = "web_view_title";
    public static final String WEB_VIEW_URL = "web_view_url";
    public static final int FOLLOW = 1;
    public static final int FANS = 2;
    public static final int RELATIONSHIP_PAGER_SIZE = 20;
    //我-取消关注
    public static final String CANCEL_ATTENTION = "0";
    //我-点击关注
    public static final String ATTENTIONING = "1";
    public static final int WANT_FEEDBACK = 1;
    public static final int FEEDBACK_RECORD = 2;
    //我-消息-用户角色-她他小秘
    public static final String TA_SHE_SECRETARY = "6";

    public static final String EXTERNAL_POST = "1";
    public static final String EXTERNAL_CIRCLE = "2";
    public static final String EXTERNAL_ARTICLE = "3";
    public static final String EXTERNAL_GOOD = "4";
    public static final String EXTERNAL_COMMODITY_PROJECT = "5";
    public static final String EXTERNAL_LINKS = "6";
    public static final String EXTERNAL_POSTURE = "7";
    public static final String EXTERNAL_TEST = "8";
    public static final String EXTERNAL_SHOPPING_GUIDE = "9";
    public static final String EXTERNAL_IMAGE = "10";
    public static final String EXTERNAL_QUESTION_ANSWER = "11";
    public static final String EXTERNAL_ORDER = "12";

    public static final int TYPE_REPLY_ME = 1;
    public static final int TYPE_SAME = 2;

    public static final String BLACK = "1"; //拉黑
    public static final String CANCEL_BLACK = "0";  //取消拉黑

    public static final int PRIVACY_OPEN_PASSWORD = 0;   //开启隐私保护
    public static final int PRIVACY_CLOSE_PASSWORD = 1;    //取消隐私保护
    public static final int PRIVACY_INTO_APPLICATION = 2;   //进入应用

    public static final int OPEN_FROM_TYPE_GUIDE=1; //从登陆引导页打开
    public static final int OPEN_FROM_TYPE_OTHER=2; //从其他页打开
}
