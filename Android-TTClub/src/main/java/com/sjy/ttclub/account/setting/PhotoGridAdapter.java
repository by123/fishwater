package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.PhotoCacheHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.AlphaImageView;

import java.util.ArrayList;

/**
 * Created by gangqing on 2015/12/24.
 * Email:denggangqing@ta2she.com
 */
public class PhotoGridAdapter extends ArrayAdapter<PhotoGridAdapter.PhotoItem> implements View.OnClickListener {
    private Context mContext;

    private PhotoCacheHelper mCacheHelper;

    private int mThumbSize;

    private IPhotoAdapterListener mListener;

    public PhotoGridAdapter(Context context, ArrayList<PhotoItem> list) {
        super(context, 0, list);
        mContext = context;
        mThumbSize = ResourceHelper.getDimen(R.dimen.space_60);
    }

    public void setCacheHelper(PhotoCacheHelper helper) {
        mCacheHelper = helper;
    }

    public void setAdapterListener(IPhotoAdapterListener listener) {
        mListener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.account_feedback_photo_grid_item, null);
        }
        setupViewByItem(convertView, getItem(position));
        return convertView;
    }

    private void setupViewByItem(View view, final PhotoItem item) {
        final AlphaImageView imageView = (AlphaImageView) view.findViewById(R.id.feedback_photo_grid_imageview);
        imageView.setOnClickListener(this);
        imageView.setTag(item);
        ImageView deleteView = (ImageView) view.findViewById(R.id.feedback_photo_grid_delete);
        deleteView.setTag(item);
        deleteView.setOnClickListener(this);
        if (item.type == PhotoItem.TYPE_ADD) {
            imageView.setImageResource(R.drawable.account_feedback_photo_grid_add_pic);
            deleteView.setVisibility(View.GONE);
        } else {
            deleteView.setVisibility(View.VISIBLE);
            Bitmap bitmap = mCacheHelper.getThumbnailBitmap(item.path, mThumbSize, new PhotoCacheHelper
                    .IThumbnailBitmapCallback() {
                @Override
                public void onResult(Bitmap bitmap, String path) {
                    PhotoItem photoItem = (PhotoItem) imageView.getTag();
                    boolean sameInfo = false;
                    if (path != null
                            && path.equals(photoItem.path)) {
                        sameInfo = true;
                    }
                    if (photoItem.type != PhotoItem.TYPE_ADD) {
                        if (bitmap != null && sameInfo) {
                            imageView.setImageBitmap(bitmap);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.account_feedback_photo_grid_add_pic);
                    }
                }
            });
            if (bitmap == null && item.type != PhotoItem.TYPE_ADD) {
                imageView.setImageDrawable(PhotoCacheHelper.getDefaultIcon());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        PhotoItem item = (PhotoItem) v.getTag();
        if (id == R.id.feedback_photo_grid_delete) {
            if (mListener != null) {
                mListener.onPhotoDelete(item);
            }
        } else if (id == R.id.feedback_photo_grid_imageview) {
            if (mListener != null) {
                mListener.onPhotoClicked(item);
            }
        }
    }

    public static class PhotoItem {
        public static final int TYPE_ADD = 0;
        public static final int TYPE_GALLERY = 1;
        public static final int TYPE_CAMERA = 2;
        public String path;
        public int type = TYPE_GALLERY;
    }

    public interface IPhotoAdapterListener {
        void onPhotoDelete(PhotoItem item);

        void onPhotoClicked(PhotoItem item);
    }
}
