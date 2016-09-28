package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingOrdersBean implements Serializable {

    private int orderCount;
    private List<ShoppingOrderListBean> order;

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public List<ShoppingOrderListBean> getOrder() {
        return order;
    }

    public void setOrder(List<ShoppingOrderListBean> order) {
        this.order = order;
    }
}
