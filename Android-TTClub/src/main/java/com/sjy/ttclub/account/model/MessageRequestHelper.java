package com.sjy.ttclub.account.model;

import android.content.Context;
import android.widget.LinearLayout;

import com.sjy.ttclub.bean.account.MessageDialog;
import com.sjy.ttclub.bean.account.MessageDialogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/28.
 * Email:denggangqing@ta2she.com
 */
public class MessageRequestHelper {
    private Context mContext;
    private static final int mPageSize = 20;
    private int mEndId;
    private CallBackOnSuccess mCallBack;
    private List<MessageDialogs> mMessageList = new ArrayList<MessageDialogs>();
    private MessageRequest mMessageRequest;
    private int mPage;
    private boolean mHaveLoadMore;

    public MessageRequestHelper(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBackOnSuccess callBack) {
        mCallBack = callBack;
    }

    /**
     * 获取会话列表
     *
     * @param page    分页获取，分码
     * @param isFirst 是否是第一次获取
     */
    public void messageListRequest(int page, boolean isFirst) {
        mPage = page;
        if (isFirst) {
            mMessageRequest = new MessageRequest();
            mMessageRequest.officialNewsRequest(new OfficialNewsRequestCallBack());
        } else {
            mMessageRequest.personalLetterRequest(mEndId, mPage, mPageSize, new MessageRequestCallBack());
        }
    }

    /**
     * 删除会话
     *
     * @param letter
     */
    public void deletePersonalLetter(List<MessageDialogs> letter) {
        mMessageRequest.deletePersonalLetterRequest(letter, new DeleteClassBack());
    }

    /**
     * 官方消息的callBack
     */
    private class OfficialNewsRequestCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof MessageDialog) {
                MessageDialog messageDialog = (MessageDialog) obj;
                if (messageDialog.getData().getMsgArray() != null && messageDialog.getData().getMsgArray().size() > 0) {
                    mMessageList.add(0, messageDialog.getData().getMsgArray().get(0));
                }
                mMessageRequest.personalLetterRequest(mEndId, mPage, mPageSize, new MessageRequestCallBack());
            }
        }

        @Override
        public void onFail(String errorStr, int code) {
            //TODO
        }
    }

    /**
     * 私信的callBack
     */
    private class MessageRequestCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof MessageDialog) {
                MessageDialog messageDialog = (MessageDialog) obj;
                List<MessageDialogs> dialogs = messageDialog.getData().getDialogs();
                mMessageList.addAll(dialogs);
                mEndId = messageDialog.getData().getEndId();
                int count = dialogs.size();
                if (count < mPageSize) {
                    mHaveLoadMore = false;
                } else {
                    mHaveLoadMore = true;
                }
                mCallBack.onSuccess(mMessageList, mHaveLoadMore);
            }
        }

        @Override
        public void onFail(String errorStr, int code) {
            //TODO
        }
    }

    /**
     * 删除会话的callBack
     */
    private class DeleteClassBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            mCallBack.onSuccessForDelete();
        }

        @Override
        public void onFail(String errorStr, int code) {
            //TODO
        }
    }

    /**
     * 回调接口
     */
    public interface CallBackOnSuccess {
        void onSuccess(List<MessageDialogs> messageLest, boolean haveLoadMore);

        void onSuccessForDelete();
    }
}
