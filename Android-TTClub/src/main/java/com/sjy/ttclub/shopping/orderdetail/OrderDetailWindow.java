/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.orderdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.MessageChatDetailsRequestHelper;
import com.sjy.ttclub.account.order.model.OrderReviewInfo;
import com.sjy.ttclub.bean.account.DialogIDBean;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.bean.shop.ShoppingOrderDetailGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderLogisticsBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingOrderDetail;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.pay.alipay.AliPay;
import com.sjy.ttclub.pay.wechat.MicroPay;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;
import com.sjy.ttclub.shopping.order.widget.OrderDialog;
import com.sjy.ttclub.shopping.orderdetail.adapter.OrderDetailRecyclerViewAdapter;
import com.sjy.ttclub.shopping.orderdetail.model.OrderDetailRequest;
import com.sjy.ttclub.shopping.orderdetail.model.TemplateHolder;
import com.sjy.ttclub.shopping.orderdetail.widget.OrderOperationDialog;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
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
public class OrderDetailWindow extends DefaultWindow implements View.OnClickListener, IDialogOnClickListener, LoadingLayout.OnBtnClickListener, MessageChatDetailsRequestHelper.DialogIdCallBackSuccess, ActionSheetPanel.OnActionSheetClickListener {

    private final static String TAG = "OrderDetailWindow";

    private Activity mContext;
    private AbstractWindow mWindow;

    private RecyclerView mRecyclerView;
    private OrderDetailRecyclerViewAdapter mAdapter;
    private List<TemplateHolder> mList = new ArrayList<>();

    private OrderDialog mOrderDialog;
    private ImageView mImageViewZFB, mImageViewWX;

    private AlphaTextView mAtvCancel, mAtvDel, mAtvGet, mAtvPoint, mAtvPay;

    private OrderDetailRequest mOrderDetailRequest;
    private OrderResultInfo mOrderResultInfo;
    private OrderReviewInfo mOrderReviewInfo;

    private LoadingDialog mDialog;
    private OrderOperationDialog mTextDialog;
    private LoadingLayout mLoadingLayout;

    private ActionSheetPanel mPanel;
    private final String ItemsPhone = "0";
    private final String ItemsLetter = "1";

    private final int STATE_2 = 2, STATE_3 = 3, STATE_4 = 4, STATE_5 = 5, STATE_6 = 6, STATE_7 = 7, STATE_0 = 0;
    private int mCurrState = STATE_2;
    private String mOrderSumPrice;

    public OrderDetailWindow(Context context, IDefaultWindowCallBacks callBacks, OrderResultInfo resultInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mWindow = this;
        setLaunchMode(LAUNCH_MODE_SINGLE_INSTANCE);

        mOrderResultInfo = resultInfo;
        mOrderReviewInfo = new OrderReviewInfo();

        initUI();
        initActionBar();
    }

