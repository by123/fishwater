/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.account;

import android.os.Bundle;
import android.os.Message;

import com.sjy.ttclub.account.login.BindPhoneWindow;
import com.sjy.ttclub.account.login.LoginWindow;
import com.sjy.ttclub.account.login.RetrievePasswordWindow;
import com.sjy.ttclub.account.login.SignupWindow;
import com.sjy.ttclub.account.login.VerificationWindow;
import com.sjy.ttclub.account.message.LetterChatWindow;
import com.sjy.ttclub.account.message.MessageWindow;
import com.sjy.ttclub.account.message.ReplyMeWindow;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.personal.ChangeNicknameWindow;
import com.sjy.ttclub.account.personal.ChangePasswordWindow;
import com.sjy.ttclub.account.personal.LettersBlacklistWindow;
import com.sjy.ttclub.account.personal.LevelWindow;
import com.sjy.ttclub.account.personal.PersonalWindow;
import com.sjy.ttclub.account.personal.RelationshipWindow;
import com.sjy.ttclub.account.personal.pricacy.PrivacyProtectionWindow;
import com.sjy.ttclub.account.setting.AlertSettingWindow;
import com.sjy.ttclub.account.setting.FeedbackContentWindow;
import com.sjy.ttclub.account.setting.FeedbackWindow;
import com.sjy.ttclub.account.setting.SettingWindow;
import com.sjy.ttclub.bean.account.FeedbackBean;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.community.userinfopage.CommunityMyPostWindow;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.ThreadManager;

/**
 * Created by 邓钢清 on 2015/11/9.
 * Email: denggangqing@ta2she.com
 */
public class AccountController extends DefaultWindowController {

    private AccountMainTab mAccountTab;
    private PrivacyProtectionWindow mPrivacyWindow;

