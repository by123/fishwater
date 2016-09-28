package com.sjy.ttclub.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.framework.ActivityResult;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public class ThirdpartyManager implements INotify {

    private static ThirdpartyManager sInstance;
    private Activity mActivity;
    private Context mContext;

    private WechatHandler mWechatHandler;
    private QQHandler mQQHandler;
    private SinaHandler mSinaHandler;

    private ThirdpartyManager(Context context) {
        mContext = context;
        mWechatHandler = new WechatHandler();
        mQQHandler = new QQHandler();
        mSinaHandler = new SinaHandler();
    }

    public static void init(Context context) {
        sInstance = new ThirdpartyManager(context);
    }

    public static ThirdpartyManager getInstance() {
        return sInstance;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        mWechatHandler.setActivity(activity);
        mQQHandler.setActivity(activity);
        mSinaHandler.setActivity(activity);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACTIVITY_RESULT);
    }

    public WechatHandler getWechatHandler() {
        return mWechatHandler;
    }

    public SinaHandler getSinaHandler() {
        return mSinaHandler;
    }

    public void login(LoginMedia loginMedia, final ThirdpartyLoginCallback callback) {
        if (callback == null) {
            return;
        }
        if (loginMedia == LoginMedia.QQ) {
            startQQLogin(callback);
        } else if (loginMedia == LoginMedia.SINA) {
            startSinaLogin(callback);
        } else if (loginMedia == LoginMedia.WECHAT) {
            startWechatLogin(callback);
        }
    }

    private void startWechatLogin(final ThirdpartyLoginCallback callback) {
        mWechatHandler.startLogin(callback);
    }

    private void startQQLogin(final ThirdpartyLoginCallback callback) {
        mQQHandler.startLogin(callback);
    }

    private void startSinaLogin(final ThirdpartyLoginCallback callback) {
        mSinaHandler.startLogin(callback);
    }


    public void share2Sina(Intent shareIntent) {
        mSinaHandler.startShare(shareIntent);
    }

    public void share2QQ(Intent shareIntent) {
        mQQHandler.startShare(shareIntent);
    }

    public void share2Qzone(Intent shareIntent) {
        mQQHandler.startShare2Qzone(shareIntent);
    }

    public void share2WechatTimeline(Intent shareIntent) {
        mWechatHandler.share2WechatTimeline(shareIntent);
    }

    public void share2WechatFriends(Intent shareIntent) {
        mWechatHandler.share2WechatFriends(shareIntent);
    }

    public void clearTokens() {
        SinaTokenKeeper.clear(mContext);
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ACTIVITY_RESULT) {
            ActivityResult result = (ActivityResult) notification.extObj;
            mQQHandler.onActivityResult(result.requestCode, result.resultCode, result.data);
            mSinaHandler.onActivityResult(result.requestCode, result.resultCode, result.data);
        }
    }

}
