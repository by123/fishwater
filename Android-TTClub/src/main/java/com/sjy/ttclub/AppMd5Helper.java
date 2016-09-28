package com.sjy.ttclub;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.AppMd5Bean;
import com.sjy.ttclub.bean.shop.ShoppingOrderBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrder;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.shopping.order.model.OrderRequest;
import com.sjy.ttclub.util.StringUtils;

public class AppMd5Helper {
    private static final String PREFERENCE_NAME = "app_md5";
    public static final String KEY_REPORT_MD5 = "report";
    private static final String KEY_ARTICLE_MD5 = "article";
    private static final String KEY_RECORD_MD5 = "record";
    private static final String KEY_PAY_MD5 = "pay";
    private static final String KEY_PRIVATE_LETTER_LEVEL = "privateLetterLevel";
    private static final String KEY_PRODUCT_CLASSIFICATION = "goodsCategoryMd5";
    private static final String KEY_SPLASH_MD5 = "splashInfoMd5";

    public enum Md5Type {REPORT, RECORD, ARTICLE, PAY, SPLASH, PRODUCT}

    private Context mContext;

    private static AppMd5Helper sInstance;

    private int mPrivateLetterLevel = -1;

    private AppMd5Helper(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        sInstance = new AppMd5Helper(context);
    }

    public static AppMd5Helper getInstance() {
        return sInstance;
    }

    public int getStoreIntValue(String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public String getStoreStringValue(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    private void storeIntValue(String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    private void storeStringValue(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }


    public int getPrivateLetterLevel() {
        if (mPrivateLetterLevel < 0) {
            mPrivateLetterLevel = getStoreIntValue(KEY_PRIVATE_LETTER_LEVEL, -1);
        }
        return mPrivateLetterLevel;
    }

    private void notifyMd5Changed(Md5Type type) {
        Notification notification = Notification.obtain(NotificationDef.N_APP_MD5_CHANGED);
        notification.extObj = type;
        NotificationCenter.getInstance().notify(notification);
    }

    private void handleReportMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_REPORT_MD5;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.REPORT);
        }
        storeStringValue(key, md5);
    }

    private void handleRecordMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_RECORD_MD5;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.RECORD);
        }
        storeStringValue(key, md5);
    }

    private void handleArticleMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_ARTICLE_MD5;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.ARTICLE);
        }
        storeStringValue(key, md5);
    }

    private void handlePayMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_PAY_MD5;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.PAY);
        }
        storeStringValue(key, md5);
    }

    private void handleSplashMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_SPLASH_MD5;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.SPLASH);
        }
        storeStringValue(key, md5);
    }

    private void handleProductClassificationMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return;
        }
        String key = KEY_PRODUCT_CLASSIFICATION;
        String oldValue = getStoreStringValue(key);
        if (oldValue == null || !oldValue.equalsIgnoreCase(md5)) {
            notifyMd5Changed(Md5Type.PRODUCT);
        }
        storeStringValue(key, md5);
    }

    private void handlePrivateLetterLevel(int level) {
        mPrivateLetterLevel = level;
        storeIntValue(KEY_PRIVATE_LETTER_LEVEL, level);
    }

    public void tryGetAppMd5() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "init");
        httpManager.addParams("sex", String.valueOf(AccountManager.getInstance().getSex()));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    return;
                }
                try {
                    Gson gson = new Gson();
                    AppMd5Bean appMd5Bean = gson.fromJson(result, AppMd5Bean.class);
                    if (appMd5Bean.getStatus() != HttpCode.SUCCESS_CODE) {
                        return;
                    }
                    handleReportMd5(appMd5Bean.getData().getReportMd5());
                    handleRecordMd5(appMd5Bean.getData().getRecordMd5());
                    handleArticleMd5(appMd5Bean.getData().getArticleMd5());
                    handlePayMd5(appMd5Bean.getData().getPayParamMd5());
                    handlePrivateLetterLevel(appMd5Bean.getData().getPrivateLetterLevelControl());
                    handleSplashMd5(appMd5Bean.getData().getSplashInfoMd5().getOther_value());
                    handleProductClassificationMd5(appMd5Bean.getData().getProductClassification());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorStr, int code) {

            }
        });
    }
}
