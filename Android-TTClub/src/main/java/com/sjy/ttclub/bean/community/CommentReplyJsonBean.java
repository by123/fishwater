package com.sjy.ttclub.bean.community;

import java.util.List;

/**
 * Created by zwl on 2015/11/16.
 * Email: 1501448275@qq.com
 */
public class CommentReplyJsonBean {

    private int status;
    private String msg;
    private CommentReplyDataJsonBean data;

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

    public CommentReplyDataJsonBean getData() {
        return data;
    }

    public void setData(CommentReplyDataJsonBean data) {
        this.data = data;
    }

    public class CommentReplyDataJsonBean{
        private int endId;
        private List<CommentReplyBean> replys;

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<CommentReplyBean> getReplys() {
            return replys;
        }

        public void setReplys(List<CommentReplyBean> replys) {
            this.replys = replys;
        }
    }
}
