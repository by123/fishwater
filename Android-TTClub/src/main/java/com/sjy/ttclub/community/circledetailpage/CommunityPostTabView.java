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
import com.sjy.ttclub.util.ThreadManager;
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
 * Created by zhangwulin on 2016/1/14.
 * email 1501448275@qq.com
 */
public class CommunityPostTabView implements ITabView, INotify {

    private Context mContext;
    private String mTitle;
    private int mCircleType;
    private int mPostType;
    private int mCircleId;
    private CommunityCircleBean mCircle;
    private LinearLayout mParentView;
    private CommunityRequest mRequest;
    private FloatingActionButton mPostButton;

    private CommunityPostListAdapter mPostAdapter;
    private ListView mPostListView;
    private PtrClassicFrameLayout mPostRefreshLayout;
    private LoadMoreListViewContainer mPostLoadLayout;
    private LoadingLayout mDataLoadingLayout;

    private List<CommunityListItemInfo> mPostItems = new ArrayList<>();

    public CommunityPostTabView(Context context, int circleType, int postType, String mTitle) {
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
        createLayout();
    }

    @Override
    public View getTabView() {
        return mParentView;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == TAB_TO_SHOW) {
            if (mPostType == CommunityConstant.GET_POST_LIST_NEWEST) {
                if (mCircle != null && StringUtils.isNotEmpty(mCircle.getCircleName())) {
                    StatsModel.stats(StatsKeyDef.GROUP_TAB_NEW, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
                } else {
                    StatsModel.stats(StatsKeyDef.GROUP_TAB_NEW, StatsKeyDef.SPEC_KEY, mCircleId);
                }
            } else if (mPostType == CommunityConstant.GET_POST_LIST_CREAM) {
                if (mCircle != null && StringUtils.isNotEmpty(mCircle.getCircleName())) {
                    StatsModel.stats(StatsKeyDef.GROUP_TAB_ESSENCE, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
                } else {
                    StatsModel.stats(StatsKeyDef.GROUP_TAB_ESSENCE, StatsKeyDef.SPEC_KEY, mCircleId);
                }
            }
        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE) {
            updateItemsPraiseState((CommunityPostBean) notification.extObj);
        } else if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {

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
        for (int i = 0; i < mPostItems.size(); i++) {
            if (mPostItems.get(i).getItemType() != CommunityItemType.POST_TYPE_DEFAULT_DIVIDER) {
                tempPostBean = (CommunityPostBean) mPostItems.get(i).getData();
                if (tempPostBean.getPostId() == postBean.getPostId()) {
                    ((CommunityPostBean) mPostItems.get(i).getData()).setIsPraise(postBean.getIsPraise());
                    ((CommunityPostBean) mPostItems.get(i).getData()).setPraiseCount(postBean.getPraiseCount());
                }
            }
        }
        if (mPostAdapter != null) {
            mPostAdapter.notifyDataSetChanged();
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

    private void createLayout() {
        View view = View.inflate(mContext, R.layout.community_home_page_hot_post_fragment, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mParentView.addView(view, lp);
        initPostListLayoutView(view);
        tryGetAllPostsFirst();
    }


    private void initPostListLayoutView(View view) {
        mPostButton = (FloatingActionButton) view.findViewById(R.id.post_button);
        mPostButton.setVisibility(View.VISIBLE);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnPostButtonClick();
            }
        });
        mPostRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.hot_card_refresh_layout);
        mPostRefreshLayout.setVisibility(View.GONE);
        mPostLoadLayout = (LoadMoreListViewContainer) view.findViewById(R.id.hot_card_load_more_layout);
        mPostListView = (ListView) view.findViewById(R.id.hot_card_list);
        mDataLoadingLayout = (LoadingLayout) view.findViewById(R.id.no_data_bg);
        mDataLoadingLayout.setVisibility(View.VISIBLE);
        mDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        mDataLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetPosts(true, false);
            }
        });
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPostRefreshLayout.setLoadingMinTime(1000);
        mPostRefreshLayout.setDurationToCloseHeader(800);
        mPostRefreshLayout.setHeaderView(header);
        mPostRefreshLayout.addPtrUIHandler(header);
        mPostRefreshLayout.setPullToRefresh(false);
        mPostRefreshLayout.isKeepHeaderWhenRefresh();
        mPostRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                StatsModel.stats(StatsKeyDef.POST_LIST_REFRESH);
                tryGetPosts(false, false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mPostListView, header);
            }
        });

        mPostLoadLayout.useDefaultFooter();
        mPostLoadLayout.setAutoLoadMore(true);
        mPostLoadLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                StatsModel.stats(StatsKeyDef.POST_LIST_LOAD);
                tryGetPosts(false, true);
            }
        });
        mPostAdapter = new CommunityPostListAdapter(mContext, mPostItems);
        mPostListView.setAdapter(mPostAdapter);

        mPostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnListViewItemClick(position);
            }
        });
        updateAllPostButtonIcon();
    }

    private void handleOnListViewItemClick(int position) {
        String statsValue = String.valueOf(mCircleId);
        if (mCircle != null && StringUtils.isNotEmpty(mCircle.getCircleName())) {
            statsValue = mCircle.getCircleName();
        }
        StatsModel.stats(StatsKeyDef.GROUP_POST, StatsKeyDef.SPEC_KEY, statsValue);
        if (mPostType == CommunityConstant.GET_POST_LIST_HOTEST) {
            StatsModel.stats(StatsKeyDef.GROUP_POST_HOT, StatsKeyDef.SPEC_KEY, statsValue);
        } else if (mPostType == CommunityConstant.GET_POST_LIST_NEWEST) {
            StatsModel.stats(StatsKeyDef.GROUP_POST_NEW, StatsKeyDef.SPEC_KEY, statsValue);
        } else if (mPostType == CommunityConstant.GET_POST_LIST_CREAM) {
            StatsModel.stats(StatsKeyDef.GROUP_POST_ESSENCE, StatsKeyDef.SPEC_KEY, statsValue);
        }
        CommunityListItemInfo itemInfo = null;
        itemInfo = mPostItems.get(position);
        if (itemInfo.getData() != null) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
            message.arg1 = ((CommunityPostBean) itemInfo.getData()).getPostId();
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }

    private void updateAllPostButtonIcon() {
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mPostButton.setImageResource(R.drawable.community_question_edit_icon);
            return;
        }
        if (mCircle == null) {
            mPostButton.setImageResource(R.drawable.community_text_edit_icon);
            return;
        }
        if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_IMAGE) {
            mPostButton.setImageResource(R.drawable.community_image_edit_icon);
            return;
        }
        mPostButton.setImageResource(R.drawable.community_text_edit_icon);
    }

    private void handlerOnPostButtonClick() {
        //统计
        StatsModel.stats(StatsKeyDef.GROUP_WRITE_POST);

        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mCircle == null) {
            return;
        }
        mCircle.setCircleType(mCircleType);
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

    private void tryGetAllPostsFirst() {
        mDataLoadingLayout.setVisibility(View.VISIBLE);
        mDataLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mPostRefreshLayout.setVisibility(View.GONE);
        tryGetPosts(true, false);
    }

    private void tryGetPosts(final boolean isInit, final boolean isLoadMore) {
        if (mRequest == null) {
            return;
        }
        mRequest.startRequest(isLoadMore, new CommunityRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetPostFail(errorType, isInit, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList) {
                handlerGetPostsSuccess(newList, isInit, isLoadMore);
            }
        });
    }

    private void handlerGetPostsSuccess(ArrayList<CommunityListItemInfo> newList, boolean isInit, boolean isLoadMore) {
        mPostLoadLayout.loadMoreFinish(false, mRequest.hasMore());
        if (!isLoadMore) {
            mPostItems.clear();
        }
        mPostItems.addAll(newList);

        if (mPostAdapter != null) {
            mPostAdapter.notifyDataSetChanged();
        }
        if (mPostItems.isEmpty()) {
            mDataLoadingLayout.setVisibility(View.VISIBLE);
            mDataLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
            mPostRefreshLayout.setVisibility(View.GONE);
            return;
        }
        if (!isLoadMore && isInit) {
            mDataLoadingLayout.setVisibility(View.GONE);
            mDataLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mPostRefreshLayout.setVisibility(View.VISIBLE);
            mPostRefreshLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
            return;
        }
    }

    private void handlerGetPostFail(int errorCode, boolean isInit, boolean isLoadMore) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (isLoadMore) {
            mPostLoadLayout.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
            return;
        }
        if (isInit) {
            mDataLoadingLayout.setVisibility(View.VISIBLE);
            mPostRefreshLayout.setVisibility(View.GONE);
            if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
                mDataLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error), true);
            } else {
                mDataLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            }
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
        ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }
}
