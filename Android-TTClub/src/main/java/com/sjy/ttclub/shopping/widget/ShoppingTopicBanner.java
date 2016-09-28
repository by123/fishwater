package com.sjy.ttclub.shopping.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.common.BannerHelper;
import com.sjy.ttclub.widget.ImageCycleView;

import java.util.ArrayList;

/**
 * Created by cundong on 2015/10/9.
 * <p>
 * RecyclerView的HeaderView，简单的展示一个TextView
 */
public class ShoppingTopicBanner extends FrameLayout {

    private Context mContext;

    private ImageCycleView mBannerView;

    private ArrayList<Banner> mBannerList = new ArrayList<>();

    public ShoppingTopicBanner(Context context) {
        super(context);
        init(context);
    }

    public ShoppingTopicBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShoppingTopicBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
//        inflate(context, R.layout.shopping_header, this);
        addBannerView();
    }

    private void addBannerView() {
        FrameLayout bannerLayout = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.space_150));
        this.addView(bannerLayout, lp);

        mBannerView = new ImageCycleView(getContext());
        mBannerView.setDefaultDrawable(new BitmapDrawable());
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        bannerLayout.addView(mBannerView, flp);
    }

    public void setupBanner(ArrayList<Banner> bannerList) {
        if (bannerList == null || bannerList.isEmpty()) {
            mBannerView.setVisibility(View.INVISIBLE);
            return;
        }
        mBannerView.setVisibility(View.VISIBLE);
        mBannerList.clear();
        mBannerList.addAll(bannerList);

        mBannerView.setImageResources(createBannerImageInfoList(), new ImageCycleView.ImageCycleListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                if (position < 0 || position >= mBannerList.size()) {
                    return;
                }
                Banner banner = mBannerList.get(position);
                if (banner == null) {
                    return;
                }
                BannerHelper.handleBannerClick(getContext(), banner);
            }
        });
    }

    private ArrayList<ImageCycleView.ImageInfo> createBannerImageInfoList() {
        ArrayList<ImageCycleView.ImageInfo> infoList = new ArrayList<>();
        ImageCycleView.ImageInfo info;
        for (Banner banner : mBannerList) {
            info = new ImageCycleView.ImageInfo();
            info.title = banner.getTitle();
            info.url = banner.getImageUrl();
            infoList.add(info);
        }
        return infoList;
    }

    public void stopAdBannerAnim() {
        mBannerView.stopImageCycle();
    }

    public void startAdBannerAnim() {
        mBannerView.startImageCycle();
    }
}