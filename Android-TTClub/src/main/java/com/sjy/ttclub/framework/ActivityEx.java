/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.adapter.ControllerInitMananger;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.thirdparty.ThirdpartyManager;
import com.sjy.ttclub.umeng.UmengManager;


public class ActivityEx extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置复制黏贴窗口顶部悬浮
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

        ContextManager.setContext(this);
        AppEnv.init(this);
        ControllerInitMananger.initControllers(this);
        AccountManager.getInstance().setActivity(this);
        ThirdpartyManager.getInstance().setActivity(this);
        UmengManager.getInstance().configPlatforms(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatsModel.resume(this);
        AppEnv.getInstance().setForeground(true);
        notifyForegroundChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatsModel.pause(this);
        AppEnv.getInstance().setForeground(false);
        notifyForegroundChange();
    }

    @Override
    protected void onDestroy() {
        doFinish();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doFinish();
            }
        }, 1000);
    }

    private boolean mFinished = false;

    private void doFinish() {
        if (mFinished) {
            return;
        }
        mFinished = true;
        onFinish();
    }

    protected void onFinish() {

    }

    private void notifyForegroundChange() {
        NotificationCenter.getInstance().notify(
                new Notification(NotificationDef.N_FOREGROUND_CHANGE, AppEnv
                        .getInstance().isForeground()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult result = new ActivityResult();
        result.resultCode = resultCode;
        result.requestCode = requestCode;
        result.data = data;

        Notification notification = Notification.obtain(NotificationDef.N_ACTIVITY_RESULT);
        notification.extObj = result;
        NotificationCenter.getInstance().notify(notification);
    }

}
