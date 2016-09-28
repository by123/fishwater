/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.bean.collect;

import java.util.List;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
public class CollectArticleBean {
    private int status;
    private String msg;
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private int endId;
        private List<ArticleInfo> posts;

        public List<ArticleInfo> getArticles() {
            return posts;
        }

        public int getEndId() {
            return endId;
        }
    }

    public static class ArticleInfo {
        private String articleId;
        private String articleTitle;
        private String imageUrl;
        private int type;

        public String getId() {
            return articleId;
        }

        public String getTitle() {
            return articleTitle;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getType() {
            return type;
        }
    }
}
