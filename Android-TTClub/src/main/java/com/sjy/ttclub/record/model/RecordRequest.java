package com.sjy.ttclub.record.model;

import android.content.Context;

import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpManager;

/**
 * Created by zhxu on 2015/12/8.
 * Email:357599859@qq.com
 */
public class RecordRequest {

    private Context mContext;

    public RecordRequest(Context ctx) {
        mContext = ctx;
    }

    public void getRecordByMonth(int year, int month, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "recordMonth");
        httpManager.addParams("year", year + "");
        httpManager.addParams("month", month + "");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
    }

    public void getRecordByDay(int year, int month, int day, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "recordDay");
        httpManager.addParams("year", year + "");
        httpManager.addParams("month", month + "");
        httpManager.addParams("day", day + "");
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
    }

    /**
     * 提交数据
     *
     * @param json
     */
    public void commitData(String json, String papaTime, String papaTimeRange, HttpCallbackAdapter callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "recordSent");
        httpManager.addParams("jsonData", json);
        httpManager.addParams("papaTime", papaTime);
        httpManager.addParams("papaTimeRange", papaTimeRange);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);

    }

}
