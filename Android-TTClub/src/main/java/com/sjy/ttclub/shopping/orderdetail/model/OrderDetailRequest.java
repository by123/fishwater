/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.orderdetail.model;

import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrderDetail;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrderResult;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderDetailRequest {

    public final static String ACTION_CANCEL = "cancelOrder";
    public final static String ACTION_DELETE = "deleteOrder";
    public final static String ACTION_CONFIRM = "confirmOrder";

    private static OrderDetailRequest mOrderRequest;

    public OrderDetailRequest() {
    }

    public static OrderDetailRequest getInstance() {
        if (mOrderRequest == null) {
            mOrderRequest = new OrderDetailRequest();
        }
        return mOrderRequest;
    }

    /**
     * 获取订单详情
     */
    public void getOrderDetail(String orderId, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "orderDetail");
        httpManager.addParams("orderId", orderId);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingOrderDetail.class, callBack);
    }

    /**
     * 订单取消，删除，确认
     */
    public void postOrderState(String action, String orderId, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", action);
        httpManager.addParams("orderId", orderId);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingOrderResult.class, callBack);
    }
}
