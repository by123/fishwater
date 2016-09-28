package com.sjy.ttclub.bean.community;

import com.sjy.ttclub.account.setting.PhotoGridAdapter;

import java.util.List;

/**
 * Created by zhangwulin on 2016/1/4.
 * email 1501448275@qq.com
 */
public class CommunitySendPostBean {
    private int cirlceId;
    private String theme;
    private String content;
    private int topicId;
    private int isAnony;
    private int circleType;
    private List<PhotoGridAdapter.PhotoItem> photoItems;

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }

    public int getCirlceId() {
        return cirlceId;
    }

    public void setCirlceId(int cirlceId) {
        this.cirlceId = cirlceId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getIsAnony() {
        return isAnony;
    }

    public void setIsAnony(int isAnony) {
        this.isAnony = isAnony;
    }

    public List<PhotoGridAdapter.PhotoItem> getPhotoItems() {
        return photoItems;
    }

    public void setPhotoItems(List<PhotoGridAdapter.PhotoItem> photoItems) {
        this.photoItems = photoItems;
    }
}
