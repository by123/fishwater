package com.sjy.ttclub.shopping.product;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.MessageChatDetailsRequestHelper;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.bean.account.DialogIDBean;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.bean.shop.ShoppingGoodsDetailBean;
import com.sjy.ttclub.bean.shop.ShoppingGoodsImageUrlsBean;
import com.sjy.ttclub.bean.shop.ShoppingReviewBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingGoodsDetail;
import com.sjy.ttclub.collect.CollectRequest;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.shopping.ShoppingConstant;
import com.sjy.ttclub.shopping.model.ShoppingRequest;
import com.sjy.ttclub.shopping.product.comments.ProductCommentItemHolder;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarRequest;
import com.sjy.ttclub.shopping.widget.ShoppingCountdownView;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ImageCycleView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.PriceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiawei on 2015/12/28.
 */
public class ProductDetailWindow extends DefaultWindow implements View.OnClickListener,
        MessageChatDetailsRequestHelper.DialogIdCallBackSuccess, LoadingLayout.OnBtnClickListener {
    private ImageCycleView mImageCycleView;
    private LoadingLayout mLoadingLayout;
    private TextView mTitleContent, mBuyer;
    private PriceView mDiscountPrice, mOriginalPrice;
    private TextView mWelfare;
    private TextView mCommentTitleView;
    private TextView mCommentMore;
    private View mPanicTimeView;
    private ShoppingCountdownView mCountdownView;
    private View mCommentView;
    private ProductCommentItemHolder mCommentHolder;
    private WebView mWebView;
    private String mGoodsId;
    private TextView mAddCollect, mBuy, mSecretary;
    private TextView mSpecTitle;
    private TextView mAddShoppingCar;

    private ShoppingGoodsDetailBean mDetailBean;

    private ShoppingRequest mShoppingRequest;
    private CollectRequest mCollectRequest;

    private boolean mIsCollected = false;
    private ProductBuyPanel mProductBuyPanel;

    public ProductDetailWindow(Context context, IDefaultWindowCallBacks callBacks, String goodsId) {
        super(context, callBacks);
        setLaunchMode(LAUNCH_MODE_SINGLE_INSTANCE);
        setTitle(R.string.shopping_product_title);
        mGoodsId = goodsId;
        mShoppingRequest = new ShoppingRequest();
        mCollectRequest = new CollectRequest(context, CommonConst.COLLECT_TYPE_PRODUCT);
        mCommentHolder = new ProductCommentItemHolder();
        initActionBar();
        initViews();
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_cart));
        item.setItemId(MenuItemIdDef.TITLEBAR_SHOPPING_CART);
        actionList.add(item);

        item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.share_icon_gray));
        item.setItemId(MenuItemIdDef.TITLEBAR_SHARE);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    private void initViews() {

        View view = View.inflate(getContext(), R.layout.shopping_product_detail_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        view.setWillNotDraw(false);

        mImageCycleView = (ImageCycleView) view.findViewById(R.id.imageCycle);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageCycleView.getLayoutParams();
        params.width = HardwareUtil.windowWidth;
        params.height = HardwareUtil.windowWidth;
        mImageCycleView.setLayoutParams(params);

        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.loading_layout);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mTitleContent = (TextView) view.findViewById(R.id.product_detail_title_context);
        mDiscountPrice = (PriceView) view.findViewById(R.id.product_detail_discount_price);
        mOriginalPrice = (PriceView) view.findViewById(R.id.product_detail_original_price);
        mBuyer = (TextView) view.findViewById(R.id.product_detail_buyer);
        mWelfare = (TextView) view.findViewById(R.id.product_detail_welfare);
        mPanicTimeView = view.findViewById(R.id.product_detail_limit_buy_info);
        mCountdownView = (ShoppingCountdownView) view.findViewById(R.id.product_detail_countdown);
        mCommentTitleView = (TextView) view.findViewById(R.id.product_detail_comments_title);
        mCommentView = view.findViewById(R.id.product_detail_comment_item);
        mCommentHolder.initHolder(mCommentView);
        mCommentMore = (TextView) view.findViewById(R.id.product_detail_comments_more);
        mAddShoppingCar = (TextView) findViewById(R.id.product_detail_add_buy_car);
        mAddShoppingCar.setOnClickListener(this);

        mWebView = (WebView) view.findViewById(R.id.product_detail_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);

        mAddCollect = (TextView) view.findViewById(R.id.product_detail_collect);
        mAddCollect.setOnClickListener(this);

        mBuy = (TextView) view.findViewById(R.id.product_detail_buy);
        mBuy.setOnClickListener(this);

        mSecretary = (TextView) view.findViewById(R.id.product_detail_secretary);
        mSecretary.setOnClickListener(this);

        mSpecTitle = (TextView) view.findViewById(R.id.product_detail_spec_text);
        mSpecTitle.setOnClickListener(this);

        view.findViewById(R.id.product_detail_comments_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_detail_spec_text:
                tryOpenProductBuyPanel(ShoppingConstant.PANEL_TYPE_SPEC);
                break;
            case R.id.product_detail_buy:
                tryOpenProductBuyPanel(ShoppingConstant.PANEL_TYPE_BUY);
                break;
            case R.id.product_detail_add_buy_car:
                tryOpenProductBuyPanel(ShoppingConstant.PANEL_TYPE_BUY_CAR);
                break;
            case R.id.product_detail_secretary:
                MessageChatDetailsRequestHelper helper = new MessageChatDetailsRequestHelper(getContext());
                helper.setDialogIdCallBack(this);
                helper.dialogId("0", "2");
                break;
            case R.id.product_detail_comments_layout:
                handleCommentsClick();
                break;
            case R.id.product_detail_collect:
                handleCollectClick();
                break;
            default:
                break;
        }
    }

    private void tryOpenProductBuyPanel(int panelType) {
        if (mProductBuyPanel != null && mProductBuyPanel.isShowing()) {
            return;
        }
        mProductBuyPanel = new ProductBuyPanel(getContext(), mDetailBean, panelType);
        mProductBuyPanel.showPanel();
    }

    private void handleCommentsClick() {
        if (StringUtils.isEmpty(mGoodsId)) {
            return;
        }
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PRODUCT_COMMENTS_WINDOW;
        msg.obj = mGoodsId;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void handleCollectClick() {
        if (!AccountManager.getInstance().isLogin()) {
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            return;
        }
        if (mIsCollected) {
            mCollectRequest.cancelCollectRequest(StringUtils.parseInt(mGoodsId), new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {
                    handlerGetDataFailed(errorType);
                }

                @Override
                public void onResultSuccess() {
                    updateCollectState(false);
                }
            });
        } else {
            mCollectRequest.addColloectRequest(StringUtils.parseInt(mGoodsId), new CollectRequest.CollectRequestResultCallback() {
                @Override
                public void onResultFail(int errorType) {
                    handlerGetDataFailed(errorType);
                }

                @Override
                public void onResultSuccess() {
                    updateCollectState(true);
                }
            });
        }
    }

    private void showProductImagePreview(final List<Banner> banners, int position) {
        PhotoPreviewInfo info = new PhotoPreviewInfo();
        info.position = position;
        ArrayList<String> photoList = new ArrayList<>(banners.size());
        for (Banner banner : banners) {
            photoList.add(banner.getImageUrl());
        }
        info.photoList = photoList;
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW;
        msg.obj = info;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void setupBannerData(final List<Banner> banners) {
        if (banners.size() > 0) {
            mImageCycleView.setImageResources(createBannerImageInfoList(banners), new ImageCycleView
                    .ImageCycleListener() {
                @Override
                public void onImageClick(int position, View imageView) {
                    showProductImagePreview(banners, position);
                }
            });
            mImageCycleView.stopImageCycle();
        } else {
            mImageCycleView.setVisibility(View.GONE);
        }
    }

    private ArrayList<ImageCycleView.ImageInfo> createBannerImageInfoList(List<Banner> banners) {
        ArrayList<ImageCycleView.ImageInfo> infoList = new ArrayList<>();
        ImageCycleView.ImageInfo info;
        for (Banner banner : banners) {
            info = new ImageCycleView.ImageInfo();
            info.title = banner.getTitle();
            info.url = banner.getImageUrl();
            infoList.add(info);
        }
        return infoList;
    }

    @Override
    public void onSuccessForDialogId(DialogIDBean bean) {
        if (mDetailBean == null) {
            return;
        }
        MessageDialogs dialogs = new MessageDialogs();
        dialogs.setDialogId(bean.getData().dialogId);
        dialogs.setUserId(bean.getData().toUserId);
        dialogs.setUserRoleId(Constant.TA_SHE_SECRETARY);
        LetterChatParamBean letterChatParamBean = new LetterChatParamBean();
        letterChatParamBean.setImageUrl(mDetailBean.getThumbUrl());
        letterChatParamBean.setPrice(String.valueOf(mDetailBean.getSalePrice()));
        letterChatParamBean.setSpId(String.valueOf(mDetailBean.getGoodsId()));
        letterChatParamBean.setTitle(mDetailBean.getTitle());
        letterChatParamBean.setLetter(dialogs);
        Message message = Message.obtain();
        message.obj = letterChatParamBean;
        message.what = MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHOPPING_CART) {

            //统计PRODUCT_CART
            StatsModel.stats(StatsKeyDef.PRODUCT_CART, "from", "product");

            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_SHOPPING_CAR_WINDOW;
            MsgDispatcher.getInstance().sendMessage(message);
        } else if (itemId == MenuItemIdDef.TITLEBAR_SHARE) {
            shareAction();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            tryGetProductDetailData();
            sendGetShoppingCarCountRequest();
            NotificationCenter.getInstance().register(this, NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE);
        } else if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE);
            mCountdownView.stopCountdown();
            mImageCycleView.stopImageCycle();
            mWebView.destroy();
            CommonUtils.gc();
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE) {
            sendGetShoppingCarCountRequest();
        }
    }

    @Override
    public void OnClick() {
        tryGetProductDetailData();
        sendGetShoppingCarCountRequest();
    }

    private void tryGetProductDetailData() {
        mShoppingRequest.getShoppingGoodsDetail(mGoodsId, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj == null) {
                    handlerGetDataFailed(CommonConst.ERROR_TYPE_DATA);
                    return;
                }
                JTBShoppingGoodsDetail detail = (JTBShoppingGoodsDetail) obj;

                handleGetDataSuccess(detail.getData());
            }

            @Override
            public void onError(String errorStr, int code) {
                handlerGetDataFailed(CommonConst.ERROR_TYPE_NETWORK);
            }
        });
    }

    private void sendGetShoppingCarCountRequest() {
        final TitleBarActionItem item = getTitleBarInner().getActionBar().getItem(MenuItemIdDef.TITLEBAR_SHOPPING_CART);
        new ShoppingCarRequest().startGetShoppingCarCountRequest(new ShoppingCarRequest.ShoppingCarCountResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(errorType);
                setItems(item, 0);
            }

            @Override
            public void onResultSuccess(int count) {
                setItems(item, count);
            }
        });
    }

    private void setItems(TitleBarActionItem item, int count) {
        item.setRedTipVisibility(count == 0 ? false : true);
        item.setRedTipText(count + "");
    }

    private void updateBanners(ShoppingGoodsDetailBean detailBean) {
        mLoadingLayout.hide();
        List<ShoppingGoodsImageUrlsBean> bannerImages = detailBean.getImageUrls();
        if (bannerImages != null) {
            List<Banner> bannerList = new ArrayList<>();
            for (ShoppingGoodsImageUrlsBean image : bannerImages) {
                Banner banner = new Banner();
                banner.setImageUrl(image.getImageUrl());
                bannerList.add(banner);
            }
            setupBannerData(bannerList);
        }
    }

    private void updatePriceInfo(ShoppingGoodsDetailBean detailBean) {
        mDiscountPrice.setText(mDetailBean.getSalePrice());
        mOriginalPrice.setText(mDetailBean.getMarketPrice());
        mBuyer.setText(String.valueOf(mDetailBean.getSaleCount()));
        int postageType = StringUtils.parseInt(detailBean.getPostageType());
        if (postageType == ShoppingConstant.POSTAGE_TYPE_FULL) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_manbaoyo);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mWelfare.setCompoundDrawables(drawable, null, null, null);
        } else if (postageType == ShoppingConstant.POSTAGE_TYPE_GENERAL) {
            Drawable drawable = getResources().getDrawable(R.drawable.shopping_baoyou);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mWelfare.setCompoundDrawables(drawable, null, null, null);
        }
        mWelfare.setText(detailBean.getPostageDes());
        int panicMark = StringUtils.parseInt(detailBean.getIsPanicShopping());
        if (panicMark == ShoppingConstant.PANIC_TYPE_TRUE) {
            mPanicTimeView.setVisibility(VISIBLE);
            mCountdownView.setStartTime(StringUtils.parseLong(detailBean.getPanicShopping().getSurplusSeconds()));
            mCountdownView.startCountdown();
        } else if (panicMark == ShoppingConstant.PANIC_TYPE_FALSE) {
            mPanicTimeView.setVisibility(GONE);
        }
    }

    private void updateCommentInfo(ShoppingGoodsDetailBean detailBean) {
        int reviewCount = detailBean.getReviewCount();
        if (reviewCount > 0) {
            mCommentTitleView.setText(ResourceHelper.getString(R.string.shopping_product_comment_info) + "(" + reviewCount + ")");
            ShoppingReviewBean reviewInfo = detailBean.getReview();
            if (reviewInfo == null) {
                mCommentView.setVisibility(GONE);
                return;
            }
            mCommentView.setVisibility(View.VISIBLE);
            mCommentHolder.setupCommentView(reviewInfo);
        } else {
            mCommentTitleView.setText(ResourceHelper.getString(R.string.shopping_product_comment_nothing));
            mCommentView.setVisibility(GONE);
            mCommentMore.setVisibility(View.GONE);
        }
    }

    private void updateCollectState(boolean isCollected) {
        mIsCollected = isCollected;
        if (mIsCollected) {
            Drawable drawable = getResources().getDrawable(R.drawable.product_detail_icon_collected);
            mAddCollect.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            mAddCollect.setTextColor(getResources().getColor(R.color.shopping_collected_text_color));
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.product_detail_icon_collect);
            mAddCollect.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            mAddCollect.setTextColor(getResources().getColor(R.color.shopping_collect_text_color));
        }
    }

    private void handleGetDataSuccess(ShoppingGoodsDetailBean detailBean) {
        mDetailBean = detailBean;
        if (mDetailBean == null) {
            mLoadingLayout.setDefaultNetworkError(true);
            return;
        }

        //统计PRODUCT_DETAIL_VIEW
        StatsModel.stats(StatsKeyDef.PRODUCT_DETAIL_VIEW, "spec", detailBean.getTitle());

        updateBanners(detailBean);
        mTitleContent.setText(mDetailBean.getTitle());
        updatePriceInfo(detailBean);

        if (detailBean.getSku() != null) {
            if (detailBean.getSku().size() == 1) {
                mSpecTitle.setText("已选：" + detailBean.getSku().get(0).getName());
            }
        }

        updateCommentInfo(detailBean);

        final String detailUrl = HttpUrls.PRODUCT_DETAIL + mGoodsId + "&version=" + SystemHelper.getAppInfo().versionName;
        mWebView.loadUrl(detailUrl);

        boolean isCollected = detailBean.getIsCollect() == CommonConst.COLLECT_ADD;
        updateCollectState(isCollected);
    }

    /**
     * 数据异常处理
     *
     * @param errorCode
     */
    private void handlerGetDataFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            if (mDetailBean == null) {
                mLoadingLayout.setDefaultNetworkError(true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            if (mDetailBean == null) {
                mLoadingLayout.setDefaultNetworkError(true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.shopping_network_error_retry));
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_INVALID_TOKEN) {
            return;
        }
    }

    private void shareAction() {
        if(mDetailBean == null){
            return;
        }
        String shareUrl = HttpUrls.H5_URL + "?a=goodsShare&id=" + mGoodsId + "&apiver=" + CommonConst.API_VERSION;
        String shareTitle = mDetailBean.getTitle();
        String shareContent = ResourceHelper.getString(R.string.shopping_product_share_content);
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareUrl(shareUrl);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareTitle(shareTitle);
        builder.setShareContent(shareContent);
        builder.setShareImageUrl(mDetailBean.getThumbUrl());
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);

        Bundle shareBundle = new Bundle();
        shareBundle.putString("type", "product");
        String title = shareTitle;
        if (StringUtils.isEmpty(title)) {
            title = String.valueOf(mGoodsId);
        }
        shareBundle.putString("spec", title);
        builder.setStatsBundle(shareBundle);

        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHARE;
        message.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
