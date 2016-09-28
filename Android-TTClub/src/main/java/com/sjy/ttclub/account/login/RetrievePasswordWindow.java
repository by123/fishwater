package com.sjy.ttclub.account.login;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.CodeRequest;
import com.sjy.ttclub.account.model.RetrievePasswordRequest;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by gangqing on 2015/11/25.
 * Email:denggangqing@ta2she.com
 */
public class RetrievePasswordWindow extends DefaultWindow implements TextWatcher, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText mPhone;
    private TextView mObtainCode;
    private EditText mCode;
    private EditText mNewPassword;
    private Button mSure;
    private CheckBox mSwitch;
    private CountDownTimer mCountDownTimer;
    private String mPhoneText;
    private String mCodeText;
    private String mNewPasswordText;
    private boolean mCodeClickable;
    private boolean mSureAble;
    private boolean mCodeBackgroundChangeable = true;

    public RetrievePasswordWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_login_forget_password_title);
        View view = View.inflate(getContext(), R.layout.account_retrieve_password_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mPhone = (EditText) view.findViewById(R.id.retrieve_password_phone);
        mPhone.addTextChangedListener(this);
        mObtainCode = (TextView) view.findViewById(R.id.retrieve_password_obtain_code);
        mObtainCode.setOnClickListener(this);
        mCode = (EditText) view.findViewById(R.id.retrieve_password_code);
        mCode.addTextChangedListener(this);
        mNewPassword = (EditText) view.findViewById(R.id.retrieve_password_password);
        mNewPassword.addTextChangedListener(this);
        mSure = (Button) view.findViewById(R.id.retrieve_password_sure);
        mSure.setOnClickListener(this);
        mSwitch = (CheckBox) view.findViewById(R.id.retrieve_password_switch);
        mSwitch.setOnCheckedChangeListener(this);

        final int space_10 = getResources().getDimensionPixelSize(R.dimen.space_10);
        final int space_5 = getResources().getDimensionPixelSize(R.dimen.space_5);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                mObtainCode.setClickable(false);
                mObtainCode.setText("重新获取" + "(" + millisUntilFinished / 1000 + ")");
                mObtainCode.setBackgroundResource(R.drawable.account_not_click);
                mObtainCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = false;
                mCodeBackgroundChangeable = false;
            }

            public void onFinish() {
                mObtainCode.setClickable(true);
                mObtainCode.setText("重新获取");
                mObtainCode.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mObtainCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = true;
                mCodeBackgroundChangeable = true;
            }
        };
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
        mCodeText = mCode.getText().toString();
        mNewPasswordText = mNewPassword.getText().toString();
        if (mPhoneText.length() == 11) {
            if (mCodeText.length() == 4 && mNewPasswordText.length() > 5) {
                mSure.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mSureAble = true;
            } else {
                mSure.setBackgroundResource(R.drawable.account_not_click);
                mSureAble = false;
            }
            if (mCodeBackgroundChangeable) {
                mObtainCode.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mCodeClickable = true;
            }
        } else {
            mObtainCode.setBackgroundResource(R.drawable.account_not_click);
            mCodeClickable = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retrieve_password_obtain_code:
                obtainCode();
                break;
            case R.id.retrieve_password_sure:
                sure();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void obtainCode() {
        if (mCodeClickable) {
            CodeRequest codeRequest = new CodeRequest();
            codeRequest.getCode(mPhoneText);
            mCountDownTimer.start();
        }
    }

    /**
     * 修改密码-提交数据
     */
    private void sure() {
        if (mSureAble) {
            RetrievePasswordRequest retrievePasswordRequest = new RetrievePasswordRequest(getContext()) {
                @Override
                public void success() {
                    DeviceManager.getInstance().hideInputMethod();
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_RETRIEVE_PASSWORD_WINDOW);
                }
            };
            retrievePasswordRequest.getPersonalData(mPhoneText, mCodeText, mNewPasswordText);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.retrieve_password_switch:
                if (isChecked) {
                    mNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //设置光标显示的位置
                mNewPassword.setSelection(mNewPassword.getText().toString().length());
                break;
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_AFTER_POP_OUT== stateFlag) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }
}
