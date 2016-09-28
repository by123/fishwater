package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.view.View;

import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.TabWindow;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;

/**
 * Created by gangqing on 2015/12/24.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackWindow extends TabWindow {
    private FeedbackCommitTabView mCommitTabView;
    private FeedbackRecordTabView mRecordTabView;

    public FeedbackWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTabbarInTitlebar(true);
        initTab();
    }

    @SuppressWarnings("ResourceType")
    protected View onCreateTitleBar() {
        FeedbackTitleBar titleBar = new FeedbackTitleBar(getContext(), this);
        titleBar.setLayoutParams(getTitleBarLPForBaseLayer());
        titleBar.setId(ID_TITLEBAR);
        getBaseLayer().addView(titleBar);
        return titleBar;
    }

    private void initTab() {
        mCommitTabView = new FeedbackCommitTabView(getContext());
        addTab(mCommitTabView);

        mRecordTabView = new FeedbackRecordTabView(getContext());
        addTab(mRecordTabView);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (MenuItemIdDef.TITLEBAR_COMMIT == itemId) {
            mCommitTabView.commit();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_POP_OUT) {
            mCommitTabView.destroy();
            DeviceManager.getInstance().hideInputMethod();
        } else if (stateFlag == STATE_ON_HIDE) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }
}
