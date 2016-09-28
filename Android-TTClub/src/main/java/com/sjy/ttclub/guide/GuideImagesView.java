package com.sjy.ttclub.guide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

public class GuideImagesView extends FrameLayout {
    private ViewPager mImageViewPager = null;
    private ImageCycleAdapter mImageViewPagerAdapter;
    private IndicatorLayout mIndicatorLayout;

    private ArrayList<ImageInfo> mInfoList = new ArrayList<>();
    private Drawable mDefaultDrawable;

    private boolean mIsTouchDown = false;

    private float mRatio = 0;

    private OnGuidePageChangeListener mPageChangeListener;

    public GuideImagesView(Context context) {
        this(context, null);
    }

    public GuideImagesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        addImageViewPager();
        addImageIndicatorView();
    }

    private void addImageViewPager() {
        mImageViewPager = new ViewPager(getContext());


        mImageViewPager.setOnPageChangeListener(new PageChangeListener());
        mImageViewPagerAdapter = new ImageCycleAdapter();
        mImageViewPager.setAdapter(mImageViewPagerAdapter);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mImageViewPager, lp);
    }

    private void addImageIndicatorView() {
        mIndicatorLayout = new IndicatorLayout(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen
                .image_cycle_view_indicator_height));
        lp.bottomMargin = ResourceHelper.getDimen(R.dimen.space_60);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        this.addView(mIndicatorLayout, lp);
    }

    public void setAspectRatio(float ratio) {
        mRatio = ratio;
    }

    public void setDefaultDrawable(Drawable drawable) {
        mDefaultDrawable = drawable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setImageResources(ArrayList<ImageInfo> infoList) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        mInfoList.clear();
        mInfoList.addAll(infoList);
        mIndicatorLayout.setupIndicator(infoList);
        mImageViewPagerAdapter.notifyDataSetChanged();
        mImageViewPager.setCurrentItem(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mIsTouchDown = true;
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mIsTouchDown = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isIsTouchDown() {
        return mIsTouchDown;
    }

    private final class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            int size = mInfoList.size();
            if (size == 1) {
                index = 0;
            } else {
                index = index % mInfoList.size();
            }
            mIndicatorLayout.setSelectedIndicator(index);

            if (mPageChangeListener != null) {
                mPageChangeListener.onPageChange(index);
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        public ImageCycleAdapter() {

        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            int tempIndex = 0;
            int size = mInfoList.size();
            if (size == 1) {
                tempIndex = 0;
            } else {
                tempIndex = position % mInfoList.size();
            }
            final int index = tempIndex;
            int sourceId = mInfoList.get(index).sourceId;
            final ImageItemView imageView = new ImageItemView(getContext());
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setImageURI(sourceId);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageItemView view = (ImageItemView) object;
            container.removeView(view);
        }

    }

    /**
     * 设置图片
     *
     * @param draweeView
     * @param imageUrl
     */
    private void setImageUri(SimpleDraweeView draweeView, String imageUrl) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(controller);
    }

    private class ImageItemView extends FrameLayout {
        private ImageView mImageView;
        private View mMaskView;

        public ImageItemView(Context context) {
            super(context);
            addImageView();
            addMaskView();
        }

        private void addImageView() {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mImageView = new ImageView(getContext());
            mImageView.setScaleType(ScaleType.CENTER_CROP);
            this.addView(mImageView, lp);
        }

        private void addMaskView() {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mMaskView = new View(getContext());
            mMaskView.setBackgroundResource(R.drawable.default_listview_seletor);
            this.addView(mMaskView, lp);
        }

        public void setImageURI(int resourceId) {
            mImageView.setImageResource(resourceId);
        }

        public ImageView getImageView() {
            return mImageView;
        }
    }

    private class IndicatorLayout extends LinearLayout {

        public IndicatorLayout(Context context) {
            super(context);
            setOrientation(LinearLayout.HORIZONTAL);
        }

        public void setupIndicator(ArrayList<ImageInfo> infoList) {
            this.removeAllViews();
            int infoSize = infoList.size();
            if (infoSize <= 1) {
                return;
            }
            ImageView indicatorView;
            int size = getResources().getDimensionPixelSize(R.dimen.image_cycle_view_indicator_item_size);
            int gap = getResources().getDimensionPixelSize(R.dimen.space_5);
            LayoutParams lp;
            for (int i = 0; i < infoSize; i++) {
                indicatorView = new ImageView(getContext());
                indicatorView.setScaleType(ScaleType.CENTER_CROP);
                lp = new LayoutParams(size, size);
                lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                if (i == 0) {
                    indicatorView.setImageResource(R.drawable.guide_dot_select_icon);
                } else {
                    indicatorView.setImageResource(R.drawable.guide_dot_icon);
                    lp.leftMargin = gap;
                }
                this.addView(indicatorView, lp);
            }
        }

        public void setSelectedIndicator(int index) {
            int childCount = this.getChildCount();
            if (childCount <= index) {
                return;
            }
            for (int i = 0; i < childCount; i++) {
                ImageView indicatorView = (ImageView) this.getChildAt(i);
                if (i == index) {
                    indicatorView.setImageResource(R.drawable.guide_dot_select_icon);
                } else {
                    indicatorView.setImageResource(R.drawable.guide_dot_icon);
                }
            }
        }
    }

    public static class ImageInfo {
        public int sourceId;
        public Object value;
    }

    public void setOnGuidePageChangeListener(OnGuidePageChangeListener pageChangeListener) {
        this.mPageChangeListener = pageChangeListener;
    }

    public interface OnGuidePageChangeListener {
        void onPageChange(int index);
    }
}
