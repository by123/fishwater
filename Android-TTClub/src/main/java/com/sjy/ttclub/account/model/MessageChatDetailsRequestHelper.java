package com.sjy.ttclub.account.model;

import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.account.BlacklistBean;
import com.sjy.ttclub.bean.account.DialogIDBean;
import com.sjy.ttclub.bean.account.MessageDialogDetails;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;

import java.util.List;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class MessageChatDetailsRequestHelper {
    private static final int PAGE_SIZE = 20;
    private int mEndId;
    private int mStartId;
    private int mPage = 1;
    private int mGetType;
    private Context mContext;

    private MessageChatDetailsRequest messageChatDetailsRequest;
    private ChatCallBack mCallBack;
    private DialogIdCallBackSuccess mDialogIdCallBack;
    private BlacklistCallBackSuccess mBlacklist;

    public MessageChatDetailsRequestHelper(Context context) {
        mContext = context;
        messageChatDetailsRequest = new MessageChatDetailsRequest();
    }

    /**
     * 发送
     */
    public void send(String dialogId, String toUserId, String content, String linkType, String linkContent, String linkUrl) {
        messageChatDetailsRequest.sendChatRequest(dialogId, toUserId, content, linkType, linkContent, linkUrl, new SendIHttpCallBack());
    }

    /**
     * 私信
     *
     * @param dialogsId
     * @param type
     */
    public void personalChatDetailsRequest(String dialogsId, int type, int getType) {
        mGetType = getType;
        messageChatDetailsRequest.personalChatRequest(dialogsId, mStartId, mEndId, type, new ChatIHttpCallBack());
    }

    /**
     * 官方消息
     */
    public void officialNewsDetailsRequest() {
        messageChatDetailsRequest.officialNewsChatRequest(mEndId, mPage, new ChatIHttpCallBack());
    }

    /**
     * 获取会话ID
     *
     * @param toUserId 私信对象ID（与她他小秘私信时传0，由后端随机分配返回）
     * @param type     私信对象类型（1:普通会员 2:她他小秘）
     */
    public void dialogId(String toUserId, String type) {
        messageChatDetailsRequest.dialogIdRequest(toUserId, type, new DialogIdCallBack());
    }

    /**
     * 黑名单列表
     */
    public void blacklist() {
        messageChatDetailsRequest.blacklistRequest(mEndId, mPage, PAGE_SIZE, new BlacklistCallBack());
    }

    /**
     * 拉黑、取消拉黑
     *
     * @param toUserId
     */
    public void shieldingRequest(String toUserId, String flag, IHttpCallBack callBack) {
        if (callBack == null) {
            messageChatDetailsRequest.shieldingRequest(toUserId, flag, new DhieldingCallBack());
        } else {
            messageChatDetailsRequest.shieldingRequest(toUserId, flag, callBack);
        }
    }

    /**
     * 获取聊天记录
     */
    private class ChatIHttpCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof MessageDialogDetails) {
                MessageDialogDetails details = (MessageDialogDetails) obj;
                List<MessageDialogDetails.LetterObj.Letters> letters = details.getData().getLetters();
                if (letters == null) {
                    letters = details.getData().getMsgArray();
                }
                mEndId = details.getData().getEndId();
                mStartId = details.getData().getStartId();
                if (letters.size() < PAGE_SIZE) {
                    mCallBack.onSuccess(letters, false, mGetType);
                } else {
                    mPage++;
                    mCallBack.onSuccess(letters, true, mGetType);
                }
            }
        }

        @Override
        public void onFail(String errorStr, int code) {
            //TODO
        }
    }

    /**
     * 发送
     */
    private class SendIHttpCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            mCallBack.onSuccessForSend();
        }

        @Override
        public void onFail(String errorStr, int code) {
            if (HttpCode.HAVE_BLACK == code) {
                ToastHelper.showToast(mContext, R.string.account_message_blacked, Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 获取会话ID
     */
    private class DialogIdCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof DialogIDBean) {
                DialogIDBean bean = (DialogIDBean) obj;
                mDialogIdCallBack.onSuccessForDialogId(bean);
            }
        }

        @Override
        public void onFail(String errorStr, int code) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_network_error_retry));
        }
    }

    /**
     * 黑名单列表
     */
    private class BlacklistCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof BlacklistBean) {
                BlacklistBean bean = (BlacklistBean) obj;
                List<BlacklistBean.BlacklistObj.Blacklists> blacklists = bean.getData().getBlacklists();
                mEndId = bean.getData().getEndId();
                if (blacklists.size() < PAGE_SIZE) {
                    mBlacklist.onSuccessForBlacklist(blacklists, false);
                } else {
                    mBlacklist.onSuccessForBlacklist(blacklists, true);
                }
            }
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    /**
     * 拉黑
     */
    private class DhieldingCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            mCallBack.onSuccessForBlack();
        }

        @Override
        public void onFail(String errorStr, int code) {
            if (HttpCode.HAVE_BLACK == code) {
                ToastHelper.showToast(mContext, R.string.account_message_blacked, Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 私信聊天的回调接口
     */
    public interface ChatCallBack {
        void onSuccess(List<MessageDialogDetails.LetterObj.Letters> letters, boolean isHaveMore, int getType);

        void onSuccessForSend();

        void onSuccessForBlack();
    }

    /**
     * 私信黑名单的回调接口
     */
    public interface BlacklistCallBackSuccess {
        void onSuccessForBlacklist(List<BlacklistBean.BlacklistObj.Blacklists> blacklist, boolean isHaveMore);
    }

    /**
     * 获取会话ID的回调接口
     */
    public interface DialogIdCallBackSuccess {
        void onSuccessForDialogId(DialogIDBean bean);
    }

    public void setCallBack(ChatCallBack callBack) {
        mCallBack = callBack;
    }

    public void setDialogIdCallBack(DialogIdCallBackSuccess dialogIdCallBack) {
        mDialogIdCallBack = dialogIdCallBack;
    }

    public void setBlacklistCallBack(BlacklistCallBackSuccess blacklistCallBack) {
        mBlacklist = blacklistCallBack;
    }
}
