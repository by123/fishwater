package com.sjy.ttclub.web;

import android.os.Message;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by zhangwulin on 2016/1/9.
 * email 1501448275@qq.com
 */
public class WebViewController extends DefaultWindowController{

    public WebViewController() {
        registerMessage(MsgDef.MSG_SHOW_WEB_VIEW_BROWSER);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MsgDef.MSG_SHOW_WEB_VIEW_BROWSER) {
            if (msg.obj instanceof WebViewBrowserParams) {
                showWebViewBrowser((WebViewBrowserParams) msg.obj);
            }
        }
    }

    @Override
    public Object sendMessageSync(Message message) {
        return super.sendMessageSync(message);
    }

    /**
     * 打开WebView浏览窗口
     *
     * @param params
     */
    private void showWebViewBrowser(WebViewBrowserParams params) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof WebViewBrowserWindow) {
            return;
        }
        WebViewBrowserWindow webViewBrowser = new WebViewBrowserWindow(mContext, this);
        webViewBrowser.setupWebViewWindow(params);
        mWindowMgr.pushWindow(webViewBrowser);
    }
}
