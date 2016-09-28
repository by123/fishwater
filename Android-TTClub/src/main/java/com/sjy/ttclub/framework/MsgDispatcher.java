/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.framework;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import com.lsym.ttclub.BuildConfig;

import java.util.ArrayList;

public class MsgDispatcher implements Callback {

    public static final String TAG = "MsgDispatcher";

    private static MsgDispatcher sMsgDispatcher;

    private ArrayList<IMessageHandler> mHandlers = new ArrayList<IMessageHandler>();

    private Handler mMsgDispatcherHandler = null;

    private MsgDispatcher() {
        mMsgDispatcherHandler = new Handler(Looper.getMainLooper(), this);
    }


    /* package */
    static void init(Context context) {
        sMsgDispatcher = new MsgDispatcher();
    }

    public void destroy() {
        mHandlers.clear();
    }

    public static MsgDispatcher getInstance() {
        if (sMsgDispatcher == null) {
            throw new IllegalStateException("MsgDispatcher should init first!!");
        }
        return sMsgDispatcher;
    }

    public void register(IMessageHandler handler) {
        if (handler == null) {
            return;
        }
        synchronized (mHandlers) {
            for (IMessageHandler msgHandler : mHandlers) {
                if (msgHandler != null && msgHandler == handler) {
                    return;
                }
            }
            mHandlers.add(handler);
        }
    }

    public void unregister(IMessageHandler handler) {
        synchronized (mHandlers) {
            for (IMessageHandler msgHandler : mHandlers) {
                if (msgHandler != null && msgHandler == handler) {
                    mHandlers.remove(handler);
                    return;
                }
            }
        }
    }

    public boolean sendMessage(int what, long delay) {
        Message message = mMsgDispatcherHandler.obtainMessage();
        message.what = what;
        return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
    }

    public boolean sendMessage(int what) {
        return sendMessage(what, 0);
    }

    public boolean sendMessage(int what, int arg1, int arg2, long delay) {
        Message message = mMsgDispatcherHandler.obtainMessage(what, arg1, arg2,
                null);
        return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
    }

    public boolean sendMessage(int what, int arg1, int arg2) {
        return sendMessage(what, arg1, arg2, 0);
    }

    public boolean sendMessage(Message message, long delay) {
        return mMsgDispatcherHandler.sendMessageDelayed(message, delay);
    }

    public boolean sendMessage(Message message) {
        return sendMessage(message, 0);
    }

    public boolean sendMessage(int what, int arg1, int arg2, Object obj,
                               long delay) {
        return mMsgDispatcherHandler.sendMessageDelayed(
                mMsgDispatcherHandler.obtainMessage(what, arg1, arg2, obj),
                delay);
    }

    public boolean sendMessage(int what, int arg1, int arg2, Object obj) {
        return sendMessage(what, arg1, arg2, obj, 0);
    }

    public void removeMessages(int what) {
        mMsgDispatcherHandler.removeMessages(what);
    }

    private IMessageHandler findHandler(Message msg) {
        if (msg == null) {
            return null;
        }
        IMessageHandler handler = null;
        synchronized (mHandlers) {
            for (IMessageHandler msgHandler : mHandlers) {
                if (msgHandler == null) {
                    continue;
                }
                ArrayList<Integer> messages = msgHandler.messages();
                if (null != messages && messages.contains(msg.what)) {
                    if (!BuildConfig.DEBUG) {
                        handler = msgHandler;
                        break;
                    } else {
                        if (handler != null) {
                            //throw exception for debug version
                            String errorMsg = "msg: " + msg.what
                                    + " duplicated in " + handler + " and "
                                    + msgHandler;
                            throw new IllegalArgumentException(errorMsg);
                        }
                        handler = msgHandler;
                    }
                }
            }

        }

        return handler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        IMessageHandler c = findHandler(msg);
        if (c != null) {
            c.handleMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * Note : donnot thread safety
     *
     * @param msg
     * @return Object
     */
    public Object sendMessageSync(Message msg) {

        IMessageHandler c = findHandler(msg);
        if (c != null) {
            return c.handleMessageSync(msg);
        }

        return null;
    }

    /**
     * Note : donnot thread safety
     *
     * @param what
     * @return Object
     */
    public Object sendMessageSync(int what) {
        Message message = mMsgDispatcherHandler.obtainMessage();
        message.what = what;
        return sendMessageSync(message);
    }

    public Object sendMessageSync(int what, Object obj) {
        return sendMessageSync(what, 0, 0, obj);
    }

    public Object sendMessageSync(int what, int arg1, int arg2) {
        return sendMessageSync(what, arg1, arg2, null);
    }

    public Object sendMessageSync(int what, int arg1, int arg2, Object obj) {
        Message message = mMsgDispatcherHandler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
        return sendMessageSync(message);
    }

    public interface IMessageHandler {
        public ArrayList<Integer> messages();

        public void handleMessage(Message msg);

        public Object handleMessageSync(Message msg);
    }

}
