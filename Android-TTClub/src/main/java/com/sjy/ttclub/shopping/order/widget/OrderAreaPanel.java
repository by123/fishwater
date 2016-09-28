package com.sjy.ttclub.shopping.order.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.shopping.order.adapter.XmlParserHandler;
import com.sjy.ttclub.shopping.order.model.BaseModel;
import com.sjy.ttclub.shopping.order.model.CityModel;
import com.sjy.ttclub.shopping.order.model.DistrictModel;
import com.sjy.ttclub.shopping.order.model.OrderAddressInfo;
import com.sjy.ttclub.shopping.order.model.ProvinceModel;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.BasePanel;
import com.sjy.ttclub.widget.WheelView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by zhxu on 2016/1/15.
 * Email:357599859@qq.com
 */
public class OrderAreaPanel extends BasePanel implements WheelView.OnWheelViewListener, View.OnClickListener {

    private LinearLayout mPanel;
    private WheelView mViewProvince, mViewCity, mViewDistrict;
    private AlphaTextView mTvFinish;

    protected String mCurrentProvinceName;
    protected int mCurrentProvinceId;
    protected String mCurrentCityName;
    protected int mCurrentCityId;
    protected String mCurrentDistrictName;
    protected int mCurrentDistrictId;

    private List<ProvinceModel> mProvinceList = new ArrayList<>();
    private List<CityModel> mCitiesList = new ArrayList<>();
    private List<DistrictModel> mDistrictList = new ArrayList<>();

    private IOrderAreaPanelListener mPanelListener;

    public OrderAreaPanel(Context context) {
        this(context, true);
    }

    public OrderAreaPanel(Context context, boolean autoInit) {
        super(context, autoInit);
        initProvinceData();
    }

    public void setPanelListener(IOrderAreaPanelListener panelListener) {
        this.mPanelListener = panelListener;
    }

    @Override
    protected View onCreateContentView() {
        mPanel = (LinearLayout) View.inflate(mContext, R.layout.order_area_panel, null);
        mViewProvince = (WheelView) mPanel.findViewById(R.id.wv_province);
        mViewProvince.setOnWheelViewListener(this);
        mViewCity = (WheelView) mPanel.findViewById(R.id.wv_city);
        mViewCity.setOnWheelViewListener(this);
        mViewDistrict = (WheelView) mPanel.findViewById(R.id.wv_district);
        mViewDistrict.setOnWheelViewListener(this);
        mTvFinish = (AlphaTextView) mPanel.findViewById(R.id.area_btn_ok);
        mTvFinish.setOnClickListener(this);
        return mPanel;
    }

    public void initArea(OrderAddressInfo mAddressInfo) {
        if (StringUtils.parseInt(mAddressInfo.provinceId) == 0) {
            return;
        }
        mViewProvince.setCurrentPosition(getPositionByList(mAddressInfo.provinceId, mProvinceList));
        mViewCity.setCurrentPosition(getPositionByList(mAddressInfo.cityId, mCitiesList));
        mViewDistrict.setCurrentPosition(getPositionByList(mAddressInfo.districtId, mDistrictList));
    }

    private <T> int getPositionByList(String provinceId, List<T> list) {
        int pos = 0;
        for (T model : list) {
            if (StringUtils.parseInt(provinceId) == ((BaseModel) model).getId() || StringUtils.parseInt(provinceId) == 0) {
                break;
            }
            pos++;
        }
        return pos;
    }

    protected void initProvinceData() {
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();

            mProvinceList.addAll(handler.getDataList());//初始化省 WheelView
            List<String> provinceList = new ArrayList<>();
            for (ProvinceModel provinceModel : mProvinceList) {
                provinceList.add(provinceModel.getName());
            }
            mViewProvince.setItemList(provinceList);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    public void onSelected(WheelView wView, int selectedIndex, String item) {
        if (wView == mViewProvince) {
            updateProvince(selectedIndex);
            mViewCity.setCurrentPosition(0);
        } else if (wView == mViewCity) {
            updateCities(selectedIndex);
            mViewDistrict.setCurrentPosition(0);
        } else if (wView == mViewDistrict) {
            updateDistrict(selectedIndex);
        }
        if (mPanelListener != null) {
            mPanelListener.onSelected(mCurrentProvinceName + mCurrentCityName + mCurrentDistrictName);
        }
    }

    private void updateProvince(int pos) {
        ProvinceModel provinceModel = mProvinceList.get(pos);
        mCitiesList.clear();
        mCitiesList.addAll(provinceModel.getCityList());
        List<String> citiesList = new ArrayList<>();
        for (CityModel cityModel : mCitiesList) {
            citiesList.add(cityModel.getName());
        }
        mViewCity.setItemList(citiesList);
        mCurrentProvinceName = provinceModel.getName();
        mCurrentProvinceId = provinceModel.getId();
    }

    private void updateCities(int pos) {
        CityModel cityModel = mCitiesList.get(pos);
        mDistrictList.clear();
        mDistrictList.addAll(cityModel.getDistrictList());
        List<String> districtList = new ArrayList<>();
        for (DistrictModel districtModel : mDistrictList) {
            districtList.add(districtModel.getName());
        }
        mViewDistrict.setItemList(districtList);
        mCurrentCityName = cityModel.getName();
        mCurrentCityId = cityModel.getId();
    }

    private void updateDistrict(int pos) {
        DistrictModel districtModel = mDistrictList.get(pos);
        mCurrentDistrictName = districtModel.getName();
        mCurrentDistrictId = districtModel.getId();
    }

    public int getCurrentProvinceId() {
        return mCurrentProvinceId;
    }

    public int getCurrentCityId() {
        return mCurrentCityId;
    }

    public int getCurrentDistrictId() {
        return mCurrentDistrictId;
    }

    @Override
    public void onClick(View v) {
        hidePanel();
    }

    public interface IOrderAreaPanelListener {
        void onSelected(String desc);
    }
}
