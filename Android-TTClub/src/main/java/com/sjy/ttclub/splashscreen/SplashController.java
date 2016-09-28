/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */


package com.sjy.ttclub.splashscreen;

import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.ThreadManager;


public class SplashController extends DefaultWindowController {

    private static final int ANIM_DURATION = 400;
    private SplashWindow mSplashWindow;
    private boolean mIsSplashWindowHiding = false;

    public SplashController() {
        registerMessage(MsgDef.MSG_CLOSE_SPLASH_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_SPLASH_WINDOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_APP_MD5_CHANGED);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_CLOSE_SPLASH_WINDOW) {
            Notification notification = Notification
                    .obtain(NotificationDef.N_SPLASH_FINISHED);
            NotificationCenter.getInstance().notify(notification);
            hideSplashWindow(msg.arg1 == 0);
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_APP_MD5_CHANGED) {
            AppMd5Helper.Md5Type type = (AppMd5Helper.Md5Type) notification.extObj;
            if (type == AppMd5Helper.Md5Type.SPLASH) {
                handleSplashDataChange();
            }
        }
    }

    private void handleSplashDataChange() {
        SplashHelper splashHelper = new SplashHelper();
        splashHelper.tryGetSplashData();
    }

    private void hideSplashWindow(boolean anim) {
        if (mIsSplashWindowHiding) {
            return;
        }
        if (mSplashWindow != null) {
            if (!anim) {
                removeSplashWindow();
            } else {
                AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(ANIM_DURATION);
                animation.setFillAfter(true);
                animation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        removeSplashWindow();
                    }
                });
                if (mSplashWindow != null) {
                    mIsSplashWindowHiding = true;
                    mSplashWindow.startAnimation(animation);
                }

                ThreadManager.postDelayed(ThreadManager.THREAD_UI,
                        new Runnable() {

                            @Override
                            public void run() {
                                if (mIsSplashWindowHiding) {
                                    removeSplashWindow();
                                }
                            }
                        }, ANIM_DURATION + 100);
            }
        }
    }

    private void removeSplashWindow() {
        mIsSplashWindowHiding = false;
        if (mSplashWindow != null) {
            mWindowMgr.removeExtLayerContent(mSplashWindow);
        }
        mSplashWindow = null;

    }

    private void showSplashWindow() {
        if (mSplashWindow != null) {
            return;
        }

        mSplashWindow = new SplashWindow(mContext, this);
        mWindowMgr.addExtLayerContent(mSplashWindow);
    }

    @Override
    public Object handleMessageSync(Message msg) {
        Object result = null;
        if (MsgDef.MSG_SHOW_SPLASH_WINDOW == msg.what) {
            showSplashWindow();
            sendHideSplashWindowMsg();
        } else if (MsgDef.MSG_GET_SPLASH_WINDOW == msg.what) {
            if (mIsSplashWindowHiding) {
                return null;
            }
            return mSplashWindow;
        }

        return result;
    }

    private void sendHideSplashWindowMsg() {
        if (SplashUtil.isShowSplash() && !SplashUtil.isImageSplash()) {
            mDispatcher.sendMessage(MsgDef.MSG_CLOSE_SPLASH_WINDOW, 4000);
        } else {
            mDispatcher.sendMessage(MsgDef.MSG_CLOSE_SPLASH_WINDOW, 2000);
        }
    }

}
