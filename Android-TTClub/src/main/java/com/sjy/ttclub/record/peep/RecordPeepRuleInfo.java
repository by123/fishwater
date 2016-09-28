package com.sjy.ttclub.record.peep;

import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.record.RecordPeepRuleBean;
import com.sjy.ttclub.record.RecordConst;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by linhz on 2016/1/7.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepRuleInfo {

    private int mPeepLevel;
    private ArrayList<String> mRuleSex = new ArrayList<String>();
    private ArrayList<String> mRuleMarriage = new ArrayList<String>();
    private ArrayList<String> mRuleTime = new ArrayList<String>();

    private boolean mIsShowMe = false;

    private String mSpecialTimeName;

    public RecordPeepRuleInfo(RecordPeepRuleBean ruleBean) {
        RecordPeepRuleBean.Data data = ruleBean.data;
        mPeepLevel = StringUtils.parseInt(data.userPeepLevel);
        if (StringUtils.isNotEmpty(data.userPeepSex)) {
            String[] result = StringUtils.split(data.userPeepSex, ",");
            mRuleSex.addAll(Arrays.asList(result));
        }

        if (StringUtils.isNotEmpty(data.userPeepMarriage)) {
            String[] result = StringUtils.split(data.userPeepMarriage, ",");
            mRuleMarriage.addAll(Arrays.asList(result));
        }

        if (StringUtils.isNotEmpty(data.userPeepTime)) {
            String[] result = StringUtils.split(data.userPeepTime, ",");
            mRuleTime.addAll(Arrays.asList(result));
        }
        mIsShowMe = StringUtils.parseInt(data.userPeepShowMe) == RecordConst.PEEP_ME_ENABLE;

        mSpecialTimeName = data.holidayName;
    }

    public boolean hasSexPermission(int sex) {
        return mRuleSex.contains(String.valueOf(sex));
    }

    public int getDefaultSexPermission() {
        int sex = StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getSex());
        if (hasSexPermission(sex)) {
            return sex;
        }
        if (hasSexPermission(RecordConst.PEEP_SEX_MAN)) {
            return RecordConst.PEEP_SEX_MAN;
        } else if (hasSexPermission(RecordConst.PEEP_SEX_WOMAN)) {
            return RecordConst.PEEP_SEX_WOMAN;
        } else {
            return RecordConst.PEEP_SEX_ALL;
        }
    }


    public boolean hasMarriagePermission(int marriage) {
        return mRuleMarriage.contains(String.valueOf(marriage));
    }

    public int getDefaultMarriagePermission() {
        int marriage = StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getMarriage());
        if (hasMarriagePermission(marriage)) {
            return marriage;
        }
        if (hasMarriagePermission(RecordConst.PEEP_MARRIAGE_SINGLE)) {
            return RecordConst.PEEP_MARRIAGE_SINGLE;
        } else if (hasMarriagePermission(RecordConst.PEEP_MARRIAGE_INLOVE)) {
            return RecordConst.PEEP_MARRIAGE_INLOVE;
        } else if (hasMarriagePermission(RecordConst.PEEP_MARRIAGE_MARRIAGED)) {
            return RecordConst.PEEP_MARRIAGE_MARRIAGED;
        } else {
            return RecordConst.PEEP_MARRIAGE_ALL;
        }
    }

    public boolean hasTimePermission(int time) {
        return mRuleTime.contains(String.valueOf(time));
    }

    public boolean hasPeepPermission() {
        if (mPeepLevel > 0) {
            return true;
        }
        return false;
    }

    public int getPeepLevel() {
        return mPeepLevel;
    }

    public boolean hasPermissionShowMe() {
        return mIsShowMe;
    }

    public String getSpecialTimeName() {
        return mSpecialTimeName;
    }

    public ArrayList<String> getMarriagePermissionList() {
        return mRuleMarriage;
    }

    public ArrayList<String> getTimePermissionList() {
        return mRuleTime;
    }


}
