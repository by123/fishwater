package com.sjy.ttclub.umeng;

import android.content.Context;
import android.util.Log;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.push.IPushEventListener;
import com.sjy.ttclub.push.IPushManager;
import com.sjy.ttclub.util.ThreadManager;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by linhz on 2015/12/12.
 * Email: linhaizhong@ta2she.com
 */
public class UmengPushManager implements IPushManager {
    private Context mContext;
    private IPushEventListener mListener;


    public UmengPushManager(Context context) {
        mContext = context;
    }

    @Override
    public void initPush() {
        final PushAgent pushAgent = PushAgent.getInstance(mContext);
        if (BuildConfig.DEBUG) {
            pushAgent.setDebugMode(true);
        }
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                UTrack.getInstance(context).trackMsgClick(msg);
                ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onPushCustomMessageEvent(msg.extra);
                        }
                    }
                });
            }

        };
        pushAgent.setMessageHandler(messageHandler);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, final UMessage msg) {
                UTrack.getInstance(context).trackMsgClick(msg);
                ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onPushNotificationEvent(msg.extra);
                        }
                    }
                });
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);
        pushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(String s) {
                Log.i("UmengPush", "onRegistered -> " + s);
                if (mListener != null) {
                    mListener.onPushRegistered(s);
                }
            }
        });

        if (pushAgent.isRegistered()) {
            if (mListener != null) {
                mListener.onPushRegistered(pushAgent.getRegistrationId());
            }
        }

    }

    @Override
    public void setPushEventListener(IPushEventListener listener) {
        mListener = listener;
    }

    @Override
    public void addTag(final String tag) {
        final PushAgent pushAgent = PushAgent.getInstance(mContext);
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] tags = tag.split(",");
                    pushAgent.getTagManager().add(tags);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void removeTag(final String tag) {
        final PushAgent pushAgent = PushAgent.getInstance(mContext);
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] tags = tag.split(",");
                    pushAgent.getTagManager().delete(tags);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
