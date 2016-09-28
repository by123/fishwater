package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

/**
 * Created by zhangwulin on 2015/12/24.
 * email 1501448275@qq.com
 */
public class CommunityQaDetailContentView extends LinearLayout {
    private SimpleDraweeView mUserIcon;
    private TextView mUserName;
    private TextView mPostTime;
    private TextView mUserLevel;
    private TextView mCardTitle;
    private TextView mCardContent;
    private TextView mFloorHostTextView;
    private Context mContext;
    private CommunityPostBean mPost;
    private RoundingParams roundingParams;

    public CommunityQaDetailContentView(Context context) {
        this(context, null);
    }

    public CommunityQaDetailContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityQaDetailContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View parentView = View.inflate(mContext, R.layout.community_qa_post_detail_content_view, null);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentView, lp);
        mUserIcon = (SimpleDraweeView) parentView.findViewById(R.id.user_icon);
        mUserName = (TextView) parentView.findViewById(R.id.user_name);
        mCardTitle = (TextView) parentView.findViewById(R.id.qa_detail_theme);
        mCardContent = (TextView) parentView.findViewById(R.id.qa_detail_content);
        mFloorHostTextView = (TextView) parentView.findViewById(R.id.floor_host);
        mUserLevel = (TextView) parentView.findViewById(R.id.user_level);
        mPostTime = (TextView) parentView.findViewById(R.id.post_time);
        mUserIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserIconOnClick();
            }
        });
    }

    public void setQaDetailContentView(CommunityPostBean post) {
        this.mPost = post;
        setmUserIcon(post);
        setmUserName(post);
        setmCardContent(post);
        setmCardTitle(post);
        setmPostTime(post);
        setmFloorHostTextView(post);
        setmUserLevel(post);
    }

    private void handleUserIconOnClick() {
        if (mPost == null) {
            return;
        }
        if (mPost.getIsAnony() == CommunityConstant.POST_CONTENT_ANNOY) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = mPost.getUserId();
        MsgDispatcher.getInstance().sendMessage(message);
        //统计post_user_master
        StatsModel.stats(StatsKeyDef.POST_USER_MASTER);
    }


    private void setmUserIcon(CommunityPostBean post) {
        roundingParams = mUserIcon.getHierarchy().getRoundingParams();
        if (post.getUserSex() == CommonConst.SEX_MAN) {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_man), getContext().getResources().getDimension(R.dimen.space_1));
        } else {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_woman), getContext().getResources().getDimension(R.dimen.space_1));
        }
        mUserIcon.getHierarchy().setRoundingParams(roundingParams);
        if (StringUtils.isEmpty(post.getHeadimageUrl())) {
            mUserIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
            return;
        }
        mUserIcon.setImageURI(Uri.parse(post.getHeadimageUrl()));
    }

    private void setmUserName(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getNickname())) {
            return;
        }
        mUserName.setText(post.getNickname());
    }

    private void setmPostTime(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getPublishTime())) {
            mPostTime.setText(post.getPublishTime());
            return;
        }
        mPostTime.setText(TimeUtil.getCDTime(post.getPublishTime()));
    }

    private void setmUserLevel(CommunityPostBean post) {
        mUserLevel.setTextColor(ResourceHelper.getColor(R.color.llgray));
        mUserLevel.setBackgroundResource(R.drawable.community_user_level_bg);
        switch (post.getRoleFlag()) {
            case 1:
                mUserLevel.setText("LV" + post.getUserLevel());
                break;
            case 2:
                mUserLevel.setText("LV" + post.getUserLevel());
                break;
            case 3:
                mUserLevel.setText("小编");
                mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
                mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
                break;
            case 4:
                mUserLevel.setText("专家");
                mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
                mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
                break;
            default:
                mUserLevel.setText("LV" + post.getUserLevel());
                break;
        }
    }

    private void setmCardTitle(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getPostTitle())) {
            mCardTitle.setVisibility(GONE);
            return;
        }
        mCardTitle.setVisibility(VISIBLE);
        mCardTitle.setText(post.getPostTitle());
    }

    private void setmCardContent(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getContent())) {
            mCardContent.setVisibility(GONE);
            return;
        }
        mCardContent.setVisibility(VISIBLE);
        EmoticonsUtils.setContent(getContext(), mCardContent, post.getContent());
    }

    private void setmFloorHostTextView(CommunityPostBean postCard) {
        mFloorHostTextView.setText(ResourceHelper.getString(R.string.community_qa_host));
    }

}
