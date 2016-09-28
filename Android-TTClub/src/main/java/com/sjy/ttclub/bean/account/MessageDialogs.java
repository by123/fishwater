package com.sjy.ttclub.bean.account;

import java.io.Serializable;

/**
 * Created by gangqing on 2015/12/7.
 * Email:denggangqing@ta2she.com
 */
public class MessageDialogs implements Serializable {
    //公有属性
    private String nickname;
    private String headimageUrl;
    //普通私信和她他小秘
    private String dialogId;
    private String userId;
    private String userSex;
    private String userLevel;
    private String letterId;
    private String letterContent;
    private String letterTime;
    private String pullBlackFlag;
    private String notreadCount;
    private String userRoleId;
    private boolean isShow = false;
    private boolean isCheck = false;
    //官方消息
    private String content;
    private String createtime;
    private String linkContent;
    private String linkType;
    private String linkUrl;
    private String msgId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getLinkContent() {
        return linkContent;
    }

    public void setLinkContent(String linkContent) {
        this.linkContent = linkContent;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getLetterId() {
        return letterId;
    }

    public void setLetterId(String letterId) {
        this.letterId = letterId;
    }

    public String getLetterContent() {
        return letterContent;
    }

    public void setLetterContent(String letterContent) {
        this.letterContent = letterContent;
    }

    public String getLetterTime() {
        return letterTime;
    }

    public void setLetterTime(String letterTime) {
        this.letterTime = letterTime;
    }

    public String getPullBlackFlag() {
        return pullBlackFlag;
    }

    public void setPullBlackFlag(String pullBlackFlag) {
        this.pullBlackFlag = pullBlackFlag;
    }

    public String getNotreadCount() {
        return notreadCount;
    }

    public void setNotreadCount(String notreadCount) {
        this.notreadCount = notreadCount;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }
}
