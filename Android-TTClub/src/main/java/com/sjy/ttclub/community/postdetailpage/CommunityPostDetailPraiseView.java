package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.praise.PraiseRequest;
import com.sjy.ttclub.praise.PraiseUtil;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhangwulin on 2015/12/17.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailPraiseView extends LinearLayout {

    private Context mContext;
    private ImageView mPraiseIcon;
    private TextView mPraiseCountTextView;
    private View mDividerView;
    private TextView mEndTagTextView;
    private CommunityPostBean mPost;
    private PraiseRequest mPraiseRequest;

    public CommunityPostDetailPraiseView(Context context) {
        this(context, null);
    }

    public CommunityPostDetailPraiseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityPostDetailPraiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View parentView = View.inflate(mContext, R.layout.community_post_detail_item_praise_view, null);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(parentView, lp);
        mDividerView = parentView.findViewById(R.id.end_divider);
        mEndTagTextView = (TextView) parentView.findViewById(R.id.end_tag);
        mPraiseIcon = (ImageView) parentView.findViewById(R.id.praise_icon);
        mPraiseCountTextView = (TextView) parentView.findViewById(R.id.btn_praise_count);
        mPraiseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePraiseLayoutOnClick();
            }
        });
    }

    public void setCardDetailPraiseView(CommunityPostBean post) {
        this.mPost = post;
        initPraiseRequest();
        setmPraiseIcon(post);
        setmPraiseCountTextView(post);
        setQaType(post);
    }

    private void initPraiseRequest() {
        mPraiseRequest = new PraiseRequest(mContext);
    }
    private void handlePraiseLayoutOnClick() {
        // 统计post_praise_master
        StatsModel.stats(StatsKeyDef.POST_PRAISE_MASTER);
        if (mPraiseRequest == null) {
            return;
        }
        if (mPost == null) {
            return;
        }
        mPraiseIcon.startAnimation(AnimotionDao.getScaleAnimation(false));
        if (!PraiseUtil.isPraised(mContext, mPost)) {
            mPost.setPraiseCount(mPost.getPraiseCount() + 1);
            mPost.setIsPraise(1);
            mPraiseRequest.addCardPraise(mPost.getPostId());
            PraiseUtil.savePraiseState(mContext, mPost.getPostId());
            setmPraiseIcon(mPost);
            setmPraiseCountTextView(mPost);
            notifyPostPraiseStateChange();
            return;
        }
        if (mPost.getPraiseCount() == 0) {
            mPost.setPraiseCount(0);
        } else {
            mPost.setPraiseCount(mPost.getPraiseCount() - 1);
        }
        mPost.setIsPraise(0);
        mPraiseRequest.cancelCardPraise(mPost.getPostId());
        PraiseUtil.removePraiseState(mContext, mPost.getPostId());
        setmPraiseCountTextView(mPost);
        setmPraiseIcon(mPost);
        notifyPostPraiseStateChange();
    }

    private void notifyPostPraiseStateChange(){
        if(mPost==null){
            return;
        }
        Notification notification=Notification.obtain(NotificationDef.N_COMMUNITY_UPDATE_POST_PRAISE,mPost);
        NotificationCenter.getInstance().notify(notification);
    }
    private void setQaType(CommunityPostBean post){
        if(post.getCircleType()==CommunityConstant.CIRCLE_TYPE_QA_POST){
            mDividerView.setVisibility(GONE);
            mEndTagTextView.setVisibility(GONE);
        }else {
            mDividerView.setVisibility(VISIBLE);
            mEndTagTextView.setVisibility(VISIBLE);
        }
    }
    private void setmPraiseIcon(CommunityPostBean post) {
        if (post.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            if (PraiseUtil.isPraised(mContext, post)) {
                mPraiseIcon.setImageResource(R.drawable.community_qa_question_selected);
                mPraiseCountTextView.setTextColor(ResourceHelper.getColor(R.color.community_praise_text_color));
            } else {
                mPraiseIcon.setImageResource(R.drawable.community_qa_question);
                mPraiseCountTextView.setTextColor(ResourceHelper.getColor(R.color.llgray));
            }
            return;
        }
        if (PraiseUtil.isPraised(mContext, post)) {
            mPraiseIcon.setImageResource(R.drawable.community_post_praise_select);
            mPraiseCountTextView.setTextColor(ResourceHelper.getColor(R.color.community_praise_text_color));
        } else {
            mPraiseIcon.setImageResource(R.drawable.community_post_praise);
            mPraiseCountTextView.setTextColor(ResourceHelper.getColor(R.color.llgray));
        }
    }

    private void setmPraiseCountTextView(CommunityPostBean post) {
        if (post.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mPraiseCountTextView.setText(CommunityConstant.MAX_SHOW_VALUE);
            return;
        }
        mPraiseCountTextView.setText(String.valueOf(post.getPraiseCount()));
    }

}
