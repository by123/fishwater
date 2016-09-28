package com.sjy.ttclub.account.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Message;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.login.LoginPanel;
import com.sjy.ttclub.bean.account.AccountBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.thirdparty.ThirdpartyLoginCallback;
import com.sjy.ttclub.thirdparty.ThirdpartyManager;
import com.sjy.ttclub.thirdparty.ThirdpartyUserInfo;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2015/11/27.
 * Email:denggangqing@ta2she.com
 */
public class AccountManager implements AccountRequest.AccountRequestCallback {
    private static final String PREFERENCE_NAME = "account_info";
    private static final String ACCOUNT_INFO_JSON = "account_info_json";
    private static final String ACCOUNT_SEX = "account_sex";
    private static final String ACCOUNT_MARRIAGE = "account_marriage";
    private static final String ACCOUNT_SEXY_LIFE = "account_sexy_life";
    private static final String ACCOUNT_TOKEN = "token";
    private Context mContext;
    private AccountInfo mAccountInfo;
    private AccountRequest mAccountRequest;
    private static AccountManager sInstance;
    private SharedPreferences mSharedPreferences;
    private LoginPanel mLoginPanel;

    /*package*/AccountManager(Context context) {
        mContext = context;
        mAccountRequest = new AccountRequest(context);
        mAccountRequest.setRequestCallback(this);
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mAccountInfo = getAccountInfo();
    }

    public static void init(Context context) {
        sInstance = new AccountManager(context);
    }

    public void setActivity(Activity activity) {
        mContext = activity;
        mAccountRequest.setActivity(activity);
    }

    public static AccountManager getInstance() {
        return sInstance;
    }

    /**
     * 获取个人资料
     *
     * @return
     */
    public AccountInfo getAccountInfo() {
        if (mAccountInfo == null) {
            mAccountInfo = getAccountInfoFromLocal();
        }
        return mAccountInfo;
    }

