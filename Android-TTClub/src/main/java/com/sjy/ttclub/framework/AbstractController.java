/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.content.Context;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

public abstract class AbstractController implements INotify, UICallBacks {
    protected Context mContext;
    protected MsgDispatcher mDispatcher;
    protected WindowManager mWindowMgr;
    private ArrayList<Integer> mMessages;
    protected AppEnv mAppEnv;

    public AbstractController() {
        mContext = AppEnv.getInstance().getMainContext();
        mAppEnv = AppEnv.getInstance();
        mDispatcher = MsgDispatcher.getInstance();
        mWindowMgr = AppEnv.getInstance().getWindowManager();
    }

    public AbstractWindow getCurrentWindow() {
        return mWindowMgr.getCurrentWindow();
    }

    protected void registerMessage(int message) {
        if (mMessages == null) {
            mMessages = new ArrayList<Integer>();
            mDispatcher.register(this);
        }
        if (!mMessages.contains(message)) {
            mMessages.add(message);
        }
    }

    public void unregisterFromMsgDispatcher() {
        mDispatcher.unregister(this);
    }

    public boolean sendMessage(Message message) {
        return mDispatcher.sendMessage(message);
    }

    public void sendMessage(Message message, long delay) {
        mDispatcher.sendMessage(message, delay);
    }

    public boolean sendMessage(int what) {
        return mDispatcher.sendMessage(what);
    }

    public boolean sendMessage(int what, int arg1, int arg2, Object obj) {
        return mDispatcher.sendMessage(what, arg1, arg2, obj);
    }

    public boolean sendMessage(int what, int arg1, int arg2) {
        return mDispatcher.sendMessage(what, arg1, arg2);
    }

    public Object sendMessageSync(int what) {
        return mDispatcher.sendMessageSync(what);
    }

    public Object sendMessageSync(int what, Object obj) {
        return mDispatcher.sendMessageSync(what, obj);
    }

    public Object sendMessageSync(int what, int arg1, int arg2) {
        return mDispatcher.sendMessageSync(what, arg1, arg2);
    }

    public Object sendMessageSync(Message message) {
        return mDispatcher.sendMessageSync(message);
    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public Object handleMessageSync(Message msg) {
        return null;
    }

    @Override
    public ArrayList<Integer> messages() {
        return mMessages;
    }

    @Override
    public View onGetViewBehind(View view) {
        if (view instanceof AbstractWindow) {
            return mWindowMgr.getWindowBehind((AbstractWindow) view);
        }
        return null;
    }

    protected boolean onWindowBackKeyEvent(AbstractWindow window) {
        return false;
    }

    @Override
    public void onWindowExitEvent(AbstractWindow window, boolean withAnimation) {
        AbstractWindow currentWindow = getCurrentWindow();
        if (currentWindow == window) {
            mWindowMgr.popWindow(withAnimation);
        }
    }

    @Override
    public boolean onWindowKeyEvent(AbstractWindow window, int keyCode, KeyEvent event) {
        boolean retVal = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (AbstractWindow.isHaveKeyDownEvent) {
                if (!onWindowBackKeyEvent(window)) {
                    onWindowExitEvent(window, true);
                }
            }
            retVal = true;
        }
        return retVal;
    }

    @Override
    public void onWindowStateChange(AbstractWindow window, int stateFlag) {

    }

    @Override
    public void notify(Notification notification) {

    }
}
