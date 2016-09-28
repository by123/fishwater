package com.sjy.ttclub.share;

import android.content.Context;
import android.content.res.Resources;

import com.lsym.ttclub.R;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/12.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class SharePlatformConfig {

    private static final ArrayList<SharePlatformInfo> sPlatformList = new ArrayList<SharePlatformInfo>();
    private static boolean sIsInited = false;

    private static void initPlatformList(Context context) {
        sPlatformList.clear();
        Resources res = context.getResources();
        SharePlatformInfo info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_QZONE;
        info.title = res.getString(R.string.share_platform_qzone);
        info.icon = res.getDrawable(R.drawable.share_qzone);
        info.type = SharePlatformInfo.TYPE_NORMAL;
        sPlatformList.add(info);

        info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_WECHAT_FRIENDS;
        info.title = res.getString(R.string.share_platform_wechat_friends);
        info.icon = res.getDrawable(R.drawable.share_wechat_friends);
        info.type = SharePlatformInfo.TYPE_NORMAL;
        sPlatformList.add(info);

        info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_WECHAT_TIMELINE;
        info.title = res.getString(R.string.share_platform_wechat_timeline);
        info.icon = res.getDrawable(R.drawable.share_wechat_timeline);
        info.type = SharePlatformInfo.TYPE_NORMAL;
        sPlatformList.add(info);

        info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_QQ;
        info.title = res.getString(R.string.share_platform_qq);
        info.icon = res.getDrawable(R.drawable.share_qq);
        info.type = SharePlatformInfo.TYPE_NORMAL;
        sPlatformList.add(info);

        info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_WEIBO;
        info.title = res.getString(R.string.share_platform_weibo);
        info.icon = res.getDrawable(R.drawable.share_weibo);
        info.type = SharePlatformInfo.TYPE_NORMAL;
        sPlatformList.add(info);

        info = new SharePlatformInfo();
        info.id = SharePlatformInfo.ID_MORE;
        info.title = res.getString(R.string.share_platform_more);
        info.icon = res.getDrawable(R.drawable.share_more);
        info.type = SharePlatformInfo.TYPE_MORE;
        sPlatformList.add(info);

    }

    public static ArrayList<SharePlatformInfo> getPlatformList(Context context) {
        if (!sIsInited) {
            initPlatformList(context);
            sIsInited = true;
        }

        return (ArrayList<SharePlatformInfo>) sPlatformList.clone();
    }
}
