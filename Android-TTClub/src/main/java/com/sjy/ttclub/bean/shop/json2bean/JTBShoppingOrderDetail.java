package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderDetailBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2016/1/8.
 * Email:357599859@qq.com
 */
public class JTBShoppingOrderDetail extends BaseBean implements Serializable {

    private ShoppingOrderDetailBean data;

    public ShoppingOrderDetailBean getData() {
        return data;
    }

    public void setData(ShoppingOrderDetailBean data) {
        this.data = data;
    }
}