    /**
     * 设置个人头像
     *
     * @param simpleDraweeView
     */
    public void setHeadImage(SimpleDraweeView simpleDraweeView) {
        if (simpleDraweeView != null) {
            try {

                if (mAccountInfo == null) {
                    simpleDraweeView.setImageResource(R.drawable.account_no_login_head_image);
                } else {
                    String url = mAccountInfo.getImageUrl();
                    if (StringUtils.isEmpty(url)) {
                        simpleDraweeView.setImageResource(R.drawable.account_no_login_head_image);
                    } else {
                        simpleDraweeView.setImageURI(Uri.parse(url));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDefaultImage(SimpleDraweeView simpleDraweeView) {
        simpleDraweeView.setImageResource(R.drawable.account_no_login_head_image);
    }

    /**
     * 保存token到本地,由于使用token获取数据时并没有返回token，所以token需独立保存
     */
    private void saveTokenToLocal(String token) {
        mSharedPreferences.edit().putString(ACCOUNT_TOKEN, token).commit();
    }

    /**
     * 获取token
     */
    public String getToken() {
        if (mAccountInfo != null) {
            if (StringUtils.isNotEmpty(mAccountInfo.getToken())) {
                return mAccountInfo.getToken();
            }
        }
        return getTokenFromLocal();
    }

    /**
     * 从本地获取token
     *
     * @return
     */
    private String getTokenFromLocal() {
        return mSharedPreferences.getString(ACCOUNT_TOKEN, "");
    }

    /**
     * 从本地获取用户资料
     *
     * @return
     */
    private AccountInfo getAccountInfoFromLocal() {
        String json = getAccountInfoJsonFromLocal();
        AccountBean accountBean = null;
        AccountInfo accountInfo = null;
        try {
            if (StringUtils.isNotEmpty(json)) {
                accountBean = new Gson().fromJson(json, AccountBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accountBean != null) {
            accountInfo = new AccountInfo(accountBean.getData());
        }
        return accountInfo;
    }

    /**
     * 保存用户资料到本地
     *
     * @param json
     */
    private void saveAccountInfoJsonToLocal(String json) {
        mSharedPreferences.edit().putString(ACCOUNT_INFO_JSON, json).commit();
    }

    /**
     * 从本地获取用户资料的json数据
     *
     * @return
     */
    public String getAccountInfoJsonFromLocal() {
        return mSharedPreferences.getString(ACCOUNT_INFO_JSON, "");
    }

    /**
     * 保存性别，婚恋情况，性经验
     *
     * @param sex
     * @param marriage
     * @param sexyLife
     */
    public void saveSexMarriageSexyLife(int sex, int marriage, int sexyLife) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ACCOUNT_SEX, sex);
        editor.putInt(ACCOUNT_MARRIAGE, marriage);
        editor.putInt(ACCOUNT_SEXY_LIFE, sexyLife);
        editor.commit();
    }

    public boolean isManSex() {
        int sex = getSex();
        return sex == CommonConst.SEX_MAN;
    }

    /**
     * 获取性别
     *
     * @return
     */
    public int getSex() {
        int sex = CommonConst.DEFAULT_SEX;
        if (mAccountInfo != null) {
            sex = StringUtils.parseInt(mAccountInfo.getSex(), sex);
        } else {
            sex = mSharedPreferences.getInt(ACCOUNT_SEX, CommonConst.DEFAULT_SEX);
        }
        return sex;
    }

    public String switchSex() {
        if (CommonConst.SEX_WOMAN == getSex()) {
            return "woman";
        } else {
            return "man";
        }
    }

    /**
     * 获取婚恋情况
     *
     * @return
     */
    public int getMarriage() {
        int marriage = CommonConst.DEFAULT_MARRIAGE_STATE;
        if (mAccountInfo != null) {
            marriage = StringUtils.parseInt(mAccountInfo.getMarriage(), marriage);
        } else {
            marriage = mSharedPreferences.getInt(ACCOUNT_MARRIAGE, CommonConst.DEFAULT_MARRIAGE_STATE);
        }

        return marriage;
    }

    /**
     * 获取性经验
     *
     * @return
     */
    public int getSexyLife() {
        int life = CommonConst.DEFAULT_SEX_SKILL;
        if (mAccountInfo != null) {
            life = StringUtils.parseInt(mAccountInfo.getSexyLife(), life);
        } else {
            life = mSharedPreferences.getInt(ACCOUNT_SEXY_LIFE, CommonConst.DEFAULT_SEX_SKILL);
        }

        return life;
    }

    /**
     * 判断是否已经登录
     *
     * @return
     */
    public boolean isLogin() {
        if (mAccountInfo == null) {
            return false;
        }
        String token = getToken();
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        return true;
    }

    /**
     * 友盟登录
     *
     * @param loginMedia
     */
    public void loginThirdparty(LoginMedia loginMedia) {
        mAccountRequest.setAutoToast(true);
        mAccountRequest.showLoadingDialog();
        ThirdpartyManager.getInstance().login(loginMedia, new ThirdpartyLoginCallback() {
            @Override
            public void onLoginSuccess(LoginMedia media, ThirdpartyUserInfo userInfo) {
                mAccountRequest.startLoginThirdparty(media, userInfo);
            }

            @Override
            public void onLoginFailed(LoginMedia media) {
                ToastHelper.showToast(R.string.account_login_third_party_error);
            }
        });
    }

    /**
     * 手机号登录
     *
     * @param phone
     * @param password
     */
    public void login(String phone, String password) {
        mAccountRequest.setAutoToast(true);
        mAccountRequest.startLoginRequest(phone, password, getSex(), getMarriage(), getSexyLife());
    }

    /**
     * 打开登录面板
     */
    public void showLoginPanel() {
        if (mLoginPanel == null) {
            mLoginPanel = new LoginPanel(mContext);
        }
        mLoginPanel.showPanel();
    }

    /**
     * 更新用户数据
     */
    public void notifyAccountDataChanged() {
        mAccountRequest.setAutoToast(false);
        mAccountRequest.startLoginOnlyToken();
    }

    /**
     * 退出当前账号
     */
    public void logout() {
        mAccountInfo = null;
        saveAccountInfoJsonToLocal("");
        saveTokenToLocal("");
        notifyAccountStatus();
    }

    /**
     * 登录状态改变，发通知
     */
    private void notifyAccountStatus() {
        Notification notification = Notification.obtain(NotificationDef.N_ACCOUNT_STATE_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }

    private void notifyOrderReload() {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_MAIN_RELOAD);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    public void onLoginFailed(int errorCode) {
        Notification notification = Notification.obtain(NotificationDef.N_ACCOUNT_LOGIN_FAILED);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    public void onLoginSuccess(AccountInfo accountInfo, String json) {
        mAccountInfo = accountInfo;
        saveAccountInfoJsonToLocal(json);
        saveTokenToLocal(mAccountInfo.getToken());
        Notification notification = Notification.obtain(NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().notify(notification);

        notifyAccountStatus();
        notifyOrderReload();
    }

    /**
     * 跳转登陆窗口
     */
    public void tryOpenGuideLoginWindow() {
        tryOpenGuideLoginWindow(false);
    }

    public void tryOpenGuideLoginWindow(boolean isCloseBackWindow) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_GUIDE_LOGIN_WINDOW;
        message.obj = isCloseBackWindow;
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
