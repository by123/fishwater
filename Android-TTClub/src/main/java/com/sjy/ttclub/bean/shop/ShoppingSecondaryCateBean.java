package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingSecondaryCateBean implements Serializable {

    private int cateId;
    private String name;

    public int getId() {
        return cateId;
    }

    public void setId(int id) {
        this.cateId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
