/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account.order;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.order.model.OrderListRequest;
import com.sjy.ttclub.account.order.model.OrderReviewInfo;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaImageView;

import java.util.ArrayList;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderReviewWindow extends DefaultWindow implements View.OnClickListener {

    private final static String TAG = "OrderCommentWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    private OrderListRequest mOrderListRequest;

    private OrderReviewInfo mReviewInfo;

    private SimpleDraweeView sdvImg;
    private RatingBar mRating;
    private EditText mEditContent;
    private AlphaImageView mAnony;

    private boolean mIsAnony = false;

    public OrderReviewWindow(Context context, IDefaultWindowCallBacks callBacks, OrderReviewInfo reviewInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        mReviewInfo = reviewInfo;

        initUI();
        initActionBar();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_btn_review));
        View view = View.inflate(mContext, R.layout.order_review, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        sdvImg = (SimpleDraweeView) findViewById(R.id.order_review_goods_img);
        sdvImg.setAspectRatio(1);
        sdvImg.setImageURI(Uri.parse(mReviewInfo.goodsImg));
        mRating = (RatingBar) findViewById(R.id.order_review_rating);
        mEditContent = (EditText) findViewById(R.id.order_review_content);
        mAnony = (AlphaImageView) findViewById(R.id.order_review_anony);
        mAnony.setOnClickListener(this);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setItemId(MenuItemIdDef.TITLEBAR_ORDER_REVIEW_PUSH);
        item.setText(ResourceHelper.getString(R.string.order_review_push));
        actionList.add(item);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    private void notifyOrderReviewPush(int goodId) {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_REVIEW_PUSH_SUCCESS);
        notification.extObj = goodId;
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
        } else if (stateFlag == STATE_ON_DETACH) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }

    @Override
    public void notify(Notification notification) {
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_ORDER_REVIEW_PUSH) {
            if (mRating.getRating() == 0) {
                ToastHelper.showToast(mContext, R.string.order_review_error4);
                return;
            }
            String content = mEditContent.getText() + "";
            if (StringUtils.isEmpty(content)) {
                ToastHelper.showToast(mContext, R.string.order_review_hint);
                return;
            }
            mReviewInfo.isAnony = mIsAnony ? 1 : 0;
            mReviewInfo.content = content;
            mReviewInfo.rating = mRating.getRating();
            postGoodsReview();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.order_review_anony) {
            mAnony.setSelected(mIsAnony ? false : true);
            mIsAnony = mIsAnony ? false : true;
        }
    }

    /*******************
     * 接口访问
     ********************/
    private void postGoodsReview() {
        mOrderListRequest = OrderListRequest.getInstance();
        mOrderListRequest.postGoodsReview(mReviewInfo, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }

                //统计PRODUCT_REVIEW_POST
                StatsModel.stats(StatsKeyDef.PRODUCT_REVIEW_POST, "spec", "OrderGoodID:" + mReviewInfo.orderGoodsId);

                ToastHelper.showToast(mContext, R.string.order_review_success);
                close();
            }

            @Override
            public void onError(String errorStr, int code) {
                if (code == HttpCode.ERROR_CODE_601) {
                    ToastHelper.showToast(mContext, R.string.order_review_error1);
                } else if (code == HttpCode.ERROR_CODE_602) {
                    ToastHelper.showToast(mContext, R.string.order_review_error2);
                }
                close();
            }
        });
    }

    private void close() {
        notifyOrderReviewPush(mReviewInfo.orderGoodsId);
        mCallBacks.onWindowExitEvent(mWindow, false);
    }
}
