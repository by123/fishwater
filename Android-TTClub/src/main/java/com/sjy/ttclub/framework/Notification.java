/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;


public class Notification {

    public int id;
    public Object extObj;

    public Notification(int id, Object extObj) {
        this.id = id;
        this.extObj = extObj;
    }

    public Notification(int id) {
        this(id, null);
    }

    public static Notification obtain(int id, Object extObj) {
        return new Notification(id, extObj);
    }

    public static Notification obtain(int id) {
        return new Notification(id);
    }

    public static Notification obtain(Notification orig) {
        Notification notification = new Notification(orig.id, orig.extObj);
        return notification;
    }

}
