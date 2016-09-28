package com.sjy.ttclub.shopping.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by zhxu on 2016/1/14.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryTopGridView extends GridView {

    public ShoppingCategoryTopGridView(Context context) {
        this(context, null, 0);
    }

    public ShoppingCategoryTopGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShoppingCategoryTopGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
