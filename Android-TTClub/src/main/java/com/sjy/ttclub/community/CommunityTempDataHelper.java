package com.sjy.ttclub.community;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityCircleBean;

/**
 * Created by zhangwulin on 2016/1/4.
 * email 1501448275@qq.com
 */
public class CommunityTempDataHelper {
    private Context mContext;
    private static CommunityTempDataHelper sInstance;
    private static boolean mIsReplyToSomeOne = false;
    private CommunityCircleBean mTempCircle;

    CommunityTempDataHelper(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        sInstance = new CommunityTempDataHelper(context);
    }

    public static CommunityTempDataHelper getInstance() {
        return sInstance;
    }

    public boolean getIsReplyToSomeOne() {
        return mIsReplyToSomeOne;
    }

    public void setIsReplyToSomeOne(boolean isReplyToSomeOne) {
        mIsReplyToSomeOne = isReplyToSomeOne;
    }

    public CommunityCircleBean getmTempCircle() {
        if (mTempCircle == null) {
            mTempCircle = new CommunityCircleBean();
        }
        return mTempCircle;
    }

    public void setmTempCircle(CommunityCircleBean tempCircle) {
        mTempCircle = tempCircle;
    }
}
