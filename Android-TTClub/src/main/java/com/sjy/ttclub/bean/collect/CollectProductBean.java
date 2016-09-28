/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.bean.collect;

import java.util.List;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
public class CollectProductBean {

    private int status;
    private String msg;
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private int endId;
        private List<ProductInfo> goods;

        public List<ProductInfo> getGoods() {
            return goods;
        }

        public int getEndId() {
            return endId;
        }
    }

    public static class ProductInfo {
        private String goodsId;
        private String title;
        private float marketPrice;
        private float salePrice;
        private int saleCount;
        private String description;
        private String thumbUrl;

        public String getId() {
            return goodsId;
        }

        public String getTitle() {
            return title;
        }

        public float getMarketPrice() {
            return marketPrice;
        }

        public float getSalePrice() {
            return salePrice;
        }

        public int getSaleCount() {
            return saleCount;
        }

        public String getDescription() {
            return description;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }
    }
}
