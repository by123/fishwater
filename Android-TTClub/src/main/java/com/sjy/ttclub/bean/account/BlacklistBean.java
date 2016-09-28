package com.sjy.ttclub.bean.account;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhxu on 2015/11/17.
 * Email:357599859@qq.com
 */
public class BlacklistBean {

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

    public BlacklistObj getData() {
        return data;
    }

    public void setData(BlacklistObj data) {
        this.data = data;
    }

    private BlacklistObj data;

    public class BlacklistObj {
        private int endId;
        private List<Blacklists> blacklists;

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public List<Blacklists> getBlacklists() {
            return blacklists;
        }

        public void setBlacklists(List<Blacklists> blacklists) {
            this.blacklists = blacklists;
        }

        public class Blacklists implements Serializable {
            private String blacklistId;
            private String userId;
            private String nickname;

            public String getBlacklistId() {
                return blacklistId;
            }

            public void setBlacklistId(String blacklistId) {
                this.blacklistId = blacklistId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getHeadimageUrl() {
                return headimageUrl;
            }

            public void setHeadimageUrl(String headimageUrl) {
                this.headimageUrl = headimageUrl;
            }

            public String getUserLevel() {
                return userLevel;
            }

            public void setUserLevel(String userLevel) {
                this.userLevel = userLevel;
            }

            public String getUserSex() {
                return userSex;
            }

            public void setUserSex(String userSex) {
                this.userSex = userSex;
            }

            private String headimageUrl;
            private String userLevel;
            private String userSex;
        }
    }
}
