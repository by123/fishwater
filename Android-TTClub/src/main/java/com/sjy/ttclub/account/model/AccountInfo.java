package com.sjy.ttclub.account.model;

import com.sjy.ttclub.bean.account.AccountBean;

/**
 * Created by gangqing on 2015/12/15.
 * Email:denggangqing@ta2she.com
 */
public class AccountInfo {
    private String mAge;
    private String mCurrentProgress;
    private String mFollowersCount;
    private String mFollowingCount;
    private String mImageUrl;
    private String mIsMaxLevel;
    private String mLevel;
    private String mMarriage;
    private String mNickname;
    private String mProgressBar;
    private String mSex;
    private String mSexIfUpdate;
    private String mSexyLife;
    private String mToken;
    private String mUserId;
    private String mUserRoleId;
    private String mIfBindPhone;
    private String mRanking;
    private String mRecordCount;
    private String mSexCount;
    private String mSexPoint;
    private String mUserExpers;
    private String mIfHasUploadSex;

    /*package*/AccountInfo(AccountBean.Data data) {
        mAge = data.age;
        mCurrentProgress = data.currentProgress;
        mFollowersCount = data.followersCount;
        mFollowingCount = data.followingCount;
        mImageUrl = data.imageUrl;
        mIsMaxLevel = data.isMaxLevel;
        mLevel = data.level;
        mMarriage = data.marriage;
        mNickname = data.nickname;
        mProgressBar = data.progressBar;
        mSex = data.sex;
        mSexIfUpdate = data.sexIfUpdate;
        mSexyLife = data.sexyLife;
        mToken = data.token;
        mUserId = data.userId;
        mUserRoleId = data.userRoleId;
        mIfBindPhone = data.ifBindPhone;
        mRanking = data.ranking;
        mRecordCount = data.recordCount;
        mSexCount = data.sexCount;
        mSexPoint = data.sexPoint;
        mUserExpers = data.userExpers;
        mIfHasUploadSex = data.ifHasUploadSex;
    }

    public String getAge() {
        return mAge;
    }

    public String getCurrentProgress() {
        return mCurrentProgress;
    }

    public String getFollowersCount() {
        return mFollowersCount;
    }

    public String getFollowingCount() {
        return mFollowingCount;
    }

    /*package*/String getImageUrl() {
        return mImageUrl;
    }

    public String getIsMaxLevel() {
        return mIsMaxLevel;
    }

    public String getLevel() {
        return mLevel;
    }

    public String getMarriage() {
        return mMarriage;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        this.mNickname = nickname;
    }

    public String getProgressBar() {
        return mProgressBar;
    }

    public String getSex() {
        return mSex;
    }

    public String getSexIfUpdate() {
        return mSexIfUpdate;
    }

    public String getSexyLife() {
        return mSexyLife;
    }

    public String getToken() {
        return mToken;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserRoleId() {
        return mUserRoleId;
    }

    public String getIfBindPhone() {
        return mIfBindPhone;
    }

    public String getRanking() {
        return mRanking;
    }

    public String getRecordCount() {
        return mRecordCount;
    }

    public String getSexCount() {
        return mSexCount;
    }

    public String getSexPoint() {
        return mSexPoint;
    }

    public String getUserExpers() {
        return mUserExpers;
    }

    public String getIfHasUploadSex() {
        return mIfHasUploadSex;
    }
}
