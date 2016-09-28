package com.sjy.ttclub.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.lsym.ttclub.R;
import com.sjy.ttclub.share.ShareSender;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;

import java.io.ByteArrayOutputStream;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public abstract class BaseHandler {
    abstract void setActivity(Activity activity);

    abstract void startLogin(ThirdpartyLoginCallback callback);

    abstract void startShare(Intent shareIntent);

    abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public static void toastAppNotInstall() {
        ToastHelper.showToast(R.string.share_app_not_installed);
    }

    public static void toastAppVersionLow() {
        ToastHelper.showToast(R.string.share_app_verion_low);
    }

    public byte[] getImageThumbnail(String filePath) {
        Bitmap bitmap = getImageThumbnailBitmap(filePath);
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] result = output.toByteArray();
        bitmap.recycle();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Bitmap getImageThumbnailBitmap(String filePath) {
        Bitmap bitmap = BitmapUtil.createBitmapThumbnail(filePath, BitmapUtil.THUMBNAIL_SIZE, BitmapUtil
                .THUMBNAIL_SIZE);
        if (bitmap == null) {
            bitmap = BitmapUtil.createBitmapThumbnail(ShareSender.getInstance().getDefaultImageUrl(), BitmapUtil
                    .THUMBNAIL_SIZE, BitmapUtil.THUMBNAIL_SIZE);
            if (bitmap == null) {
                Bitmap appIcon = ResourceHelper.getBitmap(R.drawable.app_icon);
                bitmap = Bitmap.createBitmap(appIcon, 0, 0, BitmapUtil.THUMBNAIL_SIZE, BitmapUtil
                        .THUMBNAIL_SIZE);
                appIcon.recycle();
            }
        }
        return bitmap;
    }
}
