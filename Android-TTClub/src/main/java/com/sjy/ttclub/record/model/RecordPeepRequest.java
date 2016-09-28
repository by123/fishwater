package com.sjy.ttclub.record.model;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.bean.record.RecordPeepRuleBean;
import com.sjy.ttclub.bean.record.data.RecordPeepData;
import com.sjy.ttclub.bean.record.data.RecordPeepDataJsonBean;
import com.sjy.ttclub.bean.record.data.RecordSelfData;
import com.sjy.ttclub.bean.record.data.RecordSelfDataJsonBean;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.record.peep.RecordPeepSelectInfo;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2016/1/7.
 * Email:denggangqing@ta2she.com
 */
public class RecordPeepRequest {
    private SelfCallBack mSelfCallBack;

    public void getPeepRule(HttpCallbackAdapter callBack) {
        if (callBack == null) {
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "peepRuleGet");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, RecordPeepRuleBean.class, callBack);

    }

    public void getPeepData(RecordPeepSelectInfo selectInfo, final PeepCallback callback) {
        if (callback == null) {
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "peepDataGet");
        httpManager.addParams("sex", String.valueOf(selectInfo.sex));
        httpManager.addParams("marriage", String.valueOf(selectInfo.marriage));
        httpManager.addParams("time", String.valueOf(selectInfo.time));
        httpManager.addParams("showMe", String.valueOf(selectInfo.showMe));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, RecordPeepDataJsonBean.class, new HttpCallbackAdapter
                () {

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                if (code == HttpCode.FAIL_CODE) {
                    ToastHelper.showToast(R.string.record_peep_permission_deny);
                } else {
                    ToastHelper.showToast(R.string.data_error);
                }
                callback.onRequestFail(code);
            }

            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj instanceof RecordPeepDataJsonBean) {
                    RecordPeepDataJsonBean jsonBean = (RecordPeepDataJsonBean) obj;
                    if (jsonBean.getData() != null) {
                        callback.onRequestSuccess(jsonBean.getData());
                    } else {
                        ToastHelper.showToast(R.string.data_error);
                        callback.onRequestFail(HttpCode.FAIL_CODE);
                    }
                } else {
                    ToastHelper.showToast(R.string.data_error);
                    callback.onRequestFail(HttpCode.FAIL_CODE);
                }
            }
        });

    }

    public void getSelfData() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "recordGet");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, RecordSelfDataJsonBean.class, new RecordSelfCallBack());
    }

    private class RecordSelfCallBack extends AccountBaseIHttpCallBack {

        @Override
        public void onFail(String errorStr, int code) {
            if (mSelfCallBack != null) {
                mSelfCallBack.onFailed();
            }
        }

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof RecordSelfDataJsonBean) {
                RecordSelfDataJsonBean jsonBean = (RecordSelfDataJsonBean) obj;
                if (mSelfCallBack != null) {
                    mSelfCallBack.onSuccess(jsonBean.getData());
                }
            }
        }
    }

    public void setSelfCallBack(SelfCallBack callBack) {
        mSelfCallBack = callBack;
    }

    public interface PeepCallback {
        void onRequestSuccess(RecordPeepData data);

        void onRequestFail(int errorCode);
    }

    public interface SelfCallBack {
        void onSuccess(RecordSelfData jsonBean);

        void onFailed();
    }


}
