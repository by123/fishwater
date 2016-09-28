package com.sjy.ttclub.bean.community;

import java.util.List;

public class CommunityCommentDataBean {
    private int status;
    private String msg;
    private CommunityCommentJsonBean data;

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

    public CommunityCommentJsonBean getData() {
        return data;
    }

    public void setData(CommunityCommentJsonBean data) {
        this.data = data;
    }

    public class CommunityCommentJsonBean {
        private int endId;
        private List<CommentBean> comments;

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<CommentBean> getComments() {
            return comments;
        }

        public void setComments(List<CommentBean> comments) {
            this.comments = comments;
        }
    }
}