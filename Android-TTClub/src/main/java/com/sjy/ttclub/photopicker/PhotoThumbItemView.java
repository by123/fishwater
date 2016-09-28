package com.sjy.ttclub.photopicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.PhotoCacheHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoThumbItemView extends FrameLayout {

    private ImageView mImageView;
    private View mMaskView;
    private View mCheckView;

    private PhotoInfo mPhotoInfo;

    private IPhotoStateChangeListener mListener;
    private int mColumnSize;
    private int mMaxNum = -1;

    private PhotoCacheHelper mCacheHelper;

    public PhotoThumbItemView(Context context, int size) {
        super(context);
        mColumnSize = size;
        initViews(size);
    }

    private void initViews(int size) {
        View parentView = View.inflate(getContext(), R.layout.photo_picker_item_view, null);
        FrameLayout.LayoutParams lp = new LayoutParams(size, size);
        this.addView(parentView, lp);

        mImageView = (ImageView) parentView.findViewById(R.id.photo_picker_image_view);
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPhotoInfo.isSelected && mListener != null && mMaxNum > 0) {
                    int num = mListener.getSelectedNum();
                    if (num >= mMaxNum) {
                        String text = ResourceHelper.getString(R.string.photo_picker_max_limit);
                        if (text != null) {
                            text = text.replace("#photo_num#", String.valueOf(mMaxNum));
                        }
                        ToastHelper.showToast(getContext(), text, Toast.LENGTH_LONG);
                        return;
                    }
                }
                mPhotoInfo.isSelected = !mPhotoInfo.isSelected;
                updateCheckState(mPhotoInfo.isSelected);
                if (mListener != null) {
                    mListener.onPhotoStateChanged(mPhotoInfo);
                }
            }
        });
        mMaskView = parentView.findViewById(R.id.photo_picker_mask_view);
        mCheckView = parentView.findViewById(R.id.photo_picker_checked_view);
    }

    public void setPhotoStateChangeListener(IPhotoStateChangeListener listener) {
        mListener = listener;
    }

    public void setMaxNum(int maxNum) {
        mMaxNum = maxNum;
    }

    public void setCacheHelper(PhotoCacheHelper helper) {
        mCacheHelper = helper;
    }

    public void setupItemView(PhotoInfo info) {
        mPhotoInfo = info;
        updateCheckState(info.isSelected);
        final String photoPath = info.path;
        if (StringUtils.isEmpty(photoPath)) {
            mImageView.setImageDrawable(PhotoCacheHelper.getDefaultIcon());
        } else {
            Bitmap bitmap = mCacheHelper.getThumbnailBitmap(photoPath,
                    mColumnSize, new PhotoCacheHelper.IThumbnailBitmapCallback() {

                        @Override
                        public void onResult(Bitmap bitmap, String path) {
                            boolean sameInfo = false;
                            if (path != null
                                    && path.equals(mPhotoInfo.path)) {
                                sameInfo = true;
                            }
                            if (bitmap != null && sameInfo) {
                                mImageView.setImageBitmap(bitmap);
                            }
                        }
                    });
            if (bitmap == null) {
                mImageView.setImageDrawable(PhotoCacheHelper.getDefaultIcon());
            }
        }
    }

    private void updateCheckState(boolean isSelected) {
        mMaskView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        mCheckView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }

    public PhotoInfo getPhotoInfo() {
        return mPhotoInfo;
    }

    public interface IPhotoStateChangeListener {
        void onPhotoStateChanged(PhotoInfo info);

        int getSelectedNum();
    }
}
