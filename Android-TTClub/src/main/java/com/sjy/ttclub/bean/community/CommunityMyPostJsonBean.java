package com.sjy.ttclub.bean.community;

import java.util.List;

/**
 * Created by zwl on 2015/11/23.
 * Email: 1501448275@qq.com
 */
public class CommunityMyPostJsonBean {
    private int status;
    private String msg;
    private CommunityMyPostDataBean data;

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

    public CommunityMyPostDataBean getData() {
        return data;
    }

    public void setData(CommunityMyPostDataBean data) {
        this.data = data;
    }

    public class CommunityMyPostDataBean {
        private int endId;
        private List<MyPostBean> posts;

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<MyPostBean> getPosts() {
            return posts;
        }

        public void setPosts(List<MyPostBean> posts) {
            this.posts = posts;
        }
    }
}
