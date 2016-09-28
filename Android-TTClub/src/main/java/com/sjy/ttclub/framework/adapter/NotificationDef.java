/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.adapter;

public class NotificationDef {
    private static int sBaseId = 0;

    private static int generateId() {
        return sBaseId++;
    }

    public static final int N_NETWORK_STATE_CHANGE = generateId();
    public static final int N_ORIENTATION_CHANGE = generateId();
    public static final int N_FULL_SCREEN_MODE_CHANGE = generateId();
    public static final int N_FOREGROUND_CHANGE = generateId();
    public static final int N_SPLASH_FINISHED = generateId();
    public static final int N_ACTIVITY_RESULT = generateId();
    public static final int N_GUIDE_FINISHED = generateId();
    public static final int N_PRIVACY_FINISHED = generateId();
    public static final int N_APP_MD5_CHANGED = generateId();
    public static final int N_MARKET_AUDITED_CHANGED = generateId();
    public static final int N_HOMEPAGE_IS_INIT_SHOW = generateId();

    /**
     * 我
     */
    public static final int N_ACCOUNT_STATE_CHANGED = generateId(); //帐号状态改变
    public static final int N_ACCOUNT_LOGIN_SUCCESS = generateId(); //登录成功
    public static final int N_ACCOUNT_LOGIN_FAILED = generateId();  //登录失败
    public static final int N_MESSAGE_UNREAD_COUNT_CHANGED = generateId();  //获取到了未读消息数

    public static final int N_MESSAGE_SOFTWARE_SHOW = generateId();
    public static final int N_MESSAGE_SOFTWARE_HIDE = generateId();
    public static final int N_ORDER_MAIN_RELOAD = generateId();
    public static final int N_ORDER_ADDRESS_CHANGED = generateId();
    public static final int N_ORDER_LIST_CHANGED = generateId();
    public static final int N_ORDER_REVIEW_PUSH_SUCCESS = generateId();

    /**
     * 社区
     */
    public static final int N_COMMUNITY_IS_SEND_SOME_RELPY = generateId();
    public static final int N_COMMUNITY_UPDATE_COMMENT_PRAISE = generateId();
    public static final int N_COMMUNITY_UPDATE_POST_PRAISE = generateId();
    public static final int N_COMMUNITY_SHOW_TIPS = generateId();
    public static final int N_EMOJINKEYBOARD_SHOW = generateId();
    public static final int N_COMMUNITY_LOAD_CIRCLE_INFO = generateId();

    public static final int N_SHOPPING_CAR_COUNT_CHANGE = generateId();
}
