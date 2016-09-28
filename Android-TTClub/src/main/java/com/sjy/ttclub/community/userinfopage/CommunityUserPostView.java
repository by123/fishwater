package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhangwulin on 2015/12/31.
 * email 1501448275@qq.com
 */
public class CommunityUserPostView extends LinearLayout {

    private Context mContext;
    private TextView mPostContent;
    private TextView mPostTheme;
    private TextView mPostReplyCounts;
    private SimpleDraweeView mDrawee1, mDrawee2, mDrawee3;
    private SimpleDraweeView mCircleIcon;
    private TextView mCircleName;
    private LinearLayout mDraweesLayout;
    private ImageView mPraiseIcon;
    private TextView mPraiseCount;
    private static int JUMP_TYPE_POST=6;
    private static int JUMP_TYPE_QA_POST=7;
    public CommunityUserPostView(Context context) {
        this(context, null);
    }

    public CommunityUserPostView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityUserPostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        createView();
    }

    private void createView() {
        View view = View.inflate(mContext, R.layout.community_user_post_view_layout, null);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(view, lp);
        mPostContent = (TextView) view.findViewById(R.id.hot_community_card_content);
        mPostTheme = (TextView) view.findViewById(R.id.hot_community_card_theme);
        mPostReplyCounts = (TextView) view.findViewById(R.id.hot_reply_counts);
        mPraiseCount = (TextView) view.findViewById(R.id.hot_community_priase_count);
        mCircleIcon = (SimpleDraweeView) view.findViewById(R.id.from_circle_icon);
        mCircleName = (TextView) view.findViewById(R.id.from_circle);
        mPraiseIcon = (ImageView) view.findViewById(R.id.hot_community_priase_count_icon);
        mDraweesLayout = (LinearLayout) view.findViewById(R.id.images_ll_layout);
        mDrawee1 = (SimpleDraweeView) view.findViewById(R.id.card_image1);
        mDrawee2 = (SimpleDraweeView) view.findViewById(R.id.card_image2);
        mDrawee3 = (SimpleDraweeView) view.findViewById(R.id.card_image3);
    }

    public void setUserPostView(CommunityPostBean post) {
        setmCircleInfo(post);
        setmPostContent(post);
        setmPostReplyCounts(post);
        setmPostTheme(post);
        setmPraiseCount(post);
        setmPraiseIcon(post);
        setmDraweesLayout(post);
    }

    private void setmPostContent(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getBriefContent())) {
            mPostContent.setVisibility(View.GONE);
            return;
        }
        mPostContent.setVisibility(View.VISIBLE);
        EmoticonsUtils.setContent(mContext, mPostContent, post.getBriefContent().replace("\n", ""));
    }

    private void setmPostTheme(CommunityPostBean post) {
        if (StringUtils.isEmpty(post.getPostTitle())) {
            mPostTheme.setVisibility(View.GONE);
            return;
        }
        mPostTheme.setVisibility(View.VISIBLE);
        mPostTheme.setText(post.getPostTitle().replace("\n", ""));
    }

    private void setmPostReplyCounts(CommunityPostBean post) {
        if (post.getReplyCount() > CommunityConstant.MAX_COUNTS) {
            mPostReplyCounts.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mPostReplyCounts.setText(post.getReplyCount() + "");
        }
    }

    private void setmCircleInfo(CommunityPostBean post) {
        if (post.getJumpType() == JUMP_TYPE_POST || post.getJumpType() == JUMP_TYPE_QA_POST) {
            if (StringUtils.isNotEmpty(post.getCircleIconUrl())) {
                mCircleIcon.setVisibility(View.VISIBLE);
                mCircleIcon.setImageURI(Uri.parse(post.getCircleIconUrl()));
            }
            if (StringUtils.isNotEmpty(post.getCircleName())) {
                mCircleName.setVisibility(View.VISIBLE);
                mCircleName.setText(post.getCircleName());
            }
        } else {
            mCircleIcon.setVisibility(View.GONE);
            mCircleName.setVisibility(View.GONE);
        }
    }

    private void setmDraweesLayout(CommunityPostBean post) {
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mDrawee1.getLayoutParams();
        params1.width = HardwareUtil.getDeviceWidth() / 3 - 25;
        params1.height = params1.width;
        mDrawee1.setLayoutParams(params1);

        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mDrawee2.getLayoutParams();
        params2.width = HardwareUtil.getDeviceWidth() / 3 - 25;
        params2.height = params2.width;
        mDrawee2.setLayoutParams(params2);

        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) mDrawee3.getLayoutParams();
        params3.width = HardwareUtil.getDeviceWidth() / 3 - 25;
        params3.height = params3.width;
        mDrawee3.setLayoutParams(params3);
        switch (post.getImages().size()) {
            case 0:
                mDraweesLayout.setVisibility(View.GONE);
                break;
            case 1:
                mDraweesLayout.setVisibility(View.VISIBLE);
                mDrawee1.setVisibility(VISIBLE);
                mDrawee2.setVisibility(INVISIBLE);
                mDrawee3.setVisibility(INVISIBLE);
                setImage(mDrawee1, post.getImages().get(0).getImageUrl());
                break;
            case 2:
                mDraweesLayout.setVisibility(View.VISIBLE);
                mDrawee1.setVisibility(VISIBLE);
                mDrawee2.setVisibility(VISIBLE);
                mDrawee3.setVisibility(INVISIBLE);
                setImage(mDrawee1, post.getImages().get(0).getImageUrl());
                setImage(mDrawee2, post.getImages().get(1).getImageUrl());
                break;
            default:
                mDraweesLayout.setVisibility(View.VISIBLE);
                mDrawee1.setVisibility(VISIBLE);
                mDrawee2.setVisibility(VISIBLE);
                mDrawee3.setVisibility(VISIBLE);
                setImage(mDrawee1, post.getImages().get(0).getImageUrl());
                setImage(mDrawee2, post.getImages().get(1).getImageUrl());
                setImage(mDrawee3, post.getImages().get(2).getImageUrl());
                break;
        }
    }

    private void setImage(SimpleDraweeView draweeView, String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            draweeView.setVisibility(INVISIBLE);
        }
        draweeView.setImageURI(Uri.parse(imagePath));
    }

    private void setmPraiseIcon(CommunityPostBean post) {
        if (post.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            mPraiseIcon.setImageResource(R.drawable.community_userinfo_qa_icon);
        } else {
            mPraiseIcon.setImageResource(R.drawable.community_userinfo_praise_icon);
        }
    }

    private void setmPraiseCount(CommunityPostBean post) {
        if (post.getPraiseCount() > CommunityConstant.MAX_COUNTS) {
            mPraiseCount.setText(CommunityConstant.MAX_SHOW_VALUE);
        } else {
            mPraiseCount.setText(String.valueOf(post.getPraiseCount()));
        }
    }
}
