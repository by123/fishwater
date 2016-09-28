/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework.adapter;

public class MenuItemIdDef {
    private static byte sBaseId = 0;

    private static byte generateId() {
        return sBaseId++;
    }

    // titlebar
    public static final int TITLEBAR_BACK = generateId();
    public static final int TITLEBAR_SHARE = generateId();
    public static final int TITLEBAR_EDIT = generateId();
    public static final int TITLEBAR_MENU = generateId();
    public static final int TITLEBAR_SETTINGS = generateId();
    public static final int TITLEBAR_PASSWORD_CHANGE_CONFIRM = generateId();
    public static final int TITLEBAR_LEVEL_EXPLAIN = generateId();

    public static final int TITLEBAR_COMMIT = generateId();

    public static final int TITLEBAR_SHOPPING_TEMP = generateId();
    public static final int TITLEBAR_SHOPPING_SECRETARY = generateId();
    public static final int TITLEBAR_SHOPPING_CART = generateId();
    public static final int TITLEBAR_ORDER_FINISH = generateId();
    public static final int TITLEBAR_ORDER_REVIEW_PUSH = generateId();
    public static final int TITLEBAR_SHOPPING_CLEAR = generateId();
}
