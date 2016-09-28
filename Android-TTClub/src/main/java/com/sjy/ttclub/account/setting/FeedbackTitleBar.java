package com.sjy.ttclub.account.setting;

import android.content.Context;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.ITitleBarListener;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.framework.ui.TitleBarCommandId;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/28.
 * Email: linhaizhong@ta2she.com
 */
public class FeedbackTitleBar extends DefaultTitleBar {
    private static final int ACTION_BAR_COMMIT = 1;
    private static final int ACTION_BAR_RECORD = 2;

    public FeedbackTitleBar(Context context, ITitleBarListener titleBarListener) {
        super(context, titleBarListener);
    }

    @Override
    public void acceptCommand(int commandId, Object object) {
        switch (commandId) {
            case TitleBarCommandId.TAB_SWITCH_ACTION_BAR:
                int index = (Integer) object;
                if (index == 0) {
                    switchActionItems(ACTION_BAR_COMMIT, false);
                } else {
                    switchActionItems(ACTION_BAR_RECORD, false);
                }
                break;

            default:
                break;
        }
    }

    private void switchActionItems(int actionBarId, boolean animated) {
        if (actionBarId == ACTION_BAR_COMMIT) {
            ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
            TitleBarActionItem item = new TitleBarActionItem(getContext());
            item.setText(ResourceHelper.getString(R.string.account_feedback_commit));
            item.setItemId(MenuItemIdDef.TITLEBAR_COMMIT);
            actionList.add(item);

            setActionItems(actionList);
        } else {
            setActionItems(null);
        }
    }
}
