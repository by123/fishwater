package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.MessageUnreadCountBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;

/**
 * Created by gangqing on 2015/12/30.
 * Email:denggangqing@ta2she.com
 */
public class MessageUnreadCountRequestHelper {
    private static MessageUnreadCountBean.Data mBean;

    public void getMessageUnreadCount() {
        MessageUnreadCountRequest request = new MessageUnreadCountRequest();
        request.messageUnreadCountRequest(new MessageUnreadCountRequestCallBack());
    }

    private class MessageUnreadCountRequestCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof MessageUnreadCountBean) {
                MessageUnreadCountBean bean = (MessageUnreadCountBean) obj;
                mBean = bean.data;
                sendMessage();
            }
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    public static MessageUnreadCountBean.Data getBean() {
        return mBean;
    }

    private void sendMessage() {
        Notification notification = Notification.obtain(NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }
}
