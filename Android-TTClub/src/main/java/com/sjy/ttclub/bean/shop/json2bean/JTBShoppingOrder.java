package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class JTBShoppingOrder extends BaseBean implements Serializable {

    private ShoppingOrderBean data;

    public ShoppingOrderBean getData() {
        return data;
    }

    public void setData(ShoppingOrderBean data) {
        this.data = data;
    }
}
