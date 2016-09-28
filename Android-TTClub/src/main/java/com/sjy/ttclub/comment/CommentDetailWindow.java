package com.sjy.ttclub.comment;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.bean.community.CommunitySendCommentResultBean;
import com.sjy.ttclub.bean.community.ReportBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.common.RespondCodeHelper;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityTempDataHelper;
import com.sjy.ttclub.community.model.CommunityReportHelper;
import com.sjy.ttclub.community.widget.CommunityCommentRuleTipPanel;
import com.sjy.ttclub.emoji.EmotionKeyBoard;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2015/12/29.
 * email 1501448275@qq.com
 */
public class CommentDetailWindow extends DefaultWindow {
    private ListView mReplyListView;
    private LoadMoreListViewContainer mLoadMoreContainer;
    private LoadingLayout mLoadingLayout;
    private CommentView mCommentView;
    private LinearLayout mContentLinearLayout;
    private CommentReplyRequest mReplyRequest;
    private int mCommentType;
    private int mCommentId;
    private CommentBean mComment;
    private CommentDetailAdapter mCommentDetailAdapter;
    private List<CommentReplyBean> mReplies = new ArrayList<>();
    private int mIsFirstShowRuleTip = 1;
    private static final String SETTING_KEY = "mIsFirstShowRuleTip";
    private Context mContext;
    private TitleBar mTitleBar;
    private TitleBarActionItem mTitleBarActionItem;
    private List<TitleBarActionItem> mTitleBarItems = new ArrayList<>();
    private static final int ACTION_REPORT = 0;
    private CommunityReportHelper mReportHelper;
    private PostCommentRequest mPostCommentRequest;
    private EmotionKeyBoard mEmotionKeyBoard;
    private boolean mIsSendSomeReply = false;
    private static boolean mIsReplyToSomeOne = false;
    private static CommentReplyBean mTempReplyBean;
    private ActionSheetPanel panel;

