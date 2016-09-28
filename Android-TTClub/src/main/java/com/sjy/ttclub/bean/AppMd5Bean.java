package com.sjy.ttclub.bean;

/**
 * Created by linhz on 2015/12/21.
 * Email: linhaizhong@ta2she.com
 */
public class AppMd5Bean {
    private int status;
    private String msg;

    private Data data;

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }


    public static class Data {
        private String recordMd5;
        private String reportMd5;
        private String articleMd5;
        private String payParamMd5;
        private int privateLetterLevelControl;
        private SplashMD5Info splashInfoMd5;
        private String productClassification;

        public String getProductClassification() {
            return productClassification;
        }

        public SplashMD5Info getSplashInfoMd5() {
            return splashInfoMd5;
        }

        public String getRecordMd5() {
            return recordMd5;
        }

        public String getReportMd5() {
            return reportMd5;
        }

        public String getArticleMd5() {
            return articleMd5;
        }

        public String getPayParamMd5() {
            return payParamMd5;
        }

        public int getPrivateLetterLevelControl() {
            return privateLetterLevelControl;
        }
    }

    public static class SplashMD5Info{
        private String other_value;

        public String getOther_value() {
            return other_value;
        }

        public void setOther_value(String other_value) {
            this.other_value = other_value;
        }
    }
}
