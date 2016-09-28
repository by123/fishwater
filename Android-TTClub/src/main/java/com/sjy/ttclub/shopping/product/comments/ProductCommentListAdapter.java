package com.sjy.ttclub.shopping.product.comments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingReviewBean;

import java.util.ArrayList;

/**
 * Created by chenjiawei on 2015/12/31.
 */
public class ProductCommentListAdapter extends ArrayAdapter<ShoppingReviewBean> {

    public ProductCommentListAdapter(Context context, ArrayList<ShoppingReviewBean> reviewInfos) {
        super(context, 0, reviewInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductCommentItemHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.shopping_product_detail_comment_item, null);
            holder = new ProductCommentItemHolder();
            holder.initHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ProductCommentItemHolder) convertView.getTag();
        }
        holder.setupCommentView(getItem(position));
        return convertView;
    }
}
