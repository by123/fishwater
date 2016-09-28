/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account;

import android.content.Context;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.MessageUnreadCountRequestHelper;
import com.sjy.ttclub.account.widget.sexpanel.SexDataPanel;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.UICallBacks;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.umeng.UmengVersionUpdateHelper;
import com.sjy.ttclub.util.PrivacyProtectionHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by 邓钢清 on 2015/11/9.
 * Email: denggangqing@ta2she.com
 */
public class AccountMainTab extends AbstractWindow implements View.OnClickListener, IMainTabView, View.OnLongClickListener {
    private SimpleDraweeView mHeadImage; //头像
    private LinearLayout mLogin; //已经登录时显示
    private TextView mNickname;  //妮称
    private TextView mLevel; //等级
    private TextView mNoLogin;  //未登录时显示
    private TextView mFollowNumber; //关注
    private TextView mFansNumber;   //粉丝
    private TextView mVersion, mUpgrade, mNewVersionName; //版本更新
    private ImageView mSwitchOff;
    private AccountManager mAccountManager;
    private UmengVersionUpdateHelper mUmengVersionUpdateHelper;
    private TextView mMessageUnreadCount;
    private TextView mSexPoint;
    private ImageView mUnreadSamePoint;

    public AccountMainTab(Context context, UICallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        View view = View.inflate(getContext(), R.layout.account_main_layout, null);
        getBaseLayer().addView(view, getBaseLayerLP());

        mAccountManager = AccountManager.getInstance();
        mUmengVersionUpdateHelper = new UmengVersionUpdateHelper(getContext());

        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_STATE_CHANGED);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED);

        initUI(view);
        mUmengVersionUpdateHelper.checkUpdate(false);
        setData();
    }

    /**
     * 初始化UI
     */
    public void initUI(View view) {
        mHeadImage = (SimpleDraweeView) view.findViewById(R.id.account_head_image);
        mHeadImage.setOnClickListener(this);
        mLogin = (LinearLayout) view.findViewById(R.id.account_login);
        mLogin.setOnClickListener(this);
        mNickname = (TextView) view.findViewById(R.id.account_nickname);
        mLevel = (TextView) view.findViewById(R.id.account_level);
        mNoLogin = (TextView) view.findViewById(R.id.account_no_login);
        mNoLogin.setOnClickListener(this);
        mFollowNumber = (TextView) view.findViewById(R.id.account_follow_number);
        mFansNumber = (TextView) view.findViewById(R.id.account_fans_number);
        mVersion = (TextView) view.findViewById(R.id.account_version_update);
        mUpgrade = (TextView) view.findViewById(R.id.account_upgrade);
        mNewVersionName = (TextView) view.findViewById(R.id.account_new_version_name);
        mMessageUnreadCount = (TextView) view.findViewById(R.id.account_message_unread_count);
        mSexPoint = (TextView) view.findViewById(R.id.account_sex_point);
        mSwitchOff = (ImageView) view.findViewById(R.id.account_switch_off);
        mSwitchOff.setOnClickListener(this);
        mUnreadSamePoint = (ImageView) findViewById(R.id.account_message_same_unread);

        View settingView = view.findViewById(R.id.account_setting);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) settingView.getLayoutParams();
        if (SystemUtil.isTransparentStatusBarEnable()) {
            lp.topMargin += SystemUtil.getStatusBarHeight(getContext());
        }
        settingView.setOnClickListener(this);

        view.findViewById(R.id.account_weibo_wechat).setOnLongClickListener(this);
        view.findViewById(R.id.account_version).setOnClickListener(this);
        view.findViewById(R.id.account_follow_rl).setOnClickListener(this);
        view.findViewById(R.id.account_fans_rl).setOnClickListener(this);
        view.findViewById(R.id.account_collect).setOnClickListener(this);
        view.findViewById(R.id.account_all_order).setOnClickListener(this);
        view.findViewById(R.id.account_my_speech).setOnClickListener(this);
        view.findViewById(R.id.account_message).setOnClickListener(this);
        view.findViewById(R.id.account_feedback).setOnClickListener(this);
        view.findViewById(R.id.account_sex_area).setOnClickListener(this);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == IMainTabView.TAB_TO_SHOW) {
            StatsModel.stats(StatsKeyDef.MYSELF_VIEW);
            new MessageUnreadCountRequestHelper().getMessageUnreadCount();
        }
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_SHOW) {
            AccountManager.getInstance().notifyAccountDataChanged();
        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ACCOUNT_STATE_CHANGED) {
            setData();
        } else if (notification.id == NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED) {
            setMessageUnreadCount();
        }
    }

    /**
     * 设置数据
     */
    private void setData() {
        boolean isLogin = mAccountManager.isLogin();
        if (isLogin) {
            mLogin.setVisibility(View.VISIBLE);
            mNoLogin.setVisibility(View.GONE);
            setLoginData();
        } else {
            mLogin.setVisibility(View.GONE);
            mNoLogin.setVisibility(View.VISIBLE);
            setNoLoginData();
        }
        setCommonData();
    }

    /**
     * 设置版本更新的样式显示
     */
    public void setVersionVisible() {
        if (mUmengVersionUpdateHelper == null) {
            return;
        } else if (mUmengVersionUpdateHelper.haveNewVersion()) {
            mUpgrade.setVisibility(View.VISIBLE);
            mVersion.setText(ResourceHelper.getString(R.string.account_updating_version));
            mNewVersionName.setVisibility(View.VISIBLE);
            String newVersion = mUmengVersionUpdateHelper.getNewVersion();
            mNewVersionName.setText(newVersion);
        } else {
            mUpgrade.setVisibility(View.GONE);
            mVersion.setText(ResourceHelper.getString(R.string.account_updating_check_version));
            mNewVersionName.setVisibility(View.GONE);
        }
    }

    /**
     * 设置登录时的数据
     */
    private void setLoginData() {
        AccountInfo accountInfo = mAccountManager.getAccountInfo();
        //用户名
        mNickname.setText(accountInfo.getNickname());
        //等级
        mLevel.setText(accountInfo.getLevel());
        if ((CommonConst.SEX_MAN + "").equals(accountInfo.getSex())) {
            mLevel.setBackgroundResource(R.drawable.account_level_man);
            mLevel.setTextColor(getResources().getColor(R.color.account_level_color_man));
        } else {
            mLevel.setBackgroundResource(R.drawable.account_level_woman);
            mLevel.setTextColor(getResources().getColor(R.color.account_level_color_woman));
        }
        setRelationshipChange(accountInfo);
        //情趣指数
        mSexPoint.setText("情趣指数：" + accountInfo.getSexPoint());
    }

    private void setRelationshipChange(AccountInfo accountInfo) {
        //关注数
        mFollowNumber.setText(accountInfo.getFollowingCount());
        //粉丝数
        mFansNumber.setText(accountInfo.getFollowersCount());
    }

    /**
     * 设置未登录的样式
     */
    private void setNoLoginData() {
        mFollowNumber.setText(null);
        mFansNumber.setText(null);
        //情趣指数
        mSexPoint.setText(ResourceHelper.getString(R.string.account_sex_symbol));
    }

    /**
     * 设置公共样式
     */
    private void setCommonData() {
        //头像
        AccountManager.getInstance().setHeadImage(mHeadImage);
        setPrivacyProtection();
    }

    public void setPrivacyProtection() {
        //隐私保护
        if (PrivacyProtectionHelper.isOpenPrivacyProtection()) {
            mSwitchOff.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
        } else {
            mSwitchOff.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_head_image:   //头像
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_USER_AVATAR);
                if (AccountManager.getInstance().isLogin()) {
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_PERSONAL_WINDOW);
                } else {
                    AccountManager.getInstance().tryOpenGuideLoginWindow();
                }
                break;
            case R.id.account_login:    //昵称、等级
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_PERSONAL_WINDOW);
                break;
            case R.id.account_no_login: //登录、注册
                AccountManager.getInstance().tryOpenGuideLoginWindow();
                break;
            case R.id.account_setting:  //设置
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_SETTING);
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_SETTING_WINDOW);
                break;
            case R.id.account_version:  //版本更新
                mUmengVersionUpdateHelper.checkUpdate(true);
                break;
            case R.id.account_follow_rl:    //关注
                //统计
                StatsModel.stats(StatsKeyDef.ME_FOLLOW);
                startRelationship(Constant.FOLLOW);
                break;
            case R.id.account_fans_rl:  //粉丝
                //统计
                StatsModel.stats(StatsKeyDef.ME_FANS);
                startRelationship(Constant.FANS);
                break;
            case R.id.account_collect:  //收藏
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_COLLECT);
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_COLLECT_WINDOW);
                break;
            case R.id.account_all_order:    //全部订单
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_ORDERS);
                if (AccountManager.getInstance().isLogin()) {
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_ORDER_LIST_WINDOW);
                } else {
                    AccountManager.getInstance().tryOpenGuideLoginWindow();
                }
                break;
            case R.id.account_my_speech:    //我的发言
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_POST);
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_MY_POST_WINDOW;
                message.arg1 = CommunityConstant.CIRCLE_TYPE_POST;
                MsgDispatcher.getInstance().sendMessage(message);
                break;
            case R.id.account_message:      //消息
                //统计
                StatsModel.stats(StatsKeyDef.ME_MESSAGE);
                showMessageWindow();
                break;
            case R.id.account_feedback: //用户反馈
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_FEEDBACK_WINDOW);
                break;
            case R.id.account_sex_area: //情趣指数
                //统计
                StatsModel.stats(StatsKeyDef.ME_SEXY_INDEX);
                showSexSymbol();
                break;
            case R.id.account_switch_off:   //隐私保护
                //统计
                StatsModel.stats(StatsKeyDef.MYSELF_PRIVACY);
                privacyProtection();
                break;
        }
    }

    /**
     * 打开情趣指数面板
     */
    private void showSexSymbol() {
        if (AccountManager.getInstance().isLogin()) {
            SexDataPanel sexDataPanel = new SexDataPanel(getContext());
            sexDataPanel.showPanel();
        } else {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
    }

    /**
     * 打开关注、粉丝界面
     */
    private void startRelationship(int type) {
        if (AccountManager.getInstance().isLogin()) {
            Message message = Message.obtain();
            message.arg1 = type;
            message.what = MsgDef.MSG_SHOW_RELATIONSHIP_WINDOW;
            MsgDispatcher.getInstance().sendMessage(message);
        } else {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
    }

    private void showMessageWindow() {
        if (AccountManager.getInstance().isLogin()) {
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_MESSAGE_WINDOW);
        } else {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.account_weibo_wechat:
                ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                String message = getContext().getResources().getString(R.string.account_copy);
                cmb.setText(message);
                String tes = getContext().getResources().getString(R.string.account_copyed) + cmb.getText();
                ToastHelper.showToast(getContext(), tes, Toast.LENGTH_SHORT);
                break;
        }
        return false;
    }

    public void setMessageUnreadCount() {
        if (MessageUnreadCountRequestHelper.getBean() == null) {
            return;
        }
        int msgAllCount = MessageUnreadCountRequestHelper.getBean().getAllCount();
        int sameCount = MessageUnreadCountRequestHelper.getBean().praiseMeCount;
        if (msgAllCount != 0) {
            mMessageUnreadCount.setVisibility(View.VISIBLE);
            mUnreadSamePoint.setVisibility(GONE);
            mMessageUnreadCount.setText(String.valueOf(msgAllCount));
        } else if (sameCount != 0) {
            mUnreadSamePoint.setVisibility(VISIBLE);
            mMessageUnreadCount.setVisibility(View.GONE);
        } else {
            mUnreadSamePoint.setVisibility(GONE);
            mMessageUnreadCount.setVisibility(View.GONE);
        }
    }

    private void privacyProtection() {
        Message msg = Message.obtain();
        if (PrivacyProtectionHelper.isOpenPrivacyProtection()) {
            msg.arg1 = Constant.PRIVACY_CLOSE_PASSWORD;
        } else {
            msg.arg1 = Constant.PRIVACY_OPEN_PASSWORD;
        }
        msg.what = MsgDef.MSG_SHOW_PRIVACY_PROTECTION_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }
}
