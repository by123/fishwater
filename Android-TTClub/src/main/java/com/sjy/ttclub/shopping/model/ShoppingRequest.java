/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.model;

import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingAddress;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCart;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCategoryList;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCategory;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingDirectBalance;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingGoodsDetail;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingMain;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingReviewsList;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingTopicList;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.shopping.order.model.OrderBalanceInfo;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class ShoppingRequest {

    public final static String MULTI = "multi";
    public final static String TIME = "time";
    public final static String SALE = "sale";
    public final static String PRICEPLUS = "pricePlus";
    public final static String PRICEMINUS = "priceMinus";

    public final static String ALL = "multi";
    public final static String SATISFIED = "time";
    public final static String GENERAL = "sale";
    public final static String UNSATISFIED = "unsatisfied";

    private static ShoppingRequest mShoppingRequest;

    public ShoppingRequest() {
    }

    public static ShoppingRequest getInstance() {
        if (mShoppingRequest == null) {
            mShoppingRequest = new ShoppingRequest();
        }
        return mShoppingRequest;
    }

    /**
     * 获取分类
     */
    public void getShoppingCategory(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "category");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingCategory.class, callBack);
    }

    /**
     * 获取首页
     */
    public void getShoppingMain(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "welfarePage");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, JTBShoppingMain.class, callBack);
    }

    /**
     * 获取分类列表
     */
    public void getShoppingCategoryList(String cateId, String cateId2, String brandId, int page, String sortType, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "commonCate");
        httpManager.addParams("cateId", cateId);
        if (!StringUtils.isEmpty(cateId2)) {
            httpManager.addParams("cateId2", cateId2);
        }
        if (!StringUtils.isEmpty(brandId)) {
            httpManager.addParams("brandId", brandId);
        }
        httpManager.addParams("page", page + "");
        httpManager.addParams("pageSize", "10");
        if (!StringUtils.isEmpty(sortType)) {
            httpManager.addParams("sortType", sortType);
        } else {
            httpManager.addParams("sortType", MULTI);
        }
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingCategoryList.class, callBack);
    }

    /**
     * 获取专题列表
     */
    public void getShoppingTopicList(String columnId, int page, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "topicCate");
        httpManager.addParams("columnId", columnId);
        httpManager.addParams("page", page + "");
        httpManager.addParams("pageSize", "10");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingTopicList.class, callBack);
    }

    /**
     * 获取商品详情
     */
    public void getShoppingGoodsDetail(String goodsId, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "goodsDetail");
        httpManager.addParams("goodsId", goodsId);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingGoodsDetail.class, callBack);
    }

    /**
     * 获取商品评论列表
     */
    public void getShoppingReviewList(String goodsId, String endId, String type, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "goodsReviews");
        httpManager.addParams("goodsId", goodsId);
        httpManager.addParams("type", type);
        httpManager.addParams("endId", endId);
        httpManager.addParams("pageSize", "10");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, JTBShoppingReviewsList.class, callBack);
    }

    /**
     * 获取商品评论列表
     */
    public void getShoppingReviewList(String goodsId, String endId, IHttpCallBack callBack) {
        getShoppingReviewList(goodsId, endId, ALL, callBack);
    }

    /**
     * 发布商品评价
     */
    public void postGoodsReview(String orderGoodsId, String content, String rating, boolean isAnonymous, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "postGoodsReview");
        httpManager.addParams("postGoodsReview", orderGoodsId);
        httpManager.addParams("content", content);
        httpManager.addParams("rating", rating);
        String anonymous = isAnonymous ? "1" : "0";
        httpManager.addParams("isAnony", anonymous);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, JTBShoppingReviewsList.class, callBack);
    }

    /**
     * 刷新购物车数量
     */
    public void getShoppingCart(IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartCount");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.GET, JTBShoppingCart.class, callBack);
    }
}
