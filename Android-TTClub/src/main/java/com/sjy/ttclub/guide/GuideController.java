package com.sjy.ttclub.guide;

import android.os.Message;

import com.sjy.ttclub.account.login.LoginWindow;
import com.sjy.ttclub.account.login.SignupWindow;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;

/**
 * Created by zhangwulin on 2016/1/5.
 * email 1501448275@qq.com
 */
public class GuideController extends DefaultWindowController {

    private GuideLoginWindow mGuideLoginWindow;
    private GuideNewUserWindow mGuideNewUserWindow;

    public GuideController() {
        registerMessage(MsgDef.MSG_SHOW_GUIDE_LOGIN_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_GUIDE_NEW_USER_WINDOW);
        registerMessage(MsgDef.MSG_GET_GUIDE_WINDOW_STATE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
//        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS_CLOSE_GUIDE);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MsgDef.MSG_SHOW_GUIDE_LOGIN_WINDOW) {
            showGuideLoginWindow((Boolean) msg.obj);
        } else if (msg.what == MsgDef.MSG_SHOW_GUIDE_NEW_USER_WINDOW) {
            showGuideNewUserWindow();
        }
    }

    @Override
    public Object handleMessageSync(Message msg) {
        if (msg.what == MsgDef.MSG_GET_GUIDE_WINDOW_STATE) {
            if (mGuideLoginWindow != null || mGuideNewUserWindow != null) {
                return true;
            }
            return false;
        }
        return super.handleMessageSync(msg);
    }

    private void showGuideLoginWindow(boolean isLogout) {
        if (getCurrentWindow() instanceof GuideLoginWindow) {
            return;
        }
        GuideLoginWindow window = new GuideLoginWindow(mContext, this, isLogout);
        mGuideLoginWindow = window;
        mWindowMgr.pushWindow(window);
    }

    private void showGuideNewUserWindow() {
        AbstractWindow currentWindow = getCurrentWindow();
        if (currentWindow instanceof GuideNewUserWindow) {
            return;
        }
        boolean popBehideWindow = (currentWindow instanceof GuideLoginWindow);
        GuideNewUserWindow window = new GuideNewUserWindow(mContext, this);
        mGuideNewUserWindow = window;
        window.setPopBackWindowAfterPush(popBehideWindow);
        mWindowMgr.pushWindow(window, false);
        mGuideLoginWindow = null;
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                @Override
                public void run() {
                    shouldOpenNewUserWindow();
                }
            }, 20);
        }
    }

    @Override
    public void onWindowExitEvent(AbstractWindow window, boolean withAnimation) {
        super.onWindowExitEvent(window, withAnimation);
        if (window instanceof GuideNewUserWindow) {
            mGuideNewUserWindow = null;
            Notification notification = Notification.obtain(NotificationDef.N_GUIDE_FINISHED);
            NotificationCenter.getInstance().notify(notification);
            tryNotifyHomepageFirstShow();
        } else if (window instanceof GuideLoginWindow) {
            mGuideLoginWindow = null;
        }
    }

    private void shouldOpenNewUserWindow() {
        boolean shouldShowUserGuider = true;
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (info != null) {
            shouldShowUserGuider = StringUtils.parseInt(info.getIfHasUploadSex(), -1) == CommonConst.UPDATE_SEX_NO_COMPLETE;
        }
        if (shouldShowUserGuider) {
            showGuideNewUserWindow();
        } else {
            if (getCurrentWindow() instanceof GuideLoginWindow) {
                mGuideLoginWindow = null;
                mWindowMgr.popWindow(false);
                Notification notification = Notification.obtain(NotificationDef.N_GUIDE_FINISHED);
                NotificationCenter.getInstance().notify(notification);
                tryNotifyHomepageFirstShow();
            }
        }
    }

    private void tryNotifyHomepageFirstShow() {
        if (SettingFlags.getBooleanFlag(CommonConst.HOMEPAGE_FIRST_SHOW, true)) {
            Notification notification = Notification.obtain(NotificationDef.N_HOMEPAGE_IS_INIT_SHOW);
            NotificationCenter.getInstance().notify(notification, 300);
            SettingFlags.setFlag(CommonConst.HOMEPAGE_FIRST_SHOW, false);
        }
    }
}
