package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryListBean implements Serializable {

    private List<ShoppingGoodsBean> goods;

    public List<ShoppingGoodsBean> getGoods() {
        return goods;
    }
}
