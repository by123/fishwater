package com.sjy.ttclub.bean.shop;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/21.
 * Email:357599859@qq.com
 */
public class ShoppingPanicBean implements Serializable {

    private int panicShoppingId;
    private String surplusSeconds;
    private List<ShoppingGoodsBean> goods;

    public int getPanicShoppingId() {
        return panicShoppingId;
    }

    public void setPanicShoppingId(int panicShoppingId) {
        this.panicShoppingId = panicShoppingId;
    }

    public String getSurplusSeconds() {
        return surplusSeconds;
    }

    public void setSurplusSeconds(String surplusSeconds) {
        this.surplusSeconds = surplusSeconds;
    }

    public List<ShoppingGoodsBean> getGoods() {
        return goods;
    }

    public void setGoods(List<ShoppingGoodsBean> goods) {
        this.goods = goods;
    }
}
