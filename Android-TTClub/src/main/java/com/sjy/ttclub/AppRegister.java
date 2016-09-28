package com.sjy.ttclub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sjy.ttclub.common.CommonConst;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);

        msgApi.registerApp(CommonConst.getWechatAPPID(context));
    }
}
