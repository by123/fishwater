package com.lsym.ttclub.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sjy.ttclub.thirdparty.ThirdpartyManager;
import com.sjy.ttclub.thirdparty.WechatHandler;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WechatHandler handler = ThirdpartyManager.getInstance().getWechatHandler();
        if (handler.getWxApi() != null) {
            handler.getWxApi().handleIntent(getIntent(), this);
        } else {
            finish();
        }
    }

    @Override
    protected final void onNewIntent(Intent paramIntent) {
        WechatHandler handler = ThirdpartyManager.getInstance().getWechatHandler();
        if (handler.getWxApi() != null) {
            handler.getWxApi().handleIntent(paramIntent, this);
        } else {
            this.finish();
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        WechatHandler handler = ThirdpartyManager.getInstance().getWechatHandler();
        handler.onResp(resp);
        this.finish();
    }

    @Override
    public void onReq(BaseReq req) {
        this.finish();
    }
}
