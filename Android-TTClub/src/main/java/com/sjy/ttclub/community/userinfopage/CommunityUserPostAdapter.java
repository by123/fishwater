package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2015/12/31.
 * email 1501448275@qq.com
 */
public class CommunityUserPostAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> items = new ArrayList<>();

    public CommunityUserPostAdapter(Context context, List<CommunityListItemInfo> datas) {

        this.mContext = context;
        this.items = datas;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        CommunityPostBean mPost = (CommunityPostBean) items.get(position).getData();
        if (convertView == null) {
            convertView = new CommunityUserPostView(mContext);
        }
        setUserPostViewData((CommunityUserPostView)convertView,mPost);
        return convertView;
    }

    private void setUserPostViewData(CommunityUserPostView postView, CommunityPostBean postBean) {
        if (postBean == null) {
            return;
        }
        postView.setUserPostView(postBean);
    }
}
