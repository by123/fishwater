package com.sjy.ttclub.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityTempDataHelper;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.DeviceManager;
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
import com.sjy.ttclub.util.TimeUtil;
import com.sjy.ttclub.widget.ExpandTextView;

/**
 * Created by Administrator on 2015/12/1.
 */
public class CommentView extends LinearLayout {

    private TextView mUserName;
    private TextView mUserLevel;
    private TextView mContent;
    private ExpandTextView mPreviousContent;
    private TextView mFloorAndTime;
    private ImageView mCommentPraise;
    private ImageView mCommentManIcon;
    private ImageView mCommentWoManIcon;
    private SimpleDraweeView mUserIcon;
    private TextView mCommentPraiseCount;
    private TextView mFloorHost;
    private LinearLayout mCommentLineLayout;
    private LinearLayout mPraiseLayout;
    private CommentBean mComment;
    private int mCommentType = CommunityConstant.COMMENTS_TYPE_POST;
    private boolean mIsClickable = true;
    private boolean mIsCommentDetail = false;
    private PraiseRequest mPraiseRequest;
    private OnCommentLayoutClickListener commentLayoutClickListener;
    private boolean mIsLimitContentLine = true;
    private CommunityPostBean mPost;

    public CommentView(Context context) {
        this(context, null);

    }

