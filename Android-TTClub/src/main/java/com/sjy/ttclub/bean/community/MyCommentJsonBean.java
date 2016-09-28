package com.sjy.ttclub.bean.community;

import java.util.List;

/**
 * Created by zhangwulin on 2015/12/30.
 * email 1501448275@qq.com
 */
public class MyCommentJsonBean {
    private int status;
    private String msg;
    private CommunityMyReplyDataJsonBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CommunityMyReplyDataJsonBean getData() {
        return data;
    }

    public void setData(CommunityMyReplyDataJsonBean data) {
        this.data = data;
    }

    public class CommunityMyReplyDataJsonBean{
        private int endId;
        private List<MyCommentBean> msgArray;

        public List<MyCommentBean> getMsgArray() {
            return msgArray;
        }

        public void setMsgArray(List<MyCommentBean> msgArray) {
            this.msgArray = msgArray;
        }

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }
    }
}
