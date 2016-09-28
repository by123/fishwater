package com.sjy.ttclub.comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.community.postdetailpage.CommunityPostDetailAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CommentsListViewAdapter extends ArrayAdapter<CommentBean> {

    private int mType;
    private OnCommentListItemClickListener mListener;
    public CommentsListViewAdapter(Context context, List<CommentBean> commentList) {
        super(context, 0, commentList);
    }

    public void setCommentType(int type) {
        mType = type;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new CommentView(getContext());
        }
        CommentBean comment = getItem(position);
        setCommentViewData((CommentView) view, comment);
        return view;
    }

    private void setCommentViewData(CommentView commentView, CommentBean comment) {
        commentView.setCommentView(comment, mType);
        commentView.setOnCommentLayoutClickListener(new CommentView.OnCommentLayoutClickListener() {
            @Override
            public void onLayoutClick(CommentBean commentBean) {
                if(mListener!=null){
                    mListener.onClick(commentBean);
                }
            }
        });
    }

    public void setOnCommentListItemClickListener(OnCommentListItemClickListener listener){
        this.mListener=listener;
    }

    public interface OnCommentListItemClickListener{
        void onClick(CommentBean commentBean);
    }
}
