/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.sjy.ttclub.util.file.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL10;

public class HardwareUtil {

    // SAFE_STATIC_VAR
    private static Context sContext = null;

    private static final boolean DEBUG = false;
    private static final String TAG = "HardwareUtil";

    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;

    private static final String CPU_INFO_CORE_COUNT_FILE_PATH = "/sys/devices/system/cpu/";
    private static final String CPU_INFO_MAX_FREQ_FILE_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private static final String MEMORY_INFO_PATH = "/proc/meminfo";
    private static final String MEMORY_OCUPIED_INFO_PATH = "/proc/self/status";
    private static final String MEMORY_INFO_TAG_VM_RSS = "VmRSS:";
    private static final String MEMORY_INFO_TAG_VM_DATA = "VmData:";
    public static final String FILE_IMEI = "8B277D535A8C846BDDD370A589B9D93C3B2B6247";

    private static final double COMPUTE_SCREENSIZE_DIFF_LIMIT = 0.5f;

    public static final int DEFAULT_DENSITY = 240;
    public static final int HIGH_QUALITY_DENSITY = 320;
    // unit in kb
    public static final long G_UNIT_IN_KB = 1024 * 1024;

    // SAFE_STATIC_VAR
    private static boolean sHasInitedAndroidId = false;
    // SAFE_STATIC_VAR
    private static String sAndroidId = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitMacAddress = false;
    // SAFE_STATIC_VAR
    private static String sMacAddress = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitIMEI = false;
    // SAFE_STATIC_VAR
    private static String sIMEI = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitCpuCoreCount = false;
    // SAFE_STATIC_VAR
    private static int sCpuCoreCount = 1;
    // SAFE_STATIC_VAR
    private static boolean sHasInitMaxCpuFrequence = false;
    // SAFE_STATIC_VAR
    private static int sMaxCpuFrequence = 0;
    // SAFE_STATIC_VAR
    private static boolean sHasInitCpuArch = false;
    // SAFE_STATIC_VAR
    private static String sCpuArch = "";
    // SAFE_STATIC_VAR
    private static boolean sHasInitTotalMemory = false;
    // SAFE_STATIC_VAR
    private static long sTotalMemory = 0;
    // SAFE_STATIC_VAR
    private static boolean sHasInitDeviceSize = false;
    // SAFE_STATIC_VAR
    private static double sDeviceSize = 0;

    private static boolean sHasInitCpuInfo = false;
    private static String sCpuInfoArch = "";
    private static String sCpuInfoVfp = "";
    private static String sCpuArchit = "";

    /**
     * screenWidth & screenHeight means the display resolution. windowWidth &
     * windowHeight means the application window rectangle. in not full screen
     * mode: screenHeight == windowHeight + systemStatusBarHeight in full screen
     * mode: screenHeight == windowHeight
     * <p/>
     * no matter what situation, screenWidth === windowWidth;
     */
    // SAFE_STATIC_VAR
    public static int screenWidth, screenHeight;
    // SAFE_STATIC_VAR
    public static int windowWidth, windowHeight;
    public static float density = 1.0f; //
    public static float densityDpi = 240;

    /**
     * @param context
     * @note You must call this before calling any other methods!!
     */
    public static void initialize(Context context) {
        if (context != null) {
            sContext = context.getApplicationContext();
        }
    }

    /**
     * Call this to clear reference of Context instance, which is set by
     * {@link #initialize(Context)}.
     */
    public static void destroy() {
        sContext = null;
    }

    private static void checkIfContextInitialized() {
        if (sContext == null) {
            throw new RuntimeException(
                    "context has not been initialized! You MUST call this only after initialize() is invoked.");
        }
    }

