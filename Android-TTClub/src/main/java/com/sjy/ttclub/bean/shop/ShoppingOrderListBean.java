package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingOrderListBean implements Serializable {

    private int orderId;
    private String orderNo;
    private String orderPrice;
    private int payType;
    private int orderStatus;
    private List<ShoppingOrderGoodsBean> goods;
    private List<ShoppingOrderLogisticsBean> logistics;

    public int getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public int getPayType() {
        return payType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public List<ShoppingOrderGoodsBean> getGoods() {
        return goods;
    }

    public List<ShoppingOrderLogisticsBean> getLogistics() {
        return logistics;
    }

    public void setLogistics(List<ShoppingOrderLogisticsBean> logistics) {
        this.logistics = logistics;
    }
}
