package com.sjy.ttclub.bean.community;

import com.sjy.ttclub.bean.Banner;

import java.util.List;

/**
 * Created by zwl on 2015/11/13.
 * Email: 1501448275@qq.com
 */
public class CommunityHomeJsonBean {

    private int status;
    private String msg;
    private CommunityHomeDataJsonBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CommunityHomeDataJsonBean getData() {
        return data;
    }

    public void setData(CommunityHomeDataJsonBean data) {
        this.data = data;
    }

    public class CommunityHomeDataJsonBean{
        private List<Banner> banners;
        private List<CommunityCircleBean> hotCircles;
        private CommunityQABean qa;

        public List<Banner> getBanners() {
            return banners;
        }

        public void setBanners(List<Banner> banners) {
            this.banners = banners;
        }

        public List<CommunityCircleBean> getHotCircles() {
            return hotCircles;
        }

        public void setHotCircles(List<CommunityCircleBean> hotCircles) {
            this.hotCircles = hotCircles;
        }

        public CommunityQABean getQa() {
            return qa;
        }

        public void setQa(CommunityQABean qa) {
            this.qa = qa;
        }
    }
}
