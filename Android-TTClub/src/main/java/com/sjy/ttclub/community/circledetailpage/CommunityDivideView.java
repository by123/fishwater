package com.sjy.ttclub.community.circledetailpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zhangwulin on 2015/12/22.
 * email 1501448275@qq.com
 */
public class CommunityDivideView extends LinearLayout {

    public CommunityDivideView(Context context) {
        this(context,null);
    }

    public CommunityDivideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityDivideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        View view=new View(getContext());
        view.setBackgroundResource(R.color.gray_bg);
        LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper.getDimen(R.dimen.space_10));
        addView(view,lp);
    }
}
