package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.community.circledetailpage.CommunityHotPostView;
import com.sjy.ttclub.community.circledetailpage.CommunityQaView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zwl
 */
public class CommunityHotPostAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> mHotItems = new ArrayList<>();
    private static final int CARD_TYPE = CommunityItemType.POST_TYPE_IMAGE;
    private static final int QA_TYPE = CommunityItemType.POST_TYPE_QA;

    public CommunityHotPostAdapter(Context con, List<CommunityListItemInfo> coms) {
        this.mHotItems = coms;
        this.mContext = con;

    }


    @Override
    public int getCount() {
        return this.mHotItems.size();
    }


    @Override
    public Object getItem(int position) {
        return mHotItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mHotItems.get(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        switch (type) {
            case CARD_TYPE:
                CommunityPostBean cb = (CommunityPostBean) mHotItems.get(position).getData();
                if (convertView == null) {
                    convertView = new CommunityHotPostView(mContext);
                }
                setCommunityCardViewData((CommunityHotPostView) convertView, cb);
                break;
            case QA_TYPE:
                CommunityPostBean qa = (CommunityPostBean) mHotItems.get(position).getData();
                if (convertView == null) {
                    convertView = new CommunityQaView(mContext);
                }
                setCommunityQaViewData((CommunityQaView) convertView, qa);
                break;
            default:
                CommunityPostBean cb1 = (CommunityPostBean) mHotItems.get(position).getData();
                if (convertView == null) {
                    convertView = new CommunityHotPostView(mContext);
                }
                setCommunityCardViewData((CommunityHotPostView) convertView, cb1);
                break;
        }
        return convertView;
    }

    private void setCommunityQaViewData(CommunityQaView qaView, CommunityPostBean postCard) {
        qaView.setCommunityQaView(postCard);
    }

    private void setCommunityCardViewData(CommunityHotPostView cardView, CommunityPostBean postCard) {
        cardView.setCommunityCardView(postCard);
    }
}
