package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by zhangwulin on 2015/12/17.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailTitleView extends LinearLayout {
    private Context mContext;
    private TextView mTitleTextView;
    public CommunityPostDetailTitleView(Context context) {
        this(context, null);
    }

    public CommunityPostDetailTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityPostDetailTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View parentView = View.inflate(mContext, R.layout.community_post_detail_item_title_view, null);
        addView(parentView);
        mTitleTextView = (TextView) parentView.findViewById(R.id.comment_title_text);
    }

    public void setTitleView(String title) {
        setmTitleTextView(title);
    }



    private void setmTitleTextView(String title) {
        if (StringUtils.isEmpty(title)) {
            return;
        }
        mTitleTextView.setText(title);
    }
}
