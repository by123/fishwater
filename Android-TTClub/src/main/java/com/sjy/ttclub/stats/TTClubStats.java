/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.stats;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by linhz on 2015/10/28.
 * Email: linhaizhong@ta2she.com
 */
public interface TTClubStats {
    void config();

    void statsKey(String key);

    void statsKeyValue(String eventId, String key, String value);

    void statsKeyValue(String eventId, HashMap<String, String> map);

    void pause(Context ctx);

    void resume(Context ctx);

    void destory();
}
