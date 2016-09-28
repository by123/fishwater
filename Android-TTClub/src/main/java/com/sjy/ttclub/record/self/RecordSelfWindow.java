package com.sjy.ttclub.record.self;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.record.data.RecordSelfData;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.record.RecordDataConfig;
import com.sjy.ttclub.record.adapter.RecordSelfViewPagerAdapter;
import com.sjy.ttclub.record.model.RecordPeepRequest;
import com.sjy.ttclub.record.widget.RecordDataTitle;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.CustomScrollView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.ScrollViewPager;

/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class RecordSelfWindow extends DefaultWindow implements RecordPeepRequest.SelfCallBack, ViewPager
        .OnPageChangeListener, RecordDataTitle.RecordDataTitleListener {
    private int mCurrentPosition;
    private SimpleDraweeView mUserImage;
    private TextView mNickname;
    private RecordPeepRequest mRecordPeepRequest;
    private ScrollViewPager mViewPager;
    private RecordSelfViewPagerAdapter mAdapter;

    private RecordDataTitle mRecordTitle;
    private RecordDataTitle mRecordFloatTitle;
    private LoadingLayout mLoadingLayout;

    private int mRecordTitlePos;


    public RecordSelfWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.record_self_title);
        setTitleBarBackground(ResourceHelper.getColor(R.color.record_top_bg));
        setTitleIcon(ResourceHelper.getDrawable(R.drawable.title_back));
        setTitleColor(ResourceHelper.getColor(R.color.white));

        View view = View.inflate(getContext(), R.layout.record_self_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        initView(view);
        initData();
        mRecordPeepRequest = new RecordPeepRequest();
        mRecordPeepRequest.setSelfCallBack(this);

        //统计-我的数据页展示
        StatsModel.stats(StatsKeyDef.MY_RECORD_VIEW, "gender", AccountManager.getInstance().switchSex());
    }

    private void initView(View view) {
        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.record_self_loading_layout);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mRecordPeepRequest.getSelfData();
            }
        });
        mUserImage = (SimpleDraweeView) view.findViewById(R.id.record_self_head_image);
        mNickname = (TextView) view.findViewById(R.id.record_self_nickname);
        mRecordFloatTitle = new RecordDataTitle();
        mRecordFloatTitle.initTitleView(view.findViewById(R.id.record_self_float_title_layout), false);
        mRecordFloatTitle.setTitleListener(this);
        mRecordTitle = new RecordDataTitle();
        mRecordTitle.setTitleListener(this);
        mRecordTitle.initTitleView(view.findViewById(R.id.record_self_title_layout), false);
        mRecordTitle.setTopView(view.findViewById(R.id.record_self_head_layout));
        mViewPager = (ScrollViewPager) view.findViewById(R.id.record_self_view_pager);

        CustomScrollView scrollView = (CustomScrollView) view.findViewById(R.id.record_self_scroll_view);
        scrollView.setScrollViewListener(new CustomScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(CustomScrollView scrollView, int x, int y, int oldx, int oldy) {
                handleScrollChanged(y);
            }
        });
    }

    private void initData() {
        mCurrentPosition = 0;
        AccountManager.getInstance().setHeadImage(mUserImage);
        AccountInfo accountInfo = AccountManager.getInstance().getAccountInfo();
        mNickname.setText(accountInfo.getNickname());
        mRecordTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.SELF_TITLE[0]));
        mRecordTitle.setCurrentIndex(mCurrentPosition);
        mRecordFloatTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.SELF_TITLE[0]));
        mRecordFloatTitle.setCurrentIndex(mCurrentPosition);

        mAdapter = new RecordSelfViewPagerAdapter(getContext());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onRecordDataPageSelected(int index) {
        //统计-我的记录-切换tab
        StatsModel.stats(StatsKeyDef.MY_RECORD_SWITCH_TAB, "gender", AccountManager.getInstance().switchSex());

        mViewPager.setCurrentItem(index);
        mCurrentPosition = index;
        mRecordTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.SELF_TITLE[index]));
        mRecordTitle.setCurrentIndex(index);
        mRecordFloatTitle.setTitleText(ResourceHelper.getString(RecordDataConfig.SELF_TITLE[index]));
        mRecordFloatTitle.setCurrentIndex(index);
    }

    private void handleScrollChanged(int posY) {
        if (mRecordTitlePos < 10) {
            mRecordTitlePos = mRecordTitle.getLocationY();
        }
        if (mRecordTitlePos <= 0) {
            return;
        }
        if (posY >= mRecordTitlePos) {
            mRecordFloatTitle.setVisibility(View.VISIBLE);
        } else {
            mRecordFloatTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFailed() {
        mLoadingLayout.setDefaultNetworkError(true);
    }

    @Override
    public void onSuccess(RecordSelfData jsonBean) {
        mLoadingLayout.setVisibility(View.GONE);
        mAdapter.setRecordSelfData(jsonBean);
    }

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

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            mRecordPeepRequest.getSelfData();
        }
    }
}
