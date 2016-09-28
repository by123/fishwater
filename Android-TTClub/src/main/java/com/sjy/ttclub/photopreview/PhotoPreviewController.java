package com.sjy.ttclub.photopreview;

import android.os.Message;

import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by linhz on 2015/12/16.
 * Email: linhaizhong@ta2she.com
 */
public class PhotoPreviewController extends DefaultWindowController implements BasePanel.PanelListener {
    private PhotoPreviewPanel mPreviewPanel;

    public PhotoPreviewController() {
        registerMessage(MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW) {
            if (msg.obj instanceof PhotoPreviewInfo) {
                startPhotoPreview((PhotoPreviewInfo) msg.obj);
            }
        }
    }

    private void startPhotoPreview(PhotoPreviewInfo info) {
        if (mPreviewPanel != null && mPreviewPanel.isShowing()) {
            return;
        }
        if (mPreviewPanel != null) {
            mPreviewPanel.hidePanel();
        }
        mPreviewPanel = new PhotoPreviewPanel(mContext);
        mPreviewPanel.setPanelListener(this);
        mPreviewPanel.setupPreviewPanel(info);
        mPreviewPanel.showPanel();
    }

    @Override
    public void onPanelHide() {
        mPreviewPanel = null;
    }

    @Override
    public void onPanelShow() {

    }
}
