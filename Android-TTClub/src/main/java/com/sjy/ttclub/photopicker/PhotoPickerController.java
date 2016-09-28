package com.sjy.ttclub.photopicker;

import android.os.Message;

import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
public class PhotoPickerController extends DefaultWindowController {


    public PhotoPickerController() {
        registerMessage(MsgDef.MSG_SHOW_PHOTO_PICKER_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_PHOTO_CROP_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_PHOTO_PICKER_WINDOW) {
            if (msg.obj instanceof PhotoPickerCallback) {
                showPickerWindow((PhotoPickerCallback) msg.obj);
            }
        } else if (msg.what == MsgDef.MSG_SHOW_PHOTO_CROP_WINDOW) {
            if (msg.obj instanceof PhotoCropCallback) {
                boolean anim = msg.arg1 == 0;
                showCropWindow((PhotoCropCallback) msg.obj, anim);
            }
        }
    }

    private void showPickerWindow(PhotoPickerCallback callback) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof PhotoPickerWindow) {
            return;
        }
        PhotoPickerWindow pickerWindow = new PhotoPickerWindow(mContext, this);
        pickerWindow.setResultCallback(callback);
        mWindowMgr.pushWindow(pickerWindow);
    }

    private void showCropWindow(PhotoCropCallback callback, boolean anim) {
        if (callback.getOrigPhotoPath() == null) {
            return;
        }

        AbstractWindow window = getCurrentWindow();
        if (window instanceof PhotoCropWindow) {
            return;
        }
        PhotoCropWindow pickerWindow = new PhotoCropWindow(mContext, this);
        pickerWindow.setCropCallback(callback);
        mWindowMgr.pushWindow(pickerWindow, anim);
    }
}
