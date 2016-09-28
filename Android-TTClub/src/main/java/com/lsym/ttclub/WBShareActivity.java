package com.lsym.ttclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sjy.ttclub.thirdparty.SinaHandler;
import com.sjy.ttclub.thirdparty.ThirdpartyManager;


public class WBShareActivity extends Activity implements IWeiboHandler.Response {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SinaHandler sinaHandler = ThirdpartyManager.getInstance().getSinaHandler();
        if (sinaHandler.getShareApi() != null) {
            sinaHandler.getShareApi().handleWeiboResponse(getIntent(), this);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SinaHandler sinaHandler = ThirdpartyManager.getInstance().getSinaHandler();
        if (sinaHandler.getShareApi() != null) {
            sinaHandler.getShareApi().handleWeiboResponse(intent, this);
        } else {
            finish();
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        SinaHandler sinaHandler = ThirdpartyManager.getInstance().getSinaHandler();
        sinaHandler.onResponse(baseResponse);

        this.finish();
    }
}
