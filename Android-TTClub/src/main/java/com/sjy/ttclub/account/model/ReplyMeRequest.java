package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.ReplyMeMsgArray;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpManager;

import java.util.List;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class ReplyMeRequest {
    private ReplyMeCallBack mReplyMeCallBack;
    private int mEndId;
    private int mPage;
    private static final int PAGE_SIZE = 20;

    /**
     * 回复我的
     */
    public void msgReplyMeRequest() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "msgReplyMe");
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.addParams("page", String.valueOf(mPage));
        httpManager.addParams("pageSize", String.valueOf(PAGE_SIZE));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, ReplyMeMsgArray.class, new MsgReplyMeCallBack());
    }

    /**
     * 赞、同问
     */
    public void msgPraiseMeRequest() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "msgPraiseMe");
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.addParams("page", String.valueOf(mPage));
        httpManager.addParams("pageSize", String.valueOf(PAGE_SIZE));
        httpManager.request(HttpUrls.USER_URL, HttpMethod.POST, ReplyMeMsgArray.class, new MsgReplyMeCallBack());
    }

    private class MsgReplyMeCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            System.out.println("result:+" + result);
            ReplyMeMsgArray array = (ReplyMeMsgArray) obj;
            List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> arrayList = array.getData().getMsgArray();
            mEndId = array.getData().getEndId();
            mPage++;
            if (arrayList.size() < PAGE_SIZE) {
                mReplyMeCallBack.msgReplyMeRequestOnSuccess(arrayList, false);
            } else {
                mReplyMeCallBack.msgReplyMeRequestOnSuccess(arrayList, true);
            }
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    public interface ReplyMeCallBack {
        void msgReplyMeRequestOnSuccess(List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> arrayList, boolean isHaveMore);
    }

    public void setReplyMeCallBack(ReplyMeCallBack callBack) {
        this.mReplyMeCallBack = callBack;
    }
}
