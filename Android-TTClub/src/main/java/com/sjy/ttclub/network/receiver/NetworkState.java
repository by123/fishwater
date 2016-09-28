package com.sjy.ttclub.network.receiver;

/**
 * Created by linhz on 2015/11/26.
 * Email: linhaizhong@ta2she.com
 */
public class NetworkState {
    /**
     * network state change type
     */
    public int type;

    public boolean isConnected;

    /**/ NetworkState(int type, boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }

}
