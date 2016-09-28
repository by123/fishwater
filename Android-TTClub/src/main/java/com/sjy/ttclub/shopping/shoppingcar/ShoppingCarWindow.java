package com.sjy.ttclub.shopping.shoppingcar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsState;
import com.sjy.ttclub.bean.shop.ShoppingCarParamBean;
import com.sjy.ttclub.community.CommunityConstant;
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
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.shopping.order.model.OrderBalanceInfo;
import com.sjy.ttclub.shopping.shoppingcar.adapter.ShoppingCarItemAdapter;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.PriceView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class ShoppingCarWindow extends DefaultWindow implements View.OnClickListener {
    private LoadingLayout mLoadingLayout;
    private ShoppingCarRequest mShoppingCarRequest;
    private ListView mGoodsListView;
    private ShoppingCarItemAdapter mShoppingCarItemAdapter;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans = new ArrayList<>();
    private CheckBox mCheckBoxAll;
    private PriceView mGoodsCostAll;
    private TextView mOperationBtn;
    private LinearLayout mShoppingCarBottomArea;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsChangedBeans;
    private boolean mIsEditMode;
    private boolean mIsEditModeIsClick = true;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsInvalidBeans = new ArrayList<>();
    private TextView mInvalidGoods;
    private LinearLayout mInvalidGoodsArea;
    private View mFootView;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    public ShoppingCarWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setLaunchMode(LAUNCH_MODE_SINGLE_INSTANCE);
        setTitle(R.string.shopping_car_title);
        mShoppingCarRequest = new ShoppingCarRequest();
        initActionBar();
        initUI();
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            sendGetShoppingCarListRequest();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shopping_car_invalid:
                Message message = Message.obtain();
                message.obj = mShoppingCarGoodsInvalidBeans;
                message.what = MsgDef.MSG_SHOPPING_CAR_INVALID_WINDOW_OPEN;
                MsgDispatcher.getInstance().sendMessage(message);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        } else if (stateFlag == STATE_BEFORE_POP_OUT) {
            ShoppingCarState.isSelected.clear();
            mShoppingCarGoodsBeans.clear();

        } else if (stateFlag == STATE_AFTER_PUSH_IN) {
            sendGetShoppingCarListRequest();
        } else if (stateFlag == STATE_ON_SHOW) {
            mShoppingCarGoodsInvalidBeans.clear();
            sendGetShoppingCarListRequest();
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_EDIT) {
            if (mIsEditModeIsClick) {
                handleEditClick();
            }
        }
    }

    @Override
    public boolean onWindowBackKeyEvent() {
        boolean editState = isInEditMode();
        if (editState) {
            handleEditClick();
            return true;
        }
        return super.onWindowBackKeyEvent();
    }

    public void handleEditClick() {
        mIsEditMode = isInEditMode();
        if (mIsEditMode) {
            exitEditState();
            mOperationBtn.setCompoundDrawables(null, null, null, null);
            mOperationBtn.setText(ResourceHelper.getString(R.string.shopping_car_goods_operation_account) + "(" + mShoppingCarGoodsBeans.size() + ")");
            mShoppingCarItemAdapter.setPageState(ShoppingCarConstant.GOODSPAGE);
            mShoppingCarItemAdapter.notifyDataSetChanged();
            if (mShoppingCarGoodsChangedBeans != null) {
                sendShoppingCarUpdateRequest(mShoppingCarGoodsChangedBeans);
            }
        } else {
            enterEditState();
            Drawable drawable = getResources().getDrawable(R.drawable.shopping_car_icon_delete);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mOperationBtn.setCompoundDrawables(drawable, null, null, null);
            mOperationBtn.setText(ResourceHelper.getString(R.string.shopping_car_goods_operation_delete) + "(" + mShoppingCarGoodsBeans.size() + ")");
            mShoppingCarItemAdapter.setPageState(ShoppingCarConstant.OPERATIONPAGE);
            mShoppingCarItemAdapter.notifyDataSetChanged();
        }
        mIsEditMode = isInEditMode();
        String text = ResourceHelper.getString(R.string.collect_edit);
        if (mIsEditMode) {
            text = ResourceHelper.getString(R.string.collect_edit_finish);
        }
        TitleBarActionItem item = getTitleBarInner().getActionBar().getItem(MenuItemIdDef.TITLEBAR_EDIT);
        item.setText(text);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.collect_edit));
        item.setItemId(MenuItemIdDef.TITLEBAR_EDIT);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    public void initUI() {
        View view = View.inflate(getContext(), R.layout.shopping_car_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        mGoodsListView = (ListView) findViewById(R.id.lv_shopping_car);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);
        mLoadingLayout.setDefaultLoading();
        mCheckBoxAll = (CheckBox) findViewById(R.id.cb_shopping_car_all);
        mCheckBoxAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mCheckBoxAll.isChecked();
                double sumCost = 0;
                for (int i = 0; i < mShoppingCarGoodsBeans.size(); i++) {
                    ShoppingCarState.isSelected.put(i, isChecked);
                    sumCost += Double.valueOf(mShoppingCarGoodsBeans.get(i).getSalePrice());
                }
                if (isChecked) {
                    mGoodsCostAll.setText(mDecimalFormat.format(sumCost));
                } else {
                    mGoodsCostAll.setText("0.00");
                }
                mShoppingCarItemAdapter.notifyDataSetChanged();
            }
        });
        mGoodsCostAll = (PriceView) findViewById(R.id.tv_shopping_car_cost);
        mOperationBtn = (TextView) findViewById(R.id.tv_shopping_car_operation);
        mOperationBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditMode) {
                    //删除购物车操作
                    if (mShoppingCarGoodsBeans.size() > 0) {
                        mIsEditModeIsClick = false;
                        sendShoppingCarDeleteRequest(mShoppingCarGoodsBeans);
                    }
                    mShoppingCarGoodsBeans.clear();
                    mShoppingCarGoodsInvalidBeans.clear();
                } else {
                    if (AccountManager.getInstance().isLogin()) {
                        Message message = Message.obtain();
                        message.what = MsgDef.MSG_SHOW_ORDER_WINDOW;
                        message.obj = encapsulation();
                        MsgDispatcher.getInstance().sendMessage(message);
                    } else {
                        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
                    }
                }
            }
        });
        mShoppingCarBottomArea = (LinearLayout) findViewById(R.id.ll_shopping_car_bottom);
        mShoppingCarBottomArea.setVisibility(GONE);
        mShoppingCarItemAdapter = new ShoppingCarItemAdapter(getContext(), mShoppingCarGoodsBeans);
        mShoppingCarItemAdapter.setOnGoodsCostPristListener(new ShoppingCarItemAdapter.OnGoodsCostPriseListener() {
            @Override
            public void onChange() {
                double sumCost = 0;
                boolean isCkAll = true;
                int checkCount = 0;
                for (int i = 0; i < mShoppingCarGoodsBeans.size(); i++) {
                    boolean ckState = ShoppingCarState.isSelected.get(i);
                    if (ckState) {
                        sumCost += Double.valueOf(mShoppingCarGoodsBeans.get(i).getSalePrice());
                        checkCount++;
                    } else {
                        isCkAll = false;
                    }
                }
                mCheckBoxAll.setChecked(isCkAll);
                mGoodsCostAll.setText(mDecimalFormat.format(sumCost));
                changeBtnOperation(checkCount);
                mShoppingCarItemAdapter.notifyDataSetChanged();
            }
        });
        mShoppingCarItemAdapter.setOnGoodsOperationListener(new ShoppingCarItemHolder.OnGoodsOperationListener() {
            @Override
            public void onGoodsOperationChange(List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans) {
                setShoppingCarGoodsChangedBeans(mShoppingCarGoodsBeans);
            }
        });
        mFootView = View.inflate(getContext(), R.layout.shopping_car_invalid_goods_main, null);
        mInvalidGoods = (TextView) mFootView.findViewById(R.id.invalid_goods_title);
        mInvalidGoodsArea = (LinearLayout) mFootView.findViewById(R.id.ll_shopping_car_invalid);
        mInvalidGoodsArea.setOnClickListener(this);
        mGoodsListView.addFooterView(mFootView);
        mGoodsListView.setAdapter(mShoppingCarItemAdapter);
    }

    private OrderBalanceInfo encapsulation() {
        OrderBalanceInfo balanceInfo = new OrderBalanceInfo();
        List<ShoppingCarGoodsBean> checkList = new ArrayList<>();
        for (int i = 0; i < mShoppingCarGoodsBeans.size(); i++) {
            if (ShoppingCarState.isSelected.get(i)) {
                checkList.add(mShoppingCarGoodsBeans.get(i));
            }
        }
        balanceInfo.setList(checkList);
        return balanceInfo;
    }

    private void handleGetDataSuccess(ShoppingCarGoodsState shoppingCarGoodsState) {
        if (shoppingCarGoodsState.getValid() == null && shoppingCarGoodsState.getInvalid() == null) {
//            mIsEditModeIsClick = false;
            mLoadingLayout.setVisibility(VISIBLE);
            mShoppingCarBottomArea.setVisibility(GONE);
            mLoadingLayout.setBgContent(R.drawable.shopping_cart_icon_cart,
                    ResourceHelper.getString(R.string.shopping_car_no_goods_tip), true);
            mLoadingLayout.setBtnText(ResourceHelper.getString(R.string.shopping_car_go_to_see));
            mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
                @Override
                public void OnClick() {
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOPPING_CAR_GO_SHOPPING);
                }
            });
            if (!mIsEditModeIsClick) {
                handleEditClick();
                mIsEditModeIsClick = true;
            }
            mShoppingCarGoodsBeans.clear();
            mShoppingCarItemAdapter.notifyDataSetChanged();
            return;
        }
        mLoadingLayout.setVisibility(GONE);
        if (shoppingCarGoodsState.getValid() != null) {
            mShoppingCarBottomArea.setVisibility(VISIBLE);
            mShoppingCarGoodsBeans.clear();
            mShoppingCarGoodsBeans.addAll(shoppingCarGoodsState.getValid());
            double goodsCostAll = 0;
            for (ShoppingCarGoodsBean s : mShoppingCarGoodsBeans) {
                goodsCostAll += Double.valueOf(s.getSalePrice());
            }
            mGoodsCostAll.setText(mDecimalFormat.format(goodsCostAll));
            mCheckBoxAll.setChecked(true);
            changeBtnOperation(mShoppingCarGoodsBeans.size());
            //初始化购物车选中状态
            ShoppingCarState.initCheckBoxState(mShoppingCarGoodsBeans.size());
        }
        if (mShoppingCarGoodsInvalidBeans.size() == 0) {
            mGoodsListView.removeFooterView(mFootView);
        } else {
            mInvalidGoods.setText(mShoppingCarGoodsInvalidBeans.size() + "件商品失效");
        }
        mShoppingCarItemAdapter.notifyDataSetChanged();
    }

    /**
     * 数据异常处理
     *
     * @param errorCode
     */
    private void handlerGetDataFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            if (mShoppingCarGoodsBeans == null) {
                mLoadingLayout.setDefaultNetworkError(true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast
                        .LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            if (mShoppingCarGoodsBeans == null) {
                mLoadingLayout.setDefaultNetworkError(true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error_retry),
                        Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        mShoppingCarBottomArea.setVisibility(GONE);
    }


    private void sendGetShoppingCarListRequest() {
        mShoppingCarRequest.startRequest(new ShoppingCarRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(errorType);
            }

            @Override
            public void onResultSuccess(ShoppingCarGoodsState shoppingCarGoodsData) {
                if (shoppingCarGoodsData.getInvalid() != null) {
                    mShoppingCarGoodsInvalidBeans.addAll(shoppingCarGoodsData.getInvalid());
                }
                handleGetDataSuccess(shoppingCarGoodsData);
            }
        });
    }

    private void sendShoppingCarUpdateRequest(List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans) {
        List<ShoppingCarParamBean> shoppingCarParamBeans = new ArrayList<>();
        for (ShoppingCarGoodsBean s : mShoppingCarGoodsBeans) {
            ShoppingCarParamBean shoppingCarParamBean = new ShoppingCarParamBean();
            shoppingCarParamBean.setId(StringUtils.parseInt(s.getItemId()));
            shoppingCarParamBean.setCount(StringUtils.parseInt(s.getGoodsCount()));
            shoppingCarParamBeans.add(shoppingCarParamBean);
        }
        ShoppingCarRequest shoppingCarRequest = new ShoppingCarRequest(shoppingCarParamBeans);
        shoppingCarRequest.startShoppingCarUpdateRequest(new ShoppingCarRequest.ShoppingCarRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(errorType);
            }

            @Override
            public void onResultSuccess() {

            }
        });
    }

    private void sendShoppingCarDeleteRequest(List<ShoppingCarGoodsBean> shoppingCarGoodsBeans) {
        List<ShoppingCarParamBean> shoppingCarParamBeanList = new ArrayList<>();
        for (int i = 0; i < shoppingCarGoodsBeans.size(); i++) {
            boolean isChecked = ShoppingCarState.isSelected.get(i);
            if (isChecked) {
                ShoppingCarParamBean s = new ShoppingCarParamBean();
                s.setId(StringUtils.parseInt(shoppingCarGoodsBeans.get(i).getItemId()));
                shoppingCarParamBeanList.add(s);
            }
        }
        mShoppingCarRequest = new ShoppingCarRequest(shoppingCarParamBeanList);
        mShoppingCarRequest.startShoppingCarDeleteRequest(new ShoppingCarRequest.ShoppingCarRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(errorType);
            }

            @Override
            public void onResultSuccess() {
                Notification notification = Notification.obtain(NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE);
                NotificationCenter.getInstance().notify(notification);
                sendGetShoppingCarListRequest();
            }
        });
    }

    public void setShoppingCarGoodsChangedBeans(List<ShoppingCarGoodsBean> mShoppingCarGoodsChangedBeans) {
        this.mShoppingCarGoodsChangedBeans = mShoppingCarGoodsChangedBeans;
    }


    private void changeBtnOperation(int count) {
        if (!mIsEditMode) {
            mOperationBtn.setCompoundDrawables(null, null, null, null);
            mOperationBtn.setText(ResourceHelper.getString(R.string.shopping_car_goods_operation_account) + "(" + count + ")");
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.shopping_car_icon_delete);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mOperationBtn.setCompoundDrawables(drawable, null, null, null);
            mOperationBtn.setCompoundDrawablePadding(ResourceHelper.getDimen(R.dimen.space_10));
            mOperationBtn.setText(ResourceHelper.getString(R.string.shopping_car_goods_operation_delete) + "(" + count + ")");
        }
    }

}
