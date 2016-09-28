package com.sjy.ttclub.bean.account;

import java.util.List;

/**
 * Created by linhz on 2015/12/29.
 * Email: linhaizhong@ta2she.com
 */
public class FeedbackBean {
    public Data data;
    public String msg;
    public int status;

    public class Data {
        public List<FeedbackInfo> messageArray;
    }

    public class FeedbackInfo {
        //公共
        public String content;
        public String createTime;
        //反馈记录
        public String contentId;
        public String dialogueCount;
        //反馈详情
        public String replyId;
        public String userId;
        public String uuid;
    }
}
