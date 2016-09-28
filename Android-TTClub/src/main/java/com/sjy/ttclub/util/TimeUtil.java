package com.sjy.ttclub.util;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gangqing on 2015/12/28.
 * Email:denggangqing@ta2she.com
 */
public class TimeUtil {
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getSystemTime() {
        String date = "";
        date = mSimpleDateFormat.format(new java.util.Date());
        return date;
    }

    public static String formatTime(String time, DateFormat dateFormat) {
        if (dateFormat == null) {
            dateFormat = mDateFormat;
        }
        Date myDate = null;
        try {
            myDate = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(myDate);
    }

    /**
     * 获取网络时间
     *
     * @return
     */
    public static String getTime() {
        // 取得资源对象
        URL url;
        URLConnection uc = null;
        try {
            url = new URL("http://www.bjtime.cn");
            // 生成连接对象
            uc = url.openConnection();
            // 发出连接
            uc.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = uc.getDate();
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String getCDTime(Date oldTime) {
        String time = "";
        if (oldTime == null) {
            return time;
        }
        String nowTime = getSystemTime();
        Date now, date;
        try {
            now = mSimpleDateFormat.parse(nowTime);
            date = oldTime;
            long cdTime = now.getTime() - date.getTime();
            long day = cdTime / (24 * 60 * 60 * 1000);
            long hour = (cdTime / (60 * 60 * 1000) - day * 24);
            long min = ((cdTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long second = (cdTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (day >= 1) {
                if (day < 2) {
                    time = "昨天";
                } else {
                    time = mDateFormat.format(oldTime);
                }
            } else {
                if (hour > 0 && hour < 24) {
                    time = hour + "小时前";
                } else {
                    if (min > 0 && min < 60) {
                        time = min + "分钟前";
                    } else if ((second >= 0 && second <= 60)) {
                        time = "刚刚";
                    } else {
                        time = mDateFormat.format(oldTime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取时间差
     *
     * @param oldTime
     * @return
     */
    public static String getCDTime(String oldTime) {
        String time = "";
        if (StringUtils.isEmpty(oldTime)) {
            return time;
        }
        try {
            Date date = mSimpleDateFormat.parse(oldTime);
            return getCDTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long getDiffTime(String oldTime) {
        long cdTime = 0;
        String nowTime = getSystemTime();
        Date now, date;
        try {
            now = mSimpleDateFormat.parse(nowTime);
            date = mSimpleDateFormat.parse(oldTime);
            cdTime = now.getTime() - date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdTime;
    }

    public static String getCDTime(long oldTime) {
        String time = "";
        if (oldTime == 0) {
            return time;
        }
        String nowTime = getSystemTime();
        Date now, date;
        try {
            now = mSimpleDateFormat.parse(nowTime);
            date = mSimpleDateFormat.parse(mSimpleDateFormat.format(new Date(oldTime * 1000)));
            long cdTime = now.getTime() - date.getTime();
            long day = cdTime / (24 * 60 * 60 * 1000);
            long hour = (cdTime / (60 * 60 * 1000) - day * 24);
            long min = ((cdTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long second = (cdTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (day >= 1) {
                if (day < 2) {
                    time = "昨天";
                } else {
                    time = mDateFormat.format(new Date(oldTime * 1000));
                }
            } else {
                if (hour > 0 && hour < 24) {
                    time = hour + "小时前";
                } else {
                    if (min > 0 && min < 60) {
                        time = min + "分钟前";
                    } else if ((second >= 0 && second <= 60)) {
                        time = "刚刚";
                    } else {
                        time = mDateFormat.format(new Date(oldTime * 1000));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }
}
