/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

public abstract class TitleBar extends LinearLayout implements
        View.OnClickListener {

    private FrameLayout mLayoutNavigation;
    private BackActionButton mBackActionButton;

    protected FrameLayout mFrameLayoutTab;

    protected ActionBar mActionBar;

    protected ITitleBarListener mTitleBarListener;

    public TitleBar(Context context, ITitleBarListener titleBarListener) {
        super(context);
        mTitleBarListener = titleBarListener;
        initComponent();
        initListener();
    }

    private void initComponent() {
        Context context = getContext();

        mLayoutNavigation = new FrameLayout(context);
        mLayoutNavigation.setLayoutParams(new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1f));

        mBackActionButton = new BackActionButton(getContext());
        mBackActionButton.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL));
        mBackActionButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        mLayoutNavigation.addView(mBackActionButton);

        mFrameLayoutTab = new FrameLayout(context);
        mFrameLayoutTab.setLayoutParams(new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 0f));

        mActionBar = createActionBar();
        mActionBar.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        this.addView(mLayoutNavigation);
        this.addView(mFrameLayoutTab);
        this.addView(mActionBar);

        this.setBackgroundColor(TitleBar.getBgColor());
    }

    private void initListener() {
        mBackActionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mTitleBarListener != null) {
                    mTitleBarListener.onBackActionButtonClick();
                }
            }
        });

    }

    public abstract ActionBar createActionBar();

    public abstract void acceptCommand(int commandId, Object object);

    public void setTitle(String title) {
        mBackActionButton.getTextView().setVisibility(View.VISIBLE);
        mBackActionButton.getTextView().setText(title);
    }

    public void setTitle(int titleResId) {
        mBackActionButton.getTextView().setVisibility(View.VISIBLE);
        mBackActionButton.getTextView().setText(titleResId);
    }

    public void hideBackActionButton() {
        mBackActionButton.setVisibility(View.GONE);
    }

    public void showBackActionButton() {
        mBackActionButton.setVisibility(View.VISIBLE);
    }

    public void hideTitleView() {
        mBackActionButton.getTextView().setVisibility(View.GONE);
        LayoutParams navigationLayoutParams = (LayoutParams) mFrameLayoutTab
                .getLayoutParams();
        navigationLayoutParams.weight = 3f;

        LayoutParams actionBarLayoutParams = (LayoutParams) mActionBar
                .getLayoutParams();
        actionBarLayoutParams.width = 0;
        actionBarLayoutParams.weight = 1f;
    }

    public void showTitleView() {
        if (TextUtils.isEmpty(mBackActionButton.getTextView().getText())) {
            mBackActionButton.getTextView().setVisibility(View.GONE);
        } else {
            mBackActionButton.getTextView().setVisibility(View.VISIBLE);
        }

        LayoutParams navigationLayoutParams = (LayoutParams) mFrameLayoutTab
                .getLayoutParams();
        navigationLayoutParams.weight = 0f;

        LayoutParams actionBarLayoutParams = (LayoutParams) mActionBar
                .getLayoutParams();
        actionBarLayoutParams.width = LayoutParams.WRAP_CONTENT;
        actionBarLayoutParams.weight = 0f;
    }

    public String getTitle() {
        return mBackActionButton.getTextView().getText().toString();
    }

    public void setTitleIcon(Drawable drawable) {
        mBackActionButton.setBackActionIcon(drawable);
    }

    public void setTitleColor(int color) {
        mBackActionButton.setTextColor(color);
    }

    public void setTabView(View view) {
        mFrameLayoutTab.addView(view);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TitleBarActionItem) {

            mTitleBarListener
                    .onTitleBarActionItemClick(((TitleBarActionItem) v)
                            .getItemId());
        }
    }

    protected TextView getTitleView() {
        return mBackActionButton.getTextView();
    }

    public void switchActionBar(int actionBarId, boolean animated) {
        mActionBar.switchActionItems(actionBarId, animated);
    }

    public void setActionItems(List<TitleBarActionItem> items) {
        mActionBar.setActionItems(items);
    }

    public ActionBar getActionBar() {
        return mActionBar;
    }


    public static int getBgColor() {
        return ResourceHelper.getColor(R.color.title_bg_color);

    }

}
