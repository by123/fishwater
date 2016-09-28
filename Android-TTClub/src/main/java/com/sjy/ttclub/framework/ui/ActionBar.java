/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;

public abstract class ActionBar extends LinearLayout {

    public List<TitleBarActionItem> mItems;
    protected OnClickListener mOnClickListener;

    public ActionBar(Context context, OnClickListener onClickListener) {
        super(context);
        mOnClickListener = onClickListener;
        initComponent();
    }

    private void initComponent() {
        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    }

    public TitleBarActionItem getItem(int id) {
        TitleBarActionItem res = null;
        if (null != mItems) {
            for (TitleBarActionItem item : mItems) {
                if (item.getItemId() == id) {
                    res = item;
                    break;
                }
            }
        }

        return res;
    }

    public void setActionItems(List<TitleBarActionItem> items) {
        this.removeAllViews();
        mItems = items;
        if (items == null || items.size() == 0) {
            return;
        }
        for (TitleBarActionItem item : mItems) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            item.setLayoutParams(layoutParams);
            this.addView(item);
            item.setOnClickListener(mOnClickListener);
        }
    }

    public abstract void switchActionItems(int actionBarId, boolean animated);

    public abstract void acceptCommand(int commandId, Object object);


    public abstract void enterEditState();

    public abstract void outEditState();


}
