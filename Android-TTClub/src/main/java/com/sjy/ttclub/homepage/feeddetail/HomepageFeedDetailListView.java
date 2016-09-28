package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.homepage.ArticleComments;
import com.sjy.ttclub.bean.homepage.ArticleDetail;
import com.sjy.ttclub.bean.homepage.ArticleRecommends;
import com.sjy.ttclub.homepage.HomepageCommentsLayout;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.network.HeaderManager;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.web.WebChromeClientBaseImpl;
import com.sjy.ttclub.web.WebViewClientBaseImpl;
import com.sjy.ttclub.web.WebViewJsInterface;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/10/31.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageFeedDetailListView extends ListView implements HomepageWebViewJsInterface.ICallBack {

    public static final int ITEM_TYPE_TOP_LAYOUT = 0;
    public static final int ITEM_TYPE_WEBVIEW = 1;
    public static final int ITEM_TYPE_FLOAT_VIEW = 2;
    public static final int ITEM_TYPE_COMMENTS = 3;
    public static final int ITEM_TYPE_PRODUCT = 4;
    public static final int ITEM_TYPE_RECOMMEND = 5;

    private static final int MAX_TYPE = 6;

    private ArrayList<ItemInfo> mDefaultItemInfo = new ArrayList<ItemInfo>(4);
    private ArrayList<ItemInfo> mItemList = new ArrayList<ItemInfo>(4);

    private FeedAdapter mAdapter;

    private int mFeedType;

    private CustomWebView mWebView;
    private View mTopLayout;
    private FloatViewWrapper mFloatView;

    private IFeedListViewListener mListener;

    public HomepageFeedDetailListView(Context context, int type, IFeedListViewListener listener) {
        super(context);
        mFeedType = type;
        mListener = listener;
        setDividerHeight(1);
        setDivider(new ColorDrawable(Color.WHITE));
        setCacheColorHint(Color.TRANSPARENT);
        setSelector(new ColorDrawable(Color.TRANSPARENT));

        mAdapter = new FeedAdapter();
        setAdapter(mAdapter);

        initDefaultList(HomepageConst.WEB_INDEX_START);

        setBackgroundResource(R.color.white);

    }

    private void initDefaultList(int state) {
        mDefaultItemInfo.clear();
        mItemList.clear();

        ItemInfo info = new ItemInfo();
        info.type = ITEM_TYPE_TOP_LAYOUT;
        if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            if (state == HomepageConst.WEB_INDEX_START) {
                mDefaultItemInfo.add(info);
            }
        } else {
            mDefaultItemInfo.add(info);
        }

        info = new ItemInfo();
        info.type = ITEM_TYPE_WEBVIEW;
        mDefaultItemInfo.add(info);

        if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            if (state == HomepageConst.WEB_INDEX_END) {
                info = new ItemInfo();
                info.type = ITEM_TYPE_FLOAT_VIEW;
                mDefaultItemInfo.add(info);
            }
        } else {
            info = new ItemInfo();
            info.type = ITEM_TYPE_FLOAT_VIEW;
            mDefaultItemInfo.add(info);
        }

        mItemList.addAll(mDefaultItemInfo);
        mAdapter.notifyDataSetChanged();

        if (mFeedType == HomepageConst.FEED_TYPE_TEST && state == HomepageConst.WEB_INDEX_START) {
            smoothScrollToPosition(0);
        }
    }

    public void setListViewListener(IFeedListViewListener listener) {
        mListener = listener;
        if (mFloatView != null) {
            mFloatView.getFloatView().setFloatItemClickListener(listener);
        }
    }

    public void updateWebView(String url) {
        if (mWebView == null) {
            mWebView = createDefaultWebView();
        }
        mWebView.stopLoading();
        mWebView.loadUrl(url, HeaderManager.defaultHeader());
    }

    public void updateFloatView(int id, String text) {
        if (mFloatView == null) {
            mFloatView = createFloatView();
        }
        mFloatView.getFloatView().updateItemText(id, text);
    }

    public void updateFloatViewIcon(int id, int resId) {
        if (mFloatView == null) {
            mFloatView = createFloatView();
        }
        mFloatView.getFloatView().updateItemIcon(id, resId);
    }

    public void loadListView(int feedType, String url) {
        mFeedType = feedType;
        mFloatView = null;
        mTopLayout = null;
        mWebView = null;
        setAdapter(mAdapter);
        smoothScrollToPosition(0);
        initDefaultList(HomepageConst.WEB_INDEX_START);
        updateWebView(url);
    }

    public int getFloatViewPosition() {
        return ITEM_TYPE_FLOAT_VIEW;
    }

    public void updateListView(ArticleDetail detail) {
        updateTopLayout(detail);
        if (mFeedType == HomepageConst.FEED_TYPE_PRODUCT) {
            updateProduct(detail);
        }
    }

    private void updateTopLayout(ArticleDetail detail) {
        if (mTopLayout == null) {
            mTopLayout = createTopLayout();
        }
        String url = null;
        if (detail != null) {
            String imageUrl = detail.getData().getImageUrl();
            if (StringUtils.isNotEmpty(imageUrl)) {
                url = imageUrl;
            }
        }
        if (StringUtils.isEmpty(url)) {
            mTopLayout.setVisibility(View.GONE);
        } else {
            mTopLayout.setVisibility(View.VISIBLE);
            SimpleDraweeView draweeView = (SimpleDraweeView) mTopLayout.findViewById(R.id
                    .homepage_detail_toplayout_image);
            draweeView.setImageURI(Uri.parse(url));
            if (mFeedType == HomepageConst.FEED_TYPE_PIC && detail != null) {
                TextView titleView = (TextView) mTopLayout.findViewById(R.id
                        .homepage_detail_toplayout_title);
                titleView.setText(detail.getData().getTitle());

                TextView numView = (TextView) mTopLayout.findViewById(R.id
                        .homepage_detail_toplayout_photo_num);
                String numText = detail.getData().getImageCount() + " " + getResources().getString(R.string
                        .homepage_pic_num_postfix);
                numView.setText(numText);

            }
        }
    }

    public void updateTopPraise(boolean isPraise, int count) {
        if (mFeedType != HomepageConst.FEED_TYPE_PIC) {
            return;
        }
        if (mTopLayout == null) {
            mTopLayout = createTopLayout();
        }
        TextView praiseView = (TextView) mTopLayout.findViewById(R.id
                .homepage_detail_toplayout_praise);
        int praiseCount = count;
        if (praiseCount < 0) {
            praiseCount = 0;
        }
        if (praiseCount > 9999) {
            praiseCount = 9999;
        }
        if (isPraise && praiseCount == 0) {
            praiseCount = 1;
        }
        praiseView.setText(String.valueOf(praiseCount));

        praiseView.setSelected(isPraise);
    }


    private void updateProduct(ArticleDetail detail) {
        for (ItemInfo tmpInfo : mItemList) {
            if (tmpInfo.type == ITEM_TYPE_PRODUCT) {
                tmpInfo.object = detail;
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
        //插入到float view后面
        int recommendIndex = getItemTypeIndex(ITEM_TYPE_FLOAT_VIEW);
        if (recommendIndex < mItemList.size()) {
            recommendIndex += 1;
        }
        ItemInfo info = new ItemInfo();
        info.type = ITEM_TYPE_PRODUCT;
        info.object = detail;
        mItemList.add(recommendIndex, info);
        mAdapter.notifyDataSetChanged();
    }

    public void updateArticleComments(ArticleComments comments) {
        if (comments.getData() == null || comments.getData().getCommentsCount() <= 0) {
            return;
        }
        for (ItemInfo tmpInfo : mItemList) {
            if (tmpInfo.type == ITEM_TYPE_COMMENTS) {
                tmpInfo.object = comments;
                mAdapter.notifyDataSetChanged();
                return;
            }
        }

        //查找推荐是否已经添加，如果添加了，插入到推荐前面
        int recommendIndex = getItemTypeIndex(ITEM_TYPE_RECOMMEND);
        ItemInfo info = new ItemInfo();
        info.type = ITEM_TYPE_COMMENTS;
        info.object = comments;
        mItemList.add(recommendIndex, info);
        mAdapter.notifyDataSetChanged();
    }

    private int getItemTypeIndex(int type) {
        //查找推荐是否已经添加，如果添加了，插入到推荐前面
        int index = mItemList.size();
        for (int i = index - 1; i >= 0; i--) {
            if (mItemList.get(i).type == type) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    public void updateArticleRecommends(ArticleRecommends recommends) {
        if (recommends.getData() == null || recommends.getData().size() == 0) {
            return;
        }
        for (ItemInfo tmpInfo : mItemList) {
            if (tmpInfo.type == ITEM_TYPE_RECOMMEND) {
                tmpInfo.object = recommends;
                mAdapter.notifyDataSetChanged();
                return;
            }
        }

        ItemInfo info = new ItemInfo();
        info.type = ITEM_TYPE_RECOMMEND;
        info.object = recommends;
        mItemList.add(info);
        mAdapter.notifyDataSetChanged();
    }

    private CustomWebView createDefaultWebView() {
        CustomWebView webView = new CustomWebView(getContext());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClientBaseImpl(mListener));
        webView.setWebChromeClient(new HomepageWebChromeClient(mListener));
        webView.addJavascriptInterface(new HomepageWebViewJsInterface(this), "android");
        return webView;
    }

    private FloatViewWrapper createFloatView() {
        FloatViewWrapper wrapperFloatView = new FloatViewWrapper(getContext());
        ArrayList<HomepageFeedFloatView.FloatItemInfo> itemList = HomepageFeedDetailView.createFloatItems(getContext
                (), mFeedType);
        HomepageFeedFloatView floatView = wrapperFloatView.getFloatView();
        floatView.setFloatItemClickListener(mListener);
        floatView.setupFloatItemView(itemList);
        return wrapperFloatView;
    }

    private View createCommentsView() {
        HomepageCommentsLayout view = new HomepageCommentsLayout(getContext());
        view.setCommentsListener(mListener);
        return view;
    }

    private View createRecommendsView() {
        HomepageRecommendsLayout view = new HomepageRecommendsLayout(getContext());
        view.setRecommendListener(mListener);
        return view;
    }

    private View createProductDetailView() {
        return new HomepageProductDetailView(getContext());
    }

    private View createTopLayout() {
        View view = View.inflate(getContext(), R.layout.homepage_detail_toplayout, null);
        int minHeight;
        if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            minHeight = ResourceHelper.getDimen(R.dimen.homepage_ad_banner_height);
        } else if (mFeedType == HomepageConst.FEED_TYPE_PIC) {
            view.findViewById(R.id.homepage_detail_toplayout_mask).setVisibility(View.VISIBLE);
            view.findViewById(R.id.homepage_detail_toplayout_detail).setVisibility(View.VISIBLE);
            view.findViewById(R.id.homepage_detail_toplayout_praise).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFloatItemClick(HomepageFeedDetailView.FLOAT_ITEM_ID_PARISE);
                }
            });

            view.findViewById(R.id.homepage_detail_toplayout_share).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFloatItemClick(HomepageFeedDetailView.FLOAT_ITEM_ID_SHARE);
                }
            });
            minHeight = ResourceHelper.getDimen(R.dimen.space_460);
        } else {
            minHeight = ResourceHelper.getDimen(R.dimen.homepage_detail_top_image_height);
        }
        view.setMinimumHeight(minHeight);
        view.findViewById(R.id.homepage_detail_toplayout_image).setMinimumHeight(minHeight);

        return view;
    }

    private View createViewByItemInfo(ItemInfo info) {
        View view = null;
        if (info.type == ITEM_TYPE_TOP_LAYOUT) {
            if (mTopLayout == null) {
                mTopLayout = createTopLayout();
            }
            view = mTopLayout;
        } else if (info.type == ITEM_TYPE_WEBVIEW) {
            if (mWebView == null) {
                mWebView = createDefaultWebView();
            }
            view = mWebView;
        } else if (info.type == ITEM_TYPE_FLOAT_VIEW) {
            if (mFloatView == null) {
                mFloatView = createFloatView();
            }
            view = mFloatView;
        } else if (info.type == ITEM_TYPE_COMMENTS) {
            view = createCommentsView();
        } else if (info.type == ITEM_TYPE_RECOMMEND) {
            view = createRecommendsView();
        } else if (info.type == ITEM_TYPE_PRODUCT) {
            view = createProductDetailView();
        }
        return view;
    }

    private void setupCommentsView(HomepageCommentsLayout view, ItemInfo info) {
        if (!(info.object instanceof ArticleComments)) {
            return;
        }
        view.setupCommentsLayout((ArticleComments) info.object);
    }

    private void setupRecommendsView(HomepageRecommendsLayout view, ItemInfo info) {
        if (!(info.object instanceof ArticleRecommends)) {
            return;
        }
        view.setupRecommendView((ArticleRecommends) info.object);
    }

    private void setupProductView(HomepageProductDetailView view, ItemInfo info) {
        if (!(info.object instanceof ArticleDetail)) {
            return;
        }
        ArticleDetail productDetail = (ArticleDetail) info.object;
        view.setupProductView(productDetail);
    }

    private void setupViewByItemInfo(View view, ItemInfo info) {
        if (info.type == ITEM_TYPE_PRODUCT) {
            setupProductView((HomepageProductDetailView) view, info);
        } else if (info.type == ITEM_TYPE_COMMENTS) {
            setupCommentsView((HomepageCommentsLayout) view, info);
        } else if (info.type == ITEM_TYPE_RECOMMEND) {
            setupRecommendsView((HomepageRecommendsLayout) view, info);
        }
    }

    @Override
    public void onTestStateChanged(final int state, final String result) {
        if (mListener != null) {
            mListener.onTestStateChanged(state, result);
        }
        initDefaultList(state);
    }

    @Override
    public void onGetImagesFinished(ArrayList<String> list) {
        if (mListener != null) {
            mListener.onGetImagesFinished(list);
        }
    }

    private class FeedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= 0 && position < mItemList.size()) {
                return mItemList.get(position).type;
            }
            return ITEM_TYPE_TOP_LAYOUT;
        }

        @Override
        public int getViewTypeCount() {
            return MAX_TYPE;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= 0 && position < mItemList.size()) {
                ItemInfo info = mItemList.get(position);
                if (convertView == null) {
                    convertView = createViewByItemInfo(info);
                }
                setupViewByItemInfo(convertView, info);
            } else {
                if (convertView == null) {
                    convertView = createTopLayout();
                }
            }
            convertView.setBackgroundColor(getResources().getColor(R.color.white));
            return convertView;
        }
    }

    private class FloatViewWrapper extends FrameLayout {
        private HomepageFeedFloatView mInnerFloatView;

        public FloatViewWrapper(Context context) {
            super(context);
            mInnerFloatView = new HomepageFeedFloatView(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_50);
            lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.space_40);
            this.addView(mInnerFloatView, lp);
        }

        public HomepageFeedFloatView getFloatView() {
            return mInnerFloatView;
        }


    }

    private class CustomWebView extends WebView {
        private WebViewClient mWebviewClient;

        public CustomWebView(Context context) {
            super(context);
        }

        @Override
        public void setWebViewClient(WebViewClient client) {
            mWebviewClient = client;
            super.setWebViewClient(client);
        }
    }

    private class ItemInfo {
        public int type;
        public Object object;
    }

    public interface IFeedListViewListener extends HomepageFeedFloatView.OnFloatItemClickListener,
            HomepageRecommendsLayout.RecommendListener, HomepageCommentsLayout.CommentsListener,
            WebViewClientBaseImpl.WebViewClientCallback, WebChromeClientBaseImpl.WebChromeClientCallback,
            WebViewJsInterface.JsInterfaceCallback {
        void onTestStateChanged(int state, String result);
    }
}
