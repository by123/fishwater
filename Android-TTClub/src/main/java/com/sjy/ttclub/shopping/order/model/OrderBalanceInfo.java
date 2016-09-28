package com.sjy.ttclub.shopping.order.model;

import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2016/1/4.
 * Email:357599859@qq.com
 */
public class OrderBalanceInfo {
    public int panicShoppingId;
    public int goodsId;
    public int specId;
    public int goodsCount;

    public List<ShoppingCarGoodsBean> getList() {
        return list;
    }

    public void setList(List<ShoppingCarGoodsBean> list) {
        this.list = list;
    }

    private List<ShoppingCarGoodsBean> list = new ArrayList<>();

    public String totalPrice;
    public String goodsPrice;
    public int payType;
    public int addrId;
    public String userRemark;
}
