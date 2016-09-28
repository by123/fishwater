package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.AlphaImageView;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/10/31.
 * Email: linhaizhong@ta2she.com
 */
public class RecordFloatView extends LinearLayout {
    private OnFloatItemClickListener mItemClickListener;

    public RecordFloatView(Context context) {
        this(context, null);
    }

    public RecordFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordFloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setFloatItemClickListener(OnFloatItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setupFloatItemView(ArrayList<FloatItemInfo> itemList) {
        this.removeAllViewsInLayout();
        ItemLayout itemLayout;
        int width = getResources().getDimensionPixelSize(R.dimen.record_float_view_item_width);
        int height = getResources().getDimensionPixelSize(R.dimen.record_float_view_item_height);
        int margin = getResources().getDimensionPixelSize(R.dimen.space_10);
        LayoutParams lp = new LayoutParams(width, height);
        lp.bottomMargin = margin;
        for (FloatItemInfo info : itemList) {
            itemLayout = new ItemLayout(getContext());
            itemLayout.setupItem(info);
            addView(itemLayout, lp);
        }
    }

    private class ItemLayout extends FrameLayout {
        private AlphaImageView mIconView;
        private TextView mTopView;
        private FloatItemInfo mItemInfo;

        public ItemLayout(Context context) {
            super(context);
            initItemLayout();
        }

        private void initItemLayout() {
            int width = getResources().getDimensionPixelSize(R.dimen.record_float_view_item_width);
            int height = getResources().getDimensionPixelSize(R.dimen.record_float_view_item_height);
            int iconSize = getResources().getDimensionPixelSize(R.dimen.record_float_view_item_icon_size);
            mIconView = new AlphaImageView(getContext());
            mIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(mItemInfo);
                    }
                }
            });
            LayoutParams lp = new LayoutParams(iconSize, iconSize);
            lp.gravity = Gravity.CENTER;
            addView(mIconView, lp);

            mTopView = new TextView(getContext());
            mTopView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.record_window_text_size));
            mTopView.setGravity(Gravity.CENTER);
            mTopView.setTextColor(getResources().getColor(R.color.white));
            lp = new LayoutParams(width, height);
            addView(mTopView, lp);
        }

        public void setupItem(FloatItemInfo info) {
            mItemInfo = info;
            mIconView.setImageDrawable(info.icon);
            mTopView.setText(info.topIconText);
        }
    }

    public static class FloatItemInfo {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_HAS_TOPICON = 1;
        public int id;
        public int type = TYPE_NORMAL;
        public Drawable icon;
        public String topIconText = "0";
    }

    public interface OnFloatItemClickListener {
        void onItemClick(FloatItemInfo info);
    }
}
