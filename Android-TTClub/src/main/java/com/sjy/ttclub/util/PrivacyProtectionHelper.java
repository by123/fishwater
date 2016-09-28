package com.sjy.ttclub.util;

import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by gangqing on 2016/1/5.
 * Email:denggangqing@ta2she.com
 */
public class PrivacyProtectionHelper {
    private static final String PRIVACY_PROTECTION_PASSWORD = "open_password";
    private static final String PRIVACY_PROTECTION_STATE = "isOpenPrivacy";

    /**
     * 保存隐私密码
     */
    public static void savePassword(String password, boolean isOpen) {
        if (password == null) {
            password = "";
        }
        SettingFlags.setFlag(PRIVACY_PROTECTION_PASSWORD, password);
        SettingFlags.setFlag(PRIVACY_PROTECTION_STATE, isOpen);
        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_PRIVACY_PROTECTION_STATE_CHANGE);
    }

    /**
     * 从本地获取隐私密码
     */
    private static String getPasswordForLocal() {
        return SettingFlags.getStringFlag(PRIVACY_PROTECTION_PASSWORD, "");
    }

    /**
     * 是否开启了隐私保护
     *
     * @return
     */
    public static boolean isOpenPrivacyProtection() {
        if (StringUtils.isNotEmpty(getPasswordForLocal())) {
            return SettingFlags.getBooleanFlag(PRIVACY_PROTECTION_STATE, false);
        }
        return false;
    }

    /**
     * 判断密码是否正确
     *
     * @param password
     */
    public static boolean equalsPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        String localPassword = getPasswordForLocal();
        if (password.equals(localPassword)) {
            return true;
        } else {
            return false;
        }
    }

}
