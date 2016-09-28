/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.order;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cundong.recyclerview.EndlessRecyclerOnScrollListener;
import com.cundong.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.order.adapter.OrderRecyclerViewAdapter;
import com.sjy.ttclub.account.order.model.OrderListRequest;
import com.sjy.ttclub.bean.shop.ShoppingOrderListBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrders;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.shopping.utils.ShoppingRecyclerViewStateUtils;
import com.sjy.ttclub.shopping.widget.ShoppingLoadingFooter;
import com.sjy.ttclub.util.ResourceHelper;
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
public class OrderListWindow extends DefaultWindow implements PtrHandler, LoadingLayout.OnBtnClickListener {

    private final static String TAG = "OrderListWindow";

    public final static int TYPE_LIST = 0;

    private Activity mContext;

    private OrderListRequest mOrderListRequest;

    private int mCurrPage = 1;

    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private OrderRecyclerViewAdapter mAdapter;
    private List<ShoppingOrderListBean> mOrderList = new ArrayList<>();

    public PtrClassicFrameLayout mPtrLayout;
    private LinearLayoutManager mLayoutManager;
    private LoadingLayout mLoadingLayout;

    public OrderListWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        mContext = (Activity) context;

        initUI();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_list));
        View view = View.inflate(mContext, R.layout.order_list, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoadingLayout = (LoadingLayout) findViewById(R.id.order_list_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mPtrLayout = (PtrClassicFrameLayout) findViewById(R.id.order_list_ptr);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.order_list);
        mAdapter = new OrderRecyclerViewAdapter(mContext, mOrderList);
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnScrollListener(mOnScrollListener);
        loadTemplate(TYPE_LIST);
    }

    /**
     * 加载模板
     *
     * @param temp
     */
    private void loadTemplate(int temp) {
        mOnScrollListener.switchMode(temp);

        if (temp == TYPE_LIST) {
            if (mLayoutManager == null) {
                mLayoutManager = new LinearLayoutManager(mContext);
            }
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    public void showLoadView(ShoppingLoadingFooter.State state) {
        if (state == ShoppingLoadingFooter.State.Normal) {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mRecyclerView, state);
        } else {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mContext, mRecyclerView, 10, state);
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

    /**
     * @param isClear 清空List 重复加载接口数据
     */
    public void loadData(boolean isClear) {
        if (!isClear) {
            mCurrPage++;
        } else {
            mCurrPage = 1;
        }
        getOrderList(mCurrPage, isClear);
    }

    private void notifyItems(boolean isRemove) {
        int headerCount = mHeaderAndFooterRecyclerViewAdapter.getHeaderViewsCount();
        if (isRemove) {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeRemoved(headerCount, mOrderList.size());
        } else {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemInserted(headerCount + mOrderList.size());
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            NotificationCenter.getInstance().register(this, NotificationDef.N_ORDER_LIST_CHANGED);
            loadData(true);
        } else if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ORDER_LIST_CHANGED);
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
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ORDER_LIST_CHANGED) {
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
    private void getOrderList(int page, final boolean isClear) {
        mOrderListRequest = OrderListRequest.getInstance();
        mOrderListRequest.getOrderList(page, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }

                if (isClear) {
                    notifyItems(true);
                    mOrderList.clear();
                }

                mLoadingLayout.hide();
                mPtrLayout.refreshComplete();
                showLoadView(ShoppingLoadingFooter.State.Normal);

                JTBShoppingOrders orders = (JTBShoppingOrders) obj;
                List<ShoppingOrderListBean> orderList = orders.getData().getOrder();
                if (orderList == null) {
                    showLoadView(ShoppingLoadingFooter.State.TheEnd);
                } else {
                    notifyItems(false);
                    mOrderList.addAll(orderList);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                netWorkError(code);
            }
        });
    }

    private void netWorkError(int code) {
        mPtrLayout.refreshComplete();
        showLoadView(ShoppingLoadingFooter.State.Normal);
        if (mOrderList.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }
}
