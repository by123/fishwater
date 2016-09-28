/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderSuccessWindow extends DefaultWindow implements View.OnClickListener {

    private final static String TAG = "OrderSuccessWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    private TextView tvSendTime, tvArriveTime, tvCommunicate;

    private Button btnPay, btnDetail;

    private OrderResultInfo mOrderResultInfo;

    public OrderSuccessWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        setLaunchMode(LAUNCH_MODE_SINGLE_INSTANCE);

        initUI();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_main));
        View view = View.inflate(mContext, R.layout.order_success, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        tvSendTime = (TextView) findViewById(R.id.order_success_send_time);
        tvArriveTime = (TextView) findViewById(R.id.order_success_arrive_time);
        tvCommunicate = (TextView) findViewById(R.id.order_success_communicate);

        tvSendTime.setText(Html.fromHtml(String.format(ResourceHelper.getString(R.string.order_success_payable), CommonConst.TOTALPRICE)));
        tvArriveTime.setText(ResourceHelper.getString(R.string.order_success_thank));
        tvCommunicate.setText(ResourceHelper.getString(R.string.order_success_send_time));

        btnPay = (Button) findViewById(R.id.order_pay_pos_text);
        btnPay.setOnClickListener(this);
        btnDetail = (Button) findViewById(R.id.order_detail);
        btnDetail.setOnClickListener(this);

        mOrderResultInfo = new OrderResultInfo();
        mOrderResultInfo.orderId = CommonConst.ORDERID;
        mOrderResultInfo.orderNum = CommonConst.ORDERNUM;
        mOrderResultInfo.accountsPayable = CommonConst.TOTALPRICE + "";

        notifyOrderListReload();
    }

    private void notifyOrderListReload() {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_LIST_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
        } else if (stateFlag == STATE_ON_DETACH) {
        }
    }

    @Override
    public void notify(Notification notification) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.order_pay_pos_text) {
        } else if (v.getId() == R.id.order_detail) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_ORDER_DETAIL_WINDOW;
            message.obj = mOrderResultInfo;
            MsgDispatcher.getInstance().sendMessage(message);
        }
        mCallBacks.onWindowExitEvent(mWindow, true);
    }
}
