package com.sjy.ttclub.bean.account;

/**
 * Created by gangqing on 2015/12/30.
 * Email:denggangqing@ta2she.com
 */
public class MessageUnreadCountBean {
    public Data data;
    public String msg;
    public int status;

    public class Data {
        public int auditedPostCount;    //已审核的帖子数量
        public int msgAllCount; //消息 总数量
        public int officialMsgCount;    //'官方消息'数量
        public int praiseMeCount;   //'点赞同问'数量
        public int privletterCount; //接收到的私信总数量
        public int replyMeCount;    //“回复我的”数量

        public int getAllCount() {
            return auditedPostCount + officialMsgCount + privletterCount + replyMeCount;
        }
    }
}
