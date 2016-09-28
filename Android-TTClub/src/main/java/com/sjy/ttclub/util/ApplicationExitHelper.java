package com.sjy.ttclub.util;

import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.AppEnv;

/**
 * Created by gangqing on 2016/1/13.
 * Email:denggangqing@ta2she.com
 */
public class ApplicationExitHelper {
    private Context mContext;
    private boolean mIsFirst = true;
    private long mFirst;
    private long mSecond;

    public ApplicationExitHelper(Context context) {
        mContext = context;
    }

    public void exitApplication() {
        mSecond = System.currentTimeMillis();
        if (mIsFirst || mSecond - mFirst >= 1000 * 5) {
            mFirst = System.currentTimeMillis();
            mIsFirst = false;
            ToastHelper.showToast(mContext, R.string.exit_application_alert, Toast.LENGTH_SHORT);
        } else if (mSecond - mFirst < 1000 * 3) {
            AppEnv appEnv = AppEnv.getInstance();
            if (appEnv != null) {
                appEnv.getMainActivity().finish();
            }
        } else if (mSecond - mFirst < 1000 * 5) {
            mFirst = mSecond;
            ToastHelper.showToast(mContext, R.string.exit_application_alert, Toast.LENGTH_SHORT);
        }
    }
}
