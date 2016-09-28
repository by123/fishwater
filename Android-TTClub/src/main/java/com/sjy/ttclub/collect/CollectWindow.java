package com.sjy.ttclub.collect;

import android.content.Context;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.TabWindow;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/23.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectWindow extends TabWindow {

    public CollectWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.collect_window_title);
        setTabbarInTitlebar(false);
        initActionBar();
        initTab();
    }

    private void initTab() {
        CollectTabView tabView = new CollectTabView(getContext(), CommonConst.COLLECT_TYPE_ARTICLE);
        addTab(tabView);

        tabView = new CollectTabView(getContext(), CommonConst.COLLECT_TYPE_PRODUCT);
        addTab(tabView);

        tabView = new CollectTabView(getContext(), CommonConst.COLLECT_TYPE_POST);
        addTab(tabView);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.collect_edit));
        item.setItemId(MenuItemIdDef.TITLEBAR_EDIT);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (MenuItemIdDef.TITLEBAR_EDIT == itemId) {
            handleEditClick();
        }
    }

    public void handleEditClick() {
        boolean isEditMode = isInEditMode();
        if (isEditMode) {
            exitEditState();
        } else {
            enterEditState();
        }
        isEditMode = isInEditMode();
        String text = ResourceHelper.getString(R.string.collect_edit);
        if (isEditMode) {
            text = ResourceHelper.getString(R.string.collect_edit_finish);
        }
        TitleBarActionItem item = getTitleBarInner().getActionBar().getItem(MenuItemIdDef.TITLEBAR_EDIT);
        item.setText(text);
        CollectTabView tab = (CollectTabView) getCurrentTab();
        tab.setEditMode(isEditMode);
    }
}
