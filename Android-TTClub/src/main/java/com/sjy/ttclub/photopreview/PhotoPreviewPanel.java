package com.sjy.ttclub.photopreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.fresco.FrescoHelper;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.SystemUtil;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.util.ValueHolder;
import com.sjy.ttclub.util.file.FileUtils;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.BasePanel;
import com.sjy.ttclub.widget.FixedViewPager;

import java.io.File;
import java.util.ArrayList;

/*package*/ class PhotoPreviewPanel extends BasePanel {
    private FixedViewPager mViewPager;
    private TextView mPageIndexView;
    private ImagePagerAdapter mPagerAdapter;

    private ArrayList<String> mImageList = new ArrayList<String>();
    private int mPosition = 0;

    public PhotoPreviewPanel(Context context) {
        super(context, false);
        initPanel();
        setCancelTouchOutside(false);
        setDimValue(0.8f);
        setAnimation(R.style.SlideRight2LeftAnim);
    }

    @Override
    protected View onCreateContentView() {
        View parentView = View.inflate(mContext, R.layout.image_preview_layout, null);
        setupViews(parentView);
        return parentView;
    }

    @Override
    protected FrameLayout.LayoutParams getLayoutParams() {
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.MATCH_PARENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    private void setupViews(View parentView) {
        mViewPager = (FixedViewPager) parentView.findViewById(R.id.image_preview_viewpager);
        mPageIndexView = (TextView) parentView.findViewById(R.id.image_preview_page_index);
        View backButton = parentView.findViewById(R.id.image_preview_back);
        if (SystemUtil.isTransparentStatusBarEnable()) {
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) backButton.getLayoutParams();
            flp.topMargin += SystemUtil.getStatusBarHeight(mContext);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanel();
            }
        });
        mPagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int currentPage = position + 1;
                mPageIndexView.setText(currentPage + "/" + mImageList.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });
    }


    public void setupPreviewPanel(PhotoPreviewInfo info) {
        mImageList.clear();
        mImageList.addAll(info.photoList);
        mPosition = info.position;

        mPagerAdapter.notifyDataSetChanged();
        int currentPage = mPosition + 1;
        mPageIndexView.setText(currentPage + "/" + mImageList.size());
        mViewPager.setCurrentItem(mPosition);
    }

    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageItemView imageView = new ImageItemView(mContext);
            String url = null;
            if (position < mImageList.size()) {
                url = mImageList.get(position);
            }
            imageView.setupPhotoView(url);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(imageView, lp);
            return imageView;
        }

    }

    private class ImageItemView extends FrameLayout {
        private PhotoView mPhotoView;
        private TextView mLoadingView;
        private String mImageUrl;

        public ImageItemView(Context context) {
            super(context);
            addPhotoView();
            addLoadingView();
        }

        private void addLoadingView() {
            mLoadingView = new TextView(getContext());
            mLoadingView.setTextColor(getResources().getColor(R.color.white));
            mLoadingView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen
                    .space_18));
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            this.addView(mLoadingView, lp);
        }

        private void addPhotoView() {
            mPhotoView = new PhotoView(getContext());
            mPhotoView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showSavePhotoPanel(mImageUrl);
                    return true;
                }
            });
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(mPhotoView, lp);
        }

        public void setupPhotoView(String url) {
            if (StringUtils.isEmpty(url)) {
                return;
            }
            mImageUrl = url;
            if (url.startsWith("http://") || url.startsWith("https://")) {
                mLoadingView.setVisibility(VISIBLE);
                mLoadingView.setText(getResources().getString(R.string.loading));
                FrescoHelper.loadImage(url, new FrescoHelper.FrescoLoadImageCallback() {
                    @Override
                    public void onImageLoadFinish(Bitmap bitmap) {
                        if (bitmap != null) {
                            mLoadingView.setVisibility(View.GONE);
                            mPhotoView.setImageBitmap(bitmap);
                        } else {
                            mLoadingView.setVisibility(View.VISIBLE);
                            mLoadingView.setText(getResources().getString(R.string.loading_failed));
                        }
                    }
                });
            } else {
                mLoadingView.setVisibility(View.GONE);
                mPhotoView.setImageBitmap(BitmapUtil.createBitmapThumbnail(url, HardwareUtil.screenWidth, HardwareUtil
                        .screenHeight));
            }


        }

        private void showSavePhotoPanel(final String imageUrl) {
            mImageUrl = imageUrl;
            ActionSheetPanel panel = new ActionSheetPanel(getContext());
            ActionSheetPanel.ActionSheetItem item = new ActionSheetPanel.ActionSheetItem();
            item.title = getResources().getString(R.string.photo_save);
            item.id = "save";
            panel.addSheetItem(item);
            panel.setSheetItemClickListener(new ActionSheetPanel.OnActionSheetClickListener() {
                @Override
                public void onActionSheetItemClick(String id) {
                    savePhoto(imageUrl);
                }
            });
            panel.showPanel();

        }

        private void savePhoto(final String imageUrl) {
            final File file = FrescoHelper.getCachedImageOnDisk(imageUrl);
            if (file == null) {
                ToastHelper.showToast(getContext(), R.string.save_failed);
            } else {
                final ValueHolder holder = new ValueHolder();
                holder.bolValue = true;
                String fileName = FileUtils.getFileNameFromPath(imageUrl, true);
                final String finalPath = PathManager.getInstance().getExtImagePath() + fileName;
                ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileUtils.copy(file, new File(finalPath));
                        } catch (Exception e) {
                            holder.bolValue = false;
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        if (holder.bolValue == true) {
                            CommonUtils.notifyGralleryRefresh(getContext(), finalPath);
                            ToastHelper.showToast(getContext(), R.string.save_success);
                        } else {
                            ToastHelper.showToast(getContext(), R.string.save_failed);
                        }
                    }
                });
            }
        }
    }

}
