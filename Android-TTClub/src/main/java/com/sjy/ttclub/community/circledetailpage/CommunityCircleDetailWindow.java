package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityTempDataHelper;
import com.sjy.ttclub.community.allcirclespage.CommunityCirlceClickEventRequest;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.TabWindow;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.widget.TabPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwl on 2015/11/26.
 * Email: 1501448275@qq.com
 */
public class CommunityCircleDetailWindow extends TabWindow implements IMainTabView, TabPager.IScrollable {
    private int mCircleId;
    private int mCircleType;
    private CommunityPostTabView mAllQaPostView;
    private CommunityPostTabView mNewQaPostView;
    private CommunityPostTabView mCreamQaPostView;
    private CommunityPostTabView mHotPostView;
    private CommunityPostTabView mNewPostView;
    private CommunityPostTabView mCreamPostView;

    private TitleBar mTitleBar;
    private TitleBarActionItem mTitleBarActionItem;
    private List<TitleBarActionItem> mTitleBarItems = new ArrayList<>();
    private CommunityCircleInfoRequest mCircleInfoRequest;
    private CommunityCircleBean mCircle;
    private static final int ACTION_ITEM_SHARE = 0;
    private static final int ACTION_ITEM_CIRCLE_DETAIL = 1;

    public CommunityCircleDetailWindow(Context context, IDefaultWindowCallBacks callBacks, int circleType) {
        super(context, callBacks);
        setEnableSwipeGesture(true);
        setTabbarInTitlebar(false);
        mCircleInfoRequest = new CommunityCircleInfoRequest(getContext());
        this.mCircleType = circleType;
        initRegisterNotify();
        StatsModel.stats(StatsKeyDef.POST_LIST_VIEW);
    }

    public void setCirlceInfo(int circleId) {
        this.mCircleId = circleId;
        tryGetCircleInfo();
        initViewByType();
        tryAddCircleClickEventStats();
        initTitleBarItem();
    }

