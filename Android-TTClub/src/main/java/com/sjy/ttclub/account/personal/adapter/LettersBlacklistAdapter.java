package com.sjy.ttclub.account.personal.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.MessageChatDetailsRequestHelper;
import com.sjy.ttclub.bean.account.BlacklistBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

import java.util.List;

/**
 * Created by gangqing on 2015/12/31.
 * Email:denggangqing@ta2she.com
 */
public class LettersBlacklistAdapter extends BaseAdapter implements IHttpCallBack {
    private List<BlacklistBean.BlacklistObj.Blacklists> mList;
    private Context mContext;
    private MessageChatDetailsRequestHelper mHelper;
    private int mPosition = -1;

    public LettersBlacklistAdapter(Context context, List<BlacklistBean.BlacklistObj.Blacklists> list) {
        mContext = context;
        mList = list;
        mHelper = new MessageChatDetailsRequestHelper(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BlacklistBean.BlacklistObj.Blacklists user = mList.get(position);
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.account_personal_blacklist_item, null);
            holder = new Holder();
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.userLevel = (TextView) convertView.findViewById(R.id.user_level);
            holder.userIcon = (SimpleDraweeView) convertView.findViewById(R.id.user_icon);
            holder.btn_delete = (TextView) convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        initData(holder, user, position);
        return convertView;
    }

    private void initData(final Holder viewHolder, final BlacklistBean.BlacklistObj.Blacklists user, final int position) {
        viewHolder.userName.setText(user.getNickname());
        if (StringUtils.isEmpty(user.getHeadimageUrl())) {
            viewHolder.userIcon.setImageResource(R.drawable.account_no_login_head_image);
        } else {
            viewHolder.userIcon.setImageURI(Uri.parse(user.getHeadimageUrl()));
        }
        if (String.valueOf(CommonConst.SEX_MAN).equals(user.getUserSex())) {
            viewHolder.userLevel.setBackgroundResource(R.drawable.account_level_man);
            viewHolder.userLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_man));
        } else {
            viewHolder.userLevel.setBackgroundResource(R.drawable.account_level_woman);
            viewHolder.userLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_woman));
        }
        viewHolder.userLevel.setText(String.valueOf(user.getUserLevel()));
        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                mHelper.shieldingRequest(user.getUserId(), Constant.CANCEL_BLACK, LettersBlacklistAdapter.this);
            }
        });
    }

    @Override
    public <T> void onSuccess(T obj, String result) {
        if (mPosition != -1) {
            mList.remove(mPosition);
            mPosition = -1;
        }
        ToastHelper.showToast(mContext, R.string.account_personal_blacklist_cancel, Toast.LENGTH_SHORT);
        notifyDataSetChanged();
    }

    @Override
    public void onError(String errorStr, int code) {
        if (HttpCode.INVALID_TOKEN_CODE == code) {
            AccountManager.getInstance().logout();
            AccountManager.getInstance().tryOpenGuideLoginWindow();
        }
    }

    public static class Holder {
        TextView userLevel;
        TextView userName;
        SimpleDraweeView userIcon;
        TextView btn_delete;
    }
}
