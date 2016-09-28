package com.sjy.ttclub;

import android.content.Intent;
import android.os.Bundle;

import com.sjy.ttclub.bean.Banner;

/**
 * Created by linhz on 2016/1/13.
 * Email: linhaizhong@ta2she.com
 */
public class InitEventInfo {
    public static final String KEY_PUSH = "ttclub_push";

    public static final int TYPE_BANNER = 1;
    public static final int TYPE_PUSH = 2;

    public static final int TYPE_OTHERS = 10;

    public int type;
    public Banner banner;
    public Intent intent;
    public Bundle bundle;
}
