package com.sjy.ttclub.shopping.order.widget;

import android.content.Context;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.dialog.GenericDialog;

/**
 * Created by zhxu on 2016/1/5.
 * Email:357599859@qq.com
 */
public class OrderDialog extends GenericDialog {

    private View mView;

    private Context mContext;

    public final int WECHAT = 2, ALIPAY = 3;
    public int mPayType = ALIPAY;

    public OrderDialog(Context context, View view) {
        super(context);
        mView = view;
        mContext = context;

        initUI();
    }

    private void initUI() {
        setCanceledOnTouchOutside(true);
        String title = ResourceHelper.getString(R.string.order_pay_select_text);
        String cancel = ResourceHelper.getString(R.string.order_pay_cancel_text);
        String pos = ResourceHelper.getString(R.string.order_pay_pos_text);
        setTitle(title);
        addContentView(mView);
        addYesNoButton(pos, cancel);
        setRecommendButton(GenericDialog.ID_BUTTON_YES);
    }
}
