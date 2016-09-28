package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class ShoppingGoodsDetailBean implements Serializable {

    private int logistics;
    private int goodsId;
    private String title;
    private String marketPrice;
    private String salePrice;
    private int saleCount;
    private String description;
    private String thumbUrl;
    private List<ShoppingGoodsImageUrlsBean> imageUrls;
    private ShoppingPanicBean panicShopping;
    private List<ShoppingSKUBean> sku;
    private int reviewCount;
    private ShoppingReviewBean review;
    private int isCollect;
    private int cartCount;
    private String postageDes;
    private String isPanicShopping;
    private String postageType;

    public int getLogistics() {
        return logistics;
    }

    public int getGoodsId() {
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

    public int getSaleCount() {
        return saleCount;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public List<ShoppingGoodsImageUrlsBean> getImageUrls() {
        return imageUrls;
    }

    public ShoppingPanicBean getPanicShopping() {
        return panicShopping;
    }

    public List<ShoppingSKUBean> getSku() {
        return sku;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public ShoppingReviewBean getReview() {
        return review;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public int getCartCount() {
        return cartCount;
    }

    public String getPostageDes() {
        return postageDes;
    }

    public String getIsPanicShopping() {
        return isPanicShopping;
    }

    public String getPostageType() {
        return postageType;
    }
}
