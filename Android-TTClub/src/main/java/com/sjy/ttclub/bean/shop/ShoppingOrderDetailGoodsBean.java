package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2016/1/8.
 * Email:357599859@qq.com
 */
public class ShoppingOrderDetailGoodsBean implements Serializable {

    private int goodsId;
    private int goodsCount;
    private String goodsPrice;
    private int orderGoodsId;
    private int isReview;
    private int isPanicShopping;
    private int logisticsId;
    private String title;
    private String thumbUrl;
    private String specName;

    public int getGoodsId() {
        return goodsId;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public int getOrderGoodsId() {
        return orderGoodsId;
    }

    public int getIsReview() {
        return isReview;
    }

    public void setIsReview(int isReview) {
        this.isReview = isReview;
    }

    public int getIsPanicShopping() {
        return isPanicShopping;
    }

    public int getLogisticsId() {
        return logisticsId;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getSpecName() {
        return specName;
    }
}
