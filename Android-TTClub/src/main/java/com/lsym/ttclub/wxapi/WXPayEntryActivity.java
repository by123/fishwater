package com.lsym.ttclub.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI mApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, CommonConst.getWechatAPPID(this));
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Message message = Message.obtain();
            switch (resp.errCode) {
                case 0:

                    //统计PRODUCT_PAYMENT
                    StatsModel.stats(StatsKeyDef.PRODUCT_PAYMENT, "payment", "wechat");

                    message.what = MsgDef.MSG_SHOW_ORDER_SUCCESS_WINDOW;
                    break;
                default:
                    message.what = MsgDef.MSG_SHOW_ORDER_FAILED_WINDOW;
                    break;
            }
            MsgDispatcher.getInstance().sendMessage(message);
            finish();
        }
    }
}