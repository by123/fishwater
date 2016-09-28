package com.sjy.ttclub.homepage;

import android.content.Context;

import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.util.ACache;
import com.sjy.ttclub.util.StringUtils;


/**
 * Created by linhz on 2015/12/5.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageCacheManager {
    private static final String KEY_HOMEPAGE = "homepage_first_data";
    private static final String KEY_HOMEPAGE_ARTICLE = "homepage_article_data";
    private static final String VERSION_TAG = "#homepage_version_tag#";

    private static final String COMPA_DATA_VERSION = "2.8.0";

    private static ACache mACache;


    private static void ensureCache(Context context) {
        if (mACache == null) {
            mACache = ACache.get(context);
        }
    }

    public static CacheData getHomepageFirstData(Context context) {
        ensureCache(context);
        String data = mACache.getAsString(KEY_HOMEPAGE);
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        CacheData cacheData = new CacheData();
        int index = data.indexOf(VERSION_TAG);
        if (index > 0) {
            cacheData.version = data.substring(0, index);
            if (!isDataVersionCompa(cacheData.version)) {
                return null;
            }
            cacheData.data = data.substring(index + VERSION_TAG.length());
        } else {
            cacheData.data = data;
        }
        return cacheData;
    }

    public static CacheData getArticleFirstPageData(Context context) {
        ensureCache(context);
        String data = mACache.getAsString(KEY_HOMEPAGE_ARTICLE);
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        CacheData cacheData = new CacheData();
        int index = data.indexOf(VERSION_TAG);
        if (index > 0) {
            cacheData.version = data.substring(0, index);
            if (!isDataVersionCompa(cacheData.version)) {
                return null;
            }
            cacheData.data = data.substring(index + VERSION_TAG.length());
        }
        return cacheData;
    }

    public static void cacheHomepageFirstData(Context context, String data) {
        ensureCache(context);
        if (StringUtils.isEmpty(data)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(SystemHelper.getAppInfo().versionName);
        builder.append(VERSION_TAG);
        builder.append(data);
        mACache.put(KEY_HOMEPAGE, builder.toString());
    }

    public static void cacheArticleFirstData(Context context, String data) {
        ensureCache(context);
        if (StringUtils.isEmpty(data)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(SystemHelper.getAppInfo().versionName);
        builder.append(VERSION_TAG);
        builder.append(data);
        mACache.put(KEY_HOMEPAGE_ARTICLE, builder.toString());
    }

    public static void clearHomepageCache(Context context) {
        ensureCache(context);
        mACache.remove(KEY_HOMEPAGE);
    }

    public static void clearArticleCache(Context context) {
        ensureCache(context);
        mACache.remove(KEY_HOMEPAGE_ARTICLE);
    }

    private static boolean isDataVersionCompa(String version) {
        return StringUtils.compareVersion(version, COMPA_DATA_VERSION) >= 0;
    }

    public static class CacheData {
        public String version;
        public String data;
    }
}
