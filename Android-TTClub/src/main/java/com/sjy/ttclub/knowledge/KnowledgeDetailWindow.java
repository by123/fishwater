package com.sjy.ttclub.knowledge;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.KnowledgeArticleBean;
import com.sjy.ttclub.bean.homepage.ArticleDetail;
import com.sjy.ttclub.collect.CollectRequest;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.common.PraiseHelper;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedFloatView;
import com.sjy.ttclub.homepage.model.HomepageDetailRequest;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.web.WebChromeClientBaseImpl;
import com.sjy.ttclub.web.WebViewClientBaseImpl;
import com.sjy.ttclub.web.WebViewJsInterface;
import com.sjy.ttclub.widget.CustomScrollView;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version 2.6
 * @author: 陈嘉伟
 * @类 说 明:
 * @创建时间：2015/11/13
 */
public class KnowledgeDetailWindow extends AbstractWindow implements HomepageFeedFloatView
        .OnFloatItemClickListener, CustomScrollView.ScrollViewListener {

    public static final int FLOAT_ITEM_ID_PARISE = 1;
    public static final int FLOAT_ITEM_ID_SHARE = 2;
    public static final int FLOAT_ITEM_ID_FAVORITE = 3;

    private SimpleDraweeView mTopImageView;
    private TextView mPreButton, mNextButton;
    private FrameLayout mWebViewContainer;
    private WebView mWebView;
    private HomepageFeedFloatView mFloatView;
    private HomepageFeedFloatView mScrollFloatView;
    private LoadingLayout mLoadingLayout;
    private CustomScrollView mScrollView;

    private List<KnowledgeArticleBean.ArticleInfo> mDataList = new ArrayList<>();
    private int mPosition;
    private int mArticleId;
    private int mPraiseCount = 0;
    private boolean mIsPraised = false;
    private boolean mIsCollected = false;
    private ArticleDetail mArticleDetail;
    private CollectRequest mCollectRequest;
    private HomepageDetailRequest mDetailRequest;

    private KnowledgeDataHelper mDataHelper;
    private KnowledgeDetailInfo mKnowledgeDetailInfo;

    private ArrayList<String> mImageList = new ArrayList<String>();

    public KnowledgeDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        initWindow();
        mDataHelper = new KnowledgeDataHelper(context);

        mDetailRequest = new HomepageDetailRequest(getContext(), HomepageConst.FEED_TYPE_KNOWLEDGE,
                HomepageConst.SOURCE_TYPE_NORMAL);
        mCollectRequest = new CollectRequest(getContext(), CommonConst.COLLECT_TYPE_ARTICLE);
    }

    private void initWindow() {
        View parentView = View.inflate(getContext(), R.layout.knowledge_detail_layout, null);
        initViews(parentView);

        getBaseLayer().addView(parentView, getBaseLayerLP());
    }

    private void initViews(View parentView) {
        mTopImageView = (SimpleDraweeView) parentView.findViewById(R.id.knowledge_detail_imageview);
        mWebViewContainer = (FrameLayout) parentView.findViewById(R.id.knowledge_detail_webview_container);
        mPreButton = (TextView) parentView.findViewById(R.id.knowledge_detail_pre);
        mNextButton = (TextView) parentView.findViewById(R.id.knowledge_detail_next);
        parentView.findViewById(R.id.knowledge_detail_toplayout_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBacks.onWindowExitEvent(KnowledgeDetailWindow.this, true);
            }
        });

        parentView.findViewById(R.id.knowledge_detail_toplayout_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareClick();
            }
        });
        mFloatView = (HomepageFeedFloatView) parentView.findViewById(R.id.knowledge_detail_float_view);
        mScrollFloatView = (HomepageFeedFloatView) parentView.findViewById(R.id.knowledge_detail_scroll_float_view);
        mScrollView = (CustomScrollView) parentView.findViewById(R.id.knowledge_detail_scrollview);
        mScrollView.setScrollViewListener(this);
        mLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.knowledge_detail_loading);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mPreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 0) {
                    mPosition = mPosition - 1;
                    startLoading();
                }
            }
        });

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mDataList.size() - 1) {
                    mPosition = mPosition + 1;
                    startLoading();
                }
            }
        });

        View topLayout = parentView.findViewById(R.id.knowledge_detail_toplayout);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) topLayout.getLayoutParams();
        if (SystemUtil.isTransparentStatusBarEnable()) {
            lp.topMargin += SystemUtil.getStatusBarHeight(getContext());
        }
        topLayout.setLayoutParams(lp);

        mFloatView.setupFloatItemView(createFloatItems());
        mScrollFloatView.setupFloatItemView(createFloatItems());
        mFloatView.setFloatItemClickListener(this);
        mScrollFloatView.setFloatItemClickListener(this);
        mCollectRequest = new CollectRequest(getContext(), CommonConst.COLLECT_TYPE_ARTICLE);
    }

    private void startImagePreview(String imageUrl) {
        int index = mImageList.indexOf(imageUrl);
        if (index >= 0) {
            PhotoPreviewInfo info = new PhotoPreviewInfo();
            info.photoList = mImageList;
            info.position = index;
            Message msg = Message.obtain();
            msg.what = MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW;
            msg.obj = info;
            MsgDispatcher.getInstance().sendMessage(msg);
        }
    }

    private void gotoUserInfo(String id) {
        if (StringUtils.isEmpty(id)) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = StringUtils.parseInt(id);
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public void setupWindow(KnowledgeDetailInfo info) {
        mKnowledgeDetailInfo = info;
        startLoading();
    }


    private void startLoading() {
        showLoadingView();
        tryGetData();
    }

    private void tryGetData() {
        if (mDataList.isEmpty()) {
            mDataHelper.getKnowledgeData(new KnowledgeDataHelper.ResultCallback() {
                @Override
                public void onRequestFailed(int errorType) {
                    handleGetDataFailed();
                }

                @Override
                public void onRequestSuccess(KnowledgeArticleBean articleBean) {
                    if (articleBean == null) {
                        handleGetDataFailed();
                    } else {
                        handleGetDataSuccess(articleBean);
                    }

                }
            });
        } else {
            startLoadArticle();
        }
    }

    private void handleGetDataFailed() {
        handleNetworkError();
    }

    private void handleGetDataSuccess(KnowledgeArticleBean articleBean) {
        List<List<KnowledgeArticleBean.ArticleInfo>> dataList = articleBean.getData();
        boolean founded = false;
        int position = -1;
        int type = -1;
        for (int i = 0; i < dataList.size(); i++) {
            List<KnowledgeArticleBean.ArticleInfo> otherDetails = dataList.get(i);
            for (int j = 0; j < otherDetails.size(); j++) {
                KnowledgeArticleBean.ArticleInfo detail = otherDetails.get(j);
                if (detail.getAid() == mKnowledgeDetailInfo.articleId) {
                    position = j;
                    type = i;
                    founded = true;
                    break;
                }
            }
            if (founded) {
                break;
            }
        }
        if (founded) {
            mDataList.clear();
            mDataList.addAll(dataList.get(type));
            mPosition = position;

            startLoadArticle();
        }
    }


    private void startLoadArticle() {
        if (mPosition < mDataList.size()) {
            mArticleId = mDataList.get(mPosition).getAid();
        }
        mIsPraised = PraiseHelper.isSavePraiseStateInLocal(getContext(), mArticleId);
        if (mIsPraised && mPraiseCount <= 0) {
            mPraiseCount = 1;
        }
        updatePraiseView();
        tryGetArticleInfo();
        updateViews();
        setupWebView();
    }

    private void setupWebView() {
        mWebViewContainer.removeAllViewsInLayout();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);

        WebView webView = new WebView(getContext());
        mWebView = webView;
        mWebViewContainer.addView(webView, lp);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClientBaseImpl(new WebChromeClientBaseImpl
                .WebChromeClientCallback() {


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 95) {
                    showContentViews();
                }
            }
        }));
        webView.setWebViewClient(new WebViewClientBaseImpl(new WebViewClientBaseImpl.WebViewClientCallback() {
            @Override
            public void onUrlTypeLoading(int urlType, String value) {
                switch (urlType) {
                    case HomepageConst.URL_TYPE_USER_INFO:
                        gotoUserInfo(value);
                        break;
                    case HomepageConst.URL_TYPE_IMAGE:
                        startImagePreview(value);
                        break;
                }
            }

            @Override
            public void onPageStarted(String url) {

            }

            @Override
            public void onPageFinished(String url) {

            }

            @Override
            public void onReceivedError() {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                        .network_error_retry), true);
                mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
                    @Override
                    public void OnClick() {
                        if(mWebView != null){
                            mWebView.loadUrl(createArticleUrl(mArticleId));
                        }
                    }
                });
            }
        }));

        webView.addJavascriptInterface(new WebViewJsInterface(new WebViewJsInterface.JsInterfaceCallback() {
            @Override
            public void onGetImagesFinished(ArrayList<String> resultList) {
                if (resultList == null || resultList.isEmpty()) {
                    return;
                }
                mImageList.clear();
                mImageList.addAll(resultList);
            }
        }), "android");

        webView.loadUrl(createArticleUrl(mArticleId));

    }

    private void updateViews() {
        //设置点击样式
        if (0 == mPosition) {
            mPreButton.setTextColor(ResourceHelper.getColor(R.color.knowledge_detail_no_optional));
        } else {
            mPreButton.setTextColor(ResourceHelper.getColor(R.color.knowledge_detail_optional));
        }

        if ((mDataList.size() - 1) == mPosition) {
            mNextButton.setTextColor(ResourceHelper.getColor(R.color.knowledge_detail_no_optional));
        } else {
            mNextButton.setTextColor(ResourceHelper.getColor(R.color.knowledge_detail_optional));
        }
        mScrollView.scrollTo(0, 0);

    }

    public String createArticleUrl(int articleId) {
        StringBuilder builder = new StringBuilder(HttpUrls.H5_URL);
        builder.append("?");
        builder.append("a").append("=").append("articleDetail");
        builder.append("&");
        builder.append("id").append("=").append(articleId);
        return builder.toString();
    }

    @Override
    public void onFloatItemClick(int id) {
        if (id == FLOAT_ITEM_ID_SHARE) {
            handleShareClick();
        } else if (id == FLOAT_ITEM_ID_PARISE) {
            handlePraiseClick();
        } else if (id == FLOAT_ITEM_ID_FAVORITE) {
            handleCollectClick();
        }
    }

    public ArrayList<HomepageFeedFloatView.FloatItemInfo> createFloatItems() {
        ArrayList<HomepageFeedFloatView.FloatItemInfo> itemList = new ArrayList<HomepageFeedFloatView.FloatItemInfo>(3);
        Resources res = getResources();
        HomepageFeedFloatView.FloatItemInfo info = new HomepageFeedFloatView.FloatItemInfo();
        info.id = FLOAT_ITEM_ID_PARISE;
        info.type = HomepageFeedFloatView.FloatItemInfo.TYPE_HAS_TOPICON;
        info.icon = res.getDrawable(R.drawable.home_wenzhang_nozan);
        itemList.add(info);

        info = new HomepageFeedFloatView.FloatItemInfo();
        info.id = FLOAT_ITEM_ID_FAVORITE;
        info.icon = res.getDrawable(R.drawable.home_wenzhang_nocollect);
        itemList.add(info);

        info = new HomepageFeedFloatView.FloatItemInfo();
        info.id = FLOAT_ITEM_ID_SHARE;
        info.icon = res.getDrawable(R.drawable.home_wenzhang_share);
        itemList.add(info);


        return itemList;
    }

    @Override
    public void onScrollChanged(CustomScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        int curPos = scrollY + HardwareUtil.screenHeight;
        int height = mTopImageView.getHeight();
        if (mWebView != null) {
            height += mWebView.getHeight();
        }
        if (curPos > height) {
            mFloatView.setVisibility(View.GONE);
        } else {
            mFloatView.setVisibility(View.VISIBLE);
        }
    }


    private String createShareUrl() {
        String url = HttpUrls.SHARE_ARTICLE_URL + mArticleId;
        return url;
    }

    public void handleShareClick() {
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareUrl(createShareUrl());
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        String title = null;
        if (mArticleDetail != null) {
            title = mArticleDetail.getData().getTitle();
            builder.setShareTitle(title);
            builder.setShareContent(mArticleDetail.getData().getBrief());
        }
        builder.setShareImageUrl(mDataList.get(mPosition).getUrl());
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
        Bundle shareBundle = new Bundle();
        shareBundle.putString("type", "gesture");
        if (StringUtils.isEmpty(title)) {
            title = String.valueOf(mArticleId);
        }
        shareBundle.putString("spec", title);
        builder.setStatsBundle(shareBundle);

        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHARE;
        msg.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    public void showLoadingView() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.loading), false);
    }

    private void showContentViews() {
        mScrollView.setVisibility(View.VISIBLE);
        mFloatView.setVisibility(View.VISIBLE);
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 0);
                if (mWebView != null) {
                    mWebView.setScrollY(0);
                }
                mLoadingLayout.setVisibility(View.GONE);
            }
        }, 250);

    }

    public void handlePraiseClick() {
        mIsPraised = !mIsPraised;
        int num = mIsPraised ? 1 : 0;
        PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_ARTICLE, mArticleId, num);
        num = mIsPraised ? 1 : -1;
        mPraiseCount = mPraiseCount + num;
        if (mIsPraised) {
            PraiseHelper.savePraiseState(getContext(), mArticleId);
        } else {
            PraiseHelper.removePraiseState(getContext(), mArticleId);
        }

        updatePraiseView();
        StatsModel.stats(StatsKeyDef.KNOWLEDGE_PRAISE);
    }

    public void updatePraiseView() {
        int resId = mIsPraised ? R.drawable.home_wenzhang_zan : R.drawable.home_wenzhang_nozan;
        mFloatView.updateItemIcon(FLOAT_ITEM_ID_PARISE, resId);
        mScrollFloatView.updateItemIcon(FLOAT_ITEM_ID_PARISE, resId);

        String text = String.valueOf(mPraiseCount);
        if (mPraiseCount < 0) {
            mPraiseCount = 0;
            text = String.valueOf(mPraiseCount);
        } else if (mPraiseCount > 9999) {
            text = "9999+";
        }
        mFloatView.updateItemText(FLOAT_ITEM_ID_PARISE, text);
        mScrollFloatView.updateItemText(FLOAT_ITEM_ID_PARISE, text);
    }

    private void statsArticleDetail() {
        String sexValue = StatsKeyDef.VALUE_WOMAN;
        if (AccountManager.getInstance().isManSex()) {
            sexValue = StatsKeyDef.VALUE_MAN;
        }
        String specValue = null;
        if (mArticleDetail != null && mArticleDetail.getData() != null) {
            specValue = mArticleDetail.getData().getTitle();
        }
        if (StringUtils.isEmpty(specValue)) {
            specValue = String.valueOf(mArticleId);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("gender", sexValue);
        map.put("spec", specValue);
        StatsModel.stats(StatsKeyDef.KNOWLEDGE_DETAIL, map);
    }

    public void tryGetArticleInfo() {

        mDetailRequest.startDetailRequest(String.valueOf(mArticleId), new HomepageDetailRequest.DetailRequestCallback
                () {


            @Override
            public void onResultFail(int errorType) {
                handleNetworkError();
            }

            @Override
            public void onResultSuccess(ArticleDetail articleDetail) {
                handleGetArticleInfoSuccess(articleDetail);
            }
        });


    }

    private void handleGetArticleInfoSuccess(ArticleDetail articleDetail) {
        mArticleDetail = articleDetail;
        mTopImageView.setImageURI(Uri.parse(mArticleDetail.getData().getImageUrl()));
        mPraiseCount = mArticleDetail.getData().getPraiseCount();
        mIsCollected = mArticleDetail.getData().isCollect();
        updateCollectIcon();
        updatePraiseView();

        statsArticleDetail();
    }

    private void handleNetworkError() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.network_error_retry),
                true);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                if (mDataList.isEmpty()) {
                    tryGetData();
                } else {
                    tryGetArticleInfo();
                }
            }
        });
    }


    private void handleCollectClick() {
        if (!AccountManager.getInstance().isLogin()) {
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            return;
        }
        int articleId = mArticleId;
        if (mIsCollected) {
            mCollectRequest.cancelCollectRequest(articleId, new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {

                }

                @Override
                public void onResultSuccess() {
                    mIsCollected = false;
                    updateCollectIcon();
                }
            });
        } else {
            mCollectRequest.addColloectRequest(articleId, new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {
                }

                @Override
                public void onResultSuccess() {
                    mIsCollected = true;
                    updateCollectIcon();
                }
            });
        }
        StatsModel.stats(StatsKeyDef.KNOWLEDGE_COLLECT);
    }


    private void updateCollectIcon() {
        int resId = mIsCollected ? R.drawable.home_wenzhang_collect : R.drawable.home_wenzhang_nocollect;
        mFloatView.updateItemIcon(FLOAT_ITEM_ID_FAVORITE, resId);
        mScrollFloatView.updateItemIcon(FLOAT_ITEM_ID_FAVORITE, resId);
    }

}
