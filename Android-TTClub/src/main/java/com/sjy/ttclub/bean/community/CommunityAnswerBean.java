package com.sjy.ttclub.bean.community;

import java.io.Serializable;

/**
 * Created by zwl on 2015/11/10.
 * Email: 1501448275@qq.com
 */
public class CommunityAnswerBean implements Serializable {
    private String headimageUrl;

    public String getHeadimageUrl() {
        return headimageUrl;
    }

    public void setHeadimageUrl(String headimageUrl) {
        this.headimageUrl = headimageUrl;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    private String answerName;
    private String answer;
    private int userSex;

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }


}
