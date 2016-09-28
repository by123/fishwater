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
import com.sjy.ttclub.bean.community.MyCommentBean;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
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
public class CommunityMyCommentListTabView implements ITabView {
    private Context mContext;
    private String mTitle;
    private int mType;
    private LinearLayout mParentView;
    private MyReplyRequest mMyCommentRequest;
    private ListView mMyCommentListView;
    private PtrClassicFrameLayout mMyCommentRefreshLayout;
    private LoadMoreListViewContainer mMyCommentLoadLayout;
    private LoadingLayout mMyDataLoadingLayout;
    private CommunityMyCommentAdapter mMyCommentAdapter;
    private List<MyCommentBean> mMyCommentItems = new ArrayList<>();
    private static final int TYPE_CARD = 1;
    private static final int TYPE_QA = 2;
    private static final int TYPE_ARTICAL = 3;
    private static final int TYPE_POST_COMMENT = 4;
    private static final int TYPE_QA_COMMENT = 5;
    private static final int TYPE_ARTICAL_COMMENT = 6;
    private static final int TYPE_RISEGESTURE_ARTICAL = 9;
    private static final int TYPE_TEST_ARTICAL = 7;
    private static final int TYPE_SHOPPING_ARTICAL = 8;
    private static final int TYPE_PIC_ARTICAL = 10;
    private static final int TYPE_TEST_ARTICAL_COMMENT = 11;
    private static final int TYPE_SHOPPING_ARTICAL_COMMENT = 12;
    private static final int TYPE_RISEGESTURE_ARTICAL_COMMENT = 13;
    private static final int TYPE_PIC_ARTICAL_COMMENT = 14;

    public CommunityMyCommentListTabView(Context context, String mTitle, int type) {
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
        initCommunityMyCommentRequest(mContext);
        createMyCommentListLayout();
    }

    @Override
    public View getTabView() {
        return mParentView;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    private void initCommunityMyCommentRequest(Context context) {
        mMyCommentRequest = new MyReplyRequest(context);
    }

    private void createMyCommentListLayout() {
        View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mParentView.addView(view, lp);
        initMyCommentListLayoutView(view);
        tryGetMyCommentsFirst();
    }

    private void initMyCommentListLayoutView(View view) {
        mMyCommentRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mMyCommentRefreshLayout.setVisibility(View.GONE);
        mMyCommentLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mMyCommentListView = (ListView) view.findViewById(R.id.hot_card_list);
        mMyDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mMyDataLoadingLayout.setVisibility(View.VISIBLE);
        mMyDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mMyDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetMyComments(true, false, mType);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mMyCommentRefreshLayout.setLoadingMinTime(1000);
        mMyCommentRefreshLayout.setDurationToCloseHeader(800);
        mMyCommentRefreshLayout.setHeaderView(header);
        mMyCommentRefreshLayout.addPtrUIHandler(header);
        mMyCommentRefreshLayout.setPullToRefresh(false);
        mMyCommentRefreshLayout.isKeepHeaderWhenRefresh();
        mMyCommentRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetMyComments(false, false, mType);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mMyCommentListView, header);
            }
        });

        mMyCommentLoadLayout.useDefaultFooter();
        mMyCommentLoadLayout.setAutoLoadMore(true);
        mMyCommentLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetMyComments(false, true, mType);
            }
        });
        mMyCommentAdapter = new CommunityMyCommentAdapter(mContext, mMyCommentItems);
        mMyCommentListView.setAdapter(mMyCommentAdapter);

        mMyCommentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMyCommentItems.isEmpty()) {
                    return;
                }
                handleOnListViewItemClick(mMyCommentItems.get(position));
            }
        });
    }

    /*帖子评论:4；问答评论:5；文章评论:6 测试文章评论:11；导购文章评论:12；涨姿势文章评论:13；图片文章评论:14；*/
    private void handleOnListViewItemClick(MyCommentBean commentBean) {
        switch (commentBean.getType()) {
            case TYPE_CARD:
                intentToCardDetail(commentBean);
                break;
            case TYPE_QA:
                intentToCardDetail(commentBean);
                break;
            case TYPE_POST_COMMENT:
                intentToCardDetail(commentBean);
                break;
            case TYPE_QA_COMMENT:
                intentToCardDetail(commentBean);
                break;
            case TYPE_ARTICAL:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_ARTICLE);
                break;
            case TYPE_TEST_ARTICAL:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_TEST);
                break;
            case TYPE_SHOPPING_ARTICAL:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_PRODUCT);
                break;
            case TYPE_PIC_ARTICAL:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_PIC);
                break;
            case TYPE_ARTICAL_COMMENT:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_ARTICLE);
                break;
            case TYPE_TEST_ARTICAL_COMMENT:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_TEST);
                break;
            case TYPE_SHOPPING_ARTICAL_COMMENT:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_PRODUCT);
                break;
            case TYPE_PIC_ARTICAL_COMMENT:
                intentToArticle(commentBean, HomepageConst.FEED_TYPE_PIC);
                break;
        }
    }

    private void intentToCardDetail(MyCommentBean commentBean) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
        message.arg1 = commentBean.getToPostId();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void intentToArticle(MyCommentBean commentBean, int type) {
        HomepageFeedDetailInfo info = new HomepageFeedDetailInfo();
        info.type = type;
        info.articleId = String.valueOf(commentBean.getToPostId());

        Message message = Message.obtain();
        message.obj = info;
        message.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
        MsgDispatcher.getInstance().sendMessage(message);

    }

    private void tryGetMyCommentsFirst() {
        mMyDataLoadingLayout.setVisibility(View.VISIBLE);
        mMyDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mMyCommentRefreshLayout.setVisibility(View.GONE);
        tryGetMyComments(true, false, mType);
    }


    private void tryGetMyComments(final boolean isInit, final boolean isLoadMore, int type) {
        if (mMyCommentRequest == null) {
            return;
        }
        mMyCommentRequest.startRequest(isLoadMore, type, new MyReplyRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetMyCommentsFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<MyCommentBean> allList, ArrayList<MyCommentBean> newList) {
                handlerGetMyCommentsSuccess(newList, isInit, isLoadMore);
            }
        });
    }

    private void handlerGetMyCommentsFail(int errorCode, boolean isLoadMore) {
        if (isLoadMore) {
            mMyCommentLoadLayout.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
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
        if (mMyCommentItems.isEmpty()) {
            mMyDataLoadingLayout.setVisibility(View.VISIBLE);
            mMyDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            mMyCommentRefreshLayout.setVisibility(View.GONE);
            return;
        }
        ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }

    private void handlerGetMyCommentsSuccess(ArrayList<MyCommentBean> newList, boolean isInit, boolean isLoadMore) {
        mMyCommentLoadLayout.loadMoreFinish(false, mMyCommentRequest.hasMore());
        if (!isLoadMore) {
            mMyCommentItems.clear();
        }
        mMyCommentItems.addAll(newList);

        if (mMyCommentAdapter != null) {
            mMyCommentAdapter.notifyDataSetChanged();
        }
        if (mMyCommentItems.isEmpty()) {
            mMyDataLoadingLayout.setVisibility(View.VISIBLE);
            mMyDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
            mMyCommentRefreshLayout.setVisibility(View.GONE);
            return;
        }
        if (!isLoadMore && isInit) {
            mMyDataLoadingLayout.setVisibility(View.GONE);
            mMyDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mMyCommentRefreshLayout.setVisibility(View.VISIBLE);
            mMyCommentRefreshLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
        }
    }
}