    /**
     * @return A 64-bit number (as a hex string) that is randomly generated on
     * the device's first boot and should remain constant for the
     * lifetime of the device. (The value may change if a factory reset
     * is performed on the device
     */
    public static String getAndroidId() {
        checkIfContextInitialized();
        if (sHasInitedAndroidId) {
            return sAndroidId;
        }

        try {
            sAndroidId = Settings.Secure.getString(
                    sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sAndroidId == null) {
            sAndroidId = "";
        }
        sHasInitedAndroidId = true;
        if (DEBUG) {
            Log.i(TAG, "getAndroidId: " + sAndroidId);
        }
        return sAndroidId;
    }

    /**
     * @return if get mac address failed, "" will be returned.
     */
    public static String getMacAddress() {
        checkIfContextInitialized();
        if (sHasInitMacAddress || sContext == null) {
            return sMacAddress;
        }

        try {
            WifiManager wifi = (WifiManager) sContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            sMacAddress = info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sMacAddress == null) {
            sMacAddress = "";
        } else if (!TextUtils.isEmpty(sMacAddress)) {
            sHasInitMacAddress = true;
        }

        if (DEBUG) {
            Log.i(TAG, "getMacAddress: " + sMacAddress);
        }
        return sMacAddress;
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> allInterface = NetworkInterface
                    .getNetworkInterfaces();
            if (allInterface == null) {
                return null;
            }
            InetAddress foundAddr = null;
            NetworkInterface foundInerface = null;

            for (; allInterface.hasMoreElements(); ) {
                NetworkInterface element = allInterface.nextElement();
                if (element == null) {
                    continue;
                }
                Enumeration<InetAddress> enumIpAddr = element
                        .getInetAddresses();
                if (enumIpAddr == null) {
                    continue;
                }

                for (; enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress == null) {
                        continue;
                    }
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        if (null == foundAddr) {
                            foundAddr = inetAddress;
                            foundInerface = element;
                        } else {
                            String foundName = foundInerface.getName();
                            String newName = element.getName();
                            if (foundName != null && !foundName.contains("p2p")
                                    && newName != null
                                    && (newName.contains("p2p"))) {
                                foundAddr = inetAddress;
                                foundInerface = element;
                            } else if (foundName != null
                                    && !foundName.contains("wlan")
                                    && !foundName.contains("p2p")
                                    && newName != null
                                    && (newName.contains("wlan"))) {
                                foundAddr = inetAddress;
                                foundInerface = element;
                            }
                        }
                    }
                }
            }

            if (foundAddr != null) {
                return foundAddr.getHostAddress();
            }

        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getIMEI() {
        checkIfContextInitialized();
        if (sHasInitIMEI || sContext == null) {
            return sIMEI;
        }
        sIMEI = getIMEIInner();
        if (TextUtils.isEmpty(sIMEI)) {
            sIMEI = "null";
        }
        sHasInitIMEI = true;
        return sIMEI;
    }

