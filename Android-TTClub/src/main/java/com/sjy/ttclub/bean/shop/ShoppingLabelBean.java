package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingLabelBean implements Serializable {

    private int hid;
    private String name;
    private int type;

    public int getHid() {
        return hid;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
