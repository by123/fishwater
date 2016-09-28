/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order;

import android.os.Message;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.bean.shop.ShoppingOrderBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrder;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.shopping.order.model.OrderAddressInfo;
import com.sjy.ttclub.shopping.order.model.OrderBalanceInfo;
import com.sjy.ttclub.shopping.order.model.OrderRequest;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderController extends DefaultWindowController {

    private final static String TAG = "OrderController";

    public OrderController() {
        registerMessage(MsgDef.MSG_SHOW_ORDER_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_ORDER_SUCCESS_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_ORDER_FAILED_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_ORDER_ADDRESS_WINDOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_APP_MD5_CHANGED);
    }

    private void showOrderAddressWindow(OrderAddressInfo addressInfo) {
        OrderAddressWindow orderAddressWindow = new OrderAddressWindow(mContext, this, addressInfo);
        mWindowMgr.pushWindow(orderAddressWindow);
    }

    private void showOrderFailedWindow() {
        OrderFailedWindow orderFailedWindow = new OrderFailedWindow(mContext, this);
        mWindowMgr.pushWindow(orderFailedWindow);
    }

    private void showOrderSuccessWindow() {
        OrderSuccessWindow orderSuccessWindow = new OrderSuccessWindow(mContext, this);
        mWindowMgr.pushWindow(orderSuccessWindow);
    }

    private void showOrderWindow(OrderBalanceInfo orderBalanceInfo) {
        OrderWindow orderWindow = new OrderWindow(mContext, this, orderBalanceInfo);
        mWindowMgr.pushWindow(orderWindow);
    }

    @Override
    public void handleMessage(Message msg) {
        if (MsgDef.MSG_SHOW_ORDER_WINDOW == msg.what) {
            showOrderWindow((OrderBalanceInfo) msg.obj);
        } else if (MsgDef.MSG_SHOW_ORDER_SUCCESS_WINDOW == msg.what) {
            showOrderSuccessWindow();
        } else if (MsgDef.MSG_SHOW_ORDER_FAILED_WINDOW == msg.what) {
            showOrderFailedWindow();
        } else if (MsgDef.MSG_SHOW_ORDER_ADDRESS_WINDOW == msg.what) {
            showOrderAddressWindow((OrderAddressInfo) msg.obj);
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_APP_MD5_CHANGED) {
            AppMd5Helper.Md5Type type = (AppMd5Helper.Md5Type) notification.extObj;
            if (type == AppMd5Helper.Md5Type.PAY) {
                getPayParam();
            }
        }
    }

    /**
     * 支付参数
     */
    private void getPayParam() {
        OrderRequest request = OrderRequest.getInstance();
        request.getPayParam(new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                ShoppingOrderBean shoppingOrder = ((JTBShoppingOrder) obj).getData();
                CommonConst.setAliPayPartner(mContext, shoppingOrder.getAlipayPartner());
                CommonConst.setAliPaySeller(mContext, shoppingOrder.getAlipaySeller());
                CommonConst.setAliPayUrl(mContext, shoppingOrder.getAlipayCallbackUrl());
                CommonConst.setGoodsDescription(mContext, shoppingOrder.getGoodsDescription());
                CommonConst.setGoodsTitle(mContext, shoppingOrder.getGoodsTitle());
                CommonConst.setRSAPrivate(mContext, shoppingOrder.getAlipayRsaPrivate());
                CommonConst.setRSAPublic(mContext, shoppingOrder.getAlipayRsaPublic());
                CommonConst.setWechatAPPID(mContext, shoppingOrder.getWeixinAppId());
                CommonConst.setWechatKey(mContext, shoppingOrder.getWeixinApiKey());
                CommonConst.setWechatMCHID(mContext, shoppingOrder.getWeixinMchId());
                CommonConst.setWeChatUrl(mContext, shoppingOrder.getWeixinCallbackUrl());
            }
        });
    }
}
