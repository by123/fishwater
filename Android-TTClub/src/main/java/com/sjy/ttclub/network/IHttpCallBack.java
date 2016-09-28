/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.network;

/**
 * Created by zhxu on 2015/11/23.
 * Email:357599859@qq.com
 */
public interface IHttpCallBack {
    <T> void onSuccess(T obj, String result);

    void onError(String errorStr, int code);
}
