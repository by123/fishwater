package com.sjy.ttclub.bean.community;

import java.io.Serializable;
import java.util.List;

public class MyPostBean implements Serializable {
    //帖子id
    private int postId;
    //帖子标题
    private String postTitle;
    private String content;
    //发帖时间
    private String publishTime;
    //审核状态 未通过：-1，未审核：0，已审核：1；
    private int postStatus;
    private int replyCount;
    private int circleType;
    private int readCount;
    private List<ImageCard> images;
    private String isAnony;


    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(int postStatus) {
        this.postStatus = postStatus;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ImageCard> getImages() {
        return images;
    }

    public void setImages(List<ImageCard> images) {
        this.images = images;
    }

    public String getIsAnony() {
        return isAnony;
    }

    public void setIsAnony(String isAnony) {
        this.isAnony = isAnony;
    }
}
