package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhangwulin on 2015/12/4.
 * email 1501448275@qq.com
 */
public class CommunityTopPostView extends LinearLayout {
    private LinearLayout parentLayout;
    private TextView mTopContent;
    private LinearLayout.LayoutParams lp;
    public CommunityTopPostView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.account_selector_option);
        parentLayout = new LinearLayout(getContext());
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentLayout, lp);
        addTopIcon();
        addTopContent();
        addDivider();
    }

    private void addTopIcon() {
        TextView mTopIcon = new TextView(getContext());
        mTopIcon.setText(getContext().getString(R.string.community_top));
        mTopIcon.setTextColor(ResourceHelper.getColor(R.color.white));
        mTopIcon.setBackgroundResource(R.drawable.community_card_top_sign);
        mTopIcon.setTextSize(9);
        mTopIcon.setPadding(ResourceHelper.getDimen(R.dimen.space_5),ResourceHelper.getDimen(R.dimen.space_1),ResourceHelper.getDimen(R.dimen.space_5), ResourceHelper.getDimen(R.dimen.space_1));
        mTopIcon.setGravity(Gravity.CENTER);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.CENTER;
        lp.setMargins(ResourceHelper.getDimen(R.dimen.space_15),ResourceHelper.getDimen(R.dimen.space_15),ResourceHelper.getDimen(R.dimen.space_15),ResourceHelper.getDimen(R.dimen.space_15));
        parentLayout.addView(mTopIcon,lp);
    }

    private void addTopContent() {
        mTopContent = new TextView(getContext());
        mTopContent.setTextColor(getContext().getResources().getColor(R.color.llgray));
        mTopContent.setGravity(Gravity.CENTER);
        mTopContent.setSingleLine(true);
        mTopContent.setTextSize(13);
        mTopContent.setEllipsize(TextUtils.TruncateAt.END);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.CENTER;
        parentLayout.addView(mTopContent,lp);
    }

    private void addDivider() {
        View divider = new View(getContext());
        divider.setBackgroundColor(ResourceHelper.getColor(R.color.community_gray_line));
        lp=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.height=ResourceHelper.getDimen(R.dimen.divider_height);
        addView(divider, lp);
    }

    public void setTopView(CommunityPostBean postCard){
        mTopContent.setText(postCard.getPostTitle());
    }
}
