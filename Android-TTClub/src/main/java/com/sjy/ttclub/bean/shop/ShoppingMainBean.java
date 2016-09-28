package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/21.
 * Email:357599859@qq.com
 */
public class ShoppingMainBean implements Serializable {

    private ShoppingPanicBean panicShopping;
    private int cartCount;
    private List<ShoppingTopicBean> topic;

    public ShoppingPanicBean getPanicShopping() {
        return panicShopping;
    }

    public void setPanicShopping(ShoppingPanicBean panicShopping) {
        this.panicShopping = panicShopping;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public List<ShoppingTopicBean> getTopic() {
        return topic;
    }

    public void setTopic(List<ShoppingTopicBean> topic) {
        this.topic = topic;
    }
}
