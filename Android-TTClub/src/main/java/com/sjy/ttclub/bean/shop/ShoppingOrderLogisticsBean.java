package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/22.
 * Email:357599859@qq.com
 */
public class ShoppingOrderLogisticsBean implements Serializable {

    private int logisticsId;
    private String logisticsNum;
    private String logisticsCompany;
    private String lastestInfo;
    private int logisticsStatus;
    private String latestTime;
    private String logoUrl;

    public int getLogisticsId() {
        return logisticsId;
    }

    public String getLogisticsNum() {
        return logisticsNum;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public String getLastestInfo() {
        return lastestInfo;
    }

    public int getLogisticsStatus() {
        return logisticsStatus;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
