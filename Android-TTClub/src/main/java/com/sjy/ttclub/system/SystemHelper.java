package com.sjy.ttclub.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;

import java.util.UUID;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class SystemHelper {
    public static final String KEY_UUID = "UUid";
    private static final String SHARE_PREFERENCE_NAME_OLD = "user";
    private static AppInfo sAppInfo;
    private static DeviceInfo sDeviceInfo;

    public static void init(Context context) {
        sAppInfo = new AppInfo();
        sAppInfo.marketChannel = MarketChannelManager.getInstance().getMarketChannel();
        String apkVersionName;
        int apkVersionCode;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                apkVersionName = packageInfo.versionName;
                apkVersionCode = packageInfo.versionCode;
            } else {
                apkVersionCode = 10;
                apkVersionName = "2.6.0";
            }
        } catch (Exception e) {
            e.printStackTrace();
            apkVersionName = "2.6.0";
            apkVersionCode = 10;
        }
        sAppInfo.versionCode = apkVersionCode;
        sAppInfo.versionName = apkVersionName;
        sAppInfo.packageName = context.getPackageName();

        sDeviceInfo = new DeviceInfo();
        sDeviceInfo.sdkVersionName = Build.VERSION.RELEASE;
        sDeviceInfo.sdkVersionCode = Build.VERSION.SDK_INT;
        sDeviceInfo.deviceName = Build.MODEL;
        sDeviceInfo.manufacturer = Build.MANUFACTURER;
        sDeviceInfo.androidId = HardwareUtil.getAndroidId();
        sDeviceInfo.imei = HardwareUtil.getIMEI();
        sDeviceInfo.macAdr = HardwareUtil.getMacAddress();
        sDeviceInfo.simNo = HardwareUtil.getSimNo();
        UUID deviceUuid = new UUID(sDeviceInfo.androidId.hashCode(), ((long) sDeviceInfo.imei.hashCode() << 32) |
                sDeviceInfo.simNo.hashCode());
        sDeviceInfo.deviceId = deviceUuid.toString();

        String uuid = SettingFlags.getStringFlag(KEY_UUID, null);
        if (StringUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SettingFlags.setFlag(KEY_UUID, uuid);
        }
        sDeviceInfo.uuid = uuid;

        if (BuildConfig.DEBUG == true) {
            Log.i("SystemHelper", "app info --> " + sAppInfo);
            Log.i("SystemHelper", "device info --> " + sDeviceInfo);
        }
    }

    public static AppInfo getAppInfo() {
        return sAppInfo;
    }

    public static DeviceInfo getDeviceInfo() {
        return sDeviceInfo;
    }

    public static SharedPreferences getOldSharePreference(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_PREFERENCE_NAME_OLD, Context.MODE_PRIVATE);
        return sp;
    }
}
