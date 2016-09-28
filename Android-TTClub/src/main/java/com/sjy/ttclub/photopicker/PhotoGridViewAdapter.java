package com.sjy.ttclub.photopicker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sjy.ttclub.util.PhotoCacheHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoGridViewAdapter extends ArrayAdapter<PhotoInfo> {
    private int mColumnsSize;

    private int mMaxNum = -1;

    private PhotoThumbItemView.IPhotoStateChangeListener mListener;

    private PhotoCacheHelper mCacheHelper;

    public PhotoGridViewAdapter(Context context, ArrayList<PhotoInfo> list) {
        super(context, 0, list);
    }

    public void setColumnsSize(int size) {
        mColumnsSize = size;
    }

    public void setPhotoStateChangeListener(PhotoThumbItemView.IPhotoStateChangeListener listener) {
        mListener = listener;
    }

    public void setMaxNum(int maxNum) {
        mMaxNum = maxNum;
    }

    public void setCacheHelper(PhotoCacheHelper helper) {
        mCacheHelper = helper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoThumbItemView itemView;
        if (convertView == null) {
            itemView = new PhotoThumbItemView(getContext(), mColumnsSize);
            itemView.setPhotoStateChangeListener(mListener);
            itemView.setMaxNum(mMaxNum);
            itemView.setCacheHelper(mCacheHelper);
            convertView = itemView;
        } else {
            itemView = (PhotoThumbItemView) convertView;
        }
        itemView.setupItemView(getItem(position));
        return convertView;
    }
}
