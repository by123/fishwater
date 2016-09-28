package com.sjy.ttclub.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.thirdparty.ThirdpartyManager;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.util.file.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by linhz on 2015/11/12.
 * Email: linhaizhong@ta2she.com
 */
public class ShareSender {
    public static final String DEFAULT_URL = "http://www.ta2she.com";
    private static final String APP_ICON_NAME = "app_icon.png";
    private static ShareSender sInstance;
    private Context mContext;

    private String mDefaultImagePath = null;

    private ShareSender(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        sInstance = new ShareSender(context);
    }

    public static ShareSender getInstance() {
        return sInstance;
    }

    public String getDefaultImageUrl() {
        return mDefaultImagePath;
    }

    private void tryCopyAppIcon() {
        if (mDefaultImagePath != null) {
            if (FileUtils.isFileExists(mDefaultImagePath)) {
                return;
            }
        }

        String folderPath = PathManager.getInstance().getExtCachePath();
        if (StringUtils.isEmpty(folderPath)) {
            folderPath = PathManager.getInstance().getExtDataPath();
        }
        if (StringUtils.isEmpty(folderPath)) {
            return;
        }
        if (!folderPath.endsWith(File.separator)) {
            folderPath = folderPath + File.separator;
        }
        String fileName = folderPath + APP_ICON_NAME;
        Bitmap icon = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.app_icon)).getBitmap();
        boolean success = false;
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean mkSucc = folder.mkdirs();
            }

            File file = new File(fileName);
            FileOutputStream out = new FileOutputStream(file);
            if (icon.compress(Bitmap.CompressFormat.PNG, 70, out)) {
                out.flush();
                success = true;
                IOUtil.safeClose(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            mDefaultImagePath = fileName;
        }
    }

    public void sendShare(String platformId, Intent shareIntent) {
        tryCopyAppIcon();
        Bundle statsBundle = ShareIntentBuilder.parseStatsBundle(shareIntent);
        statsShare(statsBundle, platformId);
        if (SharePlatformInfo.ID_QQ.equals(platformId)) {
            ThirdpartyManager.getInstance().share2QQ(shareIntent);
            StatsModel.stats(StatsKeyDef.SHARE_QQ);
        } else if (SharePlatformInfo.ID_QZONE.equals(platformId)) {
            ThirdpartyManager.getInstance().share2Qzone(shareIntent);
            StatsModel.stats(StatsKeyDef.SHARE_QZONE);
        } else if (SharePlatformInfo.ID_WECHAT_FRIENDS.equals(platformId)) {
            ThirdpartyManager.getInstance().share2WechatFriends(shareIntent);
            StatsModel.stats(StatsKeyDef.SHARE_WECHAT_FRIENDS);
        } else if (SharePlatformInfo.ID_WECHAT_TIMELINE.equals(platformId)) {
            ThirdpartyManager.getInstance().share2WechatTimeline(shareIntent);
            StatsModel.stats(StatsKeyDef.SHARE_WECHAT_TIMELINE);
        } else if (SharePlatformInfo.ID_WEIBO.equals(platformId)) {
            ThirdpartyManager.getInstance().share2Sina(shareIntent);
            StatsModel.stats(StatsKeyDef.SHARE_WEIBO);
        }
    }

    private void statsShare(Bundle bundle, String platformId) {
        String statsKey = StatsKeyDef.SHARE;
        if (bundle == null || bundle.isEmpty()) {
            StatsModel.stats(StatsKeyDef.SHARE, StatsKeyDef.SHARE, statsKey);
        } else {
            Iterator<String> it = bundle.keySet().iterator();
            HashMap<String, String> map = new HashMap<>();
            String key;
            Object value;
            while (it.hasNext()) {
                key = it.next();
                value = bundle.get(key);
                map.put(key, String.valueOf(value));
            }
            if (!map.isEmpty()) {
                StatsModel.stats(StatsKeyDef.SHARE, map);
            }
        }

    }

    public void send2LocalApp(Intent appIntent, Intent shareIntent) {
        if (shareIntent == null || appIntent == null) {
            return;
        }
        Bundle statsBundle = ShareIntentBuilder.parseStatsBundle(shareIntent);
        statsShare(statsBundle, SharePlatformInfo.ID_MORE);
        shareIntent = ShareIntentBuilder.createStandardShareIntent(shareIntent);
        ComponentName cp = appIntent.getComponent();
        shareIntent.setComponent(cp);
        try {
            mContext.startActivity(shareIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDefaultShareTitle() {
        return mContext.getResources().getString(R.string.share_default_title);
    }
}
