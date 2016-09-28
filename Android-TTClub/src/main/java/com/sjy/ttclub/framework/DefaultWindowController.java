/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;


public class DefaultWindowController extends AbstractController implements
        IDefaultWindowCallBacks {

    @Override
    public void onTitleBarBackClicked(AbstractWindow window) {
        if (getCurrentWindow() == window) {
            mWindowMgr.popWindow(true);
        }
    }

}
