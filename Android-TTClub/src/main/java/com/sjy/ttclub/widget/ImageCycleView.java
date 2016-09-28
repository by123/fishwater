package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lsym.ttclub.R;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;

public class ImageCycleView extends FrameLayout {
    private static final int CYCLE_DURATION = 3 * 1000;

    private ViewPager mImageViewPager = null;
    private ImageCycleAdapter mImageViewPagerAdapter;
    private IndicatorLayout mIndicatorLayout;

    private ArrayList<ImageInfo> mInfoList = new ArrayList<ImageInfo>();
    private boolean mIsStop;
    private TextView mImageTitleView;

    private ImageCycleListener mListener;

    private Drawable mDefaultDrawable;

    private boolean mIsTouchDown = false;

    private float mRatio = 0;

    public ImageCycleView(Context context) {
        this(context, null);
    }

    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        addImageViewPager();
        addImageTitleView();
        addImageIndicatorView();
    }

    private void addImageViewPager() {
        mImageViewPager = new ViewPager(getContext());


        mImageViewPager.setOnPageChangeListener(new PageChangeListener());
        mImageViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        startImageCycle();
                        break;
                    default:
                        stopImageCycle();
                        break;
                }
                return false;
            }
        });
        mImageViewPagerAdapter = new ImageCycleAdapter();
        mImageViewPager.setAdapter(mImageViewPagerAdapter);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mImageViewPager, lp);
    }

    private void addImageTitleView() {
        mImageTitleView = new TextView(getContext());
        mImageTitleView.setGravity(Gravity.CENTER_VERTICAL);
        mImageTitleView.setBackgroundColor(getResources().getColor(R.color.image_cycle_view_text_bg));
        mImageTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen
                .image_cycle_view_title_textsize));
        mImageTitleView.setPadding(getResources().getDimensionPixelSize(R.dimen.image_cycle_view_title_paddingLeft),
                0, 0, 0);
        mImageTitleView.setTextColor(getResources().getColor(R.color.image_cycle_view_text_color));
        mImageTitleView.setVisibility(View.INVISIBLE);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .image_cycle_view_title_height));
        lp.gravity = Gravity.BOTTOM;
        this.addView(mImageTitleView, lp);
    }

    private void addImageIndicatorView() {
        mIndicatorLayout = new IndicatorLayout(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen
                .image_cycle_view_indicator_height));
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.image_cycle_view_indicator_marginRight);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        this.addView(mIndicatorLayout, lp);
    }

    public void setAspectRatio(float ratio) {
        mRatio = ratio;
    }

    public void setDefaultDrawable(Drawable drawable) {
        mDefaultDrawable = drawable;
    }

    public void startImageCycle() {
        mIsStop = false;
        removeCallbacks(mImageTimerTask);
        postDelayed(mImageTimerTask, CYCLE_DURATION);
    }

    public void stopImageCycle() {
        mIsStop = true;
        removeCallbacks(mImageTimerTask);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopImageCycle();
    }

    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            int index = mImageViewPager.getCurrentItem() + 1;
            int size = mInfoList.size();
            if (size <= 0) {
                return;
            }
            if (size == 1) {
                index = 0;
            } else {
                index = index % size;
            }
            mIndicatorLayout.setSelectedIndicator(index);
            mImageViewPager.setCurrentItem(index, true);
            if (!mIsStop) {
                postDelayed(mImageTimerTask, CYCLE_DURATION);
            }
        }
    };

    public void setImageResources(ArrayList<ImageInfo> infoList, ImageCycleListener listener) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        mListener = listener;
        mInfoList.clear();
        mInfoList.addAll(infoList);
        mIndicatorLayout.setupIndicator(infoList);
        String title = mInfoList.get(0).title;
        if (StringUtils.isEmpty(title)) {
            mImageTitleView.setVisibility(View.INVISIBLE);
        } else {
            mImageTitleView.setText(title);
            mImageTitleView.setVisibility(View.VISIBLE);
        }
        mImageViewPagerAdapter.notifyDataSetChanged();
        mImageViewPager.setCurrentItem(0);
        startImageCycle();
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
            if (state == ViewPager.SCROLL_STATE_IDLE && !mIsStop) {
                removeCallbacks(mImageTimerTask);
                postDelayed(mImageTimerTask, 3000);
            }
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
            String title = mInfoList.get(index).title;
            if (StringUtils.isEmpty(title)) {
                mImageTitleView.setVisibility(View.GONE);
            } else {
                mImageTitleView.setText(title);
                mImageTitleView.setVisibility(View.VISIBLE);
            }
            mIndicatorLayout.setSelectedIndicator(index);
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
            String url = mInfoList.get(index).url;
            final ImageItemView imageView = new ImageItemView(getContext());
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if (mRatio > 0.001) {
                imageView.setAspectRatio(mRatio);
            }
            imageView.setImageURI(url);
            container.addView(imageView);

            imageView.setImageOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onImageClick(index, imageView.getImageView());
                    }
                }
            });
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
        if (StringUtils.isEmpty(imageUrl)) {
            return;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(controller);
    }

    private class ImageItemView extends FrameLayout {
        private SimpleDraweeView mImageView;
        private View mMaskView;

        public ImageItemView(Context context) {
            super(context);
            addImageView();
            addMaskView();
        }

        private void addImageView() {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mImageView = new SimpleDraweeView(getContext());
            mImageView.setScaleType(SimpleDraweeView.ScaleType.CENTER_CROP);
            this.addView(mImageView, lp);
        }

        private void addMaskView() {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mMaskView = new View(getContext());
            mMaskView.setBackgroundResource(R.drawable.default_listview_seletor);
            this.addView(mMaskView, lp);
        }

        public void setAspectRatio(float ratio) {
            mImageView.setAspectRatio(ratio);
        }

        public void setImageURI(String url) {
            setImageUri(mImageView, url);
        }

        public void setImageOnClickListener(OnClickListener listener) {
            mMaskView.setOnClickListener(listener);
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
            int gap = getResources().getDimensionPixelSize(R.dimen.image_cycle_view_indicator_item_gap);
            LinearLayout.LayoutParams lp;
            for (int i = 0; i < infoSize; i++) {
                indicatorView = new ImageView(getContext());
                indicatorView.setScaleType(ScaleType.CENTER_CROP);
                lp = new LinearLayout.LayoutParams(size, size);
                lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                if (i == 0) {
                    indicatorView.setImageResource(R.drawable.ic_dot_on);
                } else {
                    indicatorView.setImageResource(R.drawable.ic_dot_don);
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
                    indicatorView.setImageResource(R.drawable.ic_dot_on);
                } else {
                    indicatorView.setImageResource(R.drawable.ic_dot_don);
                }
            }
        }
    }

    public static class ImageInfo {
        public String url;
        public String title;
        public Object value;
    }

    public interface ImageCycleListener {
        void onImageClick(int position, View imageView);
    }
}
