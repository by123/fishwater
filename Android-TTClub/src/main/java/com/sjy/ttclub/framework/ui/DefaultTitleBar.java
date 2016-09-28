/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.ui;

import android.content.Context;

public class DefaultTitleBar extends TitleBar {

    public DefaultTitleBar(Context context, ITitleBarListener titleBarListener) {
        super(context, titleBarListener);
    }

    public ActionBar createActionBar() {
        ActionBar actionBar = new DefaultActionBar(getContext(), this);
        return actionBar;
    }

    @Override
    public void acceptCommand(int commandId, Object object) {

    }

}
