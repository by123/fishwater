package com.sjy.ttclub.homepage;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sjy.ttclub.util.CommonUtils;

public class HomepageListView extends ListView {

    private HomepageListViewAdapter mAdapter;

    public HomepageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HomepageListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomepageListView(Context context) {
        this(context, null);

    }

    private void init() {
        CommonUtils.disableEdgeEffect(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {

        mAdapter = (HomepageListViewAdapter) adapter;
        super.setAdapter(adapter);
    }

    public boolean shouldDisableTouchEvent(MotionEvent event) {
        if (mAdapter == null) {
            return false;
        }

        boolean visible = mAdapter.isTransparentViewVisible();
        if (visible) {
            Rect rect = mAdapter.getTransparentViewHitRect();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (rect.contains(x, y)) {
                return true;
            }
        }

        return false;
    }


}