    public static String getSimNo() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)sContext.getSystemService(Context.TELEPHONY_SERVICE);
        String simNo =  mTelephonyMgr.getSimSerialNumber();
        if (StringUtils.isEmpty(simNo)) {
            return "null";
        }
        return simNo;
    }

    /**
     * @note make sure the READ_PHONE_STATE permission is opened in
     * AndroidManifest.xml if this method is used.<br>
     */
    public static String getIMEIInner() {
        String imei = null;
        try {
            TelephonyManager telephonyMgr = (TelephonyManager) sContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    public static String getImsi() {
        if (null == sContext) {
            return "null";
        }
        String defImsi = "null";
        String imsi = null;
        if (null != sContext) {
            try {
                TelephonyManager telephonyMgr = (TelephonyManager) sContext
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (null != telephonyMgr) {
                    imsi = telephonyMgr.getSubscriberId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(imsi)) {
            imsi = defImsi;
        }
        return imsi;
    }

    public static int getCpuCoreCount() {
        if (sHasInitCpuCoreCount) {
            return sCpuCoreCount;
        }

        final class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                try {
                    if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                        return true;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return false;
            }
        }

        try {
            File dir = new File(CPU_INFO_CORE_COUNT_FILE_PATH);
            File[] files = dir.listFiles(new CpuFilter());
            sCpuCoreCount = files.length;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (sCpuCoreCount < 1) {
            sCpuCoreCount = 1;
        }
        sHasInitCpuCoreCount = true;
        if (DEBUG) {
            Log.i(TAG, "getCpuCoreCount: " + sCpuCoreCount);
        }
        return sCpuCoreCount;
    }

    public static int getMaxCpuFrequence() {
        if (sHasInitMaxCpuFrequence) {
            return sMaxCpuFrequence;
        }

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(CPU_INFO_MAX_FREQ_FILE_PATH);
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                sMaxCpuFrequence = StringUtils.parseInt(text.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (sMaxCpuFrequence < 0) {
            sMaxCpuFrequence = 0;
        }
        sHasInitMaxCpuFrequence = true;
        if (DEBUG) {
            Log.i(TAG, "getMaxCpuFrequence: " + sMaxCpuFrequence + " Hz");
        }
        return sMaxCpuFrequence;
    }

    /**
     * Processor : ARMv7 Processor rev 0 (v7l) processor : 0 BogoMIPS : 996.14
     * <p/>
     * processor : 1 BogoMIPS : 996.14
     * <p/>
     * Features : swp half thumb fastmult vfp edsp vfpv3 vfpv3d16 CPU
     * implementer : 0x41 CPU architecture: 7 CPU variant : 0x1 CPU part : 0xc09
     * CPU revision : 0
     * <p/>
     * Hardware : star Revision : 0000 Serial : 0000000000000000
     */
    private static void initCpuInfo() {
        if (sHasInitCpuInfo) {
            return;
        }
        BufferedReader bis = null;
        try {
            bis = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
            HashMap<String, String> cpuInfoMap = new HashMap<String, String>();
            String line;
            while ((line = bis.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    String[] pairs = line.split(":");
                    if (pairs.length > 1) {
                        cpuInfoMap.put(pairs[0].trim(), pairs[1].trim());
                    }
                }
            }

            String processor = cpuInfoMap.get("Processor");
            if (processor != null) {
                int index1 = processor.indexOf("(");
                int index2 = processor.lastIndexOf(")");
                int len = index2 - index1;
                if (index1 > 0 && index2 > 0 && len > 0) {
                    sCpuInfoArch = processor.substring(index1 + 1, index2);
                } else {
                    sCpuInfoArch = "v" + cpuInfoMap.get("CPU architecture");
                }
            }
            sCpuInfoVfp = cpuInfoMap.get("Features");
            sCpuArchit = cpuInfoMap.get("CPU part");
            sHasInitCpuInfo = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCpuInfoArch() {
        initCpuInfo();
        return sCpuInfoArch;
    }

    public static String getCpuInfoArchit() {
        initCpuInfo();
        return sCpuArchit;
    }

    public static String getCpuInfoVfp() {
        initCpuInfo();
        return sCpuInfoVfp;
    }

    public static String getCpuArch() {
        if (sHasInitCpuArch) {
            return sCpuArch;
        }

        final int ICE_CREAM_SANDWICH = 14;
        final int JELLY_BEAN = 16;
        if (Build.VERSION.SDK_INT < ICE_CREAM_SANDWICH
                || Build.VERSION.SDK_INT > JELLY_BEAN) {
            BufferedReader input = null;
            try {
                Process process = Runtime.getRuntime().exec(
                        "getprop ro.product.cpu.abi");
                input = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String strAbi = input.readLine();

                if (strAbi != null && strAbi.contains("x86")) {
                    sCpuArch = "x86";
                } else if (strAbi != null && strAbi.contains("armeabi-v7a")) {
                    sCpuArch = "armv7";
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtil.safeClose(input);
            }
        }

        if (TextUtils.isEmpty(sCpuArch)) {
            try {
                sCpuArch = System.getProperty("os.arch").toLowerCase();
                if (sCpuArch != null && sCpuArch.contains("i686")) {
                    sCpuArch = "x86";
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (sCpuArch == null) {
            sCpuArch = "";
        }
        sHasInitCpuArch = true;
        if (DEBUG) {
            Log.i(TAG, "getCpuArch: " + sCpuArch);
        }
        return sCpuArch;
    }

    public static boolean isLowDevice() {
        long total = getTotalMemory();
        return total < G_UNIT_IN_KB;
    }

    /**
     * @return device total memory (KB)
     */
    public static long getTotalMemory() {
        if (sHasInitTotalMemory) {
            return sTotalMemory;
        }

        final int bufferSize = 8192; //
        try {
            FileReader fr = new FileReader(MEMORY_INFO_PATH);
            BufferedReader br = new BufferedReader(fr, bufferSize);
            String memory = br.readLine();
            if (memory != null) {
                String[] arrayOfString = memory.split("\\s+");
                if (arrayOfString != null && arrayOfString.length > 1
                        && arrayOfString[1] != null) {
                    sTotalMemory = StringUtils.parseLong(arrayOfString[1].trim());
                }
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sTotalMemory < 0) {
            sTotalMemory = 0;
        }
        sHasInitTotalMemory = true;
        if (DEBUG) {
            Log.i(TAG, "getTotalMemory: " + sTotalMemory + " KB");
        }
        return sTotalMemory;
    }

    /**
     * @return device free memory (KB)
     */
    public static int getFreeMemory() {
        int memory = 0;
        FileInputStream is = null;
        try {
            File fpath = new File(MEMORY_INFO_PATH);
            if (!fpath.exists()) {
                return memory;
            }

            final int BUFFER_LENGTH = 1024;
            byte buffer[] = new byte[BUFFER_LENGTH];
            is = new FileInputStream(fpath);
            int length = is.read(buffer);
            length = length >= BUFFER_LENGTH ? BUFFER_LENGTH : length;
            buffer[length - 1] = '\0';
            String str = new String(buffer);

            memory += getMemorySizeFromMemInfo(str, "MemFree:");
            memory += getMemorySizeFromMemInfo(str, "Buffers:");
            memory += getMemorySizeFromMemInfo(str, "Cached:");

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(is);
        }

        return memory < 0 ? 0 : memory;
    }

    private static int getMemorySizeFromMemInfo(String infoStr, String keyWord) {

        if (StringUtils.isEmpty(infoStr) || StringUtils.isEmpty(keyWord)) {
            return 0;
        }

        int memSize = 0;
        int idxStart = infoStr.indexOf(keyWord);
        if (idxStart >= 0) {
            idxStart += keyWord.length();
            int idxEnd = infoStr.indexOf("kB", idxStart);
            if (idxEnd >= 0) {
                String mem = infoStr.substring(idxStart, idxEnd).trim();
                memSize = StringUtils.parseInt(mem);
            }
        }
        return memSize;
    }

    public static int getOcupiedRssMemory() {
        return getOcupiedMemory(MEMORY_INFO_TAG_VM_RSS);
    }

    /**
     * @return Size of "data" segment (KB)
     */
    public static int getOcupiedDataMemory() {
        return getOcupiedMemory(MEMORY_INFO_TAG_VM_DATA);
    }

    public static long getJavaHeapSize() {
        if (Build.VERSION.SDK_INT >= 9) {
            return Runtime.getRuntime().totalMemory();
        } else {
            return Runtime.getRuntime().totalMemory()
                    + Debug.getNativeHeapAllocatedSize();
        }
    }

    public static double getDeviceSize() {
        checkIfContextInitialized();
        // return HardwareUtilImpl.getDeviceSize(sContext);
        if (sHasInitDeviceSize || sContext == null) {
            return sDeviceSize;
        }

        final DisplayMetrics dm = new DisplayMetrics();
        final WindowManager wm = (WindowManager) sContext
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        final int width = guessSolutionValue(dm.widthPixels);
        final int height = guessSolutionValue(dm.heightPixels);
        final float dpi = dm.densityDpi;
        final float xdpi = dm.xdpi;
        final float ydpi = dm.ydpi;

        double screenSize = 0;
        if (dpi != 0) {
            screenSize = Math.sqrt(width * width + height * height) / dpi;
        }

        double screenSize2 = 0;
        if (xdpi != 0 && ydpi != 0) {
            double widthInches = width / xdpi;
            double heightInches = height / ydpi;
            screenSize2 = Math.sqrt(widthInches * widthInches + heightInches
                    * heightInches);
        }

        final double diff = Math.abs(screenSize2 - screenSize);
        sDeviceSize = diff <= COMPUTE_SCREENSIZE_DIFF_LIMIT ? screenSize2
                : screenSize;

        sHasInitDeviceSize = true;

        return sDeviceSize;
    }

    public static int getGlMaxTextureSize() {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        return maxTextureSize[0];
    }

    public static String getCpuArchPrefix() {
        String strArch = getCpuArch();
        if (strArch.startsWith("armv7")) {
            strArch = "arm7";
        } else if (strArch.startsWith("armv6")) {
            strArch = "arm6";
        } else if (strArch.startsWith("armv5")) {
            strArch = "arm5";
        } else if ("x86".equals(strArch) || "i686".equals(strArch)) {
            strArch = "x86";
        } else if ("mips".equals(strArch)) {
            strArch = "mips";
        }

        return strArch;
    }

    private static int getWindowWidth(Context context) {
        final WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int deviceWidth = Math.min(dm.widthPixels, dm.heightPixels);
        return deviceWidth;
    }

    public static int getDeviceWidth() {
        return screenWidth < screenHeight ? screenWidth : screenHeight;
    }


    public static int getDeviceHeight() {
        return screenWidth > screenHeight ? screenWidth : screenHeight;
    }

    private static int guessSolutionValue(int value) {
        if (value >= 1180 && value <= 1280) {
            return 1280;
        }
        return value;
    }

    private static int getOcupiedMemory(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return 0;
        }

        int ocupied = 0;
        FileInputStream is = null;
        try {
            File file = new File(MEMORY_OCUPIED_INFO_PATH);
            if (!file.exists()) {
                return ocupied;
            }

            final int BUFFER_LENGTH = 1000;
            byte buffer[] = new byte[BUFFER_LENGTH];
            is = new FileInputStream(file);
            int length = is.read(buffer);
            buffer[length] = '\0';
            String str = new String(buffer);
            int idxStart = str.indexOf(tag);
            if (idxStart >= 0) {
                idxStart += 7;
                int idxEnd = str.indexOf("kB", idxStart);
                if (idxEnd >= 0) {
                    String memory = str.substring(idxStart, idxEnd).trim();
                    ocupied = StringUtils.parseInt(memory);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Throwable ta) {
            }
        }

        return ocupied;
    }

    public static boolean hasRoot() {
        final String rootFileOne = "system/bin/su";
        final String rootFileTwo = "system/xbin/su";
        File file = new File(rootFileOne);
        if (file.exists()) {
            return true;
        }
        file = new File(rootFileTwo);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Specifies the type of layer backing this view. The layer can be
     * {@link #LAYER_TYPE_NONE}, {@link #LAYER_TYPE_SOFTWARE} or
     * {@link #LAYER_TYPE_HARDWARE}.
     * </p>
     *
     * @param v
     * @param type The type of layer to use with this view, must be one of
     *             {@link #LAYER_TYPE_NONE}, {@link #LAYER_TYPE_SOFTWARE} or
     *             {@link #LAYER_TYPE_HARDWARE}
     * @see #LAYER_TYPE_NONE
     * @see #LAYER_TYPE_SOFTWARE
     * @see #LAYER_TYPE_HARDWARE
     */
    public static void setLayerType(View v, int type) {
        try {
            Integer realType = -1;
            switch (type) {
                case LAYER_TYPE_NONE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_NONE");
                    break;
                case LAYER_TYPE_SOFTWARE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_SOFTWARE");
                    break;
                case LAYER_TYPE_HARDWARE:
                    realType = ReflectionHelper.getIntFileValueFromClass(
                            View.class, "LAYER_TYPE_HARDWARE");
                    break;
                default:
                    throw new RuntimeException("unsupported layer type");
            }
            if (ReflectionHelper.INVALID_VALUE == realType) {
                return;
            }

            Class<View> cls = View.class;
            @SuppressWarnings("rawtypes")
            Class paramtypes[] = new Class[2];
            paramtypes[0] = Integer.TYPE;
            paramtypes[1] = Paint.class;
            Method method = cls.getMethod("setLayerType", paramtypes);
            Object arglist[] = new Object[2];
            arglist[0] = realType;
            arglist[1] = null;
            method.invoke(v, arglist);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void buildLayer(View v) {
        try {
            Class<View> cls = View.class;
            Method method = cls.getMethod("buildLayer", new Class[0]);
            method.invoke(v, new Object[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
