/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingAddress;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingDirectBalance;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrderNum;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.pay.alipay.AliPay;
import com.sjy.ttclub.pay.wechat.MicroPay;
import com.sjy.ttclub.shopping.order.adapter.OrderListViewAdapter;
import com.sjy.ttclub.shopping.order.model.OrderBalanceInfo;
import com.sjy.ttclub.shopping.order.model.OrderRequest;
import com.sjy.ttclub.shopping.order.model.OrderTemplateHolder;
import com.sjy.ttclub.shopping.order.widget.OrderDialog;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class OrderWindow extends DefaultWindow implements View.OnClickListener, IDialogOnClickListener, LoadingLayout.OnBtnClickListener {

    private final static String TAG = "OrderWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    public List<OrderTemplateHolder> mTemplateHolder = new ArrayList<>();
    private OrderListViewAdapter mAdapter;
    private OrderListView mListView;

    public TextView mTvPay;
    private AlphaTextView mBtnCommit;
    private ImageView mImageViewZFB, mImageViewWX;
    private EditText mEditText;

    private OrderBalanceInfo mBalanceInfo;

    private OrderDialog mOrderDialog;

    private OrderRequest mOrderRequest;

    private LoadingDialog mDialog;
    private LoadingLayout mLoadingLayout;

    public OrderWindow(Context context, IDefaultWindowCallBacks callBacks, OrderBalanceInfo orderBalanceInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        mBalanceInfo = orderBalanceInfo;

        initUI();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_main));
        View view = View.inflate(mContext, R.layout.order_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoadingLayout = (LoadingLayout) findViewById(R.id.order_main_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mListView = (OrderListView) findViewById(R.id.order_main_list);
        mAdapter = new OrderListViewAdapter(mContext, mTemplateHolder);
        mListView.setAdapter(mAdapter);

        mTvPay = (TextView) findViewById(R.id.order_main_amount_payable);
        mBtnCommit = (AlphaTextView) findViewById(R.id.order_main_commit);
        mBtnCommit.setOnClickListener(this);

        mOrderDialog = new OrderDialog(mContext, createDialog());
        mOrderDialog.setOnClickListener(this);
        mBalanceInfo.payType = mOrderDialog.ALIPAY;

        mDialog = new LoadingDialog(mContext);
        mDialog.setMessage(R.string.order_pay_init);

        mEditText = (EditText) findViewById(R.id.order_main_message);
    }

    private void loadData() {
        mAdapter.resetSum();
        mTemplateHolder.clear();
        mOrderRequest = OrderRequest.getInstance();
        getAddress(mBalanceInfo);
    }

    public void update() {
        mAdapter.notifyDataSetChanged();
        mListView.resetListViewHeight();
    }

    private View createDialog() {
        View view = View.inflate(mContext, R.layout.order_dialog_pay, null);
        TextView tvZFB = (TextView) view.findViewById(R.id.tv_zfb);
        tvZFB.setOnClickListener(this);
        TextView tvWX = (TextView) view.findViewById(R.id.tv_wx);
        tvWX.setOnClickListener(this);
        mImageViewZFB = (ImageView) view.findViewById(R.id.iv_zfb);
        mImageViewZFB.setSelected(true);
        mImageViewWX = (ImageView) view.findViewById(R.id.iv_wx);
        mImageViewWX.setSelected(false);
        return view;
    }

    private void changePayType(int type) {
        mOrderDialog.mPayType = type;
        mImageViewWX.setSelected(type == mOrderDialog.WECHAT);
        mImageViewZFB.setSelected(type == mOrderDialog.ALIPAY);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            NotificationCenter.getInstance().register(this, NotificationDef.N_ORDER_MAIN_RELOAD);
            NotificationCenter.getInstance().register(this, NotificationDef.N_ORDER_ADDRESS_CHANGED);
            loadData();
        } else if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ORDER_MAIN_RELOAD);
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ORDER_ADDRESS_CHANGED);
            DeviceManager.getInstance().hideInputMethod();
        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ORDER_MAIN_RELOAD || notification.id == NotificationDef.N_ORDER_ADDRESS_CHANGED) {
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.order_main_commit) {
            if (mBalanceInfo.addrId == 0) {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_no_address_msg));
                return;
            }
            mOrderDialog.show();
        } else if (v.getId() == R.id.tv_zfb) {
            changePayType(mOrderDialog.ALIPAY);
            mBalanceInfo.payType = mOrderDialog.ALIPAY;/**设置支付类型**/
        } else if (v.getId() == R.id.tv_wx) {
            changePayType(mOrderDialog.WECHAT);
            mBalanceInfo.payType = mOrderDialog.WECHAT;/**设置支付类型**/
        }
    }

    @Override
    public void OnClick() {
        loadData();
    }

    @Override
    public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
        if (viewId == GenericDialog.ID_BUTTON_NO) {
            dialog.dismiss();
        } else if (viewId == GenericDialog.ID_BUTTON_YES) {

            mBalanceInfo.userRemark = mEditText.getText() + "";/**设置用户留言**/

            //统计PRODUCT_PURCHASE
            StatsModel.stats(StatsKeyDef.PRODUCT_PURCHASE);

            if (isFromGoods(mBalanceInfo)) {
                postOrderByGoods(mBalanceInfo);
            } else {
                postOrderByCart(mBalanceInfo);
            }
        }
        return false;
    }

    /*******************
     * 接口访问
     ********************/
    private void getAddress(final OrderBalanceInfo balanceInfo) {
        mOrderRequest.getAddress(new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mLoadingLayout.hide();

                JTBShoppingAddress address = (JTBShoppingAddress) obj;
                addReceiverItems(address);

                mBalanceInfo.addrId = address.getData().getAddrId();/**设置地址ID**/

                getGoods(balanceInfo);
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                if (code == HttpCode.FAIL_CODE) {
                    addReceiverItems(null);
                    getGoods(balanceInfo);
                } else {
                    netWorkError(code);
                }
            }
        });
    }

    /**
     * 封装收货人信息
     *
     * @param _address
     */
    private void addReceiverItems(JTBShoppingAddress _address) {
        List<JTBShoppingAddress> list = new ArrayList<>();
        JTBShoppingAddress address;
        if (_address != null) {
            address = _address;
        } else {
            address = new JTBShoppingAddress();
        }
        list.add(address);

        OrderTemplateHolder templateHolder = new OrderTemplateHolder();
        templateHolder.setTempType(OrderListViewAdapter.ITEM_TYPE_TOP);
        templateHolder.setItems(list);
        mTemplateHolder.add(templateHolder);
    }

    private void getGoods(OrderBalanceInfo balanceInfo) {
        for (ShoppingCarGoodsBean goods : balanceInfo.getList()) {
            List<ShoppingCarGoodsBean> list = new ArrayList<>();
            list.add(goods);

            OrderTemplateHolder templateHolder = new OrderTemplateHolder();//订单商品信息
            templateHolder.setTempType(OrderListViewAdapter.ITEM_TYPE_GOODS);
            templateHolder.setItems(list);
            mTemplateHolder.add(templateHolder);
        }

        if (isFromGoods(balanceInfo)) {
            getDirectBalance(balanceInfo);
        } else {
            getBalance(balanceInfo);
        }
    }

    /**
     * 是否商品直接提交
     *
     * @param balanceInfo
     * @return
     */
    private boolean isFromGoods(OrderBalanceInfo balanceInfo) {
        ShoppingCarGoodsBean carGoodsBean = balanceInfo.getList().get(0);
        if (StringUtils.isEmpty(carGoodsBean.getItemId())) {
            return true;
        } else {
            return false;
        }
    }

    private void getDirectBalance(OrderBalanceInfo balanceInfo) {
        mOrderRequest.postBalanceByGoods(balanceInfo, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                common((JTBShoppingDirectBalance) obj);
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                netWorkError(code);
            }
        });
    }

    private void getBalance(OrderBalanceInfo balanceInfo) {
        mOrderRequest.postBalanceByCart(balanceInfo, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                common((JTBShoppingDirectBalance) obj);
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                netWorkError(code);
            }
        });
    }

    private void common(JTBShoppingDirectBalance balance) {
        mLoadingLayout.hide();
        List<JTBShoppingDirectBalance> list = new ArrayList<>();
        list.add(balance);

        OrderTemplateHolder templateHolder = new OrderTemplateHolder();//统计信息
        templateHolder.setTempType(OrderListViewAdapter.ITEM_TYPE_STATS);
        templateHolder.setItems(list);
        mTemplateHolder.add(templateHolder);

        String totalPrice = balance.getData().getTotalPrice();

        mTvPay.setText(Html.fromHtml(String.format(ResourceHelper.getString(R.string.order_amount_payable), totalPrice)));

        mBalanceInfo.totalPrice = totalPrice;/**设置支付金额**/
        mBalanceInfo.goodsPrice = balance.getData().getGoodsPrice();/**设置商品单个金额**/

        update();
    }

    public void postOrderByCart(OrderBalanceInfo balanceInfo) {
        mDialog.show();
        mOrderRequest.postOrderByCart(balanceInfo, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mDialog.dismiss();
                commonPay((JTBShoppingOrderNum) obj);
            }

            @Override
            public void onError(String errorStr, int code) {
                mDialog.dismiss();
                error(code);
            }
        });
    }

    /**
     * 从商品直接下单
     */
    public void postOrderByGoods(OrderBalanceInfo balanceInfo) {
        mDialog.show();
        mOrderRequest.postOrderByGoods(balanceInfo, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mDialog.dismiss();
                commonPay((JTBShoppingOrderNum) obj);
            }

            @Override
            public void onError(String errorStr, int code) {
                mDialog.dismiss();
                error(code);
            }
        });
    }

    private void error(int code) {
        if (code == HttpCode.ERROR_CODE_604) {
            ToastHelper.showToast(mContext, R.string.order_post_error_604);
        } else if (code == HttpCode.ERROR_CODE_605) {
            ToastHelper.showToast(mContext, R.string.order_post_error_605);
        } else if (code == HttpCode.ERROR_CODE_606) {
            ToastHelper.showToast(mContext, R.string.order_post_error_606);
        } else if (code == HttpCode.ERROR_CODE_607) {
            ToastHelper.showToast(mContext, R.string.order_post_error_607);
        } else if (code == HttpCode.ERROR_CODE_608) {
            ToastHelper.showToast(mContext, R.string.order_post_error_608);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }

    /**
     * 第三方支付跳转
     *
     * @param orderNum
     */
    private void commonPay(JTBShoppingOrderNum orderNum) {

        CommonConst.ORDERID = orderNum.getData().getOrderId();
        CommonConst.ORDERNUM = orderNum.getData().getOrderNo();
        CommonConst.TOTALPRICE = Double.parseDouble(mBalanceInfo.totalPrice);

        if (mOrderDialog.mPayType == mOrderDialog.WECHAT) {
            MicroPay microPay = new MicroPay(mContext);
            microPay.sendPayReq();
        } else if (mOrderDialog.mPayType == mOrderDialog.ALIPAY) {
            AliPay aliPay = new AliPay(mContext);
            aliPay.pay();
        }

        mCallBacks.onWindowExitEvent(mWindow, true);
    }

    private void netWorkError(int code) {
        if (mTemplateHolder.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }
}
