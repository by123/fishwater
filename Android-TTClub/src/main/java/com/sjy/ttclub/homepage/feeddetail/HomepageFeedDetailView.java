package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.homepage.ArticleComments;
import com.sjy.ttclub.bean.homepage.ArticleDetail;
import com.sjy.ttclub.bean.homepage.ArticleRecommendInfo;
import com.sjy.ttclub.bean.homepage.ArticleRecommends;
import com.sjy.ttclub.collect.CollectRequest;
import com.sjy.ttclub.comment.CommentListInfo;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.common.PraiseHelper;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.model.HomepageDetailRequest;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linhz on 2015/10/31.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageFeedDetailView extends FrameLayout implements HomepageFeedFloatView.OnFloatItemClickListener,
        HomepageFeedDetailListView.IFeedListViewListener {

    public static final int FLOAT_ITEM_ID_PARISE = 1;
    public static final int FLOAT_ITEM_ID_SHARE = 2;
    public static final int FLOAT_ITEM_ID_COMMENT = 3;
    public static final int FLOAT_ITEM_ID_BUYCART = 4;
    public static final int FLOAT_ITEM_ID_FAVORITE = 5;

    private int mFeedType = HomepageConst.FEED_TYPE_ARTICLE;
    private HomepageFeedFloatView mFloatView;
    private HomepageFeedDetailListView mListView;

    private ImageView mShareButton;

    private LoadingLayout mLoadingLayout;

    private ImageView mTestIcon;

    private String mArticleTitle;
    private String mArticleBrif;
    private String mArticleId;
    private String mTestResult = "";
    private int mArticleChildType;
    private String mImageUrl;
    private int mPraiseCount = 0;
    private String mGoodsId;
    private boolean mIsCollected = false;
    private boolean mIsPraised = false;

    private boolean mIsFunnyTest = true;
    private int mTestState = HomepageConst.WEB_INDEX_START;

    private ArrayList<String> mImageList = new ArrayList<String>();

    private boolean mIsArticleLoadError = false;
    private IFeedDetailListener mListener;
    private HomepageDetailRequest mDetailRequest;
    private CollectRequest mCollectRequest;

    private String mSourceType;

    public HomepageFeedDetailView(Context context) {
        super(context);
        init();
    }

    private void init() {
        addContentListView();
        addFloatView();
        addBackButton();
        addShareButton();
        addLoadingLayout();
        addTestIconView();
        setBackgroundResource(R.color.white);

        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, getResources().getString(R.string.homepage_loading), false);

    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int floatViewPos = mListView.getFloatViewPosition();
            int pos = firstVisibleItem + visibleItemCount;
            if (mFloatView != null) {
                if (pos > floatViewPos) {
                    mFloatView.setVisibility(View.INVISIBLE);
                } else {
                    //图片模块不显示float 按钮
                    if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
                        mFloatView.setVisibility(View.GONE);
                    } else if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
                        if (mTestState == HomepageConst.WEB_INDEX_END) {
                            mFloatView.setVisibility(View.VISIBLE);
                        } else {
                            mFloatView.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        mFloatView.setVisibility(View.VISIBLE);
                    }

                }
            }
        }
    };

    private void addContentListView() {
        mListView = new HomepageFeedDetailListView(getContext(), mFeedType, this);
        mListView.setListViewListener(this);
        mListView.setOnScrollListener(mOnScrollListener);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        this.addView(mListView, flp);
    }

    private void addFloatView() {
        mFloatView = new HomepageFeedFloatView(getContext());
        mFloatView.setFloatItemClickListener(this);
        mFloatView.setupFloatItemView(createFloatItems(getContext(), mFeedType));
        if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            mFloatView.setVisibility(View.GONE);
        } else if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            mFloatView.setVisibility(View.INVISIBLE);
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mFloatView, lp);
    }

    private void addBackButton() {
        AlphaImageView backButton = new AlphaImageView(getContext());
        backButton.setImageResource(R.drawable.back_icon_width_bg);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackClick();
                }
                StatsModel.stats(StatsKeyDef.CONTENT_BACK);
            }
        });
        addView(backButton, setLayoutParams(Gravity.LEFT));
    }

    private void addShareButton() {
        mShareButton = new AlphaImageView(getContext());
        mShareButton.setImageResource(R.drawable.share_icon_width_bg);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    handleShareClick();
                    mListener.onShareClick();
                }
            }
        });
        if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            mShareButton.setVisibility(View.GONE);
        }
        addView(mShareButton, setLayoutParams(Gravity.RIGHT));
    }

    private FrameLayout.LayoutParams setLayoutParams(int gravity) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        int margin = ResourceHelper.getDimen(R.dimen.space_12);
        lp.gravity = gravity | Gravity.TOP;
        if (gravity == Gravity.RIGHT) {
            lp.rightMargin = margin;
        } else {
            lp.leftMargin = margin;
        }
        lp.topMargin = margin;
        if (SystemUtil.isTransparentStatusBarEnable()) {
            lp.topMargin += SystemUtil.getStatusBarHeight(getContext());
        }
        return lp;
    }

    private void addLoadingLayout() {
        mLoadingLayout = new LoadingLayout(getContext());
        mLoadingLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mIsArticleLoadError = false;
                mLoadingLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setBgContent(R.anim.loading, getResources().getString(R.string.homepage_loading), false);
                String url = createArticleUrl(mArticleId);
                mListView.updateWebView(url);

                tryGetDetailInfo();
                tryGetComments();
                tryGetRecommends();
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        this.addView(mLoadingLayout, lp);

        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void addTestIconView() {
        mTestIcon = new ImageView(getContext());
        mTestIcon.setVisibility(View.INVISIBLE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.RIGHT;
        lp.rightMargin = ResourceHelper.getDimen(R.dimen.space_12);
        lp.topMargin = ResourceHelper.getDimen(R.dimen.space_130);
        this.addView(mTestIcon, lp);
    }

    public void onResume() {
        tryGetComments();
    }

    public void setDetailViewListener(IFeedDetailListener listener) {
        mListener = listener;
    }

    public void setupDetailView(String articleId, int feedType, int childType, String sourceType) {
        mArticleId = articleId;
        mArticleChildType = childType;
        mSourceType = sourceType;
        mFeedType = feedType;
        loadDetailView();
    }

    private void statsArticleDetail() {
        String specValue = mArticleTitle;
        if (StringUtils.isEmpty(specValue)) {
            specValue = mArticleId;
        }
        HashMap<String, String> map = new HashMap<>(2);
        map.put("from", mSourceType);
        map.put("spec", specValue);
        StatsModel.stats(StatsKeyDef.CONTENT_VIEW, map);
    }

    private void loadDetailView() {
        mTestResult = null;
        mImageList.clear();
        mIsCollected = false;
        mPraiseCount = 0;
        mIsArticleLoadError = false;

        if (mFeedType != HomepageConst.FEED_TYPE_TEST) {
            mTestState = HomepageConst.WEB_INDEX_END;
        } else {
            mTestState = HomepageConst.WEB_INDEX_START;
        }

        if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            mShareButton.setVisibility(View.GONE);
        } else {
            mShareButton.setVisibility(View.VISIBLE);
        }

        mFloatView.setupFloatItemView(createFloatItems(getContext(), mFeedType));
        if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            mFloatView.setVisibility(View.GONE);
        } else if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            mFloatView.setVisibility(View.INVISIBLE);
        }

        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, getResources().getString(R.string
                .homepage_loading), false);

        mDetailRequest = new HomepageDetailRequest(getContext(), mFeedType, mSourceType);
        mCollectRequest = new CollectRequest(getContext(), CommonConst.COLLECT_TYPE_ARTICLE);

        String articleUrl = createArticleUrl(mArticleId);
        mListView.setScrollY(0);
        mListView.loadListView(mFeedType, articleUrl);

        mIsPraised = PraiseHelper.isSavePraiseStateInLocal(getContext(), mArticleId);
        updatePraise(0);

        tryGetDetailInfo();
        tryGetRecommends();
        tryGetComments();

        statsArticleDetail();
    }

    private String createArticleUrl(String articleId) {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpUrls.H5_URL);
        builder.append("?");
        builder.append("a").append("=").append("articleDetail");
        builder.append("&");
        builder.append("id").append("=").append(articleId);
        return builder.toString();
    }


    private void tryGetComments() {
        if (mFeedType == HomepageConst.FEED_TYPE_PRODUCT) {
            return;
        }

        if (mFeedType == HomepageConst.FEED_TYPE_TEST && mTestState !=
                HomepageConst.WEB_INDEX_END) {
            return;
        }

        mDetailRequest.startCommentRequest(mArticleId, new HomepageDetailRequest.CommentsRequestCallback() {
            @Override
            public void onResultFail(int errorType) {
                // do something for requestWithLoading failed??
            }

            @Override
            public void onResultSuccess(ArticleComments comments) {
                mListView.updateArticleComments(comments);
            }
        });
    }

    private void tryGetRecommends() {
        if (mFeedType == HomepageConst.FEED_TYPE_TEST && mTestState !=
                HomepageConst.WEB_INDEX_END) {
            return;
        }

        mDetailRequest.startRecommendRequest(mArticleId, new HomepageDetailRequest.RecommendRequestCallback() {
            @Override
            public void onResultFail(int errorType) {
                //do something for requestWithLoading failed??
            }

            @Override
            public void onResultSuccess(ArticleRecommends recommends) {
                mListView.updateArticleRecommends(recommends);
            }
        });
    }

    private void tryGetDetailInfo() {
        mDetailRequest.startDetailRequest(mArticleId, new HomepageDetailRequest.DetailRequestCallback() {
            @Override
            public void onResultFail(int errorType) {
                //do something for requestWithLoading failed??
            }

            @Override
            public void onResultSuccess(ArticleDetail articleDetail) {
                handleGetDetailSuccess(articleDetail);
            }
        });
    }

    private void handleGetDetailSuccess(ArticleDetail detail) {
        if (detail.getData() == null) {
            return;
        }
        mArticleTitle = detail.getData().getTitle();
        mArticleBrif = detail.getData().getBrief();

        String url = detail.getData().getImageUrl();
        if (StringUtils.isNotEmpty(url)) {
            mImageUrl = url;
        }
        //这里更新child type，因为banner进去后，没有child type，需要这里更新下
        mArticleChildType = detail.getData().getChildType();
        mPraiseCount = detail.getData().getPraiseCount();
        updatePraise(mPraiseCount);
        updateCollectIcon(detail.getData().isCollect());
        mListView.updateListView(detail);

        mGoodsId = detail.getData().getGoodsId();
        mIsFunnyTest = detail.getData().getTestingType() == HomepageConst.TEST_TYPE_FUNNY;
        updateTestIcon();
    }

    private void updateTestIcon() {
        if (mFeedType == HomepageConst.FEED_TYPE_TEST && mTestState == HomepageConst.WEB_INDEX_START) {
            if (mIsFunnyTest) {
                mTestIcon.setImageResource(R.drawable.homepage_test_funny);
            } else {
                mTestIcon.setImageResource(R.drawable.homepage_test_profesional);
            }
            if (mLoadingLayout.getVisibility() == View.VISIBLE) {
                mTestIcon.setVisibility(View.INVISIBLE);
            } else {
                mTestIcon.setVisibility(View.VISIBLE);
            }
        }
    }


    private void updateCollectIcon(boolean isCollected) {
        mIsCollected = isCollected;
        int resId = isCollected ? R.drawable.home_wenzhang_collect : R.drawable.home_wenzhang_nocollect;
        mListView.updateFloatViewIcon(FLOAT_ITEM_ID_FAVORITE, resId);
        mFloatView.updateItemIcon(FLOAT_ITEM_ID_FAVORITE, resId);
    }

    private void updatePraise(int count) {
        if (mIsPraised && count == 0) {
            count = 1;
        }

        String countStr = String.valueOf(count);
        if (count > 9999) {
            countStr = "9999+";
        } else if (count < 0) {
            countStr = "0";
        }
        mListView.updateFloatView(FLOAT_ITEM_ID_PARISE, countStr);
        mFloatView.updateItemText(FLOAT_ITEM_ID_PARISE, countStr);
        mListView.updateTopPraise(mIsPraised, count);
        updatePraiseIcon();
    }

    private void updatePraiseIcon() {
        int resId = mIsPraised ? R.drawable.home_wenzhang_zan : R.drawable.home_wenzhang_nozan;
        mListView.updateFloatViewIcon(FLOAT_ITEM_ID_PARISE, resId);
        mFloatView.updateItemIcon(FLOAT_ITEM_ID_PARISE, resId);
    }

    private void handlePraiseClick() {
        mIsPraised = !mIsPraised;
        if (mIsPraised) {
            PraiseHelper.savePraiseState(getContext(), mArticleId);
        } else {
            PraiseHelper.removePraiseState(getContext(), mArticleId);
        }
        int num = mIsPraised ? 1 : 0;
        PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_ARTICLE, mArticleId, num);
        num = mIsPraised ? 1 : -1;
        mPraiseCount = mPraiseCount + num;
        if (mPraiseCount < 0) {
            mPraiseCount = 0;
        }
        updatePraise(mPraiseCount);

        StatsModel.stats(StatsKeyDef.CONTENT_PRAISE);
    }


    private String createShareUrl() {
        String url;
        if (StringUtils.isEmpty(mTestResult)) {
            url = HttpUrls.SHARE_ARTICLE_URL + mArticleId;
        } else {
            url = HttpUrls.SHARE_ARTICLE_URL + mArticleId + "&iScore=" + mTestResult;
        }
        return url;
    }

    private void handleShareClick() {
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareTitle(mArticleTitle);

        if (StringUtils.isEmpty(mArticleBrif)) {
            builder.setShareContent(mArticleTitle);
        } else {
            builder.setShareContent(mArticleBrif);
        }
        builder.setShareUrl(createShareUrl());
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareImageUrl(mImageUrl);
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
        Bundle shareBundle = new Bundle();
        String type = "word";
        if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            type = "picture";
        } else if (mFeedType == HomepageConst.FEED_TYPE_PRODUCT) {
            type = "guide";
        } else if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            type = "test";
        }
        shareBundle.putString("type", type);
        String title = mArticleTitle;
        if (StringUtils.isEmpty(title)) {
            title = mArticleId;
        }
        shareBundle.putString("spec", title);
        builder.setStatsBundle(shareBundle);
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHARE;
        msg.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(msg);

        StatsModel.stats(StatsKeyDef.CONTENT_SHARE);//文章分享统计
    }

    private void handleCollectClick() {
        StatsModel.stats(StatsKeyDef.CONTENT_COLLECT);//文章收藏统计

        if (!AccountManager.getInstance().isLogin()) {
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            return;
        }

        final boolean isCollected = mIsCollected;

        int articleId = StringUtils.parseInt(mArticleId);
        if (isCollected) {
            mCollectRequest.cancelCollectRequest(articleId, new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {

                }

                @Override
                public void onResultSuccess() {
                    updateCollectIcon(false);
                }
            });
        } else {
            mCollectRequest.addColloectRequest(articleId, new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {
                }

                @Override
                public void onResultSuccess() {
                    updateCollectIcon(true);
                }
            });
        }
    }

    private void handleCommentClick() {
        onCommentsClick();
        StatsModel.stats(StatsKeyDef.CONTENT_COMMENT);
    }

    private void handleBuyClick() {
        goToGoodsDetail(mGoodsId);
        StatsModel.stats(StatsKeyDef.CONTENT_BUY);
    }

    @Override
    public void onCommentsClick() {
        CommentListInfo info = new CommentListInfo();
        info.commentType = CommunityConstant.COMMENTS_TYPE_ARTICLE;
        info.postId = StringUtils.parseInt(mArticleId);
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_COMMENT_LIST_WINDOW;
        msg.obj = info;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    @Override
    public void onRecommendClick(ArticleRecommendInfo info) {
        mArticleId = info.getAid();
        mArticleBrif = info.getBrief();
        mFeedType = info.getType();
        mImageUrl = info.getImageUrl();
        mSourceType = HomepageConst.SOURCE_TYPE_RECOMMEND;

        loadDetailView();
    }

    @Override
    public void onFloatItemClick(int id) {
        if (id == FLOAT_ITEM_ID_PARISE) {
            handlePraiseClick();
        } else if (id == FLOAT_ITEM_ID_FAVORITE) {
            handleCollectClick();
        } else if (id == FLOAT_ITEM_ID_COMMENT) {
            handleCommentClick();
        } else if (id == FLOAT_ITEM_ID_BUYCART) {
            handleBuyClick();
        } else if (id == FLOAT_ITEM_ID_SHARE) {
            handleShareClick();
        }
    }


    @Override
    public void onGetImagesFinished(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mImageList.clear();
        mImageList.addAll(list);
    }

    @Override
    public void onUrlTypeLoading(int urlType, String id) {
        switch (urlType) {
            case HomepageConst.URL_TYPE_ARTICLE:
                goToFeedDetail(id, HomepageConst.FEED_TYPE_ARTICLE);
                break;
            case HomepageConst.URL_TYPE_ARTICLE_PRODUCT:
                goToFeedDetail(id, HomepageConst.FEED_TYPE_PRODUCT);
                break;
            case HomepageConst.URL_TYPE_GOODS:
                goToGoodsDetail(id);
                break;
            case HomepageConst.URL_TYPE_POST:
                goToPostDetail(id);
                break;
            case HomepageConst.URL_TYPE_CIRCLE:
                goToCircleDetail(id, CommunityConstant.CIRCLE_TYPE_POST);
                break;
            case HomepageConst.URL_TYPE_TA_CIRCLE:
                goToCircleDetail(id, CommunityConstant.CIRCLE_TYPE_QA_POST);
                break;
            case HomepageConst.URL_TYPE_ARTICLE_TEST:
                goToFeedDetail(id, HomepageConst.FEED_TYPE_TEST);
                break;
            case HomepageConst.URL_TYPE_IMAGE:
                startImagePreview(id);
                break;
            case HomepageConst.URL_TYPE_USER_INFO:
                gotoUserInfo(id);
                break;
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

    /**
     * 跳转圈子详情
     *
     * @param postId
     */
    private void goToCircleDetail(String postId, int type) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(postId);
        message.arg2 = type;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 跳转商品详情
     *
     * @param goodsId
     */
    private void goToGoodsDetail(String goodsId) {
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        msg.obj = goodsId;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    /**
     * 跳转帖子详情
     *
     * @param postId
     */
    private void goToPostDetail(String postId) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = StringUtils.parseInt(postId);
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /**
     * 跳转文章详情
     *
     * @param articleId
     * @param type
     */
    private void goToFeedDetail(String articleId, int type) {
        mArticleId = articleId;
        mFeedType = type;
        loadDetailView();
    }

    private Runnable mGoneLoadingLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mIsArticleLoadError) {
                mLoadingLayout.setVisibility(View.GONE);
                updateTestIcon();
            }
        }
    };

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mLoadingLayout.getVisibility() == View.GONE) {
            return;
        }
        if (newProgress >= 99) {
            removeCallbacks(mGoneLoadingLayoutRunnable);
            postDelayed(mGoneLoadingLayoutRunnable, 300);
        }
    }

    @Override
    public void onPageFinished(String url) {
        removeCallbacks(mGoneLoadingLayoutRunnable);
        postDelayed(mGoneLoadingLayoutRunnable, 300);
    }

    @Override
    public void onPageStarted(String url) {

    }

    @Override
    public void onReceivedError() {
        mIsArticleLoadError = true;
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.drawable.no_network, getResources().getString(R.string
                .homepage_network_error_retry), true);
    }

    public static ArrayList<HomepageFeedFloatView.FloatItemInfo> createFloatItems(Context context, int detailType) {
        ArrayList<HomepageFeedFloatView.FloatItemInfo> itemList = new ArrayList<HomepageFeedFloatView.FloatItemInfo>(3);
        Resources res = context.getResources();
        HomepageFeedFloatView.FloatItemInfo info = new HomepageFeedFloatView.FloatItemInfo();
        info.id = FLOAT_ITEM_ID_PARISE;
        info.type = HomepageFeedFloatView.FloatItemInfo.TYPE_HAS_TOPICON;
        info.icon = res.getDrawable(R.drawable.home_wenzhang_nozan);
        itemList.add(info);

        info = new HomepageFeedFloatView.FloatItemInfo();
        info.id = FLOAT_ITEM_ID_FAVORITE;
        info.icon = res.getDrawable(R.drawable.home_wenzhang_nocollect);
        itemList.add(info);

        if (detailType == HomepageConst.FEED_TYPE_PRODUCT) {
            info = new HomepageFeedFloatView.FloatItemInfo();
            info.id = FLOAT_ITEM_ID_BUYCART;
            info.icon = res.getDrawable(R.drawable.home_wenzhang_buy);
            itemList.add(info);
        } else {
            info = new HomepageFeedFloatView.FloatItemInfo();
            info.id = FLOAT_ITEM_ID_COMMENT;
            info.icon = res.getDrawable(R.drawable.home_wenzhang_comments);
            itemList.add(info);
        }
        return itemList;
    }

    @Override
    public void onTestStateChanged(int state, String result) {
        mTestResult = result;
        mTestState = state;
        mTestIcon.setVisibility(View.INVISIBLE);
        if (mTestState == HomepageConst.WEB_INDEX_END) {
            tryGetComments();//获取评论
            tryGetRecommends();
            mFloatView.setVisibility(VISIBLE);
            StatsModel.stats(StatsKeyDef.CONTENT_TEST_FINISH);
        } else if (mTestState == HomepageConst.WEB_INDEX_PRE) {
            StatsModel.stats(StatsKeyDef.CONTENT_TEST_PREVIOUS);//统计上一题
        } else if (mTestState == HomepageConst.WEB_INDEX_AGAIN) {
            StatsModel.stats(StatsKeyDef.CONTENT_TEST_AGAIN);//统计再测一次
        } else {
            mFloatView.setVisibility(GONE);
            if (mTestState == HomepageConst.WEB_INDEX_START) {
                mTestIcon.setVisibility(View.VISIBLE);
                String value = mArticleTitle;
                if (value == null) {
                    value = mArticleId;
                }
                StatsModel.stats(StatsKeyDef.CONTENT_TEST_VIEW, StatsKeyDef.CONTENT_TEST_VIEW, value);
            }
        }
    }

    public interface IFeedDetailListener {
        void onBackClick();

        void onShareClick();
    }
}
