package com.sjy.ttclub.web;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.util.file.FileUtils;


/**
 * Created by zhxu on 2015/11/20.
 * Email:357599859@qq.com
 */
public class WebViewClientBaseImpl extends WebViewClient {

    private WebViewClientCallback mCallback;

    public WebViewClientBaseImpl(WebViewClientCallback callback) {
        mCallback = callback;
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mCallback != null) {
            mCallback.onPageStarted(url);
        }
    }


    private void tryLoadGetImageJs(WebView webView) {

        String js = "javascript:(" +
                "function(){" +
                "var objs = document.getElementsByTagName('img');" +
                "var result = new Array();" +
                "for(var i = 0; i<objs.length;i++){" +
                "result[i] = objs[i].getAttribute('data-original');" +
                "};" +
                "window.android.setImagesResource(result);" +
                "})()";
        webView.loadUrl(js);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mCallback != null) {
            mCallback.onPageFinished(url);
        }
        tryLoadGetImageJs(view);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (mCallback != null) {
            mCallback.onReceivedError();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        int urlType = -1;
        String extension = FileUtils.getExtension(url);
        if (extension != null) {
            if (extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("jpg") || extension
                    .equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                mCallback.onUrlTypeLoading(HomepageConst.URL_TYPE_IMAGE, url);
                return true;
            }
        }

        if (url.lastIndexOf("goods") > -1) {
            urlType = HomepageConst.URL_TYPE_GOODS;
        } else if (url.lastIndexOf("post") > -1) {
            urlType = HomepageConst.URL_TYPE_POST;
        } else if (url.lastIndexOf("circle") > -1) {
            urlType = HomepageConst.URL_TYPE_CIRCLE;
        } else if (url.lastIndexOf("commonArticle") > -1) {
            urlType = HomepageConst.URL_TYPE_ARTICLE;
        } else if (url.lastIndexOf("testArticle") > -1) {
            urlType = HomepageConst.URL_TYPE_ARTICLE_TEST;
        } else if (url.lastIndexOf("taCircle") > -1) {
            urlType = HomepageConst.URL_TYPE_TA_CIRCLE;
        } else if (url.lastIndexOf("taUserInfo") > -1) {
            urlType = HomepageConst.URL_TYPE_USER_INFO;
        } else if(url.lastIndexOf("guideArticle") > -1){
            urlType = HomepageConst.URL_TYPE_ARTICLE_PRODUCT;
        }
        if (urlType != -1) {
            if (mCallback != null) {
                String id = "-1";
                if (url.lastIndexOf("&id=") > -1) {
                    id = url.substring(url.lastIndexOf("&id=") + 4);
                }
                mCallback.onUrlTypeLoading(urlType, id);
                return true;
            }
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    public interface WebViewClientCallback {
        void onUrlTypeLoading(int urlType, String value);

        void onPageStarted(String url);

        void onPageFinished(String url);

        void onReceivedError();
    }
}
