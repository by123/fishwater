/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.order;

import android.os.Message;

import com.sjy.ttclub.account.order.model.OrderReviewInfo;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderListController extends DefaultWindowController {

    private final static String TAG = "OrderListController";

    public OrderListController() {
        registerMessage(MsgDef.MSG_SHOW_ORDER_LIST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_ORDER_REVIEW_LIST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_ORDER_REVIEW_WINDOW);
    }

    private void showOrderListWindow() {
        OrderListWindow orderAddressWindow = new OrderListWindow(mContext, this);
        mWindowMgr.pushWindow(orderAddressWindow);
    }

    private void showOrderReviewWindow(OrderReviewInfo reviewInfo) {
        OrderReviewWindow reviewWindow = new OrderReviewWindow(mContext, this, reviewInfo);
        mWindowMgr.pushWindow(reviewWindow);
    }

    private void showOrderReviewListWindow(OrderReviewInfo reviewInfo) {
        OrderReviewListWindow reviewListWindow = new OrderReviewListWindow(mContext, this, reviewInfo);
        mWindowMgr.pushWindow(reviewListWindow);
    }

    @Override
    public void handleMessage(Message msg) {
        if (MsgDef.MSG_SHOW_ORDER_LIST_WINDOW == msg.what) {
            showOrderListWindow();
        } else if (MsgDef.MSG_SHOW_ORDER_REVIEW_LIST_WINDOW == msg.what) {
            showOrderReviewListWindow((OrderReviewInfo) msg.obj);
        } else if (MsgDef.MSG_SHOW_ORDER_REVIEW_WINDOW == msg.what) {
            showOrderReviewWindow((OrderReviewInfo) msg.obj);
        }
    }
}