    private void tryAddCircleClickEventStats() {
        CommunityCirlceClickEventRequest clickEventRequest = new CommunityCirlceClickEventRequest();
        if (!AccountManager.getInstance().isLogin()) {
            return;
        }
        if (AccountManager.getInstance().getAccountInfo() == null) {
            return;
        }
        clickEventRequest.startRequest(StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getUserId()), mCircleId);
    }

    private void initTitleBarItem() {
        mTitleBar = (TitleBar) getTitleBar();
        mTitleBarActionItem = new TitleBarActionItem(getContext());
        mTitleBarActionItem.setEnabled(true);
        mTitleBarActionItem.setItemId(ACTION_ITEM_CIRCLE_DETAIL);
        mTitleBarActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.community_circle_detail_info_icon));
        mTitleBarItems.add(mTitleBarActionItem);
        mTitleBarActionItem = new TitleBarActionItem(getContext());
        mTitleBarActionItem.setEnabled(true);
        mTitleBarActionItem.setItemId(ACTION_ITEM_SHARE);
        mTitleBarActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.title_share_icon));
        mTitleBarItems.add(mTitleBarActionItem);
        mTitleBar.setActionItems(mTitleBarItems);
    }

    private void initViewByType() {
        if (mCircleType == CommunityConstant.CIRCLE_TYPE_POST) {
            createPostTab();
        } else {
            createQaPostTab();
        }
    }

    private void createPostTab() {
        mHotPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_HOTEST,
                ResourceHelper.getString(R.string.community_hot));
        mHotPostView.setCircleId(mCircleId);
        addTab(mHotPostView);
        mNewPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_NEWEST,
                ResourceHelper.getString(R.string.community_new));
        mNewPostView.setCircleId(mCircleId);
        addTab(mNewPostView);
        mCreamPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_CREAM,
                ResourceHelper.getString(R.string.community_cream));
        mCreamPostView.setCircleId(mCircleId);
        addTab(mCreamPostView);
    }

    private void createQaPostTab() {
        mAllQaPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_HOTEST,
                ResourceHelper.getString(R.string.community_hot));
        mAllQaPostView.setCircleId(mCircleId);
        addTab(mAllQaPostView);
        mNewQaPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_NEWEST,
                ResourceHelper.getString(R.string.community_new));
        mNewQaPostView.setCircleId(mCircleId);
        addTab(mNewQaPostView);
        mCreamQaPostView = new CommunityPostTabView(getContext(), mCircleType, CommunityConstant.GET_POST_LIST_CREAM,
                ResourceHelper.getString(R.string.community_cream));
        mCreamQaPostView.setCircleId(mCircleId);
        addTab(mCreamQaPostView);
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_NETWORK_STATE_CHANGE);
    }

    private void unRegisterNotify() {
        if (mHotPostView != null) {
            mHotPostView.unRegisterNotify();
        }
        if (mNewPostView != null) {
            mHotPostView.unRegisterNotify();
        }
        if (mCreamPostView != null) {
            mCreamPostView.unRegisterNotify();
        }
        if (mAllQaPostView != null) {
            mAllQaPostView.unRegisterNotify();
        }
        if (mCreamQaPostView != null) {
            mCreamQaPostView.unRegisterNotify();
        }
        if (mNewQaPostView != null) {
            mNewQaPostView.unRegisterNotify();
        }
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_NETWORK_STATE_CHANGE);
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                break;
            case STATE_BEFORE_POP_OUT:
                unRegisterNotify();
                break;
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        switch (itemId) {
            case ACTION_ITEM_SHARE:
                showShareWindow();
                break;
            case ACTION_ITEM_CIRCLE_DETAIL:
                showCircleInfoDetailWindow();
                break;
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_NETWORK_STATE_CHANGE) {
            tryGetCircleInfo();
        }
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == TAB_TO_SHOW) {
//            if (getCurrentTabIndex() == CommunityConstant.COMMUNITY_TAB_INDEX_NEWEST) {
//                if (mCircle != null && StringUtils.isNotEmpty(mCircle.getCircleName())) {
//                    StatsModel.stats(StatsKeyDef.GROUP_TAB_NEW, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
//                } else {
//                    StatsModel.stats(StatsKeyDef.GROUP_TAB_NEW, StatsKeyDef.SPEC_KEY, mCircleId);
//                }
//            } else if (getCurrentTabIndex() == CommunityConstant.COMMUNITY_TAB_INDEX_CREAM) {
//                if (mCircle != null && StringUtils.isNotEmpty(mCircle.getCircleName())) {
//                    StatsModel.stats(StatsKeyDef.GROUP_TAB_ESSENCE, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
//                } else {
//                    StatsModel.stats(StatsKeyDef.GROUP_TAB_ESSENCE, StatsKeyDef.SPEC_KEY, mCircleId);
//                }
//            }
        }
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public boolean canScroll(boolean scrollLeft) {
        return false;
    }

    private void showCircleInfoDetailWindow() {
        if(mCircle==null){
            return;
        }
        if (StringUtils.isNotEmpty(mCircle.getCircleName())) {
            StatsModel.stats(StatsKeyDef.GROUP_INTRODUCTION, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
        } else {
            StatsModel.stats(StatsKeyDef.GROUP_INTRODUCTION, StatsKeyDef.SPEC_KEY, mCircleId);
        }
        Message message = Message.obtain();
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = ResourceHelper.getString(R.string.community_circle_info_title);
        params.url = createCircleDetailUrl();
        message.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        message.obj = params;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private String createCircleDetailUrl() {
        String mCircleDetailUrl = "";
        if (mCircle != null) {
            mCircleDetailUrl = HttpUrls.CIRCLE_DETAIL_URL + mCircle.getCircleId();
        }
        return mCircleDetailUrl;
    }

    private void tryGetCircleInfo() {
        mCircleInfoRequest.startRequestById(mCircleId, new CommunityCircleInfoRequest.RequestCircleInfoCallback() {
            @Override
            public void onResultFail(int errorCode) {
                handlerGetCircleInfoFail(errorCode);
            }

            @Override
            public void onResultSuccess(CommunityCircleBean circle) {
                handlerGetCircleInfoSuccess(circle);
            }
        });
    }

    private void handlerGetCircleInfoFail(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_CIRCEL_NOT_EXIST) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_get_circle_not_exist), Toast.LENGTH_SHORT);
        } else {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_get_circle_info_fail), Toast.LENGTH_SHORT);
        }
    }

    private void handlerGetCircleInfoSuccess(CommunityCircleBean circle) {
        this.mCircle = circle;
        CommunityTempDataHelper.getInstance().setmTempCircle(circle);
        Notification notification = Notification.obtain(NotificationDef.N_COMMUNITY_SHOW_TIPS, circle);
        NotificationCenter.getInstance().notify(notification);
        if (mCircle == null) {
            return;
        }
        if (mHotPostView != null) {
            mHotPostView.setCircleInfo(mCircle);
        }
        if (mAllQaPostView != null) {
            mAllQaPostView.setCircleInfo(mCircle);
        }
        if (mCreamPostView != null) {
            mCreamPostView.setCircleInfo(mCircle);
        }
        if (mCreamQaPostView != null) {
            mCreamQaPostView.setCircleInfo(mCircle);
        }
        if (mNewPostView != null) {
            mNewPostView.setCircleInfo(mCircle);
        }
        if (mNewQaPostView != null) {
            mNewQaPostView.setCircleInfo(mCircle);
        }
        if (StringUtils.isEmpty(mCircle.getCircleName())) {
            setTitle(ResourceHelper.getString(R.string.community_circle_detail));
            StatsModel.stats(StatsKeyDef.GROUP_VIEW, StatsKeyDef.SPEC_KEY, mCircleId);
        } else {
            setTitle(mCircle.getCircleName());
            //统计
            StatsModel.stats(StatsKeyDef.GROUP_VIEW, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
        }
    }

    private void showShareWindow() {
        if (mCircle == null) {
            return;
        }
        String shareTitle = ResourceHelper.getString(R.string.community_share_title_add_string)
                + mCircle.getCircleName()
                + ResourceHelper.getString(R.string.community_share_title_add_string_at_bottom);
        String shareContent = mCircle.getTheme()
                + ResourceHelper.getString(R.string.community_share_content_add_string);
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareImageUrl(mCircle.getIconUrl());
        builder.setShareTitle(shareTitle);
        builder.setShareContent(shareContent);
        Bundle shareBundle = new Bundle();
        String type = "group";
        if(mCircleType == CommunityConstant.CIRCLE_TYPE_QA_POST){
            type = "qa";
        }
        shareBundle.putString("type", type);
        String title = mCircle.getCircleName();
        if(StringUtils.isEmpty(title)){
            title = String.valueOf(mCircle.getCircleId());
        }
        shareBundle.putString("spec", title);
        builder.setStatsBundle(shareBundle);
        String shareUrl = HttpUrls.SHARE_CIRCLE_URL + mCircleId;
        builder.setShareUrl(shareUrl);
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHARE;
        message.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
