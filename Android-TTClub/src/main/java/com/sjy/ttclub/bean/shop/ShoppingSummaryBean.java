package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class ShoppingSummaryBean implements Serializable {

    private String rating;
    private int satisfiedCount;
    private int generalCount;
    private int unsatisfiedCount;
    private int totalCount;

    public String getRating() {
        return rating;
    }

    public int getSatisfiedCount() {
        return satisfiedCount;
    }

    public int getGeneralCount() {
        return generalCount;
    }

    public int getUnsatisfiedCount() {
        return unsatisfiedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

}
