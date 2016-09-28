package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sjy.ttclub.bean.account.MessageDialogs;

import java.util.List;

/**
 * Created by gangqing on 2015/12/25.
 * Email:denggangqing@ta2she.com
 */
public class MessageListAdapter extends BaseAdapter {
    private static final int TYPE_OFFICIAL_NEWS = 0;
    private static final int TYPE_NORMAL_NEWS = 1;
    private static final int MAX_TYPE = 2;
    private Context mContext;
    private List<MessageDialogs> mLetterList;

    public MessageListAdapter(Context context, List<MessageDialogs> letterList) {
        mContext = context;
        mLetterList = letterList;
    }

    @Override
    public int getCount() {
        return mLetterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (!TextUtils.isEmpty(mLetterList.get(position).getMsgId())) {
            type = TYPE_OFFICIAL_NEWS;
        } else {
            type = TYPE_NORMAL_NEWS;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE;
    }

    @Override
    public Object getItem(int position) {
        return mLetterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageDialogs data = mLetterList.get(position);
        BaseHolder holder;
        int type = getItemViewType(position);
//        if (type == TYPE_OFFICIAL_NEWS) {
//            if (convertView == null) {
//                holder = new MessageOfficialNewsHolder(mContext);
//                convertView = holder.getRootView();
//            } else {
//                holder = (BaseHolder) convertView.getTag();
//            }
//        } else {
            if (convertView == null) {
                holder = new MessagePersonalLetterHolder(mContext);
                convertView = holder.getRootView();
            } else {
                holder = (BaseHolder) convertView.getTag();
            }
//        }
        holder.setData(position, data);
        return convertView;
    }
}
