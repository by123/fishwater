package com.sjy.ttclub.system;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class AppInfo {
    public static final int versionCodeForOldAPI = 4;
    public String versionName;
    public int versionCode;
    public String marketChannel;
    public String packageName;

    /*package*/AppInfo() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("versionName : ").append(versionName).append("\n");
        builder.append("versionCode : ").append(versionCode).append("\n");
        builder.append("marketChannel : ").append(marketChannel).append("\n");
        return builder.toString();
    }
}
