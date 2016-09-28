package com.sjy.ttclub.shopping.product;

import android.os.Message;

import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.DefaultWindowController;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.shopping.product.comments.ProductCommentListWindow;

/**
 * Created by chenjiawei on 2015/12/28.
 */
public class ProductDetailController extends DefaultWindowController {

    public ProductDetailController() {
        registerMessage(MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW);
        registerMessage(MsgDef.MSG_SHOW_PRODUCT_COMMENTS_WINDOW);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW) {
            if (msg.obj != null) {
                showProductDetailWindow(msg.obj.toString());
            }
        } else if (msg.what == MsgDef.MSG_SHOW_PRODUCT_COMMENTS_WINDOW) {
            if (msg.obj instanceof String) {
                showProductCommentsWindow((String) msg.obj);
            }
        }

    }

    private void showProductDetailWindow(String goodsId) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ProductDetailWindow) {
            return;
        }

        ProductDetailWindow productDetailWindow = new ProductDetailWindow(mContext, this, goodsId);
        mWindowMgr.pushWindow(productDetailWindow);
    }

    private void showProductCommentsWindow(String goodsId) {
        AbstractWindow window = getCurrentWindow();
        if (window instanceof ProductCommentListWindow) {
            return;
        }

        ProductCommentListWindow listWindow = new ProductCommentListWindow(mContext, this, goodsId);
        mWindowMgr.pushWindow(listWindow);
    }
}
