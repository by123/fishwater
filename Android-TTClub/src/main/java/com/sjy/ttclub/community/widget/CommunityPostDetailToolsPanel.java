package com.sjy.ttclub.community.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.TitleButtonBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by zhangwulin on 2015/12/18.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailToolsPanel extends BasePanel implements View.OnClickListener {
    private View parentView;
    private TextView mSeeMoreTextView;
    private TextView mReportTextView;
    private TextView mCollectTextView;
    private TextView mShareTextView;
    private TextView mCancelButton;
    private OnMoreSeeClickListener listener;
    private CommunityPostBean mPost;
    private TitleButtonBean mButtonBean;

    public CommunityPostDetailToolsPanel(Context context, CommunityPostBean post, TitleButtonBean buttonBean) {
        super(context);
        this.mPost = post;
        this.mButtonBean=buttonBean;
        initView();
    }

    @Override
    protected View onCreateContentView() {
        parentView = View.inflate(mContext, R.layout.community_more_setting_pop, null);
        return parentView;
    }

    private void initView() {
        mCancelButton = (TextView) parentView.findViewById(R.id.cancel_button);
        mSeeMoreTextView = (TextView) parentView.findViewById(R.id.more_see);
        mCollectTextView = (TextView) parentView.findViewById(R.id.more_collect);
        mReportTextView = (TextView) parentView.findViewById(R.id.more_report);
        mShareTextView = (TextView) parentView.findViewById(R.id.more_share);
        mCollectTextView.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mShareTextView.setOnClickListener(this);
        mSeeMoreTextView.setOnClickListener(this);
        mReportTextView.setOnClickListener(this);
        if (mPost.getIsCollect() == CommunityConstant.NO_COLLECT_FLAG) {
            mCollectTextView.setSelected(false);
            mCollectTextView.setText(ResourceHelper.getString(R.string.community_collect));
        } else {
            mCollectTextView.setSelected(true);
            mCollectTextView.setText(ResourceHelper.getString(R.string.community_cancel_collect));
        }
        if(mButtonBean.isSeeSelected()){
            mSeeMoreTextView.setText(ResourceHelper.getString(R.string.community_recover_see_comment));
        }else{
            mSeeMoreTextView.setText(ResourceHelper.getString(R.string.community_drop_see_comment));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_see:
                handlerOnMoreSeeClick();
                hidePanel();
                break;
            case R.id.more_collect:
                handlerOnCollectClick();
                hidePanel();
                break;
            case R.id.more_share:
                handlerOnShareClick();
                hidePanel();
                break;
            case R.id.more_report:
                handlerOnReportClick();
                hidePanel();
                break;
            case R.id.cancel_button:
                hidePanel();
                break;
            default:
                break;
        }
    }

    private void handlerOnMoreSeeClick() {
        if (listener == null) {
            return;
        }
        if (mSeeMoreTextView == null) {
            return;
        }
        listener.onMoreSeeClick(mSeeMoreTextView);
    }

    private void handlerOnCollectClick() {
        if (listener == null) {
            return;
        }
        if (mCollectTextView == null) {
            return;
        }
        listener.onCollectClick(mCollectTextView);
    }

    private void handlerOnShareClick() {
        if (mPost == null) {
            return;
        }
        if (mPost.getImages() == null) {
            return;
        }
        String imageUrl = "";
        if (mPost.getImages().size() > 0) {
            imageUrl = mPost.getImages().get(0).getImageUrl();
        }
        showShareWindow(imageUrl);
    }

    private void handlerOnReportClick() {
        if (listener == null) {
            return;
        }
        listener.onReportClick();
    }

    public void setMoreSeeListener(OnMoreSeeClickListener listener) {
        this.listener = listener;
    }

    public interface OnMoreSeeClickListener {
        void onMoreSeeClick(TextView seeText);

        void onCollectClick(TextView collectText);

        void onReportClick();
    }

    private void showShareWindow(String imageUrl) {
        if (mPost == null) {
            return;
        }
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareImageUrl(imageUrl);
        builder.setShareTitle(mPost.getPostTitle());
        String content = mPost.getContent();
        if (content.length() >= 140) {
            content.substring(0, 80);
        }
        if (StringUtils.isEmpty(content)) {
            content = mPost.getPostTitle();
        }
        builder.setShareContent(content);
        Bundle shareBundle = new Bundle();
        shareBundle.putString("type","post");
        String title = mPost.getPostTitle();
        if(StringUtils.isEmpty(title)){
            title = String.valueOf(mPost.getPostId());
        }
        shareBundle.putString("spec", title);
        builder.setStatsBundle(shareBundle);
        String shareUrl = HttpUrls.SHARE_CARD_URL + mPost.getPostId();
        builder.setShareUrl(shareUrl);
        Message message=Message.obtain();
        message.what= MsgDef.MSG_SHARE;
        message.obj=builder.create();
        MsgDispatcher.getInstance().sendMessage(message);

    }
}
