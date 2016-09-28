package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.UICallBacks;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.TabPager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class CommunityMainTab extends AbstractWindow implements IMainTabView, TabPager.IScrollable {

    private CommunityMainTabView mFindView;

    public CommunityMainTab(Context context, UICallBacks callBacks) {
        super(context, callBacks,true);
        setEnableSwipeGesture(false);
        initTabWindow();
    }

    private void initTabWindow() {
        mFindView = new CommunityMainTabView(getContext());
        getBaseLayer().addView(mFindView, getBaseLayerLP());
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == ITabView.TAB_TO_HIDE) {
            mFindView.setEnableBannerAnim(false);
        } else if (tabChangedFlag == ITabView.TAB_TO_SHOW) {
            StatsModel.stats(StatsKeyDef.COMMUNITY_TAB);
            StatsModel.stats(StatsKeyDef.COMMUNITY_CARE_DISCOVERY);
            mFindView.setEnableBannerAnim(true);
        }
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_HIDE) {
            mFindView.setEnableBannerAnim(false);
        } else if (stateFlag == STATE_ON_SHOW) {
            mFindView.setEnableBannerAnim(true);
        }
        if (stateFlag == STATE_AFTER_PUSH_IN) {

        }
    }

    @Override
    public boolean canScroll(boolean scrollLeft) {
        return mFindView.canScrollTab();
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
    }

    /**
     * @return void 返回类型
     * @throws
     * @说 明:首次登陆社区
     * @参 数:
     */
    private void tryFirstLoginRequest() {
        IHttpManager iHttpManager = HttpManager.getBusinessHttpManger();
        iHttpManager.addParams("a", "ubap");
        iHttpManager.request(HttpUrls.USER_URL, HttpMethod.POST, null, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                handlerOnRequestFistLogin(result);
            }

            @Override
            public void onError(String errorStr, int code) {
                handlerOnRequestFistLoginFail(code);
            }
        });
    }

    private void handlerOnRequestFistLogin(String result) {
        if (result != null && result.length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("status");
                if (HttpCode.SUCCESS_CODE == code) {
                    JSONObject obj = jsonObject.getJSONObject("data");
                    int islevelUp = StringUtils.parseInt(obj.getString("isLevelUp"), 0);
                    int newLevel = StringUtils.parseInt(obj.getString("newLevel"));
                    int obtainedExp = StringUtils.parseInt(obj.getString("obtainedExp"));
                    if (islevelUp == 0) {
                        ToastHelper.showToast(getContext(), "首次登录社区！" + "经验+" + obtainedExp, Toast.LENGTH_SHORT);
                    } else {
                        ToastHelper.showToast(getContext(), "首次登录社区！" + "升到" + newLevel + "级", Toast.LENGTH_SHORT);
                    }
                } else {
                    handlerOnRequestFistLoginFail(CommunityConstant.ERROR_TYPE_DATA);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handlerOnRequestFistLoginFail(CommunityConstant.ERROR_TYPE_DATA);
            }
        } else {
            handlerOnRequestFistLoginFail(CommunityConstant.ERROR_TYPE_DATA);
        }
    }

    private void handlerOnRequestFistLoginFail(int errorCode) {
        switch (errorCode) {
            case 104:
                break;
            case 101:
                //已登录今天
                break;
            default:
                break;
        }
    }
}
