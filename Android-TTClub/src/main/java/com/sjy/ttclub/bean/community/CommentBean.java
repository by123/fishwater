package com.sjy.ttclub.bean.community;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwl on 2015/11/16.
 * Email: 1501448275@qq.com
 */
public class CommentBean implements Serializable {
    //评论id
    private int commentId;
    //帖子id
    private int postId;
    //圈子id
    private int circleId;
    //回帖内容
    private String content;
    //回帖楼层
    private int floor;
    //回帖时间
    private String cmtTime;
    //回帖评论数
    private int replyCount;
    //点赞数
    private int praiseCount;
    //用户id
    private int userId;
    //用户昵称
    private String nickname;
    //用户头像url
    private String headimageUrl;
    //用户等级
    private int userLevel;
    //0:无性别  1：男 2：女
    private int userSex;
    //帖子的用户id
    private int toUserId;
    //用户角色
    private int roleFlag;
    //是否匿名
    private int isAnony;
    private boolean prasie;
    private String circleName;
    private String postTitle;
    private int isHost;
    private int circleType;

    private int isLimitMale;
    //本地記錄text是否展開狀態
    private int expandState;

    private String createTime;
    private String userTagName;
    private ReferencedComment referencedComment;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserTagName() {
        return userTagName;
    }

    public void setUserTagName(String userTagName) {
        this.userTagName = userTagName;
    }

    public ReferencedComment getReferencedComment() {
        return referencedComment;
    }

    public void setReferencedComment(ReferencedComment referencedComment) {
        this.referencedComment = referencedComment;
    }

    public int getExpandState() {
        return expandState;
    }

    public void setExpandState(int expandState) {
        this.expandState = expandState;
    }

    public int getIsLimitMale() {
        return isLimitMale;
    }

    public void setIsLimitMale(int isLimitMale) {
        this.isLimitMale = isLimitMale;
    }

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }

    //评论的回复
    private List<CommentReplyBean> replys = new ArrayList<CommentReplyBean>();

    public int getIsHost() {
        return isHost;
    }

    public void setIsHost(int isHost) {
        this.isHost = isHost;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public boolean isPrasie() {
        return prasie;
    }

    public void setPrasie(boolean prasie) {
        this.prasie = prasie;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getCmtTime() {
        return cmtTime;
    }

    public void setCmtTime(String cmtTime) {
        this.cmtTime = cmtTime;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
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

    public String getHeadimageUrl() {
        return headimageUrl;
    }

    public void setHeadimageUrl(String headimageUrl) {
        this.headimageUrl = headimageUrl;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public int getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(int roleFlag) {
        this.roleFlag = roleFlag;
    }

    public int getIsAnony() {
        return isAnony;
    }

    public void setIsAnony(int isAnony) {
        this.isAnony = isAnony;
    }

    public List<CommentReplyBean> getReplys() {
        return replys;
    }

    public void setReplys(List<CommentReplyBean> replys) {
        this.replys = replys;
    }
}
