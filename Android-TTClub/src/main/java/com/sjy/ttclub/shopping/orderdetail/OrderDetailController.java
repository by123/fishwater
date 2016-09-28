/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.orderdetail;

import android.os.Message;

import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderDetailController extends DefaultWindowController {

    private final static String TAG = "OrderDetailController";

    public OrderDetailController() {
        registerMessage(MsgDef.MSG_SHOW_ORDER_DETAIL_WINDOW);
    }

    private void showOrderDetailWindow(OrderResultInfo resultInfo) {
        OrderDetailWindow orderDetailWindow = new OrderDetailWindow(mContext, this, resultInfo);
        mWindowMgr.pushWindow(orderDetailWindow);
    }

    @Override
    public void handleMessage(Message msg) {
        if (MsgDef.MSG_SHOW_ORDER_DETAIL_WINDOW == msg.what) {
            showOrderDetailWindow((OrderResultInfo) msg.obj);
        }
    }
}
