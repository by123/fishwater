/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingAddress;
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
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.shopping.order.model.OrderAddressInfo;
import com.sjy.ttclub.shopping.order.model.OrderRequest;
import com.sjy.ttclub.shopping.order.widget.OrderAreaPanel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import java.util.ArrayList;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderAddressWindow extends DefaultWindow implements View.OnClickListener, OrderAreaPanel.IOrderAreaPanelListener {

    private final static String TAG = "OrderAddressWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    private EditText mEditName, mEditPhone, mEditAddress;
    private TextView mTextViewArea;
    private AlphaImageView ivNameDel, ivPhoneDel;

    private OrderAreaPanel mAreaPanel;

    private OrderRequest mOrderRequest;

    private LoadingDialog mDialog;

    private OrderAddressInfo mAddressInfo;

    private boolean isInit = true;

    public OrderAddressWindow(Context context, IDefaultWindowCallBacks callBacks, OrderAddressInfo orderAddressInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        mAddressInfo = orderAddressInfo;

        initActionBar();
        initUI();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_address_title));
        View view = View.inflate(mContext, R.layout.order_address_add, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mTextViewArea = (TextView) findViewById(R.id.tv_area);
        mTextViewArea.setOnClickListener(this);
        mEditName = (EditText) findViewById(R.id.et_name);
        mEditPhone = (EditText) findViewById(R.id.et_phone);
        mEditAddress = (EditText) findViewById(R.id.et_address);
        ivNameDel = (AlphaImageView) findViewById(R.id.iv_name_del);
        ivNameDel.setOnClickListener(this);
        ivPhoneDel = (AlphaImageView) findViewById(R.id.iv_phone_del);
        ivPhoneDel.setOnClickListener(this);
        mDialog = new LoadingDialog(mContext);
        mDialog.setMessage(R.string.order_pay_load);

        String receiver = mAddressInfo.receiver;
        String mobile = mAddressInfo.mobile;
        String detailAddress = mAddressInfo.detailAddress;
        if (!StringUtils.isEmpty(receiver)) {
            mEditName.setText(receiver);
        }
        if (!StringUtils.isEmpty(mobile)) {
            mEditPhone.setText(mobile);
        }
        if (!StringUtils.isEmpty(detailAddress)) {
            mEditAddress.setText(detailAddress);
        }
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setItemId(MenuItemIdDef.TITLEBAR_ORDER_FINISH);
        item.setText(ResourceHelper.getString(R.string.order_address_save));
        actionList.add(item);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    private void notifyAddressChanged() {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_ADDRESS_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    public void onSelected(String desc) {
        mTextViewArea.setText(desc);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_ORDER_FINISH) {
            if (StringUtils.isEmpty(mEditName.getText() + "")) {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_address_error1));
                return;
            }
            mAddressInfo.receiver = mEditName.getText() + "";
            if (StringUtils.isEmpty(mEditPhone.getText() + "")) {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_address_error2));
                return;
            }
            if (mEditPhone.getText().length() != 11) {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_address_error3));
                return;
            }
            mAddressInfo.mobile = mEditPhone.getText() + "";
            if (StringUtils.isEmpty(mEditAddress.getText() + "")) {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_address_error4));
                return;
            }
            mAddressInfo.detailAddress = mEditAddress.getText() + "";
            mAddressInfo.cityId = mAreaPanel.getCurrentCityId() + "";
            mAddressInfo.districtId = mAreaPanel.getCurrentDistrictId() + "";
            mAddressInfo.provinceId = mAreaPanel.getCurrentProvinceId() + "";
            addAddress(mAddressInfo);
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            mAreaPanel = new OrderAreaPanel(mContext);
            mAreaPanel.setPanelListener(this);
            mAreaPanel.initArea(mAddressInfo);
        } else if (stateFlag == STATE_ON_DETACH) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }

    @Override
    public void notify(Notification notification) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_name_del) {
            mEditName.setText("");
        } else if (v.getId() == R.id.iv_phone_del) {
            mEditPhone.setText("");
        } else if (v.getId() == R.id.tv_area) {
            mAreaPanel.showPanel();
            if (isInit) {
                isInit = false;
                mAreaPanel.initArea(mAddressInfo);
            }
        }
    }

    /*******************
     * 接口访问
     ********************/
    private void addAddress(OrderAddressInfo addressInfo) {
        mDialog.show();
        mOrderRequest = OrderRequest.getInstance();
        mOrderRequest.addAddress(addressInfo, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mDialog.dismiss();
                JTBShoppingAddress address = (JTBShoppingAddress) obj;
                if (address.getData().getAddrId() > 0) {
                    notifyAddressChanged();
                }
                mCallBacks.onWindowExitEvent(mWindow, true);
            }
        });
    }
}
