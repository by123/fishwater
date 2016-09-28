package com.sjy.ttclub.account.personal.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.RelationshipRequest;
import com.sjy.ttclub.bean.account.RelationshipBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaImageView;

import java.util.List;

/**
 * Created by 邓钢清 on 2015/11/11.
 * Email: denggangqing@ta2she.com
 */
public class FansAdapter extends BaseAdapter {
    private Context mContext;
    private List<RelationshipBean.Data.Follow> data;
    private static final String ONLY_FANS = "0";
    private static final String ATTENTION_EACH_OTHER = "1";

    public FansAdapter(Context context, List<RelationshipBean.Data.Follow> attentionListData) {
        mContext = context;
        this.data = attentionListData;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        FansHolder holder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.account_relationship_fans_item, null);
            holder = new FansHolder();
            holder.headImage = (SimpleDraweeView) view.findViewById(R.id.account_fans_pic_image);
            holder.attentioName = (TextView) view.findViewById(R.id.account_fans_name);
            holder.attentionLevel = (TextView) view.findViewById(R.id.account_fans_level);
            holder.account_attention_image = (AlphaImageView) view.findViewById(R.id.account_fans_image);
            holder.attentioned = (TextView) view.findViewById(R.id.account_fans_followed);
            holder.attention_info = (LinearLayout) view.findViewById(R.id.attention_info);
            view.setTag(holder);
        } else {
            holder = (FansHolder) view.getTag();
        }
        initData(holder, position);
        return view;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    private void goToPersonalInfo(String userid) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = StringUtils.parseInt(userid);
        MsgDispatcher.getInstance().sendMessage(message);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class FansHolder {
        public SimpleDraweeView headImage;  //头像
        public TextView attentioName;  //昵称
        public TextView attentionLevel;    //等级
        public AlphaImageView account_attention_image; //取消关注按钮
        public TextView attentioned;    //已关注
        public LinearLayout attention_info;
    }

    private void initData(FansHolder holder, final int position) {
        //头像
        holder.headImage.setImageURI(Uri.parse(data.get(position).imageUrl));
        holder.headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPersonalInfo(data.get(position).userid);
            }
        });
        holder.attention_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPersonalInfo(data.get(position).userid);
            }
        });
        //昵称
        holder.attentioName.setText(data.get(position).nickname);
        //等级
        if (String.valueOf(CommonConst.SEX_MAN).equals(data.get(position).sex)) {
            holder.attentionLevel.setBackgroundResource(R.drawable.account_level_man);
            holder.attentionLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_man));
        } else {
            holder.attentionLevel.setBackgroundResource(R.drawable.account_level_woman);
            holder.attentionLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_woman));
        }
        holder.attentionLevel.setText(data.get(position).level);
        final RelationshipRequest relationshipRequest = new RelationshipRequest();
        if (ONLY_FANS.equals(data.get(position).ifFollow)) {   //只是粉丝
            holder.account_attention_image.setVisibility(View.VISIBLE);
            holder.attentioned.setVisibility(View.GONE);
            //关注按钮
            holder.account_attention_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relationshipRequest.startCancelOrFollow(data.get(position).userid, Constant.ATTENTIONING);
                    data.get(position).ifFollow = ATTENTION_EACH_OTHER;
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.account_attention_image.setVisibility(View.GONE);
            holder.attentioned.setVisibility(View.VISIBLE);
        }
    }
}
