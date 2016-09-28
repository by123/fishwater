package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingCategoryListBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class JTBShoppingCategoryList extends BaseBean implements Serializable {

    private ShoppingCategoryListBean data;

    public ShoppingCategoryListBean getData() {
        return data;
    }

    public void setData(ShoppingCategoryListBean data) {
        this.data = data;
    }
}
