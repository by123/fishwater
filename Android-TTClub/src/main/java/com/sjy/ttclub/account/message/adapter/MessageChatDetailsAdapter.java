package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.ExternalLinksHelperParam;
import com.sjy.ttclub.bean.account.MessageDialogDetails;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.LetterExternalLinksHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class MessageChatDetailsAdapter extends BaseAdapter {
    private static final int TYPE_RIGHT_GOODS = 0;
    private static final int TYPE_RIGHT_NORMAL = 1;
    private static final int TYPE_LEFT_NORMAL = 2;
    private static final int MAX_TYPE = 3;
    private static final String LINK_TYPE_GOOD = "4";    //链接类型：商品
    private Context mContext;
    private List<MessageDialogDetails.LetterObj.Letters> mDetailsList = new ArrayList<MessageDialogDetails.LetterObj.Letters>();

    public MessageChatDetailsAdapter(Context context, List<MessageDialogDetails.LetterObj.Letters> detailsList) {
        this.mContext = context;
        this.mDetailsList = detailsList;
    }

    @Override
    public int getCount() {
        return mDetailsList == null ? 0 : mDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        MessageDialogDetails.LetterObj.Letters detail = mDetailsList.get(position);
        if (TextUtils.isEmpty(detail.getSenderFlag())) {
            return TYPE_LEFT_NORMAL;
        } else {
            int flag = StringUtils.parseInt(detail.getSenderFlag());
            if (flag != TYPE_LEFT_NORMAL && flag != TYPE_RIGHT_NORMAL) {
                return TYPE_LEFT_NORMAL;
            } else if (LINK_TYPE_GOOD.equals(detail.getLinkType()) && flag == TYPE_RIGHT_NORMAL) {
                return TYPE_RIGHT_GOODS;
            } else {
                return flag;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageDialogDetails.LetterObj.Letters detail = mDetailsList.get(position);
        Holder holder = new Holder();
        switch (getItemViewType(position)) {
            case TYPE_RIGHT_GOODS:
                convertView = View.inflate(mContext, R.layout.account_message_chat_right_good, null);
                holder.userIcon = (SimpleDraweeView) convertView.findViewById(R.id.user_head);
                holder.ta_she_secretary_good_title = (TextView) convertView.findViewById(R.id.ta_she_secretary_good_title);
                holder.ta_she_secretary_good_link = (TextView) convertView.findViewById(R.id.ta_she_secretary_good_link);
                initDataRightLinkTypeGood(holder, detail);
                break;
            case TYPE_RIGHT_NORMAL:
                convertView = View.inflate(mContext, R.layout.account_message_chat_right, null);
                holder.userIcon = (SimpleDraweeView) convertView.findViewById(R.id.user_head);
                holder.content = (TextView) convertView.findViewById(R.id.chat_content);
                holder.postTime = (TextView) convertView.findViewById(R.id.chat_time);
                initDataRight(holder, detail);
                break;
            case TYPE_LEFT_NORMAL:
                convertView = View.inflate(mContext, R.layout.account_message_chat_left, null);
                holder.userIcon = (SimpleDraweeView) convertView.findViewById(R.id.user_head_left);
                holder.content = (TextView) convertView.findViewById(R.id.chat_content);
                holder.postTime = (TextView) convertView.findViewById(R.id.chat_time_left);
                holder.link = (TextView) convertView.findViewById(R.id.check_content_link);
                initDataLeft(holder, detail);
                break;
        }
        initDataPublic(holder, detail);
        convertView.setTag(holder);
        return convertView;
    }

    public class Holder {
        SimpleDraweeView userIcon;
        TextView content, link, postTime, ta_she_secretary_good_title, ta_she_secretary_good_link;
    }

    private void initDataPublic(Holder holder, final MessageDialogDetails.LetterObj.Letters detail) {
        if (StringUtils.isEmpty(detail.getHeadimageUrl())) {
            holder.userIcon.setImageResource(R.drawable.account_no_login_head_image);
        } else {
            holder.userIcon.setImageURI(Uri.parse(detail.getHeadimageUrl()));
        }
    }

    private void initDataRightLinkTypeGood(Holder holder, final MessageDialogDetails.LetterObj.Letters detail) {
        holder.ta_she_secretary_good_title.setText(detail.getLetterContent());
        holder.ta_she_secretary_good_link.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.ta_she_secretary_good_link.setText(detail.getLinkContent());
        holder.ta_she_secretary_good_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToExternalLink(detail);
            }
        });
    }

    private void initDataRight(Holder holder, MessageDialogDetails.LetterObj.Letters detail) {
        if (!StringUtils.isEmpty(detail.getLetterContent())) {
            EmoticonsUtils.setContent(mContext, holder.content, detail.getLetterContent().replace("/n", ""));
        }
    }

    private void initDataLeft(Holder holder, final MessageDialogDetails.LetterObj.Letters detail) {
        if (TextUtils.isEmpty(detail.getLinkContent())) {
            holder.link.setVisibility(View.GONE);
        } else {
            holder.link.setVisibility(View.VISIBLE);
            holder.link.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            holder.link.setText(detail.getLinkContent());
        }
        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToExternalLink(detail);
            }
        });
        //官方消息
        if (!TextUtils.isEmpty(detail.getMsgId())) {
            if (!StringUtils.isEmpty(detail.getContent())) {
                EmoticonsUtils.setContent(mContext, holder.content, detail.getContent().replace("/n", ""));
            }
        } else {
            if (!StringUtils.isEmpty(detail.getLetterContent())) {
                EmoticonsUtils.setContent(mContext, holder.content, detail.getLetterContent().replace("/n", ""));
            }
            if (!Constant.TA_SHE_SECRETARY.equals(detail.getUserRoleId())) {
                goToUserInfoCenter(holder, detail);
            }
        }
    }

    private void goToUserInfoCenter(Holder holder, final MessageDialogDetails.LetterObj.Letters detail) {
        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
                message.arg1 = StringUtils.parseInt(detail.getUserId());
                MsgDispatcher.getInstance().sendMessage(message);
            }
        });
    }

    private void goToExternalLink(MessageDialogDetails.LetterObj.Letters detail) {
        ExternalLinksHelperParam param = new ExternalLinksHelperParam();
        param.setType(detail.getLinkType());
        param.setId(detail.getLinkUrl());
        LetterExternalLinksHelper linksHelper = new LetterExternalLinksHelper();
        linksHelper.goExternalLinks(param);
    }
}
