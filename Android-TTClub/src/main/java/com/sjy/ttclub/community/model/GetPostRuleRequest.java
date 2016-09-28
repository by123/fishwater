package com.sjy.ttclub.community.model;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityPostJsonBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zwl on 2015/11/17.
 * Email: 1501448275@qq.com
 */
public class GetPostRuleRequest {
    private Context mContext;
    private int mCircleId;
    private boolean mIsRequesting = false;
    private LoadingDialog dialog;
    public GetPostRuleRequest(Context mContext, int mCircleId) {
        this.mContext = mContext;
        this.mCircleId = mCircleId;

    }

    public void startRequest(GetRuleRequestResultCallback callback) {
        getRuleRequest(mCircleId, callback);
    }

    private void getRuleRequest(int circleId, final GetRuleRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        dialog=new LoadingDialog(mContext);
        dialog.show();
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "postNotice");
        mHttpManager.addParams("circleId", String.valueOf(circleId));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (result != null && result.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        if (HttpCode.SUCCESS_CODE == code) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String targetUrl = "";
                            try {
                                targetUrl = data.getString("targetUrl");
                            } catch (Exception e) {
                                //默认链接
                                targetUrl = "";
                            }
                            if (targetUrl.length() > 0) {
                                handleGetRuleSuccess(targetUrl, callback);
                            } else {
                                handleGetRuleFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
                            }
                        } else if (code == 104) {
                            handleGetRuleFailed(CommunityConstant.ERROR_TYPE_INVALID_TOKEN, callback);
                        } else if (code == 21) {
                            handleGetRuleFailed(CommunityConstant.ERROR_TYPE_CARD_NOT_EXIST, callback);
                        } else {
                            handleGetRuleFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handleGetRuleFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
                    }
                } else {
                    handleGetRuleFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetRuleFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
            }
        });
    }

    private void handleGetRuleFailed(int errorType, GetRuleRequestResultCallback callback) {
        mIsRequesting = false;
        if (dialog != null) {
            dialog.dismiss();
        }
        callback.onResultFail(errorType);
    }

    private void handleGetRuleSuccess(String url,GetRuleRequestResultCallback callback) {
        if (dialog != null) {
            dialog.dismiss();
        }
        mIsRequesting = false;
        callback.onResultSuccess(url);
    }

    public interface GetRuleRequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(String url);
    }
}
