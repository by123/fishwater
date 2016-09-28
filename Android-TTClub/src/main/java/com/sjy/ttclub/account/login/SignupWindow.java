package com.sjy.ttclub.account.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.CodeRequest;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.AlphaTextView;

import java.util.regex.Matcher;

/**
 * Created by gangqing on 2015/11/24.
 * Email:denggangqing@ta2she.com
 */
public class SignupWindow extends DefaultWindow implements TextWatcher, View.OnClickListener {
    private EditText mPhone;
    private EditText mNickname;
    private EditText mPassword;
    private AlphaTextView mSignUp;
    private AlphaTextView mLoginButton;
    private AlphaImageView mPhoneClearButton;
    private AlphaImageView mNicknameClearButton;
    private AlphaImageView mPasswordClearButton;
    private String mPhoneText = "";
    private String mNicknameText = "";
    private String mPasswordText = "";
    private boolean SingUpAble;
    private int mIsFromWhere;

    public SignupWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_signup_signup);
        View view = View.inflate(getContext(), R.layout.account_signup_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mPhone = (EditText) findViewById(R.id.account_signup_phone);
        mPhone.addTextChangedListener(this);
        mNickname = (EditText) findViewById(R.id.account_signup_nickname);
        mNickname.addTextChangedListener(this);
        //昵称不能使用系统表情和空格
        mNickname.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = CommonUtils.emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                }
                if (source.equals(" ")) {
                    return "";
                }
                return null;
            }
        },
                new InputFilter.LengthFilter(8)});
        mPassword = (EditText) findViewById(R.id.account_signup_password);
        mPassword.addTextChangedListener(this);
        mSignUp = (AlphaTextView) findViewById(R.id.sig_up);
        mSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        mPhoneClearButton = (AlphaImageView) findViewById(R.id.account_signup_phone_clear_button);
        mNicknameClearButton = (AlphaImageView) findViewById(R.id.account_signup_nickname_clear_button);
        mPasswordClearButton = (AlphaImageView) findViewById(R.id.account_signup_password_clear_button);
        mLoginButton = (AlphaTextView) findViewById(R.id.account_login_button_in_register);
        findViewById(R.id.account_register_wechat).setOnClickListener(this);
        findViewById(R.id.account_register_weibo).setOnClickListener(this);
        findViewById(R.id.account_register_qq).setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mPhoneClearButton.setOnClickListener(this);
        mNicknameClearButton.setOnClickListener(this);
        mPasswordClearButton.setOnClickListener(this);

        //统计
        StatsModel.stats(StatsKeyDef.PAGE_REGISTER_VIEW);
    }

    public void isOpenFromWhere(int where) {
        mIsFromWhere = where;
    }

    public void signUp() {
        if (mPhoneText.length() != 11) {
            ToastHelper.showToast(getContext(),
                    ResourceHelper.getString(R.string.account_login_check_phone), Toast.LENGTH_SHORT);
            return;
        }
        if (mPasswordText.length() < 6) {
            ToastHelper.showToast(getContext(),
                    ResourceHelper.getString(R.string.account_login_check_password), Toast.LENGTH_SHORT);
            return;
        }
        if (mNicknameText.length() == 0) {
            ToastHelper.showToast(getContext(),
                    ResourceHelper.getString(R.string.account_signup_input_nickname_hint), Toast.LENGTH_SHORT);
            return;
        }
        mPhoneText = mPhone.getText().toString();
        obtainCode();
        DeviceManager.getInstance().hideInputMethod();
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.PHONE, mPhoneText);
        bundle.putString(Constant.NICKNAME, mNicknameText);
        bundle.putString(Constant.PASSWORD, mPasswordText);
        msg.obj = bundle;
        msg.what = MsgDef.MSG_SHOW_VERIFICATION_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_BEFORE_POP_OUT) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_signup_phone_clear_button:
                mPhone.setText("");
                break;
            case R.id.account_signup_nickname_clear_button:
                mNickname.setText("");
                break;
            case R.id.account_signup_password_clear_button:
                mPassword.setText("");
                break;
            case R.id.account_login_button_in_register:
                DeviceManager.getInstance().hideInputMethod();
                if (mIsFromWhere == Constant.OPEN_FROM_TYPE_GUIDE) {
                    Message message = Message.obtain();
                    message.what = MsgDef.MSG_SHOW_LOGIN_WINDOW;
                    message.arg1 = Constant.OPEN_FROM_TYPE_OTHER;
                    MsgDispatcher.getInstance().sendMessage(message);
                } else {
                    mCallBacks.onWindowExitEvent(this, true);
                }
                break;
            case R.id.account_register_qq: //qq注册
                AccountManager.getInstance().loginThirdparty(LoginMedia.QQ);
                break;
            case R.id.account_register_wechat: //微信注册
                AccountManager.getInstance().loginThirdparty(LoginMedia.WECHAT);
                break;
            case R.id.account_register_weibo:  //新浪微博注册
                AccountManager.getInstance().loginThirdparty(LoginMedia.SINA);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPhoneText = mPhone.getText().toString();
        if (mPhoneText.length() > 0) {
            mPhoneClearButton.setVisibility(VISIBLE);
        } else {
            mPhoneClearButton.setVisibility(GONE);
        }
        mNicknameText = mNickname.getText().toString();
        if (mNicknameText.length() > 0) {
            mNicknameClearButton.setVisibility(VISIBLE);
        } else {
            mNicknameClearButton.setVisibility(GONE);
        }
        mPasswordText = mPassword.getText().toString();
        if (mPasswordText.length() > 0) {
            mPasswordClearButton.setVisibility(VISIBLE);
        } else {
            mPasswordClearButton.setVisibility(GONE);
        }
        if (mPhoneText.length() == 11 && mNicknameText.length() > 0 && mPasswordText.length() > 5 && mPasswordText.length() < 19) {
            mSignUp.setBackgroundResource(R.drawable.account_login_on);
            SingUpAble = true;
        } else {
            mSignUp.setBackgroundResource(R.drawable.account_login_normal);
            SingUpAble = false;
        }
    }

    /**
     * 获取验证码
     */
    public void obtainCode() {
        if (SingUpAble) {
            CodeRequest codeRequest = new CodeRequest();
            codeRequest.getCode(mPhoneText);
        }
    }
}
