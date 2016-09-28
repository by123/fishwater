package com.sjy.ttclub.bean.account;

import java.util.List;

/**
 * Created by gangqing on 2015/12/23.
 * Email:denggangqing@ta2she.com
 */
public class RelationshipBean {
    public int status;
    public String msg;
    public Data data;

    public class Data {
        public String followersCount;
        public String followingCount;
        public List<Follow> followArray;

        public class Follow {
            public String ifFollow;
            public String imageUrl;
            public String level;
            public String nickname;
            public String sex;
            public String timestamp;
            public String userRoleId;
            public String userid;
        }
    }
}
