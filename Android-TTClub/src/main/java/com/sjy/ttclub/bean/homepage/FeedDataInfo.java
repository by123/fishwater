package com.sjy.ttclub.bean.homepage;

import java.util.List;

/**
 * Created by chenjiawei on 2016/1/9.
 */
public class FeedDataInfo {
    private String endId;
    private List<FeedInfo> feed;

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public List<FeedInfo> getFeed() {
        return feed;
    }

    public void setFeed(List<FeedInfo> feed) {
        this.feed = feed;
    }
}
