package com.sjy.ttclub.bean.shop.json2bean;

import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.shop.ShoppingNewTopicBean;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class JTBShoppingTopicList extends BaseBean implements Serializable {

    private ShoppingNewTopicBean data;

    public ShoppingNewTopicBean getData() {
        return data;
    }

    public void setData(ShoppingNewTopicBean data) {
        this.data = data;
    }
}
