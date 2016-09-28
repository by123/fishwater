package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.shopping.model.ShoppingSelectorInfo;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaTextView;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryTopGridViewAdapter extends BaseAdapter {

    public final static String TAG = "ShoppingCategoryTopGridViewAdapter";

    private Context mContext;

    private ViewHolder mViewHolder;

    private List<ShoppingSelectorInfo> mSelectors;

    private LinearLayout mGridViewLayout;

    private PtrClassicFrameLayout mPtrLayout;

    private IShoppingCategoryTopGridViewAdapter mIShoppingListener;

    public ShoppingCategoryTopGridViewAdapter(Context context, List<ShoppingSelectorInfo> list, LinearLayout gridViewLayout, PtrClassicFrameLayout ptrLayout, IShoppingCategoryTopGridViewAdapter iShoppingListener) {
        mContext = context;
        mSelectors = list;
        mGridViewLayout = gridViewLayout;
        mPtrLayout = ptrLayout;
        mIShoppingListener = iShoppingListener;
    }

    private void setupGridView(final int position) {
        final String title = mSelectors.get(position).title;
        if (!StringUtils.isEmpty(title)) {
            mViewHolder.tvCategory.setText(title + " ×");
        }
        final int cateId = mSelectors.get(position).selectId;
        mViewHolder.tvCategory.setTag(cateId);
        mViewHolder.tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIShoppingListener != null) {
                    mIShoppingListener.onRefresh(mSelectors.get(position).type);
                }
                mSelectors.remove(mSelectors.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        mGridViewLayout.setVisibility(mSelectors.size() == 0 ? View.GONE : View.VISIBLE);
        ((FrameLayout.LayoutParams) mPtrLayout.getLayoutParams()).topMargin = mSelectors.size() == 0 ? ResourceHelper.getDimen(R.dimen.space_52) : ResourceHelper.getDimen(R.dimen.space_101);
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
            convertView = View.inflate(mContext, R.layout.shopping_category_top_view_grid_items, null);
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

    public interface IShoppingCategoryTopGridViewAdapter {
        void onRefresh(int type);
    }
}