    public CommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initPraiseRequest();
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.comment_view_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentView, lp);

        mPreviousContent = (ExpandTextView) parentView.findViewById(R.id.previous_content);
        mUserIcon = (SimpleDraweeView) parentView.findViewById(R.id.user_icon);
        mCommentManIcon = (ImageView) parentView.findViewById(R.id.comment_man_icon);
        mCommentWoManIcon = (ImageView) parentView.findViewById(R.id.comment_woman_icon);
        mUserName = (TextView) parentView.findViewById(R.id.user_name);
        mUserLevel = (TextView) parentView.findViewById(R.id.level);
        mContent = (TextView) parentView.findViewById(R.id.evolution_content);
        mCommentPraise = (ImageView) parentView.findViewById(R.id.evolution_praise);
        mCommentPraiseCount = (TextView) parentView.findViewById(R.id.evolution_praise_count);
        mFloorAndTime = (TextView) parentView.findViewById(R.id.floor);
        mFloorHost = (TextView) parentView.findViewById(R.id.floor_host);
        mCommentLineLayout = (LinearLayout) parentView.findViewById(R.id.comment_content_ll_layout);
        mPraiseLayout = (LinearLayout) parentView.findViewById(R.id.comment_praise_layout);
        mCommentLineLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handeCommentLineLayoutOnClick();
            }
        });
        mUserIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUserIconOnClick();
            }
        });
        mPraiseLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePraiseLayoutOnClick();
            }
        });
        mPreviousContent.setOnTextExpandListener(new ExpandTextView.OnTextExpandListener() {
            @Override
            public void onTextExpand(int state) {
                mComment.setExpandState(state);
            }
        });
        mPreviousContent.setOnTextExpandClickListener(new ExpandTextView.OnTextExpandClickListener() {
            @Override
            public void onTextExpandClick() {
                handeCommentLineLayoutOnClick();
            }
        });
    }

    private void initPraiseRequest() {
        mPraiseRequest = new PraiseRequest(getContext());
    }

    public void setCommentClickable(boolean isClickable) {
        this.mIsClickable = isClickable;
        mCommentLineLayout.setClickable(isClickable);
        mUserIcon.setClickable(isClickable);
        mPraiseLayout.setClickable(isClickable);
    }

    public void disableCommentBg() {
        mCommentLineLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setmIsCommentDetail(boolean isCommentDetail) {
        this.mIsCommentDetail = isCommentDetail;
    }

    public void setCmtType(int cmtType) {
        this.mCommentType = cmtType;
    }

    public void setCommentView(CommunityPostBean postBean, CommentBean comment, int cmtType) {
        this.mComment = comment;
        this.mCommentType = cmtType;
        this.mPost = postBean;
        setFloorHost(postBean, comment);
        setCommentViewFloorHostView(comment);
    }

    public void setCommentView(CommentBean comment, int cmtType) {
        this.mComment = comment;
        this.mCommentType = cmtType;
        mFloorHost.setVisibility(GONE);
        setCommentViewFloorHostView(comment);
    }

    public void setCommentView(CommentBean comment) {
        this.mComment = comment;
        setmFloorHost(comment);
        setCommentViewFloorHostView(comment);
    }

    private void setCommentViewFloorHostView(CommentBean comment) {
        setUserIcon(comment);
        setUserSex(comment);
        setUserName(comment);
        setFloor(comment);
        setLevel(comment);
        setContent(comment);
        setPraise(comment);
        setmCommentPraiseCount(comment);
        setPreviousContent(comment);
    }

    public void setEnableDivider(boolean enable) {
//        mDivideView.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void handeCommentLineLayoutOnClick() {
        if (mComment == null) {
            return;
        }
        if (commentLayoutClickListener != null) {
            commentLayoutClickListener.onLayoutClick(mComment);
        }
        if (mIsCommentDetail) {
            return;
        }
        if (!mIsClickable) {
            return;
        }
       /* if (mPost != null) {
            CommunityCircleBean circleBean = new CommunityCircleBean();
            circleBean.setIsLimitMale(mPost.getIsLimitMale());
            CommunityTempDataHelper.getInstance().setmTempCircle(circleBean);
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_COMMENT_DETAIL_WINDOW;
        message.arg1 = mComment.getCommentId();
        message.arg2 = mCommentType;
        MsgDispatcher.getInstance().sendMessage(message);*/
    }

    private void handleUserIconOnClick() {
        if (mComment == null) {
            return;
        }
        //统计post_user_reply
        StatsModel.stats(StatsKeyDef.POST_USER_REPLY);

        if (mComment.getIsAnony() == CommunityConstant.POST_CONTENT_ANNOY) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_USER_INFO_WINDOW;
        message.arg1 = mComment.getUserId();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void handlePraiseLayoutOnClick() {
        if (mComment == null) {
            return;
        }
        mCommentPraise.startAnimation(AnimotionDao.getScaleAnimation(false));
        if (PraiseUtil.isSavePraiseStateInLocal(getContext(), mComment.getCommentId())) {
            handleCancelPraise();
        } else {
            handleAddPraise();
        }
        //统计post_user_praise
        StatsModel.stats(StatsKeyDef.POST_USER_PRAISE);
    }

    private void handleCancelPraise() {
        if (mCommentType == CommunityConstant.COMMENTS_TYPE_ARTICLE) {
            mPraiseRequest.cancelArticleCommentPraise(mComment.getCommentId());
        } else {
            mPraiseRequest.cancelCardCommentPraise(mComment.getCommentId());
        }
        PraiseUtil.removePraiseState(getContext(), mComment.getCommentId());
        mComment.setPraiseCount(mComment.getPraiseCount() - 1);
        mComment.setPrasie(false);
        mCommentPraiseCount.setText(mComment.getPraiseCount() + "");
        mCommentPraise.setImageResource(R.drawable.community_btn_praise);
        mCommentPraiseCount.setTextColor(ResourceHelper.getColor(R.color.community_llgray));
        notifyPraiseStateChange();
    }

    private void handleAddPraise() {
        if (mCommentType == CommunityConstant.COMMENTS_TYPE_ARTICLE) {
            mPraiseRequest.addArticleCommentPraise(mComment.getCommentId());
        } else {
            mPraiseRequest.addCardCommentPraise(mComment.getCommentId());
        }
        PraiseUtil.savePraiseState(getContext(), mComment.getCommentId());
        mComment.setPraiseCount(mComment.getPraiseCount() + 1);
        mComment.setPrasie(true);
        mCommentPraise.setImageResource(R.drawable.community_btn_praise_select);
        mCommentPraiseCount.setTextColor(ResourceHelper.getColor(R.color.community_praise_text_color));
        if (mComment.getPraiseCount() >= CommunityConstant.MAX_COUNTS) {
            mCommentPraiseCount.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mCommentPraiseCount.setText(mComment.getPraiseCount() + "");
        }
        notifyPraiseStateChange();
    }

    private void notifyPraiseStateChange() {
        if (mComment == null) {
            return;
        }
        Notification notification = Notification.obtain(NotificationDef.N_COMMUNITY_UPDATE_COMMENT_PRAISE, mComment);
        NotificationCenter.getInstance().notify(notification);
    }

    private void handleReplyContentOnClick() {
        if (mComment == null || !mIsClickable) {
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_COMMENT_DETAIL_WINDOW;
        message.arg1 = mComment.getCommentId();
        message.arg2 = mCommentType;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void setUserName(CommentBean comment) {
        if (StringUtils.isEmpty(comment.getNickname())) {
            return;
        }
        mUserName.setText(comment.getNickname().replace("\n", ""));
    }

    private void setLevel(CommentBean comment) {
        mUserLevel.setTextColor(ResourceHelper.getColor(R.color.llgray));
        mUserLevel.setBackgroundResource(R.drawable.community_user_level_bg);
        switch (comment.getRoleFlag()) {
            case 1:
                setBrainTrustTag(comment);
                break;
            case 2:
                mUserLevel.setText("LV" + comment.getUserLevel());
                break;
            case 3:
                mUserLevel.setText(ResourceHelper.getString(R.string.community_user_role_editor));
                mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
                mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
                break;
            case 4:
                mUserLevel.setText(ResourceHelper.getString(R.string.community_user_role_expert));
                mUserLevel.setBackgroundResource(R.drawable.community_editer_level_bg);
                mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
                break;
            default:
                mUserLevel.setText("LV" + comment.getUserLevel());
                break;
        }
    }

    private void setBrainTrustTag(CommentBean comment) {
        if (comment.getIsHost() == CommunityConstant.NOT_HOST && StringUtils.isNotEmpty(comment.getUserTagName())) {
            mUserLevel.setText(ResourceHelper.getString(R.string.community_user_role_brain_trust));
            mUserLevel.setBackgroundResource(R.drawable.community_brain_trust_level_bg);
            mUserLevel.setTextColor(ResourceHelper.getColor(R.color.white));
            return;
        }
        mUserLevel.setText("LV" + comment.getUserLevel());
    }

    private void setUserSex(CommentBean comment) {
        if (comment.getUserSex() == CommonConst.SEX_WOMAN) {
            mCommentManIcon.setVisibility(INVISIBLE);
            mCommentWoManIcon.setVisibility(VISIBLE);
            return;
        }
        mCommentWoManIcon.setVisibility(INVISIBLE);
        mCommentManIcon.setVisibility(VISIBLE);
    }

    private void setPreviousContent(CommentBean comment) {
        if (comment.getReferencedComment() == null) {
            mPreviousContent.setVisibility(GONE);
            return;
        }
        if (StringUtils.isEmpty(comment.getReferencedComment().getContent())) {
            mPreviousContent.setVisibility(GONE);
            return;
        }
        if (StringUtils.isEmpty(comment.getReferencedComment().getNickname())) {
            mPreviousContent.setVisibility(GONE);
            return;
        }
        mPreviousContent.setVisibility(VISIBLE);
        mPreviousContent.setText(
                comment.getExpandState(),
                comment.getReferencedComment().getNickname(),
                comment.getReferencedComment().getContent()
        );
    }

    private void setContent(CommentBean comment) {
        mContent.setVisibility(VISIBLE);
        if (comment.getReplys().size() == 0) {
            EmoticonsUtils.setContent(getContext(), mContent, comment.getContent());
            return;
        }
        EmoticonsUtils.setContent(getContext(), mContent, comment.getReplys().get(0).getContent());
    }

    private void setFloor(CommentBean comment) {
        mFloorAndTime.setText(TimeUtil.getCDTime(comment.getCreateTime()));
    }

    private void setPraise(CommentBean comment) {
        if (PraiseUtil.isSavePraiseStateInLocal(getContext(), comment.getCommentId())) {
            mCommentPraise.setImageDrawable(ResourceHelper.getDrawable(R.drawable
                    .community_btn_praise_select));
            mCommentPraiseCount.setTextColor(ResourceHelper.getColor(R.color
                    .community_praise_text_color));
            return;
        }
        mCommentPraise.setImageDrawable(ResourceHelper.getDrawable(R.drawable
                .community_btn_praise));
        mCommentPraiseCount.setTextColor(ResourceHelper.getColor(R.color.community_llgray));
    }

    private void setUserIcon(CommentBean comment) {
        if (StringUtils.isEmpty(comment.getHeadimageUrl())) {
            mUserIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
            return;
        }
        mUserIcon.setImageURI(Uri.parse(comment.getHeadimageUrl()));
    }

    private void setmCommentPraiseCount(CommentBean comment) {
        if (comment.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mCommentPraiseCount.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mCommentPraiseCount.setText(comment.getPraiseCount() + "");
        }
    }

    private void setFloorHost(CommunityPostBean postBean, CommentBean comment) {
        if (comment.getIsHost() == CommunityConstant.IS_HOST) {
            if (postBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
                mFloorHost.setText(ResourceHelper.getString(R.string.community_qa_host));
            } else {
                mFloorHost.setText(ResourceHelper.getString(R.string.community_host));
            }
            mFloorHost.setVisibility(View.VISIBLE);
        } else {
            mFloorHost.setVisibility(View.GONE);
        }
    }

    private void setmFloorHost(CommentBean comment) {
        if (comment.getIsHost() == CommunityConstant.IS_HOST) {
            mFloorHost.setVisibility(View.VISIBLE);
        } else {
            mFloorHost.setVisibility(View.GONE);
        }
    }

    /*  private void setRelyLayout(CommentBean comment) {
          switch (comment.getReplys().size()) {
              case 0:
                  mReplyContent.setVisibility(View.GONE);
                  break;
              case 1:
                  mReplyContent.setVisibility(View.VISIBLE);
                  mReplyContent1.setVisibility(View.VISIBLE);
                  mReplyContent2.setVisibility(View.GONE);
                  setReplyContent1(comment);
                  break;
              default:
                  mReplyContent.setVisibility(View.VISIBLE);
                  mReplyContent1.setVisibility(View.VISIBLE);
                  mReplyContent2.setVisibility(View.VISIBLE);
                  setReplyContent1(comment);
                  setReplyContent2(comment);
                  break;
          }
          if (comment.getReplys().size() >= 2 && comment.getReplyCount() > 2) {
              mMoreReply.setVisibility(View.VISIBLE);
              mMoreReply.setText("更多" + (comment.getReplyCount() - CommunityConstant.COMMENT_TOP_REPLY_COUNT) + "条评论");
          } else {
              mMoreReply.setVisibility(View.GONE);
          }
      }

      private void setReplyContent1(CommentBean comment) {

          setReplyContent(getContext(), mReplyContent1, comment.getReplys().get(0));
      }

      private void setReplyContent2(CommentBean comment) {

          setReplyContent(getContext(), mReplyContent2, comment.getReplys().get(1));
      }
  */
    private void setReplyContent(Context con, TextView content, final CommentReplyBean reply) {
//        EmoticonsUtils.setContent2(con, content, reply);
    }

    public void setmIsLimitContentLine(boolean isLimit) {
        mIsLimitContentLine = isLimit;
    }

    public interface OnCommentLayoutClickListener {
        void onLayoutClick(CommentBean commentBean);
    }

    public void setOnCommentLayoutClickListener(OnCommentLayoutClickListener listener) {
        this.commentLayoutClickListener = listener;
    }

}
