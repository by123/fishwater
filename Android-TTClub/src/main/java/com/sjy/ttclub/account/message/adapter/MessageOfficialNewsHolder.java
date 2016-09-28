package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.MessageUnreadCountRequestHelper;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

/**
 * Created by gangqing on 2015/12/10.
 * Email:denggangqing@ta2she.com
 */
public class MessageOfficialNewsHolder extends BaseHolder<MessageDialogs> implements View.OnClickListener {
    private TextView mName;
    private SimpleDraweeView mHeadImage;
    private TextView mContent;
    private TextView mMsgCount;
    private TextView mTime;

    public MessageOfficialNewsHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.account_message_official_news, null);
        mName = (TextView) view.findViewById(R.id.user_name);
        mName.setOnClickListener(this);
        mHeadImage = (SimpleDraweeView) view.findViewById(R.id.head_img);
        mHeadImage.setOnClickListener(this);
        mContent = (TextView) view.findViewById(R.id.last_content);
        mMsgCount = (TextView) view.findViewById(R.id.same_msg);
        mTime = (TextView) view.findViewById(R.id.last_post_time);
        return view;
    }

    @Override
    public void refreshUI(int position, MessageDialogs data) {
        //头像
        if (StringUtils.isNotEmpty(data.getHeadimageUrl())) {
            mHeadImage.setImageURI(Uri.parse(data.getHeadimageUrl()));
        } else {
            mHeadImage.setBackgroundResource(R.drawable.account_no_login_head_image);
        }
        //用户名
        mName.setText(data.getNickname());
        if (MessageUnreadCountRequestHelper.getBean() != null) {
            int notReadCount = MessageUnreadCountRequestHelper.getBean().officialMsgCount;
            if (notReadCount != 0) {
                mMsgCount.setVisibility(View.VISIBLE);
                mMsgCount.setText(String.valueOf(notReadCount));
            } else {
                mMsgCount.setVisibility(View.GONE);
            }
        }
        //最后一条内容
        if (!StringUtils.isEmpty(data.getContent())) {
            EmoticonsUtils.setContent(mContext, mContent, data.getContent().replace("/n", ""));
        }
        //时间
        mTime.setText(TimeUtil.getCDTime(data.getCreatetime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_img:
                break;
            case R.id.user_name:
                break;
        }
    }
}
