package com.sjy.ttclub.shopping.order.adapter;

import com.sjy.ttclub.shopping.order.model.CityModel;
import com.sjy.ttclub.shopping.order.model.DistrictModel;
import com.sjy.ttclub.shopping.order.model.ProvinceModel;
import com.sjy.ttclub.util.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XmlParserHandler extends DefaultHandler {

    private List<ProvinceModel> mProvinceList = new ArrayList<>();

    private ProvinceModel mProvinceModel = new ProvinceModel();
    private CityModel mCityModel = new CityModel();
    private DistrictModel mDistrictModel = new DistrictModel();

    public XmlParserHandler() {

    }

    public List<ProvinceModel> getDataList() {
        return mProvinceList;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("province")) {
            mProvinceModel = new ProvinceModel();
            mProvinceModel.setId(StringUtils.parseInt(attributes.getValue(0)));
            mProvinceModel.setName(attributes.getValue(1));
        } else if (qName.equals("city")) {
            mCityModel = new CityModel();
            mCityModel.setId(StringUtils.parseInt(attributes.getValue(0)));
            mCityModel.setName(attributes.getValue(1));
        } else if (qName.equals("district")) {
            mDistrictModel = new DistrictModel();
            mDistrictModel.setId(StringUtils.parseInt(attributes.getValue(0)));
            mDistrictModel.setName(attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("district")) {
            mCityModel.addDistrictModel(mDistrictModel);
        } else if (qName.equals("city")) {
            mProvinceModel.addCityModel(mCityModel);
        } else if (qName.equals("province")) {
            mProvinceList.add(mProvinceModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }
}
