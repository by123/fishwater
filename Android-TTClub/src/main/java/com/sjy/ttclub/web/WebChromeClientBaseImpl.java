package com.sjy.ttclub.web;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by zhxu on 2015/11/20.
 * Email:357599859@qq.com
 */
public class WebChromeClientBaseImpl extends WebChromeClient {

    private WebChromeClientCallback mCallback;

    public WebChromeClientBaseImpl(WebChromeClientCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mCallback != null) {
            mCallback.onProgressChanged(view, newProgress);
        }
    }

    public interface WebChromeClientCallback {
        void onProgressChanged(WebView view, int newProgress);
    }
}
