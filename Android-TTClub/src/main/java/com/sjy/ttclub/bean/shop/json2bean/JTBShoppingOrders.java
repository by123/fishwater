package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingCartBean;
import com.sjy.ttclub.bean.shop.ShoppingOrdersBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class JTBShoppingOrders extends BaseBean implements Serializable {

    private ShoppingOrdersBean data;

    public ShoppingOrdersBean getData() {
        return data;
    }

    public void setData(ShoppingOrdersBean data) {
        this.data = data;
    }
}
