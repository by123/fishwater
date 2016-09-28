package com.sjy.ttclub.record.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.record.RecordConfig;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class RecordGridViewAdapter extends BaseAdapter {

    public final static String TAG = "RecordGridViewAdapter";

    private final static int ADD = 0;
    private final static int REMOVE = 1;

    private int mType;

    private int temp = -1;//GridView+CheckBox 单选

    private Context mContext;
    private List<RecordConfig.RecordBean> mGridViewItemInfo;

    private RecordConfig mRecordConfig;

    public RecordGridViewAdapter(Context context, int type) {
        mContext = context;
        mType = type;
        mRecordConfig = RecordConfig.getInstance();

        setGridViewItemInfo();
        notifyDataSetChanged();
    }

    private void setGridViewItemInfo() {
        switch (mType) {
            case RecordConfig.TYPE_MYSELF_WHEN:
                mRecordConfig.initWhen();
                mGridViewItemInfo = mRecordConfig.getWhen();
                break;
            case RecordConfig.TYPE_FEEL:
                mRecordConfig.initFeel();
                mGridViewItemInfo = mRecordConfig.getFeel();
                break;
            case RecordConfig.TYPE_POSTURE:
                mRecordConfig.initPosture();
                mGridViewItemInfo = mRecordConfig.getPosture();
                break;
            case RecordConfig.TYPE_SCENE:
                mRecordConfig.initScene();
                mGridViewItemInfo = mRecordConfig.getScene();
                break;
            case RecordConfig.TYPE_LIVEN:
                mRecordConfig.initLiven();
                mGridViewItemInfo = mRecordConfig.getLiven();
                break;
            case RecordConfig.TYPE_TOOLS:
                mRecordConfig.initTools();
                mGridViewItemInfo = mRecordConfig.getTools();
                break;
            case RecordConfig.TYPE_TIME:
                mRecordConfig.initTime();
                mGridViewItemInfo = mRecordConfig.getTime();
                break;
        }
    }

    private void setupGridView(View view, int position) {

        final String value = mGridViewItemInfo.get(position).getValue();
        int icon = mGridViewItemInfo.get(position).getIcon();
        String text = mGridViewItemInfo.get(position).getText();

        final TextView tv = (TextView) view.findViewById(R.id.record_window_grid_items_tv);
        tv.setText(text);

        CheckBox ckb = (CheckBox) view.findViewById(R.id.record_window_grid_items_ckb);
        resetCheckBox(position, ckb);
        ckb.setTag(position);
        ckb.setBackgroundResource(icon);
        ckb.setButtonDrawable(android.R.color.transparent);
        ckb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv.setTextColor(mContext.getResources().getColor(R.color.white));
                    changeListRecord(value, ADD);
                    temp = (int) buttonView.getTag();
                    notifyDataSetChanged();
                } else {
                    tv.setTextColor(mContext.getResources().getColor(R.color.record_list_items_content));
                    changeListRecord(value, REMOVE);
                }
            }
        });
    }

    private void changeListRecord(String value, int operation) {
        if (mType == RecordConfig.TYPE_POSTURE) {
            listRecordArrayAdd(value, operation);
        } else if (mType == RecordConfig.TYPE_LIVEN) {
            listRecordArrayAdd(value, operation);
        } else if (mType == RecordConfig.TYPE_TOOLS) {
            listRecordArrayAdd(value, operation);
        } else {
            listRecordAdd(value, operation);
        }
    }

    private void listRecordArrayAdd(String value, int operation) {
        int cate = mGridViewItemInfo.get(0).getCategory();
        if (operation == ADD) {
            mRecordConfig.putArray(cate, value);
        } else {
            mRecordConfig.removeArray(cate, value);
        }
    }

    private void listRecordAdd(String value, int operation) {
        int cate = mGridViewItemInfo.get(0).getCategory();
        if (operation == ADD) {
            mRecordConfig.put(cate, value);
        } else {
            mRecordConfig.remove(cate, value);
        }
    }

    /**
     * 重置CheckBox
     *
     * @param position
     * @param ckb
     */
    private void resetCheckBox(int position, CheckBox ckb) {
        if (mType != RecordConfig.TYPE_POSTURE && mType != RecordConfig.TYPE_LIVEN && mType != RecordConfig.TYPE_TOOLS) {
            if (temp != position) {
                ckb.setChecked(false);
            }
        }
    }

    @Override
    public int getCount() {
        return mGridViewItemInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mGridViewItemInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.record_window_grid_items, null);
        }
        setupGridView(convertView, position);
        return convertView;
    }
}
