package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2016/1/6.
 * Email:357599859@qq.com
 */
public class ShoppingOrderBean implements Serializable {

    private String alipayPartner;
    private String alipaySeller;
    private String alipayRsaPrivate;
    private String alipayRsaPublic;
    private String weixinAppId;
    private String weixinMchId;
    private String weixinApiKey;
    private String goodsTitle;
    private String goodsDescription;
    private String alipayCallbackUrl;
    private String weixinCallbackUrl;

    public String getAlipayPartner() {
        return alipayPartner;
    }

    public void setAlipayPartner(String alipayPartner) {
        this.alipayPartner = alipayPartner;
    }

    public String getAlipaySeller() {
        return alipaySeller;
    }

    public void setAlipaySeller(String alipaySeller) {
        this.alipaySeller = alipaySeller;
    }

    public String getAlipayRsaPrivate() {
        return alipayRsaPrivate;
    }

    public void setAlipayRsaPrivate(String alipayRsaPrivate) {
        this.alipayRsaPrivate = alipayRsaPrivate;
    }

    public String getAlipayRsaPublic() {
        return alipayRsaPublic;
    }

    public void setAlipayRsaPublic(String alipayRsaPublic) {
        this.alipayRsaPublic = alipayRsaPublic;
    }

    public String getWeixinAppId() {
        return weixinAppId;
    }

    public void setWeixinAppId(String weixinAppId) {
        this.weixinAppId = weixinAppId;
    }

    public String getWeixinMchId() {
        return weixinMchId;
    }

    public void setWeixinMchId(String weixinMchId) {
        this.weixinMchId = weixinMchId;
    }

    public String getWeixinApiKey() {
        return weixinApiKey;
    }

    public void setWeixinApiKey(String weixinApiKey) {
        this.weixinApiKey = weixinApiKey;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public String getAlipayCallbackUrl() {
        return alipayCallbackUrl;
    }

    public void setAlipayCallbackUrl(String alipayCallbackUrl) {
        this.alipayCallbackUrl = alipayCallbackUrl;
    }

    public String getWeixinCallbackUrl() {
        return weixinCallbackUrl;
    }

    public void setWeixinCallbackUrl(String weixinCallbackUrl) {
        this.weixinCallbackUrl = weixinCallbackUrl;
    }
}
