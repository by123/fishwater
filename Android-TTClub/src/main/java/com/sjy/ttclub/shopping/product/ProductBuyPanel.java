package com.sjy.ttclub.shopping.product;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsBean;
import com.sjy.ttclub.bean.shop.ShoppingCarParamBean;
import com.sjy.ttclub.bean.shop.ShoppingGoodsDetailBean;
import com.sjy.ttclub.bean.shop.ShoppingSKUBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.shopping.ShoppingConstant;
import com.sjy.ttclub.shopping.order.model.OrderBalanceInfo;
import com.sjy.ttclub.shopping.product.widget.FlowRadioGroup;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarConstant;
import com.sjy.ttclub.shopping.shoppingcar.ShoppingCarRequest;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.BasePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiawei on 2015/12/29.
 */
public class ProductBuyPanel extends BasePanel implements View.OnClickListener {
    private Context mContext;
    private SimpleDraweeView mProductImage;
    private TextView mClose, mChooseSpec, mChooseSpecCount;
    private FlowRadioGroup mRadioBtn;
    private TextView mNumReduce, mNumAdd, mNum;
    private TextView mAddBuyCar, mBuy, mProductPrice;
    private ShoppingGoodsDetailBean mData;
    private List<ShoppingSKUBean> mProductSpInfo;
    private RadioButton mRadioButton;
    private LinearLayout.LayoutParams mRadioLayout;
    private int mProductNum = 1;
    private boolean mRadioCheckState = false;
    private int mPanelType = 0;
    private ShoppingCarRequest mShoppingCarRequest;
    private int mRadioCheckId;

    public ProductBuyPanel(Context context, ShoppingGoodsDetailBean data, int panelType) {
        super(context, false);
        this.mContext = context;
        this.mData = data;
        this.mPanelType = panelType;
        initPanel();
    }

