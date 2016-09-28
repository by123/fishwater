/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.view.KeyEvent;
import android.view.View;


public interface UICallBacks extends MsgDispatcher.IMessageHandler {
    void onWindowExitEvent(AbstractWindow window, boolean withAnimation);

    void onWindowStateChange(AbstractWindow window, int stateFlag);

    boolean onWindowKeyEvent(AbstractWindow window, int keyCode, KeyEvent event);

    View onGetViewBehind(View view);
}
