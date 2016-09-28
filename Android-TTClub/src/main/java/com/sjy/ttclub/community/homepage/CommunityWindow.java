package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by zwl on 2015/11/9.
 * Email: 1501448275@qq.com
 */
public class CommunityWindow extends DefaultWindow {
    private LinearLayout mRootView;

    public CommunityWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        onCreateContent();
    }

    private View onCreateContent() {
        addRootView();
        addTitle();
        addViewPage();
        getBaseLayer().addView(mRootView, getContentLPForBaseLayer());
        return mRootView;
    }

    @Override
    protected View onCreateTitleBar() {
        return null;
    }

    private void addRootView() {
        mRootView = new LinearLayout(getContext());
        mRootView.setOrientation(LinearLayout.VERTICAL);
    }

    private void addViewPage() {

    }

    private void addTitle() {
        LinearLayout mParentLayout = new LinearLayout(getContext());
        mParentLayout.setOrientation(LinearLayout.VERTICAL);
        mParentLayout.setBackgroundColor(ResourceHelper.getColor(R.color.title_bg_color));
        ImageView mBackView = new ImageView(getContext());
        mBackView.setImageResource(R.drawable.title_back);
        mBackView.setPadding(5, 5, 5, 5);
        mParentLayout.addView(mBackView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                100);
        lp.gravity = Gravity.LEFT;
        mRootView.addView(mParentLayout, lp);
    }
}
