package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingOrderGoodsBean implements Serializable {

    private int goodsId;
    private int goodsCount;
    private String goodsPrice;
    private int logisticsId;
    private String title;
    private String thumbUrl;

    public int getGoodsId() {
        return goodsId;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public String getGoodsPrice() {
        return goodsPrice;
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
}
