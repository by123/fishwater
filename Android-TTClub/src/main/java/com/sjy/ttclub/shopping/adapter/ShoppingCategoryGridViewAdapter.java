package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingCategoryBean;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryGridViewAdapter extends BaseAdapter {

    public final static String TAG = "ShoppingCategoryGridViewAdapter";

    private Context mContext;

    private ViewHolder mViewHolder;

    private List<ShoppingCategoryBean> mCategoryBeanList;

    private int imgWidth = 0;

    public ShoppingCategoryGridViewAdapter(Context context, List<ShoppingCategoryBean> list) {
        mContext = context;
        mCategoryBeanList = list;

        imgWidth = HardwareUtil.getDeviceWidth() / 10;
    }

    private void setupGridView(int position) {
        final String thumbUrl = mCategoryBeanList.get(position).getThumbUrl();
        mViewHolder.sdvCategory.getLayoutParams().height = imgWidth;
        mViewHolder.sdvCategory.getLayoutParams().width = imgWidth;
        if (!StringUtils.isEmpty(thumbUrl)) {
            mViewHolder.sdvCategory.setImageURI(Uri.parse(thumbUrl));
        }
        final String cateName = mCategoryBeanList.get(position).getName();
        if (!StringUtils.isEmpty(cateName)) {
            mViewHolder.tvCategory.setText(cateName);
        }
        final int cateId = mCategoryBeanList.get(position).getId();
        mViewHolder.tvCategory.setTag(cateId);
    }

    @Override
    public int getCount() {
        return mCategoryBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.shopping_main_list_category_grid_items, null);
            mViewHolder.sdvCategory = (SimpleDraweeView) convertView.findViewById(R.id.ic_category);
            mViewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
            mViewHolder.tvCategory.setTag(mCategoryBeanList.get(position).getHotLabels());
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        setupGridView(position);
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView sdvCategory;
        TextView tvCategory;
    }
}
