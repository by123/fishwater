package com.sjy.ttclub.account.order.model;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrders;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhxu on 2016/1/7.
 * Email:357599859@qq.com
 */
public class OrderListRequest {

    private static OrderListRequest mOrderListRequest;

    public OrderListRequest() {
    }

    public static OrderListRequest getInstance() {
        if (mOrderListRequest == null) {
            mOrderListRequest = new OrderListRequest();
        }
        return mOrderListRequest;
    }

    /**
     * 获取订单列表
     */
    public void getOrderList(int page, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "orderList");
        httpManager.addParams("type", "2");
        httpManager.addParams("page", page + "");
        httpManager.addParams("pageSize", "10");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingOrders.class, callBack);
    }

    /**
     * 发布商品评价
     */
    public void postGoodsReview(OrderReviewInfo reviewInfo, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "postGoodsReview");
        httpManager.addParams("orderGoodsId", reviewInfo.orderGoodsId + "");
        httpManager.addParams("content", reviewInfo.content);
        httpManager.addParams("rating", reviewInfo.rating + "");
        httpManager.addParams("isAnony", reviewInfo.isAnony + "");
        httpManager.addParams("orderId", reviewInfo.orderId + "");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, BaseBean.class, callBack);
    }
}
