package com.sjy.ttclub.comment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhangwulin on 2015/12/29.
 * email 1501448275@qq.com
 */
public class CommentReplyView extends LinearLayout {
    private Context mContext;
    private TextView mContentTextView;
    private View mDivideView;
    private LinearLayout.LayoutParams lp;
    private OnCommentReplyLayoutClickListener commentLayoutClickListener;

    public CommentReplyView(Context context) {
        this(context, null);
    }

    public CommentReplyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentReplyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        createView();
    }

    private void createView() {
        mContentTextView = new TextView(mContext);
        mContentTextView.setTextColor(ResourceHelper.getColor(R.color.actionsheet_black));
        mContentTextView.setTextSize(13);
        mContentTextView.setBackground(ResourceHelper.getDrawable(R.drawable.account_selector_option));
        mContentTextView.setPadding(ResourceHelper.getDimen(R.dimen.space_60), ResourceHelper.getDimen(R.dimen.space_10), ResourceHelper.getDimen(R.dimen.space_10), ResourceHelper.getDimen(R.dimen.space_10));
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mContentTextView, lp);

        mDivideView = new View(mContext);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper.getDimen(R.dimen.divider_height));
        addView(mDivideView, lp);

        mContentTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerContentViewOnClick();
            }
        });
    }

    public void setCommentReplyView(CommentReplyBean replyBean) {
        setmContentTextView(replyBean);
    }

    private void setmContentTextView(CommentReplyBean replyBean) {
        if (StringUtils.isEmpty(replyBean.getContent())) {
            return;
        }
//        EmoticonsUtils.setContent2(mContext, mContentTextView, replyBean);
    }

    private void handlerContentViewOnClick() {
        if (commentLayoutClickListener != null) {
            commentLayoutClickListener.onLayoutClick();
        }
    }

    public interface OnCommentReplyLayoutClickListener {
        void onLayoutClick();
    }

    public void setOnCommentReplyLayoutClickListener(OnCommentReplyLayoutClickListener listener) {
        this.commentLayoutClickListener = listener;
    }
}
