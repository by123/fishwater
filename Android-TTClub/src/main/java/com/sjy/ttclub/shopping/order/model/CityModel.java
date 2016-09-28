package com.sjy.ttclub.shopping.order.model;

import java.util.ArrayList;
import java.util.List;

public class CityModel extends BaseModel {
    private ArrayList<DistrictModel> mDistrictList = new ArrayList<>();

    public CityModel() {
        super();
    }

    public List<DistrictModel> getDistrictList() {
        return mDistrictList;
    }

    public void addDistrictModel(DistrictModel districtModel) {
        mDistrictList.add(districtModel);
    }

    @Override
    public String toString() {
        return "CityModel [name=" + mName + ", districtList=" + mDistrictList + "]";
    }
}
