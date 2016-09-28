package com.sjy.ttclub.shopping.product.comments;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingGoodsReviewBean;
import com.sjy.ttclub.bean.shop.ShoppingReviewBean;
import com.sjy.ttclub.bean.shop.ShoppingSummaryBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.shopping.ShoppingConstant;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarRequest;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
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
 * Created by chenjiawei on 2015/12/31.
 */
public class ProductCommentListWindow extends DefaultWindow {
    private LoadingLayout mLoadingLayout;
    private PtrClassicFrameLayout mPtrLayout;
    private LoadMoreListViewContainer mLoadMoreContainer;
    private ListView mListView;
    private TextView mMixCommentInfo;
    private RatingBar mMixRatingBar;
    private RadioGroup mRadioGroup;
    private ProductCommentListAdapter mListAdapter;
    private ArrayList<ShoppingReviewBean> mCommentList;
    private ProductCommentsRequest mCommentsRequest;

    private String mType = ShoppingConstant.COMMENT_TYPE_ALL;

    public ProductCommentListWindow(Context context, IDefaultWindowCallBacks callBacks, String goodsId) {
        super(context, callBacks);
        setTitle(R.string.shopping_product_comment_window_title);
        initActionBar();
        onCreateContent();
        mCommentsRequest = new ProductCommentsRequest(context, goodsId);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.shopping_cart));
        item.setItemId(MenuItemIdDef.TITLEBAR_SHOPPING_CART);
        actionList.add(item);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHOPPING_CART) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_SHOPPING_CAR_WINDOW;
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }

    private View onCreateContent() {
        View view = View.inflate(getContext(), R.layout.shopping_product_comments_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mMixCommentInfo = (TextView) view.findViewById(R.id.mix_comment_info);
        mMixRatingBar = (RatingBar) view.findViewById(R.id.mix_comment_star);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.product_comment_sort_radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_group_all) {
                    mType = ShoppingConstant.COMMENT_TYPE_ALL;
                } else if (checkedId == R.id.radio_group_satisfy) {
                    mType = ShoppingConstant.COMMENT_TYPE_SATISFIED;
                } else if (checkedId == R.id.radio_group_general) {
                    mType = ShoppingConstant.COMMENT_TYPE_GENERAL;
                } else if (checkedId == R.id.radio_group_unsatisfiy) {
                    mType = ShoppingConstant.COMMENT_TYPE_UNSATISFIED;
                } else {
                    mType = ShoppingConstant.COMMENT_TYPE_ALL;
                }
                tryGetCommentsData(false);
            }
        });

        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.product_comments_loading_layout);
        mLoadingLayout.setDefaultLoading();

        mListView = (ListView) view.findViewById(R.id.product_comment_list);

        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.product_comment_list_ptr);
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
        mPtrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mCommentsRequest.isRequesting()) {
                    frame.refreshComplete();
                    return;
                }
                if (mCommentsRequest.isEmpty()) {
                    mLoadingLayout.setDefaultLoading();
                }
                tryGetCommentsData(false);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });

        mLoadMoreContainer = (LoadMoreListViewContainer) view.findViewById(R.id.product_comment_list_loadmore);
        mLoadMoreContainer.useDefaultFooter();
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.loadMoreFinish(false, true);
        mLoadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetCommentsData(true);
            }
        });

        mCommentList = new ArrayList<ShoppingReviewBean>();
        mListAdapter = new ProductCommentListAdapter(getContext(), mCommentList);
        mListView.setAdapter(mListAdapter);
        sendGetShoppingCarCountRequest();
    }

    private void tryGetCommentsData(boolean isLoadMore) {
        mCommentsRequest.startRequest(isLoadMore, mType, new ProductCommentsRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(errorType);
            }

            @Override
            public void onResultSuccess(ShoppingGoodsReviewBean reviewBean, List<ShoppingReviewBean> reviewList) {
                handleGetDataSuccess(reviewBean, reviewList);
            }
        });
    }

    private void handleGetDataSuccess(ShoppingGoodsReviewBean reviewBean, List<ShoppingReviewBean> reviewList) {
        mLoadingLayout.setVisibility(View.GONE);

        ShoppingSummaryBean summaryBean = reviewBean.getSummary();
        String allText = ResourceHelper.getString(R.string.shopping_product_comment_all);
        String satisfyText = ResourceHelper.getString(R.string.shopping_product_comment_satisfy);
        String generalText = ResourceHelper.getString(R.string.shopping_product_comment_general);
        String unsatisfyText = ResourceHelper.getString(R.string.shopping_product_comment_unsatisfy);
        if (summaryBean != null) {
            allText = allText + "(" + summaryBean.getTotalCount() + ")";
            satisfyText = satisfyText + "(" + summaryBean.getSatisfiedCount() + ")";
            generalText = generalText + "(" + summaryBean.getGeneralCount() + ")";
            unsatisfyText = unsatisfyText + "(" + summaryBean.getUnsatisfiedCount() + ")";

            String commentText = ResourceHelper.getString(R.string.shopping_product_comment_rating);
            commentText = commentText + summaryBean.getRating();
            mMixCommentInfo.setText(commentText);
            mMixRatingBar.setRating(StringUtils.parseFloat(summaryBean.getRating()));
        }
        ((RadioButton) mRadioGroup.findViewById(R.id.radio_group_all)).setText(allText);
        ((RadioButton) mRadioGroup.findViewById(R.id.radio_group_satisfy)).setText(satisfyText);
        ((RadioButton) mRadioGroup.findViewById(R.id.radio_group_general)).setText(generalText);
        ((RadioButton) mRadioGroup.findViewById(R.id.radio_group_unsatisfiy)).setText(unsatisfyText);

        mCommentList.clear();
        mCommentList.addAll(reviewList);
        mListAdapter.notifyDataSetChanged();

        mPtrLayout.refreshComplete();
        mLoadMoreContainer.loadMoreFinish(false, mCommentsRequest.hasMore());
    }

    private void handlerGetDataFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        mPtrLayout.refreshComplete();
        mLoadMoreContainer.loadMoreFinish(false, mCommentsRequest.hasMore());
        if (mCommentList.isEmpty()) {
            mLoadingLayout.setDefaultNetworkError(true);
        } else {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.data_error), Toast
                    .LENGTH_SHORT);
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            tryGetCommentsData(false);
        }
    }

    private void sendGetShoppingCarCountRequest() {
        final TitleBarActionItem item = getTitleBarInner().getActionBar().getItem(MenuItemIdDef.TITLEBAR_SHOPPING_CART);
        new ShoppingCarRequest().startGetShoppingCarCountRequest(new ShoppingCarRequest.ShoppingCarCountResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFailed(CommonConst.ERROR_TYPE_NETWORK);
                item.setRedTipVisibility(false);
                item.setRedTipText("");
            }

            @Override
            public void onResultSuccess(int count) {
                if (count > 0) {
                    item.setRedTipVisibility(true);
                    item.setRedTipText(count + "");
                } else {
                    item.setRedTipVisibility(false);
                    item.setRedTipText("");
                }
            }
        });
    }

}
