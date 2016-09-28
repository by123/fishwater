package com.sjy.ttclub.record.peep;

import com.sjy.ttclub.record.RecordConst;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepSelectInfo {
    public int sex = RecordConst.PEEP_SEX_MAN;
    public int marriage = RecordConst.PEEP_MARRIAGE_SINGLE;
    public int time = RecordConst.PEEP_TIME_YESTODAY;
    public int showMe = RecordConst.PEEP_ME_DISABLE;

    public RecordPeepSelectInfo(RecordPeepRuleInfo ruleInfo) {
        showMe = RecordConst.PEEP_ME_DISABLE;
        time = RecordConst.PEEP_TIME_YESTODAY;
        sex = ruleInfo.getDefaultSexPermission();
        marriage = ruleInfo.getDefaultMarriagePermission();
    }
}
