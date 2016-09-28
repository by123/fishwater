
package com.sjy.ttclub.web;

import android.os.Message;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.ShareBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.file.FileUtils;

import java.util.ArrayList;

/**
 * Created by zhangwulin on 2016/1/9.
 * email 1501448275@qq.com
 */
public class WebViewJsInterface {

    public JsInterfaceCallback mCallback;

    public WebViewJsInterface(JsInterfaceCallback callback) {
        mCallback = callback;
    }

    @JavascriptInterface
    public void setImagesResource(final String[] result) {
        if (result == null || result.length == 0) {
            return;
        }
        final ArrayList<String> imageUrlList = new ArrayList<String>(result.length);
        String extension = null;
        for (int i = 0; i < result.length; i++) {
            extension = FileUtils.getExtension(result[i]);
            if (extension != null && !extension.equalsIgnoreCase("gif")) {
                imageUrlList.add(result[i]);
            }
        }
        ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {

                mCallback.onGetImagesFinished(imageUrlList);
            }
        });
    }

    @JavascriptInterface
    public void actionShare(final String result) {
        if (result == null || result.length() == 0) {
            return;
        }
        ShareBean shareBean = new Gson().fromJson(result, ShareBean.class);
        tryOpenSharePanel(shareBean);
    }

    private void tryOpenSharePanel(ShareBean shareBean) {
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setTargetPlatform(shareBean.getTarget());
        builder.setShareUrl(shareBean.getUrl());
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareImageUrl(shareBean.getImageUrl());
        builder.setShareTitle(shareBean.getTitle());
        builder.setShareContent(shareBean.getContent());
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHARE;
        message.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public interface JsInterfaceCallback {
        void onGetImagesFinished(ArrayList<String> resultList);
    }
}
