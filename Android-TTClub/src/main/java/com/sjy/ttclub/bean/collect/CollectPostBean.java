/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.bean.collect;

import java.util.List;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
public class CollectPostBean {
    private int status;
    private String msg;
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private int endId;
        private List<PostInfo> posts;

        public List<PostInfo> getPosts() {
            return posts;
        }

        public int getEndId(){
            return endId;
        }
    }

    public static class PostInfo {
        private String postId;
        private String postTitle;
        private int circleType;

        public String getId() {
            return postId;
        }

        public String getTitle() {
            return postTitle;
        }

        public int getCircleType(){
            return circleType;
        }
    }
}
