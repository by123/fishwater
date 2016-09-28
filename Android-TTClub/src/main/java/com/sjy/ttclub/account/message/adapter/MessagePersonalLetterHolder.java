package com.sjy.ttclub.account.message.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

/**
 * Created by gangqing on 2015/12/3.
 * Email:denggangqing@ta2she.com
 */
public class MessagePersonalLetterHolder extends BaseHolder<MessageDialogs> implements View.OnClickListener {
    public final static String IS_PULL_BLACK = "1";
    public final static String SEX_MAN = "1";
    public final static String TA_SHE_SECRETARY = "6";
    private SimpleDraweeView mUserIcon;
    private TextView mUserName;
    private TextView mSameMsg;
    private TextView mLastContent;
    private TextView mLastTime;
    private TextView mLevel;
    private TextView mIsBlack;
    private CheckBox mCheck;
    private LinearLayout mContent;
    private ImageView mOfficialIco;
    private MessageDialogs mData;

    public MessagePersonalLetterHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.account_message_personal_letter_item, null);
        mUserIcon = (SimpleDraweeView) view.findViewById(R.id.head_img);
        mUserIcon.setOnClickListener(this);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mUserName.setOnClickListener(this);
        mSameMsg = (TextView) view.findViewById(R.id.same_msg);
        mLastContent = (TextView) view.findViewById(R.id.last_content);
        mLastTime = (TextView) view.findViewById(R.id.last_post_time);
        mLevel = (TextView) view.findViewById(R.id.level);
        mLevel.setOnClickListener(this);
        mIsBlack = (TextView) view.findViewById(R.id.black);
        mCheck = (CheckBox) view.findViewById(R.id.check);
        mContent = (LinearLayout) view.findViewById(R.id.ll_content);
        mOfficialIco = (ImageView) view.findViewById(R.id.official_ico);
        return view;
    }

    @Override
    public void refreshUI(int position, MessageDialogs data) {
        mData = data;
        //不同点：等级
        if (TA_SHE_SECRETARY.equals(data.getUserRoleId())) { //她他小秘
            ViewGroup.LayoutParams p = mLevel.getLayoutParams();
            p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLevel.setLayoutParams(p);
            mOfficialIco.setVisibility(View.VISIBLE);
            mLevel.setVisibility(View.GONE);
            mIsBlack.setVisibility(View.GONE);
        } else {  //普通私信
            String sex = data.getUserSex();
            if (TextUtils.isEmpty(sex)) {
                return;
            }
            switch (data.getUserSex()) {
                case SEX_MAN:
                    setSex(R.drawable.account_level_man, R.color.account_level_color_man);
                    break;
                default:
                    setSex(R.drawable.account_level_woman, R.color.account_level_color_woman);
                    break;
            }
            mOfficialIco.setVisibility(View.GONE);
            mLevel.setVisibility(View.VISIBLE);
            mIsBlack.setVisibility(View.VISIBLE);
            mLevel.setText(data.getUserLevel());
        }
        setNormal(data);
        if (StringUtils.isNotEmpty(data.getHeadimageUrl())) {
            mUserIcon.setImageURI(Uri.parse(data.getHeadimageUrl()));
        } else {
            mUserIcon.setBackgroundResource(R.drawable.account_no_login_head_image);
        }
        mUserName.setText(data.getNickname());
    }

    private void setSex(int resId, int colorId) {
        mLevel.setTextColor(ResourceHelper.getColor(colorId));
        mLevel.setBackgroundResource(resId);
    }

    private void setNormal(final MessageDialogs data) {
        if (!TA_SHE_SECRETARY.equals(data.getUserRoleId()) && StringUtils.parseInt(data.getNotreadCount()) > 0) {
            mSameMsg.setVisibility(View.VISIBLE);
            mSameMsg.setText(data.getNotreadCount());
        } else {
            mSameMsg.setVisibility(View.GONE);
        }
        if (!StringUtils.isEmpty(data.getLetterContent())) {
            EmoticonsUtils.setContent(mContext, mLastContent, data.getLetterContent().replace("/n", ""));
        }
        mLastTime.setText(TimeUtil.getCDTime(data.getLetterTime()));
        if (data.getPullBlackFlag().equals(IS_PULL_BLACK)) {
            mIsBlack.setVisibility(View.VISIBLE);
        } else {
            mIsBlack.setVisibility(View.GONE);
        }

        if (data.isShow()) {
            mCheck.setVisibility(View.VISIBLE);
            ViewTreeObserver vto2 = mCheck.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mCheck.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    param.width = (int) getScreenWidth(mContext) + mCheck.getWidth();
                    mContent.setPadding(0, 0, 35, 0);
                    mContent.setLayoutParams(param);
                }
            });

        } else {
            mCheck.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param.width = (int) getScreenWidth(mContext);
            mContent.setPadding(0, 0, 0, 0);
            mContent.setLayoutParams(param);
        }
        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheck(isChecked);
            }
        });
        if (data.isCheck()) {
            mCheck.setSelected(true);
            mCheck.requestFocus();
        } else {
            mCheck.setSelected(false);
            mCheck.requestFocus();
        }
        mCheck.setChecked(data.isCheck());
    }

    public static float getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_img:
            case R.id.user_name:
            case R.id.level:
                gotoPersonalCenter();
                break;
        }
    }

    private void gotoPersonalCenter() {
        if (Constant.TA_SHE_SECRETARY.equals(mData.getUserRoleId())) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = StringUtils.parseInt(mData.getUserId());
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
