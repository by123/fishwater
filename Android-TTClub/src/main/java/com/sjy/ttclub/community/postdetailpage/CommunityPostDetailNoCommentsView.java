package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhangwulin on 2015/12/21.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailNoCommentsView extends LinearLayout {
    private TextView mNoCommentsTip;
    private LinearLayout.LayoutParams lp;
    public CommunityPostDetailNoCommentsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommunityPostDetailNoCommentsView(Context context) {
        this(context, null);
    }

    public CommunityPostDetailNoCommentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        setBackgroundResource(R.color.white);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mNoCommentsTip=new TextView(getContext());
        mNoCommentsTip.setGravity(Gravity.CENTER);
        lp=new LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper.getDimen(R.dimen.space_40));
        lp.gravity= Gravity.CENTER;
        addView(mNoCommentsTip, lp);
    }

    public void setNoCommentsTipView(String tip){
        if(StringUtils.isEmpty(tip)){
            mNoCommentsTip.setText(getContext().getString(R.string.community_no_comments_tip));
            return;
        }
        mNoCommentsTip.setText(tip);
    }
}
