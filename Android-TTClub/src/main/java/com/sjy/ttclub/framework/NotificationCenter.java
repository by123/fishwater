/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sjy.ttclub.util.ThreadManager;

public class NotificationCenter {
    private final static String TAG = NotificationCenter.class.getName();

    private final static int PRIVATE_MSG_REGISTER = 2;
    private final static int PRIVATE_MSG_UNREGISTER = 3;
    private final static int PRIVATE_MSG_NOTIFY_ON_MAINTHREAD = 4;

    private static NotificationCenter sNotificationCenter;

    private HashMap<Integer, ArrayList<WeakReference<INotify>>> mNotifyMap = new HashMap<Integer, ArrayList<WeakReference<INotify>>>();
    private Handler mHandler = null;

    private NotificationCenter(Context context) {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == PRIVATE_MSG_REGISTER) {
                    registerInner((INotify) msg.obj, (byte) msg.arg1);
                } else if (msg.what == PRIVATE_MSG_UNREGISTER) {
                    unregisterInner((INotify) msg.obj, (byte) msg.arg1);
                } else if (msg.what == PRIVATE_MSG_NOTIFY_ON_MAINTHREAD) {
                    notifyInner((Notification) msg.obj);
                }
            }
        };
    }

    /* package */static void init(Context context) {
        sNotificationCenter = new NotificationCenter(context);
    }

    public void destroy() {
        mNotifyMap.clear();
    }

    public static NotificationCenter getInstance() {
        if (sNotificationCenter == null) {
            throw new IllegalStateException("should init first!!");
        }
        return sNotificationCenter;
    }

    public void register(INotify notify, int notificationID) {
        if (ThreadManager.isMainThread()) {
            registerInner(notify, notificationID);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(PRIVATE_MSG_REGISTER,
                    notificationID, 0, notify));
        }
    }

    public void unregister(INotify notify, int notificationID) {
        if (ThreadManager.isMainThread()) {
            unregisterInner(notify, notificationID);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(PRIVATE_MSG_UNREGISTER,
                    notificationID, 0, notify));
        }
    }

    public void notify(Notification notification) {
        if (ThreadManager.isMainThread()) {
            notifyInner(notification);
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(
                    PRIVATE_MSG_NOTIFY_ON_MAINTHREAD, notification));
        }
    }

    public void notify(Notification notification, long delay) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                PRIVATE_MSG_NOTIFY_ON_MAINTHREAD, notification), delay);
    }

    /**
     * must call in the same thread! in this case, work in ui thread
     * 
     * @param notify
     * @param notificationID
     */
    private void registerInner(INotify notify, int notificationID) {
        ArrayList<WeakReference<INotify>> notifyList = mNotifyMap
                .get(notificationID);
        if (notifyList == null) {
            notifyList = new ArrayList<WeakReference<INotify>>();
            mNotifyMap.put(notificationID, notifyList);
        }

        boolean hasRegistered = false;
        for (WeakReference<INotify> weakNotify : notifyList) {
            INotify temp = weakNotify.get();
            if (temp != null && notify == temp) {
                hasRegistered = true;
                break;
            }
        }

        if (!hasRegistered) {
            notifyList.add(new WeakReference<INotify>(notify));
        }
    }

    private void unregisterInner(INotify notify, int notificationID) {
        ArrayList<WeakReference<INotify>> notifyList = mNotifyMap
                .get(notificationID);
        if (notifyList == null) {
            return;
        }
        for (WeakReference<INotify> weakNotify : notifyList) {
            INotify temp = weakNotify.get();
            if (temp != null && temp == notify) {
                notifyList.remove(weakNotify);
                break;
            }
        }

    }

    private void notifyInner(Notification notification) {
        if (notification == null) {
            return;
        }
        ArrayList<WeakReference<INotify>> notifyList = mNotifyMap
                .get(notification.id);
        if (notifyList == null) {
            return;
        }
        ArrayList<WeakReference<INotify>> tmpList = (ArrayList<WeakReference<INotify>>) notifyList
                .clone();
        INotify notify;
        for (WeakReference<INotify> weakObject : tmpList) {
            notify = weakObject.get();
            if (notify != null) {
                notify.notify(notification);
            }
        }
    }

}
