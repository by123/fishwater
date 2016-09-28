package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.widget.MultImageShowView;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.praise.PraiseRequest;
import com.sjy.ttclub.praise.PraiseUtil;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhangwulin on 2015/12/4.
 * email 1501448275@qq.com
 */
public class CommunityPostView extends LinearLayout {
    View parentView;
    TextView mContentTextView;
    TextView mThemeTextView;
    TextView mCommentCountTextView;
    TextView mUserNameTextView;
    TextView mFocusCountsTextView;
    ImageView mCardTypeIcon;
    TextView mUserLevelTextView;
    SimpleDraweeView mUserIcon;
    TextView mCardTimeTextView;
    MultImageShowView mMultiImageShowView;
    LinearLayout mPraiseLayout;
    ImageView mAttentionButton;
    ImageView mPraiseIcon;
    TextView mPrasieCounts;
    RoundingParams roundingParams;
    private CommunityPostBean mPostCard;
    private PraiseRequest mPraiseRequest;

    public CommunityPostView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        parentView = View.inflate(getContext(), R.layout.community_card_item_layout, null);
        addView(parentView);
        mContentTextView = (TextView) parentView.findViewById(R.id.hot_community_card_content);
        mThemeTextView = (TextView) parentView.findViewById(R.id.hot_community_card_theme);
        mCommentCountTextView = (TextView) parentView.findViewById(R.id.hot_reply_counts);
        mUserNameTextView = (TextView) parentView.findViewById(R.id.community_new_card_user_name);
        mMultiImageShowView = (MultImageShowView) parentView.findViewById(R.id.mult_images);
        mFocusCountsTextView = (TextView) parentView.findViewById(R.id.hot_community_foucs_count);
        mUserLevelTextView = (TextView) parentView.findViewById(R.id.community_new_card_user_level);
        mCardTimeTextView = (TextView) parentView.findViewById(R.id.community_new_card_time);
        mUserIcon = (SimpleDraweeView) parentView.findViewById(R.id.hot_community_circle_icon);
        mCardTypeIcon = (ImageView) parentView.findViewById(R.id.card_flag);
        mPraiseLayout = (LinearLayout) parentView.findViewById(R.id.ll_btn_praise);
        mPraiseIcon = (ImageView) parentView.findViewById(R.id.btn_community_new_card_praise);
        mPrasieCounts = (TextView) parentView.findViewById(R.id.community_new_card_praise_counts);

        mPraiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePraiseLayoutOnClick();
            }
        });
        mUserNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserInfoOnClick();
            }
        });
        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserInfoOnClick();
            }
        });
    }

    public void setCommunityCardView(CommunityPostBean postCard) {
        this.mPostCard = postCard;
        initPraiseRequest();
        setmUserIcon(postCard);
        setmUserNameTextView(postCard);
        setmUserLevelTextView(postCard);
        setmPraiseIcon(postCard);
        setmCardTypeIcon(postCard);
        setmPrasieCounts(postCard);
        setmCardTimeTextView(postCard);
        setmContentTextView(postCard);
        setmThemeTextView(postCard);
        setmCommentCountTextView(postCard);
        setmFocusCountsTextView(postCard);
        setmMultiImageShowView(postCard);
    }

    private void handleUserInfoOnClick() {
        if (mPostCard == null) {
            return;
        }
        //统计community_care_user
        StatsModel.stats(StatsKeyDef.POST_LIST_USER);
        if (mPostCard.getIsAnony() == CommunityConstant.POST_CONTENT_ANNOY) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = mPostCard.getUserId();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void handlePraiseLayoutOnClick() {
        if (mPostCard == null) {
            return;
        }
        if (mPraiseRequest == null) {
            return;
        }
        //统计community_care_praise
        StatsModel.stats(StatsKeyDef.POST_LIST_PRAISE);
        mPraiseIcon.startAnimation(AnimotionDao.getScaleAnimation(false));
        if (PraiseUtil.isPraised(getContext(), mPostCard)) {
            mPostCard.setIsPraise(0);
            mPostCard.setPraiseCount(mPostCard.getPraiseCount() - 1);
            mPraiseRequest.cancelCardPraise(mPostCard.getPostId());
            PraiseUtil.removePraiseState(getContext(), mPostCard.getPostId());
            setmPraiseIcon(mPostCard);
            setmPrasieCounts(mPostCard);
            notifyPostPraiseStateChange();
            return;
        }
        mPostCard.setIsPraise(1);
        mPostCard.setPraiseCount(mPostCard.getPraiseCount() + 1);
        mPraiseRequest.addCardPraise(mPostCard.getPostId());
        PraiseUtil.savePraiseState(getContext(), mPostCard.getPostId());
        setmPraiseIcon(mPostCard);
        setmPrasieCounts(mPostCard);
        notifyPostPraiseStateChange();
    }

    private void notifyPostPraiseStateChange() {
        if (mPostCard == null) {
            return;
        }
        Notification notification = Notification.obtain(NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE, mPostCard);
        NotificationCenter.getInstance().notify(notification);
    }

    private void initPraiseRequest() {
        mPraiseRequest = new PraiseRequest(getContext());
    }

    private void setmContentTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPostTitle())) {
            mContentTextView.setVisibility(View.GONE);
            return;
        }
        if (StringUtils.isEmpty(postCard.getBriefContent())) {
            mContentTextView.setVisibility(View.GONE);
            return;
        }
        mContentTextView.setVisibility(View.VISIBLE);
        EmoticonsUtils.setContent(getContext(), mContentTextView, postCard.getBriefContent().replace("\n", ""));
    }

    private void setmThemeTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPostTitle())) {
            if (StringUtils.isEmpty(postCard.getBriefContent())) {
                mThemeTextView.setVisibility(GONE);
            } else {
                mThemeTextView.setVisibility(VISIBLE);
                mThemeTextView.setText(postCard.getBriefContent());
            }
            return;
        }
        mThemeTextView.setVisibility(View.VISIBLE);
        mThemeTextView.setText(postCard.getPostTitle());
    }

    private void setmCommentCountTextView(CommunityPostBean postCard) {
        if (postCard.getReplyCount() > CommunityConstant.MAX_COUNTS) {
            mCommentCountTextView.setText(CommunityConstant.MAX_SHOW_VALUE);
            return;
        }
        mCommentCountTextView.setText(String.valueOf(postCard.getReplyCount()));
    }

    private void setmUserNameTextView(CommunityPostBean postCard) {
        //发帖角色名
        if (StringUtils.isEmpty(postCard.getNickname())) {
            mUserNameTextView.setVisibility(GONE);
            return;
        }
        mUserNameTextView.setVisibility(VISIBLE);
        mUserNameTextView.setText(postCard.getNickname());
    }

    private void setmFocusCountsTextView(CommunityPostBean postCard) {
        if (postCard.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mFocusCountsTextView.setText(CommunityConstant.MAX_SHOW_VALUE);
            return;
        }
        mFocusCountsTextView.setText(postCard.getReadCount() + "");
    }

    private void setmCardTypeIcon(CommunityPostBean postCard) {
        mCardTypeIcon.setVisibility(View.INVISIBLE);
        if (postCard.getPostTag() == 3) {
            mCardTypeIcon.setVisibility(View.VISIBLE);
            mCardTypeIcon.setImageResource(R.drawable.community_card_jian_bg);
            return;
        }
        if (postCard.getPostTag() == 4) {
            mCardTypeIcon.setVisibility(View.VISIBLE);
            mCardTypeIcon.setImageResource(R.drawable.community_card_jing_bg);
            return;
        }
    }

    private void setmUserLevelTextView(CommunityPostBean postCard) {
        mUserLevelTextView.setTextColor(ResourceHelper.getColor(R.color.community_llgray));
        mUserLevelTextView.setBackgroundResource(R.drawable.community_user_level_bg);
        switch (postCard.getRoleFlag()) {
            case 1:
                mUserLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
            case 2:
                mUserLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
            case 3:
                mUserLevelTextView.setText("小编");
                mUserLevelTextView.setTextColor(ResourceHelper.getColor(R.color.white));
                mUserLevelTextView.setBackgroundResource(R.drawable.community_editer_level_bg);
                break;
            case 4:
                mUserLevelTextView.setText("专家");
                mUserLevelTextView.setBackgroundResource(R.drawable.community_editer_level_bg);
                mUserLevelTextView.setTextColor(ResourceHelper.getColor(R.color.white));
                break;
            default:
                mUserLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
        }
    }

    private void setmUserIcon(CommunityPostBean postCard) {
        roundingParams = mUserIcon.getHierarchy().getRoundingParams();
        if (postCard.getUserSex() == CommonConst.SEX_MAN) {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_man), ResourceHelper.getDimen(R.dimen.space_1));
        } else {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_woman), ResourceHelper.getDimen(R.dimen.space_1));
        }
        mUserIcon.getHierarchy().setRoundingParams(roundingParams);
        if (StringUtils.isEmpty(postCard.getHeadimageUrl())) {
            mUserIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
            return;
        }
        mUserIcon.setImageURI(Uri.parse(postCard.getHeadimageUrl()));
    }

    private void setmCardTimeTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPublishTime())) {
            mCardTimeTextView.setVisibility(GONE);
            return;
        }
        mCardTimeTextView.setVisibility(VISIBLE);
        mCardTimeTextView.setText(postCard.getPublishTime());
    }

    private void setmMultiImageShowView(CommunityPostBean postCard) {
        if (postCard.getImages() == null) {
            mMultiImageShowView.setVisibility(GONE);
            return;
        }
        mMultiImageShowView.setVisibility(VISIBLE);
        mMultiImageShowView.setDatas(postCard);
        mMultiImageShowView.setType(postCard.getImages().size());
    }

    private void setmPraiseIcon(CommunityPostBean postCard) {
        if (PraiseUtil.isPraised(getContext(), postCard)) {
            mPraiseIcon.setImageResource(R.drawable.community_btn_praise_select);
            mPrasieCounts.setTextColor(ResourceHelper.getColor(R.color.community_praise_text_color));
        } else {
            mPraiseIcon.setImageResource(R.drawable.community_btn_praise);
            mPrasieCounts.setTextColor(ResourceHelper.getColor(R.color.llgray));
        }
    }

    private void setmPrasieCounts(CommunityPostBean postCard) {
        mPrasieCounts.setVisibility(VISIBLE);
        if (postCard.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mPrasieCounts.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            if (postCard.getPraiseCount() <= 0) {
                mPrasieCounts.setVisibility(GONE);
            } else {
                mPrasieCounts.setText(String.valueOf(postCard.getPraiseCount()));
            }
        }
    }
}
