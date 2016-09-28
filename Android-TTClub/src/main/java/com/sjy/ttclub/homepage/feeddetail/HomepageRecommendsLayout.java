package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.homepage.ArticleRecommendInfo;
import com.sjy.ttclub.bean.homepage.ArticleRecommends;

import java.util.List;

/**
 * Created by linhz on 2015/12/8.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageRecommendsLayout extends LinearLayout implements View.OnClickListener {
    private LinearLayout mRecommendLayout;
    private RecommendListener mRecommendListener;

    public HomepageRecommendsLayout(Context context) {
        super(context);
        initViews();
    }

    private void initViews() {
        setOrientation(LinearLayout.VERTICAL);
        addRecommendTitle();
        addRecommendMoreTitle();
        addDivider();
        addRecommendContents();
    }

    private void addRecommendTitle() {
        TextView textView = new TextView(getContext());
        textView.setSingleLine();
        textView.getPaint().setFakeBoldText(true);
        textView.setText(getResources().getString(R.string.homepage_detail_more_article_title));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.space_20));
        textView.setTextColor(getResources().getColor(R.color.black_dark));
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_70);
        this.addView(textView, lp);
    }

    private void addRecommendMoreTitle() {
        TextView textView = new TextView(getContext());
        textView.setSingleLine();
        textView.setText(getResources().getString(R.string.homepage_detail_more_article));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.space_14));
        textView.setTextColor(getResources().getColor(R.color.black));
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_12);
        lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.space_16);
        this.addView(textView, lp);
    }


    private void addDivider() {
        View divider = new View(getContext());
        divider.setBackgroundColor(getResources().getColor(R.color.homepage_list_divider_color));
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .divider_height));
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_14);
        lp.rightMargin = lp.leftMargin;
        this.addView(divider, lp);
    }

    private void addRecommendContents() {
        mRecommendLayout = new LinearLayout(getContext());
        mRecommendLayout.setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_14);
        lp.rightMargin = lp.leftMargin;
        this.addView(mRecommendLayout, lp);
    }

    public void setupRecommendView(ArticleRecommends recommends) {
        List<ArticleRecommendInfo> list = recommends.getData();
        mRecommendLayout.removeAllViewsInLayout();
        for (ArticleRecommendInfo info : list) {
            addRecommendItemView(info);
        }
    }

    private void addRecommendItemView(ArticleRecommendInfo info) {
        View view = View.inflate(getContext(), R.layout.homepage_detail_recommend_item_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_14);
        lp.bottomMargin = lp.topMargin;
        mRecommendLayout.addView(view, lp);

        View maskView = view.findViewById(R.id.homepage_detail_recommend_mask);
        maskView.setOnClickListener(this);
        maskView.setTag(info);

        if (info.getImageUrl() != null) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.homepage_detail_recommend_image);
            draweeView.setImageURI(Uri.parse(info.getImageUrl()));
        }
        TextView titleView = (TextView) view.findViewById(R.id.homepage_detail_recommend_title);
        titleView.setText(info.getTitle());

        TextView authorView = (TextView) view.findViewById(R.id.homepage_detail_recommend_author);
        authorView.setText(info.getUserNick());

        TextView viewNumView = (TextView) view.findViewById(R.id.homepage_detail_recommend_view_num);
        String numText = getResources().getString(R.string.homepage_detail_recommend_view_num);
        numText = numText.replace("#num#", String.valueOf(info.getReadCount()));
        viewNumView.setText(numText);
    }

    public void setRecommendListener(RecommendListener listener) {
        mRecommendListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof ArticleRecommendInfo) {
            if (mRecommendListener != null) {
                mRecommendListener.onRecommendClick((ArticleRecommendInfo) v.getTag());
            }
        }
    }

    public interface RecommendListener {
        void onRecommendClick(ArticleRecommendInfo info);
    }
}
