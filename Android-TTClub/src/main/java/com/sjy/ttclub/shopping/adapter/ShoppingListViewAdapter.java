/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingCategoryBean;
import com.sjy.ttclub.bean.shop.ShoppingGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingPanicBean;
import com.sjy.ttclub.bean.shop.ShoppingTopicBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.model.ShoppingCategoryInfo;
import com.sjy.ttclub.shopping.model.ShoppingTopicInfo;
import com.sjy.ttclub.shopping.model.TemplateHolder;
import com.sjy.ttclub.shopping.widget.ShoppingCountdownView;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.PriceView;

import java.util.List;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class ShoppingListViewAdapter extends BaseAdapter implements View.OnClickListener, AdapterView
        .OnItemClickListener {

    private final static String TAG = "ShoppingListViewAdapter";

    public static final int ITEM_TYPE_GRID_VIEW = 0;
    public static final int ITEM_TYPE_SURPLUS = 1;
    public static final int ITEM_TYPE_GOODS = 2;
    public static final int ITEM_TYPE_TOPIC = 3;
    private static final int MAX_ITEM_TYPE = 4;

    private Context mContext;

    private List<TemplateHolder> mTemplateHolder;

    private ShoppingTopicInfo mTopicInfo = new ShoppingTopicInfo();

    private View mSurplusTopView;
    private View mTopicTopView;

    public ShoppingListViewAdapter(Context context, List<TemplateHolder> templateHolder) {
        mContext = context;
        mTemplateHolder = templateHolder;
    }

    public View stepCategoryGridView(List<ShoppingCategoryBean> list) {
        ShoppingCategoryGridViewAdapter adapter = new ShoppingCategoryGridViewAdapter(mContext, list);
        GridView gridView = new GridView(mContext);
        gridView.setNumColumns(list.size());
        gridView.setAdapter(adapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setOnItemClickListener(this);
        return gridView;
    }

    public View stepTopView(ShoppingPanicBean panicBean) {
        if (mSurplusTopView == null) {
            View view = View.inflate(mContext, R.layout.shopping_main_list_surplus_view, null);
            mSurplusTopView = view;
        }
        ShoppingCountdownView tvTime = (ShoppingCountdownView) mSurplusTopView.findViewById(R.id.surplus_countdown_time);
        String plusSeconds = panicBean.getSurplusSeconds();
        if (!StringUtils.isEmpty(plusSeconds)) {
            tvTime.setStartTime(StringUtils.parseLong(plusSeconds));
            tvTime.startCountdown();
        }
        return mSurplusTopView;
    }

    public View stepTopicView(List<ShoppingTopicBean> topicBeans) {
        if (mTopicTopView == null) {
            mTopicTopView = View.inflate(mContext, R.layout.shopping_main_list_topic_view, null);
        }
        View view = mTopicTopView;
        SimpleDraweeView sdvIcon = (SimpleDraweeView) view.findViewById(R.id.topic_ic);
        TextView tvTitle = (TextView) view.findViewById(R.id.topic_title);
        ShoppingCountdownView tvSurplus = (ShoppingCountdownView) view.findViewById(R.id.surplus_countdown_time);
        SimpleDraweeView sdvImage = (SimpleDraweeView) view.findViewById(R.id.topic_image);
        ShoppingTopicBean topicBean = topicBeans.get(0);
        final String thumbUrl = topicBean.getThumbUrl();
        if (!StringUtils.isEmpty(thumbUrl)) {
            sdvIcon.setImageURI(Uri.parse(thumbUrl));
        }
        final String title = topicBean.getTitle();
        if (!StringUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        final String surplus = topicBean.getSurplusSeconds();
        if (!StringUtils.isEmpty(surplus)) {
            tvSurplus.setStartTime(StringUtils.parseLong(surplus));
            tvSurplus.startCountdown();
        }
        final String imgUrl = topicBean.getColumn().get(0).getThumbUrl();
        if (!StringUtils.isEmpty(imgUrl)) {
            sdvImage.setAspectRatio(3);
            sdvImage.setImageURI(Uri.parse(imgUrl));
            sdvImage.setOnClickListener(this);
        }
        mTopicInfo.columnId = topicBean.getColumn().get(0).getId() + "";
        mTopicInfo.title = title;
        return mTopicTopView;
    }

    public View stepGoodsItems(List<ShoppingGoodsBean> list) {
        ShoppingGoodsBean shoppingGoodsBean = list.get(0);
        View view = View.inflate(mContext, R.layout.shopping_main_list_goods_items, null);
        SimpleDraweeView sdvGoodsThumb = (SimpleDraweeView) view.findViewById(R.id.thumb_goods);
        TextView tvTitle = (TextView) view.findViewById(R.id.title_goods);
        PriceView tvSPrice = (PriceView) view.findViewById(R.id.sale_price);
        PriceView tvMPrice = (PriceView) view.findViewById(R.id.market_price);
        TextView tvSale = (TextView) view.findViewById(R.id.sale_count);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.item_goods);
        layout.setOnClickListener(this);
        layout.setTag(R.id.tag_panic_goods_id, shoppingGoodsBean.getId());
        layout.setTag(R.id.tag_panic_goods_title, shoppingGoodsBean.getTitle());

        if (shoppingGoodsBean != null) {
            final String thumbUrl = shoppingGoodsBean.getThumbUrl();
            if (!StringUtils.isEmpty(thumbUrl)) {
                sdvGoodsThumb.setImageURI(Uri.parse(thumbUrl));
            }
            final String title = shoppingGoodsBean.getTitle();
            if (!StringUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            final int saleCount = shoppingGoodsBean.getSaleCount();
            tvSale.setText(getText(R.string.shopping_sale_count, saleCount));
            final String salePrice = shoppingGoodsBean.getSalePrice();
            if (!StringUtils.isEmpty(salePrice)) {
                tvSPrice.setText(salePrice);
            }
            final String marketPrice = shoppingGoodsBean.getMarketPrice();
            if (!StringUtils.isEmpty(marketPrice)) {
                tvMPrice.setText(marketPrice);
            }
        }
        return view;
    }

    private CharSequence getText(int resId, Object val) {
        return String.format(ResourceHelper.getString(resId), val);
    }

    private View createConvertView(int position) {
        int type = mTemplateHolder.get(position).getTempType();
        List items = mTemplateHolder.get(position).getItems();
        if (type == ITEM_TYPE_GRID_VIEW) {
            return stepCategoryGridView(items);
        } else if (type == ITEM_TYPE_SURPLUS) {
            return stepTopView((ShoppingPanicBean) items.get(0));
        } else if (type == ITEM_TYPE_GOODS) {
            return stepGoodsItems(items);
        } else if (type == ITEM_TYPE_TOPIC) {
            return stepTopicView(items);
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
        if (v.getId() == R.id.topic_image) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_SHOPPING_TOPIC_WINDOW;
            message.obj = mTopicInfo;
            MsgDispatcher.getInstance().sendMessage(message);
        } else if (v.getId() == R.id.item_goods) {

            //统计PRODUCT_SELL
            StatsModel.stats(StatsKeyDef.PRODUCT_SELL, "spec", v.getTag(R.id.tag_panic_goods_title) + "");

            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
            message.obj = String.valueOf(v.getTag(R.id.tag_panic_goods_id));
            MsgDispatcher.getInstance().sendMessage(message);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvCategory = (TextView) view.findViewById(R.id.tv_category);
        ShoppingCategoryInfo categoryInfo = new ShoppingCategoryInfo();
        categoryInfo.cateId = tvCategory.getTag().toString();
        categoryInfo.title = tvCategory.getText().toString();
        categoryInfo.categoryBean = (ShoppingCategoryBean) parent.getAdapter().getItem(position);
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_SHOPPING_CATEGORY_WINDOW;
        message.obj = categoryInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public void onVisibilityChanged(boolean visible) {
        //todo something??
    }
}
