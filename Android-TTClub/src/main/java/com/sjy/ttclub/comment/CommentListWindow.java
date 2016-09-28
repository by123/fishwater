package com.sjy.ttclub.comment;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommunitySendCommentResultBean;
import com.sjy.ttclub.common.RespondCodeHelper;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.emoji.EmotionKeyBoard;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
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

/**
 * Created by linhz on 2015/12/30.
 * Email: linhaizhong@ta2she.com
 */
public class CommentListWindow extends DefaultWindow {
    private LoadMoreListViewContainer mLoadMoreLayout;
    private PtrFrameLayout mRefreshLayout;
    private ListView mCommentListView;
    private LoadingLayout mLoadingLayout;
    private EmotionKeyBoard mEmotionKeyBoard;
    private ArrayList<CommentBean> mCommentList;
    private CommentsListViewAdapter mAdapter;
    private PostCommentRequest mPostCommentRequest;
    private CommentsRequest mCommentRequest;
    private CommentListInfo mCommentInfo;
    private CommentBean mTipComment;
    private LinearLayout mContentBackground;
    private LinearLayout mTipCommentLayout;
    private TextView mTipCommentContent;
    private SimpleDraweeView mTipCommentHeadIcon;

    public CommentListWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.comment_list_title);
        onCreateContent();
    }


    public void setupWindow(CommentListInfo info) {
        mCommentInfo = info;
        mCommentRequest = new CommentsRequest(getContext(), info.commentType);
        mPostCommentRequest = new PostCommentRequest(getContext(), CommunityConstant.POST_TYPE_COMMENT, info.postId);
        mAdapter.setCommentType(mCommentInfo.commentType);
    }


    private View onCreateContent() {
        View view = View.inflate(getContext(), R.layout.comments_list_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        initCommentListView(view);
        initRefreshLayout(view);
        initLoadMoreLayout(view);
        initLoadingLayout(view);
        initEmotionKeyBoard(view);
        initRegisterNotify();
    }

    private void initCommentListView(View view) {
        mCommentList = new ArrayList<>();
        mCommentListView = (ListView) view.findViewById(R.id.comment_listview);
        mTipCommentLayout = (LinearLayout) view.findViewById(R.id.tip_comment_layout);
        mTipCommentHeadIcon = (SimpleDraweeView) view.findViewById(R.id.tip_comment_head_icon);
        mTipCommentContent = (TextView) view.findViewById(R.id.tip_comment_content);
        mContentBackground = (LinearLayout) view.findViewById(R.id.comment_list_content_bg);
        mContentBackground.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerOnContentBgClick();
            }
        });
        mAdapter = new CommentsListViewAdapter(getContext(), mCommentList);
        mAdapter.setOnCommentListItemClickListener(new CommentsListViewAdapter.OnCommentListItemClickListener() {
            @Override
            public void onClick(CommentBean commentBean) {
                mTipComment = commentBean;
                handlerOnItemClick(commentBean);
            }
        });
        mCommentListView.setAdapter(mAdapter);
    }

    private void handlerOnItemClick(CommentBean commentBean) {
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

    private void handlerOnContentBgClick() {
        mTipComment = null;
        mContentBackground.setVisibility(GONE);
        mTipCommentLayout.setVisibility(GONE);
        if (mEmotionKeyBoard == null) {
            return;
        }
        mEmotionKeyBoard.setEmotionEditHit(ResourceHelper.getString(R.string.community_post_comment_default_hit));
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.closeSoftware();
    }

    private void initEmotionKeyBoard(View view) {
        mEmotionKeyBoard = (EmotionKeyBoard) view.findViewById(R.id.emoji_keyboard);
        mEmotionKeyBoard.setEmotionButtonVisible(true);
        mEmotionKeyBoard.setOnPostButtonOnClickListener(new EmotionKeyBoard.OnPostButtonOnClickListener() {
            @Override
            public void onPostButtonClick() {
                handlerPostCommentButtonOnClick();
            }
        });
    }

    private void initRefreshLayout(View view) {
        mRefreshLayout = (PtrClassicFrameLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setVisibility(View.VISIBLE);
        final TTRefreshHeader header = new TTRefreshHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mRefreshLayout.setLoadingMinTime(1000);
        mRefreshLayout.setDurationToCloseHeader(800);
        mRefreshLayout.setHeaderView(header);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setPullToRefresh(false);
        mRefreshLayout.isKeepHeaderWhenRefresh();
        mRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mCommentRequest.isRequesting()) {
                    return;
                }
                tryGetComments(false);
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mCommentListView, header);
            }
        });
    }

    private void initLoadMoreLayout(View view) {
        mLoadMoreLayout = (LoadMoreListViewContainer) view.findViewById(R.id.loadmore_layout);
        mLoadMoreLayout.useDefaultFooter();
        mLoadMoreLayout.setAutoLoadMore(true);
        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetComments(true);
            }
        });
    }

    private void initLoadingLayout(View view) {
        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.loading_layout);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetComments(false);
            }
        });
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);

    }


    @Override
    public boolean onWindowBackKeyEvent() {
        boolean isShowEmojiKeyboard = mEmotionKeyBoard.isShowEmojiKeyBord();
        if (isShowEmojiKeyboard) {
            mEmotionKeyBoard.closeSoftware();
            return true;
        }
        return false;
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            tryGetCommentsFirst();
        } else if (stateFlag == STATE_AFTER_POP_OUT) {
            DeviceManager.getInstance().hideInputMethod();
            unRegisterNotify();
        }
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_EMOJINKEYBOARD_SHOW);
    }

    private void unRegisterNotify() {
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_EMOJINKEYBOARD_SHOW);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_HIDE) {
            mContentBackground.setVisibility(GONE);
            mTipCommentLayout.setVisibility(GONE);
        } else if (notification.id == NotificationDef.N_EMOJINKEYBOARD_SHOW) {
            if(mTipComment!=null) {
                mTipCommentLayout.setVisibility(VISIBLE);
            }else {
                mTipCommentLayout.setVisibility(GONE);
            }
            mContentBackground.setVisibility(VISIBLE);
            mContentBackground.startAnimation(AnimotionDao.getAlphaAnimationShow(500));
        } else if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_SHOW) {
            mContentBackground.setVisibility(VISIBLE);
            mContentBackground.startAnimation(AnimotionDao.getAlphaAnimationShow(500));
        }
    }


    private void tryGetCommentsFirst() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        tryGetComments(false);
    }

    private void tryGetComments(final boolean isLoadMore) {
        mCommentRequest.startRequest(String.valueOf(mCommentInfo.postId), isLoadMore, new CommentsRequest
                .RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handleRequestCommentsDataFail(errorType);
            }

            @Override
            public void onResultSuccess(List<CommentBean> allList, List<CommentBean> newList) {
                handleRequestCommentsDataSuccess(allList);
            }
        });
    }

    private void handleRequestCommentsDataFail(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }

        if (mCommentList.size() == 0) {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_data_error)
                    , true);
            return;
        }
        mLoadMoreLayout.loadMoreFinish(false, mCommentRequest.hasMore());
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            ToastHelper.showToast(getContext(), R.string.homepage_network_error);
        } else if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(getContext(), R.string.homepage_data_error);
        }
    }

    private void handleRequestCommentsDataSuccess(List<CommentBean> commentList) {
        mEmotionKeyBoard.setVisibility(View.VISIBLE);
        mLoadMoreLayout.loadMoreFinish(false, mCommentRequest.hasMore());
        mCommentList.clear();
        mCommentList.addAll(commentList);
        mLoadingLayout.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    private void handlerPostCommentButtonOnClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (info == null) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (StringUtils.parseInt(info.getLevel()) < 1) {
            ToastHelper.showToast(getContext(), R.string.comment_level_not_enough_one);
            return;
        }
        if (StringUtils.isEmpty(mEmotionKeyBoard.getEmotionEditContent().toString())) {
            ToastHelper.showToast(getContext(), R.string.community_post_no_content_tip);
            return;
        }
        tryPostComment();
    }

    private void tryPostComment() {
        int contentId = 0;
        int postType = CommunityConstant.POST_TYPE_COMMENT;
        if (mTipComment != null) {
            contentId = mTipComment.getCommentId();
            postType = CommunityConstant.POST_TYPE_REPLY;

        }
        mPostCommentRequest.startCommentRequest(postType, mCommentInfo.commentType, contentId, mEmotionKeyBoard.getEmotionEditContent(), new
                PostCommentRequest.PostRequestResultCallback() {
                    @Override
                    public void onResultFailed(int errorCode) {
                        handlePostCommentFail(errorCode);
                    }

                    @Override
                    public void onResultSuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
                        handlePostCommentSuccess(resultBean);
                    }
                });
    }

    private void handlePostCommentFail(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        RespondCodeHelper.handlerResultStatus(getContext(), errorCode);
    }

    private void handlePostCommentSuccess(CommunitySendCommentResultBean.ResultBean resultBean) {
        tryGetComments(true);
        mEmotionKeyBoard.setEmotionEditContent("");
        mEmotionKeyBoard.closeSoftware();
        mEmotionKeyBoard.setEmotionEditHit(ResourceHelper.getString(R.string.community_post_comment_default_hit));
        mTipCommentLayout.setVisibility(GONE);
        mContentBackground.setVisibility(GONE);
        mTipComment = null;
    }

}
