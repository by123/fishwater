package com.sjy.ttclub.bean.splash;

import com.sjy.ttclub.bean.BaseBean;

import java.io.Serializable;

/**
 * Created by zhangwulin on 2016/1/7.
 * email 1501448275@qq.com
 */
public class SplashDataBean extends BaseBean implements Serializable{

    private SplashData data;

    public SplashData getData() {
        return data;
    }

    public void setData(SplashData data) {
        this.data = data;
    }

    public class SplashData {
        private int bannerId;
        private String title;
        private String imageUrl;
        private int adAttr;
        private String adAttrValue;
        private String startTime;
        private String endTime;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getBannerId() {
            return bannerId;
        }

        public void setBannerId(int bannerId) {
            this.bannerId = bannerId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getAdAttr() {
            return adAttr;
        }

        public void setAdAttr(int adAttr) {
            this.adAttr = adAttr;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getAdAttrValue() {
            return adAttrValue;
        }

        public void setAdAttrValue(String adAttrValue) {
            this.adAttrValue = adAttrValue;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
