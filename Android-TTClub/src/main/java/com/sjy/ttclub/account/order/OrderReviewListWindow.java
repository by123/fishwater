/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.order;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.order.adapter.OrderReviewRecyclerViewAdapter;
import com.sjy.ttclub.account.order.model.OrderReviewInfo;
import com.sjy.ttclub.bean.shop.ShoppingOrderDetailGoodsBean;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderReviewListWindow extends DefaultWindow {

    private final static String TAG = "OrderReviewListWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    private RecyclerView mRecyclerView;
    private OrderReviewInfo mReviewInfo;

    private OrderReviewRecyclerViewAdapter mAdapter;

    private int ALREADY = 1;

    public OrderReviewListWindow(Context context, IDefaultWindowCallBacks callBacks, OrderReviewInfo reviewInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        mReviewInfo = reviewInfo;

        initUI();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_btn_review));
        View view = View.inflate(mContext, R.layout.order_review_list, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mRecyclerView = (RecyclerView) findViewById(R.id.order_review_list);
        mAdapter = new OrderReviewRecyclerViewAdapter(mContext, mReviewInfo);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void notifyOrderListReload() {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_LIST_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            NotificationCenter.getInstance().register(this, NotificationDef.N_ORDER_REVIEW_PUSH_SUCCESS);
        } else if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ORDER_REVIEW_PUSH_SUCCESS);
        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ORDER_REVIEW_PUSH_SUCCESS) {
            notifyOrderListReload();
            int changeGoodsId = (int) notification.extObj;
            for (int i = 0; i < mReviewInfo.goodsList.size(); i++) {
                ShoppingOrderDetailGoodsBean goodsBean = mReviewInfo.goodsList.get(i);
                if (goodsBean.getOrderGoodsId() == changeGoodsId) {
                    mReviewInfo.goodsList.get(i).setIsReview(ALREADY);
                    mAdapter.notifyItemChanged(i);
                }
            }
//            mCallBacks.onWindowExitEvent(mWindow, false);
        }
    }
}
