/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.StringUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/16.
 * Email: linhaizhong@ta2she.com
 */
public class MarketChannelManager {
    private static final String PREFERENCE_NAME = "market_channel";
    private static final String KEY_MARKET_CHANNEL = "market_channel";
    private static final int MARKET_CHANNEL_AUTDITED = 1;

    private static final String VERSION = "version";
    private static final String IS_AUDITED = "isAudited";

    public static final String CHANNEL_XIAOMI = "xiaomi";

    private static MarketChannelManager sInstance;
    private Context mContext;
    private ArrayList<String> mNeedAuditChannelList = new ArrayList<String>();
    private String mCurrentChannel;

    private MarketChannelManager(Context context) {
        mContext = context;
        try {
            PackageManager pm = context.getPackageManager();
            mCurrentChannel = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
            mCurrentChannel = "unknow";
        }
        initAuditChannelList();
    }

    private void initAuditChannelList() {
        mNeedAuditChannelList.add(CHANNEL_XIAOMI);
    }

    public static void init(Context context) {
        sInstance = new MarketChannelManager(context);
    }

    public static MarketChannelManager getInstance() {
        return sInstance;
    }

    public String getMarketChannel() {
        return mCurrentChannel;
    }

    public boolean isMarketAudited() {
        if (!isMarketChannelNeedAudit()) {
            return true;
        }
        int defaultValue = -1;
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int value = sp.getInt(KEY_MARKET_CHANNEL, defaultValue);
        if (value == MARKET_CHANNEL_AUTDITED) {
            return true;
        }
        return false;
    }

    public void tryGetMarketAuditState() {
        if (!isMarketChannelNeedAudit()) {
            return;
        }
        int defaultValue = -1;
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int value = sp.getInt(KEY_MARKET_CHANNEL, defaultValue);
        if (value >= MARKET_CHANNEL_AUTDITED) {
            return;
        }
        final String versionName = SystemHelper.getAppInfo().versionName;
        IHttpManager httpManager = HttpManager.getHttpManger();
        httpManager.request(HttpUrls.AUDITED_URL, HttpMethod.GET, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONObject marketObject = dataObject.getJSONObject(mCurrentChannel);
                    if (marketObject != null) {
                        String version = marketObject.getString(VERSION);
                        if (StringUtils.isNotEmpty(version)) {
                            int versionCompare = StringUtils.compareVersion(version, versionName);
                            if (versionCompare > 0) {
                                int auditValue = 1;
                                saveMarketAuditValue(auditValue);
                                notifyAudited();
                            } else if (versionCompare == 0) {
                                int auditValue = marketObject.getInt(IS_AUDITED);
                                saveMarketAuditValue(auditValue);
                                if (auditValue > 0) {
                                    notifyAudited();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String errorStr, int code) {

            }
        });

    }

    private void notifyAudited(){
        Notification notification = Notification.obtain(NotificationDef
                .N_MARKET_AUDITED_CHANGED);
        NotificationCenter.getInstance().notify(notification, 300);
    }

    private void saveMarketAuditValue(int value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_MARKET_CHANNEL, value).commit();
    }

    private boolean isMarketChannelNeedAudit() {
        if (mNeedAuditChannelList.contains(mCurrentChannel)) {
            return true;
        }

        return false;
    }
}
