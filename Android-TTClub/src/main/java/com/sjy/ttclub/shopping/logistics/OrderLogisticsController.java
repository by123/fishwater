/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.logistics;

import android.os.Message;

import com.sjy.ttclub.bean.shop.ShoppingOrderDetailBean;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderLogisticsController extends DefaultWindowController {

    private final static String TAG = "OrderLogisticsController";

    public OrderLogisticsController() {
        registerMessage(MsgDef.MSG_SHOW_ORDER_LOGISTICS_WINDOW);
    }

    private void showOrderLogisticsWindow(ShoppingOrderDetailBean detailBean) {
        OrderLogisticsWindow orderLogisticsWindow = new OrderLogisticsWindow(mContext, this, detailBean);
        mWindowMgr.pushWindow(orderLogisticsWindow);
    }

    @Override
    public void handleMessage(Message msg) {
        if (MsgDef.MSG_SHOW_ORDER_LOGISTICS_WINDOW == msg.what) {
            showOrderLogisticsWindow((ShoppingOrderDetailBean) msg.obj);
        }
    }
}
