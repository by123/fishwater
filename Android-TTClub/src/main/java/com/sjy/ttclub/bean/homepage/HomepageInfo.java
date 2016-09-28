package com.sjy.ttclub.bean.homepage;


import com.sjy.ttclub.bean.Banner;

import java.util.List;

/**
 * Created by Administrator on 2015/9/28 0028.
 */
public class HomepageInfo {
    /**
     * 状态
     */
    private int status;
    /**
     * 消息
     */
    private String msg;

    private Data data;

    public class Data {
        private sexIndex sexIndex;
        private List<Banner> banners;
        private List<SexTalents> sexTalents;
        private int circleId;

        public class sexIndex {
            /**
             * 情趣积分
             */
            private String sexPoint;
            /**
             * 爱爱次数
             */
            private String sexCount;
            /**
             * 打卡记录
             */
            private String recordCount;
            /**
             * 用户积分
             */
            private String userExpers;
            /**
             * 排名
             */
            private String ranking;

            public String getSexPoint() {
                return sexPoint;
            }

            public String getSexCount() {
                return sexCount;
            }

            public String getRecordCount() {
                return recordCount;
            }

            public String getUserExpers() {
                return userExpers;
            }

            public String getRanking() {
                return ranking;
            }

        }

        public class SexTalents {
            /**
             * 用户ID
             */
            private String uid;
            /**
             * 广用户昵称
             */
            private String nickName;
            /**
             * 用户icon URL
             */
            private String iconUrl;

            public String getUid() {
                return uid;
            }

            public String getNickName() {
                return nickName;
            }

            public String getIconUrl() {
                return iconUrl;
            }

        }

        public int getCircleId() {
            return circleId;
        }

        public sexIndex getSexIndex() {
            return sexIndex;
        }

        public List<Banner> getBanners() {
            return banners;
        }

        public List<SexTalents> getSexTalents() {
            return sexTalents;
        }

    }

    public int getStatus() {
        return status;
    }


    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

}
