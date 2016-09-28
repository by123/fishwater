/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.app.Application;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.community.CommunityTempDataHelper;
import com.sjy.ttclub.fresco.FrescoHelper;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.push.PushManager;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.system.MarketChannelManager;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.thirdparty.ThirdpartyManager;
import com.sjy.ttclub.umeng.UmengManager;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.file.StorageHelper;

/**
 * Created by linhz on 2015/11/8.
 * Email: linhaizhong@ta2she.com
 */
public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SettingFlags.init(getApplicationContext());
        ContextManager.setAppContext(getApplicationContext());
        AppEnv.setApplicationContext(getApplicationContext());
        HardwareUtil.initialize(getApplicationContext());
        ResourceHelper.init(getApplicationContext());
        MarketChannelManager.init(getApplicationContext());
        SystemHelper.init(getApplicationContext());
        AppMd5Helper.init(getApplicationContext());

        ThirdpartyManager.init(getApplicationContext());
        AccountManager.init(getApplicationContext());

        UmengManager.init(getApplicationContext());
        StatsModel.init(getApplicationContext());
        PushManager.init(getApplicationContext());
        /**
         * Fresoc 初始化
         */
        StorageHelper.init(getApplicationContext());
        PathManager.init(getApplicationContext());
        FrescoHelper.initFresco(getApplicationContext());
        /**
         * XUtils 初始化
         */
        HttpManager.init(this);
        /**
         * 社区
         */
        CommunityTempDataHelper.init(getApplicationContext());
    }

}
