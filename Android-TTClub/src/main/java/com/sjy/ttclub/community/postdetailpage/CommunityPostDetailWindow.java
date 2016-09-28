package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.CommunitySendCommentResultBean;
import com.sjy.ttclub.bean.community.ReportBean;
import com.sjy.ttclub.bean.community.TitleButtonBean;
import com.sjy.ttclub.collect.CollectRequest;
import com.sjy.ttclub.comment.PostCommentRequest;
import com.sjy.ttclub.comment.TopCommentsRequest;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.common.RespondCodeHelper;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.CommunityReportHelper;
import com.sjy.ttclub.community.widget.CommunityPostDetailToolsPanel;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.emoji.EmotionKeyBoard;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2015/12/28.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailWindow extends DefaultWindow {

    private ListView mPostDetailListView;
    private LoadingLayout mPostDetailLoadingLayout;
    private LoadMoreListViewContainer mLoadMoreContainer;
    private CommunityPostDetailAdapter mPostDetailAdapter;
    private CommunityPostBean mPostBean;
    private CommunityPostRequest mPostRequest;
    private TopCommentsRequest mTopCommentsRequest;
    private CommunityCommentsRequest mCommentsRequest;
    private List<CommentBean> mTopComments = new ArrayList<>();
    private List<CommentBean> mGeneralComments = new ArrayList<>();
    private TitleBar mTitleBar;
    private TitleBarActionItem mHostTitleBarActionItem;
    private TitleBarActionItem mTitleBarActionItem;
    private List<TitleBarActionItem> mTitleBarItems = new ArrayList<>();
    private int INIT_POST = 0;
    private int REFRESH_POST = 1;
    private int mPostId;
    private static final int ACTION_ITEM_TOOL = 0;
    private static final int ACTION_ITEM_HOST = 1;
    private TitleButtonBean mTitleButton;
    private CollectRequest mCollectRequest;
    final static int ORDER_TYPE_ASCEND = 1;
    final static int ORDER_TYPE_DROP = 2;
    final static int ORDER_TYPE_ONLY_HOST = 3;
    private static int loadMoreType;
    private CommunityReportHelper mReportHelper;
    private EmotionKeyBoard mEmotionKeyBoard;
    private PostCommentRequest mPostCommentRequest;
    private LinearLayout mTipCommentLayout;
    private SimpleDraweeView mTipCommentHeadIcon;
    private TextView mTipCommentContent;
    private CommentBean mTipComment;
    private FrameLayout mContentBg;

    public CommunityPostDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        createView();
        initRegisterNotify();
    }

    private void createView() {
        View parentView = View.inflate(getContext(), R.layout.community_post_detail_layout, null);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        loadMoreType = ORDER_TYPE_ASCEND;
        mContentBg = (FrameLayout) parentView.findViewById(R.id.content_bg);
        mContentBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerOnContentBgClick();
            }
        });
        mTipCommentLayout = (LinearLayout) parentView.findViewById(R.id.tip_comment_layout);
        mTipCommentHeadIcon = (SimpleDraweeView) parentView.findViewById(R.id.tip_comment_head_icon);
        mTipCommentContent = (TextView) parentView.findViewById(R.id.tip_comment_content);
        mEmotionKeyBoard = (EmotionKeyBoard) parentView.findViewById(R.id.emoji_keyboard);
        mEmotionKeyBoard.setEmotionButtonVisible(true);
        mEmotionKeyBoard.setOnPostButtonOnClickListener(new EmotionKeyBoard.OnPostButtonOnClickListener() {
            @Override
            public void onPostButtonClick() {
                handlerPostButtonOnClick();
            }
        });
        mPostDetailListView = (ListView) parentView.findViewById(R.id.post_detail_list_view);
        mPostDetailLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.loading_layout);
        mLoadMoreContainer = (LoadMoreListViewContainer) parentView.findViewById(R.id.list_view_container);
        mPostDetailLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mPostDetailLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetPostDetailContent(INIT_POST);
            }
        });
        mLoadMoreContainer.useDefaultFooter();
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                switch (loadMoreType) {
                    case ORDER_TYPE_ASCEND://升序加载更多
                        tryGetCommentsData(true);
                        break;
                    case ORDER_TYPE_DROP://加载更多降序
                        tryGetCommentsDataByDropOrder(true);
                        break;
                    case ORDER_TYPE_ONLY_HOST: //加载更多只看楼主
                        tryGetCommentsDataByHost(true);
                        break;
                    default:
                        break;
                }
                //统计post_load
                StatsModel.stats(StatsKeyDef.POST_LOAD);
            }
        });

    }

    public void setPostId(int postId) {
        this.mPostId = postId;
        mTitleButton = new TitleButtonBean(false, false, false);
        initRequest();
    }

    private void initRequest() {
        mPostRequest = new CommunityPostRequest(getContext(), mPostId);
        mTopCommentsRequest = new TopCommentsRequest(getContext(), mPostId);
        mCollectRequest = new CollectRequest(getContext(), CommonConst.COLLECT_TYPE_POST);
        mReportHelper = new CommunityReportHelper(getContext());
    }

    private void initUI() {
        if (mPostBean == null) {
            return;
        }
        setBarTitle();
        initTitleBarItem();
        mPostCommentRequest = new PostCommentRequest(getContext(), CommunityConstant.POST_TYPE_COMMENT, mPostId);
        mPostCommentRequest.setAutoToast(false);
        mCommentsRequest = new CommunityCommentsRequest(getContext(), mPostId, mPostBean.getUserId());
        mPostDetailAdapter = new CommunityPostDetailAdapter(getContext(), mPostBean);
        mPostDetailAdapter.setOnCommentItemClickListener(new CommunityPostDetailAdapter.OnCommentItemClickListener() {
            @Override
            public void onItemClick(CommentBean commentBean) {
                mTipComment = commentBean;
                handleSetTipCommentData(commentBean);
            }
        });
        mPostDetailAdapter.setTitleButton(mTitleButton);
        mPostDetailListView.setAdapter(mPostDetailAdapter);
        mPostDetailAdapter.updateItemList(mTopComments, mGeneralComments);
        tryGetTopCommentsData();
        tryGetCommentsData(false);
    }

    private void handlerOnContentBgClick() {
        mTipComment = null;
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                mContentBg.setVisibility(GONE);
                mTipCommentLayout.setVisibility(GONE);
            }
        },300);
        mEmotionKeyBoard.setEmotionEditHit(ResourceHelper.getString(R.string.community_post_comment_default_hit));
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.closeSoftware();
    }

    private void handleSetTipCommentData(CommentBean commentBean) {
        mTipCommentLayout.startAnimation(AnimotionDao.getAlphaAnimationShow(500));
        mTipCommentLayout.setVisibility(VISIBLE);
        if (StringUtils.isNotEmpty(commentBean.getContent())) {
            EmoticonsUtils.setContent(getContext(), mTipCommentContent, commentBean.getContent());
        }
        if (StringUtils.isNotEmpty(commentBean.getHeadimageUrl())) {
            mTipCommentHeadIcon.setImageURI(Uri.parse(commentBean.getHeadimageUrl()));
        }
        if (mEmotionKeyBoard == null) {
            return;
        }
        mEmotionKeyBoard.getmEmoticonsEditText().requestFocus();
        mEmotionKeyBoard.setEmotionEditHit("回复" + commentBean.getNickname());
        mEmotionKeyBoard.tryOpenSoftBorad();
    }

    private void setBarTitle() {
        if (mPostBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            setTitle(ResourceHelper.getString(R.string.community_qa_detail));
            //统计qa_post_view
            StatsModel.stats(StatsKeyDef.QUESTION_VIEW);
        } else {
            setTitle(ResourceHelper.getString(R.string.community_post_detail));
            //统计post_view
            StatsModel.stats(StatsKeyDef.POST_VIEW);
        }
    }

    private void initTitleBarItem() {
        mTitleBarItems.clear();
        mTitleBar = (TitleBar) getTitleBar();
        mHostTitleBarActionItem = new TitleBarActionItem(getContext());
        mHostTitleBarActionItem.setEnabled(true);
        mHostTitleBarActionItem.setItemId(ACTION_ITEM_HOST);
        if (mPostBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mHostTitleBarActionItem.setText(ResourceHelper.getString(R.string.community_qa_host));
        } else {
            mHostTitleBarActionItem.setText(ResourceHelper.getString(R.string.community_host));
        }
        mHostTitleBarActionItem.setTextBackground(ResourceHelper.getDrawable(R.drawable.community_post_detail_host_bg));
        mHostTitleBarActionItem.getTextView().setTextColor(ResourceHelper.getColorStateList(R.color.community_post_detail_host_text_color));
        mTitleBarItems.add(mHostTitleBarActionItem);
        mTitleBarActionItem = new TitleBarActionItem(getContext());
        mTitleBarActionItem.setEnabled(true);
        mTitleBarActionItem.setItemId(ACTION_ITEM_TOOL);
        mTitleBarActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.community_post_detail_tools_icon));
        mTitleBarItems.add(mTitleBarActionItem);
        mTitleBar.setActionItems(mTitleBarItems);
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_COMMUNITY_IS_SEND_SOME_RELPY);
        NotificationCenter.getInstance().register(this, NotificationDef.N_COMMUNITY_UPDATE_COMMENT_PRAISE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_EMOJINKEYBOARD_SHOW);
    }

    private void unRegisterNotify() {
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_COMMUNITY_IS_SEND_SOME_RELPY);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_COMMUNITY_UPDATE_COMMENT_PRAISE);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_EMOJINKEYBOARD_SHOW);
    }

    private void updateCommentItemReplys(CommentBean commentBean) {
        for (int i = 0; i < mGeneralComments.size(); i++) {
            if (commentBean.getCommentId() == mGeneralComments.get(i).getCommentId()) {
                int newAddReplyCount = (commentBean.getReplys().size()) - (mGeneralComments.get(i).getReplyCount());
                mGeneralComments.get(i).setReplys(commentBean.getReplys());
                if (commentBean.getReplys().size() > 2) {
                    mGeneralComments.get(i).setReplyCount(commentBean.getReplys().size());
                }
                updatePostReplyCount(newAddReplyCount);
            }
        }
        for (int i = 0; i < mTopComments.size(); i++) {
            if (commentBean.getCommentId() == mTopComments.get(i).getCommentId()) {
                mTopComments.get(i).setReplys(commentBean.getReplys());
                if (commentBean.getReplys().size() > 2) {
                    mTopComments.get(i).setReplyCount(commentBean.getReplys().size());
                }
            }
        }
        if (mPostDetailAdapter != null) {
            mPostDetailAdapter.updateItemList(mTopComments, mGeneralComments);
        }
    }

    private void updatePostReplyCount(int newAddReplyCount) {
        if (mPostBean == null) {
            return;
        }
        mPostBean.setReplyCount(mPostBean.getReplyCount() + newAddReplyCount);
    }

    private void updateItemsPraiseCount(CommentBean commentBean) {
        for (int i = 0; i < mGeneralComments.size(); i++) {
            if (commentBean.getCommentId() == mGeneralComments.get(i).getCommentId()) {
                mGeneralComments.get(i).setPraiseCount(commentBean.getPraiseCount());
            }
        }
        for (int i = 0; i < mTopComments.size(); i++) {
            if (commentBean.getCommentId() == mTopComments.get(i).getCommentId()) {
                mTopComments.get(i).setPraiseCount(commentBean.getPraiseCount());
            }
        }
        if (mPostDetailAdapter != null) {
            mPostDetailAdapter.updateItemList(mTopComments, mGeneralComments);
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_COMMUNITY_IS_SEND_SOME_RELPY) {
            updateCommentItemReplys((CommentBean) notification.extObj);
        } else if (notification.id == NotificationDef.N_COMMUNITY_UPDATE_COMMENT_PRAISE) {
            updateItemsPraiseCount((CommentBean) notification.extObj);
        } else if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            tryGetPostDetailContent(REFRESH_POST);
        } else if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_HIDE) {
            mContentBg.setVisibility(GONE);
            mTipCommentLayout.setVisibility(GONE);
        } else if (notification.id == NotificationDef.N_EMOJINKEYBOARD_SHOW) {
            if (mTipComment != null) {
                mTipCommentLayout.setVisibility(VISIBLE);
            } else {
                mTipCommentLayout.setVisibility(GONE);
            }
            mContentBg.setVisibility(VISIBLE);
            mContentBg.startAnimation(AnimotionDao.getAlphaAnimationShow(500));
        } else if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_SHOW) {
            mContentBg.setVisibility(VISIBLE);
            mContentBg.startAnimation(AnimotionDao.getAlphaAnimationShow(500));
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        switch (itemId) {
            case ACTION_ITEM_TOOL:
                tryBiuldToolsWindow();
                break;
            case ACTION_ITEM_HOST:
                handleFloorHostButtonOnClik();
                break;
        }
    }

    @Override
    public void onBackActionButtonClick() {
        unRegisterNotify();
        super.onBackActionButtonClick();
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                tryGetPostDetailContent(INIT_POST);
                break;
            case STATE_BEFORE_POP_OUT:
                unRegisterNotify();
                break;
            case STATE_ON_HIDE:
                DeviceManager.getInstance().hideInputMethod();
                break;
            case STATE_AFTER_POP_OUT:
                DeviceManager.getInstance().hideInputMethod();
                break;
        }
    }

    /**
     * 修改button状态
     *
     * @param isHost
     * @param isSee
     */
    private void changeButtonState(boolean isHost, boolean isSee) {
        mTitleButton.setHostSelected(isHost);
        mTitleButton.setSeeSelected(isSee);
    }

    private void changeHostButtonState(View view) {
        if (view == null) {
            return;
        }
        if (mTitleButton.isHostSelected()) {
            view.setSelected(true);
            return;
        }
        view.setSelected(false);
    }

    private void tryBiuldToolsWindow() {
        if (mPostBean == null) {
            return;
        }
        CommunityPostDetailToolsPanel toolsPanel = new CommunityPostDetailToolsPanel(getContext(), mPostBean, mTitleButton);
        toolsPanel.setMoreSeeListener(new CommunityPostDetailToolsPanel.OnMoreSeeClickListener() {
            @Override
            public void onMoreSeeClick(TextView seeText) {
                handlerOnMoreSeeClick();
            }

            @Override
            public void onCollectClick(TextView collectText) {
                hanlerOnCollectClick(collectText);
            }

            @Override
            public void onReportClick() {
                handleOnReportClick();
            }
        });
        toolsPanel.showPanel();
    }

    private void handleFloorHostButtonOnClik() {
        StatsModel.stats(StatsKeyDef.POST_COMMENT_MASTER);
        if (!mTitleButton.isHostSelected()) {
            loadMoreType = ORDER_TYPE_ONLY_HOST;
            changeButtonState(true, false);
            changeHostButtonState(mHostTitleBarActionItem);
            tryGetCommentsDataByHost(false);
            toastOnlySeeHostCommentTip();
            trySmoothScrollToComments();
        } else {
            loadMoreType = ORDER_TYPE_ASCEND;
            changeButtonState(false, false);
            changeHostButtonState(mHostTitleBarActionItem);
            tryGetCommentsData(false);
            trySmoothScrollToComments();
        }
    }

    private void toastOnlySeeHostCommentTip() {
        if (mPostBean == null) {
            return;
        }
        if (mPostBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            ToastHelper.showToast(getContext(), "已切换到只看题主", Toast
                    .LENGTH_SHORT);
            return;
        }
        ToastHelper.showToast(getContext(), "已切换到只看楼主", Toast
                .LENGTH_SHORT);
    }

    private void handlerOnMoreSeeClick() {
        StatsModel.stats(StatsKeyDef.POST_COMMENT_DESC);
        if (!mTitleButton.isSeeSelected()) {
            changeButtonState(false, true);
            changeHostButtonState(mHostTitleBarActionItem);
            tryGetCommentsDataByDropOrder(false);
            loadMoreType = ORDER_TYPE_DROP;
            trySmoothScrollToComments();
        } else {
            changeButtonState(false, false);
            changeHostButtonState(mHostTitleBarActionItem);
            tryGetCommentsData(false);
            loadMoreType = ORDER_TYPE_ASCEND;
            trySmoothScrollToComments();
        }
    }

    private void trySmoothScrollToComments() {
        if (mPostDetailListView == null) {
            return;
        }
        if (mPostBean == null) {
            return;
        }
        if (mPostBean.getImages() == null) {
            return;
        }
        if (mTopComments.size() > 0) {
            mPostDetailListView.setSelection(mPostBean.getImages().size() + mTopComments.size() + 3);
            return;
        }
        mPostDetailListView.setSelection(mPostBean.getImages().size() + 2);
    }

    private void hanlerOnCollectClick(final TextView collectText) {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mPostBean == null) {
            return;
        }
        if (mPostBean.getIsCollect() == CommunityConstant.HAVE_COLLECT_FLAG) {
            tryCancelCollectRequest(collectText);
            return;
        }
        tryAddCollectRequest(collectText);
    }


    private void tryAddCollectRequest(final TextView textView) {
        mCollectRequest.addColloectRequest(mPostId, new CollectRequest.CollectRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_add_collect_fail_tip),
                        Toast.LENGTH_SHORT);
            }

            @Override
            public void onResultSuccess() {
                textView.setSelected(true);
                textView.setText(ResourceHelper.getString(R.string.community_cancel_collect));
                mPostBean.setIsCollect(CommunityConstant.HAVE_COLLECT_FLAG);
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_add_collect_tip), Toast
                        .LENGTH_SHORT);
            }
        });
    }

    private void tryCancelCollectRequest(final TextView textView) {
        mCollectRequest.cancelCollectRequest(mPostId, new CollectRequest.CollectRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_cancel_collect_fail_tip)
                        , Toast.LENGTH_SHORT);
            }

            @Override
            public void onResultSuccess() {
                textView.setSelected(false);
                mPostBean.setIsCollect(CommunityConstant.NO_COLLECT_FLAG);
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_cancel_collect_tip),
                        Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 打开举报窗口
     */
    private void handleOnReportClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mPostBean == null) {
            return;
        }

        if (!mReportHelper.reportPostReasonIsSaveInLocal(getContext())) {
            mReportHelper.getReportReasonsListFromServer(getContext());
            return;
        }
        ActionSheetPanel panel = new ActionSheetPanel(getContext());
        final List<ReportBean> reportBeans = mReportHelper.getReportPostList(getContext());
        ActionSheetPanel.ActionSheetItem item;
        for (ReportBean reportBean : reportBeans) {
            item = new ActionSheetPanel.ActionSheetItem();
            item.title = reportBean.getReportReason();
            item.id = reportBean.getReportValue();
            panel.addSheetItem(item);
        }
        panel.setSheetItemClickListener(new ActionSheetPanel.OnActionSheetClickListener() {
            @Override
            public void onActionSheetItemClick(String id) {
                if (id == null) {
                    return;
                }

                mReportHelper.sendReport(getContext(),
                        mPostBean.getUserId(),
                        mPostBean.getPostId(), mReportHelper.REPORT_POST, id);

            }
        });
        panel.showPanel();
    }

    private void tryGetPostDetailContent(final int initRequestflag) {
        if (mPostRequest == null) {
            return;
        }
        if (initRequestflag == INIT_POST) {
            mLoadMoreContainer.setVisibility(GONE);
            mPostDetailLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
            mPostDetailLoadingLayout.setVisibility(VISIBLE);
        }
        mPostRequest.startRequest(new CommunityPostRequest.RequestPostResultCallback() {
            @Override
            public void onResultFailed(int errorCode) {
                handleGetCardFailed(errorCode, initRequestflag);
            }

            @Override
            public void onResultSuccess(CommunityPostBean postBean) {
                handleGetCardSuccess(postBean, initRequestflag);
            }
        });
    }

    private void handleGetCardFailed(int errorCode, int flag) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (flag == INIT_POST) {
            mPostDetailLoadingLayout.setVisibility(View.VISIBLE);
            mEmotionKeyBoard.setVisibility(View.GONE);
            mLoadMoreContainer.setVisibility(View.GONE);
        } else {
            mPostDetailLoadingLayout.setVisibility(View.GONE);
            mEmotionKeyBoard.setVisibility(View.VISIBLE);
            mLoadMoreContainer.setVisibility(View.VISIBLE);
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            if (flag == INIT_POST) {
                mPostDetailLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), false);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            if (flag == INIT_POST) {
                mPostDetailLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry),
                        true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error_retry), Toast
                        .LENGTH_SHORT);
            }
            return;
        }
        mPostDetailLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), false);
    }

    private void handleGetCardSuccess(CommunityPostBean postBean, int flag) {
        if (flag == INIT_POST) {
            mPostDetailLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mPostDetailLoadingLayout.setVisibility(View.GONE);
            mEmotionKeyBoard.setVisibility(View.VISIBLE);
            mEmotionKeyBoard.startAnimation(AnimotionDao.getTranslateUpVisible());
            mLoadMoreContainer.startAnimation(AnimotionDao.getAlphaAnimationShow());
            mLoadMoreContainer.setVisibility(View.VISIBLE);
        }
        mPostBean = postBean;
        initUI();

    }

    private void tryGetTopCommentsData() {
        if (mTopCommentsRequest == null) {
            return;
        }
        mTopCommentsRequest.startHotRequest(false, new TopCommentsRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetCommentFail(errorType, false);
            }

            @Override
            public void onResultSuccess(ArrayList<CommentBean> allList, ArrayList<CommentBean> newList) {
                handlerGetHotCommentSuccess(newList);
            }
        });
    }

    private void tryGetCommentsData(final boolean isLoadMore) {
        mCommentsRequest.startRequest(isLoadMore, new CommunityCommentsRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetCommentFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommentBean> allList, ArrayList<CommentBean> newList) {
                handlerGetCommentSuccess(newList, isLoadMore);
            }
        });
    }

    /**
     * 获取只看楼主的评论
     *
     * @param isLoadMore
     */
    private void tryGetCommentsDataByHost(final boolean isLoadMore) {
        mCommentsRequest.startRequestByHost(isLoadMore, new CommunityCommentsRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetCommentFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommentBean> allList, ArrayList<CommentBean>
                    newList) {
                handlerGetCommentSuccess(newList, isLoadMore);
            }
        });
    }

    /**
     * 获取降序评论
     *
     * @param isLoadMore
     */
    private void tryGetCommentsDataByDropOrder(final boolean isLoadMore) {
        mCommentsRequest.startRequestByDropOrder(isLoadMore, new CommunityCommentsRequest
                .RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetCommentFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommentBean> allList, ArrayList<CommentBean>
                    newList) {
                handlerGetCommentSuccess(newList, isLoadMore);
            }
        });
    }

    private void handlerGetCommentFail(int errorCode, boolean isLoadMore) {
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA && isLoadMore) {
            mLoadMoreContainer.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error_retry), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
    }

    private void handlerGetHotCommentSuccess(ArrayList<CommentBean> dataList) {
        if (!mTopComments.isEmpty()) {
            mTopComments.clear();
        }
        mTopComments.addAll(dataList);
        mPostDetailAdapter.updateItemList(mTopComments, mGeneralComments);
    }

    private void handlerGetCommentSuccess(ArrayList<CommentBean> dataList, boolean isLoadMore) {
        if (!isLoadMore) {
            mGeneralComments.clear();
        }
        mGeneralComments.addAll(dataList);
        if (mGeneralComments.size() > 0) {
            mLoadMoreContainer.loadMoreFinish(false, mCommentsRequest.hasMore());
        }
        mPostDetailAdapter.updateItemList(mTopComments, mGeneralComments);
    }

    private void handlerPostButtonOnClick() {
        StatsModel.stats(StatsKeyDef.POST_REPLY);
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getLevel()) < 1) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_level_not_enough_one), Toast.LENGTH_SHORT);
            return;
        }
        if (mPostBean == null) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_psot_failed)
                    , Toast.LENGTH_SHORT);
            return;
        }
        if (getEditContent().length() == 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_no_content_tip), Toast.LENGTH_SHORT);
            return;
        }
        if (StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getSex()) == CommonConst.SEX_WOMAN) {
            sendComment();
            return;
        }
        if (mPostBean.getIsLimitMale() != 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_limit_man_comment), Toast.LENGTH_SHORT);
            return;
        }
        sendComment();
    }

    /**
     * 获取编辑框里的内容
     *
     * @return
     */
    private String getEditContent() {
        return mEmotionKeyBoard.getEmotionEditContent();
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        int contentId = 0;
        int postType = CommunityConstant.POST_TYPE_COMMENT;
        if (mTipComment != null) {
            contentId = mTipComment.getCommentId();
            postType = CommunityConstant.POST_TYPE_REPLY;
        }
        mPostCommentRequest.startCommentRequest(postType, CommunityConstant.CONTENT_TYPE_CARD, contentId, getEditContent(), new
                PostCommentRequest.PostRequestResultCallback() {
                    @Override
                    public void onResultFailed(int errorCode) {
                        handleSendCommentFail(errorCode);
                    }

                    @Override
                    public void onResultSuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
                        handleSendCommentSuccess(resultBean);
                    }
                });
    }

    private void handleSendCommentFail(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_psot_failed), Toast.LENGTH_SHORT);
            return;
        }
        RespondCodeHelper.handlerResultStatus(getContext(), errorCode);
    }

    private void handleSendCommentSuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
        if (resultBean.getDayReplyCount() <= resultBean.getMaxRewardTimes()) {
            if (resultBean.getIsLevelUp() == 0) {
                ToastHelper.showToast(getContext(), "回帖成功！", "经验+" + resultBean.getObtainedExp() + "(" + resultBean
                        .getDayReplyCount() + "/" + resultBean.getMaxRewardTimes() + ")");
            } else {
                ToastHelper.showToast(getContext(), "回帖成功！", "升到" + resultBean.getCurrentLevel() + "级");
            }
        }
        if (loadMoreType == ORDER_TYPE_ASCEND) {
            tryGetCommentsData(true);
        } else {
            loadMoreType = ORDER_TYPE_ASCEND;
            changeButtonState(false, false);
            changeHostButtonState(mHostTitleBarActionItem);
            tryGetCommentsData(false);
        }
        mPostBean.setReplyCount(mPostBean.getReplyCount() + 1);
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.closeSoftware();
        mEmotionKeyBoard.setEmotionEditHit(ResourceHelper.getString(R.string.community_post_comment_default_hit));
        mTipCommentLayout.setVisibility(GONE);
        mContentBg.setVisibility(GONE);
        mTipComment = null;
    }

}
