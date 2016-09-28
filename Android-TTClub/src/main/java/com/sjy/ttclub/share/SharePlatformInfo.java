/**
 * ****************************************************************************
 * Copyright (C) 2005-2013 UCWEB Corporation. All rights reserved
 * File        : 2013-11-30
 * <p/>
 * Description :
 * <p/>
 * Creation    : 2013-11-30
 * Author      : linhz@ucweb.com
 * History     : Creation, 2013-11-30, linhz, Create the file
 * ****************************************************************************
 */
package com.sjy.ttclub.share;

import android.graphics.drawable.Drawable;

public class SharePlatformInfo {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_MORE = 2;

    //注意：id不能修改，必须与页端调用id一致
    public static final String ID_QQ = "qq";
    public static final String ID_QZONE = "qzone";
    public static final String ID_WECHAT_FRIENDS = "wechat_friends";
    public static final String ID_WECHAT_TIMELINE = "wechat_timeline";
    public static final String ID_WEIBO = "weibo";
    public static final String ID_MORE = "more";

    public int type = TYPE_NORMAL;
    public Drawable icon;
    public String title;
    public String id;

    public SharePlatformInfo() {

    }

    public boolean isMoreType() {
        return type == TYPE_MORE;
    }
}