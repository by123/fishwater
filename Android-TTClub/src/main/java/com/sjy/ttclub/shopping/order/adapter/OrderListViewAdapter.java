/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.order.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingAddressBean;
import com.sjy.ttclub.bean.shop.ShoppingBalanceBean;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingAddress;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingDirectBalance;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.order.model.OrderAddressInfo;
import com.sjy.ttclub.shopping.order.model.OrderTemplateHolder;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class OrderListViewAdapter extends BaseAdapter implements View.OnClickListener {

    private final static String TAG = "OrderListViewAdapter";

    public static final int ITEM_TYPE_TOP = 0;
    public static final int ITEM_TYPE_GOODS = 1;
    public static final int ITEM_TYPE_STATS = 2;
    private static final int MAX_ITEM_TYPE = 3;

    private int mSum = 0;

    private Context mContext;

    private View mTopView;
    private View mStatsView;

    private List<OrderTemplateHolder> mTemplateHolder;

    private OrderAddressInfo mAddressInfo;

    public OrderListViewAdapter(Context context, List<OrderTemplateHolder> templateHolder) {
        mContext = context;
        mTemplateHolder = templateHolder;
        mAddressInfo = new OrderAddressInfo();
    }

    public void resetSum() {
        mSum = 0;
    }

    public View createStatsView(ShoppingBalanceBean balance) {
        if (mStatsView == null) {
//            View statsView = View.inflate(mContext, R.layout.order_main_goods_sum, null);//旧模板
            View statsView = View.inflate(mContext, R.layout.order_detail_bottom_view, null);
            mStatsView = statsView;
        }
        if (balance != null) {
            TextView tvSum = (TextView) mStatsView.findViewById(R.id.tv_goods_price);
            TextView tvFreight = (TextView) mStatsView.findViewById(R.id.tv_goods_freight);
            TextView tvGoodsSum = (TextView) mStatsView.findViewById(R.id.tv_goods_sum_pay);
            tvSum.setText(getText(R.string.shopping_price, Double.parseDouble(balance.getGoodsPrice())));
            String freight = balance.getPostageFee();
            if (Double.parseDouble(freight) == 0) {
                tvFreight.setText(ResourceHelper.getString(R.string.order_free_shipping));
            } else {
                tvFreight.setText(freight);
            }
            tvGoodsSum.setText(Html.fromHtml(String.format(ResourceHelper.getString(R.string.order_detail_goods_total), balance.getTotalPrice())));
        }
        return mStatsView;
    }

    public View createTopView(ShoppingAddressBean address) {
        if (mTopView == null) {
//            View topView = View.inflate(mContext, R.layout.order_main_address, null);//旧模板
            View topView = View.inflate(mContext, R.layout.order_detail_address, null);
            mTopView = topView;
        }
        LinearLayout layoutAdd = (LinearLayout) mTopView.findViewById(R.id.layout_address_add);
        LinearLayout layoutAddress = (LinearLayout) mTopView.findViewById(R.id.layout_address);
        TextView tvReceiver = (TextView) mTopView.findViewById(R.id.tv_receiver);
        TextView tvMobile = (TextView) mTopView.findViewById(R.id.tv_mobile);
        TextView tvAddress = (TextView) mTopView.findViewById(R.id.tv_address);
        ImageView ivAdd = (ImageView) mTopView.findViewById(R.id.tv_address_add);
        layoutAdd.setOnClickListener(this);

        if (address != null) {
            tvReceiver.setText(getText(R.string.order_goods_receiver, address.getReceiver()));
            tvMobile.setText(address.getMobile());
            tvAddress.setText(getText(R.string.order_goods_address, address.getDetailDistrict() + address.getDetailAddr()));
            ivAdd.setVisibility(View.GONE);
            tvAddress.setVisibility(View.VISIBLE);
            mAddressInfo.receiver = address.getReceiver();
            mAddressInfo.mobile = address.getMobile();
            mAddressInfo.detailAddress = address.getDetailAddr();
            mAddressInfo.districtId = address.getDistrictId() + "";
            mAddressInfo.cityId = address.getCityId() + "";
            mAddressInfo.provinceId = address.getProvinceId() + "";
        } else {
            ivAdd.setVisibility(View.VISIBLE);
            layoutAddress.setVisibility(View.GONE);
            tvAddress.setText(ResourceHelper.getString(R.string.order_no_address));
        }
        return mTopView;
    }

    public View createGoodsItems(ShoppingCarGoodsBean goodsInfo) {
//        View view = View.inflate(mContext, R.layout.order_main_goods, null); //旧模板
        View view = View.inflate(mContext, R.layout.order_detail_goods_items, null);
        if (goodsInfo != null) {
//            LinearLayout titleView = (LinearLayout) view.findViewById(R.id.order_main_goods_top);
//            TextView tvSum = (TextView) view.findViewById(R.id.order_main_goods_sum);
//            ImageView tvPanic = (ImageView) view.findViewById(R.id.order_main_goods_panic);
            SimpleDraweeView sdvGoodsThumb = (SimpleDraweeView) view.findViewById(R.id.sdv_img);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_goods_name);
            TextView tvSpec = (TextView) view.findViewById(R.id.tv_goods_spec);
            TextView tvCount = (TextView) view.findViewById(R.id.tv_goods_count);
            TextView tvPrice = (TextView) view.findViewById(R.id.tv_goods_price);
//            mSum += StringUtils.parseInt(goodsInfo.getGoodsCount());
//            tvSum.setText(getText(R.string.order_goods_sum, mSum));
//            if (position == 1) {
//                titleView.setVisibility(View.VISIBLE);
//            } else {
//                titleView.setVisibility(View.GONE);
//            }
            final String thumbUrl = goodsInfo.getThumbUrl();
            if (!StringUtils.isEmpty(thumbUrl)) {
                sdvGoodsThumb.setImageURI(Uri.parse(thumbUrl));
            }
            final String title = goodsInfo.getTitle();
            if (!StringUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            final String specName = goodsInfo.getSpecName();
            if (!StringUtils.isEmpty(specName)) {
                tvSpec.setText(getText(R.string.order_goods_spec_name, specName));
            }
//            if (StringUtils.parseInt(goodsInfo.getIsPanicShopping()) == 1) {
//                tvPanic.setVisibility(View.VISIBLE);
//            } else {
//                tvPanic.setVisibility(View.GONE);
//            }
            tvCount.setText(getText(R.string.order_detail_goods_count, goodsInfo.getGoodsCount()));
            tvPrice.setText(getText(R.string.shopping_price, Double.parseDouble(goodsInfo.getSalePrice())));
        }
        return view;
    }

    private CharSequence getText(int resId, Object val) {
        return String.format(ResourceHelper.getString(resId), val);
    }

    private View createConvertView(int position) {
        int type = mTemplateHolder.get(position).getTempType();
        List items = mTemplateHolder.get(position).getItems();
        if (type == ITEM_TYPE_TOP) {
            JTBShoppingAddress jtbShoppingAddress = (JTBShoppingAddress) items.get(0);
            return createTopView(jtbShoppingAddress.getData());
        } else if (type == ITEM_TYPE_GOODS) {
            ShoppingCarGoodsBean carGoodsBean = (ShoppingCarGoodsBean) items.get(0);
            return createGoodsItems(carGoodsBean);
        } else if (type == ITEM_TYPE_STATS) {
            JTBShoppingDirectBalance jtbShoppingDirectBalance = (JTBShoppingDirectBalance) items.get(0);
            return createStatsView(jtbShoppingDirectBalance.getData());
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTemplateHolder.get(position).getTempType();
    }

    @Override
    public int getViewTypeCount() {
        return MAX_ITEM_TYPE;
    }

    @Override
    public int getCount() {
        return mTemplateHolder.size();
    }

    @Override
    public Object getItem(int position) {
        return mTemplateHolder.get(position).getItems();
    }

    @Override
    public long getItemId(int position) {
        return mTemplateHolder.get(position).getTempType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createConvertView(position);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_ORDER_ADDRESS_WINDOW;
        message.obj = mAddressInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
