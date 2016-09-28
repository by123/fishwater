package com.sjy.ttclub.record.self;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.data.RecordMorePapa;
import com.sjy.ttclub.bean.record.data.RecordOnePapa;
import com.sjy.ttclub.bean.record.data.RecordSelfCount;
import com.sjy.ttclub.bean.record.data.RecordSelfData;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.record.RecordDataConfig;
import com.sjy.ttclub.record.widget.RecordDataGroupView;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by gangqing on 2016/1/9.
 * Email:denggangqing@ta2she.com
 */
public class RecordSelfViewPagerHelper implements RecordDataGroupView.SelfShareClicked, View.OnClickListener {
    private int mType;
    private View mRootView;
    private Context mContext;
    private TextView mRecordCount1;
    private TextView mRecordCount2;
    private TextView mTimes;
    private TextView mDays;
    private TextView mAvgTime;
    private View mLine;
    private LinearLayout mAvgTimeLy;
    private LinearLayout mCountLayout;
    private LinearLayout mShareCount;
    private RecordSelfData mData;
    private RecordDataGroupView mRecordDataGroupView;
    private View mShareLogo;

    public RecordSelfViewPagerHelper(Context context) {
        mContext = context;
        mRootView = initView();
    }

    public void setData(RecordSelfData data, int type) {
        mData = data;
        mType = type;
        initData(type);
    }

    private View initView() {
        View view = View.inflate(mContext, R.layout.record_self_view_pager_layout, null);
        mRecordCount1 = (TextView) view.findViewById(R.id.record_self_count1);
        mRecordCount2 = (TextView) view.findViewById(R.id.record_self_count2);
        mTimes = (TextView) view.findViewById(R.id.record_self_times);
        mDays = (TextView) view.findViewById(R.id.record_self_days);
        mAvgTime = (TextView) view.findViewById(R.id.record_self_avg_time);
        mLine = view.findViewById(R.id.record_self_line);
        mAvgTimeLy = (LinearLayout) view.findViewById(R.id.record_self_avg_time_ll);
        mCountLayout = (LinearLayout) view.findViewById(R.id.record_self_count_ll);
        mShareLogo = view.findViewById(R.id.share_bottom_logo);
        mShareCount = (LinearLayout) view.findViewById(R.id.record_self_count_share);
        mShareCount.setOnClickListener(this);
        mRecordDataGroupView = (RecordDataGroupView) view.findViewById(R.id.record_peep_main_data_group_view);
        mRecordDataGroupView.setShareText(ResourceHelper.getString(R.string.record_data_share));
        mRecordDataGroupView.setIsSelf(true);
        mRecordDataGroupView.setSelfShareClicked(this);

        return view;
    }

    public View getRootView() {
        return mRootView;
    }

    private void initData(int type) {
        switch (type) {
            case RecordDataConfig.TYPE_MORE_PAPA:
                initMorePapa();
                break;
            case RecordDataConfig.TYPE_ONE_PAPA:
                initOnePapa();
                break;
            case RecordDataConfig.TYPE_NO_DO:
                initNoDO();
                break;
        }
    }

    /**
     * 啪啪记录
     */
    private void initMorePapa() {
        //次数
        mLine.setVisibility(View.VISIBLE);
        mAvgTimeLy.setVisibility(View.VISIBLE);
        mRecordCount1.setText(ResourceHelper.getString(RecordDataConfig.SELF_RECORD_COUNT[0]));
        if (mData == null || mData.getMorePapa() == null) {
            return;
        }
        RecordSelfCount count = mData.getMorePapa().getMyCount();
        if (count != null) {
            setCountText(mRecordCount2, count.getCountAll(), "");
            setCountText(mTimes, count.getCountThirty(), "次");
            setCountText(mDays, count.getLastTime(), "天");
            setCountText(mAvgTime, count.getAvgTime(), "分钟");
            mRecordDataGroupView.setSampleText(getSampleText(count.getCountAll()));
        } else {
            mRecordDataGroupView.setSampleText(getSampleText("0"));
        }
        RecordMorePapa myData = mData.getMorePapa().getMyData();
        if (StringUtils.isEmpty(count.getCountAll())) {
            mRecordDataGroupView.setVisibility(View.VISIBLE);
            mRecordDataGroupView.setupGroupView(null);
        } else if (myData != null) {
            mRecordDataGroupView.setVisibility(View.VISIBLE);
            mRecordDataGroupView.setupGroupView(RecordSelfHelper.prepareMorePapaDataViewInfoList(myData));
        } else {
            mRecordDataGroupView.setVisibility(View.GONE);
        }

    }

    private void setCountText(TextView tv, String count, String type) {
        if (StringUtils.isNotEmpty(count)) {
            tv.setText(count + type);
        }
    }

