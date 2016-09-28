package com.sjy.ttclub;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.common.BannerHelper;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.push.PushManager;
import com.sjy.ttclub.system.MarketChannelManager;
import com.sjy.ttclub.util.ApplicationExitHelper;

import java.util.ArrayList;


/**
 * Created by linhz on 2015/11/4.
 * Email: linhaizhong@ta2she.com
 */
public class TTClubController extends DefaultWindowController {

    private static TTClubController sInstance;

    private TTClubWindow mTTClubWindow;
    private ApplicationExitHelper mHelper;
    private ArrayList<InitEventInfo> mEventList = new ArrayList<InitEventInfo>();

    private boolean mIsStartupFinished = false;

    public TTClubController() {
        registerMessage(MsgDef.MSG_SHOW_MAIN_WINDOW);
        registerMessage(MsgDef.MSG_INIT);
        registerMessage(MsgDef.MSG_INIT_ACTION_EVENTS);

        NotificationCenter.getInstance().register(this, NotificationDef.N_SPLASH_FINISHED);
        NotificationCenter.getInstance().register(this, NotificationDef.N_GUIDE_FINISHED);
        NotificationCenter.getInstance().register(this, NotificationDef.N_PRIVACY_FINISHED);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MARKET_AUDITED_CHANGED);
        sInstance = TTClubController.this;
    }

    /*package*/
    static TTClubController getInstance() {
        return sInstance;
    }

    /*package*/ void onCreate(Activity activity) {
        new TTClubStartupManager().start();
    }

    /*package*/ void onPause(Activity activity) {

    }

    /*package*/ void onResume(Activity activity) {

    }

    /*package*/ void onDestory(Activity activity) {

    }

    private void setupMainWindow() {
        if (mTTClubWindow == null) {
            mTTClubWindow = new TTClubWindow(mContext, this);
        }
        if (mTTClubWindow.getParent() == null) {
            mWindowMgr.createWindowStack(mTTClubWindow);
        }
    }

    /*package*/ AbstractWindow getSplashScreen() {
        Object window = mDispatcher.sendMessageSync(MsgDef.MSG_GET_SPLASH_WINDOW);
        if (window instanceof AbstractWindow) {
            return (AbstractWindow) window;
        }
        return null;
    }

    private void init() {
        EmoticonsUtils.initEmoticonsDB(mContext);
        MarketChannelManager.getInstance().tryGetMarketAuditState();
        AppMd5Helper.getInstance().tryGetAppMd5();
    }

    @Override
    public boolean onWindowBackKeyEvent(AbstractWindow window) {
        AbstractWindow currentWindow = getCurrentWindow();
        AbstractWindow rootWindow = mWindowMgr.getCurrentRootWindow();
        if (currentWindow == rootWindow) {
            if (mHelper == null) {
                mHelper = new ApplicationExitHelper(mContext);
            }
            mHelper.exitApplication();
        }
        return super.onWindowBackKeyEvent(window);
    }

    @Override
    public Object handleMessageSync(Message msg) {
        Object result = null;
        if (msg.what == MsgDef.MSG_SHOW_MAIN_WINDOW) {
            setupMainWindow();
        } else if (msg.what == MsgDef.MSG_INIT) {
            init();
        } else if (msg.what == MsgDef.MSG_INIT_ACTION_EVENTS) {
            if (msg.obj instanceof InitEventInfo) {
                addInitActionEvent((InitEventInfo) msg.obj);
            }
            tryProcessInitActions();
        }
        return result;
    }

    private void addInitActionEvent(InitEventInfo info) {
        mEventList.add(info);
    }

    private void tryProcessInitActions() {
        if (!mIsStartupFinished) {
            return;
        }
        Object result = mDispatcher.sendMessageSync(MsgDef.MSG_GET_GUIDE_WINDOW_STATE);

        boolean isGuideShowing = false;
        if (result instanceof Boolean) {
            isGuideShowing = (Boolean) result;
        }
        if (isGuideShowing) {
            return;
        }
        result = mDispatcher.sendMessageSync(MsgDef.MSG_GET_APP_PRIVACY_STATE);
        boolean isPrivacyShowing = false;
        if (result instanceof Boolean) {
            isPrivacyShowing = (Boolean) result;
        }
        if (isPrivacyShowing) {
            return;
        }

        ArrayList<InitEventInfo> tempList = (ArrayList<InitEventInfo>) mEventList.clone();
        for (InitEventInfo info : tempList) {
            if (info.type == InitEventInfo.TYPE_BANNER) {
                handleBannerEvents(info.banner);
            } else if (info.type == InitEventInfo.TYPE_PUSH) {
                handlePushEvents(info.bundle);
            }
        }
        mEventList.clear();
    }

    private void handleBannerEvents(Banner banner) {
        if (banner == null) {
            return;
        }

        BannerHelper.handleBannerClick(mContext, banner);
    }

    private void handlePushEvents(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        PushManager.handlePushEvent(bundle);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_SPLASH_FINISHED ||
                notification.id == NotificationDef.N_GUIDE_FINISHED ||
                notification.id == NotificationDef.N_PRIVACY_FINISHED) {

            if (notification.id == NotificationDef.N_SPLASH_FINISHED) {
                mIsStartupFinished = true;
            }
            tryProcessInitActions();
        }else if(notification.id == NotificationDef.N_MARKET_AUDITED_CHANGED){
            if(mTTClubWindow != null){
                mTTClubWindow.resetTabs();
            }
        }
    }
}
