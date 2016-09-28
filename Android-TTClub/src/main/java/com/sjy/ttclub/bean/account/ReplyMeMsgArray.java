package com.sjy.ttclub.bean.account;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/11/16.
 * Email:357599859@qq.com
 */
public class ReplyMeMsgArray {

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

    public MsgArrayObj getData() {
        return data;
    }

    public void setData(MsgArrayObj data) {
        this.data = data;
    }

    private MsgArrayObj data;

    public class MsgArrayObj {
        private int endId;
        private List<MsgArrays> msgArray;

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<MsgArrays> getMsgArray() {
            return msgArray;
        }

        public void setMsgArray(List<MsgArrays> msgArray) {
            this.msgArray = msgArray;
        }

        public class MsgArrays implements Serializable {
            private String msgId;

            public String getMsgId() {
                return msgId;
            }

            public void setMsgId(String msgId) {
                this.msgId = msgId;
            }

            public String getReplyContent() {
                return replyContent;
            }

            public void setReplyContent(String replyContent) {
                this.replyContent = replyContent;
            }

            public String getToPostId() {
                return toPostId;
            }

            public void setToPostId(String toPostId) {
                this.toPostId = toPostId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getPostTitle() {
                return postTitle;
            }

            public void setPostTitle(String postTitle) {
                this.postTitle = postTitle;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getHeadimageUrl() {
                return headimageUrl;
            }

            public void setHeadimageUrl(String headimageUrl) {
                this.headimageUrl = headimageUrl;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            private String replyContent;
            private String toPostId;
            private String toReplyId;
            private String type;//帖子:1；问答:2；文章:3；帖子评论:4；问答评论:5；文章评论:6；
            private String comment;
            private String imageUrl;
            private String postTitle;
            private String createtime;
            private String headimageUrl;
            private String nickname;
            private String level;
            private String sex;

            public String getToReplyId() {
                return toReplyId;
            }

            public void setToReplyId(String toReplyId) {
                this.toReplyId = toReplyId;
            }
        }
    }
}
