/**
 *
 */
package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.ImageCard;
import com.sjy.ttclub.bean.community.TitleButtonBean;
import com.sjy.ttclub.comment.CommentView;
import com.sjy.ttclub.community.CommunityConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张武林
 */
public class CommunityPostDetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> items = new ArrayList<>();
    private List<CommentBean> generalComments = new ArrayList<>();
    private List<CommentBean> topComments = new ArrayList<>();
    private List<ImageCard> images = new ArrayList<>();
    private CommunityPostBean mPost;
    //item 类型
    final int TYPE_POST_CONTENT = 0;//帖子内容项
    final int TYPE_POST_IAMGE = 1;//帖子图片项
    final int TYPE_POST_PRAISE = 2;//点赞项
    final int TYPE_EV_TITLE_ALL = 3;//全部标题
    final int TYPE_EV_CONTENT_TOP = 4;//评论项
    final int TYPE_EV_CONTENT = 5;//评论项
    final int TYPE_NO_EV = 6;//无评论项
    private static int mPostType;
    private TitleButtonBean mButtonState;
    private OnCommentItemClickListener mListener;

    public CommunityPostDetailAdapter(Context con, CommunityPostBean post) {
        this.mContext = con;
        this.mPost = post;
        this.mPostType = post.getCircleType();
    }

    public void setTitleButton(TitleButtonBean buttonState) {
        this.mButtonState = buttonState;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        return 7;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = items.get(position).getItemType();
        switch (type) {
            case TYPE_POST_CONTENT:
                if (convertView == null) {
                    convertView = createContentView(mPost);
                }
                setContentViewData(convertView, mPost);
                break;
            case TYPE_POST_IAMGE:
                ImageCard image;
                image = (ImageCard) items.get(position).getData();
                if (convertView == null) {
                    convertView = new CommunityPostDetailImageView(mContext);
                }
                setCardDetailImageViewData((CommunityPostDetailImageView) convertView, image);
                break;
            case TYPE_POST_PRAISE:
                if (convertView == null) {
                    convertView = new CommunityPostDetailPraiseView(mContext);
                }
                setCardDetailPraiseViewData((CommunityPostDetailPraiseView) convertView, mPost);
                break;
            case TYPE_EV_CONTENT_TOP:
                if (convertView == null) {
                    convertView = new CommunityPostDetailTitleView(mContext);
                }
                setTitleViewData((CommunityPostDetailTitleView) convertView, mContext.getString(R.string.community_title_top_comment));
                break;
            case TYPE_EV_TITLE_ALL:
                if (convertView == null) {
                    convertView = new CommunityPostDetailTitleView(mContext);
                }
                setTitleViewData((CommunityPostDetailTitleView) convertView);
                break;
            case TYPE_EV_CONTENT:
                CommentBean comment;
                comment = (CommentBean) items.get(position).getData();
                if (convertView == null) {
                    convertView = new CommentView(mContext);
                }
                setCommentViewData((CommentView) convertView, comment);
                break;
            case TYPE_NO_EV:
                if (convertView == null) {
                    convertView = new CommunityPostDetailNoCommentsView(mContext);
                }
                setNoCommentsTip((CommunityPostDetailNoCommentsView) convertView);
                break;
            default:
                break;
        }
        return convertView;
    }

    private View createContentView(CommunityPostBean postBean) {
        View view = null;
        if (postBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            view = new CommunityQaDetailContentView(mContext);
        } else if (postBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_POST) {
            view = new CommunityPostDetailContentView(mContext);
        }
        return view;
    }

    private void setContentViewData(View convertView, CommunityPostBean postBean) {
        if (postBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            setQaDetailContentData((CommunityQaDetailContentView) convertView, mPost);
        } else if (postBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_POST) {
            setCardDetailContentData((CommunityPostDetailContentView) convertView, mPost);
        }
    }

    private void setCardDetailContentData(CommunityPostDetailContentView contentView, CommunityPostBean post) {
        contentView.setPostDetailContentView(post, mPostType);
    }

    private void setQaDetailContentData(CommunityQaDetailContentView contentView, CommunityPostBean post) {
        contentView.setQaDetailContentView(post);
    }

    private void setCommentViewData(CommentView commentView, CommentBean comments) {
        commentView.setCommentView(mPost, comments, CommunityConstant.COMMENTS_TYPE_POST);
        commentView.setOnCommentLayoutClickListener(new CommentView.OnCommentLayoutClickListener() {
            @Override
            public void onLayoutClick(CommentBean commentBean) {
                if (mListener != null) {
                    mListener.onItemClick(commentBean);
                }
            }
        });
    }

    private void setCardDetailImageViewData(CommunityPostDetailImageView imageView, ImageCard imageCard) {
        imageView.setCardDetailImageView(imageCard);
        imageView.setPost(mPost);
    }

    private void setCardDetailPraiseViewData(CommunityPostDetailPraiseView praiseView, CommunityPostBean post) {
        praiseView.setCardDetailPraiseView(post);
    }

    private void setTitleViewData(CommunityPostDetailTitleView postDetailTitleView) {
        if (mPost == null) {
            postDetailTitleView.setTitleView(mContext.getString(R.string.community_title_comment));
            return;
        }
        if (mPost.getReplyCount() > 0) {
            postDetailTitleView.setTitleView(mContext.getString(R.string.community_title_comment) + mPost.getReplyCount());
        } else {
            postDetailTitleView.setTitleView(mContext.getString(R.string.community_title_comment));
        }
    }

    private void setTitleViewData(CommunityPostDetailTitleView postDetailTitleView, String title) {
        if (mPostType == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            postDetailTitleView.setTitleView(mContext.getString(R.string.community_title_qa_top_comment));
            return;
        }
        postDetailTitleView.setTitleView(title);
    }

    private void setNoCommentsTip(CommunityPostDetailNoCommentsView view) {
        if (mButtonState != null && mButtonState.isHostSelected()) {
            if (mPostType == CommunityConstant.CIRCLE_TYPE_POST) {
                view.setNoCommentsTipView(mContext.getString(R.string.community_no_comments_tips_host));
            } else {
                view.setNoCommentsTipView(mContext.getString(R.string.community_no_comments_tips_question_host));
            }
            return;
        }
        view.setNoCommentsTipView(mContext.getString(R.string.community_no_comments_tip));
    }

    public void updateItemList() {
        items.clear();

        CommunityListItemInfo item = new CommunityListItemInfo();
        item.setItemType(TYPE_POST_CONTENT);
        item.setData(mPost);
        items.add(item);

        if (mPost.getImages().size() > 0) {
            for (ImageCard image : mPost.getImages()) {
                item = new CommunityListItemInfo();
                item.setItemType(TYPE_POST_IAMGE);
                item.setData(image);
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void updateItemList(List<CommentBean> topComments, List<CommentBean> comments) {
        generalComments = comments;
        items.clear();

        CommunityListItemInfo item = new CommunityListItemInfo();
        item.setItemType(TYPE_POST_CONTENT);
        item.setData(mPost);
        items.add(item);

        if (mPost.getImages().size() > 0) {
            for (ImageCard image : mPost.getImages()) {
                item = new CommunityListItemInfo();
                item.setItemType(TYPE_POST_IAMGE);
                item.setData(image);
                items.add(item);
            }
        }
        item = new CommunityListItemInfo();
        item.setItemType(TYPE_POST_PRAISE);
        items.add(item);
        if (topComments.size() > 0) {
            item = new CommunityListItemInfo();
            item.setItemType(TYPE_EV_CONTENT_TOP);
            items.add(item);
        }
        if (topComments.size() > 0) {
            for (CommentBean comment : topComments) {
                item = new CommunityListItemInfo();
                item.setItemType(TYPE_EV_CONTENT);
                item.setData(comment);
                items.add(item);
            }
        }
        item = new CommunityListItemInfo();
        item.setItemType(TYPE_EV_TITLE_ALL);
        items.add(item);

        if (comments.size() > 0) {
            for (CommentBean comment : comments) {
                item = new CommunityListItemInfo();
                item.setItemType(TYPE_EV_CONTENT);
                item.setData(comment);
                items.add(item);
            }
        } else {
            item = new CommunityListItemInfo();
            item.setItemType(TYPE_NO_EV);
            items.add(item);
        }
        notifyDataSetChanged();
    }


    public void setOnCommentItemClickListener(OnCommentItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnCommentItemClickListener {
        void onItemClick(CommentBean commentBean);
    }
}
