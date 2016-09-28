package com.sjy.ttclub.account.order.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingOrderGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingOrderListBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhxu on 2016/1/7.
 * Email:357599859@qq.com
 */
public class OrderListViewAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;

    private ShoppingOrderListBean mOrderListBean;

    private final int WECHAT = 2, ALIPAY = 3;

    private int mPayType;

    public OrderListViewAdapter(Context context, ShoppingOrderListBean orderListBean) {
        mContext = context;
        mOrderListBean = orderListBean;
    }

    public void setPayType(int payType) {
        mPayType = payType;
    }

    private void stepListView(ViewHolder viewHolder) {
        ShoppingOrderGoodsBean goodsBean = mOrderListBean.getGoods().get(0);
        final String thumbUrl = goodsBean.getThumbUrl();
        final String title = goodsBean.getTitle();
        final String price = goodsBean.getGoodsPrice();
        String payType = null;
        if (mPayType == WECHAT) {
            payType = ResourceHelper.getString(R.string.order_pay_wx_text);
        } else if (mPayType == ALIPAY) {
            payType = ResourceHelper.getString(R.string.order_pay_zfb_text);
        }
        if (!StringUtils.isEmpty(thumbUrl)) {
            viewHolder.sdvImage.setImageURI(Uri.parse(thumbUrl));
            viewHolder.sdvImage.setAspectRatio(1);
        }
        if (!StringUtils.isEmpty(title)) {
            viewHolder.tvTitle.setText(title);
        }
        if (!StringUtils.isEmpty(title)) {
            viewHolder.tvTitle.setText(title);
        }
        viewHolder.tvCount.setText(String.format(ResourceHelper.getString(R.string.order_goods_count), mOrderListBean.getGoods().size()));
        if (!StringUtils.isEmpty(price)) {
            viewHolder.tvOther.setText(Html.fromHtml(String.format(ResourceHelper.getString(R.string.order_list_items_other), mOrderListBean.getOrderPrice(), payType)));
        }
        viewHolder.layoutItems.setOnClickListener(this);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return mOrderListBean.getGoods().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.order_list_items, null);
            viewHolder.layoutItems = (LinearLayout) convertView.findViewById(R.id.items_order_layout);
            viewHolder.sdvImage = (SimpleDraweeView) convertView.findViewById(R.id.items_order_img);
            viewHolder.tvCount = (TextView) convertView.findViewById(R.id.items_order_count);
            viewHolder.tvOther = (TextView) convertView.findViewById(R.id.items_order_other);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.items_order_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        stepListView(viewHolder);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        OrderResultInfo orderResultInfo = new OrderResultInfo();
        orderResultInfo.accountsPayable = mOrderListBean.getOrderPrice();
        orderResultInfo.orderNum = mOrderListBean.getOrderNo();
        orderResultInfo.orderId = mOrderListBean.getOrderId() + "";
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_ORDER_DETAIL_WINDOW;
        message.obj = orderResultInfo;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    class ViewHolder {
        LinearLayout layoutItems;
        SimpleDraweeView sdvImage;
        TextView tvTitle, tvOther, tvCount;
    }
}
