package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.MyCommentBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;

/**
 * Created by Administrator on 2015/12/2.
 */
public class CommunityMyCommentView extends LinearLayout {
    private TextView mUserName;
    private TextView mContent;
    private TextView mFloor;
    private TextView mTime;
    private ImageView mPraiseIcon;
    private SimpleDraweeView mUserIcon;
    private TextView mPraiseCount;
    private TextView mFloorHost;
    private TextView mCardFrom;
    private TextView mCardTitle;
    private SimpleDraweeView mCardIcon;

    private MyCommentBean mComment;

    public CommunityMyCommentView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.community_my_comment_view_layout, null);
        addView(parentView);

        mUserIcon = (SimpleDraweeView) parentView.findViewById(R.id.user_icon);
        mUserName = (TextView) parentView.findViewById(R.id.user_name);
        mContent = (TextView) parentView.findViewById(R.id.evolution_content);
        mTime = (TextView) parentView.findViewById(R.id.evolution_time);
        mPraiseCount = (TextView) parentView.findViewById(R.id.evolution_praise_count);
        mFloor = (TextView) parentView.findViewById(R.id.floor);
        mFloorHost = (TextView) parentView.findViewById(R.id.floor_host);
        mCardFrom = (TextView) parentView.findViewById(R.id.card_from);
        mCardIcon = (SimpleDraweeView) parentView.findViewById(R.id.card_type_icon);
        mCardTitle = (TextView) parentView.findViewById(R.id.card_title);

        mUserIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserIconOnClick();
            }
        });
    }

    public void setMyCommentView(MyCommentBean comment) {
        this.mComment = comment;
        setUserIcon(comment);
        setUserName(comment);
        setmCardIcon(comment);
        setmCardTitle(comment);
        setContent(comment);
        setPraiseCount(comment);
        setTime(comment);
        setFloor(comment);
    }

    private void handleUserIconOnClick() {
        if (mComment == null) {
            return;
        }
    }

    private void setUserName(MyCommentBean comment) {
        mUserName.setText(comment.getNickname());
    }

    private void setContent(MyCommentBean comment) {
        EmoticonsUtils.setContent(getContext(), mContent, comment.getReplyContent());
    }


    private void setFloor(MyCommentBean comment) {
    }

    private void setTime(MyCommentBean comment) {
        mTime.setText(TimeUtil.getCDTime(comment.getCreatetime()));
    }

    private void setPraiseIcon(MyCommentBean comment) {
        mPraiseIcon.setVisibility(GONE);
    }

    private void setUserIcon(MyCommentBean comment) {
        if (!StringUtils.isEmpty(comment.getHeadimageUrl())) {
            mUserIcon.setImageURI(Uri.parse(comment.getHeadimageUrl()));
        } else {
            mUserIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
        }
    }

    private void setPraiseCount(MyCommentBean comment) {
        mPraiseCount.setVisibility(View.GONE);

    }

    private void setmCardTitle(MyCommentBean comment) {
        mCardTitle.setText(comment.getPostTitle());
    }

    private void setmCardIcon(MyCommentBean comment) {
        if(StringUtils.isEmpty(comment.getImageUrl())){
            if (comment.getType() == CommunityConstant.CIRCLE_TYPE_POST) {
                mCardIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.account_message_reply_me_post));
            } else if (comment.getType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
                mCardIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.account_message_reply_me_ask));
            }else {
                mCardIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.data_error));
            }
            return;
        }
        mCardIcon.setImageURI(Uri.parse(comment.getImageUrl()));
    }
}
