/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.cundong.recyclerview.EndlessRecyclerOnScrollListener;
import com.cundong.recyclerview.ExStaggeredGridLayoutManager;
import com.cundong.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.cundong.recyclerview.HeaderSpanSizeLookup;
import com.cundong.recyclerview.RecyclerViewUtils;
import com.sjy.ttclub.bean.shop.ShoppingGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingLabelBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCategoryList;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.shopping.adapter.ShoppingCategoryFilterLeftAdapter;
import com.sjy.ttclub.shopping.adapter.ShoppingCategoryFilterRightAdapter;
import com.sjy.ttclub.shopping.adapter.ShoppingCategoryMultipleAdapter;
import com.sjy.ttclub.shopping.adapter.ShoppingCategoryRecyclerViewAdapter;
import com.sjy.ttclub.shopping.adapter.ShoppingCategoryTopGridViewAdapter;
import com.sjy.ttclub.shopping.adapter.ShoppingLabelGridViewAdapter;
import com.sjy.ttclub.shopping.model.ShoppingCategoryInfo;
import com.sjy.ttclub.shopping.model.ShoppingRequest;
import com.sjy.ttclub.shopping.model.ShoppingSelectorInfo;
import com.sjy.ttclub.shopping.utils.ShoppingCategoryPanelUtils;
import com.sjy.ttclub.shopping.utils.ShoppingRecyclerViewStateUtils;
import com.sjy.ttclub.shopping.widget.ShoppingCategoryTopView;
import com.sjy.ttclub.shopping.widget.ShoppingLoadingFooter;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by zhxu on 2015/11/9.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryWindow extends DefaultWindow implements PtrHandler, View.OnClickListener, LoadingLayout.OnBtnClickListener, ShoppingCategoryPanelUtils.ICategoryPanelListener, AdapterView.OnItemClickListener, ShoppingCategoryTopGridViewAdapter.IShoppingCategoryTopGridViewAdapter, ShoppingLabelGridViewAdapter.IShoppingLabelGridViewAdapter {

    private final static int TYPE_LIST = 0;
    private final static int TYPE_STAGGER = 1;
    private int mCurrMode = TYPE_STAGGER;
    private final static String TYPE_MULTIPLE = ResourceHelper.getString(R.string.shopping_category_top_multiple);

    private Activity mContext;
    private ShoppingCategoryInfo mCategoryInfo;

    private ShoppingRequest mShoppingRequest;

    private ExStaggeredGridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLayoutManager;
    private LoadingLayout mLoadingLayout;

    private RecyclerView mRecyclerView;
    private PtrClassicFrameLayout mPtrLayout;
    private ShoppingCategoryRecyclerViewAdapter mAdapter;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private List<ShoppingGoodsBean> mCategoryList = new ArrayList<>();

    private TextView mBtnMultiple, mBtnNew, mBtnSale;
    private FrameLayout mBtnFilter;
    private ShoppingCategoryPanelUtils mShoppingCategoryPanelUtils;
    private ShoppingCategoryFilterRightAdapter mRightAdapter;
    private ShoppingCategoryFilterLeftAdapter mLeftAdapter;
    private ShoppingCategoryMultipleAdapter mMultipleAdapter;
    private ListView mListViewLeft, mListViewRight, mListViewMultiple;
    private LinearLayout mShoppingCategoryPanel, mFilterLayout, mMultipleLayout;
    private FrameLayout mFloatView;
    private ShoppingCategoryTopView mTopView;
    private TitleBarActionItem mActionItem;

    private int mCurrPage = 1, mPageSize = 10;
    String mCateId = null, mBrandId = null, mSortType = ShoppingRequest.MULTI;

    private LinearLayout mGridViewLayout;
    private GridView mGridView;
    private ShoppingCategoryTopGridViewAdapter mGridViewAdapter;
    private AlphaTextView mBtnClear;

    public ShoppingCategoryWindow(Context context, IDefaultWindowCallBacks callBacks, ShoppingCategoryInfo categoryInfo) {
        super(context, callBacks);
        mContext = (Activity) context;
        mCategoryInfo = categoryInfo;

        initActionBar();
        initUI();

        //统计PRODUCT_CATEGORY
        StatsModel.stats(StatsKeyDef.PRODUCT_CATEGORY, "spec", categoryInfo.title);
    }

    private void initUI() {
        setTitle(mCategoryInfo.title);
        View view = View.inflate(mContext, R.layout.shopping_category, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mLoadingLayout = (LoadingLayout) findViewById(R.id.shopping_category_loading);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(this);

        mPtrLayout = (PtrClassicFrameLayout) findViewById(R.id.shopping_category_list_ptr);
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

        mRecyclerView = (RecyclerView) findViewById(R.id.shopping_category_list);
        mAdapter = new ShoppingCategoryRecyclerViewAdapter(mRecyclerView, mCategoryList);
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnScrollListener(mOnScrollListener);

        mTopView = new ShoppingCategoryTopView(mContext);
        mTopView.setData(mCategoryInfo.categoryBean.getHotLabels(), this);
        RecyclerViewUtils.setHeaderView(mRecyclerView, mTopView);

        loadTemplate(TYPE_STAGGER);

//        SpacesItemDecoration decoration = new SpacesItemDecoration(ResourceHelper.getDimen(R.dimen.space_10));
//        mRecyclerView.addItemDecoration(decoration);

        mListViewMultiple = (ListView) findViewById(R.id.shopping_multiple_list);
        mListViewLeft = (ListView) findViewById(R.id.shopping_category_left);
        mLeftAdapter = new ShoppingCategoryFilterLeftAdapter(mContext);
        mListViewLeft.setAdapter(mLeftAdapter);
        mListViewLeft.setOnItemClickListener(this);
        mListViewRight = (ListView) findViewById(R.id.shopping_category_right);
        mRightAdapter = new ShoppingCategoryFilterRightAdapter(mContext, mCategoryInfo.categoryBean, mLeftAdapter);
        mListViewRight.setAdapter(mRightAdapter);
        mListViewRight.setOnItemClickListener(this);
        mMultipleAdapter = new ShoppingCategoryMultipleAdapter(mContext);
        mListViewMultiple.setAdapter(mMultipleAdapter);
        mListViewMultiple.setOnItemClickListener(this);
        mMultipleAdapter.focusMultiple();

        mShoppingCategoryPanel = (LinearLayout) findViewById(R.id.shopping_category_panel);
        mMultipleLayout = (LinearLayout) findViewById(R.id.shopping_multiple_layout);
        mFilterLayout = (LinearLayout) findViewById(R.id.shopping_filter_layout);
        mFloatView = (FrameLayout) findViewById(R.id.shopping_category_float_view);//遮挡层
        mShoppingCategoryPanelUtils = new ShoppingCategoryPanelUtils(mShoppingCategoryPanel, mFloatView, getTitleBar().getHeight());

        /**顶部条件**/
        mBtnMultiple = (TextView) findViewById(R.id.shopping_category_top_multiple);
        mBtnNew = (TextView) findViewById(R.id.shopping_category_top_new);
        mBtnSale = (TextView) findViewById(R.id.shopping_category_top_sale);
        mBtnFilter = (FrameLayout) findViewById(R.id.shopping_category_top_filter);
        mBtnMultiple.setTextColor(ResourceHelper.getColor(R.color.order_review_btn));
        mBtnMultiple.setOnClickListener(this);
        mBtnNew.setOnClickListener(this);
        mBtnSale.setOnClickListener(this);
        mBtnFilter.setOnClickListener(this);

        /**筛选GridView**/
        mGridViewLayout = (LinearLayout) findViewById(R.id.grid_layout);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mBtnClear = (AlphaTextView) findViewById(R.id.grid_clear);
        mBtnClear.setOnClickListener(this);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        mActionItem = new TitleBarActionItem(getContext());
        mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_view));
        mActionItem.setItemId(MenuItemIdDef.TITLEBAR_SHOPPING_TEMP);
        actionList.add(mActionItem);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    /**
     * 切换模板
     *
     * @param temp
     */
    private void loadTemplate(int temp) {

        mCurrMode = temp;
        mAdapter.switchMode(temp);
        mOnScrollListener.switchMode(temp);

        if (temp == TYPE_STAGGER) {
            if (mGridLayoutManager == null) {
                mGridLayoutManager = new ExStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mGridLayoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup((HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter(), mGridLayoutManager.getSpanCount()));
            }
            mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_view));
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        } else if (temp == TYPE_LIST) {
            if (mLayoutManager == null) {
                mLayoutManager = new LinearLayoutManager(mContext);
            }
            mActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_btn_layout));
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            ShoppingLoadingFooter.State state = ShoppingRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
            if (state == ShoppingLoadingFooter.State.Loading) {
                return;
            }
            showLoadView(ShoppingLoadingFooter.State.Loading);
            loadData(false);
        }
    };

    private void showLoadView(ShoppingLoadingFooter.State state) {
        if (state == ShoppingLoadingFooter.State.Normal) {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mRecyclerView, state);
        } else {
            ShoppingRecyclerViewStateUtils.setFooterViewState(mContext, mRecyclerView, mPageSize, state);
        }
    }

    private void setGriViewData(List<ShoppingSelectorInfo> selectors) {
        if (mGridViewAdapter == null) {
            mGridViewAdapter = new ShoppingCategoryTopGridViewAdapter(mContext, selectors, mGridViewLayout, mPtrLayout, this);
            mGridView.setAdapter(mGridViewAdapter);
        } else {
            mGridViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param isClear 是否清空List
     */
    private void loadData(boolean isClear) {
        if (!isClear) {
            mCurrPage++;
        } else {
            mCurrPage = 1;
        }
        if (mShoppingCategoryPanelUtils.isShow()) {
            mShoppingCategoryPanelUtils.animator(this);
        }
        mShoppingRequest = ShoppingRequest.getInstance();
        getShoppingCategoryList(mCategoryInfo.cateId, mCateId, mBrandId, mCurrPage, mSortType, isClear);
    }

    private void notifyItems(boolean isRemove) {
        int headerCount = mHeaderAndFooterRecyclerViewAdapter.getHeaderViewsCount();
        int listSize = mCategoryList.size();
        if (isRemove) {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeRemoved(headerCount, listSize);
        } else {
            mHeaderAndFooterRecyclerViewAdapter.notifyItemInserted(headerCount + listSize);
        }
    }

    /**
     * 根据选定的mBrandId&mCateId重新加载
     */
    private void reloadBySelectID() {
        List<ShoppingSelectorInfo> list = mRightAdapter.getSelectorInfoList();
        setGriViewData(list);
        for (ShoppingSelectorInfo selector : list) {
            if (selector.type == mRightAdapter.MODE_BRAND) {
                mBrandId = selector.selectId + "";
            } else if (selector.type == mRightAdapter.MODE_CATE) {
                mCateId = selector.selectId + "";
            }
        }
        loadData(true);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHOPPING_TEMP) {
            if (mCurrMode == TYPE_STAGGER) {
                loadTemplate(TYPE_LIST);
            } else {
                loadTemplate(TYPE_STAGGER);
            }
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        loadData(true);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            loadData(true);
        }
    }

    @Override
    public void OnClick() {
        loadData(true);
    }

    /**
     * 顶部筛选按钮点击
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shopping_category_top_multiple) {
            selector(mMultipleLayout, v);
        } else if (v.getId() == R.id.shopping_category_top_new) {
            reloadBySort(ShoppingRequest.TIME);
            resetBtn(mBtnNew);
        } else if (v.getId() == R.id.shopping_category_top_sale) {
            reloadBySort(ShoppingRequest.SALE);
            resetBtn(mBtnSale);
        } else if (v.getId() == R.id.shopping_category_top_filter) {
            selector(mFilterLayout, v);
        } else if (v.getId() == R.id.grid_clear) {
            mHeaderAndFooterRecyclerViewAdapter.addHeaderView(mTopView);
            mRightAdapter.getSelectorInfoList().clear();
            mGridViewAdapter.notifyDataSetChanged();
            mBrandId = null;
            mCateId = null;
            loadData(true);
        }
    }

    private void selector(LinearLayout linearLayout, View v) {
        boolean isShow = mShoppingCategoryPanelUtils.isShow();
        if (linearLayout.getVisibility() == VISIBLE && isShow) {
            mShoppingCategoryPanelUtils.animator(this);
        } else if (!isShow) {
            mShoppingCategoryPanelUtils.animator(this);
        }
        mBtnMultiple.setBackgroundResource(R.color.white);
        mBtnFilter.setBackgroundResource(R.color.white);
        v.setBackgroundResource(R.color.settings_item_press);
        mFilterLayout.setVisibility(GONE);
        mMultipleLayout.setVisibility(GONE);
        linearLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.tv_category);
        if (parent.getId() == R.id.shopping_category_left) {
            int mode = (int) textView.getTag();
            mRightAdapter.setMode(mode == mRightAdapter.MODE_BRAND ? mRightAdapter.MODE_CATE : mRightAdapter.MODE_BRAND);
            mLeftAdapter.focus(textView);
        } else if (parent.getId() == R.id.shopping_category_right) {
            mHeaderAndFooterRecyclerViewAdapter.removeHeaderView(mTopView);
            mShoppingCategoryPanelUtils.animator(this);
            mRightAdapter.focus(textView);
            reloadBySelectID();
        } else if (parent.getId() == R.id.shopping_multiple_list) {
            reloadBySort(textView.getTag().toString());
            mShoppingCategoryPanelUtils.animator(this);
            mMultipleAdapter.focus(textView);
            resetBtn(mBtnMultiple);
            mBtnMultiple.setText(textView.getText());
            loadData(true);
        }
    }

    /**
     * 重置顶部条件按钮状态
     *
     * @param v
     */
    private void resetBtn(TextView v) {
        mBtnMultiple.setText(TYPE_MULTIPLE);
        mBtnMultiple.setTextColor(ResourceHelper.getColor(R.color.shopping_items_other_color));
        mBtnNew.setTextColor(ResourceHelper.getColor(R.color.shopping_items_other_color));
        mBtnSale.setTextColor(ResourceHelper.getColor(R.color.shopping_items_other_color));
        v.setTextColor(ResourceHelper.getColor(R.color.order_review_btn));
    }

    private void reloadBySort(String sort) {
        mMultipleAdapter.unFocus();
        mSortType = sort;
        loadData(true);
    }

    /**
     * 清空刷新
     *
     * @param type
     */
    @Override
    public void onRefresh(int type) {
        if (type == mRightAdapter.MODE_BRAND) {
            mBrandId = null;
        } else if (type == mRightAdapter.MODE_CATE) {
            mCateId = null;
        }
        if (mBrandId == null && mCateId == null) {
            mHeaderAndFooterRecyclerViewAdapter.addHeaderView(mTopView);
        }
        loadData(true);
    }

    @Override
    public void onSelected(ShoppingLabelBean bean) {
        mRightAdapter.focus(bean);
        reloadBySelectID();
        mHeaderAndFooterRecyclerViewAdapter.removeHeaderView(mTopView);
    }

    @Override
    public void hide() {
        mBtnMultiple.setBackgroundResource(R.color.white);
        mBtnFilter.setBackgroundResource(R.color.white);
    }

    /*******************
     * 接口访问
     ********************/
    private void getShoppingCategoryList(String cateId, String cateId2, String brandId, int page, String sortType, final boolean isClear) {
        mShoppingRequest.getShoppingCategoryList(cateId, cateId2, brandId, page, sortType, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, result);
                }
                mLoadingLayout.hide();
                mPtrLayout.refreshComplete();
                showLoadView(ShoppingLoadingFooter.State.Normal);

                if (isClear) {
                    notifyItems(true);
                    mCategoryList.clear();
                }

                JTBShoppingCategoryList categoryList = (JTBShoppingCategoryList) obj;
                List<ShoppingGoodsBean> list = categoryList.getData().getGoods();
                if (list.size() == 0) {
                    showLoadView(ShoppingLoadingFooter.State.TheEnd);
                } else {
                    notifyItems(false);
                    mCategoryList.addAll(list);

                    if (mPageSize == mCategoryList.size()) {//滚回顶部
                        mRecyclerView.smoothScrollToPosition(0);
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                netWorkError(code);
            }
        });
    }

    private void netWorkError(int code) {
        showLoadView(ShoppingLoadingFooter.State.Normal);
        mPtrLayout.refreshComplete();
        if (mCategoryList.isEmpty() && code == HttpCode.ERROR_NETWORK) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(mContext, R.string.shopping_network_error_retry);
        }
    }
}
