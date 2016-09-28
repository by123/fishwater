/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping;

import android.os.Message;

import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.model.ShoppingCategoryInfo;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class ShoppingController extends DefaultWindowController {

    private final static String TAG = "ShoppingController";

    private ShoppingMainTab mShoppingMainTab;

    public ShoppingController() {
        registerMessage(MsgDef.MSG_SHOW_SHOPPING_CATEGORY_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW);
        registerMessage(MsgDef.MSG_GET_SHOPPING_TAB);
    }

    private void showShoppingCategoryWindow(ShoppingCategoryInfo typeInfo) {
        ShoppingCategoryWindow categoryWindow = new ShoppingCategoryWindow(mContext, this, typeInfo);
        mWindowMgr.pushWindow(categoryWindow);
    }

    private void showShoppingTopicWindow(ShoppingTopicInfo topicInfo) {
        ShoppingTopicWindow topicWindow = new ShoppingTopicWindow(mContext, this, topicInfo);
        mWindowMgr.pushWindow(topicWindow);
    }

    @Override
    public void handleMessage(Message msg) {
        if (MsgDef.MSG_SHOW_SHOPPING_CATEGORY_WINDOW == msg.what) {
            showShoppingCategoryWindow((ShoppingCategoryInfo) msg.obj);
        } else if (MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW == msg.what) {
            showShoppingTopicWindow((ShoppingTopicInfo) msg.obj);
        }
    }

    private void ensureAccountTab() {
        if (mShoppingMainTab == null) {
            mShoppingMainTab = new ShoppingMainTab(mContext, this);
        }
    }

    @Override
    public Object handleMessageSync(Message msg) {
        if (msg.what == MsgDef.MSG_GET_SHOPPING_TAB) {
            ensureAccountTab();
            return mShoppingMainTab;
        }
        return super.handleMessageSync(msg);
    }
}
