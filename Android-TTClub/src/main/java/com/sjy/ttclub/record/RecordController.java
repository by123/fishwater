package com.sjy.ttclub.record;

import android.os.Message;

import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.record.peep.RecordPeepRuleInfo;
import com.sjy.ttclub.record.peep.RecordPeepWindow;
import com.sjy.ttclub.record.self.RecordSelfWindow;

/**
 * Created by linhz on 2016/1/4.
 * Email: linhaizhong@ta2she.com
 */
public class RecordController extends DefaultWindowController {

    public RecordController() {
        registerMessage(MsgDef.MSG_SHOW_RECORD_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_RECORD_PEEP_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_RECORD_SELF_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_RECORD_WINDOW) {
            showRecordWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_RECORD_PEEP_WINDOW) {
            if (msg.obj instanceof RecordPeepRuleInfo) {
                showPeepWindow((RecordPeepRuleInfo)msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_SHOW_RECORD_SELF_WINDOW) {
            showSelfRecordWindow();
        }
    }

    private void showLoginWindow() {
        AccountManager.getInstance().showLoginPanel();
    }

    private void showRecordWindow() {
        if (!AccountManager.getInstance().isLogin()) {
            showLoginWindow();
            return;
        }

        AbstractWindow window = getCurrentWindow();
        if (window instanceof RecordWindow) {
            return;
        }

        window = new RecordWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    private void showPeepWindow(RecordPeepRuleInfo ruleInfo) {
        if (!AccountManager.getInstance().isLogin()) {
            showLoginWindow();
            return;
        }

        AbstractWindow window = getCurrentWindow();
        if (window instanceof RecordPeepWindow) {
            return;
        }

        window = new RecordPeepWindow(mContext, this, ruleInfo);
        mWindowMgr.pushWindow(window);
    }

    private void showSelfRecordWindow() {
        if (!AccountManager.getInstance().isLogin()) {
            showLoginWindow();
            return;
        }

        AbstractWindow window = getCurrentWindow();
        if (window instanceof RecordSelfWindow) {
            return;
        }

        window = new RecordSelfWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }
}
