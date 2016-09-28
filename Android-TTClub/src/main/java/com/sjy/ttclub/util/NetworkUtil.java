package com.sjy.ttclub.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.sjy.ttclub.framework.ContextManager;

import java.util.List;

/**
 * Created by linhz on 2015/11/26.
 * Email: linhaizhong@ta2she.com
 */
public class NetworkUtil {

    /**
     * 网络类型是wifi
     */
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_NONE = "no_network";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";

    //接入点类型
    public final static int NETWORK_AP_TYPE_MOBILE_WAP = 0;     //cmwap
    public final static int NETWORK_AP_TYPE_MOBILE_NET = 1;     //cmnet
    public final static int NETWORK_AP_TYPE_WIFI = 2;         //wifi
    public final static int NETWORK_AP_TYPE_NOT_CONFIRM = 99;   //not confirm

    public static final int NETWORK_ERROR = 0;
    public static final int NETWORK_2G = 1;
    public static final int NETWORK_3G4G = 2;
    public static final int NETWORK_WIFI = 3;


    /*
     * 接入点类型：
     * 0：未知(默认)
     * 1：2G
     * 2：2.5G
     * 3：2.75G
     * 4：3G
     * 5：wifi接入点
     * 6：4G
     * */
    public static final int AP_UNKNOWN = 0;
    public static final int AP_2G = 1;
    public static final int AP_2_5G = 2;
    public static final int AP_2_75G = 3;
    public static final int AP_3G = 4;
    public static final int AP_WIFI = 5;
    public static final int AP_4G = 6;

    public static final String NETWORK_CLASS_NO_NETWORK = "-1";
    public static final String NETWORK_CLASS_UNKNOWN = "0";
    public static final String NETWORK_CLASS_2G = "2G";
    public static final String NETWORK_CLASS_2_5G = "2.5G";
    public static final String NETWORK_CLASS_2_75G = "2.75G";
    public static final String NETWORK_CLASS_3G = "3G";
    public static final String NETWORK_CLASS_4G = "4G";

    // 这部分在低版本的SDK中没有定义,但高版本有定义,
    // 由于取值不与其它常量不冲突,因此直接在这里定义
    private static final int NETWORK_TYPE_EVDO_B = 12;
    private static final int NETWORK_TYPE_EHRPD = 14;
    private static final int NETWORK_TYPE_HSPAP = 15;
    private static final int NETWORK_TYPE_LTE = 13;

    private static boolean sCacheActiveNetworkInited = false;
    private static NetworkInfo sCacheActiveNetwork = null;

    public static int getNetworkAcessPoint() {
        // ap
        int iapType = AP_UNKNOWN;

        //如果 AP 为wifi就直接返回 wifi类型
        if (isWifiNetwork()) {
            iapType = AP_WIFI;
        } else {
            // 如果为mobile或其他类型，则判断网络制式
            String netType = NetworkUtil.getNetworkClass();

            if (null != netType) {
                if (NetworkUtil.NETWORK_CLASS_2G.equalsIgnoreCase(netType)) {
                    iapType = AP_2G;
                } else if (NetworkUtil.NETWORK_CLASS_2_5G.equalsIgnoreCase(netType)) {
                    iapType = AP_2_5G;
                } else if (NetworkUtil.NETWORK_CLASS_2_75G.equalsIgnoreCase(netType)) {
                    iapType = AP_2_75G;
                } else if (NetworkUtil.NETWORK_CLASS_3G.equalsIgnoreCase(netType)) {
                    iapType = AP_3G;
                } else if (NetworkUtil.NETWORK_CLASS_4G.equalsIgnoreCase(netType)) {
                    iapType = AP_4G;
                }
            }
        }

        return iapType;
    }

    /**
     * @see {@link #NETWORK_2G}, {@link #NETWORK_3G4G}, {@link #NETWORK_WIFI}, {@link #NETWORK_ERROR}
     */
    public static int getNetworkType() {
        if (NetworkUtil.isWifiNetwork()) {
            return NETWORK_WIFI;
        }
        if (NetworkUtil.is2GNetwork()) {
            return NETWORK_2G;
        } else if (NetworkUtil.is3GAboveNetwork()) {
            return NETWORK_3G4G;
        }
        return NETWORK_ERROR;
    }

