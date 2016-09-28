package com.sjy.ttclub.account.login;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.CodeRequest;
import com.sjy.ttclub.account.model.SignupRequest;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;

/**
 * Created by gangqing on 2015/11/26.
 * Email:denggangqing@ta2she.com
 */
public class VerificationWindow extends DefaultWindow implements TextWatcher, View.OnClickListener {
    private String mPhone;
    private String mNickname;
    private String mPassword;
    private String mCode;
    private TextView mVerificationPhone;
    private EditText mVerificationCode;
    private Button mVerificationSure;
    private TextView mVerificationObtainCode;
    private CountDownTimer mCountDownTimer;
    private boolean mSureClickable = false;
    private boolean mCodeClickable;

    public VerificationWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_signup_verification);
        View view = View.inflate(getContext(), R.layout.account_signup_verification, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mVerificationPhone = (TextView) view.findViewById(R.id.verification_phone);
        mVerificationCode = (EditText) view.findViewById(R.id.verification_code);
        mVerificationCode.addTextChangedListener(this);
        mVerificationSure = (Button) view.findViewById(R.id.verification_sure);
        mVerificationSure.setOnClickListener(this);
        mVerificationObtainCode = (TextView) view.findViewById(R.id.verification_obtain_code);
        mVerificationObtainCode.setOnClickListener(this);

        final int space_10 = getResources().getDimensionPixelSize(R.dimen.space_10);
        final int space_5 = getResources().getDimensionPixelSize(R.dimen.space_5);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                mVerificationObtainCode.setText("重新获取验证码" + "(" + millisUntilFinished / 1000 + ")");
                mVerificationObtainCode.setBackgroundResource(R.drawable.account_not_click);
                mVerificationObtainCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = false;
            }

            public void onFinish() {
                mVerificationObtainCode.setText("重新获取验证码");
                mVerificationObtainCode.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mVerificationObtainCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = true;
            }
        };
        mCountDownTimer.start();
    }

    public void setTransitiveData(Bundle bundle) {
        mPhone = bundle.getString(Constant.PHONE);
        mNickname = bundle.getString(Constant.NICKNAME);
        mPassword = bundle.getString(Constant.PASSWORD);
        mVerificationPhone.setText(mPhone);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mCode = mVerificationCode.getText().toString();
        if (mCode.length() == 4) {
            mVerificationSure.setBackgroundResource(R.drawable.account_selector_clickable_bg);
            mSureClickable = true;
        } else {
            mVerificationSure.setBackgroundResource(R.drawable.account_not_click);
            mSureClickable = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verification_sure:    //确定
                sure();
                break;
            case R.id.verification_obtain_code: //重新获取验证码
                obtainCode();
                break;
        }
    }

    /**
     * 注册-提交数据
     */
    private void sure() {
        if (mSureClickable) {
            SignupRequest signUpRequest = new SignupRequest(getContext()) {
                @Override
                public void success() {
                    //手机注册统计
                    StatsModel.stats(StatsKeyDef.PAGE_REGISTER_CELLPHONE);
                    DeviceManager.getInstance().hideInputMethod();
                    AccountManager.getInstance().login(mPhone, mPassword);
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_VERIFICATION_WINDOW);
                }
            };
            signUpRequest.getPersonalData(mPhone, mPassword, mNickname, mCode);
        }
    }

    /**
     * 获取状态码
     */
    private void obtainCode() {
        if (mCodeClickable) {
            CodeRequest codeRequest = new CodeRequest();
            codeRequest.getCode(mPhone);
            mCountDownTimer.start();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_AFTER_POP_OUT == stateFlag) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }
}
