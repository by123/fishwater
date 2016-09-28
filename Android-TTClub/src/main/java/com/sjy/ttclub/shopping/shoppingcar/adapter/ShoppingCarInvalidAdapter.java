package com.sjy.ttclub.shopping.shoppingcar.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.PriceView;

import java.util.List;

/**
 * Created by chenjiawei on 2016/1/7.
 */
public class ShoppingCarInvalidAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans;
    private LinearLayout mPanicArea;

    public ShoppingCarInvalidAdapter(Context context, List<ShoppingCarGoodsBean> shoppingCarGoodsBeans) {
        this.mContext = context;
        this.mShoppingCarGoodsBeans = shoppingCarGoodsBeans;
    }

    @Override
    public int getCount() {
        return mShoppingCarGoodsBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mShoppingCarGoodsBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShoppingCarInvalidHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.shopping_car_goods_invalid_main, null);
            holder = new ShoppingCarInvalidHolder();
            holder.mGoodsImage = (SimpleDraweeView) convertView.findViewById(R.id.shopping_car_goods_items_img);
            holder.mGoodsTitle = (TextView) convertView.findViewById(R.id.shopping_car_goods_items_title);
            holder.mGoodsSpec = (TextView) convertView.findViewById(R.id.shopping_car_goods_items_spec);
            holder.mGoodsCount = (TextView) convertView.findViewById(R.id.shopping_car_goods_items_count);
            holder.mGoodsPrice = (PriceView) convertView.findViewById(R.id.shopping_car_goods_items_price);
            mPanicArea = (LinearLayout) convertView.findViewById(R.id.ll_panic);
            convertView.setTag(holder);
        } else {
            holder = (ShoppingCarInvalidHolder) convertView.getTag();
        }
        holder.mGoodsImage.setImageURI(Uri.parse(mShoppingCarGoodsBeans.get(position).getThumbUrl()));
        holder.mGoodsTitle.setText(mShoppingCarGoodsBeans.get(position).getTitle());
        holder.mGoodsSpec.setText(ResourceHelper.getString(R.string.shopping_car_goods_spec_text) + mShoppingCarGoodsBeans.get(position).getSpecName());
        holder.mGoodsCount.setText(ResourceHelper.getString(R.string.shopping_car_goods_count_text) + mShoppingCarGoodsBeans.get(position).getGoodsCount());
        holder.mGoodsPrice.setText(mShoppingCarGoodsBeans.get(position).getSalePrice());
        if (StringUtils.parseInt(mShoppingCarGoodsBeans.get(position).getInvalidState()) == 1) {
            mPanicArea.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(mShoppingCarGoodsBeans.get(position).getInvalidState()) == 2) {
            mPanicArea.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    public class ShoppingCarInvalidHolder {
        private SimpleDraweeView mGoodsImage;
        private TextView mGoodsTitle;
        private TextView mGoodsSpec;
        private TextView mGoodsCount;
        private PriceView mGoodsPrice;
    }
}
