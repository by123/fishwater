package com.sjy.ttclub.homepage.feeddetail;

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
public class HomepageFeedFloatView extends LinearLayout {
    private OnFloatItemClickListener mItemClickListener;

    public HomepageFeedFloatView(Context context) {
        this(context, null);
    }

    public HomepageFeedFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomepageFeedFloatView(Context context, AttributeSet attrs, int defStyle) {
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
        ItemLayout itemLayout = null;
        int width = getResources().getDimensionPixelSize(R.dimen.space_100);
        int height = getResources().getDimensionPixelSize(R.dimen.space_60);
        LinearLayout.LayoutParams lp = null;
        for (FloatItemInfo info : itemList) {
            itemLayout = new ItemLayout(getContext());
            itemLayout.setupItem(info);
            lp = new LinearLayout.LayoutParams(width, height);
            this.addView(itemLayout, lp);
        }
    }

    public void updateItem(FloatItemInfo info) {
        int childCount = getChildCount();
        ItemLayout child = null;
        for (int i = 0; i < childCount; i++) {
            child = (ItemLayout) getChildAt(i);
            if (info.id == child.getItemInfo().id) {
                child.setupItem(info);
                return;
            }
        }
    }

    public void updateItemText(int id, String text) {
        int childCount = getChildCount();
        ItemLayout child = null;
        for (int i = 0; i < childCount; i++) {
            child = (ItemLayout) getChildAt(i);
            if (id == child.getItemInfo().id) {
                child.updateText(text);
                return;
            }
        }
    }

    public void updateItemIcon(int id, int resId) {
        int childCount = getChildCount();
        ItemLayout child = null;
        for (int i = 0; i < childCount; i++) {
            child = (ItemLayout) getChildAt(i);
            if (id == child.getItemInfo().id) {
                child.updateIcon(resId);
                return;
            }
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
            int width = getResources().getDimensionPixelSize(R.dimen.space_100);
            int height = getResources().getDimensionPixelSize(R.dimen.space_60);
            int iconSize = getResources().getDimensionPixelSize(R.dimen.space_30);
            mIconView = new AlphaImageView(getContext());
            mIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFloatItemClick(mItemInfo.id);
                    }
                }
            });
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(iconSize, iconSize);
            lp.gravity = Gravity.CENTER;
            this.addView(mIconView, lp);

            mTopView = new TextView(getContext());
            mTopView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen
                    .space_12));
            mTopView.setGravity(Gravity.CENTER_VERTICAL);
            mTopView.setTextColor(getResources().getColor(R.color.white));
            mTopView.setBackgroundResource(R.drawable.round_radius_red);
            lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams
                    .WRAP_CONTENT);
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.leftMargin = width / 2 + iconSize / 4;
            lp.topMargin = height / 2 - 3 * iconSize / 5;
            this.addView(mTopView, lp);
        }

        public void setupItem(FloatItemInfo info) {
            mItemInfo = info;
            mIconView.setImageDrawable(info.icon);
            if (info.type == FloatItemInfo.TYPE_NORMAL) {
                mTopView.setVisibility(View.GONE);
            } else {
                mTopView.setVisibility(View.VISIBLE);
            }
            mTopView.setText(info.topIconText);
        }

        public void updateText(String text) {
            mTopView.setText(text);
        }

        public void updateIcon(int resId) {
            mIconView.setImageResource(resId);
        }

        public FloatItemInfo getItemInfo() {
            return mItemInfo;
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
        void onFloatItemClick(int id);
    }


}
