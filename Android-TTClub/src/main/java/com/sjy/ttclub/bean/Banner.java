package com.sjy.ttclub.bean;

/**
 * Created by linhz on 2015/12/7.
 * Email: linhaizhong@ta2she.com
 */
public class Banner {

//    id	Int	是	推荐位的唯一ID
//    title	String	是	标题
//    url	String	是	图片的URL地址
//    typeID	Int	是	目标类型1-文章、2-商品、3-帖子
//    targetID	Int	是	跳转的目标唯一ID


    public String targetID;
    /**
     * 广告标题
     */
    private String title;
    /**
     * 广告图片url
     */
    private String url;
    /**
     * 广告属性
     */
    private int typeID;
    /**
     * 广告属性值
     */
    private String adAttrValue;

    public String getBannerId(){
        return targetID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return url;
    }

    public void setImageUrl(String imageUrl) {
        this.url = imageUrl;
    }

    public int getAdAttr() {
        return typeID;
    }

    public void setAdAttr(int adAttr) {
        this.typeID = adAttr;
    }

    public String getAdAttrValue() {
        return adAttrValue;
    }

    public void setAdAttrValue(String adAttrValue) {
        this.adAttrValue = adAttrValue;
    }
}
