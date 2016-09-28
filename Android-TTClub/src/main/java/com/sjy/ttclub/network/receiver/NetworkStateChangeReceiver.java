package com.sjy.ttclub.network.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ThreadManager;

/**
 * Created by linhz on 2015/11/26.
 * Email: linhaizhong@ta2she.com
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

                final NetworkUtil.NetworkArgs args = new NetworkUtil.NetworkArgs();
                ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {
                    @Override
                    public void run() {
                        args.mActiveNetworkInfo = NetworkUtil.getActiveNetworkInfo();
                        args.mIsConnected = NetworkUtil.isNetworkConnected();
                        args.mCurrAccessPointType = NetworkUtil.getCurrAccessPointType();
                        args.mIsMobileNetwork = NetworkUtil.isMobileNetwork();
                        args.mIsWifi = NetworkUtil.isWifiNetwork();
                        args.mAccessPointName = NetworkUtil.getAccessPointName();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        NetworkUtil.setNetworkArgs(args);
                        notifyNetworkStateChange();
                        NetworkUtil.setNetworkArgs(null);
                    }
                });
            }
        }
    }

    private static void notifyNetworkStateChange() {
        boolean isConnected = NetworkUtil.isNetworkConnected();
        int type = NetworkUtil.getCurrAccessPointType();
        NetworkState networkState = new NetworkState(type, isConnected);
        Notification notification = new Notification(NotificationDef.N_NETWORK_STATE_CHANGE, networkState);
        NotificationCenter.getInstance().notify(notification);

    }


}
