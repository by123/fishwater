package com.sjy.ttclub.account.setting.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.message.adapter.MessageChatDetailsAdapter;
import com.sjy.ttclub.bean.account.FeedbackBean;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by gangqing on 2016/1/4.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackContentAdapter extends BaseAdapter {
    private static final int TYPE_QUESTION = 0;
    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 2;
    private static final int TYPE_COUNT = 3;
    private List<FeedbackBean.FeedbackInfo> mMessageArray;
    private Context mContext;

    public FeedbackContentAdapter(Context context, List<FeedbackBean.FeedbackInfo> messageArrays) {
        mContext = context;
        mMessageArray = messageArrays;
    }


    @Override
    public int getCount() {
        return mMessageArray == null ? 0 : mMessageArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else if ("0".equals(mMessageArray.get(position).uuid)) {
            return TYPE_LEFT;
        } else {
            return TYPE_RIGHT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        switch (getItemViewType(position)) {
            case TYPE_QUESTION: //问题
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.feedback_question_item, null);
                    holder = new Holder();
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                break;
            case TYPE_LEFT: //后台回复
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.feedback_left_item, null);
                    holder = new Holder();
                    convertView.setTag(holder);
                }else{
                    holder = (Holder) convertView.getTag();
                }
                break;
            case TYPE_RIGHT:    //我
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.feedback_right_item, null);
                    holder = new Holder();
                    convertView.setTag(holder);
                }else{
                    holder = (Holder) convertView.getTag();
                }
                break;
        }

        holder.content = (TextView) convertView.findViewById(R.id.feedback_question_content);
        holder.time = (TextView) convertView.findViewById(R.id.feedback_question_time);

        if (!StringUtils.isEmpty(mMessageArray.get(position).content)) {
            EmoticonsUtils.setContent(mContext, holder.content, mMessageArray.get(position).content.replace("/n", ""));
        }
        holder.time.setText(mMessageArray.get(position).createTime);

        return convertView;
    }

    public class Holder {
        TextView content;
        TextView time;
    }
}
