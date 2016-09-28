package com.sjy.ttclub.shopping.orderdetail.widget;

import android.content.Context;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;

/**
 * Created by zhxu on 2016/1/5.
 * Email:357599859@qq.com
 */
public class OrderOperationDialog extends SimpleTextDialog {

    private Context mContext;

    public OrderOperationDialog(Context context) {
        super(context);
        mContext = context;

        initUI();
    }

    private void initUI() {
        setCanceledOnTouchOutside(true);
        setTitle(R.string.order_operation_title);
        setSubText(R.string.order_operation_subtext);
        addYesNoButton();
        setRecommendButton(SimpleTextDialog.ID_BUTTON_YES);
    }
}
