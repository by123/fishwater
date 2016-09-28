package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class ShoppingGoodsReviewBean implements Serializable {

    private List<ShoppingReviewBean> reviews;
    private String endId;
    private ShoppingSummaryBean summary;

    public List<ShoppingReviewBean> getReviews() {
        return reviews;
    }

    public void setReviews(List<ShoppingReviewBean> reviews) {
        this.reviews = reviews;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public ShoppingSummaryBean getSummary() {
        return summary;
    }

    public void setSummary(ShoppingSummaryBean summary) {
        this.summary = summary;
    }
}
