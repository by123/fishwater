package com.sjy.ttclub.photopicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.UICallBacks;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ValueHolder;
import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.widget.dialog.ProgressDialog;

import java.io.IOException;

/**
 * Created by linhz on 2015/12/28.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoCropWindow extends AbstractWindow {

    private PhotoCropCallback mCropCallback;
    private PhotoCropImageView mCropImageView;

    public PhotoCropWindow(Context context, UICallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        initWindow();
    }

    private void initWindow() {
        View parentView = View.inflate(getContext(), R.layout.photo_crop_layout, null);
        getBaseLayer().addView(parentView, getBaseLayerLP());

        View view = parentView.findViewById(R.id.photo_crop_cancel);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBacks.onWindowExitEvent(PhotoCropWindow.this, true);
            }
        });

        view = parentView.findViewById(R.id.photo_crop_yes);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });

        mCropImageView = (PhotoCropImageView) parentView.findViewById(R.id.photo_crop_view);
    }

    private void startCrop() {
        final Bitmap bitmap = mCropImageView.getClipBitmap();
        if (bitmap == null) {
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setText(R.string.photo_saving);
        dialog.show();

        final ValueHolder holder = new ValueHolder();
        ThreadManager.postDelayed(ThreadManager.THREAD_WORK, new Runnable() {
            @Override
            public void run() {
                String path = CommonUtils.storePageSnapshot(bitmap);
                holder.strValue = path;
            }
        }, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (mCropCallback != null) {
                    mCropCallback.onPhotoCropResult(holder.strValue);
                }
                mCallBacks.onWindowExitEvent(PhotoCropWindow.this, false);
            }
        }, 500);

    }

    public void setCropCallback(PhotoCropCallback callback) {
        mCropCallback = callback;
        String path = callback.getOrigPhotoPath();
        if (path == null || !FileUtils.isFileExists(path)) {
            return;
        }
        setupWindow(path);
    }

    private void setupWindow(final String path) {
        final ValueHolder holder = new ValueHolder();
        ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapUtil.createBitmapThumbnail(path, HardwareUtil.windowWidth, HardwareUtil
                        .screenHeight);
                if (bitmap == null) {
                    return;
                }
                int angle = readPictureDegree(path);
                if (angle != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(angle);
                    // 创建新的图片
                    Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    holder.bitmap = rotateBitmap;
                } else {
                    holder.bitmap = bitmap;
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                mCropImageView.setBitmap(holder.bitmap);
            }
        });
    }

    private int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
