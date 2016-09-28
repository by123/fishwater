package com.sjy.ttclub.splashscreen;

import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

/**
 * Created by zhangwulin on 2016/1/8.
 * email 1501448275@qq.com
 */
public class SplashUtil {

    /**
     * 是否展示闪屏
     *
     * @return
     */
    public static boolean isShowSplash() {
        boolean isShowSplash = false;
        if (StringUtils.isEmpty(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_STARTTIME)) ||
                StringUtils.isEmpty(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_ENDTIME))) {
            isShowSplash = false;
        }else if (TimeUtil.getDiffTime(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_STARTTIME)) > 0 && TimeUtil.getDiffTime
                (SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_ENDTIME)) < 0) {
            isShowSplash = true;
        }
        return isShowSplash;
    }

    /**
     * 是否纯图闪屏
     *
     * @return
     */
    public static boolean isImageSplash() {
        boolean isImageSplash = false;
        int adAttr = SettingFlags.getIntFlag(SplashConstant.SPLASH_KEY_ADATTR);
        if (adAttr == CommonConst.BANNER_TYPE_IMAGE) {
            isImageSplash = true;
        }
        return isImageSplash;
    }
}
