package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.account.ReplyMeMsgArray;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class ReplyMeAdapter extends BaseAdapter {
    public final static String MSG_TYPE_CARD = "1";
    public final static String MSG_TYPE_INTER_LOCUTION = "2";
    public final static String MSG_TYPE_ARTICLE = "3";
    public final static String MSG_TYPE_CARD_REPLY = "4";
    public final static String MSG_TYPE_INTER_LOCUTION_REPLY = "5";
    public final static String MSG_TYPE_ARTICLE_REPLY = "6";

    private List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> mArrayList;
    private Context mContext;

    public ReplyMeAdapter(Context context, List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> arraysList) {
        this.mContext = context;
        mArrayList = arraysList;
    }

    @Override
    public int getCount() {
        return mArrayList == null ? 0 : mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReplyMeMsgArray.MsgArrayObj.MsgArrays msg = mArrayList.get(position);
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.account_message_reply_me_item, null);
            holder = new Holder();
            holder.replyUserIcon = (SimpleDraweeView) convertView.findViewById(R.id.head_img);
            holder.level = (TextView) convertView.findViewById(R.id.level);
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.replyUserName = (TextView) convertView.findViewById(R.id.user_name);
            holder.replyContent = (TextView) convertView.findViewById(R.id.reply_content);
            holder.replyTime = (TextView) convertView.findViewById(R.id.reply_time);
            holder.rl_reply_01 = (RelativeLayout) convertView.findViewById(R.id.rl_reply_01);
            holder.ll_reply_content = (LinearLayout) convertView.findViewById(R.id.ll_reply_content);
            holder.replyType = (TextView) convertView.findViewById(R.id.reply_user_name_01);
            holder.replyTypeContent = (TextView) convertView.findViewById(R.id.reply_content_01);
            holder.img = (SimpleDraweeView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        initData(holder, msg);
        return convertView;
    }

    private void initData(Holder holder, ReplyMeMsgArray.MsgArrayObj.MsgArrays msg) {
        if (StringUtils.isNotEmpty(msg.getHeadimageUrl())) {
            holder.replyUserIcon.setImageURI(Uri.parse(msg.getHeadimageUrl()));
        } else {
            holder.replyUserIcon.setBackgroundResource(R.drawable.account_no_login_head_image);
        }
        holder.replyUserName.setText(msg.getNickname());

        Date date = new Date(StringUtils.parseLong(msg.getCreatetime()) * 1000);
        holder.replyTime.setText(TimeUtil.getCDTime(date));
        if (StringUtils.isNotEmpty(msg.getReplyContent())) {
            EmoticonsUtils.setContent(mContext, holder.replyContent, msg.getReplyContent().replace("/n", ""));
        } else {
            setSameReplyContent(holder.replyContent, msg);
        }
        if (StringUtils.isNotEmpty(msg.getComment())) {
            holder.comment.setVisibility(View.VISIBLE);
            EmoticonsUtils.setContent(mContext, holder.comment, ("评论：" + msg.getComment()).replace("/n", ""));
        } else {
            holder.comment.setVisibility(View.GONE);
        }
        switch (msg.getType()) {
            case MSG_TYPE_INTER_LOCUTION:
            case MSG_TYPE_INTER_LOCUTION_REPLY:
                if (StringUtils.isNotEmpty(msg.getImageUrl())) {
                    holder.img.setImageURI(Uri.parse(msg.getImageUrl()));
                } else {
                    holder.img.setImageResource(R.drawable.account_message_reply_me_ask);
                }
                break;
            case MSG_TYPE_CARD:
            case MSG_TYPE_ARTICLE:
            case MSG_TYPE_CARD_REPLY:
            default:
                if (StringUtils.isNotEmpty(msg.getImageUrl())) {
                    holder.img.setImageURI(Uri.parse(msg.getImageUrl()));
                } else {
                    holder.img.setImageResource(R.drawable.account_message_reply_me_post);
                }
                break;
        }
        holder.replyType.setText(msg.getPostTitle());
        //用户等级
        if (msg.getSex().equals(String.valueOf(CommonConst.SEX_MAN))) {
            holder.level.setText(msg.getLevel());
            holder.level.setBackgroundResource(R.drawable.community_level_man);
            holder.level.setTextColor(ResourceHelper.getColor(R.color.account_level_color_man));
        } else {
            holder.level.setText(msg.getLevel());
            holder.level.setBackgroundResource(R.drawable.community_level_woman);
            holder.level.setTextColor(ResourceHelper.getColor(R.color.account_level_color_woman));
        }
        holder.replyUserIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 用户信息网络获取
            }
        });
    }

    private void setSameReplyContent(TextView replyContent, ReplyMeMsgArray.MsgArrayObj.MsgArrays msg) {
        switch (msg.getType()) {
            case MSG_TYPE_CARD:
                replyContent.setText(R.string.account_message_same_card);
                break;
            case MSG_TYPE_INTER_LOCUTION:
                replyContent.setText(R.string.account_message_same_inter_locution);
                break;
            case MSG_TYPE_CARD_REPLY:
                replyContent.setText(R.string.account_message_same_card_reply);
                break;
            case MSG_TYPE_INTER_LOCUTION_REPLY:
                replyContent.setText(R.string.account_message_same_inter_locution_reply);
                break;
            case MSG_TYPE_ARTICLE_REPLY:
                replyContent.setText(R.string.account_message_same_article_reply);
                break;
        }
    }

    public class Holder {
        TextView comment;
        TextView level;
        TextView replyUserName;
        TextView replyContent;
        TextView replyTime;
        TextView replyType;
        TextView replyTypeContent;
        SimpleDraweeView replyUserIcon;
        RelativeLayout rl_reply_01;
        LinearLayout ll_reply_content;
        SimpleDraweeView img;
    }
}
