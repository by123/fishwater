package com.sjy.ttclub.bean.account;

/**
 * Created by gangqing on 2015/12/30.
 * Email:denggangqing@ta2she.com
 */
public class ExternalLinksHelperParam {
    private String id;
    private String type;
    private ReplyMeMsgArray.MsgArrayObj.MsgArrays msgObj;
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ReplyMeMsgArray.MsgArrayObj.MsgArrays getMsgObj() {
        return msgObj;
    }

    public void setMsgObj(ReplyMeMsgArray.MsgArrayObj.MsgArrays msgObj) {
        this.msgObj = msgObj;
    }
}
