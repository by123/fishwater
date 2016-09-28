package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingCartBean;
import com.sjy.ttclub.bean.shop.ShoppingCategoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class JTBShoppingCart extends BaseBean implements Serializable {

    private ShoppingCartBean data;

    public ShoppingCartBean getData() {
        return data;
    }

    public void setData(ShoppingCartBean data) {
        this.data = data;
    }
}
