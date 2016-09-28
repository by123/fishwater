package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.community.model.CommunityRequest;
import com.sjy.ttclub.community.widget.CircleSexLimitRemidPanel;
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
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
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
 * Created by zhangwulin on 2015/12/25.
 * email 1501448275@qq.com
 */
public class CommunityPostListTabView implements ITabView, INotify {
    private Context mContext;
    private String mTitle;
    private int mCircleType;
    private int mPostType;
    private int mCircleId;
    private CommunityCircleBean mCircle;
    private LinearLayout mParentView;
    private CommunityRequest mRequest;
    private FloatingActionButton mHotPostButton;
    private FloatingActionButton mCreamPostButton;
    private FloatingActionButton mNewPostButton;

    private CommunityPostListAdapter mHotPostAdapter;
    private ListView mHotPostListView;
    private PtrClassicFrameLayout mHotPostRefreshLayout;
    private LoadMoreListViewContainer mHotPostLoadLayout;
    private LoadingLayout mHotDataLoadingLayout;

    private CommunityPostListAdapter mCreamPostAdapter;
    private ListView mCreamPostListView;
    private PtrClassicFrameLayout mCreamPostRefreshLayout;
    private LoadMoreListViewContainer mCreamPostLoadLayout;
    private LoadingLayout mCreamDataLoadingLayout;

    private CommunityPostListAdapter mNewPostAdapter;
    private ListView mNewPostListView;
    private PtrClassicFrameLayout mNewPostRefreshLayout;
    private LoadMoreListViewContainer mNewPostLoadLayout;
    private LoadingLayout mNewDataLoadingLayout;

    private List<CommunityListItemInfo> mHotPostItems = new ArrayList<>();
    private List<CommunityListItemInfo> mCreamPostItems = new ArrayList<>();
    private List<CommunityListItemInfo> mNewestPostItems = new ArrayList<>();


    public CommunityPostListTabView(Context context, int circleType, int postType, String mTitle) {
        this.mContext = context;
        this.mTitle = mTitle;
        this.mCircleType = circleType;
        this.mPostType = postType;
        mParentView = new LinearLayout(mContext);
        initRegisterNotify();
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().register(this, NotificationDef.N_COMMUNITY_SHOW_TIPS);
    }

    public void unRegisterNotify() {
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_COMMUNITY_SHOW_TIPS);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onPrepareContentView() {
        initCommunityRequest(mContext);
        createLayoutByType();
    }

