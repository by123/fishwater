package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.MyPostBean;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
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
 * Created by zhangwulin on 2015/12/25.
 * email 1501448275@qq.com
 */
public class CommunityMyPostListTabView implements ITabView {
    private Context mContext;
    private String mTitle;
    private int mType;
    private LinearLayout mParentView;
    private MyPostsRequest mMyPostsRequest;
    private ListView mMyPostListView;
    private PtrClassicFrameLayout mMyPostRefreshLayout;
    private LoadMoreListViewContainer mMyPostLoadLayout;
    private LoadingLayout mMyDataLoadingLayout;
    private CommunityMyPostAdapter mMyPostAdapter;
    private List<MyPostBean> mMyPostItems = new ArrayList<>();


    public CommunityMyPostListTabView(Context context, String mTitle, int type) {
        this.mContext = context;
        this.mTitle = mTitle;
        this.mType = type;
        mParentView = new LinearLayout(mContext);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onPrepareContentView() {
        initCommunityMyRequest(mContext);
        createMyPostListLayout();
    }

    @Override
    public View getTabView() {
        return mParentView;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    private void initCommunityMyRequest(Context context) {
        mMyPostsRequest = new MyPostsRequest(context);
    }

    private void createMyPostListLayout() {
        View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mParentView.addView(view, lp);
        initMyPostListLayoutView(view);
        tryGetMyPostsFirst();
    }

    private void initMyPostListLayoutView(View view) {
        mMyPostRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mMyPostRefreshLayout.setVisibility(View.GONE);
        mMyPostLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mMyPostListView = (ListView) view.findViewById(R.id.hot_card_list);
        mMyDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mMyDataLoadingLayout.setVisibility(View.VISIBLE);
        mMyDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mMyDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetMyPosts(true, mType, false);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mMyPostRefreshLayout.setLoadingMinTime(1000);
        mMyPostRefreshLayout.setDurationToCloseHeader(800);
        mMyPostRefreshLayout.setHeaderView(header);
        mMyPostRefreshLayout.addPtrUIHandler(header);
        mMyPostRefreshLayout.setPullToRefresh(false);
        mMyPostRefreshLayout.isKeepHeaderWhenRefresh();
        mMyPostRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetMyPosts(false, mType, false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mMyPostListView, header);
            }
        });

        mMyPostLoadLayout.useDefaultFooter();
        mMyPostLoadLayout.setAutoLoadMore(true);
        mMyPostLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetMyPosts(false, mType, true);
            }
        });
        mMyPostAdapter = new CommunityMyPostAdapter(mContext, mMyPostItems);
        mMyPostListView.setAdapter(mMyPostAdapter);

        mMyPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnListViewItemClick(position);
            }
        });
    }

    private void handleOnListViewItemClick(int position) {
        if (mMyPostItems.get(position).getPostStatus() == 1) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
            message.arg1 = mMyPostItems.get(position).getPostId();
            MsgDispatcher.getInstance().sendMessage(message);
        } else if (mMyPostItems.get(position).getPostStatus() == 0 || mMyPostItems.get(position).getPostStatus() == -1) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_NO_PASS_POST_WINDOW;
            message.arg1 = mMyPostItems.get(position).getPostId();
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }


    private void tryGetMyPostsFirst() {
        mMyDataLoadingLayout.setVisibility(View.VISIBLE);
        mMyDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mMyPostRefreshLayout.setVisibility(View.GONE);
        tryGetMyPosts(true, mType, false);
    }


    private void tryGetMyPosts(final boolean isInit, int type, final boolean isLoadMore) {
        if (mMyPostsRequest == null) {
            return;
        }
        mMyPostsRequest.startRequest(isLoadMore, type, new MyPostsRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetMyPostsFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<MyPostBean> allList, ArrayList<MyPostBean> newList) {
                handlerGetMyPostsSuccess(newList, isInit, isLoadMore);
            }
        });
    }

    private void handlerGetMyPostsFail(int errorCode, boolean isLoadMore) {
        if (isLoadMore) {
            mMyPostLoadLayout.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_network_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (mMyPostItems.isEmpty()) {
            mMyDataLoadingLayout.setVisibility(View.VISIBLE);
            mMyDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            mMyPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }

    private void handlerGetMyPostsSuccess(ArrayList<MyPostBean> newList, boolean isInit, boolean isLoadMore) {
        mMyPostLoadLayout.loadMoreFinish(false, mMyPostsRequest.hasMore());
        if (!isLoadMore) {
            mMyPostItems.clear();
        }
        mMyPostItems.addAll(newList);

        if (mMyPostAdapter != null) {
            mMyPostAdapter.notifyDataSetChanged();
        }
        if (mMyPostItems.isEmpty()) {
            mMyDataLoadingLayout.setVisibility(View.VISIBLE);
            mMyDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
            mMyPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        if (!isLoadMore && isInit) {
            mMyDataLoadingLayout.setVisibility(View.GONE);
            mMyDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mMyPostRefreshLayout.setVisibility(View.VISIBLE);
            mMyPostRefreshLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
        }
    }
}
