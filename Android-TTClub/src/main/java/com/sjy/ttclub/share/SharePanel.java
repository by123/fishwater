package com.sjy.ttclub.share;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.BasePanel;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/11/12.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class SharePanel extends BasePanel {

    private PlatformLayout mPlatformLayout;
    private Intent mShareIntent;

    private ISharePanelListener mPlatformListener;

    public SharePanel(Context context) {
        super(context);
    }

    @Override
    protected View onCreateContentView() {
        mPlatformLayout = new PlatformLayout(mContext);
        return mPlatformLayout;
    }

    public void setListener(ISharePanelListener listener) {
        mPlatformListener = listener;
    }

    public void setupPlatformWindow(Intent shareIntent) {
        if (mPlatformListener == null) {
            return;
        }
        mShareIntent = shareIntent;
        ArrayList<SharePlatformInfo> platformList = SharePlatformConfig.getPlatformList(mContext);
        if (platformList.isEmpty()) {
            return;
        }
        for (int i = 0; i < platformList.size(); i++) {
            SharePlatformInfo info = platformList.get(i);
            mPlatformLayout.addPlatformView(info);
        }
    }

    private class PlatformLayout extends ScrollView implements View.OnClickListener {
        private int mMaxHeight;

        private PlatformContainer mPlatformContainer;

        public PlatformLayout(Context context) {
            super(context);
            init();
            setBackgroundResource(R.drawable.share_panel_bg);
        }

        private void init() {
            setVerticalScrollBarEnabled(false);
            Resources res = getResources();
            mMaxHeight = res.getDimensionPixelSize(R.dimen.share_panel_max_height);

            mPlatformContainer = new PlatformContainer(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.topMargin = res.getDimensionPixelSize(R.dimen.share_panel_marginTop);
            lp.bottomMargin = res.getDimensionPixelSize(R.dimen.share_panel_marginBottom);
            lp.leftMargin = res.getDimensionPixelSize(R.dimen.share_panel_marginHorizontal);
            lp.rightMargin = lp.leftMargin;
            this.addView(mPlatformContainer, lp);
        }

        public void addPlatformView(SharePlatformInfo info) {
            PlatformItemView itemView = new PlatformItemView(getContext());
            itemView.setOnClickListener(this);
            itemView.setupItemView(info);
            PlatformItemLayoutParams lp = new PlatformItemLayoutParams();
            mPlatformContainer.addView(itemView, lp);
        }

        @Override
        public void onClick(View v) {
            if (mPlatformListener == null) {
                return;
            }
            if (v instanceof PlatformItemView && mPlatformListener != null) {
                SharePlatformInfo platformInfo = ((PlatformItemView) v).getPlatformInfo();
                mPlatformListener.onSharePlatformClick(platformInfo);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxHeight = Math.min(screenHeight, mMaxHeight);
            if (measuredHeight > maxHeight) {
                measuredHeight = maxHeight;
            }
            setMeasuredDimension(measuredWidth, measuredHeight);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private static class PlatformContainer extends ViewGroup {
        private int mWidthGap;
        private int mHeightGap;
        private int mChildWidth;
        private int mChildHeight;

        public PlatformContainer(Context context) {
            super(context);
            Resources res = getResources();
            mChildWidth = res.getDimensionPixelSize(R.dimen.share_platform_item_width);
            mChildHeight = res.getDimensionPixelSize(R.dimen.share_platform_item_height);
            mWidthGap = res.getDimensionPixelSize(R.dimen.share_platform_item_width_gap);
            mHeightGap = res.getDimensionPixelSize(R.dimen.share_platform_item_height_gap);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child == null) {
                    continue;
                }
                if (child.getVisibility() != GONE) {
                    PlatformItemLayoutParams lp = (PlatformItemLayoutParams) child.getLayoutParams();
                    int childLeft = lp.x + getPaddingLeft();
                    int childTop = lp.y + getPaddingTop();
                    child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

            int childCount = getChildCount();
            int colums = widthSpecSize / (mChildWidth + mWidthGap);
            int rows = 1;
            int newWidthGap = mWidthGap;
            if (colums > 0 && colums > 1) {
                int remains = widthSpecSize % (mChildWidth + mWidthGap);
                int remainGap = remains / (colums - 1);
                newWidthGap += remainGap;
                rows = childCount / colums;
                if (childCount % colums > 0) {
                    rows += 1;
                }
            }

            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
            int newHeight = heightSpecSize;
            newHeight = getPaddingTop() + getPaddingBottom() + (rows * mChildHeight) + ((rows - 1) * mHeightGap);

            if (newHeight < heightSpecSize) {
                newHeight = heightSpecSize;
            }

            setMeasuredDimension(widthSpecSize, newHeight);

            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                measureChild(child, newWidthGap, mHeightGap, i, colums, rows);
            }
            setMeasuredDimension(widthSpecSize, newHeight);
        }

        protected void measureChild(View child, int widthGap, int heightGap, int index, int colums, int rows) {
            if (child == null || colums <= 0) {
                return;
            }

            final int width = mChildWidth;
            final int height = mChildHeight;
            final int indexX = index % colums;
            final int indexY = index / colums;
            PlatformItemLayoutParams lp = (PlatformItemLayoutParams) child.getLayoutParams();
            lp.setup(width, height, widthGap, heightGap, indexX, indexY);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childheightMeasureSpec);
        }

    }

    private class PlatformItemLayoutParams extends ViewGroup.MarginLayoutParams {
        public int x;
        public int y;

        public PlatformItemLayoutParams() {
            super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        public void setup(int width, int height, int widthGap, int heightGap, int indexX, int indexY) {
            this.width = width - leftMargin - rightMargin;
            this.height = height - topMargin - bottomMargin;

            x = indexX * (width + widthGap) + leftMargin;
            y = indexY * (height + heightGap) + topMargin;
        }

    }

    private static class PlatformItemView extends LinearLayout {

        private TextView mTitleView;
        private ImageView mIconView;
        private SharePlatformInfo mInfo;

        public PlatformItemView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            setOrientation(VERTICAL);
            addIconView();
            addTitleView();
            setBackgroundResource(R.drawable.share_platform_item_bg);
        }

        private void addIconView() {
            Resources res = getResources();
            mIconView = new ImageView(getContext());
            mIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconView.setFocusable(false);
            mIconView.setClickable(false);
            int iconWidth = res.getDimensionPixelSize(R.dimen.share_platform_item_icon_width);
            int iconHeight = res.getDimensionPixelSize(R.dimen.share_platform_item_icon_height);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iconWidth, iconHeight);
            lp.topMargin = res.getDimensionPixelSize(R.dimen.share_platform_item_icon_marginTop);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            this.addView(mIconView, lp);
        }

        private void addTitleView() {
            Resources res = getResources();
            mTitleView = new TextView(getContext());
            mTitleView.setFocusable(false);
            mTitleView.setClickable(false);

            mTitleView.setGravity(Gravity.CENTER);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen
                    .share_platform_item_text_size));
            mTitleView.setTextColor(res.getColor(R.color.share_platform_text_color));
            mTitleView.setSingleLine(false);
            mTitleView.setMaxLines(2);
            mTitleView.setEllipsize(TextUtils.TruncateAt.END);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            lp.topMargin = res.getDimensionPixelSize(R.dimen.share_platform_item_text_marginTop);
            lp.leftMargin = res.getDimensionPixelSize(R.dimen.share_platform_item_text_marginHorizontal);
            lp.rightMargin = res.getDimensionPixelSize(R.dimen.share_platform_item_text_marginHorizontal);
            this.addView(mTitleView, lp);
        }

        public void setupItemView(SharePlatformInfo info) {
            mInfo = info;
            mIconView.setImageDrawable(info.icon);
            mTitleView.setText(info.title);
        }

        public SharePlatformInfo getPlatformInfo() {
            return mInfo;
        }

    }

    public interface ISharePanelListener {
        void onSharePlatformClick(SharePlatformInfo info);
    }


}
