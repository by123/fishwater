/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.ArrayList;
import java.util.List;

public class ApkSignUtil {

    /**
     * 这里只获得到人个签名
     *
     * @param aContext
     * @param aApkPath
     * @return
     */
    public static String getApkSign(Context aContext, String aApkPath) {

        if ((null == aApkPath) || (null == aContext)) {
            return null;
        }

        String ret = null;

        try {
            PackageInfo packageInfo = aContext.getPackageManager()
                    .getPackageArchiveInfo(aApkPath,
                            PackageManager.GET_SIGNATURES);
            if (null != packageInfo) {
                Signature[] signs = packageInfo.signatures;
                if ((null != signs) && (0 < signs.length)) {
                    ret = signs[0].toCharsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 获得所有签名文件
     *
     * @param context
     * @param file
     * @return
     */
    public static List<String> getApkAllSigns(Context context, String aApkPath) {
        PackageManager pacakageManager = context.getPackageManager();
        try {
            PackageInfo packgeInfo = pacakageManager.getPackageArchiveInfo(
                    aApkPath, PackageManager.GET_SIGNATURES);
            Signature[] ss = packgeInfo.signatures;
            if (ss == null || ss.length == 0) {
                return null;
            }
            List<String> md5s = new ArrayList<String>(ss.length);
            for (int i = 0; i < ss.length; i++) {
                Signature signature = ss[i];
                md5s.add(signature.toCharsString());
            }
            return md5s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得所有签名文件
     *
     * @param context
     * @param aApkPath
     * @return
     */
    public static List<String> getApkAllSignsMd5(Context context,
                                                 String aApkPath) {
        PackageManager pacakageManager = context.getPackageManager();
        try {
            PackageInfo packgeInfo = pacakageManager.getPackageArchiveInfo(
                    aApkPath, PackageManager.GET_SIGNATURES);
            Signature[] ss = packgeInfo.signatures;
            if (ss == null || ss.length == 0) {
                return null;
            }
            List<String> md5s = new ArrayList<String>(ss.length);
            for (int i = 0; i < ss.length; i++) {
                Signature signature = ss[i];
                byte[] bs = signature.toByteArray();
                md5s.add(Md5Utils.getMD5(bs));
            }
            return md5s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里只获得到一个签名
     *
     * @param aContext
     * @param aPackageName
     * @return
     */
    public static String getAppSign(Context aContext, String aPackageName) {

        if ((null == aPackageName) || (null == aContext)) {
            return null;
        }

        String ret = null;
        PackageInfo packageInfo = CommonUtils.getPackageInfo(aContext,
                aPackageName, PackageManager.GET_SIGNATURES);

        if (null != packageInfo) {
            Signature[] signs = packageInfo.signatures;
            if (signs != null && signs.length > 0) {
                ret = signs[0].toCharsString();
            }
        }

        return ret;
    }

    /**
     * 这里只获得到一个签名
     *
     * @param aContext
     * @param aPackageName
     * @return
     */
    public static List<String> getAppAllSigns(Context aContext,
                                              String aPackageName) {

        if ((null == aPackageName) || (null == aContext)) {
            return null;
        }

        PackageInfo packageInfo = CommonUtils.getPackageInfo(aContext,
                aPackageName, PackageManager.GET_SIGNATURES);

        if (null != packageInfo) {
            Signature[] signs = packageInfo.signatures;
            if (signs == null || signs.length == 0) {
                return null;
            }
            List<String> md5s = new ArrayList<String>(signs.length);
            for (int i = 0; i < signs.length; i++) {
                Signature signature = signs[i];
                md5s.add(signature.toCharsString());
            }
            return md5s;
        }

        return null;
    }

    /**
     * 这里只获得到一个签名
     *
     * @param aContext
     * @param aPackageName
     * @return
     */
    public static List<String> getAppAllSignsMd5(Context aContext,
                                                 String aPackageName) {

        if ((null == aPackageName) || (null == aContext)) {
            return null;
        }

        PackageInfo packageInfo = CommonUtils.getPackageInfo(aContext,
                aPackageName, PackageManager.GET_SIGNATURES);

        if (null != packageInfo) {
            Signature[] ss = packageInfo.signatures;
            if (ss == null || ss.length == 0) {
                return null;
            }
            List<String> md5s = new ArrayList<String>(ss.length);
            for (int i = 0; i < ss.length; i++) {
                Signature signature = ss[i];
                byte[] bs = signature.toByteArray();
                md5s.add(Md5Utils.getMD5(bs));
            }
            return md5s;
        }

        return null;
    }

    public static String getSelfSign(Context context) {
        String pkName = CommonUtils.getPackageName(context);
        return getAppSign(context, pkName);
    }

    public static List<String> getSelfAllSigns(Context context) {
        String pkName = CommonUtils.getPackageName(context);
        return getAppAllSigns(context, pkName);
    }

    public static List<String> getSelfAllSignsMd5(Context context) {
        String pkName = CommonUtils.getPackageName(context);
        return getAppAllSignsMd5(context, pkName);
    }

}
