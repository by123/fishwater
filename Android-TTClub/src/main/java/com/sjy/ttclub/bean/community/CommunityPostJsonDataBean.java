package com.sjy.ttclub.bean.community;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zwl on 2015/11/12.
 * Email: 1501448275@qq.com
 */
public class CommunityPostJsonDataBean implements Serializable {

    private int endId;
    private List<CommunityPostBean> posts;

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public List<CommunityPostBean> getPosts() {
        return posts;
    }

    public void setPosts(List<CommunityPostBean> posts) {
        this.posts = posts;
    }
}
