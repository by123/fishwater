package com.sjy.ttclub.framework;

import android.content.Context;

/**
 * Created by linhz on 2015/11/26.
 * Email: linhaizhong@ta2she.com
 */
public class ContextManager {
    private static Context sContext;
    private static Context sAppContext;

    /*package*/
    static void setAppContext(Context context) {
        sAppContext = context;
    }

    /*package*/
    static void setContext(Context context) {
        sContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static Context getContext() {
        return sContext;
    }

    public static Object getSystemService(String name) {
        if (null == name) {
            return null;
        }

        return sAppContext.getSystemService(name);
    }

}
