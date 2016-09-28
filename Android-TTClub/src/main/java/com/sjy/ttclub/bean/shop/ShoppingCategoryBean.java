package com.sjy.ttclub.bean.shop;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryBean implements Serializable {

    private int cateId;
    private String name;
    private String thumbUrl;
    private List<ShoppingSecondaryCateBean> cate;
    private List<ShoppingBrandBean> brand;
    private List<ShoppingLabelBean> hotLabels;

    public int getId() {
        return cateId;
    }

    public String getName() {
        return name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public List<ShoppingSecondaryCateBean> getCate() {
        return cate;
    }

    public List<ShoppingBrandBean> getBrand() {
        return brand;
    }

    public List<ShoppingLabelBean> getHotLabels() {
        return hotLabels;
    }
}
