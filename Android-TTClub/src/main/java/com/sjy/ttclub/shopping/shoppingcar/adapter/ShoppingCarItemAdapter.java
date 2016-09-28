package com.sjy.ttclub.shopping.shoppingcar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarItemHolder;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarState;

import java.util.List;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class ShoppingCarItemAdapter extends BaseAdapter {
    private int pageState = 1;
    private Context mContext;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans;
    private OnGoodsCostPriseListener mOnGoodsCostPriseListener;
    private ShoppingCarItemHolder.OnGoodsOperationListener mOnGoodsOperationListener;

    public ShoppingCarItemAdapter(Context context, List<ShoppingCarGoodsBean> shoppingCarGoodsBeans) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ShoppingCarItemHolder shoppingCarItemHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.shopping_car_list_item, null);
            shoppingCarItemHolder = new ShoppingCarItemHolder();
            shoppingCarItemHolder.initHolder(convertView, pageState);
            convertView.setTag(shoppingCarItemHolder);
        } else {
            shoppingCarItemHolder = (ShoppingCarItemHolder) convertView.getTag();
            if (shoppingCarItemHolder.getPageState() != pageState) {
                shoppingCarItemHolder = new ShoppingCarItemHolder();
                shoppingCarItemHolder.initHolder(convertView, pageState);
                convertView.setTag(shoppingCarItemHolder);
            }
        }
        shoppingCarItemHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ShoppingCarState.isSelected.get(position);
                ShoppingCarState.isSelected.put(position, !isChecked);
                mOnGoodsCostPriseListener.onChange();
            }
        });
        shoppingCarItemHolder.setHolderData(mShoppingCarGoodsBeans.get(position), ShoppingCarState.isSelected, position, mShoppingCarGoodsBeans);
        shoppingCarItemHolder.setItemClickCallBack(new ShoppingCarItemHolder.ItemClickCallBack() {
            @Override
            public void onItemClick(int position) {
                boolean isCheck = ShoppingCarState.isSelected.get(position);
                ShoppingCarState.isSelected.put(position, !isCheck);
                mOnGoodsCostPriseListener.onChange();
            }
        });
        shoppingCarItemHolder.setOnGoodsOperationListener(new ShoppingCarItemHolder.OnGoodsOperationListener() {
            @Override
            public void onGoodsOperationChange(List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans) {
                setShoppingCarGoodsBeans(mShoppingCarGoodsBeans);
                mOnGoodsOperationListener.onGoodsOperationChange(mShoppingCarGoodsBeans);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void setPageState(int pageState) {
        this.pageState = pageState;
    }

    public interface OnGoodsCostPriseListener {
        void onChange();
    }


    public void setOnGoodsCostPristListener(OnGoodsCostPriseListener mOnGoodsCostPriseListener) {
        this.mOnGoodsCostPriseListener = mOnGoodsCostPriseListener;
    }

    public void setShoppingCarGoodsBeans(List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans) {
        this.mShoppingCarGoodsBeans = mShoppingCarGoodsBeans;
    }

    public void setOnGoodsOperationListener(ShoppingCarItemHolder.OnGoodsOperationListener mOnGoodsOperationListener) {
        this.mOnGoodsOperationListener = mOnGoodsOperationListener;
    }
}