    private String getSampleText(String count) {
        String text = ResourceHelper.getString(R.string.record_self_papa_sample_text);
        if (StringUtils.isEmpty(count)) {
            text = text.replace("#count#", "0");
        } else {
            text = text.replace("#count#", count);
        }
        String type = "啪啪";
        if (mType == RecordDataConfig.TYPE_ONE_PAPA) {
            type = "自嗨";
        } else if (mType == RecordDataConfig.TYPE_NO_DO) {
            type = "没做";
        }
        text = text.replace("#type#", type);
        return text;
    }

    /**
     * 自嗨记录
     */
    private void initOnePapa() {
        mLine.setVisibility(View.VISIBLE);
        mAvgTimeLy.setVisibility(View.VISIBLE);
        mRecordCount1.setText(ResourceHelper.getString(RecordDataConfig.SELF_RECORD_COUNT[1]));
        if (mData == null || mData.getOnePapa() == null) {
            return;
        }
        RecordSelfCount count = mData.getOnePapa().getMyCount();
        if (count != null) {
            setCountText(mRecordCount2, count.getCountAll(), "");
            setCountText(mTimes, count.getCountThirty(), "次");
            setCountText(mDays, count.getLastTime(), "天");
            setCountText(mAvgTime, count.getAvgTime(), "分钟");
            mRecordDataGroupView.setSampleText(getSampleText(count.getCountAll()));
        } else {
            mRecordDataGroupView.setSampleText(getSampleText("0"));
        }
        RecordOnePapa myData = mData.getOnePapa().getMyData();
        if (StringUtils.isEmpty(count.getCountAll())) {
            mRecordDataGroupView.setVisibility(View.VISIBLE);
            mRecordDataGroupView.setupGroupView(null);
        } else if (myData != null) {
            mRecordDataGroupView.setVisibility(View.VISIBLE);
            mRecordDataGroupView.setupGroupView(RecordSelfHelper.prepareOnePapaDataViewInfoList(myData));
        } else {
            mRecordDataGroupView.setVisibility(View.GONE);
        }
    }

    /**
     * 没做记录
     */
    private void initNoDO() {
        mLine.setVisibility(View.GONE);
        mAvgTimeLy.setVisibility(View.GONE);
        mRecordCount1.setText(ResourceHelper.getString(RecordDataConfig.SELF_RECORD_COUNT[2]));
        if (mData == null) {
            return;
        }
        RecordSelfCount count = mData.getNoPapa().getMyCount();
        if (count != null) {
            setCountText(mRecordCount2, count.getCountAll(), "");
            setCountText(mTimes, count.getCountThirty(), "次");
            setCountText(mDays, count.getLastTime(), "天");
            mRecordDataGroupView.setSampleText(getSampleText(count.getCountAll()));
        } else {
            mRecordDataGroupView.setSampleText(getSampleText("0"));
        }
        mRecordDataGroupView.setupGroupView(null);
    }

    @Override
    public void shareClicked(String sampleText) {
        mShareCount.setVisibility(View.GONE);
        mRecordDataGroupView.prepareShare();
        Bitmap bitmap = startSnapshot(mRootView);
        afterShareClicked(bitmap, sampleText);
    }

    private void afterShareClicked(Bitmap bitmap, String sampleText) {
        mShareCount.setVisibility(View.VISIBLE);
        mRecordDataGroupView.afterShare();
        if (bitmap == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        String path = CommonUtils.storePageSnapshot(bitmap);
        if (StringUtils.isEmpty(path)) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }

        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareImageUrl(path);
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_IMAGE);
        builder.setShareContent(sampleText);
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHARE;
        msg.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    public int getAllChildHeight(ViewGroup view) {
        int childCount = view.getChildCount();
        View child;
        int height = 0;
        LinearLayout.LayoutParams lp;
        for (int i = 0; i < childCount; i++) {
            child = view.getChildAt(i);
            lp = (LinearLayout.LayoutParams) child.getLayoutParams();
            height += lp.topMargin + lp.bottomMargin;
            height += child.getHeight();
        }
        return height;
    }

    private Bitmap startSnapshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), getAllChildHeight((ViewGroup) view), Bitmap.Config.RGB_565);
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        mRootView.draw(canvas);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_self_count_share:
                mShareLogo.setVisibility(View.VISIBLE);
                mShareCount.setVisibility(View.GONE);
                mRecordDataGroupView.prepareShare();
                Bitmap bitmap = startSnapshot(mCountLayout);
                afterShareClicked(bitmap, "");
                mShareLogo.setVisibility(View.GONE);
                mShareCount.setVisibility(View.VISIBLE);
                break;
        }
    }
}
