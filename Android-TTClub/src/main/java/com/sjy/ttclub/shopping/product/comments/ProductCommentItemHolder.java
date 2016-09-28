package com.sjy.ttclub.shopping.product.comments;

import android.net.Uri;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.shop.ShoppingReviewBean;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2016/1/2.
 * Email: linhaizhong@ta2she.com
 */
public class ProductCommentItemHolder {
    private SimpleDraweeView mUserIcon;
    private TextView mUserName;
    private TextView mCommentText;
    private TextView mProductDetail;
    private TextView mCommentTime;
    private RatingBar mRatingBar;

    public void initHolder(View view) {
        mUserIcon = (SimpleDraweeView) view.findViewById(R.id.sv_user_image);
        mUserName = (TextView) view.findViewById(R.id.tv_user_name);
        mCommentText = (TextView) view.findViewById(R.id.tv_comment_content);
        mRatingBar = (RatingBar) view.findViewById(R.id.rb_rating_bar);
        mCommentTime = (TextView) view.findViewById(R.id.tv_comment_time);
        mProductDetail = (TextView) view.findViewById(R.id.tv_product_description);
    }

    public void setupCommentView(ShoppingReviewBean reviewBean) {
        String iconUrl = reviewBean.getIconUrl();
        if (StringUtils.isNotEmpty(iconUrl)) {
            mUserIcon.setImageURI(Uri.parse(iconUrl));
        } else {
            mUserIcon.setImageResource(R.drawable.icon_user_image);
        }
        String name = reviewBean.getNickName();
        if (StringUtils.isEmpty(name)) {
            name = ResourceHelper.getString(R.string.shopping_product_anony);
        }
        mUserName.setText(name);
        mRatingBar.setRating(StringUtils.parseFloat(reviewBean.getRating()));
        mCommentText.setText(reviewBean.getContent());
        mCommentTime.setText(reviewBean.getTime());
        mProductDetail.setText(reviewBean.getSpec());
    }
}
