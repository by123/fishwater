package com.sjy.ttclub.shopping.logistics.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingOrderDetailGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderLogisticsBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.shopping.logistics.model.TemplateHolder;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class OrderLogisticsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<TemplateHolder> mTemplates;

    public final static int TYPE_TOP = 0, TYPE_GOODS = 1, TYPE_LOGISTICS = 2;

    private final int STATE_0 = 0, STATE_1 = 1, STATE_2 = 2, STATE_3 = 3, STATE_4 = 4, STATE_5 = 5, STATE_6 = 6, STATE_7 = 7;

    public void setCount(int count) {
        this.mCount = count;
    }

    private int mCount = 0;
    private boolean mIsShow = false;

    public OrderLogisticsRecyclerViewAdapter(Context context, List<TemplateHolder> templates) {
        mContext = context;
        mTemplates = templates;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("viewType", viewType + "");
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_TOP) {
            View view = mLayoutInflater.inflate(R.layout.order_logistics_top_view, parent, false);
            viewHolder = new TopViewHolder(view);
        } else if (viewType == TYPE_GOODS) {
            View view = mLayoutInflater.inflate(R.layout.order_logistics_goods_items, parent, false);
            viewHolder = new GoodsViewHolder(view);
        } else if (viewType == TYPE_LOGISTICS) {
            View view = mLayoutInflater.inflate(R.layout.order_logistics_items, parent, false);
            viewHolder = new LogisticsViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (mTemplates.get(position).getTempType()) {
            case TYPE_TOP:
                TopViewHolder topViewHolder = (TopViewHolder) holder;
                ShoppingOrderLogisticsBean orderLogisticsTopBean = (ShoppingOrderLogisticsBean) mTemplates.get(position).getItems();
                String logoUrl = orderLogisticsTopBean.getLogoUrl();
                String logisticsNo = orderLogisticsTopBean.getLogisticsNum();
                String logisticsCompany = orderLogisticsTopBean.getLogisticsCompany();
                setState(topViewHolder, orderLogisticsTopBean.getLogisticsStatus());
                if (!StringUtils.isEmpty(logoUrl)) {
                    topViewHolder.sdvLogo.setImageURI(Uri.parse(logoUrl));
                }
                if (!StringUtils.isEmpty(logisticsNo)) {
                    topViewHolder.tvNo.setText(getText(R.string.order_logistics_no, logisticsNo));
                }
                if (!StringUtils.isEmpty(logisticsCompany)) {
                    topViewHolder.tvTitle.setText(logisticsCompany);
                }
                break;
            case TYPE_LOGISTICS:
                LogisticsViewHolder logisticsViewHolder = (LogisticsViewHolder) holder;
                logisticsViewHolder.wvLogistics.getSettings().setJavaScriptEnabled(true);
                String url = HttpUrls.LOGISTICS_URL + mTemplates.get(position).getItems();
                logisticsViewHolder.wvLogistics.loadUrl(url);
                break;
            case TYPE_GOODS:
                GoodsViewHolder goodsViewHolder = (GoodsViewHolder) holder;
                ShoppingOrderDetailGoodsBean goodsBean = (ShoppingOrderDetailGoodsBean) mTemplates.get(position).getItems();
                String thumbUrl = goodsBean.getThumbUrl();
                String title = goodsBean.getTitle();
                String specName = goodsBean.getSpecName();
                String price = goodsBean.getGoodsPrice();
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
                goodsViewHolder.layoutTop.setVisibility(View.GONE);
                goodsViewHolder.layoutItems.setVisibility(View.GONE);
                goodsViewHolder.layoutBottom.setVisibility(View.GONE);
                if (position == 1) {
                    goodsViewHolder.layoutTop.setVisibility(View.VISIBLE);
                    goodsViewHolder.layoutItems.setVisibility(View.VISIBLE);
                }
                if (position == mCount - 1) {
                    goodsViewHolder.layoutBottom.setVisibility(View.VISIBLE);
                }
                if (mIsShow) {
                    goodsViewHolder.layoutItems.setVisibility(View.VISIBLE);
                    goodsViewHolder.layoutBottom.setVisibility(View.GONE);
                }
                goodsViewHolder.tvGoodsCount.setText(getText(R.string.order_detail_goods_count, goodsBean.getGoodsCount()));
                goodsViewHolder.tvCount.setText(getText(R.string.order_logistics_other_goods, mCount - 1));
                goodsViewHolder.layoutItems.setTag(goodsBean.getGoodsId());
                goodsViewHolder.layoutItems.setOnClickListener(this);
                goodsViewHolder.layoutBottom.setOnClickListener(this);
                break;
        }
    }

    private void setState(TopViewHolder topViewHolder, int state) {
        if (state == STATE_0) {
            getStateText(topViewHolder, R.string.order_logistics_state0);
        } else if (state == STATE_1) {
            getStateText(topViewHolder, R.string.order_logistics_state1);
        } else if (state == STATE_2) {
            getStateText(topViewHolder, R.string.order_logistics_state2);
        } else if (state == STATE_3) {
            getStateText(topViewHolder, R.string.order_logistics_state3);
        } else if (state == STATE_4) {
            getStateText(topViewHolder, R.string.order_logistics_state4);
        } else if (state == STATE_5) {
            getStateText(topViewHolder, R.string.order_logistics_state5);
        } else if (state == STATE_6) {
            getStateText(topViewHolder, R.string.order_logistics_state6);
        } else if (state == STATE_7) {
            getStateText(topViewHolder, R.string.order_logistics_state7);
        }
    }

    private void getStateText(TopViewHolder topViewHolder, int valResId) {
        topViewHolder.tvState.setText(String.format(ResourceHelper.getString(R.string.order_logistics_state), ResourceHelper.getString(valResId)));
    }

    private CharSequence getText(int resId, Object val) {
        return String.format(ResourceHelper.getString(resId), val);
    }

    @Override
    public int getItemCount() {
        return mTemplates.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTemplates.get(position).getTempType();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_items) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
            message.obj = String.valueOf(v.getTag());
            MsgDispatcher.getInstance().sendMessage(message);
        } else if (v.getId() == R.id.layout_bottom_items) {
            mIsShow = true;
            notifyDataSetChanged();
        }
    }

    public static class TopViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdvLogo;
        TextView tvState, tvNo, tvTitle;

        public TopViewHolder(View itemView) {
            super(itemView);
            sdvLogo = (SimpleDraweeView) itemView.findViewById(R.id.sdv_logo);
            tvState = (TextView) itemView.findViewById(R.id.tv_state);
            tvNo = (TextView) itemView.findViewById(R.id.tv_no);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public static class LogisticsViewHolder extends RecyclerView.ViewHolder {
        WebView wvLogistics;

        public LogisticsViewHolder(View itemView) {
            super(itemView);
            wvLogistics = (WebView) itemView.findViewById(R.id.wv_logistics);
        }
    }

    public static class GoodsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutTop, layoutBottom, layoutItems;
        SimpleDraweeView sdvImg;
        TextView tvGoodsName, tvGoodsSpec, tvGoodsPrice, tvGoodsCount, tvCount;

        public GoodsViewHolder(View itemView) {
            super(itemView);
            layoutTop = (LinearLayout) itemView.findViewById(R.id.layout_top_items);
            layoutBottom = (LinearLayout) itemView.findViewById(R.id.layout_bottom_items);
            layoutItems = (LinearLayout) itemView.findViewById(R.id.layout_items);
            sdvImg = (SimpleDraweeView) itemView.findViewById(R.id.sdv_img);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvGoodsSpec = (TextView) itemView.findViewById(R.id.tv_goods_spec);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
            tvGoodsCount = (TextView) itemView.findViewById(R.id.tv_goods_count);
            tvCount = (TextView) itemView.findViewById(R.id.tv_bottom_items_count);
        }
    }
}
