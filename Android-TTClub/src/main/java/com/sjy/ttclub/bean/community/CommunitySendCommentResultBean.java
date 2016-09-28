package com.sjy.ttclub.bean.community;

/**
 * Created by Administrator on 2015/12/1.
 */
public class CommunitySendCommentResultBean {
/*  "status": 0,
    "msg": "success",
    "data": {
        "dayReplyCount": "1",
        "isLevelUp": "0",
        "currentLevel": "16",
        "obtainedExp": 1,
        "maxRewardTimes": 10
    }*/

    private int status;
    private String msg;
    private ResultBean data;

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

    public ResultBean getData() {
        return data;
    }

    public void setData(ResultBean data) {
        this.data = data;
    }

    public class ResultBean{
        private int dayReplyCount;
        private int isLevelUp;
        private int currentLevel;
        private int obtainedExp;
        private int maxRewardTimes;

        public int getDayReplyCount() {
            return dayReplyCount;
        }

        public void setDayReplyCount(int dayReplyCount) {
            this.dayReplyCount = dayReplyCount;
        }

        public int getIsLevelUp() {
            return isLevelUp;
        }

        public void setIsLevelUp(int isLevelUp) {
            this.isLevelUp = isLevelUp;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }

        public void setCurrentLevel(int currentLevel) {
            this.currentLevel = currentLevel;
        }

        public int getObtainedExp() {
            return obtainedExp;
        }

        public void setObtainedExp(int obtainedExp) {
            this.obtainedExp = obtainedExp;
        }

        public int getMaxRewardTimes() {
            return maxRewardTimes;
        }

        public void setMaxRewardTimes(int maxRewardTimes) {
            this.maxRewardTimes = maxRewardTimes;
        }
    }
}
