/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import android.content.Context;

public class DefaultActionBar extends ActionBar {

    public DefaultActionBar(Context context, OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public void switchActionItems(int actionBarId, boolean animated) {
    }

    @Override
    public void enterEditState() {
        if (mItems == null || mItems.size() == 0) {
            return;
        }
        for (TitleBarActionItem item : mItems) {
            item.setEnabled(false);
        }
    }

    @Override
    public void outEditState() {
        if (mItems == null || mItems.size() == 0) {
            return;
        }
        for (TitleBarActionItem item : mItems) {
            item.setEnabled(true);
        }
    }

    @Override
    public void acceptCommand(int commandId, Object object) {

    }

}