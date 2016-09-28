package com.sjy.ttclub.record.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.sjy.ttclub.bean.record.data.RecordSelfData;
import com.sjy.ttclub.record.RecordDataConfig;
import com.sjy.ttclub.record.self.RecordSelfViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2016/1/9.
 * Email:denggangqing@ta2she.com
 */
public class RecordSelfViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<Integer> mList = new ArrayList<>();
    private RecordSelfData mSelfData;


    public RecordSelfViewPagerAdapter(Context context) {
        super();
        mContext = context;
    }

    public void setRecordSelfData(RecordSelfData selfData) {
        mSelfData = selfData;
        mList.clear();
        if (mSelfData != null) {
            mList.add(RecordDataConfig.TYPE_MORE_PAPA);
            mList.add(RecordDataConfig.TYPE_ONE_PAPA);
            mList.add(RecordDataConfig.TYPE_NO_DO);
        }
        notifyDataSetChanged();
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
        RecordSelfViewPagerHelper helper = new RecordSelfViewPagerHelper(mContext);
        helper.setData(mSelfData, mList.get(position));
        View view = helper.getRootView();
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
        container.addView(view);
        return view;
    }
}
