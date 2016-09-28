package com.sjy.ttclub.bean.account;

import java.util.List;

/**
 * Created by zhxu on 2015/11/16.
 * Email:357599859@qq.com
 */
public class MessageDialog {
    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DialogObj getData() {
        return data;
    }

    public void setData(DialogObj data) {
        this.data = data;
    }

    private DialogObj data;

    public class DialogObj {
        private int endId;
        private List<MessageDialogs> dialogs;   //普通用户私信列表
        private List<MessageDialogs> msgArray;  //官方消息

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<MessageDialogs> getDialogs() {
            return dialogs;
        }

        public void setDialogs(List<MessageDialogs> dialogs) {
            this.dialogs = dialogs;
        }

        public List<MessageDialogs> getMsgArray() {
            return msgArray;
        }

        public void setMsgArray(List<MessageDialogs> msgArray) {
            this.msgArray = msgArray;
        }
    }
}
