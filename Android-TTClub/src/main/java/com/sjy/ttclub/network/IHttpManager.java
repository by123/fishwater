/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.network;

import java.util.HashMap;

/**
 * Created by zhxu on 2015/11/25.
 * Email:357599859@qq.com
 */
public interface IHttpManager {

    <T> T requestSync(String url, HttpMethod httpMethod, Class<T> obj);

    <T> void request(String url, HttpMethod httpMethod, Class<T> obj, IHttpCallBack callBack);

    void request(String url, HttpMethod httpMethod, IHttpCallBack callBack);

    void requestWithLoading(String url, HttpMethod httpMethod, IHttpLoadingCallBack callBack);

    <T> void requestWithLoading(String url, HttpMethod httpMethod, Class<T> obj, IHttpLoadingCallBack callBack);

    void downLoadFile(String url,String tagetUrl,IDownLoadFileCallBack callBack);

    void addParams(String key, String val);

    void addParams(String name, Object value, String contentType);

    void addHeadParams(String key, String val);

    void updateHeadParams(String key, String val);

    void setHeadParams(HashMap<String, String> headParams);

    void setParams(String url, HttpMethod httpMethod);

    void setBodyContent(String content);
}
