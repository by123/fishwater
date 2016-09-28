package com.sjy.ttclub.bean.homepage;

import java.io.Serializable;

public class ArticleInfo implements Serializable {
    /**
     * 文章ID
     */
    private String id;
    /**
     * 文章类型。1，测试；2，导购；3，文章
     */
    private int type;
    /**
     * 文章属性名称
     */
    private String typeName;
    /**
     * 文章属性颜色
     */
    private int typeColor;
    /**
     * 文章子类型
     */
    private int childType;
    /**
     * 文章封面图URL
     */
    private String imageUrl;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章简介
     */
    private String brief;

    private int imageCount;

    private String publishtime;

    public ArticleInfo() {
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getChildType() {
        return childType;
    }


    public int getTypeColor() {
        return typeColor;
    }


    public String getTypeName() {
        return typeName;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getBrief() {
        return brief;
    }

    public int getImageCount() {
        return imageCount;
    }

    public String getDate() {
        return publishtime;
    }

}
