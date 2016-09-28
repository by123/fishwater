package com.sjy.ttclub.shopping;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.shop.ShoppingGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingPanicBean;
import com.sjy.ttclub.bean.shop.ShoppingTopicBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCart;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCategory;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingMain;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.UICallBacks;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.shopping.adapter.ShoppingListViewAdapter;
import com.sjy.ttclub.shopping.model.ShoppingRequest;
import com.sjy.ttclub.shopping.model.TemplateHolder;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class ShoppingMainTab extends AbstractWindow implements IMainTabView, PtrHandler, View.OnClickListener, LoadingLayout.OnBtnClickListener {

    private final static String TAG = "ShoppingMainTab";

    private Context mContext;

    private ShoppingRequest mShoppingRequest;

    private TitleBarActionItem mShopCart;

    private ListView mListView;
    public List<TemplateHolder> mTemplateHolderList = new ArrayList<>();
    private ShoppingListViewAdapter mAdapter;

    public PtrClassicFrameLayout mPtrLayout;
    private LoadingLayout mLoadingLayout;

    private boolean mIsInit = false;

    public ShoppingMainTab(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        mContext = context;
        setEnableSwipeGesture(false);
        getBaseLayer().addView(View.inflate(getContext(), R.layout.shopping_main, null), getBaseLayerLP());

        initUI();
    }

    private void initUI() {
        mLoadingLayout = (LoadingLayout) findViewById(R.id.shopping_main_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mPtrLayout = (PtrClassicFrameLayout) findViewById(R.id.shopping_list_ptr);
        final TTRefreshHeader header = new TTRefreshHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPtrLayout.setVisibility(View.VISIBLE);
        mPtrLayout.setLoadingMinTime(1000);
        mPtrLayout.setDurationToCloseHeader(800);
        mPtrLayout.setHeaderView(header);
        mPtrLayout.addPtrUIHandler(header);
        mPtrLayout.setPullToRefresh(false);
        mPtrLayout.isKeepHeaderWhenRefresh();
        mPtrLayout.setPtrHandler(this);

        mListView = (ListView) findViewById(R.id.shopping_main_list);
        mAdapter = new ShoppingListViewAdapter(mContext, mTemplateHolderList);
        mListView.setAdapter(mAdapter);
        View footView = new View(mContext);
        ListView.LayoutParams layoutParams = new ListView.LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper.getDimen(R.dimen.space_40));
        footView.setLayoutParams(layoutParams);
        mListView.addFooterView(footView);

        mShopCart = (TitleBarActionItem) findViewById(R.id.shopping_cart);
        mShopCart.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_cart));
        mShopCart.setOnClickListener(this);

        mShoppingRequest = ShoppingRequest.getInstance();
    }

    public void update() {
        mAdapter.notifyDataSetChanged();
    }

    public void loadData() {
        getShoppingCategory();
    }


    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == IMainTabView.TAB_TO_SHOW && !mIsInit) {
            loadData();
            mIsInit = true;
        }

        if (tabChangedFlag == IMainTabView.TAB_TO_SHOW) {
            mAdapter.onVisibilityChanged(true);
            refreshShopCart();
            StatsModel.stats(StatsKeyDef.WELFARE_TAB);
        } else if (tabChangedFlag == IMainTabView.TAB_TO_HIDE) {
            mAdapter.onVisibilityChanged(false);
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_SHOW) {
            NotificationCenter.getInstance().register(this, NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE);
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE) {
            refreshShopCart();
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        loadData();
        refreshShopCart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shopping_cart) {

            //统计PRODUCT_CART
            StatsModel.stats(StatsKeyDef.PRODUCT_CART, "from", "tab");

            if (AccountManager.getInstance().isLogin()) {
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_SHOPPING_CAR_WINDOW);
            } else {
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            }
        }
    }

    @Override
    public void OnClick() {
        loadData();
    }

    /*******************
     * 接口访问
     ********************/
    private void getShoppingMain() {
        mShoppingRequest.getShoppingMain(new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mPtrLayout.refreshComplete();
                mLoadingLayout.hide();

                JTBShoppingMain shoppingMain = (JTBShoppingMain) obj;
                ShoppingPanicBean panicBean = shoppingMain.getData().getPanicShopping();
                List<ShoppingGoodsBean> goodsList = panicBean.getGoods();

                if (goodsList != null) {
                    TemplateHolder templateHolderTop = new TemplateHolder();//顶部剩余时间
                    List<ShoppingPanicBean> itemsTop = new ArrayList<>();
                    templateHolderTop.setTempType(ShoppingListViewAdapter.ITEM_TYPE_SURPLUS);
                    itemsTop.add(panicBean);
                    templateHolderTop.setItems(itemsTop);
                    mTemplateHolderList.add(templateHolderTop);

                    for (ShoppingGoodsBean shoppingGoodsBean : goodsList) {
                        List<ShoppingGoodsBean> items = new ArrayList<>();//热卖项
                        items.add(shoppingGoodsBean);
                        TemplateHolder templateHolder = new TemplateHolder();
                        templateHolder.setTempType(ShoppingListViewAdapter.ITEM_TYPE_GOODS);
                        templateHolder.setItems(items);
                        mTemplateHolderList.add(templateHolder);
                    }
                }

                List<ShoppingTopicBean> topicList = shoppingMain.getData().getTopic();
                if (topicList.size() > 0) {
                    TemplateHolder templateHolderTopic = new TemplateHolder();//专题
                    templateHolderTopic.setTempType(ShoppingListViewAdapter.ITEM_TYPE_TOPIC);
                    templateHolderTopic.setItems(topicList);
                    mTemplateHolderList.add(templateHolderTopic);
                }

                update();
            }

            @Override
            public void onError(String errorStr, int code) {
                netWorkError(code);
            }
        });
    }

    private void getShoppingCategory() {
        mShoppingRequest.getShoppingCategory(new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mTemplateHolderList.clear();
                JTBShoppingCategory shoppingCategory = (JTBShoppingCategory) obj;
                TemplateHolder templateHolder = new TemplateHolder();
                templateHolder.setTempType(ShoppingListViewAdapter.ITEM_TYPE_GRID_VIEW);
                templateHolder.setItems(shoppingCategory.getData());
                mTemplateHolderList.add(templateHolder);
                getShoppingMain();
            }

            @Override
            public void onError(String errorStr, int code) {
                netWorkError(code);
            }
        });
    }

    private void netWorkError(int code) {
        mPtrLayout.refreshComplete();
        if (mTemplateHolderList.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }

    private void refreshShopCart() {
        mShoppingRequest.getShoppingCart(new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                JTBShoppingCart shoppingCart = (JTBShoppingCart) obj;
                setShopCartCount(shoppingCart.getData().getCartCount());
            }

            @Override
            public void onError(String errorStr, int code) {
                setShopCartCount(0);
            }
        });
    }

    private void setShopCartCount(int count) {
        mShopCart.setRedTipVisibility(count != 0);
        mShopCart.setRedTipText(count != 0 ? count + "" : "");
    }
}
