
package com.sjy.ttclub.framework;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarCommandId;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.OnTabChangedListener;
import com.sjy.ttclub.widget.TabWidget;

import java.util.ArrayList;

public class TabWindow extends DefaultWindow implements OnTabChangedListener {

    protected TabWidget mTabWidget;

    private static final int DEFAULT_TAB_COUNT = 2;
    private ArrayList<ITabView> mTabViewsArray = new ArrayList<ITabView>(DEFAULT_TAB_COUNT);
    private int mTabCount;

    public TabWindow(Context context, IDefaultWindowCallBacks callBacks) {
        this(context, callBacks, WindowLayerType.ONLY_USE_BASE_LAYER);
    }

    public TabWindow(Context context, IDefaultWindowCallBacks callBacks, WindowLayerType useLayerType) {
        super(context, callBacks, useLayerType);
        onCreateContent();
    }

    public TabWindow(Context context, IDefaultWindowCallBacks callBacks, boolean isFullScreen) {
        super(context, callBacks, isFullScreen);
        onCreateContent();
    }

    public void setTabbarInTitlebar(boolean enable) {
        if (enable) {
            if (null != this.getTitleBarInner()) {
                getTitleBarInner().hideTitleView();
            }
            moveTabBarToTitleBar();
        } else {
            if (null != this.getTitleBarInner()) {
                getTitleBarInner().showTitleView();
            }
            moveTabBarToTabWidget();
        }
    }

    public void addTab(ITabView tabView) {

        String title = tabView.getTitle();

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.tabbar_textsize));

        mTabWidget.addTab(tabView.getTabView(), titleView);
        mTabViewsArray.add(tabView);
        ++mTabCount;
    }

    public void setCurrentTab(int index) {
        mTabWidget.snapToTab(index, false);
    }
    public void setCurrentTab(int index,boolean enableAnim) {
        mTabWidget.snapToTab(index, enableAnim);
    }
    public int getCurrentTabIndex() {
        return mTabWidget.getCurrentTabIndex();
    }

    public ITabView getCurrentTab() {
        return mTabViewsArray.get(getCurrentTabIndex());
    }

    public int getTabSize() {
        return mTabViewsArray.size();
    }

    public void reset() {
        if (mTabWidget != null) {
            mTabWidget.reset();
        }
    }

    private View onCreateContent() {
        TabWidget tabWidget = new TabWidget(getContext());
        tabWidget.setOnTabChangedListener(this);
        mTabWidget = tabWidget;
        getBaseLayer().addView(tabWidget, getContentLPForBaseLayer());
        return tabWidget;
    }

    public void snapToTab(int index) {
        mTabWidget.snapToTab(index);
    }

    public void snapToTab(int index, boolean withAnimation) {
        mTabWidget.snapToTab(index, withAnimation);
    }


    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case AbstractWindow.STATE_AFTER_PUSH_IN:
                // 迟一桢再加载
                for (int i = 0; i < mTabCount; ++i) {
                    ITabView child = mTabViewsArray.get(i);
                    child.onPrepareContentView();
                }

                if (mTabWidget.getCurrentTabIndex() >= 0 && mTabWidget.getCurrentTabIndex() < mTabViewsArray.size()) {
                    mTabViewsArray.get(mTabWidget.getCurrentTabIndex()).onTabChanged(ITabView.TAB_AFTER_PUSH_IN);
                }

                break;

            case AbstractWindow.STATE_ON_SHOW:
            case AbstractWindow.STATE_BEFORE_PUSH_IN:
            case AbstractWindow.STATE_BEFORE_SWITCH_IN:
                if (mTabWidget.getCurrentTabIndex() >= 0 && mTabWidget.getCurrentTabIndex() < mTabViewsArray.size()) {
                    mTabViewsArray.get(mTabWidget.getCurrentTabIndex()).onTabChanged(ITabView.TAB_TO_SHOW);
                }
                break;

            case AbstractWindow.STATE_ON_HIDE:
            case AbstractWindow.STATE_BEFORE_POP_OUT:
            case AbstractWindow.STATE_BEFORE_SWITCH_OUT:
                if (mTabWidget.getCurrentTabIndex() >= 0 && mTabWidget.getCurrentTabIndex() < mTabViewsArray.size()) {
                    mTabViewsArray.get(mTabWidget.getCurrentTabIndex()).onTabChanged(ITabView.TAB_TO_HIDE);
                }
                break;
        }
    }

    @Override
    public void onTabChanged(int newTabIndex, int oldTabIndex) {
        if (newTabIndex != oldTabIndex) {

            if (getTitleBarInner() != null) {
                getTitleBarInner().acceptCommand(TitleBarCommandId.TAB_SWITCH_ACTION_BAR, newTabIndex);
            }

            if (oldTabIndex > -1 && oldTabIndex < mTabViewsArray.size()) {
                ITabView chilcToHide = mTabViewsArray.get(oldTabIndex);
                chilcToHide.onTabChanged(ITabView.TAB_TO_HIDE);
            }
            ITabView childToShow = mTabViewsArray.get(newTabIndex);
            childToShow.onTabChanged(ITabView.TAB_TO_SHOW);
        }
    }

    @Override
    public void onTabChangeStart(int newTabIndex, int oldTabIndex) {

    }

    @Override
    public void onTabChangedByTitle(int tabIndex) {
    }

    public void addTabBarDecorView(View decorView, RelativeLayout.LayoutParams lp) {
        mTabWidget.addTabBarDecorView(decorView, lp);
    }

    public void removeTabBarDecorView(View decorView) {
        mTabWidget.removeTabBarDecorView(decorView);
    }

    public View getTabTitleView(int index) {
        return mTabWidget.getTabTitleView(index);
    }

    private void moveTabBarToTitleBar() {
        View view = mTabWidget.peelingTabbarOff();
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        mTabWidget.setTabbarContainerBg(new ColorDrawable(Color.TRANSPARENT));
        if (null != this.getTitleBarInner()) {
            this.getTitleBarInner().setTabView(view);
        }
    }

    private void moveTabBarToTabWidget() {
        mTabWidget.setTabbarContainerBg(new ColorDrawable(TitleBar.getBgColor()));
        mTabWidget.pullTabbarIn();
    }


    @Override
    public void notify(Notification notification) {
        super.notify(notification);
    }

    @Override
    protected void onEnterEditState() {
        super.onEnterEditState();
        mTabWidget.lock();
    }

    @Override
    protected void onExitEditState() {
        super.onExitEditState();
        mTabWidget.unlock();
    }

    public void replaceTabContent(int index, View view) {
        mTabWidget.replaceContent(index, view);
    }

    public void recoverTabContent(int index) {
        mTabWidget.recoverContent(index);
    }
}
    
