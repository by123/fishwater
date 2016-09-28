package com.sjy.ttclub;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.system.MarketChannelManager;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.OnTabChangedListener;
import com.sjy.ttclub.widget.TabWidget;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/4.
 * Email: linhaizhong@ta2she.com
 */
public class TTClubWindow extends DefaultWindow {
    private TabWidget mTabWidget;
    private ArrayList<IMainTabView> mTabViewsArray = new ArrayList<>(4);

    public TTClubWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        initTabWidget();
        getBaseLayer().addView(mTabWidget, getBaseLayerLP());
    }

    @Override
    protected View onCreateTitleBar() {
        return null;
    }

    private void initTabWidget() {
        mTabWidget = new TabWidget(getContext(), true);
        mTabWidget.setDragSwitchEnable(true);
        mTabWidget.setTabbarHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mTabWidget.hideCursor();
        mTabWidget.setEnableSnapAnimOnTitleClick(false);
        mTabWidget.setOnTabChangedListener(new OnTabChangedListener() {
            @Override
            public void onTabChangeStart(int newTabIndex, int oldTabIndex) {

            }

            @Override
            public void onTabChanged(int newTabIndex, int oldTabIndex) {
                if (newTabIndex != oldTabIndex) {

                    if (oldTabIndex > -1 && oldTabIndex < mTabViewsArray.size()) {
                        IMainTabView chilcToHide = mTabViewsArray.get(oldTabIndex);
                        chilcToHide.onTabChanged(ITabView.TAB_TO_HIDE);
                    }

                    IMainTabView childToShow = mTabViewsArray.get(newTabIndex);
                    childToShow.onTabChanged(ITabView.TAB_TO_SHOW);
                }
            }

            @Override
            public void onTabChangedByTitle(int tabIndex) {

            }
        });
        mTabWidget.setTabItemTextColor(TabWidget.TABBG_DEFAULT, ResourceHelper.getColor(R.color
                .home_text_default_color));
        mTabWidget.setTabItemTextColor(TabWidget.TABBG_SELECTED, ResourceHelper.getColor(R.color
                .home_text_selected_color));
        mTabWidget.setTabbarContainerBg(new ColorDrawable(ResourceHelper.getColor(R.color.home_bottom_bar_bg)));
        addHomepageTab();
        addCommunityTab();
        addShoppingTab();
        addAccountTab();
    }

    private void addHomepageTab() {
        IMainTabView homepageTab = (IMainTabView) MsgDispatcher.getInstance().sendMessageSync(MsgDef
                .MSG_GET_HOMEPAGE_TAB);
        String title = ResourceHelper.getString(R.string.home_tab_homepage);
        Drawable drawable = ResourceHelper.getDrawable(R.drawable.tab_homepage);
        TextView titleView = createTabTitleView(title, drawable);
        mTabWidget.addTab(homepageTab.getView(), titleView);
        mTabViewsArray.add(homepageTab);
    }

    private void addCommunityTab() {
        IMainTabView tabView = (IMainTabView) MsgDispatcher.getInstance().sendMessageSync(MsgDef
                .MSG_GET_COMMUNITY_TAB);
        String title = ResourceHelper.getString(R.string.home_tab_community);
        Drawable drawable = ResourceHelper.getDrawable(R.drawable.tab_community);
        TextView titleView = createTabTitleView(title, drawable);
        if (MarketChannelManager.getInstance().isMarketAudited()) {
            mTabWidget.addTab(tabView.getView(), titleView);
            mTabViewsArray.add(tabView);
        }
    }

    private void addShoppingTab() {
        IMainTabView tabView = (IMainTabView) MsgDispatcher.getInstance().sendMessageSync(MsgDef
                .MSG_GET_SHOPPING_TAB);
        String title = ResourceHelper.getString(R.string.home_tab_shopping);
        Drawable drawable = ResourceHelper.getDrawable(R.drawable.tab_shopping);
        TextView titleView = createTabTitleView(title, drawable);
        mTabWidget.addTab(tabView.getView(), titleView);
        mTabViewsArray.add(tabView);
    }

    private void addAccountTab() {
        IMainTabView tabView = (IMainTabView) MsgDispatcher.getInstance().sendMessageSync(MsgDef
                .MSG_GET_ACCOUNT_TAB);
        String title = ResourceHelper.getString(R.string.home_tab_account);
        Drawable drawable = ResourceHelper.getDrawable(R.drawable.tab_account);
        TextView titleView = createTabTitleView(title, drawable);
        mTabWidget.addTab(tabView.getView(), titleView);
        mTabViewsArray.add(tabView);
    }

    private TextView createTabTitleView(String title, Drawable drawable) {
        TextView titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_10));
        titleView.setText(title);
        titleView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        titleView.setCompoundDrawablePadding(ResourceHelper.getDimen(R.dimen.space_4));
        int padding = ResourceHelper.getDimen(R.dimen.space_8);
        titleView.setPadding(padding, padding, padding, padding);
        return titleView;
    }

    public void resetTabs(){
        mTabWidget.reset();
        mTabViewsArray.clear();
        addHomepageTab();
        addCommunityTab();
        addShoppingTab();
        addAccountTab();
        mTabWidget.snapToTab(0);

    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        for (IMainTabView tabView : mTabViewsArray) {
            tabView.onWindowStateChange(stateFlag);
        }
    }
}
