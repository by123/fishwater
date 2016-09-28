package com.sjy.ttclub.account.order.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingOrderListBean;
import com.sjy.ttclub.shopping.order.model.OrderResultInfo;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by zhxu on 2015/12/23.
 * Email:357599859@qq.com
 */
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private List<ShoppingOrderListBean> mOrderList;

    private final int STATE_1 = 1, STATE_2 = 2, STATE_3 = 3, STATE_4 = 4, STATE_5 = 5, STATE_6 = 6, STATE_7 = 7;

    private OrderListViewAdapter mAdapter;

    public OrderRecyclerViewAdapter(Context context, List<ShoppingOrderListBean> orderList) {
        mOrderList = orderList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.order_recycler_view_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShoppingOrderListBean orderList = mOrderList.get(position);
        ViewHolder mHolder = (ViewHolder) holder;
        final String orderNo = orderList.getOrderNo();
        String state = null;
        if (orderList.getOrderStatus() == STATE_1) {
            state = ResourceHelper.getString(R.string.order_state1);
        } else if (orderList.getOrderStatus() == STATE_2) {
            state = ResourceHelper.getString(R.string.order_state2);
        } else if (orderList.getOrderStatus() == STATE_3) {
            state = ResourceHelper.getString(R.string.order_state3);
        } else if (orderList.getOrderStatus() == STATE_4) {
            state = ResourceHelper.getString(R.string.order_state4);
        } else if (orderList.getOrderStatus() == STATE_5) {
            state = ResourceHelper.getString(R.string.order_state5);
        } else if (orderList.getOrderStatus() == STATE_6) {
            state = ResourceHelper.getString(R.string.order_state6);
        } else if (orderList.getOrderStatus() == STATE_7) {
            state = ResourceHelper.getString(R.string.order_state7);
        }
        if (!StringUtils.isEmpty(orderNo)) {
            mHolder.tvOrderNum.setText(orderNo);
        }
        if (!StringUtils.isEmpty(state)) {
            mHolder.tvState.setText(state);
        }
        mAdapter = new OrderListViewAdapter(mContext, orderList);
        mAdapter.setPayType(orderList.getPayType());
        mHolder.listOrder.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListView listOrder;
        TextView tvOrderNum, tvState;

        public ViewHolder(View itemView) {
            super(itemView);
            listOrder = (ListView) itemView.findViewById(R.id.items_order_list);
            tvOrderNum = (TextView) itemView.findViewById(R.id.items_order_no);
            tvState = (TextView) itemView.findViewById(R.id.items_order_state);
        }
    }
}
