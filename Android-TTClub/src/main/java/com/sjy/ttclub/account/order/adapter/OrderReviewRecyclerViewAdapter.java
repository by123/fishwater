package com.sjy.ttclub.account.order.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.order.model.OrderReviewInfo;
import com.sjy.ttclub.bean.shop.ShoppingOrderDetailGoodsBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaTextView;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class OrderReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private OrderReviewInfo mReviewInfo;

    public OrderReviewRecyclerViewAdapter(Context context, OrderReviewInfo reviewInfo) {
        mReviewInfo = reviewInfo;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.order_review_goods_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShoppingOrderDetailGoodsBean goodsBean = mReviewInfo.goodsList.get(position);
        ViewHolder goodsViewHolder = (ViewHolder) holder;
        String thumbUrl = goodsBean.getThumbUrl();
        String title = goodsBean.getTitle();
        String specName = goodsBean.getSpecName();
        String price = goodsBean.getGoodsPrice();
        boolean isReview = goodsBean.getIsReview() == 1 ? true : false;
        if (!StringUtils.isEmpty(thumbUrl)) {
            goodsViewHolder.sdvImg.setImageURI(Uri.parse(thumbUrl));
        }
        if (!StringUtils.isEmpty(title)) {
            goodsViewHolder.tvGoodsName.setText(title);
        }
        if (!StringUtils.isEmpty(specName)) {
            goodsViewHolder.tvGoodsSpec.setText(specName);
        }
        if (!StringUtils.isEmpty(price)) {
            goodsViewHolder.tvGoodsPrice.setText(getText(R.string.order_detail_goods_price, price));
        }
//        goodsViewHolder.layoutItems.setOnClickListener(this);
        goodsViewHolder.atvReview.setTag(R.id.tag_thumb_url, thumbUrl);
        goodsViewHolder.atvReview.setTag(R.id.tag_order_goods_id, goodsBean.getOrderGoodsId());
        goodsViewHolder.atvReview.setOnClickListener(this);
        goodsViewHolder.atvReview.setText(ResourceHelper.getString(!isReview ? R.string.order_review_btn : R.string.order_review_btn_finish));
        goodsViewHolder.atvReview.setBackgroundResource(!isReview ? R.color.order_review_btn : R.color.order_review_btn_finish);

        goodsViewHolder.tvGoodsCount.setText(getText(R.string.order_detail_goods_count, goodsBean.getGoodsCount()));
    }

    private CharSequence getText(int resId, Object val) {
        return String.format(ResourceHelper.getString(resId), val);
    }

    @Override
    public int getItemCount() {
        return mReviewInfo.goodsList.size();
    }

    @Override
    public void onClick(View v) {

        //统计PRODUCT_REVIEWLIST_REVIEW
        StatsModel.stats(StatsKeyDef.PRODUCT_REVIEWLIST_REVIEW, "spec", "OrderGoodID:" + mReviewInfo.orderGoodsId);

        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_ORDER_REVIEW_WINDOW;
        mReviewInfo.orderGoodsId = (int) v.getTag(R.id.tag_order_goods_id);
        mReviewInfo.goodsImg = (String) v.getTag(R.id.tag_thumb_url);
        message.obj = mReviewInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutItems;
        SimpleDraweeView sdvImg;
        TextView tvGoodsName, tvGoodsSpec, tvGoodsPrice, tvGoodsCount;
        AlphaTextView atvReview;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutItems = (LinearLayout) itemView.findViewById(R.id.layout_items);
            atvReview = (AlphaTextView) itemView.findViewById(R.id.atv_review_go);
            sdvImg = (SimpleDraweeView) itemView.findViewById(R.id.sdv_img);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvGoodsSpec = (TextView) itemView.findViewById(R.id.tv_goods_spec);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
            tvGoodsCount = (TextView) itemView.findViewById(R.id.tv_goods_count);
        }
    }
}
