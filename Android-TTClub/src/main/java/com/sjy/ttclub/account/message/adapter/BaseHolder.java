package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.view.View;

/**
 * Created by gangqing on 2015/12/3.
 * Email:denggangqing@ta2she.com
 */
public abstract class BaseHolder<T> {
    private View mRootView;
    public Context mContext;

    public BaseHolder(Context context) {
        mContext = context;
        mRootView = initView();
        mRootView.setTag(this);
    }

    public abstract View initView();

    public View getRootView() {
        return mRootView;
    }

    public void setData(int position, T data) {
        refreshUI(position, data);
    }

    public abstract void refreshUI(int position, T data);
}
