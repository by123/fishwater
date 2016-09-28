package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2016/1/4.
 * Email:357599859@qq.com
 */
public class ShoppingBalanceBean implements Serializable {

    private String goodsPrice;
    private String postageFee;
    private String totalPrice;

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public String getPostageFee() {
        return postageFee;
    }

    public String getTotalPrice() {
        return totalPrice;
    }
}