    public AccountController() {
        registerMessage(MsgDef.MSG_GET_ACCOUNT_TAB);
        registerMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_SIGNUP_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_RETRIEVE_PASSWORD_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_SETTING_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_VERIFICATION_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_SETTING_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_LOGIN_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_BIND_PHONE_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_BIND_PHONE_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_RETRIEVE_PASSWORD_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_VERIFICATION_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_PERSONAL_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_PERSONAL_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_CHANGE_PASSWORD_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_PASSWORD_CHANGE_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_CHANGE_NICKNAME_WINDOW);
        registerMessage(MsgDef.MSG_CLOSE_CHANGE_NICKNAME_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_LEVEL_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_RELATIONSHIP_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_FEEDBACK_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_MESSAGE_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_REPLY_ME_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_MY_POST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_LETTERS_BLACKLIST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_FEEDBACK_CONTENT_WINDOW);
        registerMessage(MsgDef.MSG_VERSION_CHECK_FINISH);
        registerMessage(MsgDef.MSG_RELATIONSHIP_ACCOUNT_DATA_CHANGE);
        registerMessage(MsgDef.MSG_MESSAGE_HAVE_BLACK_CHANGE);
        registerMessage(MsgDef.MSG_SHOW_PRIVACY_PROTECTION_WINDOW);
        registerMessage(MsgDef.MSG_PRIVACY_PROTECTION_STATE_CHANGE);
        registerMessage(MsgDef.MSG_SHOW_ALERT_SETTING_WINDOW);
        registerMessage(MsgDef.MSG_GET_APP_PRIVACY_STATE);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_LOGIN_WINDOW) {
            showLoginWindow(msg);
        } else if (msg.what == MsgDef.MSG_SHOW_SIGNUP_WINDOW) {
            showSingupWindow(msg);
        } else if (msg.what == MsgDef.MSG_SHOW_RETRIEVE_PASSWORD_WINDOW) {
            showRetrievePasswordWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_SETTING_WINDOW) {
            showSettingWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_VERIFICATION_WINDOW) {
            Bundle bundle = (Bundle) msg.obj;
            showVerificationWindow(bundle);
        } else if (msg.what == MsgDef.MSG_CLOSE_SETTING_WINDOW) {
            closeSettingWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_LOGIN_WINDOW) {
            closeLoginWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_BIND_PHONE_WINDOW) {
            showBindPhoneWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_BIND_PHONE_WINDOW) {
            closeBindPhoneWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_RETRIEVE_PASSWORD_WINDOW) {
            closeRetrievePasswordWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_VERIFICATION_WINDOW) {
            closeVerificationWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_PERSONAL_WINDOW) {
            showPersonalWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_PERSONAL_WINDOW) {
            closePersonalWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_CHANGE_PASSWORD_WINDOW) {
            showChangePassword();
        } else if (msg.what == MsgDef.MSG_CLOSE_PASSWORD_CHANGE_WINDOW) {
            closePasswordChangeWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_CHANGE_NICKNAME_WINDOW) {
            showChangeNickname();
        } else if (msg.what == MsgDef.MSG_CLOSE_CHANGE_NICKNAME_WINDOW) {
            closeChangeNickname();
        } else if (msg.what == MsgDef.MSG_SHOW_LEVEL_WINDOW) {
            showLevelWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_RELATIONSHIP_WINDOW) {
            showRelationshipWindow(msg.arg1);
        } else if (msg.what == MsgDef.MSG_SHOW_FEEDBACK_WINDOW) {
            showFeedbackWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_FEEDBACK_WINDOW) {
            closeFeedbackWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_MESSAGE_WINDOW) {
            showMessageWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW) {
            if (msg.obj instanceof LetterChatParamBean) {
                showLetterChat((LetterChatParamBean) msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_SHOW_REPLY_ME_WINDOW) {
            showReplyMeWindow(msg.arg1);
        } else if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_MY_POST_WINDOW) {
            showCommunityMyPostWindow(msg);
        } else if (msg.what == MsgDef.MSG_SHOW_LETTERS_BLACKLIST_WINDOW) {
            showLettersBlacklistWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_FEEDBACK_CONTENT_WINDOW) {
            if (msg.obj instanceof FeedbackBean.FeedbackInfo) {
                showFeedbackContentWindow((FeedbackBean.FeedbackInfo) msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_VERSION_CHECK_FINISH) {
            setAccountMainUnread();
        } else if (msg.what == MsgDef.MSG_RELATIONSHIP_ACCOUNT_DATA_CHANGE) {
            setRelationshipDataChange();
        } else if (msg.what == MsgDef.MSG_MESSAGE_HAVE_BLACK_CHANGE) {
            initBlacklistData();
        } else if (msg.what == MsgDef.MSG_SHOW_PRIVACY_PROTECTION_WINDOW) {
            showPrivacyProtection(msg);
        } else if (msg.what == MsgDef.MSG_PRIVACY_PROTECTION_STATE_CHANGE) {
            setPrivacyProtection();
        } else if (msg.what == MsgDef.MSG_SHOW_ALERT_SETTING_WINDOW) {
            showAlertSettingWindow();
        } else if (msg.what == MsgDef.MSG_CLOSE_SIGNUP_WINDOW) {
            closeSignUpWindow();
        }
    }

    private void ensureAccountTab() {
        if (mAccountTab == null) {
            mAccountTab = new AccountMainTab(mContext, this);
        }
    }

    @Override
    public Object handleMessageSync(Message msg) {
        if (msg.what == MsgDef.MSG_GET_ACCOUNT_TAB) {
            ensureAccountTab();
            return mAccountTab;
        } else if (msg.what == MsgDef.MSG_SHOW_PRIVACY_PROTECTION_WINDOW) {
            showPrivacyProtection(msg);
        } else if (msg.what == MsgDef.MSG_GET_APP_PRIVACY_STATE) {
            if (mPrivacyWindow != null) {
                return mPrivacyWindow.isAppPrivacy();
            }
            return false;
        }
        return super.handleMessageSync(msg);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            closeLoginAndRegisterWindow();
        }
    }

    private void closeLoginAndRegisterWindow() {
        if (getCurrentWindow() instanceof LoginWindow) {
            mWindowMgr.popWindow(false);
        } else if (getCurrentWindow() instanceof SignupWindow) {
            mWindowMgr.popWindow(false);
        }
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                if (getCurrentWindow() instanceof LoginWindow) {
                    mWindowMgr.popWindow(false);
                } else if (getCurrentWindow() instanceof SignupWindow) {
                    mWindowMgr.popWindow(false);
                }
            }
        }, 10);
    }

    /**
     * 打开登录窗口
     */
    private void showLoginWindow(Message msg) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof LoginWindow) {
            return;
        }
        LoginWindow loginWindow = new LoginWindow(mContext, this);
        loginWindow.isOpenFromWhere(msg.arg1);
        mWindowMgr.pushWindow(loginWindow);
    }

    /**
     * 打开注册窗口
     */
    private void showSingupWindow(Message msg) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof SignupWindow) {
            return;
        }
        SignupWindow signupWindow = new SignupWindow(mContext, this);
        signupWindow.isOpenFromWhere(msg.arg1);
        /*if (window instanceof LoginWindow) {
            signupWindow.setPopBackWindowAfterPush(true);
        }*/
        mWindowMgr.pushWindow(signupWindow);
    }

    /**
     * 打开忘记密码窗口
     */
    private void showRetrievePasswordWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof RetrievePasswordWindow) {
            return;
        }
        RetrievePasswordWindow retrievePasswordWindow = new RetrievePasswordWindow(mContext, this);
        /*if (window instanceof LoginWindow) {
            retrievePasswordWindow.setPopBackWindowAfterPush(true);
        }*/
        mWindowMgr.pushWindow(retrievePasswordWindow);
    }

    /**
     * 打开设置窗口
     */
    private void showSettingWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof SettingWindow) {
            return;
        }
        SettingWindow settingWindow = new SettingWindow(mContext, this);
        mWindowMgr.pushWindow(settingWindow);
    }

    /**
     * 打开手机号码验证窗口
     *
     * @param bundle
     */
    private void showVerificationWindow(Bundle bundle) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof VerificationWindow) {
            return;
        }
        VerificationWindow verificationWindow = new VerificationWindow(mContext, this);
        verificationWindow.setTransitiveData(bundle);
