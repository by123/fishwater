package com.sjy.ttclub.push;

/**
 * Created by linhz on 2015/12/12.
 * Email: linhaizhong@ta2she.com
 */
public interface IPushManager {

    void initPush();

    void setPushEventListener(IPushEventListener listenr);

    void addTag(String tag);

    void removeTag(String tag);
}
