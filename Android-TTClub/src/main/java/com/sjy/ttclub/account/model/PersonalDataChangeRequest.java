package com.sjy.ttclub.account.model;

import android.content.Context;
import android.widget.Toast;

import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.Md5Utils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gangqing on 2015/12/17.
 * Email:denggangqing@ta2she.com
 */
public class PersonalDataChangeRequest {
    private Context mContext;

    public PersonalDataChangeRequest(Context context) {
        this.mContext = context;
    }

    public void changePassword(String oldPassword, String newPassword, IHttpCallBack httpCallBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "ucp");
        httpManager.addParams("oldPassword", Md5Utils.getMD5(oldPassword));
        httpManager.addParams("newPassword", Md5Utils.getMD5(newPassword));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, httpCallBack);
    }

    public void changeNickname(String newNickname, IHttpCallBack httpCallBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "uui");
        httpManager.addParams("nickname", newNickname);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, httpCallBack);
    }

    public void changeUserPicImage(String path, IHttpCallBack callBack) {
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getLevel()) < 2) {
            ToastHelper.showToast(mContext, "两级以上才能更改头像", Toast.LENGTH_SHORT);
            return;
        }
        if (StringUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "uui");
        httpManager.addParams("headimageFile", file, null);
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
    }

    /**
     * 修改年龄、婚恋、性经验
     */
    public void changeUUI(Map<String, String> map, IHttpCallBack callBack) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "uui");
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            httpManager.addParams(entry.getKey(), entry.getValue());
        }
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, callBack);
    }
}
