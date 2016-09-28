/**
 *
 */
package com.sjy.ttclub.community.userinfopage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.sjy.ttclub.bean.community.MyCommentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张武林
 */
public class CommunityMyCommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyCommentBean> myCommentBeans = new ArrayList<>();

    public CommunityMyCommentAdapter(Context context, List<MyCommentBean> comments) {
        this.mContext = context;
        this.myCommentBeans = comments;
    }


    @Override
    public int getCount() {
        return this.myCommentBeans.size();
    }


    @Override
    public Object getItem(int position) {
        return myCommentBeans.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyCommentBean comments = myCommentBeans.get(position);

        if (convertView == null) {

            convertView = new CommunityMyCommentView(mContext);
        }
        setMyCommentViewData((CommunityMyCommentView) convertView, comments);
        return convertView;
    }
    private void setMyCommentViewData(CommunityMyCommentView commentView, MyCommentBean comments) {
        commentView.setMyCommentView(comments);
    }
}
