package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.community.MyPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;


/**
 * Created by Administrator on 2015/12/2.
 */
public class CommunityMyPostView extends LinearLayout {
    private View parentView;
    private LinearLayout.LayoutParams lp;
    private TextView mCardStatusTextView;
    private TextView mCardThemeTextView;
    private TextView mCardContentTextView;
    private TextView mPostTimeTextView;
    private LinearLayout mImagesLayout;
    private SimpleDraweeView mImage1;
    private SimpleDraweeView mImage2;
    private SimpleDraweeView mImage3;
    private TextView mIsAnonyView;
    private MyPostBean mPostCard;

    public CommunityMyPostView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        parentView = View.inflate(getContext(), R.layout.community_my_post_view, null);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentView, lp);

        mCardStatusTextView = (TextView) parentView.findViewById(R.id.my_card_status);
        mCardThemeTextView = (TextView) parentView.findViewById(R.id.my_community_card_theme);
        mCardContentTextView = (TextView) parentView.findViewById(R.id.my_community_card_content);
        mPostTimeTextView = (TextView) parentView.findViewById(R.id.post_time);
        mImagesLayout = (LinearLayout) parentView.findViewById(R.id.images_ll_layout);
        mImage1 = (SimpleDraweeView) parentView.findViewById(R.id.card_image1);
        mImage2 = (SimpleDraweeView) parentView.findViewById(R.id.card_image2);
        mImage3 = (SimpleDraweeView) parentView.findViewById(R.id.card_image3);
        mIsAnonyView = (TextView) parentView.findViewById(R.id.isAnony);
    }

    public void setMyPostView(MyPostBean myPost) {
        this.mPostCard = myPost;
        setCardContentTextView(myPost);
        setCardStatusTextView(myPost);
        setCardThemeTextView(myPost);
        setPostTimeTextView(myPost);
        setImagesLayout(myPost);
        setIsAnonyView(myPost);
    }

    private void setIsAnonyView(MyPostBean myPost) {
        if (StringUtils.parseInt(myPost.getIsAnony()) == CommunityConstant.POST_CONTENT_ANNOY) {
            mIsAnonyView.setVisibility(VISIBLE);
        } else {
            mIsAnonyView.setVisibility(GONE);
        }
    }

    private void setCardThemeTextView(MyPostBean myPost) {
        if (StringUtils.isEmpty(myPost.getPostTitle()) && StringUtils.isEmpty(myPost.getContent())) {
            mCardThemeTextView.setVisibility(GONE);
            return;
        }
        mCardThemeTextView.setVisibility(VISIBLE);
        if (myPost.getPostTitle().length() > 0) {
            mCardThemeTextView.setText(myPost.getPostTitle());
            return;
        }
        if (myPost.getContent().length() > 0) {
            EmoticonsUtils.setContent(getContext(), mCardThemeTextView, myPost.getContent().replace("\n", ""));
            return;
        }
    }

    private void setCardContentTextView(MyPostBean myPost) {
        mCardContentTextView.setVisibility(GONE);
       /* if(StringUtils.isEmpty(myPost.getContent())){
            mCardContentTextView.setVisibility(GONE);
            return;
        }
        mCardContentTextView.setVisibility(GONE);
        EmoticonsUtils.setContent(getContext(), mCardContentTextView, myPost.getContent().replace("\n", ""));*/
    }

    private void setCardStatusTextView(MyPostBean myPost) {
        mCardStatusTextView.setVisibility(VISIBLE);
        switch (myPost.getPostStatus()) {
            case -1:
                mCardStatusTextView.setText("审核未通过");
                break;
            case 0:
                mCardStatusTextView.setText("审核中");
                break;
            case 1:
                mCardStatusTextView.setText("审核通过");
                mCardStatusTextView.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    private void setPostTimeTextView(MyPostBean myPost) {
        mPostTimeTextView.setText(TimeUtil.getCDTime(myPost.getPublishTime()));
    }

    private void setImagesLayout(MyPostBean myPost) {
        if (myPost.getImages().size() == 0) {
            mImagesLayout.setVisibility(GONE);
            return;
        }
        mImagesLayout.setVisibility(VISIBLE);
        switch (myPost.getImages().size()) {
            case 1:
                mImage1.setVisibility(VISIBLE);
                mImage2.setVisibility(INVISIBLE);
                mImage3.setVisibility(INVISIBLE);
                mImage1.setImageURI(Uri.parse(myPost.getImages().get(0).getImageUrl()));
                break;
            case 2:
                mImage1.setVisibility(VISIBLE);
                mImage2.setVisibility(VISIBLE);
                mImage3.setVisibility(INVISIBLE);
                mImage1.setImageURI(Uri.parse(myPost.getImages().get(0).getImageUrl()));
                mImage2.setImageURI(Uri.parse(myPost.getImages().get(1).getImageUrl()));
                break;
            default:
                mImage1.setVisibility(VISIBLE);
                mImage2.setVisibility(VISIBLE);
                mImage3.setVisibility(VISIBLE);
                mImage1.setImageURI(Uri.parse(myPost.getImages().get(0).getImageUrl()));
                mImage2.setImageURI(Uri.parse(myPost.getImages().get(1).getImageUrl()));
                mImage3.setImageURI(Uri.parse(myPost.getImages().get(2).getImageUrl()));
                break;
        }
    }
}
