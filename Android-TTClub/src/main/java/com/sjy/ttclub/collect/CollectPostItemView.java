/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.collect;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.collect.CollectPostBean;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectPostItemView extends CollectBaseItemView {

    private CollectListRequest.CollectItemInfo mItemInfo;
    private CheckBox mCheckBox;
    private TextView mTextView;

    public CollectPostItemView(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        addCheckBox();
        addTextView();
    }

    private void addCheckBox() {
        mCheckBox = new CheckBox(getContext());
        mCheckBox.setVisibility(View.GONE);
        mCheckBox.setButtonDrawable(ResourceHelper.getDrawable(R.drawable.checkbox_green));
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemInfo.isSelected = mCheckBox.isChecked();
                onSelecteStateChanged(mCheckBox.isChecked());
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_10);
        this.addView(mCheckBox, lp);
    }

    private void addTextView() {
        Resources res = getResources();
        mTextView = new TextView(getContext());
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.space_14));
        mTextView.setTextColor(res.getColor(R.color.black));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_16);
        lp.topMargin = lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.space_16);
        this.addView(mTextView, lp);
    }

    @Override
    public void setupItemView(CollectListRequest.CollectItemInfo info, boolean enableCheckbox) {
        mItemInfo = info;
        mCheckBox.setChecked(info.isSelected);
        if (enableCheckbox) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        CollectPostBean.PostInfo articleInfo = (CollectPostBean.PostInfo) info.data;
        mTextView.setText(articleInfo.getTitle());
    }
}