    @Override
    public View getTabView() {
        return mParentView;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE) {
            updateItemsPraiseState((CommunityPostBean) notification.extObj);
        } else if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            if (mPostType == CommunityConstant.GET_POST_LIST_HOTEST) {
                tryGetAllPosts(false, false);
            }
            if (mPostType == CommunityConstant.GET_POST_LIST_NEWEST) {
                tryGetCreamPosts(false, false);
            }
        } else if (notification.id == NotificationDef.N_COMMUNITY_SHOW_TIPS) {
            if (mPostType == CommunityConstant.GET_POST_LIST_HOTEST) {
                tryShowSexLimitCheck((CommunityCircleBean) notification.extObj);
            }
        }
    }

    private void tryShowSexLimitCheck(CommunityCircleBean circleBean) {
        int sex = circleBean.getLimitEnterSex();
        if (sex > 0) {
            int ckCode;
            if (sex == CommonConst.SEX_MAN) {
                ckCode = SettingFlags.getIntFlag("sex_limit_" + mCircleId + "", 0);
                if (ckCode == 0) {
                    showLimitPanel(CommonConst.SEX_WOMAN);
                    SettingFlags.setFlag("sex_limit_" + mCircleId + "", mCircleId);
                }
            } else if (sex == CommonConst.SEX_WOMAN) {
                ckCode = SettingFlags.getIntFlag("sex_limit_" + mCircleId + "", 0);
                if (ckCode == 0) {
                    showLimitPanel(CommonConst.SEX_MAN);
                    SettingFlags.setFlag("sex_limit_" + mCircleId + "", mCircleId);
                }
            }
        }
    }

    private void showLimitPanel(int sex) {
        CircleSexLimitRemidPanel circleSexLimitRemidPanel = new CircleSexLimitRemidPanel(mContext, sex);
        circleSexLimitRemidPanel.showPanel();
    }

    private void updateItemsPraiseState(CommunityPostBean postBean) {
        CommunityPostBean tempPostBean;
        for (int i = 0; i < mHotPostItems.size(); i++) {
            if (mHotPostItems.get(i).getItemType() != CommunityItemType.POST_TYPE_DEFAULT_DIVIDER) {
                tempPostBean = (CommunityPostBean) mHotPostItems.get(i).getData();
                if (tempPostBean.getPostId() == postBean.getPostId()) {
                    ((CommunityPostBean) mHotPostItems.get(i).getData()).setIsPraise(postBean.getIsPraise());
                    ((CommunityPostBean) mHotPostItems.get(i).getData()).setPraiseCount(postBean.getPraiseCount());
                }
            }
        }
        for (int i = 0; i < mCreamPostItems.size(); i++) {
            if (mCreamPostItems.get(i).getItemType() != CommunityItemType.POST_TYPE_DEFAULT_DIVIDER) {
                tempPostBean = (CommunityPostBean) mCreamPostItems.get(i).getData();
                if (tempPostBean.getPostId() == postBean.getPostId()) {
                    ((CommunityPostBean) mCreamPostItems.get(i).getData()).setIsPraise(postBean.getIsPraise());
                    ((CommunityPostBean) mCreamPostItems.get(i).getData()).setPraiseCount(postBean.getPraiseCount());
                }
            }
        }
        if (mHotPostAdapter != null) {
            mHotPostAdapter.notifyDataSetChanged();
        }
        if (mCreamPostAdapter != null) {
            mCreamPostAdapter.notifyDataSetChanged();
        }
    }

    public void setCircleId(int circleId) {
        this.mCircleId = circleId;

    }

    public void setCircleInfo(CommunityCircleBean circleInfo) {
        this.mCircle = circleInfo;

    }

    private void initCommunityRequest(Context context) {
        mRequest = new CommunityRequest(context, mPostType, mCircleId);
    }

    private void createLayoutByType() {
        createPostListLayout();
    }

    private void createPostListLayout() {
        if (mPostType == CommunityConstant.GET_POST_LIST_HOTEST) {
            View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            mParentView.addView(view, lp);
            initAllPostListLayoutView(view);
            tryGetAllPostsFirst();
        } else if (mPostType == CommunityConstant.GET_POST_LIST_NEWEST) {
            View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            mParentView.addView(view, lp);
            initCreamPostListLayoutView(view);
            tryGetCreamPostsFirst();
        }else if(mPostType==CommunityConstant.GET_POST_LIST_CREAM){
            View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            mParentView.addView(view, lp);
            initCreamPostListLayoutView(view);
            tryGetCreamPostsFirst();
        }
    }

    private void initAllPostListLayoutView(View view) {
        mHotPostButton = (FloatingActionButton) view.findViewById(R.id.post_button);
        mHotPostButton.setVisibility(View.VISIBLE);
        mHotPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnPostButtonClick();
            }
        });
        mHotPostRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mHotPostRefreshLayout.setVisibility(View.GONE);
        mHotPostLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mHotPostListView = (ListView) view.findViewById(R.id.hot_card_list);
        mHotDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mHotDataLoadingLayout.setVisibility(View.VISIBLE);
        mHotDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mHotDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetAllPosts(true, false);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mHotPostRefreshLayout.setLoadingMinTime(1000);
        mHotPostRefreshLayout.setDurationToCloseHeader(800);
        mHotPostRefreshLayout.setHeaderView(header);
        mHotPostRefreshLayout.addPtrUIHandler(header);
        mHotPostRefreshLayout.setPullToRefresh(false);
        mHotPostRefreshLayout.isKeepHeaderWhenRefresh();
        mHotPostRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetAllPosts(false, false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mHotPostListView, header);
            }
        });

        mHotPostLoadLayout.useDefaultFooter();
        mHotPostLoadLayout.setAutoLoadMore(true);
        mHotPostLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetAllPosts(false, true);
            }
        });
        mHotPostAdapter = new CommunityPostListAdapter(mContext, mHotPostItems);
        mHotPostListView.setAdapter(mHotPostAdapter);

        mHotPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnListViewItemClick(position);
            }
        });
        updateAllPostButtonIcon();

    }

    private void handleOnListViewItemClick(int position) {
        StatsModel.stats(StatsKeyDef.GROUP_POST);

        CommunityListItemInfo itemInfo = null;
        if (mPostType == CommunityConstant.GET_POST_LIST_HOTEST) {
            itemInfo = mHotPostItems.get(position);
        } else if (mPostType == CommunityConstant.GET_POST_LIST_NEWEST) {
            itemInfo = mCreamPostItems.get(position);
        }
        if (itemInfo.getData() != null) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
            message.arg1 = ((CommunityPostBean) itemInfo.getData()).getPostId();
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }

    private void updateAllPostButtonIcon() {
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mHotPostButton.setImageResource(R.drawable.community_question_edit_icon);
            return;
        }
        if (mCircle == null) {
            mHotPostButton.setImageResource(R.drawable.community_text_edit_icon);
            return;
        }
        if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_IMAGE) {
            mHotPostButton.setImageResource(R.drawable.community_image_edit_icon);
            return;
        }
        mHotPostButton.setImageResource(R.drawable.community_text_edit_icon);
    }

    private void updateCreamPostButtonIcon() {
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mCreamPostButton.setImageResource(R.drawable.community_question_edit_icon);
            return;
        }
        if (mCircle == null) {
            mCreamPostButton.setImageResource(R.drawable.community_text_edit_icon);
            return;
        }
        if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_IMAGE) {
            mCreamPostButton.setImageResource(R.drawable.community_image_edit_icon);
            return;
        }
        mCreamPostButton.setImageResource(R.drawable.community_text_edit_icon);
    }
    private void updateNewPostButtonIcon() {
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mNewPostButton.setImageResource(R.drawable.community_question_edit_icon);
            return;
        }
        if (mCircle == null) {
            mNewPostButton.setImageResource(R.drawable.community_text_edit_icon);
            return;
        }
        if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_IMAGE) {
            mNewPostButton.setImageResource(R.drawable.community_image_edit_icon);
            return;
        }
        mNewPostButton.setImageResource(R.drawable.community_text_edit_icon);
    }
    private void handlerOnPostButtonClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mCircle == null) {
            return;
        }
        mCircle.setCircleType(mCircleType);
        //统计
        StatsModel.stats(StatsKeyDef.GROUP_WRITE_POST);
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getLevel()) < 1) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.community_level_not_enough_one), Toast.LENGTH_SHORT);
            return;
        }
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            Message message = new Message();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
            message.obj = mCircle;
            MsgDispatcher.getInstance().sendMessage(message);
            return;
        }
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getSex()) == CommonConst.SEX_WOMAN) {
            Message message = new Message();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
            message.obj = mCircle;
            MsgDispatcher.getInstance().sendMessage(message);
            return;
        }
        if (mCircle.getIsLimitMale() != 0) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.community_limit_man_enter), Toast.LENGTH_SHORT);
            return;
        }
        Message message = new Message();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW;
        message.obj = mCircle;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void initCreamPostListLayoutView(View view) {
        mCreamPostButton = (FloatingActionButton) view.findViewById(R.id.post_button);
        mCreamPostButton.setVisibility(View.VISIBLE);
        mCreamPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnPostButtonClick();
            }
        });
        mCreamPostRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mCreamPostRefreshLayout.setVisibility(View.GONE);
        mCreamPostLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mCreamPostListView = (ListView) view.findViewById(R.id.hot_card_list);
        mCreamDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mCreamDataLoadingLayout.setVisibility(View.VISIBLE);
        mCreamDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mCreamDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetCreamPosts(true, false);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mCreamPostRefreshLayout.setLoadingMinTime(1000);
        mCreamPostRefreshLayout.setDurationToCloseHeader(800);
        mCreamPostRefreshLayout.setHeaderView(header);
        mCreamPostRefreshLayout.addPtrUIHandler(header);
        mCreamPostRefreshLayout.setPullToRefresh(false);
        mCreamPostRefreshLayout.isKeepHeaderWhenRefresh();
        mCreamPostRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetCreamPosts(false, false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mCreamPostListView, header);
            }
        });

        mCreamPostLoadLayout.useDefaultFooter();
        mCreamPostLoadLayout.setAutoLoadMore(true);
        mCreamPostLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetCreamPosts(false, true);
            }
        });
        mCreamPostAdapter = new CommunityPostListAdapter(mContext, mCreamPostItems);
        mCreamPostListView.setAdapter(mCreamPostAdapter);
        mCreamPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnListViewItemClick(position);
            }
        });
        updateCreamPostButtonIcon();
    }

    private void initNewPostListLayoutView(View view) {
        mNewPostButton = (FloatingActionButton) view.findViewById(R.id.post_button);
        mNewPostButton.setVisibility(View.VISIBLE);
        mNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnPostButtonClick();
            }
        });
        mNewPostRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mNewPostRefreshLayout.setVisibility(View.GONE);
        mNewPostLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mNewPostListView = (ListView) view.findViewById(R.id.hot_card_list);
        mNewDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mNewDataLoadingLayout.setVisibility(View.VISIBLE);
        mNewDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mNewDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetCreamPosts(true, false);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mNewPostRefreshLayout.setLoadingMinTime(1000);
        mNewPostRefreshLayout.setDurationToCloseHeader(800);
        mNewPostRefreshLayout.setHeaderView(header);
        mNewPostRefreshLayout.addPtrUIHandler(header);
        mNewPostRefreshLayout.setPullToRefresh(false);
        mNewPostRefreshLayout.isKeepHeaderWhenRefresh();
        mNewPostRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tryGetCreamPosts(false, false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mCreamPostListView, header);
            }
        });

        mNewPostLoadLayout.useDefaultFooter();
        mNewPostLoadLayout.setAutoLoadMore(true);
        mNewPostLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetCreamPosts(false, true);
            }
        });
        mNewPostAdapter = new CommunityPostListAdapter(mContext, mNewestPostItems);
        mNewPostListView.setAdapter(mNewPostAdapter);
        mNewPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnListViewItemClick(position);
            }
        });
        updateNewPostButtonIcon();
    }

    private void tryGetAllPostsFirst() {
        mHotDataLoadingLayout.setVisibility(View.VISIBLE);
        mHotDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mHotPostRefreshLayout.setVisibility(View.GONE);
        tryGetAllPosts(true, false);
    }

    private void tryGetCreamPostsFirst() {
        mCreamDataLoadingLayout.setVisibility(View.VISIBLE);
        mCreamDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mCreamPostRefreshLayout.setVisibility(View.GONE);
        tryGetCreamPosts(true, false);
    }
    private void tryGetNewPostsFirst() {
        mNewDataLoadingLayout.setVisibility(View.VISIBLE);
        mNewDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mNewPostRefreshLayout.setVisibility(View.GONE);
        tryGetCreamPosts(true, false);
    }
    private void tryGetAllPosts(final boolean isInit, final boolean isLoadMore) {
        if (mRequest == null) {
            return;
        }
        mRequest.startRequest(isLoadMore, new CommunityRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetAllPostFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList) {
                handlerGetAllCardsSuccess(newList, isInit, isLoadMore);
            }
        });
    }

    private void tryGetCreamPosts(final boolean isInit, final boolean isLoadMore) {
        if (mRequest == null) {
            return;
        }
        mRequest.startRequest(isLoadMore, new CommunityRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetCreamPostFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList) {
                handlerGetCreamCardsSuccess(newList, isInit, isLoadMore);
            }
        });
    }

    private void handlerGetCreamCardsSuccess(ArrayList<CommunityListItemInfo> newList, boolean isInit, boolean isLoadMore) {
        mCreamPostLoadLayout.loadMoreFinish(false, mRequest.hasMore());
        if (!isLoadMore) {
            mCreamPostItems.clear();
        }
        mCreamPostItems.addAll(newList);
        if (mCreamPostAdapter != null) {
            mCreamPostAdapter.notifyDataSetChanged();
        }
        if (mCreamPostItems.isEmpty()) {
            mCreamDataLoadingLayout.setVisibility(View.VISIBLE);
            mCreamDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
            mCreamPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        if (!isLoadMore && isInit) {
            mCreamDataLoadingLayout.setVisibility(View.GONE);
            mCreamDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mCreamPostRefreshLayout.setVisibility(View.VISIBLE);
            mCreamDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
        }
    }

    private void handlerGetAllCardsSuccess(ArrayList<CommunityListItemInfo> newList, boolean isInit, boolean isLoadMore) {
        mHotPostLoadLayout.loadMoreFinish(false, mRequest.hasMore());
        if (!isLoadMore) {
            mHotPostItems.clear();
        }
        mHotPostItems.addAll(newList);

        if (mHotPostAdapter != null) {
            mHotPostAdapter.notifyDataSetChanged();
        }
        if (mHotPostItems.isEmpty()) {
            mHotDataLoadingLayout.setVisibility(View.VISIBLE);
            mHotDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
            mHotPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        if (!isLoadMore && isInit) {
            mHotDataLoadingLayout.setVisibility(View.GONE);
            mHotDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mHotPostRefreshLayout.setVisibility(View.VISIBLE);
            mHotPostRefreshLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
            return;
        }
    }

    private void handlerGetAllPostFail(int errorCode, boolean isLoadMore) {
        if (isLoadMore) {
            mHotPostLoadLayout.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
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
        if (mHotPostItems.isEmpty()) {
            mHotDataLoadingLayout.setVisibility(View.VISIBLE);
            mHotDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            mHotPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }

    private void handlerGetCreamPostFail(int errorCode, boolean isLoadMore) {
        if (isLoadMore) {
            mCreamPostLoadLayout.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
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
        if (mCreamPostItems.isEmpty()) {
            mCreamDataLoadingLayout.setVisibility(View.VISIBLE);
            mCreamDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            mCreamPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }
}
