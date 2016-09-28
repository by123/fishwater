package com.sjy.ttclub.collect;

import android.content.Context;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linhz on 2015/12/23.
 * Email: linhaizhong@ta2she.com
 */
public class CollectRequest {

    private Context mContext;
    private int mCollectType;
    private boolean mIsRequesting = false;

    private boolean mIsAutoToast = true;

    public CollectRequest(Context context, int collectType) {
        this.mContext = context;
        this.mCollectType = collectType;
    }

    public void setAutoToast(boolean autoToast) {
        mIsAutoToast = autoToast;
    }

    /**
     * 添加收藏
     *
     * @param id
     * @param callback
     */
    public void addColloectRequest(int id, CollectRequestResultCallback callback) {
        sendCollectRequest(id, mCollectType, CommonConst.COLLECT_ADD, callback);
    }

    /**
     * 取消收藏
     *
     * @param id
     * @param callback
     */
    public void cancelCollectRequest(int id, CollectRequestResultCallback callback) {
        sendCollectRequest(id, mCollectType, CommonConst.COLLECT_CANCEL, callback);
    }

    public void sendCollectRequest(final int id, final int collectType, final int collectionFlag, final
    CollectRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected()) {
            callback.onResultFail(CommonConst.ERROR_TYPE_NETWORK);
            return;
        }

        if (mIsRequesting) {
            callback.onResultFail(CommonConst.ERROR_TYPE_REQUESTING);
            return;
        }

        mIsRequesting = true;
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "collectPost");
        httpManager.addParams("postId", String.valueOf(id));
        httpManager.addParams("collectType", String.valueOf(collectType));
        httpManager.addParams("collectFlag", String.valueOf(collectionFlag));
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                mIsRequesting = false;
                if (StringUtils.isNotEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        if (HttpCode.SUCCESS_CODE == code) {
                            handleCollectSuccess(collectionFlag, callback);
                        } else {
                            handleCollectFailed(collectionFlag, CommonConst.ERROR_TYPE_DATA, callback);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handleCollectFailed(collectionFlag, CommonConst.ERROR_TYPE_DATA, callback);
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                mIsRequesting = false;
                handleCollectFailed(collectionFlag, CommonConst.ERROR_TYPE_NETWORK, callback);
                super.onError(errorStr, code);
            }
        });
    }

    private void handleCollectFailed(int collectionFlag, int errorType, CollectRequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
        if (mIsAutoToast) {
            int textRes;
            if (collectionFlag == CommonConst.COLLECT_CANCEL) {
                textRes = R.string.collect_cancel_failed;
            } else {
                textRes = R.string.collect_collect_failed;
            }
            ToastHelper.showToast(mContext, textRes);
        }
    }

    private void handleCollectSuccess(int collectionFlag, CollectRequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultSuccess();
        if (mIsAutoToast) {
            int textRes;
            if (collectionFlag == CommonConst.COLLECT_CANCEL) {
                textRes = R.string.collect_cancel_collect_success;
            } else {
                textRes = R.string.collect_collected;
            }
            ToastHelper.showToast(mContext, textRes);
        }
    }

    public interface CollectRequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess();
    }
}
