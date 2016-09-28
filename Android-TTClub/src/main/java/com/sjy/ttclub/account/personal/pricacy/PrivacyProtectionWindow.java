package com.sjy.ttclub.account.personal.pricacy;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.widget.SecurityPasswordEditText;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.PrivacyProtectionHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ThreadManager;

/**
 * Created by gangqing on 2016/1/5.
 * Email:denggangqing@ta2she.com
 */
public class PrivacyProtectionWindow extends DefaultWindow implements SecurityPasswordEditText.SecurityEditListener {
    private SecurityPasswordEditText mSecurityPassword;
    private TextView mExplain;
    private TextView mError;
    private int mType;
    private boolean isFirst = true;
    private String mPassword;
    private String mSecondPassword;

    public PrivacyProtectionWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        View view = View.inflate(getContext(), R.layout.account_privacy, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        initView(view);
    }

    private void initView(View view) {
        mSecurityPassword = (SecurityPasswordEditText) view.findViewById(R.id.security_password);
        mSecurityPassword.setSecurityEditListener(this);
        mExplain = (TextView) view.findViewById(R.id.privacy_explain);
        mError = (TextView) view.findViewById(R.id.password_error);
    }

    public void setParams(int type) {
        mType = type;
        switch (type) {
            case Constant.PRIVACY_OPEN_PASSWORD:
                setTitle(R.string.account_privacy_protection_title1);
                mExplain.setText(ResourceHelper.getString(R.string.account_privacy_protection_explain1));
                break;
            case Constant.PRIVACY_CLOSE_PASSWORD:
                setTitle(R.string.account_privacy_protection_title2);
                mExplain.setText(ResourceHelper.getString(R.string.account_privacy_protection_explain2));
                break;
            case Constant.PRIVACY_INTO_APPLICATION:
                setTitle(R.string.account_privacy_protection_title3);
                setTitleVisibility(View.INVISIBLE);
                setEnableSwipeGesture(false);
                mExplain.setText(ResourceHelper.getString(R.string.account_privacy_protection_explain2));
                break;
        }
    }

    public boolean isAppPrivacy() {
        return (mType == Constant.PRIVACY_INTO_APPLICATION);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
    }

    @Override
    public boolean onWindowBackKeyEvent() {
        if (mType == Constant.PRIVACY_INTO_APPLICATION) {
            return true;
        }
        return super.onWindowBackKeyEvent();
    }

    @Override
    public void onNumCompleted(String password) {
        switch (mType) {
            case Constant.PRIVACY_OPEN_PASSWORD:
                if (isFirst) {
                    isFirst = false;
                    mPassword = password;
                    mExplain.setText(ResourceHelper.getString(R.string.account_privacy_protection_explain3));
                } else {
                    mSecondPassword = password;
                    savePassword();
                }
                break;
            case Constant.PRIVACY_CLOSE_PASSWORD:
                if (PrivacyProtectionHelper.equalsPassword(password)) {
                    PrivacyProtectionHelper.savePassword("", false);
                    mCallBacks.onWindowExitEvent(this, true);
                } else {
                    mError.setVisibility(VISIBLE);
                    mError.setText(ResourceHelper.getString(R.string.account_privacy_protection_error1));
                }
                break;
            case Constant.PRIVACY_INTO_APPLICATION:
                if (PrivacyProtectionHelper.equalsPassword(password)) {
                    DeviceManager.getInstance().hideInputMethod();
                    ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                        @Override
                        public void run() {
                            closeWindow();
                            Notification notification = Notification.obtain(NotificationDef.N_PRIVACY_FINISHED);
                            NotificationCenter.getInstance().notify(notification);
                        }
                    }, 200);
                } else {
                    mError.setVisibility(VISIBLE);
                    mError.setText(ResourceHelper.getString(R.string.account_privacy_protection_error1));
                }
                break;
        }
    }

    private void savePassword() {
        if (mPassword == null) {
            reset();
            return;
        } else if (mPassword.equals(mSecondPassword)) {
            PrivacyProtectionHelper.savePassword(mPassword, true);
            closeWindow();
        } else {
            mError.setVisibility(VISIBLE);
            mError.setText(ResourceHelper.getString(R.string.account_privacy_protection_error2));
            reset();
        }
    }

    private void closeWindow() {
        mCallBacks.onWindowExitEvent(this, true);
    }

    private void reset() {
        mExplain.setText(ResourceHelper.getString(R.string.account_privacy_protection_explain1));
        isFirst = true;
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_AFTER_PUSH_IN == stateFlag) {
           /* mSecurityPassword.requestEditTextFocus();
            DeviceManager.getInstance().showInputMethod();*/
        } else if (STATE_AFTER_POP_OUT == stateFlag || STATE_ON_HIDE == stateFlag) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }

}