//        if (window instanceof SignupWindow) {
//            verificationWindow.setPopBackWindowAfterPush(true);
//        }
        mWindowMgr.pushWindow(verificationWindow);
    }

    /**
     * 关闭设置窗口
     */
    private void closeSettingWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof SettingWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 关闭登录窗口
     */
    private void closeLoginWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof LoginWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 关闭登录窗口
     */
    private void closeSignUpWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof SignupWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 打开第三方登录后的绑定手机界面
     */
    private void showBindPhoneWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof BindPhoneWindow) {
            return;
        }
        BindPhoneWindow bindPhoneWindow = new BindPhoneWindow(mContext, this);
        if (window instanceof LoginWindow) {
            bindPhoneWindow.setPopBackWindowAfterPush(true);
        }
        mWindowMgr.pushWindow(bindPhoneWindow);
    }

    /**
     * 关闭第三方登录后的绑定手机界面
     */
    private void closeBindPhoneWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof BindPhoneWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 关闭忘记密码界面
     */
    private void closeRetrievePasswordWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof RetrievePasswordWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 关闭注册时绑定手机界面
     */
    private void closeVerificationWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof VerificationWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 打开个人中心界面
     */
    private void showPersonalWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof PersonalWindow) {
            return;
        }
        PersonalWindow personalWindow = new PersonalWindow(mContext, this);
        mWindowMgr.pushWindow(personalWindow);
    }

    /**
     * 关闭个人资料窗口
     */
    private void closePersonalWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof PersonalWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 打开修改密码窗口
     */
    private void showChangePassword() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ChangePasswordWindow) {
            return;
        }
        ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(mContext, this);
        mWindowMgr.pushWindow(changePasswordWindow);
    }

    /**
     * 关闭修改密码窗口
     */
    private void closePasswordChangeWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ChangePasswordWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 打开修改昵称窗口
     */
    private void showChangeNickname() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ChangeNicknameWindow) {
            return;
        }
        ChangeNicknameWindow changeNicknameWindow = new ChangeNicknameWindow(mContext, this);
        mWindowMgr.pushWindow(changeNicknameWindow);
    }

    /**
     * 关闭修改昵称窗口
     */
    private void closeChangeNickname() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ChangeNicknameWindow) {
            mWindowMgr.popWindow();
        }
    }

    /**
     * 打开等级窗口
     */
    private void showLevelWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof LevelWindow) {
            return;
        }
        LevelWindow levelWindow = new LevelWindow(mContext, this);
        mWindowMgr.pushWindow(levelWindow);
    }

    /**
     * 打开关注、粉丝界面
     *
     * @param type
     */
    private void showRelationshipWindow(int type) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof RelationshipWindow) {
            return;
        }
        RelationshipWindow relationshipWindow = new RelationshipWindow(mContext, this);
        relationshipWindow.setType(type);
        mWindowMgr.pushWindow(relationshipWindow);
    }

    /**
     * 打开反馈界面
     */
    private void showFeedbackWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof FeedbackWindow) {
            return;
        }
        FeedbackWindow feedbackWindow = new FeedbackWindow(mContext, this);
        mWindowMgr.pushWindow(feedbackWindow);
    }

    private void closeFeedbackWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof FeedbackWindow) {
            mWindowMgr.popWindow(true);
        }
    }

    /**
     * 打开消息窗口
     */
    private void showMessageWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof MessageWindow) {
            return;
        }
        MessageWindow messageWindow = new MessageWindow(mContext, this);
        mWindowMgr.pushWindow(messageWindow);
    }

    /**
     * 打开私信会话界面
     *
     * @param paramBean
     */
    private void showLetterChat(LetterChatParamBean paramBean) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof LetterChatWindow) {
            return;
        }
        LetterChatWindow letterChatWindow = new LetterChatWindow(mContext, this);
        letterChatWindow.setData(paramBean);
        mWindowMgr.pushWindow(letterChatWindow);
    }

    /**
     * 打开回复我的界面
     */
    private void showReplyMeWindow(int type) {
        AbstractWindow window = getCurrentWindow();
        ReplyMeWindow replyMeWindow = null;
        if (window instanceof ReplyMeWindow) {
            replyMeWindow = (ReplyMeWindow) window;
            replyMeWindow.reStart();
            return;
        }
        replyMeWindow = new ReplyMeWindow(mContext, this);
        replyMeWindow.setType(type);
        mWindowMgr.pushWindow(replyMeWindow);
    }

    /**
     * 打开我的发言界面
     */
    private void showCommunityMyPostWindow(Message message) {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }

        if (getCurrentWindow() instanceof CommunityMyPostWindow) {
            return;
        }
        CommunityMyPostWindow window = new CommunityMyPostWindow(mContext, this, message.arg1);
        mWindowMgr.pushWindow(window);
    }

    /**
     * 打开黑名单界面
     */
    private void showLettersBlacklistWindow() {
        if (getCurrentWindow() instanceof LettersBlacklistWindow) {
            return;
        }
        LettersBlacklistWindow window = new LettersBlacklistWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    /**
     * 打开反馈详情界面
     *
     * @param feedbackInfo
     */
    private void showFeedbackContentWindow(FeedbackBean.FeedbackInfo feedbackInfo) {
        if (getCurrentWindow() instanceof FeedbackContentWindow) {
            return;
        }
        FeedbackContentWindow window = new FeedbackContentWindow(mContext, this);
        window.setData(feedbackInfo);
        mWindowMgr.pushWindow(window);
    }

    /**
     * 设置版本更新
     */
    private void setAccountMainUnread() {
        if (mAccountTab != null) {
            mAccountTab.setVersionVisible();
        }
    }

    /**
     * 关注状态改变
     */
    private void setRelationshipDataChange() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof RelationshipWindow) {
            RelationshipWindow relationshipWindow = (RelationshipWindow) window;
            relationshipWindow.setAccountDataChange();
        }
    }

    /**
     * 黑名单更新
     */
    private void initBlacklistData() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof MessageWindow) {
            MessageWindow messageWindow = (MessageWindow) window;
            messageWindow.initData();
        }
    }

    /**
     * 打开隐私设置界面
     */
    private void showPrivacyProtection(Message msg) {
        if (getCurrentWindow() instanceof PrivacyProtectionWindow) {
            return;
        }
        PrivacyProtectionWindow window = new PrivacyProtectionWindow(mContext, this);
        window.setParams(msg.arg1);
        boolean anim = true;
        if (msg.arg1 == Constant.PRIVACY_INTO_APPLICATION) {
            mPrivacyWindow = window;
            anim = false;
        }
        mWindowMgr.pushWindow(window, anim);
    }

    /**
     * 隐私保护状态改变
     */
    private void setPrivacyProtection() {
        if (mAccountTab != null) {
            mAccountTab.setPrivacyProtection();
        }
    }

    /**
     * 找开提醒设置界面
     */
    private void showAlertSettingWindow() {
        if (getCurrentWindow() instanceof AlertSettingWindow) {
            return;
        }
        AlertSettingWindow window = new AlertSettingWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }


    @Override
    public void onWindowExitEvent(AbstractWindow window, boolean withAnimation) {
        super.onWindowExitEvent(window, withAnimation);
        if (window instanceof PrivacyProtectionWindow) {
            mPrivacyWindow = null;
        }
    }
}
