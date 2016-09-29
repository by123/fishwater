package com.sjy.ttclub.homepage;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.google.gson.Gson;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.bean.homepage.FeedInfo;
import com.sjy.ttclub.bean.homepage.HomepageInfo;
import com.sjy.ttclub.bean.homepage.JTBFeedDataInfo;
import com.sjy.ttclub.homepage.model.FeedRequest;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
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

public class HomepageMainView extends FrameLayout {

    private PtrClassicFrameLayout mPullRefreshView;
    private LoadMoreListViewContainer mLoadMoreView;
    private HomepageTopLayout mTopLayout;
    private LoadingLayout mLoadingLayout;
    private ListView mHomepageListView;
    private HomepageListViewAdapter mHomepageListAdapter;

    private FeedRequest mFeedRequest;

    private boolean mHasHomepageCacheData = false;
    private boolean mHasArticleCacheData = false;

    public HomepageMainView(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.homepage_main, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addView(view, lp);
        initView(view);

        initViewWithCacheData();

        mFeedRequest = new FeedRequest(getContext());
        tryGetHomepageData();
        startRequestArticleData(false);
    }

    private void initView(View view) {
        mPullRefreshView = (PtrClassicFrameLayout) view.findViewById(R.id.homepage_prt);
        mLoadMoreView = (LoadMoreListViewContainer) view.findViewById(R.id.homepage_load_more);
        mTopLayout = (HomepageTopLayout) view.findViewById(R.id.homepage_toplayout);

        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.homepage_loading);
        mLoadingLayout.setVisibility(View.GONE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                tryGetHomepageData();
                startRequestArticleData(false);
            }
        });

        final TTRefreshHeader header = new TTRefreshHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPullRefreshView.setVisibility(View.VISIBLE);
        mPullRefreshView.setLoadingMinTime(1000);
        mPullRefreshView.setDurationToCloseHeader(800);
        mPullRefreshView.setHeaderView(header);
        mPullRefreshView.addPtrUIHandler(header);
        mPullRefreshView.setPullToRefresh(false);
        mPullRefreshView.isKeepHeaderWhenRefresh();
        mPullRefreshView.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mFeedRequest.isRequesting()) {
                    frame.refreshComplete();
                    return;
                }
                StatsModel.stats(StatsKeyDef.HOMEPAGE_REFRESH);
                if (mFeedRequest.isEmpty() && !mHasArticleCacheData) {
                    mHomepageListView.setVisibility(View.INVISIBLE);
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading),
                            false);
                }
                startRequestArticleData(false);
                tryGetHomepageData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mHomepageListView, header);
            }
        });
        mLoadMoreView.useDefaultFooter();
        mLoadMoreView.setAutoLoadMore(true);
        mLoadMoreView.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                StatsModel.stats(StatsKeyDef.HOMEPAGE_LOADMORE);
                startRequestArticleData(true);
            }
        });

        mHomepageListView = (ListView) view.findViewById(R.id.homepage_listview);
        mHomepageListAdapter = new HomepageListViewAdapter(getContext());
        mHomepageListView.setAdapter(mHomepageListAdapter);
        mHomepageListView.setOnItemClickListener(mHomepageListAdapter);
        mLoadMoreView.setOnScrollListener(mHomepageListAdapter);

    }

    private void initViewWithCacheData() {
        HomepageCacheManager.CacheData cacheData = HomepageCacheManager.getHomepageFirstData(getContext());
        if (cacheData != null && StringUtils.isNotEmpty(cacheData.data)) {
            parseHomepageData(cacheData.data, true);
        }

        cacheData = HomepageCacheManager.getArticleFirstPageData(getContext());
        if (cacheData != null && StringUtils.isNotEmpty(cacheData.data)) {
            try {
                Gson gson = new Gson();
                JTBFeedDataInfo feedDataInfo = gson.fromJson(cacheData.data, JTBFeedDataInfo.class);
                if (feedDataInfo == null || feedDataInfo.getData() == null) {
                    return;
                }
                List<FeedInfo> feedInfoList = feedDataInfo.getData().getFeed();
                if (feedInfoList == null || feedInfoList.isEmpty()) {
                    return;
                }

                ArrayList<FeedInfo> newList = new ArrayList<FeedInfo>(feedInfoList.size());
                newList.addAll(feedInfoList);
                mHasArticleCacheData = !newList.isEmpty();
                mHomepageListAdapter.updateFeedList(newList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tryGetHomepageData() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "slideshowList");
        httpManager.addParams("position","1");
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleGetHomepageDataFailed();
                    return;
                }
                parseHomepageData(result, false);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetHomepageDataFailed();
            }
        });
    }

    private void handleGetHomepageDataFailed() {
        //do something for get first page data failed???
        if (!mHasHomepageCacheData) {
            updateTopBanners(null);
        }
    }

    private void parseHomepageData(String result, boolean isFromCache) {
        if (StringUtils.isEmpty(result)) {
            handleGetHomepageDataFailed();
            return;
        }

        Gson gson = new Gson();
        HomepageInfo homepageInfo = null;
        try {
            homepageInfo = gson.fromJson(result, HomepageInfo.class);
        } catch (Exception e) {
            handleGetHomepageDataFailed();
            e.printStackTrace();
            return;
        }

        if (homepageInfo == null || homepageInfo.getData() == null) {
            handleGetHomepageDataFailed();
            return;
        }
        //banner 数据更新
        List<Banner> bannerList = homepageInfo.getData();
        updateTopBanners(bannerList);
//        mTopLayout.setQACircleId(homepageInfo.getData().getCircleId());
        if (!isFromCache) {
            HomepageCacheManager.cacheHomepageFirstData(getContext(), result);
        } else {
            mHasHomepageCacheData = true;
        }
    }

    private void updateTopBanners(List<Banner> bannerList) {
        ArrayList<Banner> resultList = new ArrayList<Banner>();
        if (bannerList != null) {
            resultList.addAll(bannerList);
        }
        mTopLayout.setupBanner(resultList);
    }

    private void startRequestArticleData(boolean loadMore) {
        mFeedRequest.startRequest(loadMore, new FeedRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handleGetFeedFailed(errorType);
            }

            @Override
            public void onResultSuccess(ArrayList<FeedInfo> allList, ArrayList<FeedInfo> newList) {
                handleGetFeedSuccess(allList);
            }
        });
    }

    private void handleGetFeedFailed(int errorType) {
        mPullRefreshView.refreshComplete();
        if (errorType == HomepageConst.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (mFeedRequest.isEmpty()) {
            if (!mHasArticleCacheData) {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                        .homepage_network_error_retry), true);
                mHomepageListView.setVisibility(View.GONE);
            } else {
                mLoadingLayout.setVisibility(View.GONE);
                if (errorType == HomepageConst.ERROR_TYPE_DATA) {
                    toastDataError();
                } else {
                    toastNetworkError();
                }
            }
        } else {
            mLoadingLayout.setVisibility(View.GONE);
            mHomepageListView.setVisibility(View.VISIBLE);
            if (errorType == HomepageConst.ERROR_TYPE_DATA) {
                toastDataError();
            } else {
                toastNetworkError();
            }
            mLoadMoreView.loadMoreError(0, ResourceHelper.getString(R.string.homepage_network_error_retry));
        }
    }

    private void handleGetFeedSuccess(ArrayList<FeedInfo> allList) {
        mPullRefreshView.refreshComplete();
        mLoadMoreView.loadMoreFinish(false, mFeedRequest.hasMore());
        mLoadingLayout.setVisibility(View.GONE);
        mHomepageListView.setVisibility(View.VISIBLE);
        mHomepageListAdapter.updateFeedList(allList);
    }

    private void toastDataError() {
        ToastHelper.showToast(getContext(), R.string.homepage_data_error);
    }

    private void toastNetworkError() {
        ToastHelper.showToast(getContext(), R.string.homepage_network_error);
    }

    public void setMainViewCallback(HomepageMainViewCallback callback) {
        mTopLayout.setTopLayoutCallback(callback);
        mHomepageListAdapter.setListViewCallback(callback);
    }


    public void onPause() {
        mTopLayout.onPause();
    }

    public void onResume() {
        mTopLayout.onResume();
    }

    public boolean canScroll() {
        return mTopLayout.isTouchDownBanner();
    }

    public interface HomepageMainViewCallback extends HomepageTopLayout.HomepageTopLayoutCallback,
            HomepageListViewAdapter.HomepageListViewCallback {

    }

}
