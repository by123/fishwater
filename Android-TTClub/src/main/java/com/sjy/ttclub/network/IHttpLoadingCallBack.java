package com.sjy.ttclub.network;

/**
 * Created by zhangwulin on 2016/1/4.
 * email 1501448275@qq.com
 */
public interface IHttpLoadingCallBack {

    void onStart();

    void onWaiting();

    void onLoading(long total, long current, boolean isUploading);

    <T> void onSuccess(T obj, String result);

    void onError(String errorStr, int code);
}