    private void initUI() {
        setTitle(ResourceHelper.getString(R.string.order_detail_title));
        View view = View.inflate(mContext, R.layout.order_detail, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoadingLayout = (LoadingLayout) findViewById(R.id.order_detail_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mDialog = new LoadingDialog(mContext);
        mDialog.setMessage(R.string.order_pay_init);
        mTextDialog = new OrderOperationDialog(mContext);
        mTextDialog.setOnClickListener(this);
        mOrderDialog = new OrderDialog(mContext, createDialog());
        mOrderDialog.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.order_recycler_view);
        mAdapter = new OrderDetailRecyclerViewAdapter(mContext, mList);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        mAtvCancel = (AlphaTextView) findViewById(R.id.order_btn_cancel);
        mAtvCancel.setOnClickListener(this);
        mAtvDel = (AlphaTextView) findViewById(R.id.order_btn_delete);
        mAtvDel.setOnClickListener(this);
        mAtvGet = (AlphaTextView) findViewById(R.id.order_btn_get);
        mAtvGet.setOnClickListener(this);
        mAtvPoint = (AlphaTextView) findViewById(R.id.order_btn_review);
        mAtvPoint.setOnClickListener(this);
        mAtvPay = (AlphaTextView) findViewById(R.id.order_btn_pay);
        mAtvPay.setOnClickListener(this);

        mPanel = new ActionSheetPanel(mContext);
        ActionSheetPanel.ActionSheetItem itemsLetter = new ActionSheetPanel.ActionSheetItem();
        itemsLetter.id = ItemsLetter;
        itemsLetter.title = ResourceHelper.getString(R.string.shopping_product_bottom_secretary);
        mPanel.addSheetItem(itemsLetter);
        ActionSheetPanel.ActionSheetItem itemsPhone = new ActionSheetPanel.ActionSheetItem();
        itemsPhone.id = ItemsPhone;
        itemsPhone.title = ResourceHelper.getString(R.string.order_call_phone);
        mPanel.addSheetItem(itemsPhone);
        mPanel.setSheetItemClickListener(this);

        mOrderDetailRequest = OrderDetailRequest.getInstance();
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_secretary));
        item.setItemId(MenuItemIdDef.TITLEBAR_SHOPPING_SECRETARY);
        actionList.add(item);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
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

    private void update() {
        mAdapter.notifyDataSetChanged();
    }

