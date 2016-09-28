package com.sjy.ttclub.bean.community;

import java.io.Serializable;

/**
 * @author: 张武林
 * @类 说   明:
 * @创建时间：${date}
 */
public class ReportBean implements Serializable {
    //举报理由
    String reportReason;
    //举报理由对应的值
    String reportValue;

    public ReportBean(String reportValue, String reportReason) {
        this.reportValue = reportValue;
        this.reportReason = reportReason;
    }

    public String getReportValue() {
        return reportValue;
    }

    public void setReportValue(String reportValue) {
        this.reportValue = reportValue;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }
}
