package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingGoodsReviewBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class JTBShoppingReviewsList extends BaseBean implements Serializable {

    private ShoppingGoodsReviewBean data;

    public ShoppingGoodsReviewBean getData() {
        return data;
    }

    public void setData(ShoppingGoodsReviewBean data) {
        this.data = data;
    }
}