    public CommentDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        this.mContext = context;
        initSettingFlags();
        createView();
    }

    public void setCreateInfo(int commentId, int commentType) {
        this.mCommentId = commentId;
        this.mCommentType = commentType;
        initRequest();
    }

    private void initRequest() {
        mReplyRequest = new CommentReplyRequest(getContext(), mCommentType, mCommentId);
        mReportHelper = new CommunityReportHelper(getContext());
    }

    private void createView() {
        View parentView = View.inflate(getContext(), R.layout.community_post_comment_reply_layout, null);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());

        mEmotionKeyBoard = (EmotionKeyBoard) parentView.findViewById(R.id.emoji_keyboard);
        mEmotionKeyBoard.setEmotionButtonVisible(true);
        mEmotionKeyBoard.setOnPostButtonOnClickListener(new EmotionKeyBoard.OnPostButtonOnClickListener() {
            @Override
            public void onPostButtonClick() {
                handlerPostButtonOnClick();
            }
        });
        mLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.loading_layout);
        mReplyListView = (ListView) parentView.findViewById(R.id.community_comment_reply_list);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetCommentData();
            }
        });
        mLoadMoreContainer = (LoadMoreListViewContainer) parentView.findViewById(R.id.list_view_container);
        mLoadMoreContainer.useDefaultFooter();
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {

            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetReplyDatas(true);
            }
        });
    }

    private void initSettingFlags() {
        mIsFirstShowRuleTip = SettingFlags.getIntFlag(SETTING_KEY, 0);
    }

    private void initCommentRulesTip(CommentBean commentBean) {
        if (mIsFirstShowRuleTip == 0) {
            CommunityCommentRuleTipPanel commentRuleTipPanel = new CommunityCommentRuleTipPanel(mContext, commentBean.getCircleId());
            commentRuleTipPanel.showPanel();
        }
    }

    private void initTitleBarItem() {
        mTitleBar = (TitleBar) getTitleBar();
        mTitleBarActionItem = new TitleBarActionItem(getContext());
        mTitleBarActionItem.setEnabled(true);
        mTitleBarActionItem.setItemId(ACTION_REPORT);
        mTitleBarActionItem.setDrawable(ResourceHelper.getDrawable(R.drawable.community_comment_report));
        mTitleBarItems.add(mTitleBarActionItem);
        mTitleBar.setActionItems(mTitleBarItems);
    }

    private void initUI(CommentBean commentBean) {
        if (commentBean == null) {
            return;
        }
        initCommentRulesTip(commentBean);
        initTitleBarItem();
        if (!StringUtils.isEmpty(commentBean.getNickname())) {
            commentBean.setNickname(commentBean.getNickname().replace("\n", ""));
        }
        mPostCommentRequest = new PostCommentRequest(getContext(), CommunityConstant.POST_TYPE_REPLY, commentBean.getPostId());
        mPostCommentRequest.setAutoToast(false);
        mCommentDetailAdapter = new CommentDetailAdapter(getContext(), mCommentType);
        mReplyListView.setAdapter(mCommentDetailAdapter);
        mCommentDetailAdapter.updateDetailList(commentBean, mReplies);
        mCommentDetailAdapter.setOnListViewItemClickListener(new CommentDetailAdapter.OnListItemClickListener() {
            @Override
            public void onCommentViewClick() {
                resetReplyType();
            }

            @Override
            public void onReplyViewClick(CommentReplyBean replyBean) {
                setReplyTipsInEdit(replyBean);
            }
        });
        setTitle("评论 " + commentBean.getNickname());
        tryGetReplyDatas(false);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                tryGetCommentData();
                break;
            case STATE_BEFORE_POP_OUT:
                handlerOnCloseCurrentWindowToNotifyIsSendReply();
                if (mEmotionKeyBoard != null) {
                    mEmotionKeyBoard.closeSoftware();
                }
                break;
            case STATE_AFTER_POP_OUT:
                break;
            case STATE_ON_HIDE:
                break;
        }
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        switch (itemId) {
            case ACTION_REPORT:
                handlerOnReportClick();
                break;
        }
    }

    private void handlerOnReportClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mComment == null) {
            return;
        }
        if (!mReportHelper.reportPostReasonIsSaveInLocal(getContext())) {
            mReportHelper.getReportReasonsListFromServer(getContext());
            return;
        }
        if (panel != null && panel.isShowing()) {
            return;
        }
        panel = new ActionSheetPanel(getContext());
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
                        mComment.getUserId(),
                        mComment.getPostId(), mReportHelper.REPORT_REPLY, id);

            }
        });
        panel.showPanel();
    }

    /**
     * 获取评论详情
     */
    private void tryGetCommentData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mLoadMoreContainer.setVisibility(View.GONE);
        mReplyRequest.startRequestById(new CommentReplyRequest.RequestCommentResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handleGetCommentFailed(errorType);
            }

            @Override
            public void onResultSuccess(CommentBean commentBean) {
                mLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
                mLoadMoreContainer.startAnimation(AnimotionDao.getAlphaAnimationShow());
                mEmotionKeyBoard.setVisibility(VISIBLE);
                mEmotionKeyBoard.startAnimation(AnimotionDao.getTranslateUpVisible());
                mLoadingLayout.setVisibility(View.GONE);
                mLoadMoreContainer.setVisibility(View.VISIBLE);
                mComment = commentBean;
                initUI(mComment);
            }
        });
    }

    private void handleGetCommentFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            mLoadMoreContainer.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            mLoadMoreContainer.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_COMMENT_NOT_EXIST) {
            mLoadMoreContainer.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.community_data_not_exist), true);
            return;
        }
    }

    /**
     * 获取回复列表
     *
     * @param isLoadMore
     */
    private void tryGetReplyDatas(final boolean isLoadMore) {
        mReplyRequest.startRequest(isLoadMore, new CommentReplyRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetDataFail(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommentReplyBean> allList, ArrayList<CommentReplyBean> newList) {
                handlerGetDataSuccess(newList, isLoadMore);
            }
        });
    }

    /**
     * 获取数据异常处理
     *
     * @param errorCode
     */
    private void handlerGetDataFail(int errorCode, boolean isLoadMore) {
        if (isLoadMore) {
            mLoadMoreContainer.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
    }

    private void handlerGetDataSuccess(ArrayList<CommentReplyBean> dataList, boolean isLoadMore) {
        mLoadMoreContainer.loadMoreFinish(false, mReplyRequest.hasMore());
        if (!isLoadMore) {
            mReplies.clear();
        }
        mReplies.addAll(dataList);
        if (mCommentDetailAdapter != null) {
            mCommentDetailAdapter.updateDetailList(mComment, mReplies);
        }
    }

    private void handlerPostButtonOnClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (StringUtils.parseInt(info.getLevel()) < 1) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_level_not_enough_one), Toast.LENGTH_SHORT);
            return;
        }
        if (mEmotionKeyBoard.getEmotionEditContent().toString().length() == 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_post_no_content_tip), Toast.LENGTH_SHORT);
            return;
        }
        if (mCommentType == CommunityConstant.COMMENTS_TYPE_ARTICLE) {
            sendCommentReply();
            return;
        }
        if (StringUtils.parseInt(info.getSex()) == CommonConst.SEX_WOMAN) {
            sendCommentReply();
            return;
        }
        if (mComment != null && mComment.getIsLimitMale() == CommunityConstant.COMMENT_LIMIT_MAN) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_limit_man_comment), Toast.LENGTH_SHORT);
            return;
        }
        sendCommentReply();
    }

    /**
     * 获取编辑框里的内容
     *
     * @return
     */
    private String getEditContent() {
        return mEmotionKeyBoard.getEmotionEditContent();
    }

    private void resetReplyType() {
        mIsReplyToSomeOne = false;
        mEmotionKeyBoard.setEmotionEditHit("我要评论...");
    }

    private void setReplyTipsInEdit(CommentReplyBean replyBean) {
        mIsReplyToSomeOne = true;
        mTempReplyBean = replyBean;
        if (mEmotionKeyBoard == null) {
            return;
        }
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.setEmotionEditHit("回复@" + replyBean.getNickname() + ":");
    }

    /**
     * 发表回复
     */
    private void sendCommentReply() {
        int contentId = mCommentId;
        if (mIsReplyToSomeOne) {
            if (mTempReplyBean != null) {
                contentId = mTempReplyBean.getReplyId();
            }
        } else {
            contentId = mCommentId;
        }
        mPostCommentRequest.startCommentReplyRequest(mCommentType, contentId, getEditContent(), new PostCommentRequest.PostRequestResultCallback() {
            @Override
            public void onResultFailed(int errorCode) {
                handleSendCommentReplyFail(errorCode);
            }

            @Override
            public void onResultSuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
                handleSendCommentReplySuccess(resultBean);
            }
        });

    }

    private void handleSendCommentReplyFail(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_psot_failed), Toast.LENGTH_SHORT);
            return;
        }
        RespondCodeHelper.handlerResultStatus(getContext(), errorCode);
    }

    private void handleSendCommentReplySuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
        if (resultBean.getDayReplyCount() <= resultBean.getMaxRewardTimes()) {
            if (resultBean.getIsLevelUp() == 0) {
                ToastHelper.showToast(getContext(), "回帖成功！", "经验+" + resultBean.getObtainedExp() + "(" + resultBean.getDayReplyCount() + "/" + resultBean.getMaxRewardTimes() + ")");
            } else {
                ToastHelper.showToast(getContext(), "回帖成功！", "升到" + resultBean.getCurrentLevel() + "级");
            }
        }
        tryGetReplyDatas(true);
        //设置更新数据通知标识
        mIsSendSomeReply = true;
        mEmotionKeyBoard.setEmotionEditHit("");
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.closeSoftware();
    }

    private void handlerOnCloseCurrentWindowToNotifyIsSendReply() {
        if (!mIsSendSomeReply) {
            return;
        }
        mComment.setReplys(mReplies);
        Notification notification = Notification.obtain(NotificationDef.N_COMMUNITY_IS_SEND_SOME_RELPY, mComment);
        NotificationCenter.getInstance().notify(notification);

    }
}
