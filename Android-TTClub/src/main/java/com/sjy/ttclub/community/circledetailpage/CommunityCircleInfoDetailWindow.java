package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2016/1/5.
 * email 1501448275@qq.com
 */
public class CommunityCircleInfoDetailWindow extends DefaultWindow {
    private TitleBar mTitleBar;
    private TitleBarActionItem mTitleBarActionItem;
    private List<TitleBarActionItem> mTitleBarItems = new ArrayList<>();
    private CommunityCircleBean mCircleBean;
    private WebView mWebView;
    private LoadingLayout mLoadingLayout;
    private static final int ACTION_ITEM_SEND = 0;
    private String mCircleDetailUrl;
    public CommunityCircleInfoDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(ResourceHelper.getString(R.string.community_circle_info_title));
        createWebView();
    }

    private void createWebView() {
        View view = View.inflate(getContext(), R.layout.account_web_view_browser, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mWebView = (WebView) view.findViewById(R.id.account_web_view);
        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.account_web_view_loading);
        mLoadingLayout.setBgContent(R.anim.loading, getResources().getString(R.string.homepage_loading), false);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                setupWebView(createCircleDetailUrl());
            }
        });

    }

    private String createCircleDetailUrl(){
        if(mCircleBean==null){
            mCircleDetailUrl="";
        }
        mCircleDetailUrl= HttpUrls.CIRCLE_DETAIL_URL+mCircleBean.getCircleId();
        return mCircleDetailUrl;
    }
    public void setCircleInfo(CommunityCircleBean circleInfo) {
        this.mCircleBean = circleInfo;
        initTitleBar();
    }

    private void initTitleBar() {
        mTitleBar = (TitleBar) getTitleBar();
        mTitleBarActionItem = new TitleBarActionItem(getContext());
        mTitleBarActionItem.setEnabled(true);
        mTitleBarActionItem.setItemId(ACTION_ITEM_SEND);
        mTitleBarActionItem.setText(ResourceHelper.getString(R.string.community_send_button));
        mTitleBarItems.add(mTitleBarActionItem);
        mTitleBar.setActionItems(mTitleBarItems);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if(stateFlag==STATE_AFTER_PUSH_IN){
            setupWebView(createCircleDetailUrl());
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        if (itemId == ACTION_ITEM_SEND) {
            showSendPostWindow();
        }
    }

    private void showSendPostWindow() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mCircleBean == null) {
            return;
        }
        //统计
        StatsModel.stats(StatsKeyDef.GROUP_WRITE_POST);
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getLevel()) < 1) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_level_not_enough_one), Toast.LENGTH_SHORT);
            return;
        }
        if (mCircleBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            Message message = new Message();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
            message.obj = mCircleBean;
            MsgDispatcher.getInstance().sendMessage(message);
            return;
        }
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getSex()) == CommonConst.SEX_WOMAN) {
            Message message = new Message();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
            message.obj = mCircleBean;
            MsgDispatcher.getInstance().sendMessage(message);
            return;
        }
        if (mCircleBean.getIsLimitMale() != 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_limit_man_enter), Toast.LENGTH_SHORT);
            return;
        }
        Message message = new Message();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
        message.obj = mCircleBean;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void setupWebView(String path) {// 设置主页面的WebView
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String strUrl) {
                view.loadUrl(strUrl);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String strUrl, Bitmap favicon) {
                super.onPageStarted(view, strUrl, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String strUrl) {
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setBgContent(R.drawable.no_network, getResources().getString(R.string
                        .homepage_network_error_retry), true);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 90) {
                    ThreadManager.removeRunnable(mGoneLoadingLayoutRunnable);
                    ThreadManager.postDelayed(ThreadManager.THREAD_UI, mGoneLoadingLayoutRunnable, 500);
                }
            }
        });
        mWebView.loadUrl(path);// 打开浏览器
    }

    private Runnable mGoneLoadingLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            mLoadingLayout.setVisibility(View.GONE);
        }
    };
}
