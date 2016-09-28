package com.sjy.ttclub.bean.community;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zwl on 2015/11/12.
 * Email: 1501448275@qq.com
 */
public class CommunityPostBean implements Serializable {
    private int postId;
    private String postTitle;
    private int circleId;
    private String circleName;
    private String circleIconUrl;
    private int contentType;
    private int isLimitMale;
    private String briefContent;
    private String publishTime;
    private int postTag;
    private int userId;
    private String nickname;
    private int userLevel;
    private String headimageUrl;
    private String pageHeaderUrl;
    private int userSex;
    private int isAnony;
    private int roleFlag;
    private int isPraise;
    private int isFollow;
    private int praiseCount;
    private int replyCount;
    private int postStatus;
    private int isCollect;
    private String content;
    private int jumpType;

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public String getPageHeaderUrl() {
        return pageHeaderUrl;
    }

    public void setPageHeaderUrl(String pageHeaderUrl) {
        this.pageHeaderUrl = pageHeaderUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(int postStatus) {
        this.postStatus = postStatus;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
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

    public String getCircleIconUrl() {
        return circleIconUrl;
    }

    public void setCircleIconUrl(String circleIconUrl) {
        this.circleIconUrl = circleIconUrl;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getIsLimitMale() {
        return isLimitMale;
    }

    public void setIsLimitMale(int isLimitMale) {
        this.isLimitMale = isLimitMale;
    }

    public String getBriefContent() {
        return briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getPostTag() {
        return postTag;
    }

    public void setPostTag(int postTag) {
        this.postTag = postTag;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getHeadimageUrl() {
        return headimageUrl;
    }

    public void setHeadimageUrl(String headimageUrl) {
        this.headimageUrl = headimageUrl;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public int getIsAnony() {
        return isAnony;
    }

    public void setIsAnony(int isAnony) {
        this.isAnony = isAnony;
    }

    public int getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(int roleFlag) {
        this.roleFlag = roleFlag;
    }

    public int getIsPraise() {
        return isPraise;
    }

    public void setIsPraise(int isPraise) {
        this.isPraise = isPraise;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public List<ImageCard> getImages() {
        return images;
    }

    public void setImages(List<ImageCard> images) {
        this.images = images;
    }

    public CommunityAnswerBean getChoiceReply() {
        return choiceReply;
    }

    public void setChoiceReply(CommunityAnswerBean choiceReply) {
        this.choiceReply = choiceReply;
    }

    private int readCount;
    private int circleType;
    private int sortId;
    private List<ImageCard> images;
    private CommunityAnswerBean choiceReply;


    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
