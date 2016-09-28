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
 * Created by zhangwulin on 2015/12/3.
 * email 1501448275@qq.com
 */
public class CommunityQaView extends LinearLayout {

    private TextView mQuestionThemeTextView;
    private TextView mQuestionContentTextView;
    private TextView mQuestionerLevelTextView;
    private TextView mQuestionerNameTextView;
    private TextView mQuestionerPostTimeTextView;
    private ImageView mQAFlag;
    private TextView mQuestionCounts;
    private SimpleDraweeView mQuestionerHeadIcon;
    private SimpleDraweeView mAnswererHeadIcon;
    private LinearLayout mQuestionLayout;
    private LinearLayout mAnswerLinelayout;
    private ImageView mQuestionImageView;
    private TextView mFoucsCount;
    private TextView mReplyCount;
    private TextView mAnswerTextView;
    private RoundingParams roundingParams;
    private CommunityPostBean mPostCard;
    private PraiseRequest mPraiseRequest;

    public CommunityQaView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.community_item_qa_card, null);
        addView(parentView);
        mQuestionerHeadIcon = (SimpleDraweeView) parentView.findViewById(R.id.mQaQuestionerHeadIcon);
        mQuestionContentTextView = (TextView) parentView.findViewById(R.id.textQuestionContent);
        mQuestionerNameTextView = (TextView) parentView.findViewById(R.id.mQaQuestionerName);
        mQuestionerLevelTextView = (TextView) parentView.findViewById(R.id.mQaQuestionerLever);
        mQuestionerPostTimeTextView = (TextView) parentView.findViewById(R.id.mQACreateTime);
        mQuestionThemeTextView = (TextView) parentView.findViewById(R.id.textQuestionTheme);
        mAnswererHeadIcon = (SimpleDraweeView) parentView.findViewById(R.id.answererHeadIcon);
        mAnswerTextView = (TextView) parentView.findViewById(R.id.textAnswerContent);
        mQAFlag = (ImageView) parentView.findViewById(R.id.qa_flag);
        mQuestionLayout = (LinearLayout) parentView.findViewById(R.id.ll_btn_question);
        mQuestionCounts = (TextView) parentView.findViewById(R.id.btn_qa_question_counts);
        mQuestionImageView = (ImageView) parentView.findViewById(R.id.btn_qa_question);
        mFoucsCount = (TextView) parentView.findViewById(R.id.qa_community_foucs_count);
        mReplyCount = (TextView) parentView.findViewById(R.id.qa_reply_counts);
        mAnswerLinelayout = (LinearLayout) parentView.findViewById(R.id.answerLinelayout);

        mQuestionerHeadIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserInfoLayoutOnClick();
            }
        });
        mQuestionerNameTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserInfoLayoutOnClick();
            }
        });
        mQuestionLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleQuestionLayoutOnClick();
            }
        });
    }

    public void setCommunityQaView(CommunityPostBean postCard) {
        this.mPostCard = postCard;
        initPraiseRequest();
        setmQuestionerHeadIcon(postCard);
        setmQuestionerNameTextView(postCard);
//        setmQuestionThemeTextView(postCard);
        setmQuestionContentTextView(postCard);
        setmQuestionImageView(postCard);
        setmReplyCount(postCard);
        setmFoucsCount(postCard);
        setmAnswerLinelayout(postCard);
        setmQAFlag(postCard);
        setmQuestionerLevelTextView(postCard);
        setmQuestionerPostTimeTextView(postCard);
        setmAnswererHeadIcon(postCard);
        setmQuestionCounts(postCard);
    }


    private void handleUserInfoLayoutOnClick() {
        if (mPostCard == null) {
            return;
        }
        if (mPostCard.getIsAnony() == CommunityConstant.POST_CONTENT_ANNOY) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = mPostCard.getUserId();
        MsgDispatcher.getInstance().sendMessage(message);
        //统计post_list_user
        StatsModel.stats(StatsKeyDef.POST_LIST_USER);
    }

    private void handleQuestionLayoutOnClick() {
        if (mPostCard == null) {
            return;
        }
        StatsModel.stats(StatsKeyDef.POST_LIST_PRAISE);
        mQuestionImageView.startAnimation(AnimotionDao.getScaleAnimation(false));
        if (PraiseUtil.isPraised(getContext(), mPostCard)) {
            mPostCard.setIsPraise(0);
            mPostCard.setPraiseCount(mPostCard.getPraiseCount() - 1);
            mPraiseRequest.cancelCardPraise(mPostCard.getPostId());
            PraiseUtil.removePraiseState(getContext(), mPostCard.getPostId());
            setmQuestionCounts(mPostCard);
            setmQuestionImageView(mPostCard);
            notifyPostPraiseStateChange();
            return;
        }
        mPostCard.setIsPraise(1);
        mPostCard.setPraiseCount(mPostCard.getPraiseCount() + 1);
        mPraiseRequest.addCardPraise(mPostCard.getPostId());
        PraiseUtil.savePraiseState(getContext(), mPostCard.getPostId());
        setmQuestionCounts(mPostCard);
        setmQuestionImageView(mPostCard);
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

    private void setmQuestionThemeTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPostTitle())) {
            if (StringUtils.isEmpty(postCard.getBriefContent())) {
                mQuestionThemeTextView.setVisibility(View.GONE);
            } else {
                mQuestionThemeTextView.setVisibility(VISIBLE);
                mQuestionThemeTextView.setText(postCard.getBriefContent());
            }

            return;
        }
        mQuestionThemeTextView.setVisibility(View.VISIBLE);
        mQuestionThemeTextView.setText(postCard.getPostTitle().replace("\n", ""));
    }

    private void setmQuestionContentTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPostTitle())) {
            if (StringUtils.isEmpty(postCard.getBriefContent())) {
                mQuestionContentTextView.setVisibility(View.GONE);
            } else {
                mQuestionContentTextView.setVisibility(VISIBLE);
                EmoticonsUtils.setContent(getContext(), mQuestionContentTextView, postCard.getBriefContent().replace("\n", ""));
            }
            return;
        }
        mQuestionContentTextView.setVisibility(View.VISIBLE);
        mQuestionContentTextView.setText(postCard.getPostTitle());
    }


    private void setmQuestionerLevelTextView(CommunityPostBean postCard) {

        mQuestionerLevelTextView.setTextColor(ResourceHelper.getColor(R.color.community_llgray));
        mQuestionerLevelTextView.setBackgroundResource(R.drawable.community_user_level_bg);
        switch (postCard.getRoleFlag()) {
            case 1:
                mQuestionerLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
            case 2:
                mQuestionerLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
            case 3:
                mQuestionerLevelTextView.setText("小编");
                mQuestionerLevelTextView.setTextColor(ResourceHelper.getColor(R.color.white));
                mQuestionerLevelTextView.setBackgroundResource(R.drawable.community_editer_level_bg);
                break;
            case 4:
                mQuestionerLevelTextView.setText("专家");
                mQuestionerLevelTextView.setBackgroundResource(R.drawable.community_editer_level_bg);
                mQuestionerLevelTextView.setTextColor(ResourceHelper.getColor(R.color.white));
                break;
            default:
                mQuestionerLevelTextView.setText("LV" + postCard.getUserLevel());
                break;
        }
    }

    private void setmQuestionerNameTextView(CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getNickname())) {
            mQuestionerNameTextView.setVisibility(GONE);
            return;
        }
        mQuestionerNameTextView.setVisibility(VISIBLE);
        mQuestionerNameTextView.setText(postCard.getNickname());
    }

    private void setmQuestionerPostTimeTextView(final CommunityPostBean postCard) {
        if (StringUtils.isEmpty(postCard.getPublishTime())) {
            mQuestionerPostTimeTextView.setVisibility(GONE);
            return;
        }
        mQuestionerPostTimeTextView.setVisibility(VISIBLE);
        mQuestionerPostTimeTextView.setText(postCard.getPublishTime());
    }

    private void setmQAFlag(CommunityPostBean postCard) {
        switch (postCard.getPostTag()) {
            case 3:
                mQAFlag.setVisibility(View.VISIBLE);
                mQAFlag.setImageResource(R.drawable.community_card_jian_bg);
                break;
            case 4:
                mQAFlag.setVisibility(View.VISIBLE);
                mQAFlag.setImageResource(R.drawable.community_card_jing_bg);
                break;
            default:
                mQAFlag.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setmQuestionCounts(CommunityPostBean postCard) {
        mQuestionCounts.setVisibility(VISIBLE);
        if (postCard.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mQuestionCounts.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            if (postCard.getPraiseCount() == 0) {
                mQuestionCounts.setVisibility(GONE);
            } else {
                mQuestionCounts.setText(String.valueOf(postCard.getPraiseCount()));
            }
        }
    }

    private void setmQuestionerHeadIcon(CommunityPostBean postCard) {
        roundingParams = mQuestionerHeadIcon.getHierarchy().getRoundingParams();
        if (postCard.getUserSex() == CommonConst.SEX_MAN) {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_man), ResourceHelper.getDimen(R.dimen.space_1));
        } else {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_woman), ResourceHelper.getDimen(R.dimen.space_1));
        }
        mQuestionerHeadIcon.getHierarchy().setRoundingParams(roundingParams);
        if (StringUtils.isEmpty(postCard.getHeadimageUrl())) {
            mQuestionerHeadIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
            return;
        }
        mQuestionerHeadIcon.setImageURI(Uri.parse(postCard.getHeadimageUrl()));
    }

    private void setmAnswererHeadIcon(CommunityPostBean postCard) {
        if (postCard.getChoiceReply() == null) {
            mAnswererHeadIcon.setVisibility(View.GONE);
            return;
        }
        roundingParams = mAnswererHeadIcon.getHierarchy().getRoundingParams();
        if (postCard.getChoiceReply().getUserSex() == CommonConst.SEX_MAN) {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_man), ResourceHelper.getDimen(R.dimen.space_1));
        } else {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_woman), ResourceHelper.getDimen(R.dimen.space_1));
        }
        mAnswererHeadIcon.getHierarchy().setRoundingParams(roundingParams);
        if (StringUtils.isEmpty(postCard.getChoiceReply().getHeadimageUrl())) {
            mAnswererHeadIcon.setImageURI(Uri.parse("res://drawable-xxhdpi/" + R.drawable.community_default_head_icon));
            return;
        }
        mAnswererHeadIcon.setImageURI(Uri.parse(postCard.getChoiceReply().getHeadimageUrl()));
    }

    private void setmAnswerLinelayout(CommunityPostBean postCard) {
        if (postCard.getChoiceReply() == null) {
            mAnswerLinelayout.setVisibility(View.GONE);
            return;
        }
        if (postCard.getChoiceReply().getAnswer() == null) {
            mAnswerLinelayout.setVisibility(View.GONE);
            return;
        }
        mAnswerLinelayout.setVisibility(View.VISIBLE);
        mAnswerTextView.setVisibility(View.VISIBLE);
        mAnswerTextView.setText(postCard.getChoiceReply().getAnswer());
        EmoticonsUtils.setContent(getContext(), mAnswerTextView, postCard.getChoiceReply().getAnswer());

    }

    private void setmQuestionImageView(CommunityPostBean postCard) {
        if (PraiseUtil.isPraised(getContext(), postCard)) {
            mQuestionImageView.setImageResource(R.drawable.community_question_ask_after);
            mQuestionCounts.setTextColor(ResourceHelper.getColor(R.color.community_praise_text_color));
        } else {
            mQuestionImageView.setImageResource(R.drawable.community_question_ask_before);
            mQuestionCounts.setTextColor(ResourceHelper.getColor(R.color.llgray));
        }
    }

    private void setmFoucsCount(CommunityPostBean postCard) {
        if (postCard.getReadCount() > CommunityConstant.MAX_COUNTS) {
            mFoucsCount.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mFoucsCount.setText(String.valueOf(postCard.getReadCount()));
        }
    }

    private void setmReplyCount(CommunityPostBean postCard) {
        if (postCard.getReplyCount() > CommunityConstant.MAX_COUNTS) {
            mReplyCount.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mReplyCount.setText(String.valueOf(postCard.getReplyCount()));
        }
    }

    private void setmQaMultImageShowView(CommunityPostBean postCard) {
       /* if (postCard.getImages() == null) {
            mQaMultImageShowView.setVisibility(GONE);
            return;
        }
        mQaMultImageShowView.setVisibility(VISIBLE);
        mQaMultImageShowView.setDatas(postCard);
        mQaMultImageShowView.setType(postCard.getImages().size());*/
    }
}
