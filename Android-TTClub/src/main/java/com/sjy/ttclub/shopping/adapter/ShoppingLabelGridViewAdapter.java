package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingLabelBean;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaTextView;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class ShoppingLabelGridViewAdapter extends BaseAdapter {

    public final static String TAG = "ShoppingLabelGridViewAdapter";

    private Context mContext;

    private ViewHolder mViewHolder;

    private List<ShoppingLabelBean> mSelectors;

    private IShoppingLabelGridViewAdapter mIShoppingListener;

    public ShoppingLabelGridViewAdapter(Context context, List<ShoppingLabelBean> list, IShoppingLabelGridViewAdapter iShoppingListener) {
        mContext = context;
        mSelectors = list;
        mIShoppingListener = iShoppingListener;
    }

    private void setupGridView(final int position) {
        final String title = mSelectors.get(position).getName();
        if (!StringUtils.isEmpty(title)) {
            mViewHolder.tvCategory.setText(title);
        }
        final int cateId = mSelectors.get(position).getHid();
        mViewHolder.tvCategory.setTag(cateId);
        mViewHolder.tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIShoppingListener != null) {
                    mIShoppingListener.onSelected(mSelectors.get(position));
                }
            }
        });
    }

    @Override
    public int getCount() {
        return mSelectors.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelectors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.shopping_label_top_view_grid_items, null);
            mViewHolder.tvCategory = (AlphaTextView) convertView.findViewById(R.id.tv_category);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        setupGridView(position);
        return convertView;
    }

    class ViewHolder {
        AlphaTextView tvCategory;
    }

    public interface IShoppingLabelGridViewAdapter {
        void onSelected(ShoppingLabelBean bean);
    }
}
