package com.sjy.ttclub.record.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.RecordDataDay;
import com.sjy.ttclub.record.RecordConfig;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class RecordListViewAdapter extends BaseAdapter {

    public final static String TAG = "RecordListViewAdapter";

    private int[] mCategory = {R.string.record_state_papa, R.string.record_state_myself};

    private Context mContext;

    private ViewHolder viewHolder;

    private List<RecordDataDay> mListData;

    private RecordConfig mRecordConfig;

    public RecordListViewAdapter(Context ctx, List<RecordDataDay> list) {
        mContext = ctx;
        mListData = list;
        mRecordConfig = RecordConfig.getInstance();
        mRecordConfig.initFeel();
        mRecordConfig.initTime();
    }

    private void setupRecordView(int position) {
        RecordDataDay recordDataDay = mListData.get(position);
        int category = StringUtils.parseInt(recordDataDay.getCategory());
        if (category == RecordConfig.RECORD_TYPE_NOTHING || category == RecordConfig.RECORD_TYPE_DYM) {
            noLayoutVisibility(View.VISIBLE);
            viewHolder.noTodayLayout.setVisibility(View.VISIBLE);
        } else if (category == RecordConfig.RECORD_TYPE_NO) {
            viewHolder.noTodayLayout.setVisibility(View.INVISIBLE);
            noLayoutVisibility(View.VISIBLE);
        } else {
            noLayoutVisibility(View.GONE);
            if (RecordConfig.UN_HEAT.equals(recordDataDay.getOrgasm() + "")) {
                viewHolder.ivHeat.setImageResource(R.drawable.record_window_list_items_heat_normal);
            } else if (RecordConfig.HEAT.equals(recordDataDay.getOrgasm() + "")) {
                viewHolder.ivHeat.setImageResource(R.drawable.record_window_list_items_heat_selected);
            }
            if (!StringUtils.isEmpty(recordDataDay.getFeel())) {
                viewHolder.ivStatus.setImageResource(mRecordConfig.getFeel(recordDataDay.getFeel()).getIcon());
            }
            if (!StringUtils.isEmpty(recordDataDay.getCreatetime())) {
                viewHolder.tvDateTime.setText(changeTimeRange(recordDataDay.getPapaTimeRange()));
            }
            if (!StringUtils.isEmpty(recordDataDay.getTime())) {
                String time = mRecordConfig.getTime(recordDataDay.getTime()).getText();
                viewHolder.tvContent.setText(mContext.getString(mCategory[category - 1]) + " " + time);
            }
        }
    }

    private String changeTimeRange(String papaTimeRange) {
        String timeRange;
        switch (papaTimeRange) {
            case "1":
                timeRange = ResourceHelper.getString(R.string.record_msg_time_ange1);
                break;
            case "2":
                timeRange = ResourceHelper.getString(R.string.record_msg_time_ange2);
                break;
            case "3":
                timeRange = ResourceHelper.getString(R.string.record_msg_time_ange3);
                break;
            default:
                timeRange = ResourceHelper.getString(R.string.record_msg_time_ange1);
                break;
        }
        return timeRange;
    }

    private void noLayoutVisibility(int visibility) {
        if (visibility == View.GONE) {
            viewHolder.editLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editLayout.setVisibility(View.GONE);
        }
        viewHolder.noLayout.setVisibility(visibility);
    }

    private View createViewByItemInfo() {
        View view = View.inflate(mContext, R.layout.record_list_items, null);
        viewHolder.editLayout = (LinearLayout) view.findViewById(R.id.edit_layout);
        viewHolder.noLayout = (LinearLayout) view.findViewById(R.id.no_layout);
        viewHolder.noTodayLayout = (LinearLayout) view.findViewById(R.id.no_today_layout);
        viewHolder.ivHeat = (ImageView) view.findViewById(R.id.iv_heat);
        viewHolder.ivStatus = (ImageView) view.findViewById(R.id.iv_status);
        viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
        viewHolder.tvDateTime = (TextView) view.findViewById(R.id.tv_datetime);
        viewHolder.noTodayLayout.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = createViewByItemInfo();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setupRecordView(position);
        return convertView;
    }

    class ViewHolder {
        LinearLayout editLayout, noLayout, noTodayLayout;
        ImageView ivHeat, ivStatus;
        TextView tvContent, tvDateTime;
    }
}
