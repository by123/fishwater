package com.sjy.ttclub.homepage.feeddetail;

import android.webkit.WebView;

import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailListView;
import com.sjy.ttclub.web.WebChromeClientBaseImpl;

/**
 * Created by zhxu on 2015/11/20.
 * Email:357599859@qq.com
 */
public class HomepageWebChromeClient extends WebChromeClientBaseImpl {

    private HomepageFeedDetailListView.IFeedListViewListener mListener;

    public HomepageWebChromeClient(HomepageFeedDetailListView.IFeedListViewListener listener) {
        super(listener);
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mListener != null) {
            mListener.onProgressChanged(view, newProgress);
        }
    }
}
