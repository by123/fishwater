/**
 *
 */
package com.sjy.ttclub.community.allcirclespage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sjy.ttclub.bean.community.CommunityCircleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class CommunityCirclesAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityCircleBean> mCircles = new ArrayList<CommunityCircleBean>();

    public CommunityCirclesAdapter(Context con, List<CommunityCircleBean> coms) {
        this.mCircles = coms;
        this.mContext = con;
    }

    @Override
    public int getCount() {
        return mCircles.size();
    }

    @Override
    public Object getItem(int position) {
        return mCircles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommunityCircleBean cb = mCircles.get(position);
        if (convertView == null) {
            convertView=new CommunityCircleView(mContext);
        }
        setCircleViewData((CommunityCircleView)convertView,cb);
        return convertView;
    }
    private void setCircleViewData(CommunityCircleView circleView,CommunityCircleBean circle){
        circleView.setCircleView(circle);
    }
}
