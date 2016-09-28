package com.sjy.ttclub.network;

import java.io.File;

/**
 * Created by zhangwulin on 2016/1/7.
 * email 1501448275@qq.com
 */
public interface IDownLoadFileCallBack {

    void onSuccess(File file);

    void onError(int errorCode);

    void onFinish();
}
