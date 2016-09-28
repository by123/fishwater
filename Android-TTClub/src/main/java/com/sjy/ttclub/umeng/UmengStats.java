/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.umeng;

import android.content.Context;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.stats.TTClubStats;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linhz on 2015/10/28.
 * Email: linhaizhong@ta2she.com
 */
public class UmengStats implements TTClubStats {

    private Context mContext;

    private final String TAG = "UMENG";

    public UmengStats(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void config() {

    }

    private boolean enableStats() {
        if (BuildConfig.DEBUG == true) {
            return false;
        }
        return true;
    }

    @Override
    public void statsKey(String key) {
        if (enableStats()) {
            MobclickAgent.onEvent(mContext, key);
        }
    }

    @Override
    public void statsKeyValue(String eventId, String key, String value) {
        if (enableStats()) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put(key, value);
            MobclickAgent.onEvent(mContext, eventId, map);
        }
    }

    @Override
    public void statsKeyValue(String eventId, HashMap<String, String> map) {
        if (enableStats()) {
            MobclickAgent.onEvent(mContext, eventId, map);
        }
    }

    @Override
    public void pause(Context ctx) {
        MobclickAgent.onPause(ctx);
    }

    @Override
    public void resume(Context ctx) {
        MobclickAgent.onResume(ctx);
    }

    @Override
    public void destory() {

    }
}