    private void notifyOrderListReload() {
        Notification notification = Notification.obtain(NotificationDef.N_ORDER_LIST_CHANGED);
        NotificationCenter.getInstance().notify(notification);

        if (mCurrState != STATE_5) {//状态为5时 删除信息  直接关闭 不需要刷新当前页
            getOrderDetail();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            NotificationCenter.getInstance().register(this, NotificationDef.N_ORDER_LIST_CHANGED);
            getOrderDetail();
        } else if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ORDER_LIST_CHANGED);
        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ORDER_LIST_CHANGED && mCurrState != STATE_5) {//状态为5时 删除信息  直接关闭 不需要刷新当前页
            getOrderDetail();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_zfb) {//Dialog 支付宝
            changePayType(mOrderDialog.ALIPAY);
        } else if (v.getId() == R.id.tv_wx) {//Dialog 微支付
            changePayType(mOrderDialog.WECHAT);
        } else if (v.getId() == R.id.order_btn_cancel) {

            //统计PRODUC_ORDER_CANCEL
            StatsModel.stats(StatsKeyDef.PRODUC_ORDER_CANCEL);

            mCurrState = STATE_2;
            mTextDialog.show();
        } else if (v.getId() == R.id.order_btn_delete) {
            mCurrState = STATE_5;
            mTextDialog.show();
        } else if (v.getId() == R.id.order_btn_get) {

            //统计PRODUCT_ORDER_RECEIVE
            StatsModel.stats(StatsKeyDef.PRODUCT_ORDER_RECEIVE);

            mCurrState = STATE_4;
            mTextDialog.show();
        } else if (v.getId() == R.id.order_btn_pay) {
            mCurrState = STATE_0;
            mOrderDialog.show();
        } else if (v.getId() == R.id.order_btn_review) {

            //统计PRODUCT_ORDER_REVIEW
            StatsModel.stats(StatsKeyDef.PRODUCT_ORDER_REVIEW);

            //统计PRODUCT_REVIEWLIST_VIEW  正在进行中的订单  IOS有  ANDROID没有
            StatsModel.stats(StatsKeyDef.PRODUCT_REVIEWLIST_VIEW);

            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_ORDER_REVIEW_LIST_WINDOW;
            message.obj = mOrderReviewInfo;
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }

    @Override
    public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
        if (viewId == GenericDialog.ID_BUTTON_NO) {
            dialog.dismiss();
        } else if (viewId == GenericDialog.ID_BUTTON_YES) {
            if (mCurrState == STATE_2) {
                postOrderState(OrderDetailRequest.ACTION_CANCEL);
            } else if (mCurrState == STATE_4) {
                postOrderState(OrderDetailRequest.ACTION_CONFIRM);
            } else if (mCurrState == STATE_5) {
                postOrderState(OrderDetailRequest.ACTION_DELETE);
            } else if (mCurrState == STATE_0) {
                commonPay();
            } else if (mCurrState == STATE_7) {
            }
        }
        return false;
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHOPPING_SECRETARY) {
            mPanel.showPanel();
        }
    }

    @Override
    public void OnClick() {
        getOrderDetail();
    }

    @Override
    public void onActionSheetItemClick(String id) {
        if (id == ItemsPhone) {
            Uri uri = Uri.parse(ResourceHelper.getString(R.string.account_alert_dialog_uri));
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(uri);
            mContext.startActivity(intent);
        } else if (id == ItemsLetter) {
            MessageChatDetailsRequestHelper helper = new MessageChatDetailsRequestHelper(getContext());
            helper.setDialogIdCallBack(this);
            helper.dialogId("0", "2");
        }
    }

    @Override
    public void onSuccessForDialogId(DialogIDBean bean) {
        ShoppingOrderDetailGoodsBean goodsBean = mOrderReviewInfo.goodsList.get(0);
        if (goodsBean == null) {
            return;
        }
        MessageDialogs dialogs = new MessageDialogs();
        dialogs.setDialogId(bean.getData().dialogId);
        dialogs.setUserId(bean.getData().toUserId);
        dialogs.setUserRoleId(Constant.TA_SHE_SECRETARY);
        LetterChatParamBean letterChatParamBean = new LetterChatParamBean();
        letterChatParamBean.setImageUrl(goodsBean.getThumbUrl());
        letterChatParamBean.setPrice(mOrderSumPrice);
        letterChatParamBean.setSpId(String.valueOf(goodsBean.getGoodsId()));
        letterChatParamBean.setTitle(goodsBean.getTitle());
        letterChatParamBean.setSpId(mOrderResultInfo.orderNum);
        letterChatParamBean.setTime(mOrderResultInfo.createTime);
        letterChatParamBean.setType(12);//订单类
        letterChatParamBean.setLetter(dialogs);
        Message message = Message.obtain();
        message.obj = letterChatParamBean;
        message.what = MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    /*******************
     * 接口访问
     ********************/

    private void postOrderState(String action) {
        mOrderDetailRequest.postOrderState(action, mOrderResultInfo.orderId, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                if (mCurrState == STATE_5) {
                    ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_state_changed_del));
                    mCallBacks.onWindowExitEvent(mWindow, true);
                } else {
                    ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.order_state_changed));
                    getOrderDetail();
                }
                notifyOrderListReload();
            }
        });
    }

    private void btnDisplayByState(int state) {
        mAtvCancel.setVisibility(GONE);
        mAtvDel.setVisibility(GONE);
        mAtvGet.setVisibility(GONE);
        mAtvPay.setVisibility(GONE);
        mAtvPoint.setVisibility(GONE);
        if (state == STATE_2) {

            //统计PRODUCT_ORDER_UNPAY
            StatsModel.stats(StatsKeyDef.PRODUCT_ORDER_UNPAY);

            mAtvCancel.setVisibility(VISIBLE);
            mAtvPay.setVisibility(VISIBLE);
        } else if (state == STATE_4) {

            //统计PRODUCT_ORDER_DELIEVERED
            StatsModel.stats(StatsKeyDef.PRODUCT_ORDER_DELIEVERED);

            mAtvGet.setVisibility(VISIBLE);
        } else if (state == STATE_5) {
            mAtvDel.setVisibility(VISIBLE);
        } else if (state == STATE_7) {

            //统计PRODUCT_ORDER_UNREVIEWED
            StatsModel.stats(StatsKeyDef.PRODUCT_ORDER_UNREVIEWED);

            mAtvPoint.setVisibility(VISIBLE);
        }
    }

    private void getOrderDetail() {

        CommonConst.ORDERID = mOrderResultInfo.orderId;//设置当前订单

        mOrderDetailRequest.getOrderDetail(mOrderResultInfo.orderId, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mLoadingLayout.hide();

                JTBShoppingOrderDetail orderDetail = (JTBShoppingOrderDetail) obj;
                mList.clear();

                mOrderReviewInfo.orderId = StringUtils.parseInt(orderDetail.getData().getOrderId());//点评传参
                mOrderReviewInfo.goodsList = orderDetail.getData().getGoods();
                mOrderResultInfo.createTime = orderDetail.getData().getCreatetime();
                mOrderSumPrice = orderDetail.getData().getTotalPrice();

                btnDisplayByState(orderDetail.getData().getOrderStatus());//按钮状态

                TemplateHolder templateHolderTop = new TemplateHolder();//状态
                templateHolderTop.setTempType(OrderDetailRecyclerViewAdapter.TYPE_TOP);
                templateHolderTop.setItems(orderDetail.getData());
                mList.add(templateHolderTop);

                for (ShoppingOrderLogisticsBean orderLogisticsBean : orderDetail.getData().getLogistics()) {//物流信息
                    TemplateHolder templateHolder = new TemplateHolder();
                    templateHolder.setTempType(OrderDetailRecyclerViewAdapter.TYPE_LOGISTICS);
                    templateHolder.setItems(orderLogisticsBean);
                    mList.add(templateHolder);
                }

                TemplateHolder templateHolderAddress = new TemplateHolder();//收货人信息
                templateHolderAddress.setTempType(OrderDetailRecyclerViewAdapter.TYPE_ADDRESS);
                templateHolderAddress.setItems(orderDetail.getData());
                mList.add(templateHolderAddress);

                if (!StringUtils.isEmpty(orderDetail.getData().getUserRemark())) {
                    TemplateHolder templateHolderMessage = new TemplateHolder();//买家留言
                    templateHolderMessage.setTempType(OrderDetailRecyclerViewAdapter.TYPE_MESSAGE);
                    templateHolderMessage.setItems(orderDetail.getData());
                    mList.add(templateHolderMessage);
                }

                for (ShoppingOrderDetailGoodsBean shoppingGoodsBean : orderDetail.getData().getGoods()) {//订单商品
                    TemplateHolder templateHolder = new TemplateHolder();
                    templateHolder.setTempType(OrderDetailRecyclerViewAdapter.TYPE_GOODS);
                    templateHolder.setItems(shoppingGoodsBean);
                    mList.add(templateHolder);
                }

                TemplateHolder templateHolderBottom = new TemplateHolder();//付款信息
                templateHolderBottom.setTempType(OrderDetailRecyclerViewAdapter.TYPE_BOTTOM);
                templateHolderBottom.setItems(orderDetail.getData());
                mList.add(templateHolderBottom);

                TemplateHolder templateHolderOrder = new TemplateHolder();//订单信息
                templateHolderOrder.setTempType(OrderDetailRecyclerViewAdapter.TYPE_ORDER);
                templateHolderOrder.setItems(orderDetail.getData());
                mList.add(templateHolderOrder);

                update();
            }

            @Override
            public void onError(String errorStr, int code) {
                netWorkError(code);
            }
        });
    }

    /**
     * 第三方支付跳转
     */
    private void commonPay() {
        CommonConst.ORDERNUM = mOrderResultInfo.orderNum;
        CommonConst.TOTALPRICE = Double.parseDouble(mOrderResultInfo.accountsPayable);

        if (mOrderDialog.mPayType == mOrderDialog.WECHAT) {
            MicroPay microPay = new MicroPay(mContext);
            microPay.sendPayReq();
        } else if (mOrderDialog.mPayType == mOrderDialog.ALIPAY) {
            AliPay aliPay = new AliPay(mContext);
            aliPay.pay();
        }
    }

    private void netWorkError(int code) {
        if (mList.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }
}
