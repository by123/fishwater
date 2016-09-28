/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.collect;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ abstract class CollectBaseItemView extends LinearLayout {
    private ItemSelectStateListener mListener;

    CollectBaseItemView(Context context) {
        super(context);
    }

    abstract void setupItemView(CollectListRequest.CollectItemInfo info, boolean enableCheckbox);

    public void setSelectStateListener(ItemSelectStateListener listener) {
        mListener = listener;
    }

    protected void onSelecteStateChanged(boolean selected) {
        if (mListener != null) {
            mListener.onItemSelectStateChanged(selected);
        }
    }

    public interface ItemSelectStateListener {
        void onItemSelectStateChanged(boolean selected);
    }
}
