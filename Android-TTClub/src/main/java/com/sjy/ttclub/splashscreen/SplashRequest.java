package com.sjy.ttclub.splashscreen;

import android.content.Context;

import com.sjy.ttclub.bean.splash.SplashDataBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhangwulin on 2016/1/7.
 * email 1501448275@qq.com
 */
public class SplashRequest {
    private boolean mIsRequesting = false;
    public SplashRequest() {
    }

    public void startSplashDataRequest(final SplashDataRequestCallback callback){
        if(mIsRequesting){
            return;
        }
        mIsRequesting=true;
        IHttpManager iHttpManager= HttpManager.getBusinessHttpManger();
        iHttpManager.addParams("a","splashScreenInfo");
        iHttpManager.request(HttpUrls.URL_INDEX, HttpMethod.POST, SplashDataBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                SplashDataBean splashDataBean=(SplashDataBean)obj;
                handlerOnRequestDataSuccess(splashDataBean,callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handlerOnRequestDataFail(code,callback);
            }
        });
    }
    private void handlerOnRequestDataFail(int errorCode,SplashDataRequestCallback callback){
        mIsRequesting=false;
        callback.onResultFail();
    }
    private void handlerOnRequestDataSuccess(SplashDataBean splashDataBean,SplashDataRequestCallback callback){
        mIsRequesting=false;
        if(splashDataBean.getStatus()== HttpCode.SUCCESS_CODE){
            if(splashDataBean.getData()!=null) {
                callback.onResultSuccess(splashDataBean.getData());
            }else {
                handlerOnRequestDataFail(CommunityConstant.ERROR_TYPE_DATA,callback);
            }
        }else {
            handlerOnRequestDataFail(CommunityConstant.ERROR_TYPE_DATA,callback);
        }
    }
    public interface SplashDataRequestCallback{
        void onResultFail();
        void onResultSuccess(SplashDataBean.SplashData splashData);
    }
}
