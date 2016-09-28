package com.sjy.ttclub.community.allcirclespage;

import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhangwulin on 2015/12/10.
 * email 1501448275@qq.com
 */
public class CommunityCirlceClickEventRequest {

    private boolean mIsRequesting = false;

    public CommunityCirlceClickEventRequest() {
    }

    public void startRequest(int userId, int circleId) {
        if (mIsRequesting) {
            return;
        }
        mIsRequesting = true;
        IHttpManager iHttpManager = HttpManager.getBusinessHttpManger();
        iHttpManager.addParams("c", "bbs");
        iHttpManager.addParams("a", "circleHit");
        iHttpManager.addParams("userId", String.valueOf(userId));
        iHttpManager.addParams("circleId", String.valueOf(circleId));
        iHttpManager.request(HttpUrls.COMMUNITY_CIRCLE_STATS_URL, HttpMethod.GET, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                mIsRequesting = false;
            }

            @Override
            public void onError(String errorStr, int code) {
                mIsRequesting = false;
            }
        });
    }
}
