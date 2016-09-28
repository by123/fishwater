package com.sjy.ttclub.bean.homepage;

import java.util.List;

/**
 * Created by linhz on 2015/12/8.
 * Email: linhaizhong@ta2she.com
 */
public class ArticleRecommends {
    private String status;

    private String msg;

    private List<ArticleRecommendInfo> data;


    public List<ArticleRecommendInfo> getData() {
        return data;
    }
}
