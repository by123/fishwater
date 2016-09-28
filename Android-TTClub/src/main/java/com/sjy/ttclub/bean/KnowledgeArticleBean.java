package com.sjy.ttclub.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @version 2.6
 * @author: 陈嘉伟
 * @类 说 明:
 * @创建时间：2015/11/12
 */
public class KnowledgeArticleBean {
    /**
     * 状态
     */
    public int status;
    /**
     * 消息
     */
    public String msg;

    public List<List<ArticleInfo>> data;

    public static class ArticleInfo implements Serializable {
        public int aid;
        public String url;

        public int getAid() {
            return aid;
        }

        public String getUrl() {
            return url;
        }
    }


    public int getStatus() {
        return status;
    }


    public String getMsg() {
        return msg;
    }


    public List<List<ArticleInfo>> getData() {
        return data;
    }

}
