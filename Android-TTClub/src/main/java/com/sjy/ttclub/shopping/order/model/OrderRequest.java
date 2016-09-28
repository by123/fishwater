/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order.model;

import android.util.Log;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingAddress;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingDirectBalance;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrder;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrderNum;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderRequest {

    private final static String TAG = "OrderRequest";

    private static OrderRequest mOrderRequest;

    public OrderRequest() {
    }

    public static OrderRequest getInstance() {
        if (mOrderRequest == null) {
            mOrderRequest = new OrderRequest();
        }
        return mOrderRequest;
    }

    /**
     * 从商品直接结算
     */
    public void postBalanceByGoods(OrderBalanceInfo balanceInfo, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "directBalance");
        httpManager.addParams("panicShoppingId", balanceInfo.panicShoppingId + "");
        httpManager.addParams("goodsId", balanceInfo.goodsId + "");
        httpManager.addParams("specId", balanceInfo.specId + "");
        httpManager.addParams("goodsCount", balanceInfo.goodsCount + "");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingDirectBalance.class, callBack);
    }

    /**
     * 从购物车直接结算
     */
    public void postBalanceByCart(OrderBalanceInfo balanceInfo, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "balance");
        httpManager.addParams("data", getJson(balanceInfo.getList()));
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingDirectBalance.class, callBack);
    }

    /**
     * 获取收货人地址
     */
    public void getAddress(HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "getAddress");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingAddress.class, callBack);
    }

    /**
     * 新增地址
     */
    public void addAddress(OrderAddressInfo addressInfo, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "updateAddress");
        httpManager.addParams("receiver", addressInfo.receiver);
        httpManager.addParams("mobile", addressInfo.mobile);
        httpManager.addParams("detailAddr", addressInfo.detailAddress);
        httpManager.addParams("districtId", addressInfo.districtId);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingAddress.class, callBack);
    }

    /**
     * 订单状态的更新
     */
    public void updateOrder(int payType) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "updateOrder");
        httpManager.addParams("orderId", CommonConst.ORDERID);
        if (payType != 0) {
            httpManager.addParams("payType", payType + "");
        }
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, errorStr + " code " + code);
                }
            }
        });
    }

    /**
     * 初始化支付参数
     */
    public void getPayParam(HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "payParam");
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.GET, JTBShoppingOrder.class, callBack);
    }

    /**
     * 从购物车下订单
     */
    public void postOrderByCart(OrderBalanceInfo balanceInfo, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "order");
        httpManager.addParams("totalPrice", balanceInfo.totalPrice);
        httpManager.addParams("goodsPrice", balanceInfo.goodsPrice);
        httpManager.addParams("payType", balanceInfo.payType + "");
        httpManager.addParams("addrId", balanceInfo.addrId + "");
        httpManager.addParams("userRemark", balanceInfo.userRemark);
        httpManager.addParams("data", getJson(balanceInfo.getList()));
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingOrderNum.class, callBack);
    }

    /**
     * 从商品直接下单
     */
    public void postOrderByGoods(OrderBalanceInfo balanceInfo, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "directOrder");
        httpManager.addParams("panicShoppingId", balanceInfo.panicShoppingId + "");
        httpManager.addParams("goodsId", balanceInfo.goodsId + "");
        httpManager.addParams("specId", balanceInfo.specId + "");
        httpManager.addParams("goodsCount", balanceInfo.goodsCount + "");
        httpManager.addParams("totalPrice", balanceInfo.totalPrice);
        httpManager.addParams("goodsPrice", balanceInfo.goodsPrice);
        httpManager.addParams("payType", balanceInfo.payType + "");
        httpManager.addParams("addrId", balanceInfo.addrId + "");
        httpManager.addParams("userRemark", balanceInfo.userRemark);
        JSONArray json = new JSONArray();
        for (ShoppingCarGoodsBean carGoodsBean : balanceInfo.getList()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", carGoodsBean.getItemId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jsonObject);
        }
        httpManager.addParams("data", json.toString());
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingOrderNum.class, callBack);
    }

    private String getJson(List<ShoppingCarGoodsBean> list) {
        JSONArray json = new JSONArray();
        for (ShoppingCarGoodsBean carGoodsBean : list) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", carGoodsBean.getItemId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jsonObject);
        }
        return json.toString();
    }
}
