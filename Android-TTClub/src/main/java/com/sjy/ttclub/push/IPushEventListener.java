package com.sjy.ttclub.push;

import java.util.Map;

/**
 * Created by linhz on 2015/12/12.
 * Email: linhaizhong@ta2she.com
 */
public interface IPushEventListener {
    void onPushNotificationEvent(Map<String, String> map);

    void onPushCustomMessageEvent(Map<String, String> map);

    void onPushRegistered(String deviceToken);
}
