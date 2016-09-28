package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingMainBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/21.
 * Email:357599859@qq.com
 */
public class JTBShoppingMain extends BaseBean implements Serializable {

    private ShoppingMainBean data;

    public ShoppingMainBean getData() {
        return data;
    }

    public void setData(ShoppingMainBean data) {
        this.data = data;
    }
}
