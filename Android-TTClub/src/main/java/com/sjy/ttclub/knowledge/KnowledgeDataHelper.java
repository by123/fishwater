package com.sjy.ttclub.knowledge;

import android.content.Context;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.KnowledgeArticleBean;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.ACache;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/11/26.
 * Email: linhaizhong@ta2she.com
 */
public class KnowledgeDataHelper {
    public static final int ERROR_TYPE_NETWORK = 1;
    public static final int ERROR_TYPE_DATA = 2;
    private static final String KEY_DATA = "knowledge_article";
    private Context mContext;

    public KnowledgeDataHelper(Context context) {
        mContext = context;
    }

    public void getKnowledgeData(final ResultCallback callback) {
        String result = ACache.get(mContext).getAsString(KEY_DATA);
        boolean success = true;
        if (result != null) {
            KnowledgeArticleBean knowledgeBean = null;
            Gson gson = new Gson();
            try {
                knowledgeBean = gson.fromJson(result, KnowledgeArticleBean.class);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if (!success) {
                tryGetKnowledgeData(callback);
            } else {
                callback.onRequestSuccess(knowledgeBean);
            }
        } else {
            tryGetKnowledgeData(callback);
        }

    }

    public void tryGetKnowledgeData(final ResultCallback callback) {
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onRequestFailed(ERROR_TYPE_NETWORK);
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "experArticleConfig");
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    callback.onRequestFailed(ERROR_TYPE_DATA);
                    return;
                }
                handleRequestSuccess(result, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                callback.onRequestFailed(ERROR_TYPE_NETWORK);
            }
        });
    }

    private void handleRequestSuccess(String result, final ResultCallback callback) {
        try {
            Gson gson = new Gson();
            KnowledgeArticleBean risePosture = gson.fromJson(result, KnowledgeArticleBean.class);
            if (risePosture != null) {
                if (risePosture.getStatus() == HttpCode.SUCCESS_CODE) {
                    ACache.get(mContext).put(KEY_DATA, result);
                    callback.onRequestSuccess(risePosture);
                } else {
                    callback.onRequestFailed(ERROR_TYPE_DATA);
                }
            } else {
                callback.onRequestFailed(ERROR_TYPE_DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onRequestFailed(ERROR_TYPE_DATA);
        }
    }

    public interface ResultCallback {
        void onRequestFailed(int errorType);

        void onRequestSuccess(KnowledgeArticleBean risePosture);
    }
}
