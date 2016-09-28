package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.CommunityRequest;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ImageCycleView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;
import com.sjy.ttclub.widget.TabPager;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2015/11/27.
 */
public class CommunityMainTabView extends FrameLayout {
    private Context mContext;
    private List<CommunityListItemInfo> mHomeItems = new ArrayList<>();
    private CommunityHomepageAdapter mHomepageAdapter;
    private PtrClassicFrameLayout mHomepageRefreshLayout;
    private ListView mHomeListView;
    private CommunityHomepageRequest mHomepageRequest;
    private LoadingLayout mHomeDataLoadingLayout;
    private TextView mTitleView;

    public CommunityMainTabView(Context context) {
        super(context);
        this.mContext = context;
        createContentLayoutByType();
    }

    private void createContentLayoutByType() {
        createHomeLayout(mContext);
        initCommunityRequest(mContext);
    }

    public void setEnableBannerAnim(boolean enable) {
        if (mHomepageAdapter != null) {
            if (enable) {
                mHomepageAdapter.startImageBanner();
            } else {
                mHomepageAdapter.stopImageBanner();
            }
        }
    }

    private void createHomeLayout(Context context) {
        View view = View.inflate(context, R.layout.community_home_page_fragment, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        addView(view, lp);
        initHomepageLayout(view);
        //统计community_discovery_view
        StatsModel.stats(StatsKeyDef.COMMUNITY_DISCOVERY_VIEW);
    }

    private void initHomepageLayout(View view) {
        mTitleView = (TextView) view.findViewById(R.id.titleView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleView.getLayoutParams();
        if (SystemUtil.isTransparentStatusBarEnable()) {
            lp.topMargin += SystemUtil.getStatusBarHeight(getContext());
        }
        mTitleView.setLayoutParams(lp);
        mHomepageRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.refresh_list);
        mHomeListView = (ListView) view.findViewById(R.id.home_list);
        mHomeDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.loading_layout);
        mHomeDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetHomeData();
            }
        });

        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mHomepageRefreshLayout.setVisibility(View.VISIBLE);
        mHomepageRefreshLayout.setLoadingMinTime(1000);
        mHomepageRefreshLayout.setDurationToCloseHeader(800);
        mHomepageRefreshLayout.setHeaderView(header);
        mHomepageRefreshLayout.addPtrUIHandler(header);
        mHomepageRefreshLayout.setPullToRefresh(false);
        mHomepageRefreshLayout.isKeepHeaderWhenRefresh();
        mHomepageRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetHomeData();
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mHomeListView, header);
            }
        });
        mHomepageAdapter = new CommunityHomepageAdapter(mContext, mHomeItems);
        mHomeListView.setAdapter(mHomepageAdapter);
    }

    private void initCommunityRequest(Context context) {
        mHomepageRequest = new CommunityHomepageRequest(context);
        tryGetHomeDataFirst();
    }

    private void tryGetHomeDataFirst() {
        mHomepageRefreshLayout.setVisibility(View.GONE);
        mHomeDataLoadingLayout.setVisibility(View.VISIBLE);
        mHomeDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        tryGetHomeData();
    }

    private void tryGetHomeData() {
        mHomepageRequest.startRequest(new CommunityHomepageRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetHomeDataFailed(errorType);
            }

            @Override
            public void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo>
                    newList) {
                handlerGetHomeDataSuccess(newList);
            }
        });
    }

    /**
     * 数据异常处理
     *
     * @param errorCode
     */
    private void handlerGetHomeDataFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            if (mHomeItems.isEmpty()) {
                mHomepageRefreshLayout.setVisibility(View.GONE);
                mHomeDataLoadingLayout.setVisibility(View.VISIBLE);
                mHomeDataLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                        .homepage_network_error_retry), true);
            } else {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_network_error), Toast
                        .LENGTH_SHORT);
            }
            return;
        }
        if (mHomeItems.isEmpty()) {
            setHomeDataFaultBg();
        } else {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast
                    .LENGTH_SHORT);
        }
    }

    private void setHomeDataFaultBg() {
        mHomeDataLoadingLayout.setVisibility(View.VISIBLE);
        mHomeDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string
                .homepage_data_error), true);
        mHomepageRefreshLayout.setVisibility(View.GONE);
    }

    private void handlerGetHomeDataSuccess(ArrayList<CommunityListItemInfo> newList) {
        mHomeItems.clear();
        mHomeItems.addAll(newList);
        if (mHomeItems.isEmpty()) {
            mHomeDataLoadingLayout.setVisibility(View.VISIBLE);
            mHomeDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string
                    .homepage_data_empty), true);
            mHomepageRefreshLayout.setVisibility(View.GONE);
        } else {
            mHomeDataLoadingLayout.setVisibility(View.GONE);
            mHomepageRefreshLayout.setVisibility(View.VISIBLE);
        }
        if (mHomepageAdapter != null) {
            mHomepageAdapter.notifyDataSetChanged();
        }
    }

    public boolean canScrollTab() {
        boolean canScroll = false;
        if (mHomepageAdapter != null) {
            ImageCycleView banner = mHomepageAdapter.getBanner();
            if (banner != null) {
                canScroll = banner.isIsTouchDown();
            }
        }
        return canScroll;
    }
}
