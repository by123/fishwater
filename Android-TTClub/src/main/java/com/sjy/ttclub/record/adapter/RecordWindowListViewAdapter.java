package com.sjy.ttclub.record.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.record.RecordConfig;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class RecordWindowListViewAdapter extends BaseAdapter implements CheckBox.OnCheckedChangeListener {

    public final static String TAG = "RecordWindowListViewAdapter";

    private Context mContext;
    private ViewHolder mViewHolder;

    private RecordGridViewAdapter mRecordGridViewAdapter;

    private RecordConfig mRecordConfig;

    public RecordWindowListViewAdapter(Context ctx, int cate) {
        mContext = ctx;
        mRecordConfig = RecordConfig.getInstance();
        mRecordConfig.init(AccountManager.getInstance().getSex(), cate);//初始化数据
    }

    private void setupRecordView(ViewHolder viewHolder, int type) {
        viewHolder.ckbHeat.setVisibility(View.GONE);
        viewHolder.ckbHeat.setChecked(false);
        viewHolder.ckbHeat.setOnCheckedChangeListener(this);
        viewHolder.ckbProp.setVisibility(View.GONE);
        viewHolder.ckbProp.setChecked(false);
        viewHolder.ckbProp.setOnCheckedChangeListener(this);

        viewHolder.gridView.setVisibility(View.GONE);
        switch (type) {
            case RecordConfig.TYPE_MYSELF_WHEN:
                createRecordGridViewAdapter(RecordConfig.TYPE_MYSELF_WHEN, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_myself_when));
                break;
            case RecordConfig.TYPE_PAPA_WHEN:
                createRecordGridViewAdapter(RecordConfig.TYPE_MYSELF_WHEN, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_papa_when));
                break;
            case RecordConfig.TYPE_FEEL:
                createRecordGridViewAdapter(RecordConfig.TYPE_FEEL, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_feel));
                break;
            case RecordConfig.TYPE_HEAT:
                viewHolder.ckbHeat.setVisibility(View.VISIBLE);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_heat));
                break;
            case RecordConfig.TYPE_IS_TOOLS:
                viewHolder.ckbProp.setVisibility(View.VISIBLE);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_tools));
                break;
            case RecordConfig.TYPE_LIVEN:
                createRecordGridViewAdapter(RecordConfig.TYPE_LIVEN, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_liven));
                break;
            case RecordConfig.TYPE_POSTURE:
                createRecordGridViewAdapter(RecordConfig.TYPE_POSTURE, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_posture));
                break;
            case RecordConfig.TYPE_SCENE:
                createRecordGridViewAdapter(RecordConfig.TYPE_SCENE, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_scene));
                break;
            case RecordConfig.TYPE_TIME:
                createRecordGridViewAdapter(RecordConfig.TYPE_TIME, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_time));
                break;
            case RecordConfig.TYPE_TOOLS:
                createRecordGridViewAdapter(RecordConfig.TYPE_TOOLS, viewHolder);
                mViewHolder.tvTitle.setText(mContext.getString(R.string.record_title_tools));
                break;
        }
    }

    private void createRecordGridViewAdapter(int type, ViewHolder viewHolder) {
        viewHolder.gridView.setVisibility(View.VISIBLE);
        mRecordGridViewAdapter = new RecordGridViewAdapter(mContext, type);
        viewHolder.gridView.setAdapter(mRecordGridViewAdapter);
        viewHolder.gridView.setNumColumns(mRecordGridViewAdapter.getCount());
    }

    private View createViewByItemInfo() {
        mViewHolder = new ViewHolder();
        View convertView = View.inflate(mContext, R.layout.record_window_list_items, null);
        mViewHolder.ckbHeat = (CheckBox) convertView.findViewById(R.id.record_window_list_items_btn_heat);
        mViewHolder.ckbProp = (CheckBox) convertView.findViewById(R.id.record_window_list_items_btn_prop);
        mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.record_window_list_items_title);
        mViewHolder.gridView = (GridView) convertView.findViewById(R.id.record_window_list_items_grid);
        convertView.setTag(mViewHolder);
        return convertView;
    }

    @Override
    public int getCount() {
        return mRecordConfig.getFrameWork().length;
    }

    @Override
    public Object getItem(int position) {
        return mRecordConfig.getFrameWork()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createViewByItemInfo();
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        setupRecordView(mViewHolder, mRecordConfig.getFrameWork()[position]);
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String value;
        if (buttonView.getId() == R.id.record_window_list_items_btn_heat) {
            value = isChecked ? RecordConfig.HEAT : RecordConfig.UN_HEAT;
            mRecordConfig.put(RecordConfig.TYPE_HEAT, value);
        } else {
            value = isChecked ? RecordConfig.TOOLS : RecordConfig.UN_TOOLS;
            mRecordConfig.put(RecordConfig.TYPE_IS_TOOLS, value);
        }
    }

    class ViewHolder {
        TextView tvTitle;
        CheckBox ckbHeat, ckbProp;
        GridView gridView;
    }
}