    public static boolean isWifiNetwork() {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mIsWifi;
                }
            }
        }

        String apnName = getAccessPointName();
        return NETWORK_TYPE_WIFI.equals(apnName);
    }


    /**
     * 是否是移动网络
     *
     * @return
     */
    public static boolean isMobileNetwork() {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mIsMobileNetwork;
                }
            }
        }

        String apnName = getAccessPointName();
        return (!NETWORK_TYPE_WIFI.equals(apnName)) && (!NETWORK_TYPE_UNKNOWN.equals(apnName)) && (!NETWORK_TYPE_NONE
                .equals(apnName));
    }

    public static boolean is2GNetwork() {
        String networkClass = getNetworkClass();
        if (NETWORK_CLASS_2G.equals(networkClass)
                || NETWORK_CLASS_2_5G.equals(networkClass)
                || NETWORK_CLASS_2_75G.equals(networkClass)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是3G或以上的移动网络
     *
     * @return
     */
    public static boolean is3GAboveNetwork() {
        String networkClass = getNetworkClass();
        if (NETWORK_CLASS_NO_NETWORK.equals(networkClass) || NETWORK_CLASS_UNKNOWN.equals(networkClass) ||
                NETWORK_CLASS_2G.equals(networkClass)
                || NETWORK_CLASS_2_5G.equals(networkClass)
                || NETWORK_CLASS_2_75G.equals(networkClass)) {
            return false;
        }
        return true;
    }

    /**
     * 获取接入点
     *
     * @return cmwap:0 cmnet:1 wfi:2: 99
     */
    public static int getCurrAccessPointType() {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mCurrAccessPointType;
                }
            }
        }

        String apnName = NetworkUtil.getAccessPointName();
        if (NetworkUtil.NETWORK_CLASS_NO_NETWORK.equals(apnName) || NetworkUtil.NETWORK_CLASS_UNKNOWN.equals(apnName)) {
            return NETWORK_AP_TYPE_NOT_CONFIRM;
        }
        if (NetworkUtil.NETWORK_TYPE_WIFI.equalsIgnoreCase(apnName)) {
            return NETWORK_AP_TYPE_WIFI;
        }
        return (NetworkUtil.hasProxyForCurApn() ? NETWORK_AP_TYPE_MOBILE_WAP : NETWORK_AP_TYPE_MOBILE_NET);
    }


    public static String getCurrAccessPointTypeName() {
        int apType = NetworkUtil.getCurrAccessPointType();
        String apTypeName = "unknown";
        switch (apType) {
            case NETWORK_AP_TYPE_MOBILE_WAP:
                apTypeName = "wap";
                break;
            case NETWORK_AP_TYPE_MOBILE_NET:
                apTypeName = "net";
                break;
            case NETWORK_AP_TYPE_WIFI:
                apTypeName = "wifi";
                break;

            default:
                break;
        }
        return apTypeName;
    }

    /**
     * @return 如果当前无activeNetwork, 返回 -1
     * @see NetworkInfo#getType()
     */
    public static int getCurrentNetworkType() {
        NetworkInfo info = getActiveNetworkInfo();
        if (null == info) {
            return -1;
        }

        return info.getType();
    }

    /**
     * @return 如果当前无activityNetwork，返回 ""
     * @see NetworkInfo#getTypeName()
     */
    public static String getCurrentNetworkTypeName() {
        NetworkInfo info = getActiveNetworkInfo();
        String name = null;

        if (null != info) {
            name = info.getTypeName();
        }

        if (null == name) {
            name = "";
        }

        return name;
    }

    public static boolean isNetworkConnected() {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mIsConnected;
                }
            }
        }

        NetworkInfo info = getActiveNetworkInfo();
        return null != info && info.isConnected();
    }

    public static void isNetworkConnectedAsync(final ThreadManager.RunnableEx callback) {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                boolean connected = isNetworkConnected();
                callback.setArg(connected);
            }
        }, callback);
    }

    public static String getCustomizedMacAddress() {
        String MacAddress = HardwareUtil.getMacAddress();
        if (StringUtils.isEmpty(MacAddress)) {
            MacAddress = "unknown";
        } else {
            MacAddress = MacAddress.replace(":", "");
            MacAddress = MacAddress.replace("-", "");
        }

        return MacAddress;
    }

    public static boolean isWifiTurnOn() {
        try {
            WifiManager wifiManager = (WifiManager) ContextManager.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return false;
            }

            if (wifiManager.isWifiEnabled()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获得当前使用网络的信息<br/>
     * 即是连接的网络，如果用系统的api得到的activeNetwork为null<br/>
     * 我们还会一个个去找，以适配一些机型上的问题
     */
    public static NetworkInfo getActiveNetworkInfo() {
        return doGetActiveNetworkInfo(false);
    }

    public static NetworkInfo getActiveNetworkInfoFromCache() {
        return doGetActiveNetworkInfo(true);
    }

    private static NetworkInfo doGetActiveNetworkInfo(boolean isGetFromCache) {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mActiveNetworkInfo;
                }
            }
        }

        if (isGetFromCache) {
            if (sCacheActiveNetworkInited) {
                return sCacheActiveNetwork;
            }
        }

        NetworkInfo activeNetwork = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) ContextManager.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            if (cm == null) {
                return null;
            }

            activeNetwork = cm.getActiveNetworkInfo(); /*获得当前使用网络的信息*/

            if (activeNetwork == null || !activeNetwork.isConnected()) {//当前无可用连接,或者没有连接,尝试取所有网络再进行判断一次
                NetworkInfo[] allNetworks = cm.getAllNetworkInfo();//取得所有网络
                if (allNetworks != null) {//网络s不为null
                    for (int i = 0; i < allNetworks.length; i++) {//遍历每个网络
                        if (allNetworks[i] != null) {
                            if (allNetworks[i].isConnected()) {//此网络是连接的，可用的
                                activeNetwork = allNetworks[i];
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sCacheActiveNetwork = activeNetwork;
        if (!sCacheActiveNetworkInited) {
            sCacheActiveNetworkInited = true;
        }

        return activeNetwork;
    }


    /**
     * 判断当前wifi是否有加密（安全）
     *
     * @return true(加密网络) or false(非加密网络)
     */
    public static boolean checkCurWifiIsSecurity() {
        try {
            WifiManager wifiService = (WifiManager) ContextManager.getSystemService(Context.WIFI_SERVICE);

            WifiConfiguration wifiConfig = null;
            List<WifiConfiguration> wifiList = wifiService.getConfiguredNetworks();

            if (wifiList != null) {
                int listNum = wifiList.size();
                for (int i = 0; i < listNum; i++) {
                    WifiConfiguration conf = wifiList.get(i);
                    if (conf.status == WifiConfiguration.Status.CURRENT) {
                        wifiConfig = conf;
                        break;
                    }
                }

                if (wifiConfig == null) {
                    for (int i = 0; i < listNum; i++) {
                        WifiConfiguration conf = wifiList.get(i);
                        String confStr = conf.toString();
                        String label = "LinkAddresses: [";
                        int off = confStr.indexOf(label);
                        if (off > 0) {
                            off += label.length();
                            if (confStr.indexOf("]", off) > off) {
                                wifiConfig = conf;
                                break;
                            }
                        }
                    }
                }
            }

            if (wifiConfig != null) {
                if (!StringUtils.isEmpty(wifiConfig.preSharedKey)
                        || !StringUtils.isEmpty(wifiConfig.wepKeys[0])
                        || !StringUtils.isEmpty(wifiConfig.wepKeys[1])
                        || !StringUtils.isEmpty(wifiConfig.wepKeys[2])
                        || !StringUtils.isEmpty(wifiConfig.wepKeys[3])) {

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getProxyHost() {
        String proxyHost = null;

        // Proxy
        if (Build.VERSION.SDK_INT >= 11) {
            // Build.VERSION_CODES.ICE_CREAM_SANDWICH IS_ICS_OR_LATER
            proxyHost = System.getProperty("http.proxyHost");
        } else {
            Context context = ContextManager.getContext();
            if (context == null) {
                return proxyHost;
            }
            proxyHost = android.net.Proxy.getHost(context);

            // wifi proxy is unreachable in Android2.3 or lower version
            if (isWifiNetwork()
                    && proxyHost != null
                    && proxyHost.indexOf("10.0.0") != -1) {
                proxyHost = "";
            }

        }

        return proxyHost;
    }

    public static int getProxyPort() {
        int proxyPort = 80;

        // Proxy
        if (Build.VERSION.SDK_INT >= 11) {
            // Build.VERSION_CODES.ICE_CREAM_SANDWICH IS_ICS_OR_LATER
            String portStr = System.getProperty("http.proxyPort");
            try {
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            } catch (Exception e) {
                proxyPort = -1;
            }
        } else {
            Context context = ContextManager.getContext();
            if (context == null) {
                return proxyPort;
            }
            String proxyHost = null;
            proxyHost = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);

            // wifi proxy is unreachable in Android2.3 or lower version
            if (isWifiNetwork()
                    && proxyHost != null
                    && proxyHost.indexOf("10.0.0") != -1) {
                proxyPort = -1;
            }
        }

        return proxyPort;
    }


    public static boolean hasProxyForCurApn() {
        Context context = ContextManager.getContext();
        if (context == null) {
            return false;
        }
        try {
            if (null == getProxyHost()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAccessPointName() {
        return doGetAccessPointName(false);
    }

    public static String getAccessPointNameFromCache() {
        return doGetAccessPointName(true);
    }

    private static String doGetAccessPointName(boolean isGetFromCache) {
        if (mNetworkChangedNetworkArgs != null) {
            synchronized (NetworkUtil.class) {
                if (mNetworkChangedNetworkArgs != null) {
                    return mNetworkChangedNetworkArgs.mAccessPointName;
                }
            }
        }

        NetworkInfo activeNetwork = null;

        if (isGetFromCache) {
            activeNetwork = getActiveNetworkInfoFromCache();
        } else {
            activeNetwork = getActiveNetworkInfo();
        }

        String apnName = NETWORK_TYPE_UNKNOWN;

        if (null == activeNetwork) {
            apnName = NETWORK_TYPE_NONE;
            return apnName;
        }

        int networkType = activeNetwork.getType();
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_TYPE_WIFI;
        }

        if (activeNetwork.getExtraInfo() != null)
            apnName = activeNetwork.getExtraInfo().toLowerCase();

        if (networkType == ConnectivityManager.TYPE_MOBILE) {
            if (apnName.contains("cmwap")) {
                apnName = "cmwap";
            } else if (apnName.contains("cmnet")) {
                apnName = "cmnet";
            } else if (apnName.contains("uniwap")) {
                apnName = "uniwap";
            } else if (apnName.contains("uninet")) {
                apnName = "uninet";
            } else if (apnName.contains("3gwap")) {
                apnName = "3gwap";
            } else if (apnName.contains("3gnet")) {
                apnName = "3gnet";
            } else if (apnName.contains("ctwap")) {
                apnName = "ctwap";
            } else if (apnName.contains("ctnet")) {
                apnName = "ctnet";
            } else {

            }
        } else {
            apnName = NETWORK_TYPE_WIFI;
        }
        return apnName;
    }

    public static String getNetworkClass() {
        NetworkInfo activeNetwork = getActiveNetworkInfo();
        if (activeNetwork == null) {
            return NETWORK_CLASS_NO_NETWORK;
        }

        int netSubType = activeNetwork.getSubtype();
        switch (netSubType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2G;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return NETWORK_CLASS_2_5G;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NETWORK_CLASS_2_75G;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return NETWORK_CLASS_3G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    //以下代码是为了防止用户在networkchanged时候获取网络参数导致的Binder IPC卡死问题
    public static class NetworkArgs {
        public NetworkInfo mActiveNetworkInfo;
        public boolean mIsWifi;
        public boolean mIsConnected;
        public int mCurrAccessPointType;
        public boolean mIsMobileNetwork;
        public String mAccessPointName;
    }

    private static NetworkArgs mNetworkChangedNetworkArgs;

    public static NetworkArgs getNetworkArgs() {
        return mNetworkChangedNetworkArgs;
    }

    public static void setNetworkArgs(NetworkArgs args) {
        synchronized (NetworkUtil.class) {
            mNetworkChangedNetworkArgs = args;
        }

        if (args != null) {
            sCacheActiveNetwork = args.mActiveNetworkInfo;
        }
    }

}
