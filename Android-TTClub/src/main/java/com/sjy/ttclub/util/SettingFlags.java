/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by linhz on 2015/11/30.
 * Email: linhaizhong@ta2she.com
 */
public class SettingFlags {
    private static final String PREFERENCE_NAME = "setting_flags";
    private static SharedPreferences sSharePreference;

    public static void init(Context context) {
        sSharePreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE | Context
                .MODE_MULTI_PROCESS);
    }

    public static void setFlag(String key, String value) {
        sSharePreference.edit().putString(key, value).commit();
    }

    public static void setFlag(String key, int value) {
        sSharePreference.edit().putInt(key, value).commit();
    }

    public static void setFlag(String key, boolean value) {
        sSharePreference.edit().putBoolean(key, value).commit();
    }

    public static void setFlag(String key, long value) {
        sSharePreference.edit().putLong(key, value).commit();
    }

    public static int getIntFlag(String key) {
        return getIntFlag(key, 0);
    }

    public static int getIntFlag(String key, int defValue) {
        return sSharePreference.getInt(key, defValue);
    }

    public static long getLongFlag(String key) {
        return getLongFlag(key, 0);
    }

    public static long getLongFlag(String key, long defValue) {
        return sSharePreference.getLong(key, defValue);
    }

    public static boolean getBooleanFlag(String key) {
        return getBooleanFlag(key, false);
    }

    public static boolean getBooleanFlag(String key, boolean defValue) {
        return sSharePreference.getBoolean(key, defValue);
    }

    public static String getStringFlag(String key) {
        return getStringFlag(key, null);
    }

    public static String getStringFlag(String key, String defValue) {
        return sSharePreference.getString(key, defValue);
    }
}
