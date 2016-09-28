/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.collect;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.collect.CollectProductBean;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/11/17.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectGoodsItemVew extends CollectBaseItemView {
    private CollectListRequest.CollectItemInfo mItemInfo;
    private SimpleDraweeView mImageView;
    private CheckBox mCheckBox;
    private TextView mTextView;
    private TextView mPriceView;

    public CollectGoodsItemVew(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        addCheckBox();
        addImageView();
        addTextView();
    }

    private void addCheckBox() {
        mCheckBox = new CheckBox(getContext());
        mCheckBox.setVisibility(View.GONE);
        mCheckBox.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_green));
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

    private void addImageView() {
        mImageView = new SimpleDraweeView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int size = getResources().getDimensionPixelSize(R.dimen.space_100);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_16);
        lp.topMargin = lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.space_10);
        this.addView(mImageView, lp);
    }

    private void addTextView() {
        Resources res = getResources();
        FrameLayout parentView = new FrameLayout(getContext());
        LinearLayout.LayoutParams parentLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        parentLp.leftMargin = res.getDimensionPixelSize(R.dimen.space_16);
        this.addView(parentView, parentLp);

        mTextView = new TextView(getContext());
        mTextView.setMaxLines(2);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.space_16));
        mTextView.setTextColor(res.getColor(R.color.black));
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        flp.topMargin = flp.bottomMargin = res.getDimensionPixelSize(R.dimen.space_12);
        parentView.addView(mTextView, flp);

        mPriceView = new TextView(getContext());
        mPriceView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.space_12));
        mPriceView.setTextColor(res.getColor(R.color.collect_price_color));
        flp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        flp.bottomMargin = res.getDimensionPixelSize(R.dimen.space_10);
        parentView.addView(mPriceView, flp);
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
        CollectProductBean.ProductInfo productInfo = (CollectProductBean.ProductInfo) info.data;
        String imageUrl = productInfo.getThumbUrl();
        if (StringUtils.isEmpty(imageUrl)) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageURI(Uri.parse(imageUrl));
        }
        mTextView.setText(productInfo.getTitle());
        mPriceView.setText("￥" + String.format("%.2f", productInfo.getSalePrice()));
    }
}
