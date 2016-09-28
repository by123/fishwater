package com.sjy.ttclub.bean.community;

/**
 * Created by zhangwulin on 2015/12/8.
 * email 1501448275@qq.com
 */
public class CommunityUserInfo {
    private int userId;
    private String nickname;
    private String imageUrl;
    private int level;
    private int sex;
    private int userRoleId;
    private int ifFollow;
    private String followersCount;
    private String ifPrivLetter;

    private int articleCount;
    private int followingCount;
    private int postCount;
    private int praiseCount;
    private String userTagName;

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getUserTagName() {
        return userTagName;
    }

    public void setUserTagName(String userTagName) {
        this.userTagName = userTagName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    public int getIfFollow() {
        return ifFollow;
    }

    public void setIfFollow(int ifFollow) {
        this.ifFollow = ifFollow;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getIfPrivLetter() {
        return ifPrivLetter;
    }

    public void setIfPrivLetter(String ifPrivLetter) {
        this.ifPrivLetter = ifPrivLetter;
    }
}
