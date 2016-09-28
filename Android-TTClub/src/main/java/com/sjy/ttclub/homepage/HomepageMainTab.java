package com.sjy.ttclub.homepage;

import android.content.Context;
import android.view.View;

import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.account.login.LoginPanel;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.UICallBacks;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.widget.TabPager;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageMainTab extends AbstractWindow implements IMainTabView, TabPager.IScrollable {
    private HomepageMainView mMainView;

    public HomepageMainTab(Context context, UICallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        mMainView = new HomepageMainView(getContext());
        getBaseLayer().addView(mMainView, getBaseLayerLP());
        StatsModel.stats(StatsKeyDef.HOMEPAGE_VIEW);
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == IMainTabView.TAB_TO_SHOW) {
            StatsModel.stats(StatsKeyDef.HOMEPAGE_VIEW);
            mMainView.onResume();
        } else if (tabChangedFlag == IMainTabView.TAB_TO_HIDE) {
            mMainView.onPause();
        }
    }

    @Override
    public View getView() {
        return this;
    }

    public void setMainViewCallback(HomepageMainView.HomepageMainViewCallback callback) {
        mMainView.setMainViewCallback(callback);
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_HIDE) {
            mMainView.onPause();
        } else if (stateFlag == STATE_ON_SHOW) {
            mMainView.onResume();
        }
    }

    @Override
    public boolean canScroll(boolean scrollLeft) {
        return mMainView.canScroll();
    }
}
