/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.stats;

import android.content.Context;
import android.util.Log;

import com.sjy.ttclub.umeng.UmengStats;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by linhz on 2015/10/28.
 * Email: linhaizhong@ta2she.com
 */
public class StatsModel {
    private static final String TAG = "StatsModel";
    private static final int STATS_METHOD_NORMAL = 1;
    private static final int STATS_METHOD_UMENG = 2;
    private static final boolean ENABLE_LOG = false;

    private static TTClubStats sStatsImpl;

    public static void init(Context context) {
        sStatsImpl = factoryTTClubStats(context, STATS_METHOD_UMENG);
        sStatsImpl.config();
    }

    private static void checkInit() {
        if (sStatsImpl == null) {
            throw new IllegalArgumentException("must call init first!!");
        }
    }

    public static void pause(Context ctx) {
        checkInit();
        sStatsImpl.pause(ctx);
    }

    public static void resume(Context ctx) {
        checkInit();
        sStatsImpl.resume(ctx);
    }

    public static void stats(String key) {
        printStatsInfo(key, key);
        checkInit();
        sStatsImpl.statsKey(key);
    }

    public static void stats(String eventId, String key, int value) {
        printStatsInfo(eventId, key);
        checkInit();
        sStatsImpl.statsKeyValue(eventId, key, String.valueOf(value));
    }

    public static void stats(String eventId, String key, String value) {
        printStatsInfo(eventId, key, value);
        checkInit();
        sStatsImpl.statsKeyValue(eventId, key, value);
    }

    public static void stats(String eventId, HashMap<String, String> map) {
        printStatsInfo(eventId, map);
        checkInit();
        sStatsImpl.statsKeyValue(eventId, map);
    }

    private static TTClubStats factoryTTClubStats(Context context, int statsMethod) {
        TTClubStats stats = null;
        context = context.getApplicationContext();
        if (statsMethod == STATS_METHOD_UMENG) {
            stats = new UmengStats(context);
        } else {

        }

        return stats;
    }

    private static void printStatsInfo(String eventId, String key) {
        if (!ENABLE_LOG) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\r\neventId:").append(eventId).append("\r\n");
        builder.append("key:").append(key);
        Log.i(TAG, builder.toString());
    }

    private static void printStatsInfo(String eventId, String key, String value) {
        if (!ENABLE_LOG) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\r\neventId:").append(eventId).append("\r\n");
        builder.append("key:").append(key).append("\r\n");
        builder.append("value:").append(value);
        Log.i(TAG, builder.toString());
    }

    private static void printStatsInfo(String eventId, HashMap<String, String> map) {
        if (!ENABLE_LOG) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            builder.append("\r\neventId:").append(eventId).append("\r\n");
            builder.append("key:").append(key).append("\r\n");
            builder.append("value:").append(map.get(key)).append("\r\n");
        }
        Log.i(TAG, builder.toString());
    }
}
