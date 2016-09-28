/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.cundong.recyclerview.EndlessRecyclerOnScrollListener;
import com.cundong.recyclerview.ExStaggeredGridLayoutManager;
import com.cundong.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.cundong.recyclerview.HeaderSpanSizeLookup;
import com.cundong.recyclerview.RecyclerViewUtils;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.bean.shop.ShoppingNewTopicBean;
import com.sjy.ttclub.bean.shop.ShoppingTopicListBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingTopicList;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.shopping.adapter.ShoppingTopicRecyclerViewAdapter;
import com.sjy.ttclub.shopping.model.ShoppingRequest;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.shopping.utils.ShoppingRecyclerViewStateUtils;
import com.sjy.ttclub.shopping.widget.ShoppingLoadingFooter;
import com.sjy.ttclub.shopping.widget.ShoppingTopicBanner;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class ShoppingTopicWindow extends DefaultWindow implements PtrHandler, LoadingLayout.OnBtnClickListener {

    private final static int TYPE_LIST = 0;
    private final static int TYPE_STAGGER = 1;
    private int mCurrMode = TYPE_STAGGER;

    private Activity mContext;
    private ShoppingTopicInfo mTopicInfo;

    private ShoppingRequest mShoppingRequest;

    private ExStaggeredGridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLayoutManager;
    private LoadingLayout mLoadingLayout;

    private RecyclerView mRecyclerView;
    private PtrClassicFrameLayout mPtrLayout;
    private ShoppingTopicRecyclerViewAdapter mAdapter;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private List<ShoppingTopicListBean> mTopicList = new ArrayList<>();
    private ShoppingTopicBanner mTopicBanner;
    private ArrayList<Banner> mBanner = new ArrayList<>();
    private TitleBarActionItem mActionItem;

    private int mCurrPage = 1;

    public ShoppingTopicWindow(Context context, IDefaultWindowCallBacks callBacks, ShoppingTopicInfo topicInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mTopicInfo = topicInfo;

        initActionBar();
        initUI();

        //PRODUCT_COLLECTION
        StatsModel.stats(StatsKeyDef.PRODUCT_COLLECTION, "spec", topicInfo.title);
    }

    private void initUI() {
        String title = mTopicInfo.title;
        if (StringUtils.isEmpty(title)) {
            title = ResourceHelper.getString(R.string.shopping_topic_window_default_title);
        }
        setTitle(title);
        View view = View.inflate(mContext, R.layout.shopping_topic, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoadingLayout = (LoadingLayout) findViewById(R.id.shopping_topic_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mPtrLayout = (PtrClassicFrameLayout) findViewById(R.id.shopping_topic_list_ptr);
        final TTRefreshHeader header = new TTRefreshHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPtrLayout.setVisibility(View.VISIBLE);
        mPtrLayout.setLoadingMinTime(1000);
        mPtrLayout.setDurationToCloseHeader(800);
        mPtrLayout.setHeaderView(header);
        mPtrLayout.addPtrUIHandler(header);
        mPtrLayout.setPullToRefresh(false);
        mPtrLayout.isKeepHeaderWhenRefresh();
        mPtrLayout.setPtrHandler(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.shopping_topic_list);
        mAdapter = new ShoppingTopicRecyclerViewAdapter(mRecyclerView, mTopicList);
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnScrollListener(mOnScrollListener);

        mTopicBanner = new ShoppingTopicBanner(mContext);
        RecyclerViewUtils.setHeaderView(mRecyclerView, mTopicBanner);
        Banner banner = new Banner();
        mBanner.add(banner);

        loadTemplate(TYPE_STAGGER);

//        SpacesItemDecoration decoration = new SpacesItemDecoration(ResourceHelper.getDimen(R.dimen.space_10));
//        mRecyclerView.addItemDecoration(decoration);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        mActionItem = new TitleBarActionItem(getContext());
        mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_view));
        mActionItem.setItemId(MenuItemIdDef.TITLEBAR_SHOPPING_TEMP);
        actionList.add(mActionItem);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    /**
     * 切换模板
     *
     * @param temp
     */
    private void loadTemplate(int temp) {

        mCurrMode = temp;
        mAdapter.switchMode(temp);
        mOnScrollListener.switchMode(temp);

        if (temp == TYPE_STAGGER) {
            if (mGridLayoutManager == null) {
                mGridLayoutManager = new ExStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mGridLayoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup((HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter(), mGridLayoutManager.getSpanCount()));
            }
            mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_view));
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        } else if (temp == TYPE_LIST) {
            if (mLayoutManager == null) {
                mLayoutManager = new LinearLayoutManager(mContext);
            }
            mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_layout));
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            ShoppingLoadingFooter.State state = ShoppingRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
            if (state == ShoppingLoadingFooter.State.Loading) {
                return;
            }
            showLoadView(ShoppingLoadingFooter.State.Loading);
            loadData(false);
        }
    };

    private void showLoadView(ShoppingLoadingFooter.State state) {
        if (state == ShoppingLoadingFooter.State.Normal) {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mRecyclerView, state);
        } else {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mContext, mRecyclerView, 10, state);
        }
    }

    /**
     * @param isClear 清空List 重复加载接口数据
     */
    private void loadData(boolean isClear) {
        if (!isClear) {
            mCurrPage++;
        } else {
            mCurrPage = 1;
        }
        mShoppingRequest = ShoppingRequest.getInstance();
        getShoppingTopicList(mTopicInfo.columnId, mCurrPage, isClear);
    }

    private void notifyItems(boolean isRemove) {
        int headerCount = mHeaderAndFooterRecyclerViewAdapter.getHeaderViewsCount();
        int listSize = mTopicList.size();
        if (isRemove) {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeRemoved(headerCount, listSize);
        } else {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemInserted(headerCount + listSize);
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHOPPING_TEMP) {
            if (mCurrMode == TYPE_STAGGER) {
                loadTemplate(TYPE_LIST);
            } else {
                loadTemplate(TYPE_STAGGER);
            }
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        loadData(true);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            loadData(true);
        }
    }

    @Override
    public void OnClick() {
        loadData(true);
    }

    /*******************
     * 接口访问
     ********************/
    private void getShoppingTopicList(String columnId, int page, final boolean isClear) {
        mShoppingRequest.getShoppingTopicList(columnId, page, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }

                if (isClear) {
                    notifyItems(true);
                    mTopicList.clear();
                }
                mLoadingLayout.hide();
                mPtrLayout.refreshComplete();
                showLoadView(ShoppingLoadingFooter.State.Normal);

                JTBShoppingTopicList topicList = (JTBShoppingTopicList) obj;
                ShoppingNewTopicBean newTopicBean = topicList.getData();

                if (newTopicBean != null) {//顶部Banner
                    mBanner.get(0).setImageUrl(newTopicBean.getHeadImageUrl());
                    mTopicBanner.setupBanner(mBanner);
                } else {
                    mTopicBanner.setVisibility(GONE);
                }

                if (newTopicBean.getGoods().size() == 0) {
                    showLoadView(ShoppingLoadingFooter.State.TheEnd);
                } else {
                    notifyItems(false);
                    mTopicList.addAll(newTopicBean.getGoods());
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                netWorkError(code);
            }
        });
    }

    private void netWorkError(int code) {
        mPtrLayout.refreshComplete();
        if (mTopicList.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }
}
