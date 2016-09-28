package com.sjy.ttclub.bean.homepage;

/**
 * @version 2.5
 * @author: 陈嘉伟
 * @类 说 明:
 * @创建时间：2015/10/27 0027
 */
public class ArticleDetail {
    /**
     * 状态
     */
    public String status;
    /**
     * 消息
     */
    public String msg;

    public Data data;

    public class Data {
        private int praiseCount;
        private String imageUrl;
        private int childType;
        private int isCollect;
        private String typeName;
        private String goodsId;
        private String goodsTitle;
        private String goodsMarketPrice;
        private String goodsSalePrice;
        private String goodsSaleCount;
        private String goodsImageUrl;
        private String title;
        private String brief;
        private int imageCount;
        private int testingType;

        public int getPraiseCount() {
            return praiseCount;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getChildType() {
            return childType;
        }

        public void setIsCollect(int isCollect) {
            this.isCollect = isCollect;
        }

        public int getIsCollect() {
            return isCollect;
        }

        public boolean isCollect() {
            return isCollect == 1;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public String getGoodsTitle() {
            return goodsTitle;
        }

        public String getGoodsMarketPrice() {
            return goodsMarketPrice;
        }

        public String getGoodsSalePrice() {
            return goodsSalePrice;
        }

        public String getGoodsSaleCount() {
            return goodsSaleCount;
        }

        public String getGoodsImageUrl() {
            return goodsImageUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getBrief() {
            return brief;
        }

        public int getImageCount() {
            return imageCount;
        }

        public int getTestingType(){
            return testingType;
        }
    }

    public String getStatus() {
        return status;
    }


    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

}
