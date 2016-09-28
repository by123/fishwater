package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2015/12/30.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryFilterLeftAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> mList = new ArrayList<>();

    private int mCurrPosition = 0;

    public ShoppingCategoryFilterLeftAdapter(Context context) {
        mContext = context;
        mList.add(ResourceHelper.getString(R.string.shopping_category_filter_cate));
        mList.add(ResourceHelper.getString(R.string.shopping_category_filter_brand));
    }

    public void noBrand() {
        mList.remove(1);
        notifyDataSetChanged();
    }

    public void focus(TextView v) {
        mCurrPosition = (int) v.getTag();
        notifyDataSetChanged();
    }

    public View stepLeftView(int position) {
        View view = View.inflate(mContext, R.layout.shopping_category_filter_left_items, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_category);
        textView.setText(mList.get(position));
        if (position == mCurrPosition) {
            textView.setSelected(true);
        } else {
            textView.setSelected(false);
        }
        textView.setTag(position);
        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return stepLeftView(position);
    }
}
