/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sjy.ttclub.collect.CollectListRequest.CollectItemInfo;
import com.sjy.ttclub.common.CommonConst;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectListViewAdapter extends ArrayAdapter<CollectItemInfo> {
    private int mType;
    private boolean mEnableCheckbox = false;
    private CollectBaseItemView.ItemSelectStateListener mListener;

    public CollectListViewAdapter(Context context, ArrayList<CollectItemInfo> list) {
        super(context, 0, list);
    }

    public void setType(int type) {
        mType = type;
    }

    public void setEditMode(boolean enable) {
        mEnableCheckbox = enable;
        notifyDataSetChanged();
    }

    public void setSelectStateListener(CollectBaseItemView.ItemSelectStateListener listener) {
        mListener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CollectItemInfo itemInfo = getItem(position);
        if (convertView == null) {
            convertView = createView();
        }
        ((CollectBaseItemView) convertView).setupItemView(itemInfo, mEnableCheckbox);
        return convertView;
    }

    private CollectBaseItemView createView() {
        CollectBaseItemView view = null;
        if (mType == CommonConst.COLLECT_TYPE_POST) {
            view = new CollectPostItemView(getContext());
        } else if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
            view = new CollectArticleItemView(getContext());
        } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
            view = new CollectGoodsItemVew(getContext());
        }
        view.setSelectStateListener(mListener);
        return view;
    }


}
