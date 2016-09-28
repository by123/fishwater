package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderNumBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class JTBShoppingOrderNum extends BaseBean implements Serializable {

    private ShoppingOrderNumBean data;

    public ShoppingOrderNumBean getData() {
        return data;
    }

    public void setData(ShoppingOrderNumBean data) {
        this.data = data;
    }
}
