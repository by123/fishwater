package com.sjy.ttclub.account.login;

import android.content.Context;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.AlphaTextView;

/**
 * Created by gangqing on 2015/11/23.
 * Email:denggangqing@ta2she.com
 */
public class LoginWindow extends DefaultWindow implements View.OnClickListener {
    private EditText mLoginPhone;
    private EditText mLoginPassword;
    private AlphaImageView mLoginPhoneClearButton;
    private AlphaImageView mLoginPasswordClearButton;
    private AlphaTextView mLoginButton;
    private static final String BIND_PHONE = "0";
    private int mIsFromWhere;

    public LoginWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_login_login);
        View view = View.inflate(getContext(), R.layout.account_login_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoginPhone = (EditText) view.findViewById(R.id.account_login_phone);
        mLoginPassword = (EditText) view.findViewById(R.id.account_login_password);
        mLoginPhoneClearButton = (AlphaImageView) view.findViewById(R.id.account_login_phone_clear_button);
        mLoginPasswordClearButton = (AlphaImageView) view.findViewById(R.id.account_login_password_clear_button);
        mLoginButton = (AlphaTextView) findViewById(R.id.account_login_login);
        findViewById(R.id.account_login_signup).setOnClickListener(this);
        findViewById(R.id.account_login_forget_password).setOnClickListener(this);
        findViewById(R.id.account_login_wechat).setOnClickListener(this);
        findViewById(R.id.account_login_weibo).setOnClickListener(this);
        findViewById(R.id.account_login_qq).setOnClickListener(this);
        findViewById(R.id.account_register_button).setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginPhoneClearButton.setOnClickListener(this);
        mLoginPasswordClearButton.setOnClickListener(this);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        addTextChangeListener();
        //统计
        StatsModel.stats(StatsKeyDef.PAGE_LOGIN_VIEW);
    }

    private void addTextChangeListener() {
        mLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    mLoginPhoneClearButton.setVisibility(VISIBLE);
                } else {
                    mLoginPhoneClearButton.setVisibility(GONE);
                }
                if (mLoginPhone.getText().toString().length() == 11 && mLoginPassword.getText().toString().length() >= 6) {
                    mLoginButton.setBackgroundResource(R.drawable.account_login_on);
                } else {
                    mLoginButton.setBackgroundResource(R.drawable.account_login_normal);
                }
            }
        });

        mLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    mLoginPasswordClearButton.setVisibility(VISIBLE);
                } else {
                    mLoginPasswordClearButton.setVisibility(GONE);
                }
                if (mLoginPhone.getText().toString().length() == 11 && mLoginPassword.getText().toString().length() >= 6) {
                    mLoginButton.setBackgroundResource(R.drawable.account_login_on);
                } else {
                    mLoginButton.setBackgroundResource(R.drawable.account_login_normal);
                }
            }
        });
    }

    public void isOpenFromWhere(int where) {
        mIsFromWhere = where;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_login_login:
                DeviceManager.getInstance().hideInputMethod();
                login();
                break;
            case R.id.account_login_signup: //注册
                DeviceManager.getInstance().hideInputMethod();
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_SIGNUP_WINDOW);
                break;
            case R.id.account_login_forget_password:    //忘记密码
                DeviceManager.getInstance().hideInputMethod();
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_RETRIEVE_PASSWORD_WINDOW);
                break;
            case R.id.account_login_qq: //qq登录
                AccountManager.getInstance().loginThirdparty(LoginMedia.QQ);
                break;
            case R.id.account_login_wechat: //微信登录
                AccountManager.getInstance().loginThirdparty(LoginMedia.WECHAT);
                break;
            case R.id.account_login_weibo:  //新浪微博登录
                AccountManager.getInstance().loginThirdparty(LoginMedia.SINA);
                break;
            case R.id.account_login_phone_clear_button:  //清除电话编辑框
                mLoginPhone.setText("");
                break;
            case R.id.account_login_password_clear_button:  //清除密码编辑框
                mLoginPassword.setText("");
                break;
            case R.id.account_register_button:  //跳转注册
                DeviceManager.getInstance().hideInputMethod();
                if (mIsFromWhere == Constant.OPEN_FROM_TYPE_GUIDE) {
                    Message message = Message.obtain();
                    message.what = MsgDef.MSG_SHOW_SIGNUP_WINDOW;
                    message.arg1 = Constant.OPEN_FROM_TYPE_OTHER;
                    MsgDispatcher.getInstance().sendMessage(message);
                } else {
                    mCallBacks.onWindowExitEvent(this, true);
                }
                break;

        }
    }

    /**
     * 登录
     */
    private void login() {
        String phone = mLoginPhone.getText().toString();
        String password = mLoginPassword.getText().toString();
        AccountManager accountManager = AccountManager.getInstance();
        accountManager.login(phone, password);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_POP_OUT) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        } else if (stateFlag == STATE_BEFORE_POP_OUT) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }
}
