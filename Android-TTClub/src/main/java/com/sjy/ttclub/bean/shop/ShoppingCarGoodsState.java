package com.sjy.ttclub.bean.shop;

import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;

import java.util.List;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class ShoppingCarGoodsState {
    private List<ShoppingCarGoodsBean> valid;
    private List<ShoppingCarGoodsBean> invalid;

    public List<ShoppingCarGoodsBean> getValid() {
        return valid;
    }

    public void setValid(List<ShoppingCarGoodsBean> valid) {
        this.valid = valid;
    }

    public List<ShoppingCarGoodsBean> getInvalid() {
        return invalid;
    }

    public void setInvalid(List<ShoppingCarGoodsBean> invalid) {
        this.invalid = invalid;
    }
}
