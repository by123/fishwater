package com.sjy.ttclub.homepage.feeddetail;

import android.webkit.JavascriptInterface;

import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.web.WebViewJsInterface;

/**
 * Created by zhxu on 2015/11/12.
 * Email:357599859@qq.com
 */
public class HomepageWebViewJsInterface extends WebViewJsInterface {
    private int mPageType = HomepageConst.WEB_INDEX_START;
    private ICallBack mCallBack;

    public HomepageWebViewJsInterface(ICallBack callBack) {
        super(callBack);
        mCallBack = callBack;
    }

    public int getPageType() {
        return mPageType;
    }

    @JavascriptInterface
    public void setPageType(final String pageType) {
        ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                mPageType = StringUtils.parseInt(pageType);
                if (mCallBack != null) {
                    mCallBack.onTestStateChanged(mPageType, "");
                }
            }
        });

    }

    @JavascriptInterface
    public void setResult(final String _result) {
        ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                if (mCallBack != null) {
                    mCallBack.onTestStateChanged(mPageType, _result);
                }
            }
        });

    }


    public interface ICallBack extends JsInterfaceCallback {
        void onTestStateChanged(int state, String result);

    }
}
