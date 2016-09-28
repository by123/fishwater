package com.sjy.ttclub.bean.record;

import java.io.Serializable;

/**
 * Created by zhxu on 2015/12/8.
 * Email:357599859@qq.com
 */
public class RecordDataDay implements Serializable {
    private String createtime;
    private String category;
    private String feel;


    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrgasm() {
        return orgasm;
    }

    public void setOrgasm(String orgasm) {
        this.orgasm = orgasm;
    }

    private String time;
    private String orgasm;

    private String papaTime;
    private String papaTimeRange;

    public void setPapaTime(String papaTime) {
        this.papaTime = papaTime;
    }

    public String getPapaTime() {
        return papaTime;
    }

    public void setPapaTimeRange(String papaTimeRange) {
        this.papaTimeRange = papaTimeRange;
    }

    public String getPapaTimeRange() {
        return papaTimeRange;
    }
}