    @Override
    protected View onCreateContentView() {
        View view = View.inflate(mContext, R.layout.shopping_product_detail_buy_main, null);
        initUI(view);
        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.window_tv_close:
                hidePanel();
                break;
            case R.id.tv_num_reduce:
                if (mRadioCheckState) {
                    int reduceTmp = getProductNum() - 1;
                    if (reduceTmp >= 1) {
                        mNum.setText(reduceTmp + "");
                        setProductNum(reduceTmp);
                    }
                }
                break;
            case R.id.tv_num_add:
                if (mRadioCheckState) {
                    int addTmp = getProductNum() + 1;
                    mNum.setText(addTmp + "");
                    setProductNum(addTmp);
                }
                break;
            case R.id.window_btn_add_car:
                if (mRadioCheckState) {
                    hidePanel();
                    ShoppingCarParamBean shoppingCarParamBean = new ShoppingCarParamBean();
                    shoppingCarParamBean.setGoodsId(mData.getGoodsId() + "");
                    shoppingCarParamBean.setSpecId(mRadioCheckId + "");
                    shoppingCarParamBean.setGoodsCount(mNum.getText() + "");
                    sendAddShoppingCarRequest(shoppingCarParamBean);
                } else {
                    ToastHelper.showToast(mContext, R.string.shopping_product_add_shopping_car_remind_text);
                }
                break;
            case R.id.window_btn_confirm:
                if (mPanelType == ShoppingConstant.PANEL_TYPE_BUY_CAR) {

                } else if (mPanelType == ShoppingConstant.PANEL_TYPE_BUY) {
                    if (!mRadioCheckState) {
                        ToastHelper.showToast(mContext, R.string.shopping_product_add_shopping_car_remind_text);
                    } else {
                        hidePanel();
                        if (AccountManager.getInstance().isLogin()) {
                            Message message = Message.obtain();
                            message.what = MsgDef.MSG_SHOW_ORDER_WINDOW;
                            message.obj = encapsulation();
                            MsgDispatcher.getInstance().sendMessage(message);
                        } else {
                            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private OrderBalanceInfo encapsulation() {
        OrderBalanceInfo balanceInfo = new OrderBalanceInfo();
        balanceInfo.goodsCount = getProductNum();
        balanceInfo.goodsId = mData.getGoodsId();
        if (StringUtils.parseInt(mData.getIsPanicShopping()) == 0) {
            balanceInfo.panicShoppingId = 0;
        } else {
            balanceInfo.panicShoppingId = mData.getPanicShopping().getPanicShoppingId();
        }
        balanceInfo.specId = mRadioCheckId;

        List<ShoppingCarGoodsBean> list = new ArrayList<>();
        ShoppingCarGoodsBean carGoodsBean = new ShoppingCarGoodsBean();
        carGoodsBean.setGoodsCount(getProductNum() + "");
        carGoodsBean.setGoodsId(mData.getGoodsId() + "");
        carGoodsBean.setSalePrice(mData.getSalePrice());
        carGoodsBean.setSpecName(mChooseSpec.getText() + "");
        carGoodsBean.setThumbUrl(mData.getThumbUrl());
        carGoodsBean.setIsPanicShopping(mData.getIsPanicShopping());
        carGoodsBean.setDescription(mData.getDescription());
        carGoodsBean.setTitle(mData.getTitle());
        list.add(carGoodsBean);

        balanceInfo.setList(list);

        return balanceInfo;
    }

    private void initUI(View view) {
        mProductImage = (SimpleDraweeView) view.findViewById(R.id.window_product_img);
        mClose = (TextView) view.findViewById(R.id.window_tv_close);
        mClose.setOnClickListener(this);
        mChooseSpec = (TextView) view.findViewById(R.id.window_choose);
        mChooseSpecCount = (TextView) view.findViewById(R.id.window_count);
        mRadioBtn = (FlowRadioGroup) view.findViewById(R.id.window_btn_spec);
        mNumReduce = (TextView) view.findViewById(R.id.tv_num_reduce);
        mNumReduce.setOnClickListener(this);
        mNumAdd = (TextView) view.findViewById(R.id.tv_num_add);
        mNumAdd.setOnClickListener(this);
        mAddBuyCar = (TextView) view.findViewById(R.id.window_btn_add_car);
        mAddBuyCar.setOnClickListener(this);
        mBuy = (TextView) view.findViewById(R.id.window_btn_confirm);
        mBuy.setOnClickListener(this);
        mProductPrice = (TextView) view.findViewById(R.id.window_price);
        mNum = (TextView) view.findViewById(R.id.tv_num);
        if (mPanelType == ShoppingConstant.PANEL_TYPE_BUY) {
            mBuy.setText("确定购买");
            mAddBuyCar.setVisibility(View.GONE);
        }
        if (mPanelType == ShoppingConstant.PANEL_TYPE_BUY_CAR) {
            mAddBuyCar.setText("确定加入");
            mBuy.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (this.mData != null) {
            mNum.setText(1 + "");
            mProductSpInfo = this.mData.getSku();
            mProductImage.setImageURI(Uri.parse(mData.getThumbUrl()));
            initFlowRadioGroup();
            mRadioBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for (int i = 0; i < mProductSpInfo.size(); i++) {
                        mRadioCheckId = mProductSpInfo.get(i).getSkuId();
                        if (checkedId == mRadioCheckId) {
                            mRadioCheckState = true;
                            mChooseSpec.setText(mProductSpInfo.get(i).getName());
                            mChooseSpecCount.setText("剩余" + mProductSpInfo.get(i).getSurplusCount() + "件");
                            mProductPrice.setText("￥" + mProductSpInfo.get(i).getPrice() + "");
                            break;
                        }
                    }
                }
            });
        }
    }

    private void initFlowRadioGroup() {
        int width = HardwareUtil.windowWidth;
        int height = HardwareUtil.windowHeight;
        for (int i = 0; i < this.mProductSpInfo.size(); i++) {
            mRadioButton = new RadioButton(this.mContext);
            if (mProductSpInfo.size() == 1) {
                mChooseSpec.setText("已选：" + mProductSpInfo.get(i).getName());
                mChooseSpecCount.setText("库存" + mProductSpInfo.get(i).getSurplusCount() + "件");
                mProductPrice.setText("￥" + mProductSpInfo.get(i).getPrice() + "");
                mRadioButton.setChecked(true);
                mRadioCheckState = true;
                mRadioCheckId = mProductSpInfo.get(i).getSkuId();
            }
            mRadioButton.setId(mProductSpInfo.get(i).getSkuId());
            mRadioButton.setText(mProductSpInfo.get(i).getName());
            mRadioButton.setBackgroundResource(R.drawable.buy_panel_radio);
            mRadioButton.setTextAppearance(mContext, R.style.buyWindowRadioText);
            if (width <= 480 && height <= 800) {
                mRadioLayout = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams
                        .WRAP_CONTENT);
                mRadioLayout.setMargins(15, 20, 0, 20);
                mRadioButton.setPadding(15, 8, 15, 8);
            }
            if (width < 720 && height > 800) {
                mRadioLayout = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams
                        .WRAP_CONTENT);
                mRadioLayout.setMargins(15, 20, 0, 20);
                mRadioButton.setPadding(15, 8, 15, 8);
            }
            if (width >= 720 && height > 800 && height <= 1280) {
                mRadioLayout = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams
                        .WRAP_CONTENT);
                mRadioLayout.setMargins(20, 20, 0, 20);
                mRadioButton.setPadding(25, 15, 25, 15);
            }
            if (width > 720 && height > 1280) {
                mRadioLayout = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams
                        .WRAP_CONTENT);
                mRadioLayout.setMargins(30, 30, 0, 30);
                mRadioButton.setPadding(35, 20, 35, 20);
            }
//            mRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40));
            mRadioButton.setLayoutParams(mRadioLayout);
            mRadioButton.setButtonDrawable(mContext.getResources().getDrawable(android.R.color.transparent));
            mRadioButton.setGravity(Gravity.CENTER);
            mRadioButton.setTextSize(14);
            mRadioBtn.addView(mRadioButton);
        }
    }

    private void sendAddShoppingCarRequest(ShoppingCarParamBean shoppingCarParamBean) {
        mShoppingCarRequest = new ShoppingCarRequest(shoppingCarParamBean);
        mShoppingCarRequest.startShoppingCarAddRequest(new ShoppingCarRequest.ShoppingCarRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerAddShoppingCarFailed(errorType);
            }

            @Override
            public void onResultSuccess() {
                ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_car_add_success), Toast
                        .LENGTH_SHORT);
                Notification notification = Notification.obtain(NotificationDef.N_SHOPPING_CAR_COUNT_CHANGE);
                NotificationCenter.getInstance().notify(notification);
            }
        });
    }

    /**
     * 数据异常处理
     *
     * @param errorCode
     */
    private void handlerAddShoppingCarFailed(int errorCode) {
        if (errorCode == ShoppingCarConstant.ERROR_TYPE_FAIL) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_car_error_type_fail), Toast
                    .LENGTH_SHORT);
            return;
        }
        if (errorCode == ShoppingCarConstant.ERROR_TYPE_LESS) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_car_error_type_less), Toast
                    .LENGTH_SHORT);
            return;
        }
        if (errorCode == ShoppingCarConstant.ERROR_TYPE_NO_HAVE) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_car_error_type_no_have), Toast
                    .LENGTH_SHORT);
            return;
        }
        if (errorCode == ShoppingCarConstant.ERROR_TYPE_TOO_MUCH) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.shopping_car_error_type_too_much), Toast
                    .LENGTH_SHORT);
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_network_error_retry),
                    Toast.LENGTH_SHORT);
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
    }

    public int getProductNum() {
        return mProductNum;
    }

    public void setProductNum(int mProductNum) {
        this.mProductNum = mProductNum;
    }
}
