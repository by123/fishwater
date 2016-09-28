package com.sjy.ttclub.community;

import android.os.Message;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.comment.CommentDetailWindow;
import com.sjy.ttclub.community.allcirclespage.CommunityAllCircleWindow;
import com.sjy.ttclub.community.circledetailpage.CommunityCircleDetailWindow;
import com.sjy.ttclub.community.circledetailpage.CommunityCircleInfoDetailWindow;
import com.sjy.ttclub.community.homepage.CommunityMainTab;
import com.sjy.ttclub.community.homepage.CommunityWindow;
import com.sjy.ttclub.community.model.CommunityReportHelper;
import com.sjy.ttclub.community.postdetailpage.CommunityNoPassPostDetailWindow;
import com.sjy.ttclub.community.postdetailpage.CommunityPostDetailWindow;
import com.sjy.ttclub.community.postdetailpage.CommunitySendPostWindow;
import com.sjy.ttclub.community.userinfopage.CommunityUserInfoWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;

/**
 * Created by zwl on 2015/11/9.
 * Email: 1501448275@qq.com
 */
public class CommunityController extends DefaultWindowController {
    private CommunityMainTab mCommunityTab;

    public CommunityController() {
        registerMessage(MsgDef.MSG_GET_COMMUNITY_TAB);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_ALL_CIRCLES_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_NO_PASS_POST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_COMMUNITY_CIRCLE_INFO_DETAIL_WINDOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_APP_MD5_CHANGED);

    }

    private void showCommunityWindow() {
        if (getCurrentWindow() instanceof CommunityWindow) {
            return;
        }
        CommunityWindow window = new CommunityWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityAllCircleWindow() {
        if (getCurrentWindow() instanceof CommunityAllCircleWindow) {
            return;
        }
        CommunityAllCircleWindow window = new CommunityAllCircleWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityCircleDetailWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunityCircleDetailWindow) {
            return;
        }
        CommunityCircleDetailWindow window = new CommunityCircleDetailWindow(mContext, this, msg.arg2);
        window.setCirlceInfo(msg.arg1);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityCircleInfoDetailWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunityCircleInfoDetailWindow) {
            return;
        }
        CommunityCircleInfoDetailWindow window = new CommunityCircleInfoDetailWindow(mContext, this);
        window.setCircleInfo((CommunityCircleBean)msg.obj);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunitySendPostWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunitySendPostWindow) {
            return;
        }
        CommunitySendPostWindow window = new CommunitySendPostWindow(mContext, this);
        window.setCircleInfo((CommunityCircleBean) msg.obj);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityPostDetailWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunityPostDetailWindow) {
            return;
        }
        CommunityPostDetailWindow window = new CommunityPostDetailWindow(mContext, this);
        window.setPostId(msg.arg1);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityCommentDetailWindow(Message msg) {
        if (getCurrentWindow() instanceof CommentDetailWindow) {
            return;
        }
        CommentDetailWindow window = new CommentDetailWindow(mContext, this);
        window.setCreateInfo(msg.arg1, msg.arg2);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityUserInfoWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunityUserInfoWindow) {
            return;
        }
        CommunityUserInfoWindow window = new CommunityUserInfoWindow(mContext, this);
        window.setUserInfo(msg.arg1);
        mWindowMgr.pushWindow(window);
    }

    private void showCommunityNoPassPostWindow(Message msg) {
        if (getCurrentWindow() instanceof CommunityNoPassPostDetailWindow) {
            return;
        }
        CommunityNoPassPostDetailWindow window = new CommunityNoPassPostDetailWindow(mContext, this);
        window.setPostId(msg.arg1);
        mWindowMgr.pushWindow(window);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_WINDOW) {
            showCommunityWindow();
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_ALL_CIRCLES_WINDOW) {
            showCommunityAllCircleWindow();
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW) {
            showCommunityCircleDetailWindow(msg);
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_SEND_POST_WINDOW) {
            showCommunitySendPostWindow(msg);
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW) {
            showCommunityPostDetailWindow(msg);
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_COMMENT_DETAIL_WINDOW) {
            showCommunityCommentDetailWindow(msg);
            return;
        }
        if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW) {
            showCommunityUserInfoWindow(msg);
            return;
        }
        if(msg.what==MsgDef.MSG_SHOW_COMMUNITY_NO_PASS_POST_WINDOW){
            showCommunityNoPassPostWindow(msg);
            return;
        }
        if(msg.what==MsgDef.MSG_SHOW_COMMUNITY_CIRCLE_INFO_DETAIL_WINDOW){
            showCommunityCircleInfoDetailWindow(msg);
            return;
        }
    }

    private void ensureCommunityTab() {
        if (mCommunityTab == null) {
            mCommunityTab = new CommunityMainTab(mContext, this);
        }
    }

    @Override
    public Object handleMessageSync(Message msg) {
        if (msg.what == MsgDef.MSG_GET_COMMUNITY_TAB) {
            ensureCommunityTab();
            return mCommunityTab;
        }
        return super.handleMessageSync(msg);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_APP_MD5_CHANGED) {
            AppMd5Helper.Md5Type type = (AppMd5Helper.Md5Type) notification.extObj;
            if (type == AppMd5Helper.Md5Type.REPORT) {
                handleReportMd5Changed();
            }
        }
    }

    private void handleReportMd5Changed() {
        CommunityReportHelper reportHelper = new CommunityReportHelper(mContext);
        reportHelper.getReportReasonsListFromServer(mContext);
    }
}
