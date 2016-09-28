package com.sjy.ttclub.comment;

import android.os.Message;

import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by linhz on 2015/12/30.
 * Email: linhaizhong@ta2she.com
 */
public class CommentController extends DefaultWindowController {

    public CommentController() {
        registerMessage(MsgDef.MSG_SHOW_COMMENT_LIST_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_COMMENT_LIST_WINDOW) {
            showCommentListWindow(msg);
        } else if (msg.what == MsgDef.MSG_SHOW_COMMUNITY_COMMENT_DETAIL_WINDOW) {

        }
    }

    private void showCommentListWindow(Message msg) {
        if (!(msg.obj instanceof CommentListInfo)) {
            return;
        }

        if (getCurrentWindow() instanceof CommentListWindow) {
            return;
        }
        CommentListWindow listWindow = new CommentListWindow(mContext, this);
        listWindow.setupWindow((CommentListInfo) msg.obj);
        mWindowMgr.pushWindow(listWindow);
    }
}
