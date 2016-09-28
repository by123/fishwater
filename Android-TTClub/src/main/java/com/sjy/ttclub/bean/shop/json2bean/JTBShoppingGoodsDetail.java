package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingGoodsDetailBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class JTBShoppingGoodsDetail extends BaseBean implements Serializable {

    private ShoppingGoodsDetailBean data;

    public ShoppingGoodsDetailBean getData() {
        return data;
    }

    public void setData(ShoppingGoodsDetailBean data) {
        this.data = data;
    }
}
