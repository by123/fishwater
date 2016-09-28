package com.sjy.ttclub.bean.community;

import java.util.List;

/**
 * Created by zwl on 2015/11/23.
 * Email: 1501448275@qq.com
 */
public class CommunityAllCircleBean {

    private int status;
    private String msg;
    private CommunityAllCircleDataBean data;

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

    public CommunityAllCircleDataBean getData() {
        return data;
    }

    public void setData(CommunityAllCircleDataBean data) {
        this.data = data;
    }

    public class CommunityAllCircleDataBean{
        private List<CommunityCircleBean> circles;

        public List<CommunityCircleBean> getCircles() {
            return circles;
        }

        public void setCircles(List<CommunityCircleBean> circles) {
            this.circles = circles;
        }
    }
}
