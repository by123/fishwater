package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.homepage.ArticleInfo;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.model.HomepageFeedRequest;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class HomepageFeedListWindow extends DefaultWindow {
    private int mFeedType;
    private int mChildType;

    private PtrClassicFrameLayout mPtrLayout;
    private LoadMoreListViewContainer mLoadMoreLayout;
    private LoadingLayout mLoadingLayout;

    private ListAdapter mListAdapter;
    private ListView mListView;

    private ArrayList<ArticleInfo> mFeedList;

    private HomepageFeedRequest mFeedRequest;

    private IFeedListWindowCallback mFeedListCallback;

    public HomepageFeedListWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        onCreateContent();
    }

    public void setFeedListCallback(IFeedListWindowCallback callback) {
        mFeedListCallback = callback;
    }

    protected View onCreateContent() {
        mFeedList = new ArrayList<ArticleInfo>();
        View parentView = View.inflate(getContext(), R.layout.homepage_article_list, null);
        initContentView(parentView);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        return parentView;
    }

    private void initContentView(View parentView) {
        mLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.homepage_article_list_loading);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                requestFeedData(false);
            }
        });
        mListView = (ListView) parentView.findViewById(R.id.homepage_article_detail_listview);
        mPtrLayout = (PtrClassicFrameLayout) parentView.findViewById(R.id.homepage_article_list_ptr);
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
                if (mFeedRequest.isRequesting()) {
                    frame.refreshComplete();
                    return;
                }
                if (mFeedRequest.isEmpty()) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading),
                            false);
                }
                requestFeedData(false);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });

        mLoadMoreLayout = (LoadMoreListViewContainer) parentView.findViewById(R.id.homepage_article_list_loadmore);
        //上拉加载更多设置
        mLoadMoreLayout.useDefaultFooter();
        mLoadMoreLayout.setAutoLoadMore(true);
        mLoadMoreLayout.loadMoreFinish(false, true);
        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                requestFeedData(true);
            }
        });

        mListAdapter = new ListAdapter(getContext(), mFeedList);
        mListView.setAdapter(mListAdapter);
        mListView.setCacheColorHint(Color.TRANSPARENT);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mFeedList.size()) {
                    return;
                }
                ArticleInfo articleInfo = mFeedList.get(position);
                if (mFeedListCallback != null) {
                    mFeedListCallback.onFeedListItemClick(articleInfo);
                }
            }
        });
    }

    public void setupFeedListWindow(HomepageFeedDetailInfo info) {
        mFeedType = info.type;
        mChildType = info.childType;
        mFeedRequest = new HomepageFeedRequest(getContext(), mFeedType, mChildType);
        String title;
        if (mFeedType == HomepageConst.FEED_TYPE_PRODUCT) {
            mFeedRequest.setPageSize(HomepageConst.PAGE_SIZE_PRODUCT);
            title = ResourceHelper.getString(R.string.homepage_feed_list_product);
        } else if (mFeedType == HomepageConst.FEED_TYPE_TEST) {
            mFeedRequest.setPageSize(HomepageConst.PAGE_SIZE_TEST);
            title = ResourceHelper.getString(R.string.homepage_feed_list_test);
        } else {
            mFeedRequest.setPageSize(HomepageConst.PAGE_SIZE_ARTILCE);
            String typeName = info.typeName;
            if (StringUtils.isEmpty(typeName)) {
                typeName = ResourceHelper.getString(R.string.homepage_feed_list_article);
            }
            title = typeName;
        }
        setTitle(title);

        requestFeedData(false);
    }

    private void requestFeedData(boolean isLoadMore) {
        mFeedRequest.startRequest(isLoadMore, new HomepageFeedRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handleGetArticleFailed(errorType);
            }

            @Override
            public void onResultSuccess(ArrayList<ArticleInfo> allList, ArrayList<ArticleInfo> newList) {
                handleGetArticleSuccess(allList);
            }
        });
    }

    private void handleGetArticleFailed(int errorType) {
        mPtrLayout.refreshComplete();
        if (errorType == HomepageConst.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (mFeedRequest.isEmpty()) {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                    .homepage_network_error_retry), true);
        } else {
            mLoadingLayout.setVisibility(View.GONE);
            if (errorType == HomepageConst.ERROR_TYPE_DATA) {
                toastDataError();
            } else {
                toastNetworkError();
            }
            mLoadMoreLayout.loadMoreError(0, ResourceHelper.getString(R.string.homepage_network_error_retry));
        }
    }

    private void handleGetArticleSuccess(ArrayList<ArticleInfo> allList) {
        mPtrLayout.refreshComplete();
        mLoadMoreLayout.loadMoreFinish(false, mFeedRequest.hasMore());
        if (allList.isEmpty()) {
            mPtrLayout.setVisibility(View.INVISIBLE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(0, ResourceHelper.getString(R.string
                    .homepage_data_empty), false);
        } else {
            mLoadingLayout.setVisibility(View.GONE);
            mPtrLayout.setVisibility(View.VISIBLE);
            mFeedList.clear();
            mFeedList.addAll(allList);
            mListAdapter.notifyDataSetChanged();
        }

    }

    private void toastDataError() {
        ToastHelper.showToast(getContext(), R.string.homepage_data_error, Toast.LENGTH_LONG);
    }

    private void toastNetworkError() {
        ToastHelper.showToast(getContext(), R.string.homepage_network_error, Toast.LENGTH_LONG);
    }

    private class ListAdapter extends ArrayAdapter<ArticleInfo> {

        public ListAdapter(Context context, ArrayList<ArticleInfo> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createViewByType(mFeedType);
            }
            setupViewByType(mFeedType, convertView, getItem(position));
            return convertView;
        }

        private View createViewByType(int type) {
            View view = null;
            if (type == HomepageConst.FEED_TYPE_ARTICLE || type == HomepageConst.FEED_TYPE_PRODUCT) {
                view = View.inflate(getContext(), R.layout.homepage_item_article, null);
            } else if (type == HomepageConst.FEED_TYPE_TEST) {
                view = View.inflate(getContext(), R.layout.homepage_items_test, null);
            } else if (type == HomepageConst.FEED_TYPE_PIC) {
                view = View.inflate(getContext(), R.layout.homepage_item_pic, null);
            }
            return view;
        }


        private void setupViewByType(int type, View view, ArticleInfo articleInfo) {
            if (type == HomepageConst.FEED_TYPE_ARTICLE || type == HomepageConst.FEED_TYPE_PRODUCT) {
                setupArticleView(view, articleInfo);
            } else if (type == HomepageConst.FEED_TYPE_TEST) {
                setupTestView(view, articleInfo);
            } else if (type == HomepageConst.FEED_TYPE_PIC) {
                setupPicView(view, articleInfo);
            }
        }

        private void setupArticleView(View view, ArticleInfo articleInfo) {
            Uri uri = Uri.parse(articleInfo.getImageUrl());
            TextView firstTextView = (TextView) view.findViewById(R.id.homepage_article_title);
            TextView secondTextView = (TextView) view.findViewById(R.id.tv_homepage_article_brief);
            TextView typeView = (TextView) view.findViewById(R.id.homepage_article_title_type);
            String typeName = articleInfo.getTypeName();
            if (StringUtils.isEmpty(typeName)) {
                typeName = getResources().getString(R.string.homepage_feed_list_article);
            }
            typeView.setText(typeName);
            int typeColor = articleInfo.getTypeColor();
            int resId = R.drawable.homepage_article_type_bg_pink;
            if (typeColor == HomepageConst.ARTICLE_TYPE_COLOR_GREEN) {
                resId = R.drawable.homepage_article_type_bg_green;
            } else if (typeColor == HomepageConst.ARTICLE_TYPE_COLOR_YELLOW) {
                resId = R.drawable.homepage_article_type_bg_yellow;
            } else if (typeColor == HomepageConst.ARTICLE_TYPE_COLOR_RED) {
                resId = R.drawable.homepage_article_type_bg_pink;
            }
            typeView.setBackgroundResource(resId);

            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id
                    .homepage_article_image);
            simpleDraweeView.setImageURI(uri);
            firstTextView.setText(articleInfo.getTitle());
            secondTextView.setText(articleInfo.getBrief());
        }

        private void setupTestView(View view, ArticleInfo articleInfo) {
            Uri uri = Uri.parse(articleInfo.getImageUrl());
            TextView firstTextView = (TextView) view.findViewById(R.id.homepage_test_title);
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.homepage_test_image);
            simpleDraweeView.setImageURI(uri);
            firstTextView.setText(articleInfo.getTitle());
        }

        private void setupPicView(View view, ArticleInfo articleInfo) {
            Uri uri = Uri.parse(articleInfo.getImageUrl());
            TextView titleView = (TextView) view.findViewById(R.id.homepage_pic_title);
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.homepage_pic_image);
            TextView numView = (TextView) view.findViewById(R.id.homepage_pic_image_num);
            simpleDraweeView.setImageURI(uri);
            titleView.setText(articleInfo.getTitle());
            numView.setText(articleInfo.getImageCount() + "photos");
        }
    }

    public interface IFeedListWindowCallback {
        public void onFeedListItemClick(ArticleInfo info);
    }
}
