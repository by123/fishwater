package com.sjy.ttclub.account.order.model;

import com.sjy.ttclub.bean.shop.ShoppingOrderDetailGoodsBean;

import java.util.List;

/**
 * Created by zhxu on 2016/1/11.
 * Email:357599859@qq.com
 */
public class OrderReviewInfo {
    public String goodsImg;
    public int orderGoodsId;
    public String content;
    public float rating;
    public int isAnony;
    public int orderId;
    public List<ShoppingOrderDetailGoodsBean> goodsList;
}
