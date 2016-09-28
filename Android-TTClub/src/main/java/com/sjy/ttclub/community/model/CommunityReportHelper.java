package com.sjy.ttclub.community.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.bean.community.CommunityPostJsonBean;
import com.sjy.ttclub.bean.community.ReportBean;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.ACache;
import com.sjy.ttclub.util.ToastHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CommunityReportHelper {

    private static List<ReportBean> reportUserList = new ArrayList<>();
    private static List<ReportBean> reportPostList = new ArrayList<>();
    private static SharedPreferences.Editor ed;
    private static SharedPreferences sp;
    /**
     * 举报种类
     */
    public static int REPORT_POST = 1;//举报帖子
    public static int REPORT_USER = 2;//举报用户
    public static int REPORT_REPLY = 3;//举报回帖
    /**
     * 举报理由
     * 举报用户信息的理由列表
     * 1头像违规
     * 2昵称违规
     * 3发布内容违规
     * 4私信骚扰
     * 5其他
     * 举报帖子、回帖的理由列表
     * 1广告
     * 2色情
     * 3骚扰
     * 4泄漏隐私
     * 5其他
     */
    public static int REPORT_USER_HEAD = 1;
    public static int REPORT_USER_NAME = 2;
    public static int REPORT_USER_POSTCONTENT = 3;
    public static int REPORT_USER_PERSONALLETTER = 4;
    public static int REPORT_USER_OTHER = 5;
    public static int REPORT_AD = 1;
    public static int REPORT_SEXY = 2;
    public static int REPORT_HARASS = 3;
    public static int REPORT_LEAKAGE = 4;
    public static int REPORT_OTHER = 5;
    private Context mContext;
    public CommunityReportHelper(Context context) {
        mContext = context;
    }
    /**
     * 获取举报用户的理由
     *
     * @return
     */
    public static List<ReportBean> getReportUserList(Context context) {
        return getReportUserReasonsFromJsonArray(ACache.get(context).getAsJSONArray("reportUser"));
    }

    /**
     * 获取举报帖子的理由
     *
     * @return
     */
    public static List<ReportBean> getReportPostList(Context context) {
        return getReportPostReasonsFromJsonArray(ACache.get(context).getAsJSONArray("reportPost"));
    }

    /**
     * 获取举报详情页
     *
     * @param context
     */
    public void getReportReasonsListFromServer(final Context context) {
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "rrg");
        mHttpManager.addParams("mapMd5", AppMd5Helper.getInstance().getStoreStringValue(AppMd5Helper.getInstance().KEY_REPORT_MD5));
        mHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, CommunityPostJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (result != null && result.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        String message = jsonObject.getString("msg");
                        if (HttpCode.SUCCESS_CODE == code) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            //举报用户的理由
                            JSONArray reportUserArray = data.getJSONArray("reportUser");
                            //记录在本地
                            saveReportListInLocal(context, "reportUser", reportUserArray);
                            //举报帖子理由
                            JSONArray reportPostArray = data.getJSONArray("reportPost");
                            //记录在本地
                            saveReportListInLocal(context, "reportPost", reportPostArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
            }
        });
    }

    /**
     * 发送举报信息
     *
     * @param context
     * @param userId
     * @param objctId
     * @param reportType
     * @param reportValue
     */
    public void sendReport(final Context context, int userId, int objctId, int reportType, String reportValue) {
        IHttpManager mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "rp");
        //举报类型：1、举报帖子2、举报用户3、举报回帖
        mHttpManager.addParams("informType", String.valueOf(reportType));
        mHttpManager.addParams("reasonValue", reportValue);
        mHttpManager.addParams("informUserId", String.valueOf(userId));
        mHttpManager.addParams("informObjectId", String.valueOf(objctId));
        mHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, CommunityPostJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (result != null && result.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        if (HttpCode.SUCCESS_CODE == code) {
                            ToastHelper.showToast(context, "举报成功", Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastHelper.showToast(context, "数据异常，请稍后再试", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                ToastHelper.showToast(context, "数据异常，请稍后再试", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 将从网络获取到的举报理由的jsonArray保存在本地
     *
     * @param jsonArray
     */
    private static void saveReportListInLocal(Context context, String arrayName, JSONArray jsonArray) {
        ACache.get(context).put(arrayName, jsonArray);
    }

    /**
     * 从本地保存的jsonArray中获取举报理由列表
     *
     * @param jsonArray
     * @return
     */
    private static List<ReportBean> getReportPostReasonsFromJsonArray(JSONArray jsonArray) {
        if (reportPostList.size() > 0) {
            reportPostList.clear();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ReportBean reportBean = new ReportBean(jsonArray.getJSONObject(i).getString("value")
                        , jsonArray.getJSONObject(i).getString("name"));
                reportPostList.add(reportBean);
            } catch (JSONException e) {
                //异常
            }
        }
        return reportPostList;
    }

    /**
     * 从本地保存的jsonArray中获取举报理由列表
     *
     * @param jsonArray
     * @return
     */
    private static List<ReportBean> getReportUserReasonsFromJsonArray(JSONArray jsonArray) {
        if (reportUserList.size() > 0) {
            reportUserList.clear();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ReportBean reportBean = new ReportBean(jsonArray.getJSONObject(i).getString("value")
                        , jsonArray.getJSONObject(i).getString("name"));
                reportUserList.add(reportBean);
            } catch (JSONException e) {
                //异常
            }
        }
        return reportUserList;
    }

    /**
     * 举报头像理由是否缓存在本地
     *
     * @param context
     * @return
     */
    public static boolean reportUserReasonIsSaveInLocal(Context context) {
        boolean isExit = false;
        try {
            if (ACache.get(context).get("reportUser") != null) {
                isExit = true;
            } else {
                isExit = false;
            }
        } catch (FileNotFoundException e) {
            isExit = false;
        }

        return isExit;
    }

    /**
     * 举报帖子理由是否缓存在本地
     *
     * @param context
     * @return
     */
    public static boolean reportPostReasonIsSaveInLocal(Context context) {
        boolean isExit = false;
        try {
            if (ACache.get(context).get("reportPost") != null) {
                isExit = true;
            } else {
                isExit = false;
            }
        } catch (FileNotFoundException e) {
            isExit = false;
        }

        return isExit;
    }
}
