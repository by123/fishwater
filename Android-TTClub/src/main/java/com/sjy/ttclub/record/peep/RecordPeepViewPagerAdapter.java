package com.sjy.ttclub.record.peep;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.data.RecordPeepData;
import com.sjy.ttclub.record.RecordConst;
import com.sjy.ttclub.record.RecordDataConfig;
import com.sjy.ttclub.record.widget.RecordDataGroupView;
import com.sjy.ttclub.record.widget.RecordDataViewInfo;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/11.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private RecordPeepData mPeepData;
    private ArrayList<Integer> mList = new ArrayList<Integer>();
    private RecordPeepSelectInfo mSelectInfo;
    private String mSpecTimeName;

    public RecordPeepViewPagerAdapter(Context context) {
        super();
        mContext = context;
    }

    public void setRecordPeepData(RecordPeepData peepData, RecordPeepSelectInfo selectInfo) {
        mPeepData = peepData;
        mSelectInfo = selectInfo;
        mList.clear();
        if (peepData.getPeepData() != null) {
            mList.add(RecordDataConfig.TYPE_MORE_PAPA);
            mList.add(RecordDataConfig.TYPE_ONE_PAPA);
            mList.add(RecordDataConfig.TYPE_NO_DO);
        }
        notifyDataSetChanged();
    }

    public void setSpecTimeName(String name) {
        mSpecTimeName = name;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = createViewByPosition(position);
        container.addView(view);
        return view;
    }

    private RecordDataGroupView createViewByPosition(int position) {
        RecordDataGroupView view;
        int type = mList.get(position);
        String sex = RecordPeepHelper.getPeepSex(mSelectInfo.sex);
        String time = RecordPeepHelper.getPeepTime(mSelectInfo.time, mSpecTimeName);
        boolean showMe = mSelectInfo.showMe == RecordConst.PEEP_ME_ENABLE;
        view = new RecordDataGroupView(mContext);
        ArrayList<RecordDataViewInfo> list;
        int count;
        if (type == RecordDataConfig.TYPE_ONE_PAPA) {
            list = RecordPeepHelper.prepareOnePapaDataViewInfoList(mPeepData, sex, time, showMe);
            count = mPeepData.getPeepData().getOnePapa().getUserSampleCount();
        } else if (type == RecordDataConfig.TYPE_NO_DO) {
            list = RecordPeepHelper.prepareNoPapaDataViewInfoList(mPeepData, sex, time, showMe);
            count = mPeepData.getPeepData().getNoPapa().getUserSampleCount();
        } else {
            list = RecordPeepHelper.prepareMorePapaDataViewInfoList(mPeepData, sex, time, showMe);
            count = mPeepData.getPeepData().getMorePapa().getUserSampleCount();
        }
        if (list.isEmpty()) {
            view.setVisibility(View.GONE);
        }
        view.setVisibility(View.VISIBLE);
        view.setShareText(ResourceHelper.getString(R.string.record_data_share_discuss));
        view.setSampleText(getSampleText(count));
        view.setupGroupView(list);
        return view;
    }

    private String getSampleText(int count) {
        String text = ResourceHelper.getString(R.string.record_data_share_content);
        text = text.replace("#count#", String.valueOf(count));
        return text;
    }
}
