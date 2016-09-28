/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.network;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.network.receiver.NetworkStateChangeReceiver;
import com.sjy.ttclub.xutils.XUtilsManager;

import org.xutils.x;

/**
 * Created by linhz on 2015/10/28.
 * Email: linhaizhong@ta2she.com
 */
public class HttpManager {
    private static final int HTTP_IMPL_NORMAL = 0;
    private static final int HTTP_IMPL_XUTIL_3 = 1;

    private static NetworkStateChangeReceiver sNetStateReceiver;

    public static void init(Application application) {
        x.Ext.init(application);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    public static IHttpManager getHttpManger() {
        IHttpManager httpManager = factoryHttpManager(HTTP_IMPL_XUTIL_3);
        return httpManager;
    }

    public static IHttpManager getBusinessHttpManger() {
        IHttpManager httpManager = getHttpManger();
        httpManager.setHeadParams(HeaderManager.defaultHeader());
        return httpManager;
    }

    /**
     * 适配
     *
     * @param httpImpl 模式
     * @return
     */
    private static IHttpManager factoryHttpManager(int httpImpl) {
        IHttpManager stats = null;
        switch (httpImpl) {
            case HTTP_IMPL_XUTIL_3:
                stats = new XUtilsManager();
                break;
            default:
                break;
        }
        return stats;
    }

    /**
     * 开启网络状态广播器
     *
     * @param ctx
     */
    public static void startNetStateReceiver(Context ctx) {
        if (sNetStateReceiver != null) {
            stopNetStateReceiver(ctx);
        }

        sNetStateReceiver = new NetworkStateChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            ctx.registerReceiver(sNetStateReceiver, intentFilter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void stopNetStateReceiver(Context ctx) {
        if (sNetStateReceiver != null) {
            try {
                ctx.unregisterReceiver(sNetStateReceiver);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        sNetStateReceiver = null;
    }
}
