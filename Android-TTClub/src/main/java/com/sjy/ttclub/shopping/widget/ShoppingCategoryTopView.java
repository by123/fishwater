package com.sjy.ttclub.shopping.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingLabelBean;
import com.sjy.ttclub.shopping.adapter.ShoppingLabelGridViewAdapter;

import java.util.List;

/**
 * Created by zhxu on 2015/12/30.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryTopView extends RelativeLayout {

    private Activity mContext;

    private ShoppingCategoryTopGridView mGridView;
    private ShoppingLabelGridViewAdapter mAdapter;
    private List<ShoppingLabelBean> mList;

    public ShoppingCategoryTopView(Context context) {
        this(context, null, 0);
    }

    public ShoppingCategoryTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShoppingCategoryTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = (Activity) context;
        inflate(context, R.layout.shopping_category_top_view, this);
        mGridView = (ShoppingCategoryTopGridView) findViewById(R.id.grid_view);
    }

    public void setData(List<ShoppingLabelBean> selectors, ShoppingLabelGridViewAdapter.IShoppingLabelGridViewAdapter listen) {

        mList = selectors;

        if (mAdapter == null) {
            mAdapter = new ShoppingLabelGridViewAdapter(mContext, mList, listen);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}