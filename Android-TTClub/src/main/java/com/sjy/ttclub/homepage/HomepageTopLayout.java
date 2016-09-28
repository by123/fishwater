package com.sjy.ttclub.homepage;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.common.BannerHelper;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.ImageCycleView;
import com.sjy.ttclub.widget.Rotate3dAnimation;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/3.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageTopLayout extends LinearLayout {
    public static final int ID_RECODE = 1;
    public static final int ID_KNOWLEDGE = 2;
    public static final int ID_QA = 3;
    public static final int ID_TEST = 4;

    private ImageView mDefaultBannerView;
    private ImageCycleView mBannerView;
    private EntranceLayout mEntranceLayout;

    private ArrayList<Banner> mBannerList = new ArrayList<Banner>();
    private int mCircleId;

    private HomepageTopLayoutCallback mCallback;

    public HomepageTopLayout(Context context) {
        this(context, null);
    }

    public HomepageTopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        addBannerView();
//        addEntranceLayout();
//        addDividerView();
        setBackgroundColor(getResources().getColor(R.color.white));

//        initEntranceItem();
    }

    private void addBannerView() {
        FrameLayout bannerLayout = new FrameLayout(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .space_180));
        this.addView(bannerLayout, lp);

        mDefaultBannerView = new ImageView(getContext());
        mDefaultBannerView.setVisibility(View.INVISIBLE);
        mDefaultBannerView.setImageResource(R.drawable.homepage_default_banner);
        mDefaultBannerView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        bannerLayout.addView(mDefaultBannerView, flp);

        mBannerView = new ImageCycleView(getContext());
        mBannerView.setDefaultDrawable(getResources().getDrawable(R.drawable.homepage_default_banner));
        flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        bannerLayout.addView(mBannerView, flp);
    }

    private void addEntranceLayout() {
        mEntranceLayout = new EntranceLayout(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mEntranceLayout, lp);
    }

    private void initEntranceItem() {
        EntranceInfo info = new EntranceInfo();
        info.id = ID_RECODE;
        info.textRes = R.string.home_entrance_record;
        info.drawableRes = R.drawable.home_record_icon;
        mEntranceLayout.addEntrance(info);

        info = new EntranceInfo();
        info.id = ID_KNOWLEDGE;
        info.textRes = R.string.home_entrance_knowledge;
        info.drawableRes = R.drawable.home_knowledge_icon;
        mEntranceLayout.addEntrance(info);

        info = new EntranceInfo();
        info.id = ID_QA;
        info.textRes = R.string.home_entrance_qa;
        info.drawableRes = R.drawable.home_qa_icon;
        mEntranceLayout.addEntrance(info);

        info = new EntranceInfo();
        info.id = ID_TEST;
        info.textRes = R.string.home_entrance_test;
        info.drawableRes = R.drawable.home_test_icon;
        mEntranceLayout.addEntrance(info);
    }

    private void addDividerView() {
        View view = new View(getContext());
        view.setBackgroundColor(getResources().getColor(R.color.homepage_toplayout_divider_color));
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .space_12));
        this.addView(view, lp);
    }

    public void setTopLayoutCallback(HomepageTopLayoutCallback callback) {
        mCallback = callback;
    }

    public void setupBanner(ArrayList<Banner> bannerList) {
        if (bannerList == null || bannerList.isEmpty()) {
            mBannerView.setVisibility(View.INVISIBLE);
            mDefaultBannerView.setVisibility(View.VISIBLE);
            return;
        }
        mDefaultBannerView.setVisibility(View.INVISIBLE);
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
                handleBannerClick(banner);

            }
        });
    }

    public void setQACircleId(int id) {
        mCircleId = id;
    }

    public boolean isTouchDownBanner() {
        return mBannerView.isIsTouchDown();
    }

    private void handleBannerClick(Banner banner) {
        String title = banner.getTitle();
        if(StringUtils.isEmpty(title)){
            title = banner.getBannerId();
        }
        StatsModel.stats(StatsKeyDef.HOMEPAGE_BANNER, StatsKeyDef.HOMEPAGE_BANNER, title);

        BannerHelper.handleBannerClick(getContext(), banner);
    }

    private ArrayList<ImageCycleView.ImageInfo> createBannerImageInfoList() {
        ArrayList<ImageCycleView.ImageInfo> infoList = new ArrayList<ImageCycleView.ImageInfo>();
        ImageCycleView.ImageInfo info;
        for (Banner banner : mBannerList) {
            info = new ImageCycleView.ImageInfo();
            info.title = banner.getTitle();
            info.url = banner.getImageUrl();
            infoList.add(info);
        }
        return infoList;
    }

    public void onPause() {
        stopAdBannerAnim();
        resetPeepState();
    }

    public void onResume() {
        startAdBannerAnim();
    }
    public void stopAdBannerAnim() {
        mBannerView.stopImageCycle();
    }

    public void startAdBannerAnim() {
        mBannerView.startImageCycle();
    }

    private void resetPeepState(){
        for (TextView textView : textViews) {
            final EntranceInfo info = (EntranceInfo) textView.getTag();
            if (info.id == ID_RECODE) {
                info.textRes=R.string.home_entrance_record;
                info.drawableRes=R.drawable.home_record_icon;
                textView.setText(ResourceHelper.getString(info.textRes));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, info.drawableRes, 0, 0);
            }
        }
    }
    private AlphaTextView textView;
    private ArrayList<TextView> textViews = new ArrayList<>();

    private class EntranceLayout extends LinearLayout implements OnClickListener, INotify {

        EntranceLayout(Context context) {
            super(context);
            this.setOrientation(LinearLayout.HORIZONTAL);
            NotificationCenter.getInstance().register(this, NotificationDef.N_HOMEPAGE_IS_INIT_SHOW);
        }

        private void addEntrance(final EntranceInfo info) {
            Resources res = getResources();
            textView = new AlphaTextView(getContext());
            textView.setTag(info);
            textView.setSingleLine();
            textView.setOnClickListener(this);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.space_12));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(res.getColor(R.color.black));
            textView.setText(info.textRes);
            textView.setCompoundDrawablePadding(res.getDimensionPixelSize(R.dimen.space_10));
            textView.setCompoundDrawablesWithIntrinsicBounds(0, info.drawableRes, 0, 0);
            boolean isFirstChild = this.getChildCount() == 0;
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.topMargin = res.getDimensionPixelSize(R.dimen.space_14);
            lp.bottomMargin = lp.topMargin;
            if (!isFirstChild) {
                lp.leftMargin = res.getDimensionPixelSize(R.dimen.space_32);
            }
            textViews.add(textView);
            this.addView(textView, lp);
        }

        @Override
        public void onClick(final View v) {
            if (v.getTag() instanceof EntranceInfo) {
                if (mCallback != null) {
                    final EntranceInfo info = (EntranceInfo) v.getTag();
                    if (info.id == ID_QA) {
                        info.data = mCircleId;
                    } else if (info.id == ID_RECODE) {
                        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                            @Override
                            public void run() {
                                info.drawableRes = R.drawable.home_record_icon;
                                info.textRes = R.string.home_entrance_record;
                                ((TextView) v).setText(ResourceHelper.getString(info.textRes));
                                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, info.drawableRes, 0, 0);
                            }
                        }, 1000);

                    }
                    mCallback.onEntranceClick(info);
                }
            }
        }

        @Override
        public void notify(Notification notification) {
            if (notification.id == NotificationDef.N_HOMEPAGE_IS_INIT_SHOW)
                for (TextView textView : textViews) {
                    final EntranceInfo info = (EntranceInfo) textView.getTag();
                    if (info.id == ID_RECODE) {
                        tryStartPeepAnimation(textView, info);
                    }
                }
        }

        private void unRegisterNotify() {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_HOMEPAGE_IS_INIT_SHOW);
        }

        private void tryStartPeepAnimation(final TextView textView, final EntranceInfo info) {
            ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                @Override
                public void run() {
                    Rotate3dAnimation rotate3dAnimation = new Rotate3dAnimation(0, 180);
                    rotate3dAnimation.setDuration(500);
                    rotate3dAnimation.setFillAfter(false);
                    rotate3dAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            textView.setText("");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            textView.setText(ResourceHelper.getString(R.string.home_entrance_peep));
                            textView.clearAnimation();
                            unRegisterNotify();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    textView.startAnimation(rotate3dAnimation);
                }
            }, 1000);
            ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                @Override
                public void run() {
                    info.drawableRes = R.drawable.home_peep_icon;
                    info.textRes = R.string.home_entrance_peep;
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, info.drawableRes, 0, 0);
                }
            }, 1250);
        }
    }


    public class EntranceInfo {
        public int id;
        public int textRes;
        public int drawableRes;

        public Object data;
    }

    public interface HomepageTopLayoutCallback {
        void onEntranceClick(EntranceInfo info);
    }
}
