package com.sjy.ttclub.record.peep;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.record.data.RecordPeepData;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.record.RecordConst;
import com.sjy.ttclub.record.RecordDataConfig;
import com.sjy.ttclub.record.model.RecordPeepRequest;
import com.sjy.ttclub.record.widget.RecordDataTitle;
import com.sjy.ttclub.record.widget.RecordRadioButton;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.widget.CustomCheckBox;
import com.sjy.ttclub.widget.CustomScrollView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.ScrollViewPager;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepWindow extends DefaultWindow implements RecordDataTitle.RecordDataTitleListener {
    private static final int[] TITLE_ICO = {R.drawable.record_peep_title_icon_papa, R.drawable.record_peep_title_icon_zh, R.drawable.record_peep_title_icon_no_do};
    private CustomScrollView mScrollView;
    private RadioGroup mSexRadioGroup;
    private RadioGroup mMarriageRadioGroup;
    private RadioGroup mTimeRadioGroup;
    private CustomCheckBox mShowMeCheckBox;

    private LoadingLayout mLoadingLayout;
    private ScrollViewPager mViewPager;
    private RecordPeepViewPagerAdapter mAdapter;
    private RecordPeepRuleInfo mRuleInfo;
    private RecordPeepSelectInfo mSelectInfo;
    private RecordPeepData mPeepData;
    private RecordPeepRequest mRequest;

    private RecordDataTitle mRecordTitle;
    private RecordDataTitle mRecordFloatTitle;

    private ImageView mPeepIconView;

    private int mRecordTitlePos;


    public RecordPeepWindow(Context context, IDefaultWindowCallBacks callBacks, RecordPeepRuleInfo info) {
        super(context, callBacks);
        mRuleInfo = info;
        mSelectInfo = new RecordPeepSelectInfo(info);
        setTitle(R.string.record_peep_title);
        setTitleBarBackground(ResourceHelper.getColor(R.color.record_top_bg));
        setTitleIcon(ResourceHelper.getDrawable(R.drawable.title_back));
        setTitleColor(ResourceHelper.getColor(R.color.white));

        View view = View.inflate(context, R.layout.record_peep_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        initViews(view);

        mRequest = new RecordPeepRequest();
        //统计-展示偷窥页
        StatsModel.stats(StatsKeyDef.SNOOP_VIEW, "gender", AccountManager.getInstance().switchSex());
    }

    private void initViews(View view) {
        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.record_peep_loading_layout);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryLoadPeepData(true);
            }
        });
        mScrollView = (CustomScrollView) view.findViewById(R.id.record_peep_main_scrollview);
        mScrollView.setScrollViewListener(new CustomScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(CustomScrollView scrollView, int x, int y, int oldx, int oldy) {
                handleScrollChanged(y);
            }
        });
        View topLayout = mScrollView.findViewById(R.id.record_peep_main_top);
        initTopLayout(topLayout);
        LinearLayout container = (LinearLayout) mScrollView.findViewById(R.id.record_peep_main_container);
        initDataContainer(container);
        mRecordFloatTitle = new RecordDataTitle();
        mRecordFloatTitle.initTitleView(view.findViewById(R.id.record_peep_main_float_title), true);
        mRecordFloatTitle.setTitleListener(this);
        mRecordFloatTitle.getRootView().setBackgroundColor(ResourceHelper.getColor(R.color.white));
        mRecordTitle = new RecordDataTitle();
        mRecordTitle.initTitleView(container.findViewById(R.id.record_peep_main_title), true);
        mRecordTitle.setTopView(topLayout);
        mRecordTitle.setTitleListener(this);

        mRecordTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.PEEP_TITLE[0]));
        mRecordTitle.setCurrentIndex(0);
        mRecordFloatTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.PEEP_TITLE[0]));
        mRecordFloatTitle.setCurrentIndex(0);
    }

    private void initTopLayout(View topLayout) {
        initSexLayout(topLayout);
        initMarriageLayout(topLayout);
        initTimeLayout(topLayout);
        initShowMeView(topLayout);
    }

    @SuppressWarnings("ResourceType")
    private void initSexLayout(View topLayout) {
        boolean isSelectMan = mSelectInfo.sex == RecordConst.PEEP_SEX_MAN;
        final boolean hasPermissionMan = mRuleInfo.hasSexPermission(RecordConst.PEEP_SEX_MAN);
        final boolean hasPermissionWoman = mRuleInfo.hasSexPermission(RecordConst.PEEP_SEX_WOMAN);

        View sexLayout = topLayout.findViewById(R.id.record_peep_main_top_sex);
        ImageView iconView = (ImageView) sexLayout.findViewById(R.id.record_peep_main_top_item_image);
        iconView.setImageResource(R.drawable.record_peep_sex);
        mSexRadioGroup = (RadioGroup) sexLayout.findViewById(R.id.record_peep_main_top_item_radiogroup);

        RecordRadioButton button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_sex_man);
        button.setId(RecordConst.PEEP_SEX_MAN);
        button.setChecked(isSelectMan);
        button.setEnableToggle(hasPermissionMan);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissionMan) {
                    RecordPeepHelper.toastSexLimit(ResourceHelper.getString(R.string.record_peep_sex_man));
                }
            }
        });
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        lp.gravity = Gravity.LEFT | Gravity.TOP;
        mSexRadioGroup.addView(button, lp);
        button = new RecordRadioButton(getContext());
        button.setChecked(!isSelectMan);
        button.setText(R.string.record_peep_sex_woman);
        button.setId(RecordConst.PEEP_SEX_WOMAN);
        button.setEnableToggle(hasPermissionWoman);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissionWoman) {
                    RecordPeepHelper.toastSexLimit(ResourceHelper.getString(R.string.record_peep_sex_woman));
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        mSexRadioGroup.addView(button, lp);
        mSexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                stats();
                mSelectInfo.sex = checkedId;
                final boolean enableShowMe = mRuleInfo.hasPermissionShowMe();
                if (enableShowMe) {
                    boolean theSameSex = mSelectInfo.sex == AccountManager.getInstance().getSex();
                    mShowMeCheckBox.setEnableToggle(theSameSex);
                    if (!theSameSex) {
                        mShowMeCheckBox.setChecked(false);
                        mSelectInfo.showMe = RecordConst.PEEP_ME_DISABLE;
                    }
                }
                tryLoadPeepData(false);
            }
        });
    }

    private void stats(){
        //统计-切换筛选条件
        StatsModel.stats(StatsKeyDef.SNOOP_SWITCH_FILTER, "gender", AccountManager.getInstance().switchSex());
    }

    @SuppressWarnings("ResourceType")
    private void initMarriageLayout(View topLayout) {
        View marriageLayout = topLayout.findViewById(R.id.record_peep_main_top_marriage);
        ImageView iconView = (ImageView) marriageLayout.findViewById(R.id.record_peep_main_top_item_image);
        iconView.setImageResource(R.drawable.record_peep_marriage);
        mMarriageRadioGroup = (RadioGroup) marriageLayout.findViewById(R.id.record_peep_main_top_item_radiogroup);

        RecordRadioButton button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_marriage_single);
        button.setId(RecordConst.PEEP_MARRIAGE_SINGLE);
        final boolean hasSinglePermission = mRuleInfo.hasMarriagePermission(RecordConst.PEEP_MARRIAGE_SINGLE);
        button.setEnableToggle(hasSinglePermission);
        boolean isSelected = mSelectInfo.marriage == RecordConst.PEEP_MARRIAGE_SINGLE;
        button.setChecked(isSelected);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasSinglePermission) {
                    RecordPeepHelper.toastMarriageLimit(RecordConst.PEEP_MARRIAGE_SINGLE, mRuleInfo
                            .getMarriagePermissionList());
                }
            }
        });
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        mMarriageRadioGroup.addView(button, lp);

        button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_marriage_inlove);
        button.setId(RecordConst.PEEP_MARRIAGE_INLOVE);
        final boolean hasInlovePermission = mRuleInfo.hasMarriagePermission(RecordConst.PEEP_MARRIAGE_INLOVE);
        button.setEnableToggle(hasInlovePermission);
        boolean inloveSelected = mSelectInfo.marriage == RecordConst.PEEP_MARRIAGE_INLOVE;
        button.setChecked(inloveSelected);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInlovePermission) {
                    RecordPeepHelper.toastMarriageLimit(RecordConst.PEEP_MARRIAGE_INLOVE, mRuleInfo
                            .getMarriagePermissionList());
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        mMarriageRadioGroup.addView(button, lp);

        button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_marriage_marriaged);
        button.setId(RecordConst.PEEP_MARRIAGE_MARRIAGED);
        final boolean hasMarriagedPermission = mRuleInfo.hasMarriagePermission(RecordConst.PEEP_MARRIAGE_MARRIAGED);
        button.setEnableToggle(hasMarriagedPermission);
        boolean marriagedSelected = mSelectInfo.marriage == RecordConst.PEEP_MARRIAGE_MARRIAGED;
        button.setChecked(marriagedSelected);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasMarriagedPermission) {
                    RecordPeepHelper.toastMarriageLimit(RecordConst.PEEP_MARRIAGE_MARRIAGED, mRuleInfo
                            .getMarriagePermissionList());
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        mMarriageRadioGroup.addView(button, lp);

        button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_sex_all);
        button.setId(RecordConst.PEEP_MARRIAGE_ALL);
        final boolean hasAllPermission = mRuleInfo.hasMarriagePermission(RecordConst.PEEP_MARRIAGE_ALL);
        button.setEnableToggle(hasAllPermission);
        button.setChecked(false);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasAllPermission) {
                    RecordPeepHelper.toastMarriageLimit(RecordConst.PEEP_MARRIAGE_ALL, mRuleInfo
                            .getMarriagePermissionList());
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        mMarriageRadioGroup.addView(button, lp);
        mMarriageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                stats();
                mSelectInfo.marriage = checkedId;
                tryLoadPeepData(false);
            }
        });
    }

    @SuppressWarnings("ResourceType")
    private void initTimeLayout(View topLayout) {
        View timeLayout = topLayout.findViewById(R.id.record_peep_main_top_time);
        ImageView iconView = (ImageView) timeLayout.findViewById(R.id.record_peep_main_top_item_image);
        iconView.setImageResource(R.drawable.record_peep_time);
        mTimeRadioGroup = (RadioGroup) timeLayout.findViewById(R.id.record_peep_main_top_item_radiogroup);

        RecordRadioButton button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_time_yesterday);
        button.setId(RecordConst.PEEP_TIME_YESTODAY);
        button.setChecked(true);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        mTimeRadioGroup.addView(button, lp);

        button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_time_last_week);
        button.setId(RecordConst.PEEP_TIME_LAST_WEEK);
        final boolean hasWeekPermission = mRuleInfo.hasTimePermission(RecordConst.PEEP_TIME_LAST_WEEK);
        button.setEnableToggle(hasWeekPermission);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasWeekPermission) {
                    RecordPeepHelper.toastTimeLimit(RecordConst.PEEP_TIME_LAST_WEEK);
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        mTimeRadioGroup.addView(button, lp);

        button = new RecordRadioButton(getContext());
        button.setText(R.string.record_peep_time_last_month);
        button.setId(RecordConst.PEEP_TIME_LAST_MONTH);
        final boolean hasMonthPermission = mRuleInfo.hasTimePermission(RecordConst.PEEP_TIME_LAST_MONTH);
        button.setEnableToggle(hasMonthPermission);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasMonthPermission) {
                    RecordPeepHelper.toastTimeLimit(RecordConst.PEEP_TIME_LAST_MONTH);
                }
            }
        });
        lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
        mTimeRadioGroup.addView(button, lp);

        if (mRuleInfo != null && StringUtils.isNotEmpty(mRuleInfo.getSpecialTimeName())) {
            button = new RecordRadioButton(getContext());
            button.setHolidayView();
            button.setText(mRuleInfo.getSpecialTimeName());
            button.setId(RecordConst.PEEP_TIME_SPECIAL);
            lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_8);
            mTimeRadioGroup.addView(button, lp);
        }
        mTimeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                stats();
                mSelectInfo.time = checkedId;
                tryLoadPeepData(false);
            }
        });

    }

    private void initShowMeView(View topLayout) {
        mShowMeCheckBox = (CustomCheckBox) topLayout.findViewById(R.id.record_peep_main_top_show_me);
        final boolean enableShowMe = mRuleInfo.hasPermissionShowMe();
        mShowMeCheckBox.setEnableToggle(enableShowMe);
        mShowMeCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enableShowMe) {
                    RecordPeepHelper.toastShowMeLimit();
                    return;
                }
                boolean sexError = mSelectInfo.sex != AccountManager.getInstance().getSex();
                if (sexError) {
                    RecordPeepHelper.toastShowMeSexLimit(mSelectInfo.sex);
                    return;
                }
                mSelectInfo.showMe = mShowMeCheckBox.isChecked() ? RecordConst.PEEP_ME_ENABLE : RecordConst
                        .PEEP_ME_DISABLE;
                tryLoadPeepData(false);
            }
        });
    }

    private void initDataContainer(View container) {
        mViewPager = (ScrollViewPager) container.findViewById(R.id.record_peep_view_pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onRecordDataPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mAdapter = new RecordPeepViewPagerAdapter(getContext());
        mAdapter.setSpecTimeName(mRuleInfo.getSpecialTimeName());
        mViewPager.setAdapter(mAdapter);

        mPeepIconView = (ImageView) container.findViewById(R.id.record_peep_title_icon);
    }


    private void handleScrollChanged(int posY) {
        if (mRecordTitlePos < 10) {
            mRecordTitlePos = mRecordTitle.getLocationY();
        }
        if (mRecordTitlePos <= 0) {
            return;
        }
        int minAlpha = 40;
        int alpha = minAlpha;
        if (posY >= mRecordTitlePos) {
            mRecordFloatTitle.setVisibility(View.VISIBLE);
        } else {
            mRecordFloatTitle.setVisibility(View.INVISIBLE);
            alpha = (255 - minAlpha) * Math.abs(mRecordTitlePos - posY) / mRecordTitlePos;
            alpha += minAlpha;
            if (alpha < minAlpha) {
                alpha = minAlpha;
            } else if (alpha > 255) {
                alpha = 255;
            }
        }
        mPeepIconView.setAlpha(alpha);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            tryLoadPeepData(true);
        }
    }

    @Override
    public void onRecordDataPageSelected(int index) {
        //统计-切换tab
        StatsModel.stats(StatsKeyDef.SNOOP_SWITCH_TAB, "gender", AccountManager.getInstance().switchSex());

        mViewPager.setCurrentItem(index);
        mPeepIconView.setImageBitmap(ResourceHelper.getBitmap(TITLE_ICO[index]));
        mRecordTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.PEEP_TITLE[index]));
        mRecordTitle.setCurrentIndex(index);
        mRecordFloatTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.PEEP_TITLE[index]));
        mRecordFloatTitle.setCurrentIndex(index);
    }

    private void tryLoadPeepData(boolean init) {
        final LoadingDialog loadingDialog = new LoadingDialog(getContext());
        if (!init) {
            loadingDialog.show();
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        };
        mRequest.getPeepData(mSelectInfo, new RecordPeepRequest.PeepCallback() {
            @Override
            public void onRequestSuccess(RecordPeepData data) {
                ThreadManager.postDelayed(ThreadManager.THREAD_UI, runnable, 500);
                loadingDialog.dismiss();
                mLoadingLayout.setVisibility(View.GONE);
                mPeepData = data;
                mAdapter.setRecordPeepData(mPeepData, mSelectInfo);
            }

            @Override
            public void onRequestFail(int errorCode) {
                ThreadManager.postDelayed(ThreadManager.THREAD_UI, runnable, 500);
                if (mPeepData == null) {
                    mLoadingLayout.setDefaultNetworkError(true);
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }

}
