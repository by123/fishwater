/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.framework;

import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.framework.adapter.AppSecurityAdapter;

public class AppSecurityManager {

    private static AppSecurityManager sInstance;
    private Context mContext;

    /* package */AppSecurityManager(Context context) {
        mContext = context;
    }

    /* package */
    static void init(Context context) {
        sInstance = new AppSecurityManager(context);
    }

    public static AppSecurityManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("should init before get instance");
        }
        return sInstance;
    }

    public boolean checkAppValide() {
        if (BuildConfig.DEBUG == true) {
            Toast.makeText(mContext, "In test mode, please be attention",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        boolean valide = AppSecurityAdapter.checkAppSecurity();
        //TODO ???
        return valide;
    }
}
