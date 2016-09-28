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

/**
 * Created by zhangwulin on 2015/12/17.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailContentView extends LinearLayout {
    private SimpleDraweeView mPostHeadIcon;
    private SimpleDraweeView mUserIcon;
    private TextView mUserName;
    private TextView mPostTitle;
    private TextView mPostContent;
    private Context mContext;
    private CommunityPostBean mPost;
    private RoundingParams roundingParams;

    public CommunityPostDetailContentView(Context context) {
        this(context, null);
    }

    public CommunityPostDetailContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityPostDetailContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View parentView = View.inflate(mContext, R.layout.community_post_detail_item_content_view, null);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentView, lp);
        mPostHeadIcon = (SimpleDraweeView) parentView.findViewById(R.id.post_head_icon);
        mUserIcon = (SimpleDraweeView) parentView.findViewById(R.id.post_user_icon);
        mUserName = (TextView) parentView.findViewById(R.id.post_user_name);
        mPostTitle = (TextView) parentView.findViewById(R.id.post_detail_theme);
        mPostContent = (TextView) parentView.findViewById(R.id.post_detail_content);
        mUserIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserIconOnClick();
            }
        });
    }

    public void setPostDetailContentView(CommunityPostBean post, int postType) {
        this.mPost = post;
        setmPostHeadIcon(post);
        setmUserIcon(post);
        setmUserName(post);
        setmPostContent(post);
        setmPostTitle(post);
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


    private void setmPostHeadIcon(CommunityPostBean post) {
        mPostHeadIcon.setAspectRatio(2.0f);
        if (StringUtils.isEmpty(post.getPageHeaderUrl())) {
            mPostHeadIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_post_default_head_image));
            return;
        }
        mPostHeadIcon.setImageURI(Uri.parse(post.getPageHeaderUrl()));
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

    private void setmPostTitle(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getPostTitle())) {
            mPostTitle.setVisibility(GONE);
            return;
        }
        mPostTitle.setVisibility(VISIBLE);
        mPostTitle.setText(post.getPostTitle());
    }

    private void setmPostContent(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getContent())) {
            mPostContent.setVisibility(GONE);
            return;
        }
        mPostContent.setVisibility(VISIBLE);
        EmoticonsUtils.setContent(getContext(), mPostContent, post.getContent());
    }
}
