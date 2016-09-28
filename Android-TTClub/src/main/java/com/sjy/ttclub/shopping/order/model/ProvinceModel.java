package com.sjy.ttclub.shopping.order.model;

import java.util.ArrayList;
import java.util.List;

public class ProvinceModel extends BaseModel {

    private ArrayList<CityModel> mCityList = new ArrayList<>();

    public ProvinceModel() {

    }

    public List<CityModel> getCityList() {
        return mCityList;
    }

    public void addCityModel(CityModel cityModel) {
        mCityList.add(cityModel);
    }

    @Override
    public String toString() {
        return "ProvinceModel [name=" + mName + ", cityList=" + mCityList + "]";
    }
}
