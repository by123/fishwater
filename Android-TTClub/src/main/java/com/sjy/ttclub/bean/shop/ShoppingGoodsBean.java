package com.sjy.ttclub.bean.shop;


import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/21.
 * Email:357599859@qq.com
 */
public class ShoppingGoodsBean implements Serializable {

    private int goodsId;
    private String title;
    private String marketPrice;
    private String salePrice;
    private String description;
    private String thumbUrl;
    private int surplusCount;
    private int saleCount;

    public int getId() {
        return goodsId;
    }

    public String getTitle() {
        return title;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public int getSurplusCount() {
        return surplusCount;
    }

    public int getSaleCount() {
        return saleCount;
    }
}
