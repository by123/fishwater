package com.sjy.ttclub.bean;

/**
 * Created by linhz on 2015/12/7.
 * Email: linhaizhong@ta2she.com
 */
public class Banner {
    public String bannerId;
    /**
     * 广告标题
     */
    private String title;
    /**
     * 广告图片url
     */
    private String imageUrl;
    /**
     * 广告属性
     */
    private int adAttr;
    /**
     * 广告属性值
     */
    private String adAttrValue;

    public String getBannerId(){
        return bannerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAdAttr() {
        return adAttr;
    }

    public void setAdAttr(int adAttr) {
        this.adAttr = adAttr;
    }

    public String getAdAttrValue() {
        return adAttrValue;
    }

    public void setAdAttrValue(String adAttrValue) {
        this.adAttrValue = adAttrValue;
    }
}
