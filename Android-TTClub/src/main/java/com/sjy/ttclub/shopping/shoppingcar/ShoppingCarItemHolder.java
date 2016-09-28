package com.sjy.ttclub.shopping.shoppingcar;

import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.widget.ShoppingCountdownView;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.PriceView;

import java.util.List;
import java.util.Map;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class ShoppingCarItemHolder implements View.OnClickListener {
    public CheckBox mCheckBox;
    private SimpleDraweeView mGoodsImage;
    private SimpleDraweeView mOperationGoodsImage;
    private TextView mGoodsTitle;
    private TextView mGoodsSpec;
    private TextView mGoodsPanicLeft;
    private TextView mGoodsPanicRight;
    private TextView mGoodsCount;
    private PriceView mGoodsPrice;
    private ShoppingCountdownView mCountdownView;
    private LinearLayout mShoppingCarGoods;
    private LinearLayout mShoppingCarOperation;
    private LinearLayout mShoppingCarArea;
    private int mPageState;
    private TextView mNumReduceBtn, mNumAddBtn, mNum;
    private ShoppingCarGoodsBean mShoppingCarGoodsBean;
    private int mPosition;
    private ItemClickCallBack mItemClickCallBack;
    private List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans;
    private OnGoodsOperationListener mOnGoodsOperationListener;

    public void initHolder(View view, int pageState) {
        mPageState = pageState;
        mCheckBox = (CheckBox) view.findViewById(R.id.shopping_car_ck);
        mShoppingCarGoods = (LinearLayout) view.findViewById(R.id.ll_shopping_car_goods);
        mShoppingCarOperation = (LinearLayout) view.findViewById(R.id.ll_shopping_car_goods_operation);
        mShoppingCarArea = (LinearLayout) view.findViewById(R.id.ll_shopping_car);
        if (pageState == ShoppingCarConstant.GOODSPAGE) {
            mShoppingCarGoods.setVisibility(View.VISIBLE);
            mShoppingCarOperation.setVisibility(View.GONE);
            mShoppingCarArea.setOnClickListener(this);
            mGoodsTitle = (TextView) view.findViewById(R.id.shopping_car_goods_items_title);
            mGoodsSpec = (TextView) view.findViewById(R.id.shopping_car_goods_items_spec);
            mGoodsPanicLeft = (TextView) view.findViewById(R.id.shopping_car_goods_items_panic_left);
            mGoodsPanicRight = (TextView) view.findViewById(R.id.shopping_car_goods_items_panic_right);
            mGoodsCount = (TextView) view.findViewById(R.id.shopping_car_goods_items_count);
            mGoodsPrice = (PriceView) view.findViewById(R.id.shopping_car_goods_items_price);
            mCountdownView = (ShoppingCountdownView) view.findViewById(R.id.shopping_car_goods_items_count_down);
            mGoodsImage = (SimpleDraweeView) view.findViewById(R.id.shopping_car_goods_items_img);
        } else if (pageState == ShoppingCarConstant.OPERATIONPAGE) {
            mShoppingCarGoods.setVisibility(View.GONE);
            mShoppingCarOperation.setVisibility(View.VISIBLE);
            mShoppingCarArea.setClickable(false);
            mNumReduceBtn = (TextView) view.findViewById(R.id.tv_num_reduce);
            mNumReduceBtn.setOnClickListener(this);
            mNumAddBtn = (TextView) view.findViewById(R.id.tv_num_add);
            mNumAddBtn.setOnClickListener(this);
            mNum = (TextView) view.findViewById(R.id.tv_num);
            mOperationGoodsImage = (SimpleDraweeView) view.findViewById(R.id.shopping_car_operation_items_img);
            mOperationGoodsImage.setOnClickListener(this);
        }
    }

    public void setHolderData(ShoppingCarGoodsBean shoppingCarGoodsBean, Map<Integer, Boolean> isSelected, int position, List<ShoppingCarGoodsBean> shoppingCarGoodsBeans) {
        this.mPosition = position;
        this.mShoppingCarGoodsBean = shoppingCarGoodsBean;
        this.mShoppingCarGoodsBeans = shoppingCarGoodsBeans;
        String iconUrl = shoppingCarGoodsBean.getThumbUrl();
        if (isSelected.size() == 0) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(isSelected.get(position));
        }
        if (mPageState == ShoppingCarConstant.GOODSPAGE) {
            if (StringUtils.isNotEmpty(iconUrl)) {
                mGoodsImage.setImageURI(Uri.parse(iconUrl));
            } else {
                mGoodsImage.setImageResource(R.drawable.icon_user_image);
            }
            mGoodsTitle.setText(shoppingCarGoodsBean.getTitle());
            mGoodsSpec.setText(ResourceHelper.getString(R.string.shopping_car_goods_spec_text) + shoppingCarGoodsBean.getSpecName());
            mGoodsCount.setText(ResourceHelper.getString(R.string.shopping_car_goods_count_text) + shoppingCarGoodsBean.getGoodsCount());
            mGoodsPrice.setText(shoppingCarGoodsBean.getSalePrice());
            int isPanicShopping = StringUtils.parseInt(shoppingCarGoodsBean.getIsPanicShopping());
            if (isPanicShopping == 1) {
                mCountdownView.setStartTime(StringUtils.parseLong(shoppingCarGoodsBean.getSurplusSeconds()));
                mCountdownView.startCountdown();
            } else {
                mGoodsPanicLeft.setVisibility(View.GONE);
                mGoodsPanicRight.setVisibility(View.GONE);
                mCountdownView.setVisibility(View.GONE);
            }
        } else if (mPageState == ShoppingCarConstant.OPERATIONPAGE) {
            if (StringUtils.isNotEmpty(iconUrl)) {
                mOperationGoodsImage.setImageURI(Uri.parse(iconUrl));
            } else {
                mOperationGoodsImage.setImageResource(R.drawable.icon_user_image);
            }
            mNum.setText(shoppingCarGoodsBean.getGoodsCount());
        }
    }

    public int getPageState() {
        return mPageState;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_car_operation_items_img:
                Message message = new Message();
                message.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
                message.obj = mShoppingCarGoodsBean.getGoodsId();
                MsgDispatcher.getInstance().sendMessage(message);
                break;
            case R.id.ll_shopping_car:
                mItemClickCallBack.onItemClick(mPosition);
                break;
            case R.id.tv_num_reduce:
                int reduceGoodsCount = StringUtils.parseInt(mShoppingCarGoodsBeans.get(mPosition).getGoodsCount());
                int reduceOpGoodsCount = reduceGoodsCount - 1;
                if (reduceOpGoodsCount > 0) {
                    mShoppingCarGoodsBeans.get(mPosition).setGoodsCount(reduceOpGoodsCount + "");
                    mOnGoodsOperationListener.onGoodsOperationChange(mShoppingCarGoodsBeans);
                }
                break;
            case R.id.tv_num_add:
                int addGoodsCount = StringUtils.parseInt(mShoppingCarGoodsBeans.get(mPosition).getGoodsCount());
                int addOpGoodsCount = addGoodsCount + 1;
                if (addOpGoodsCount > 0) {
                    mShoppingCarGoodsBeans.get(mPosition).setGoodsCount(addOpGoodsCount + "");
                    mOnGoodsOperationListener.onGoodsOperationChange(mShoppingCarGoodsBeans);
                }
                break;
            default:
                break;
        }
    }

    public interface ItemClickCallBack {
        void onItemClick(int position);
    }

    public void setItemClickCallBack(ItemClickCallBack itemClickCallBack) {
        this.mItemClickCallBack = itemClickCallBack;
    }

    public interface OnGoodsOperationListener {
        void onGoodsOperationChange(List<ShoppingCarGoodsBean> mShoppingCarGoodsBeans);
    }

    public void setOnGoodsOperationListener(OnGoodsOperationListener mOnGoodsOperationListener) {
        this.mOnGoodsOperationListener = mOnGoodsOperationListener;
    }
}
