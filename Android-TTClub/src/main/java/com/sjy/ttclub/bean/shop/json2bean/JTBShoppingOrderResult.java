package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingAddressBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class JTBShoppingOrderResult extends BaseBean implements Serializable {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
