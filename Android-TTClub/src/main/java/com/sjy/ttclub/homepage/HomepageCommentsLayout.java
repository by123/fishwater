package com.sjy.ttclub.homepage;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.homepage.ArticleComments;
import com.sjy.ttclub.comment.CommentView;
import com.sjy.ttclub.community.CommunityConstant;

import java.util.List;

/**
 * Created by linhz on 2015/12/8.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageCommentsLayout extends LinearLayout {

    private TextView mHeaderView;
    private LinearLayout mCommentsLayout;
    private LinearLayout mBottomLayout;
    private TextView mMoreCommentView;
    private CommentsListener mCommentsListener;

    public HomepageCommentsLayout(Context context) {
        super(context);
        initViews();
    }

    private void initViews() {
        setOrientation(LinearLayout.VERTICAL);
        addHeaderView();
        addCommentsLayout();
        addBottomView();
    }

    private void addHeaderView() {
        mHeaderView = new TextView(getContext());
        mHeaderView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.space_14));
        mHeaderView.setTextColor(getResources().getColor(R.color.llgray));
        mHeaderView.setBackgroundColor(getResources().getColor(R.color.gray_bg));
        mHeaderView.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .space_34));
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(mHeaderView, lp);

    }

    private void addCommentsLayout() {
        mCommentsLayout = new LinearLayout(getContext());
        mCommentsLayout.setOrientation(LinearLayout.VERTICAL);
        mCommentsLayout.setBackgroundResource(R.drawable.default_listview_seletor);
        mCommentsLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClick();
            }
        });
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_10);
        lp.bottomMargin = lp.topMargin;
        lp.leftMargin = lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.space_14);
        this.addView(mCommentsLayout, lp);
    }

    private void addBottomView() {
        mBottomLayout = new LinearLayout(getContext());
        mBottomLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(mBottomLayout, lp);

        addDivider(mBottomLayout, getResources().getDimensionPixelSize(R.dimen.divider_height), getResources()
                .getDimensionPixelSize(R.dimen.space_10));

        FrameLayout parentLayout = new FrameLayout(getContext());
        parentLayout.setBackgroundResource(R.drawable.default_listview_seletor);
        parentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClick();
            }
        });
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .space_40));
        mBottomLayout.addView(parentLayout, lp);

        mMoreCommentView = new TextView(getContext());
        mMoreCommentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen
                .space_16));
        mMoreCommentView.setTextColor(getResources().getColor(R.color.black));
        mMoreCommentView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.homepage_article_comments_icon, 0, 0, 0);
        mMoreCommentView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.space_8));
        mMoreCommentView.setGravity(Gravity.CENTER);
        mMoreCommentView.setText(getResources().getString(R.string.homepage_detail_more_comments));
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        flp.gravity = Gravity.CENTER_HORIZONTAL;
        parentLayout.addView(mMoreCommentView, flp);

        addDivider(mBottomLayout, getResources().getDimensionPixelSize(R.dimen.space_1), 0);
    }

    private void addDivider(ViewGroup parentView, int topMargin) {
        addDivider(parentView, getResources().getDimensionPixelSize(R.dimen
                .divider_height), topMargin);
    }

    private void addDivider(ViewGroup parentView, int height, int topMargin) {
        View divider = new View(getContext());
        divider.setBackgroundColor(getResources().getColor(R.color.homepage_list_divider_color));
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_14);
        lp.rightMargin = lp.leftMargin;
        lp.topMargin = topMargin;
        parentView.addView(divider, lp);
    }


    public void setupCommentsLayout(ArticleComments comments) {
        String numText = getResources().getString(R.string.homepage_detail_comments_num);
        int commentNum = comments.getData().getCommentsCount();
        if (commentNum <= 0) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
        numText = numText.replace("#num#", String.valueOf(commentNum));
        mHeaderView.setText(numText);
        mCommentsLayout.removeAllViewsInLayout();
        List<CommentBean> list = comments.getData().getComments();
        for (CommentBean comment : list) {
            addCommentItemView(comment);
        }
    }

    private void addCommentItemView(CommentBean comment) {
        CommentView view = new CommentView(getContext());
        view.setCommentView(comment, CommunityConstant.COMMENTS_TYPE_ARTICLE);
        view.setEnableDivider(false);
        view.setCommentClickable(false);
        view.disableCommentBg();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_10);
        mCommentsLayout.addView(view, lp);
    }

    public void setCommentsListener(CommentsListener listener) {
        mCommentsListener = listener;
    }

    private void onCommentClick() {
        if (mCommentsListener != null) {
            mCommentsListener.onCommentsClick();
        }
    }

    public interface CommentsListener {
        void onCommentsClick();
    }
}
