/**
 *
 */
package com.sjy.ttclub.community.userinfopage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sjy.ttclub.bean.community.MyPostBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class CommunityMyPostAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyPostBean> myPosts = new ArrayList<>();

    public CommunityMyPostAdapter(Context context, List<MyPostBean> myPostBeans) {
        this.myPosts = myPostBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.myPosts.size() > 0 ? myPosts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return myPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyPostBean postBean = myPosts.get(position);

        if (convertView == null) {
           convertView=new CommunityMyPostView(mContext);
        }
        setMyCommentViewData((CommunityMyPostView)convertView,postBean);
        return convertView;
    }
    private void setMyCommentViewData(CommunityMyPostView myPostView,MyPostBean myPostBean){
        myPostView.setMyPostView(myPostBean);
    }
}
