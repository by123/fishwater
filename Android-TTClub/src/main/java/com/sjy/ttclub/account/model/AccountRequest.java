package com.sjy.ttclub.account.model;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.account.AccountBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.thirdparty.ThirdpartyUserInfo;
import com.sjy.ttclub.util.Md5Utils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

/**
 * Created by gangqing on 2015/11/25.
 * Email:denggangqing@ta2she.com
 */
/*package*/class AccountRequest {
    private Context mContext;
    private Activity mActivity;

    private LoadingDialog mLoadingDialog;

    private boolean mIsAutoToast = false;

    private AccountRequestCallback mCallback;

    public AccountRequest(Context context) {
        mContext = context;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        mContext = activity;
    }

    public void setAutoToast(boolean isAutoToast) {
        mIsAutoToast = isAutoToast;
    }

    public void setRequestCallback(AccountRequestCallback callback) {
        mCallback = callback;
    }

    public void showLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        if (!(mContext instanceof Activity)) {
            return;
        }
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    /**
     * 手机号登录
     */
    public void startLoginRequest(String phoneNo, String password, int sex, int marriage, int sexyLife) {
        if (phoneNo.length() != 11) {
            ToastHelper.showToast(mContext, R.string.account_login_check_phone, Toast.LENGTH_SHORT);
            return;
        }
        if (StringUtils.isEmpty(password)) {
            ToastHelper.showToast(mContext, R.string.account_login_check_password, Toast.LENGTH_SHORT);
            return;
        }
        showLoadingDialog();
        if (sex <= 0) {
            sex = CommonConst.DEFAULT_SEX;
        }
        if (marriage <= 0) {
            marriage = CommonConst.DEFAULT_MARRIAGE_STATE;
        }
        if (sexyLife <= 0) {
            sexyLife = CommonConst.DEFAULT_SEX_SKILL;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "login");
        httpManager.addParams("tel", phoneNo);
        httpManager.addParams("pass", Md5Utils.getMD5(password));
        httpManager.addParams("sex", String.valueOf(sex));
        httpManager.addParams("marriage", String.valueOf(marriage));
        httpManager.addParams("sexyLife", String.valueOf(sexyLife));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new CellphoneCallBack());
    }

    private class CellphoneCallBack extends MyIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            //统计-手机登录成功
            StatsModel.stats(StatsKeyDef.PAGE_LOGIN_CELLPHONE);
            super.onSuccess(obj, result);
        }
    }


    /**
     * 自动登录
     */
    public void startAutoLogin()
    {
        AccountManager accountManager = AccountManager.getInstance();
        String sessionid =accountManager.getToken();
        if (StringUtils.isEmpty(sessionid))
        {
            //跳转到登录界面
            return;
        }
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "autoLogin");
        httpManager.addParams("sessionid", sessionid);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {

            }

            @Override
            public void onError(String errorStr, int code) {

            }
        });
    }

    /**
     * token获取用户信息
     */
    public void startLoginOnlyToken() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "ugi");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new MyIHttpCallBack());
    }

    /**
     * 第三方登录
     */
    public void startLoginThirdparty(LoginMedia media, ThirdpartyUserInfo userInfo) {
        showLoadingDialog();
        AccountManager accountManager = AccountManager.getInstance();
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "userThirdLoginQuick");
        httpManager.addParams("type", media.getType());
        httpManager.addParams("typeUid", userInfo.uid);
        httpManager.addParams("typeToken", userInfo.token);
        httpManager.addParams("nickname", userInfo.nickName);
        httpManager.addParams("imageUrl", userInfo.iconUrl);
        httpManager.addParams("sex", String.valueOf(accountManager.getSex()));
        httpManager.addParams("marriage", String.valueOf(accountManager.getMarriage()));
        httpManager.addParams("sexyLife", String.valueOf(accountManager.getSexyLife()));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, AccountBean.class, new LoginThirdpartyCallBack
                (media));
    }

    private class LoginThirdpartyCallBack extends MyIHttpCallBack {
        private LoginMedia mLoginMedia;

        public LoginThirdpartyCallBack(LoginMedia loginMedia) {
            mLoginMedia = loginMedia;
        }

        @Override
        public <T> void onSuccess(T obj, String result) {
            switch (mLoginMedia) {
                case QQ:
                    //统计-QQ登录
                    StatsModel.stats(StatsKeyDef.PAGE_LOGIN_QQ);
                    break;
                case WECHAT:
                    //统计-微信登录
                    StatsModel.stats(StatsKeyDef.PAGE_LOGIN_WECHAT);
                    break;
                case SINA:
                    //统计-微博登录
                    StatsModel.stats(StatsKeyDef.PAGE_LOGIN_WEIBO);
                    break;
            }

            super.onSuccess(obj, result);
        }
    }

    private class MyIHttpCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj != null) {
                if (mIsAutoToast) {
                    ToastHelper.showToast(mContext, R.string.account_login_success, Toast.LENGTH_SHORT);
                }
                AccountBean personalBean = (AccountBean) obj;
                AccountBean.Data data = personalBean.getData();
                if (StringUtils.isEmpty(data.sessionid)) {
                    try {
                        data.sessionid = AccountManager.getInstance().getToken();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AccountInfo accountInfo = new AccountInfo(data);
                mCallback.onLoginSuccess(accountInfo, result);
            } else {
                if (mIsAutoToast) {
                    ToastHelper.showToast(mContext, R.string.account_login_error, Toast.LENGTH_SHORT);
                }
                mCallback.onLoginFailed(HttpCode.FAIL_ANALYSIS_CODE);
            }
            dismissLoadingDialog();
        }

        @Override
        public void onFail(String errorStr, int code) {
            if (mIsAutoToast) {
                switch (code) {
                    case HttpCode.ERR_NOT_REGISTER:
                        ToastHelper.showToast(mContext, R.string.account_request_error_403, Toast.LENGTH_SHORT);
                        break;
                    case HttpCode.ERROR_PASSWORD:
                        ToastHelper.showToast(mContext, R.string.account_request_error_404, Toast.LENGTH_SHORT);
                        break;
                    case HttpCode.ERROR_PROHIBIT_LOGIN:
                        ToastHelper.showToast(mContext, R.string.account_request_error_405, Toast.LENGTH_SHORT);
                        break;
                    case HttpCode.ERR_TTP_TOKEN:
                        ToastHelper.showToast(mContext, R.string.account_request_error_406, Toast.LENGTH_SHORT);
                        break;
                    default:
                        ToastHelper.showToast(mContext, R.string.account_login_error, Toast.LENGTH_SHORT);
                        break;
                }
            }
            mCallback.onLoginFailed(code);
            dismissLoadingDialog();
        }
    }

    public interface AccountRequestCallback {
        void onLoginSuccess(AccountInfo accountInfo, String json);

        void onLoginFailed(int errorCode);
    }
}
