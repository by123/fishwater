package com.sjy.ttclub.bean.account;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/11/17.
 * Email:357599859@qq.com
 */
public class MessageDialogDetails {

    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LetterObj getData() {
        return data;
    }

    public void setData(LetterObj data) {
        this.data = data;
    }

    private LetterObj data;

    public class LetterObj {
        private int startId;
        private int endId;
        private List<Letters> letters;
        private List<Letters> msgArray;

        public List<Letters> getMsgArray() {
            return msgArray;
        }

        public void setMsgArray(List<Letters> msgArray) {
            this.msgArray = msgArray;
        }

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<Letters> getLetters() {
            return letters;
        }

        public void setLetters(List<Letters> letters) {
            this.letters = letters;
        }

        public int getStartId() {
            return startId;
        }

        public void setStartId(int startId) {
            this.startId = startId;
        }

        public class Letters implements Serializable {
            private String dialogId;
            private String letterId;
            private String userId;
            private String nickname;
            private String headimageUrl;
            private String letterContent;
            private String sendTime;
            private String senderFlag;
            private String userRoleId;  //用户角色
            private String linkType;    //外部链接类型（1帖子;2圈子;3文章;4商品;5商品专题;6外部链接;7涨姿势;8测试;9导购;10图片;）
            private String linkContent; //外链内容
            private String linkUrl; //外链URL（当外部链接类型为6时为URL，其它为相对应的ID）

            private String content;
            private String createtime;
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

            public String getLetterId() {
                return letterId;
            }

            public void setLetterId(String letterId) {
                this.letterId = letterId;
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

            public String getLetterContent() {
                return letterContent;
            }

            public void setLetterContent(String letterContent) {
                this.letterContent = letterContent;
            }

            public String getSendTime() {
                return sendTime;
            }

            public void setSendTime(String sendTime) {
                this.sendTime = sendTime;
            }

            public String getSenderFlag() {
                return senderFlag;
            }

            public void setSenderFlag(String senderFlag) {
                this.senderFlag = senderFlag;
            }

            public String getUserRoleId() {
                return this.userRoleId;
            }

            public void setUserRoleId(String userRoleId) {
                this.userRoleId = userRoleId;
            }

            public String getLinkType() {
                return this.linkType;
            }

            public void setLinkType(String linkType) {
                this.linkType = linkType;
            }

            public String getLinkContent() {
                return this.linkContent;
            }

            public void setLinkContent(String linkContent) {
                this.linkContent = linkContent;
            }

            public String getLinkUrl() {
                return linkUrl;
            }

            public void setLinkUrl(String linkUrl) {
                this.linkUrl = linkUrl;
            }
        }
    }
}
