package com.sjy.ttclub.account.login;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.BindPhoneRequest;
import com.sjy.ttclub.account.model.CodeRequest;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by gangqing on 2015/12/16.
 * Email:denggangqing@ta2she.com
 */
public class BindPhoneWindow extends DefaultWindow implements View.OnClickListener, TextWatcher {
    private TextView mGetCode;
    private EditText mPhone;
    private EditText mCode;
    private Button mBind;
    private CountDownTimer mCountDownTimer;
    private String mPhoneNumber;
    private String mCodeNumber;
    private boolean mPhoneClickable;
    private boolean mCodeClickable;
    private boolean mCodeBackgroundChangeable = true;

    public BindPhoneWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_login_bind_phone);
        View view = View.inflate(getContext(), R.layout.account_bind_phone, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mGetCode = (TextView) view.findViewById(R.id.account_bind_phone_get_code);
        mGetCode.setOnClickListener(this);
        mPhone = (EditText) view.findViewById(R.id.account_bind_phone_phone);
        mCode = (EditText) view.findViewById(R.id.account_bind_phone_code);
        mBind = (Button) view.findViewById(R.id.account_bind_phone_bind);
        mBind.setOnClickListener(this);
        view.findViewById(R.id.account_bind_phone_pass).setOnClickListener(this);

        initView();
    }

    public void initView() {
        mPhone.addTextChangedListener(this);
        mCode.addTextChangedListener(this);

        final int space_10 = getResources().getDimensionPixelSize(R.dimen.space_10);
        final int space_5 = getResources().getDimensionPixelSize(R.dimen.space_5);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                mGetCode.setText("重新获取验证码" + "(" + millisUntilFinished / 1000 + ")");
                mGetCode.setBackgroundResource(R.drawable.account_not_click);
                mGetCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = false;
                mCodeBackgroundChangeable = false;
            }

            public void onFinish() {
                mGetCode.setText("重新获取验证码");
                mGetCode.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mGetCode.setPadding(space_10, space_5, space_10, space_5);
                mCodeClickable = true;
                mCodeBackgroundChangeable = true;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_bind_phone_get_code:
                obtainCode();
                break;
            case R.id.account_bind_phone_bind:
                bindPhone();
                break;
            case R.id.account_bind_phone_pass:
                DeviceManager.getInstance().hideInputMethod();
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_BIND_PHONE_WINDOW);
                break;
        }
    }

    /**
     * 获取状态码
     */
    private void obtainCode() {
        if (mCodeClickable) {
            CodeRequest codeRequest = new CodeRequest();
            codeRequest.getCode(mPhoneNumber);
            mCountDownTimer.start();
        }
    }

    /**
     * 绑定手机号码
     */
    private void bindPhone() {
        if (mPhoneClickable) {
            BindPhoneRequest bindPhoneRequest = new BindPhoneRequest(getContext());
            bindPhoneRequest.bindPhone(mPhoneNumber, mCodeNumber);
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
        mPhoneNumber = mPhone.getText().toString();
        mCodeNumber = mCode.getText().toString();
        if (mPhoneNumber.length() == 11) {
            if (mCodeNumber.length() == 4) {
                mBind.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mPhoneClickable = true;
            } else {
                mBind.setBackgroundResource(R.drawable.account_not_click);
                mPhoneClickable = false;
            }
            if (mCodeBackgroundChangeable) {
                mGetCode.setBackgroundResource(R.drawable.account_selector_clickable_bg);
                mCodeClickable = true;
            }
        } else {
            mGetCode.setBackgroundResource(R.drawable.account_not_click);
            mCodeClickable = false;
        }
    }
}
