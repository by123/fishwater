package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.CommunityUserInfo;
import com.sjy.ttclub.bean.community.ReportBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.CommunityReportHelper;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
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
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.util.UserInfoExternalLinksHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.LoadingLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2015/12/29.
 * email 1501448275@qq.com
 */
public class CommunityUserInfoWindow extends AbstractWindow implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int TYPE_ARTICLE = 1;
    private static final int TYPE_POST = 2;
    private int mType = TYPE_POST;
    private LoadMoreListViewContainer mListViewContainer;
    private LinearLayout mTopViewLayout, mUserArticle;
    private ListView mPostListView;
    private LoadingLayout mNoDataBg;
    private List<CommunityListItemInfo> mPosts = new ArrayList<>();
    private AlphaImageView mReportButton;
    private AlphaTextView mLetterButton, mAttentionButton;
    private CommunityUserInfo mUser;
    private SimpleDraweeView mUserHeadIcon;
    private TextView mUserName;
    private View mUserLine;
    private TextView mUserLevel, mUserPraiseCount, mUserSpecialIdentity;
    private TextView mUserPostCount, mUserArticleCount, mUserFollowersCount, mUserFollowingCount;
    private TextView mUserPostTitle, mUserArticleTitle;
    private SimpleDraweeView mHeadBackgound;
    private CommunityUserPostAdapter mUserPostAdapter;
    private CommunityUsersRequest mUserRequest;
    private GetChatSessionInfoRequest mGetChatSessionInfoRequest;
    private MessageDialogs mLetter;
    private AttentionRequest mAttentionRequest;
    private boolean mIsClickedPost = true;
    private boolean mIsClickedArticle = false;
    private static final String IS_PRIVATE_LETTER = "1";
    private int mUserId;
    private CommunityReportHelper mReportHelper;

    public CommunityUserInfoWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        createView();
        initRegisterNotify();
        StatsModel.stats(StatsKeyDef.PERSONAL_PROFILE_VIEW);
    }

    public void setUserInfo(int userId) {
        this.mUserId = userId;
        initRequests();
    }

    private void createView() {
        View view = View.inflate(getContext(), R.layout.community_userinfo_layout, null);
        getBaseLayer().addView(view, getBaseLayerLP());
        mTopViewLayout = (LinearLayout) findViewById(R.id.top_view_layout);
        mAttentionButton = (AlphaTextView) findViewById(R.id.btn_attention_in_userinfo);
        mHeadBackgound = (SimpleDraweeView) findViewById(R.id.community_person_bg);
        findViewById(R.id.btn_community_person_back).setOnClickListener(this);
        mReportButton = (AlphaImageView) findViewById(R.id.btn_report);
        mLetterButton = (AlphaTextView) findViewById(R.id.btn_letter);
        mUserHeadIcon = (SimpleDraweeView) findViewById(R.id.community_user_icon);
        mUserName = (TextView) findViewById(R.id.community_user_name);
        mUserLevel = (TextView) findViewById(R.id.user_level);
        mUserLine = findViewById(R.id.user_line);
        mUserPostCount = (TextView) findViewById(R.id.user_post_count);
        mUserArticle = (LinearLayout) findViewById(R.id.user_article);
        mUserPostTitle = (TextView) findViewById(R.id.user_post_title);
        mUserArticleTitle = (TextView) findViewById(R.id.user_article_title);
        mUserArticle.setOnClickListener(this);
        mUserArticleCount = (TextView) findViewById(R.id.user_article_count);
        mUserFollowersCount = (TextView) findViewById(R.id.user_followers_count);
        mUserSpecialIdentity = (TextView) findViewById(R.id.user_specialIdentity);
        mUserFollowingCount = (TextView) findViewById(R.id.user_following_count);
        findViewById(R.id.user_post).setOnClickListener(this);
        findViewById(R.id.user_followers).setOnClickListener(this);
        findViewById(R.id.user_following).setOnClickListener(this);
        mNoDataBg = (LoadingLayout) findViewById(R.id.loading_layout);
        mNoDataBg.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetPostDatas(false);
            }
        });
        mPostListView = (ListView) findViewById(R.id.community_user_card_list);
        mUserPraiseCount = (TextView) findViewById(R.id.user_praise_count);
        mUserPostAdapter = new CommunityUserPostAdapter(getContext(), mPosts);
        mPostListView.setAdapter(mUserPostAdapter);
        mPostListView.setOnItemClickListener(this);
        mListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.list_view_container);
        mListViewContainer.useDefaultFooter();
        mListViewContainer.setAutoLoadMore(true);
        // binding view and data
        mListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryGetPostDatas(true);
            }
        });
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTopViewLayout.getLayoutParams();
        if (SystemUtil.isTransparentStatusBarEnable()) {
            lp.topMargin += SystemUtil.getStatusBarHeight(getContext());
        }
        mTopViewLayout.setLayoutParams(lp);
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
    }

    private void unRegisterNotify() {
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
    }

    private void initRequests() {
        mAttentionRequest = new AttentionRequest(getContext(), mUserId);
        mGetChatSessionInfoRequest = new GetChatSessionInfoRequest(getContext());
        mUserRequest = new CommunityUsersRequest(getContext(), mUserId);
        mReportHelper = new CommunityReportHelper(getContext());
    }

    private void handlerBackButtonOnClick() {
        mCallBacks.onWindowExitEvent(this, true);
    }

    private void handlerReportButtonOnClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mUser == null) {
            return;
        }

        if (!mReportHelper.reportUserReasonIsSaveInLocal(getContext())) {
            mReportHelper.getReportReasonsListFromServer(getContext());
            return;
        }
        ActionSheetPanel panel = new ActionSheetPanel(getContext());
        final List<ReportBean> reportBeans = mReportHelper.getReportUserList(getContext());
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
                        mUserId,
                        mUserId, mReportHelper.REPORT_USER, id);

            }
        });
        panel.showPanel();
    }

    private void handerLetterButtonOnClick() {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mGetChatSessionInfoRequest.ismIsRequesting()) {
            return;
        }
        if (StringUtils.isEmpty(mUser.getIfPrivLetter())) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (mUserId == StringUtils.parseInt(AccountManager.getInstance().getAccountInfo().getUserId())) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_chat_self_limit), Toast.LENGTH_SHORT);
            return;
        }
        if (!IS_PRIVATE_LETTER.equals(mUser.getIfPrivLetter())) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_chat_level_limit), Toast.LENGTH_SHORT);
            return;
        }
        if (mLetter == null) {
            getChatSessionInfo(false);
            return;
        }
        Message msg = Message.obtain();
        LetterChatParamBean paramBean = new LetterChatParamBean();
        paramBean.setLetter(mLetter);
        msg.obj = paramBean;
        msg.what = MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);

    }

    private void handlerAttentionButtonOnclick() {
        StatsModel.stats(StatsKeyDef.PERSONAL_PROFILE_FOLLOW);
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
        if (mUser.getIfFollow() == 1) {
            //统计personal_profile_care_cancel
            StatsModel.stats(StatsKeyDef.PERSONAL_PROFILE_CARE_CANCEL, StatsKeyDef.PERSONAL_PROFILE_CARE_CANCEL, mUserId);

            handleCancelAttention();
        } else {
            //统计personal_profile_care
            StatsModel.stats(StatsKeyDef.PERSONAL_PROFILE_CARE, StatsKeyDef.PERSONAL_PROFILE_CARE, mUserId);

            handleAddAttention();
        }
    }

    private void initUserInfo() {
        if (mUser == null) {
            return;
        }
        getChatSessionInfo(true);
        setAttentionButtonStatus();

        if (!StringUtils.isEmpty(mUser.getImageUrl())) {
            mUserHeadIcon.setImageURI(Uri.parse(mUser.getImageUrl()));
            mHeadBackgound.setImageURI(Uri.parse(mUser.getImageUrl()));
        }
        mUserName.setText(mUser.getNickname());
        mUserPraiseCount.setText(String.valueOf(mUser.getPraiseCount()) + "赞");
        mUserPostCount.setText(String.valueOf(mUser.getPostCount()));
        mUserFollowersCount.setText(String.valueOf(mUser.getFollowersCount()));
        mUserFollowingCount.setText(String.valueOf(mUser.getFollowingCount()));
        setArticleCount();
        setUserLevel(mUser.getUserRoleId());
        setUserSpecialIdentity();

        notifyChangeTextStyle();
        mLetterButton.setOnClickListener(this);
        mReportButton.setOnClickListener(this);
        mAttentionButton.setOnClickListener(this);
    }

    private void setUserSpecialIdentity() {
        String userTagName = mUser.getUserTagName();
        if (StringUtils.isNotEmpty(userTagName)) {
            mUserSpecialIdentity.setVisibility(VISIBLE);
            mUserSpecialIdentity.setText(userTagName);
        } else {
            mUserSpecialIdentity.setVisibility(GONE);
        }
    }

    private void setArticleCount() {
        if (mUser.getArticleCount() == 0) {
            mUserLine.setVisibility(GONE);
            mUserArticle.setVisibility(GONE);
        } else {
            mUserArticle.setVisibility(VISIBLE);
            mUserLine.setVisibility(VISIBLE);
            mUserArticleCount.setText(String.valueOf(mUser.getArticleCount()));
        }
    }

    /**
     * 数字格式化
     *
     * @return
     */
    public String decimalFormat(String count) {
        DecimalFormat df = new DecimalFormat("0.0");
        long followingCount = StringUtils.parseLong(count);
        if (followingCount < 1000) {
            return followingCount + "";
        } else if (followingCount > 999 && followingCount < 10000) {
            return df.format((float) followingCount / 1000) + "k+";
        } else {
            return df.format((float) followingCount / 10000) + "w+";
        }
    }

    private void setAttentionButtonStatus() {
        if (mUser.getIfFollow() == 1) {
            mAttentionButton.setText(ResourceHelper.getString(R.string.community_cancel_attention_button));
        } else {
            mAttentionButton.setText(ResourceHelper.getString(R.string.community_add_attention_button));
        }
    }

    private void setUserLevel(int userRole) {
        if (userRole == CommunityConstant.USER_ROLE_GENERAL || userRole == CommunityConstant.USER_ROLE_GIRL_GODDESS) {
            mUserLevel.setText("LV" + String.valueOf(mUser.getLevel()));
            return;
        }
        if (userRole == CommunityConstant.USER_ROLE_EDITOR) {
            mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
            mUserLevel.setText(ResourceHelper.getString(R.string.community_user_info_extra_level_1));
            mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
            return;
        }
        if (userRole == CommunityConstant.USER_ROLE_TESTING_EXPRESS) {
            mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
            mUserLevel.setText(ResourceHelper.getString(R.string.community_user_info_extra_level_2));
            mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
            return;
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                tryGetUserInfo();
                break;
            case STATE_AFTER_POP_OUT:
                unRegisterNotify();
                break;
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            tryGetUserInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_community_person_back:
                handlerBackButtonOnClick();
                break;
            case R.id.btn_attention_in_userinfo:
                handlerAttentionButtonOnclick();
                break;
            case R.id.btn_report:
                handlerReportButtonOnClick();
                break;
            case R.id.btn_letter:
                StatsModel.stats(StatsKeyDef.PERSONAL_PROFILE_MESSAGE);
                handerLetterButtonOnClick();
                break;
            case R.id.user_post:
                clickPost();
                break;
            case R.id.user_article:
                clickArticle();
                break;
            case R.id.user_followers:   //粉丝
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_user_info_fans_alert));
                break;
            case R.id.user_following:   //关注
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_user_info_following_alert));
                break;
        }
    }

    private void clickPost() {
        if (mIsClickedPost) {
            return;
        }
        mType = TYPE_POST;
        mIsClickedPost = true;
        mIsClickedArticle = false;
        notifyChangeTextStyle();
        tryGetPostDatas(false);
    }

    private void clickArticle() {
        if (mIsClickedArticle) {
            return;
        }
        mType = TYPE_ARTICLE;
        mIsClickedArticle = true;
        mIsClickedPost = false;
        notifyChangeTextStyle();
        tryGetPostDatas(false);
    }

    private void notifyChangeTextStyle() {
        if (mIsClickedPost) {
            mUserPostCount.setTextColor(ResourceHelper.getColor(R.color.title_color));
            mUserPostTitle.setTextColor(ResourceHelper.getColor(R.color.title_color));
            mUserArticleCount.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserArticleTitle.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
        } else if (mIsClickedArticle) {
            mUserPostCount.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserPostTitle.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserArticleCount.setTextColor(ResourceHelper.getColor(R.color.title_color));
            mUserArticleTitle.setTextColor(ResourceHelper.getColor(R.color.title_color));
        } else {
            mUserPostCount.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserPostTitle.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserArticleCount.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
            mUserArticleTitle.setTextColor(ResourceHelper.getColor(R.color.account_text_color));
        }
    }

    private void handleCancelAttention() {
        mAttentionRequest.startCancelAttentionRequest(new AttentionRequest.AttentionRequestResultCallback() {
            @Override
            public void onResultFail(int errorCode) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                        .community_cancel_attention_fail_tip), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResultSuccess() {
                mAttentionButton.setText(ResourceHelper.getString(R.string.community_add_attention_button));
                mUser.setIfFollow(0);
                AccountManager.getInstance().notifyAccountDataChanged();
            }
        });
    }

    private void handleAddAttention() {
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (info == null) {
            return;
        }
        if (mUserId == StringUtils.parseInt(info.getUserId())) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_attention_self_limit), Toast.LENGTH_SHORT);
            return;
        }
        mAttentionRequest.startAddAttentionRequest(new AttentionRequest.AttentionRequestResultCallback() {
            @Override
            public void onResultFail(int errorCode) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_add_attention_fail_tip), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResultSuccess() {
                mAttentionButton.setText(ResourceHelper.getString(R.string.community_cancel_attention_button));
                mUser.setIfFollow(1);
                AccountManager.getInstance().notifyAccountDataChanged();
            }
        });
    }

    /**
     * 数据异常
     */
    private void setDataFaultBg() {
        mNoDataBg.setVisibility(View.VISIBLE);
        mListViewContainer.setVisibility(View.GONE);
        mNoDataBg.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), true);
    }

    private void getChatSessionInfo(final boolean isInit) {
        mGetChatSessionInfoRequest.startChatWithConsumer(mUserId, new GetChatSessionInfoRequest.RequestChatSessionInfoCallback() {
            @Override
            public void onResultFail(int errorCode) {

            }

            @Override
            public void onResultSuccess(MessageDialogs letter) {
                handleGetChatSessionSuccess(letter, isInit);
            }
        });
    }

    private void handleGetChatSessionSuccess(MessageDialogs letter, boolean isInit) {
        this.mLetter = letter;
        if (isInit) {
            return;
        }
        Message msg = Message.obtain();
        LetterChatParamBean paramBean = new LetterChatParamBean();
        paramBean.setLetter(letter);
        msg.obj = paramBean;
        msg.what = MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void tryGetPostDatas(final boolean isLoadMore) {
        if (!NetworkUtil.isNetworkConnected()) {
            setDataFaultBg();
            return;
        }
        if (mPosts.isEmpty() && !isLoadMore) {
            mNoDataBg.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.cube_views_load_more_loading), false);
            mNoDataBg.setVisibility(View.VISIBLE);
            mListViewContainer.setVisibility(View.GONE);
        }
        mUserRequest.startRequest(isLoadMore, mType, new CommunityUsersRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handleGetCardsFailed(errorType, isLoadMore);
            }

            @Override
            public void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList) {
                handleGetCardsSuccess(newList, isLoadMore);
            }
        });
    }

    private void handleGetCardsFailed(int errorCode, boolean isLoadMore) {
        //获取数据异常处理
        if (isLoadMore) {
            mListViewContainer.loadMoreError(0, ResourceHelper.getString(R.string.cube_views_load_more_error));
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            if (mPosts.isEmpty()) {
                setDataFaultBg();
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            if (mPosts.isEmpty()) {
                mNoDataBg.setVisibility(View.VISIBLE);
                mListViewContainer.setVisibility(View.GONE);
                mNoDataBg.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error_retry), Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
    }

    private void handleGetCardsSuccess(ArrayList<CommunityListItemInfo> list, boolean isLoadMore) {
        //获取数据成功
        if (!isLoadMore) {
            mPosts.clear();
        }
        mPosts.addAll(list);
        if (mPosts.isEmpty()) {
            mNoDataBg.setVisibility(View.VISIBLE);
            mNoDataBg.setBgContent(R.drawable.no_data_bg, "", false);
            mListViewContainer.setVisibility(View.GONE);
        } else {
            mNoDataBg.setVisibility(View.GONE);
            mListViewContainer.setVisibility(View.VISIBLE);
            mListViewContainer.loadMoreFinish(false, mUserRequest.hasMore());
        }
        if (mUserPostAdapter != null) {
            mUserPostAdapter.notifyDataSetChanged();
        }
    }

    private void tryGetUserInfo() {
        mUserRequest.startRequestUserInfo(new CommunityUsersRequest.RequestGetUserInfoResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerGetUserInfoFail(errorType);
            }

            @Override
            public void onResultSuccess(CommunityUserInfo userInfo) {
                handlerGetUserInfoSuccess(userInfo);
            }
        });
    }

    private void handlerGetUserInfoFail(int errorCode) {
        ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
    }

    private void handlerGetUserInfoSuccess(CommunityUserInfo userInfo) {
        this.mUser = userInfo;
        initUserInfo();
        tryGetPostDatas(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CommunityPostBean postBean = (CommunityPostBean) mPosts.get(position).getData();
        UserInfoExternalLinksHelper helper = new UserInfoExternalLinksHelper();
        helper.goToExternalLinks(postBean);
    }
}
