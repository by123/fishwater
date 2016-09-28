package com.sjy.ttclub.shopping.adapter;

import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingTopicListBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.PriceView;

import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class ShoppingTopicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;

    private RecyclerView mRecyclerView;

    private List<ShoppingTopicListBean> mTopicList;

    public final static int TYPE_LIST = 0;
    public final static int TYPE_STAGGER = 1;
    private int mCurrMode = TYPE_STAGGER;

    public ShoppingTopicRecyclerViewAdapter(RecyclerView recyclerView, List<ShoppingTopicListBean> topicList) {
        mRecyclerView = recyclerView;
        mTopicList = topicList;
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    public void switchMode(int currMode) {
        mCurrMode = currMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_STAGGER) {
            view = mLayoutInflater.inflate(R.layout.shopping_topic_masonry_item, parent, false);
            return new StaggerViewHolder(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.shopping_topic_list_items, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShoppingTopicListBean topicList = mTopicList.get(position);
        final int goodsId = topicList.getGoodsId();
        final String thumbUrl = topicList.getThumbUrl();
        final String title = topicList.getTitle();
        final String price = topicList.getSalePrice();
        final String marketPrice = topicList.getMarketPrice();
        final int saleCount = topicList.getSaleCount();
        switch (mCurrMode) {
            case TYPE_STAGGER:
                StaggerViewHolder staggerViewHolder = (StaggerViewHolder) holder;
                if (!StringUtils.isEmpty(thumbUrl)) {
                    staggerViewHolder.sdvImage.setImageURI(Uri.parse(thumbUrl));
                    staggerViewHolder.sdvImage.setAspectRatio(1);
                }
                if (!StringUtils.isEmpty(title)) {
                    staggerViewHolder.tvTitle.setText(title);
                }
                if (!StringUtils.isEmpty(price)) {
                    staggerViewHolder.tvPrice.setText(price);
                }
                staggerViewHolder.tvCount.setText(getText(R.string.shopping_sale_volume, saleCount));
                staggerViewHolder.layoutItem.setTag(goodsId);
                staggerViewHolder.layoutItem.setOnClickListener(this);
                break;
            case TYPE_LIST:
                ViewHolder mHolder = (ViewHolder) holder;
                if (!StringUtils.isEmpty(thumbUrl)) {
                    mHolder.sdvImage.setImageURI(Uri.parse(thumbUrl));
                    mHolder.sdvImage.setAspectRatio(1);
                }
                if (!StringUtils.isEmpty(title)) {
                    mHolder.tvTitle.setText(title);
                }
                if (!StringUtils.isEmpty(price)) {
                    mHolder.tvPrice.setText(price);
                }
                if (!StringUtils.isEmpty(marketPrice)) {
                    mHolder.tvMarketPrice.setText(marketPrice);
                }
                mHolder.tvCount.setText(getText(R.string.shopping_sale_volume, saleCount));
                mHolder.layoutItem.setTag(goodsId);
                mHolder.layoutItem.setOnClickListener(this);
                break;
        }
    }

    private CharSequence getText(int resId, Object val) {
        return String.format(ResourceHelper.getString(resId), val);
    }

    @Override
    public int getItemViewType(int position) {
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return TYPE_LIST;
        } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            return TYPE_STAGGER;
        }
        return TYPE_STAGGER;
    }

    @Override
    public int getItemCount() {
        return mTopicList.size();
    }

    @Override
    public void onClick(View v) {
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        message.obj = String.valueOf(v.getTag());
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public class StaggerViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdvImage;
        TextView tvTitle, tvCount;
        PriceView tvPrice;
        LinearLayout layoutItem;

        public StaggerViewHolder(View itemView) {
            super(itemView);
            sdvImage = (SimpleDraweeView) itemView.findViewById(R.id.masonry_item_img);
            tvTitle = (TextView) itemView.findViewById(R.id.masonry_item_title);
            tvPrice = (PriceView) itemView.findViewById(R.id.masonry_item_sale_price);
            tvCount = (TextView) itemView.findViewById(R.id.masonry_item_sale_count);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.masonry_item_layout);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdvImage;
        TextView tvTitle, tvCount;
        PriceView tvMarketPrice, tvPrice;
        LinearLayout layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            sdvImage = (SimpleDraweeView) itemView.findViewById(R.id.masonry_item_img);
            tvTitle = (TextView) itemView.findViewById(R.id.masonry_item_title);
            tvPrice = (PriceView) itemView.findViewById(R.id.masonry_item_sale_price);
            tvMarketPrice = (PriceView) itemView.findViewById(R.id.masonry_item_market_price);
            tvCount = (TextView) itemView.findViewById(R.id.masonry_item_sale_count);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.masonry_item_layout);
        }
    }
}
