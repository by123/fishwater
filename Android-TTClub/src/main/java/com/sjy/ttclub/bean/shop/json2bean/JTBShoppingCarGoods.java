package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsState;

import java.io.Serializable;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class JTBShoppingCarGoods extends BaseBean implements Serializable {
    private ShoppingCarGoodsState data;

    public ShoppingCarGoodsState getData() {
        return data;
    }

    public void setData(ShoppingCarGoodsState data) {
        this.data = data;
    }
}
