/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.ActivityEx;
import com.sjy.ttclub.framework.AppEnv;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.umeng.UmengManager;
import com.sjy.ttclub.util.SystemUtil;


public class TTClubActivity extends ActivityEx implements INotify {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareSoftInputMethodResize();
        TTClubController.getInstance().onCreate(this);
        HttpManager.startNetStateReceiver(this);
        UmengManager.getInstance().onAppStart();

        handleIntent(getIntent());
    }


    @Override
    protected void onPause() {
        TTClubController.getInstance().onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TTClubController.getInstance().onResume(this);
    }

    @Override
    protected void onDestroy() {
        TTClubController.getInstance().onDestory(this);
        HttpManager.stopNetStateReceiver(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean isPush = intent.getBooleanExtra(InitEventInfo.KEY_PUSH, false);
        if (isPush) {
            InitEventInfo info = new InitEventInfo();
            info.type = InitEventInfo.TYPE_PUSH;
            info.bundle = intent.getExtras();
            Message msg = Message.obtain();
            msg.what = MsgDef.MSG_INIT_ACTION_EVENTS;
            msg.obj = info;
            MsgDispatcher.getInstance().sendMessageSync(msg);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        AppEnv appEnv = AppEnv.getInstance();
        if (appEnv != null) {
            appEnv.destroy();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //如果闪屏存在的话，将key event派发给闪屏
        AbstractWindow splashScreen = TTClubController.getInstance().getSplashScreen();
        if (splashScreen != null) {
            return splashScreen.dispatchKeyEvent(event);
        }


        final AbstractWindow fallbackView = TTClubController.getInstance().getCurrentWindow();
        if (fallbackView != null) {
            return fallbackView.dispatchKeyEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void notify(Notification notification) {

    }

    private View mChildOfContent;
    private int mUsableHeightPrevious;
    private FrameLayout.LayoutParams mFrameLayoutParams;
    private Rect mTempRect = new Rect();

    private void prepareSoftInputMethodResize() {
        FrameLayout content = (FrameLayout) this.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        mFrameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (mUsableHeightPrevious == 0) {
            mUsableHeightPrevious = usableHeightNow;
        }
        if (usableHeightNow != mUsableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 6)) {
                if (SystemUtil.isTransparentStatusBarEnable()) {
                    heightDifference -= SystemUtil.getStatusBarHeight(this);
                }
                NotificationCenter.getInstance().notify(Notification.obtain(NotificationDef.N_MESSAGE_SOFTWARE_SHOW));
                // keyboard probably just became visible
                mFrameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                int windowHeight = usableHeightNow;
                if (SystemUtil.isTransparentStatusBarEnable()) {
                    windowHeight = usableHeightNow + SystemUtil.getStatusBarHeight(this);
                }
                if (windowHeight > usableHeightSansKeyboard) {
                    windowHeight = usableHeightSansKeyboard;
                }
                mFrameLayoutParams.height = windowHeight;
                NotificationCenter.getInstance().notify(Notification.obtain(NotificationDef.N_MESSAGE_SOFTWARE_HIDE));
            }

            mChildOfContent.requestLayout();
            mUsableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        mChildOfContent.getWindowVisibleDisplayFrame(mTempRect);
        return mTempRect.height();
    }

}
