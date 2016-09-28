package com.sjy.ttclub.collect;

import android.os.Message;

import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by linhz on 2015/12/23.
 * Email: linhaizhong@ta2she.com
 */
public class CollectController extends DefaultWindowController {

    public CollectController() {
        registerMessage(MsgDef.MSG_SHOW_COLLECT_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_COLLECT_WINDOW) {
            showCollectWindow();
        }
    }

    private void showCollectWindow() {
        if (!AccountManager.getInstance().isLogin()) {
            mDispatcher.sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            return;
        }
        AbstractWindow window = getCurrentWindow();
        if (window instanceof CollectWindow) {
            return;
        }
        window = new CollectWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    @Override
    protected boolean onWindowBackKeyEvent(AbstractWindow window) {
        if (window instanceof CollectWindow) {
            CollectWindow collectWindow = (CollectWindow) window;
            if (collectWindow.isInEditMode()) {
                collectWindow.handleEditClick();
                return true;
            }

        }
        return super.onWindowBackKeyEvent(window);
    }
}
