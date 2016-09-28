/**
 *
 */
package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class CommunityPostListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> mItems = new ArrayList<>();
    private static final int CARD_WITH_IMAGE = 5;
    private static final int QA_ITEM = 6;
    private static final int DEFAULT_DIVIDER = 0;

    public CommunityPostListAdapter(Context con, List<CommunityListItemInfo> items) {
        this.mItems = items;
        this.mContext = con;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mItems.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return mItems.get(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 9;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        int type = getItemViewType(position);
        CommunityPostBean card = (CommunityPostBean)mItems.get(position).getData();
        if (convertView == null) {
            convertView = createViewByType(type);
        }
        setItemViewData(type, convertView, card);
        return convertView;
    }

    private void setItemViewData(int type, View view, CommunityPostBean card) {
        if (type == CommunityItemType.POST_TYPE_TOP) {
            setTopCardViewData((CommunityTopPostView) view, card);
        } else if (type == CommunityItemType.POST_TYPE_IMAGE) {
            setCommunityCardViewData((CommunityPostView) view, card);
        } else if (type == CommunityItemType.POST_TYPE_QA) {
            setCommunityQaViewData((CommunityQaView) view, card);
        }
    }

    private View createViewByType(int type) {
        View view = null;
        if (type == CommunityItemType.POST_TYPE_TOP) {
            view = new CommunityTopPostView(mContext);
        } else if (type == CommunityItemType.POST_TYPE_DEFAULT_DIVIDER) {
            view = new CommunityDivideView(mContext);
        } else if (type == CommunityItemType.POST_TYPE_IMAGE) {
            view = new CommunityPostView(mContext);
        } else if (type == CommunityItemType.POST_TYPE_QA) {
            view = new CommunityQaView(mContext);
        }
        return view;
    }

    private void setTopCardViewData(CommunityTopPostView topCardView, CommunityPostBean postCard) {
        topCardView.setTopView(postCard);
    }

    private void setCommunityQaViewData(CommunityQaView qaView, CommunityPostBean postCard) {
        qaView.setCommunityQaView(postCard);
    }

    private void setCommunityCardViewData(CommunityPostView cardView, CommunityPostBean postCard) {
        cardView.setCommunityCardView(postCard);
    }
}
