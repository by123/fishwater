package com.sjy.ttclub.bean.community;

import java.io.Serializable;

public class CommunityCircleBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //社区圈子id
    private int circleId;
    //社区圈子名称
    private String circleName;
    //社区圈子图标
    private String iconUrl;
    //社区圈子主题
    private String theme;
    //圈子热度
    private int heat;
    //热圈子标识
    private int isHot;
    //是否被关注
    private int isAttention;
    //是否限制男性操作 0 否，1 是
    private int isLimitMale;
    //父id
    private int parentId;
    //圈子性别
    private int circleSex;
    //圈子内容属性（以图为主：1；以文为主：2）
    private int contentType;
    //圈子属性 1普通圈子，2问答圈子
    private int circleType;

    private int limitEnterSex;

    public int getLimitEnterSex() {
        return limitEnterSex;
    }

    public void setLimitEnterSex(int limitEnterSex) {
        this.limitEnterSex = limitEnterSex;
    }

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public int getIsLimitMale() {
        return isLimitMale;
    }

    public void setIsLimitMale(int isLimitMale) {
        this.isLimitMale = isLimitMale;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getCircleSex() {
        return circleSex;
    }

    public void setCircleSex(int circleSex) {
        this.circleSex = circleSex;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
}
