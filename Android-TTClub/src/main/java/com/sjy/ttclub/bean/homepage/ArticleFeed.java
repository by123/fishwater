package com.sjy.ttclub.bean.homepage;

import java.util.List;

public class ArticleFeed {
    /**
     * 状态
     */
    public int status;
    /**
     * 消息
     */
    public String msg;

    public Data data;

    public class Data {
        /**
         * 限制标识
         */
        private String endId;

        public List<ArticleInfo> articles;

        public String getEndId() {
            return endId;
        }

        public void setEndId(String endId) {
            this.endId = endId;
        }

        public List<ArticleInfo> getArticles() {
            return articles;
        }
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

}
