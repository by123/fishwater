package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2016/1/8.
 * Email:357599859@qq.com
 */
public class ShoppingOrderDetailBean implements Serializable {

    private String orderId;
    private String orderNo;
    private String createtime;
    private String startSeconds;
    private String totalPrice;
    private String postageFee;
    private String goodsPrice;
    private int payType;
    private int orderStatus;
    private String userRemark;
    private int addrId;
    private String detailAddr;
    private String mobile;
    private String receiver;
    private String detailDistrict;
    private int provinceId;
    private int cityId;
    private int districtId;
    private String surplusSeconds;
    private List<ShoppingOrderLogisticsBean> logistics;
    private List<ShoppingOrderDetailGoodsBean> goods;
    public String selectLogisticsId;

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getCreatetime() {
        return createtime;
    }

    public String getStartSeconds() {
        return startSeconds;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getPostageFee() {
        return postageFee;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public int getPayType() {
        return payType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public int getAddrId() {
        return addrId;
    }

    public String getDetailAddr() {
        return detailAddr;
    }

    public String getMobile() {
        return mobile;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDetailDistrict() {
        return detailDistrict;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public String getSurplusSeconds() {
        return surplusSeconds;
    }

    public List<ShoppingOrderLogisticsBean> getLogistics() {
        return logistics;
    }

    public List<ShoppingOrderDetailGoodsBean> getGoods() {
        return goods;
    }
}
