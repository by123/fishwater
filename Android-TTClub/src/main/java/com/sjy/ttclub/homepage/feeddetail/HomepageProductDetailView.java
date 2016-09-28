package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.homepage.ArticleDetail;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/11/1.
 * Email: linhaihong@ta2she.com
 */
public class HomepageProductDetailView extends FrameLayout {
    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mPriceView;
    private TextView mSaleCountView;
    private TextView mBuyView;
    private ArticleDetail mProductDetail;

    public HomepageProductDetailView(Context context) {
        super(context);
        init();
    }

    private void init() {
        View parentView = View.inflate(getContext(), R.layout.homepage_detail_product, null);
        parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBuyButtonClick();
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addView(parentView, lp);

        mImageView = (SimpleDraweeView) parentView.findViewById(R.id.homepage_detail_product_img);
        mTitleView = (TextView) parentView.findViewById(R.id.homepage_detail_product_title);
        mPriceView = (TextView) parentView.findViewById(R.id.homepage_detail_product_price);
        mSaleCountView = (TextView) parentView.findViewById(R.id.homepage_detail_product_salecount);
        mBuyView = (TextView) parentView.findViewById(R.id.homepage_detail_product_buy);
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBuyButtonClick();
            }
        });
    }

    public void setupProductView(ArticleDetail feed) {
        mProductDetail = feed;
        if (feed.getData() == null) {
            return;
        }
        ArticleDetail.Data data = feed.getData();
        String url = data.getGoodsImageUrl();
        String title = data.getGoodsTitle();
        String price = data.getGoodsSalePrice();
        String scaleCount = data.getGoodsSaleCount();
        if(StringUtils.isNotEmpty(url)){
            mImageView.setImageURI(Uri.parse(url));
        }
        mTitleView.setText(title);
        mPriceView.setText("￥" + price);
        mSaleCountView.setText("已销售" + scaleCount);
    }

    private void handleBuyButtonClick() {
        if (mProductDetail.getData() == null) {
            return;
        }
        String goodsId = mProductDetail.getData().getGoodsId();
        if (StringUtils.isEmpty(goodsId)) {
            return;
        }
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
        msg.obj = goodsId;
        MsgDispatcher.getInstance().sendMessage(msg);
    }
}
