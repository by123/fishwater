package com.sjy.ttclub.bean.shop;

import java.io.Serializable;

/**
 * Created by zhxu on 2016/1/4.
 * Email:357599859@qq.com
 */
public class ShoppingAddressBean implements Serializable {

    private String detailAddr;
    private String mobile;
    private String receiver;
    private String detailDistrict;
    private int addrId;
    private int provinceId;
    private int cityId;
    private int districtId;

    public String getDetailAddr() {
        return detailAddr;
    }

    public String getMobile() {
        return mobile;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDetailDistrict() {
        return detailDistrict;
    }

    public int getAddrId() {
        return addrId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public int getDistrictId() {
        return districtId;
    }
}
