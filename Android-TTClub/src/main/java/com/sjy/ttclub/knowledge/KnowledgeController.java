package com.sjy.ttclub.knowledge;

import android.os.Message;

import com.sjy.ttclub.AppMd5Helper;
import com.sjy.ttclub.bean.KnowledgeArticleBean;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;

/**
 * Created by linhz on 2015/12/21.
 * Email: linhaizhong@ta2she.com
 */
public class KnowledgeController extends DefaultWindowController {

    public KnowledgeController() {
        registerMessage(MsgDef.MSG_SHOW_KNOWLEDGE_LIST_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_KNOWLEDGE_MAIN_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_APP_MD5_CHANGED);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_KNOWLEDGE_MAIN_WINDOW) {
            showMainWindow();
        } else if (msg.what == MsgDef.MSG_SHOW_KNOWLEDGE_LIST_WINDOW) {
            if (msg.obj instanceof KnowledgeDetailInfo) {
                showListWindow((KnowledgeDetailInfo) msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW) {
            if (msg.obj instanceof KnowledgeDetailInfo) {
                showDetailWindow((KnowledgeDetailInfo) msg.obj);
            }
        }
    }

    private void showMainWindow() {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof KnowledgeMainWindow) {
            return;
        }
        window = new KnowledgeMainWindow(mContext, this);
        mWindowMgr.pushWindow(window);
    }

    private void showListWindow(KnowledgeDetailInfo articleInfo) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof KnowledgeListWindow) {
            return;
        }
        KnowledgeListWindow listWindow = new KnowledgeListWindow(mContext, this);
        listWindow.setupListWindow(articleInfo);
        mWindowMgr.pushWindow(listWindow);
    }

    private void showDetailWindow(KnowledgeDetailInfo detailInfo) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof KnowledgeDetailWindow) {
            return;
        }
        KnowledgeDetailWindow detailWindow = new KnowledgeDetailWindow(mContext, this);
        detailWindow.setupWindow(detailInfo);
        mWindowMgr.pushWindow(detailWindow);
    }

    private void handleArticleMd5Changed() {
        KnowledgeDataHelper helper = new KnowledgeDataHelper(mContext);
        helper.tryGetKnowledgeData(new KnowledgeDataHelper.ResultCallback() {
            @Override
            public void onRequestFailed(int errorType) {
                //do something
            }

            @Override
            public void onRequestSuccess(KnowledgeArticleBean risePosture) {
                //do something
            }
        });
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_APP_MD5_CHANGED) {
            AppMd5Helper.Md5Type type = (AppMd5Helper.Md5Type) notification.extObj;
            if (type == AppMd5Helper.Md5Type.ARTICLE) {
                handleArticleMd5Changed();
            }
        }
    }
}
