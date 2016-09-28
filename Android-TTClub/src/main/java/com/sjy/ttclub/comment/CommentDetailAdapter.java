/**
 *
 */
package com.sjy.ttclub.comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class CommentDetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> items = new ArrayList<>();
    private int mCommentType;
    private OnListItemClickListener mItemClickListener;
    public CommentDetailAdapter(Context con, int type) {
        this.mCommentType = type;
        this.mContext = con;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = createViewByType(type);
        }
        setViewDataByType(convertView, items.get(position));
        return convertView;
    }

    private View createViewByType(int type) {
        View view = null;
        if (type == CommunityItemType.COMMENT_TYPE) {
            view = new CommentView(mContext);
        } else if (type == CommunityItemType.COMMENT_TYPE_REPLY) {
            view = new CommentReplyView(mContext);
        }
        return view;
    }

    private void setViewDataByType(View view, CommunityListItemInfo itemInfo) {
        if (itemInfo.getItemType() == CommunityItemType.COMMENT_TYPE) {
            setCommentData((CommentView) view, (CommentBean) itemInfo.getData());
        } else if (itemInfo.getItemType() == CommunityItemType.COMMENT_TYPE_REPLY) {
            setCommentReplyData((CommentReplyView) view, (CommentReplyBean) itemInfo.getData());
        }
    }

    private void setCommentData(CommentView commentView, CommentBean commentBean) {
        commentView.setCommentView(commentBean);
        commentView.setmIsLimitContentLine(false);
        commentView.setCommentView(commentBean);
        commentView.setCmtType(mCommentType);
        commentView.setCommentClickable(true);
        commentView.setmIsCommentDetail(true);
        commentView.setOnCommentLayoutClickListener(new CommentView.OnCommentLayoutClickListener() {
            @Override
            public void onLayoutClick(CommentBean commentBean) {
                handlerOnCommentViewClick();
            }
        });
    }

    private void setCommentReplyData(CommentReplyView replyView, final CommentReplyBean replyBean) {
        replyView.setCommentReplyView(replyBean);
        replyView.setOnCommentReplyLayoutClickListener(new CommentReplyView.OnCommentReplyLayoutClickListener() {
            @Override
            public void onLayoutClick() {
                handlerOnReplyViewClick(replyBean);
            }
        });
    }

    private void handlerOnCommentViewClick() {
        if(mItemClickListener==null){
            return;
        }
        mItemClickListener.onCommentViewClick();
    }

    private void handlerOnReplyViewClick(CommentReplyBean replyBean) {
        if(mItemClickListener==null){
            return;
        }
        mItemClickListener.onReplyViewClick(replyBean);
    }
   /* private void initData(ViewHolder viewHolder, final CommentReplyBean reply) {
//        EmoticonsUtils.setContent2(con, viewHolder.replyContent, reply);
        viewHolder.replyContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                edit.setText("");
                edit.setHint("回复@" + reply.getNickname() + ":");
                *//*App.reply = reply;
                App.isReply = true;*//*
            }
        });
    }*/

    public void updateDetailList(CommentBean comment, List<CommentReplyBean> replies) {
        items.clear();
        CommunityListItemInfo itemInfo;
        if (comment != null) {
            itemInfo = new CommunityListItemInfo();
            itemInfo.setItemType(CommunityItemType.COMMENT_TYPE);
            itemInfo.setData(comment);
            items.add(itemInfo);
        }
        if (!replies.isEmpty()) {
            for (CommentReplyBean replyBean : replies) {
                itemInfo = new CommunityListItemInfo();
                itemInfo.setItemType(CommunityItemType.COMMENT_TYPE_REPLY);
                itemInfo.setData(replyBean);
                items.add(itemInfo);
            }
        }
        notifyDataSetChanged();
    }

    public void setOnListViewItemClickListener(OnListItemClickListener listener){
        mItemClickListener=listener;
    }

    public interface OnListItemClickListener {
        void onCommentViewClick();

        void onReplyViewClick(CommentReplyBean replyBean);
    }
}
