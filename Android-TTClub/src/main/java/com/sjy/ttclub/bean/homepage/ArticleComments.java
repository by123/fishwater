package com.sjy.ttclub.bean.homepage;


import com.sjy.ttclub.bean.community.CommentBean;

import java.util.List;

/**
 * Created by linhz on 2015/12/8.
 * Email: linhaizhong@ta2she.com
 */
public class ArticleComments {
    private String status;

    private String msg;

    private Data data;

    public class Data {
        private int allTotal;
        private List<CommentBean> comments;

        public int getCommentsCount() {
            return allTotal;
        }

        public List<CommentBean> getComments() {
            return comments;
        }
    }

    public Data getData() {
        return data;
    }


}
