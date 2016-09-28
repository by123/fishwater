package com.sjy.ttclub.bean.shop;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/21.
 * Email:357599859@qq.com
 */
public class ShoppingTopicBean implements Serializable {

    private int topicId;
    private String title;
    private String subTitle;
    private int template;
    private String surplusSeconds;
    private String thumbUrl;
    private List<ShoppingTopicColumnBean> column;

    public int getId() {
        return topicId;
    }

    public void setId(int id) {
        this.topicId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    public String getSurplusSeconds() {
        return surplusSeconds;
    }

    public void setSurplusSeconds(String surplusSeconds) {
        this.surplusSeconds = surplusSeconds;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public List<ShoppingTopicColumnBean> getColumn() {
        return column;
    }

    public void setColumn(List<ShoppingTopicColumnBean> column) {
        this.column = column;
    }
}
