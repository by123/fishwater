package com.sjy.ttclub.share;

import android.content.Intent;
import android.os.Message;

import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/12/16.
 * Email: linhaizhong@ta2she.com
 */
public class ShareController extends DefaultWindowController {

    public ShareController() {
        registerMessage(MsgDef.MSG_SHARE);
        ShareSender.init(mContext);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHARE) {
            if (msg.obj instanceof Intent) {
                startShare((Intent) msg.obj);
            }
        }
    }

    private void startShare(Intent intent) {
        String target = ShareIntentBuilder.parseTargetPlatform(intent);
        if (StringUtils.isEmpty(target)) {
            ShareHelper helper = new ShareHelper(mContext, intent);
            helper.showSharePanel();
        } else {
            ShareSender.getInstance().sendShare(target, intent);
        }

    }
}
