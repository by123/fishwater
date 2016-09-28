package com.sjy.ttclub.homepage;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.lsym.ttclub.R;


public class HomepageFrameLayout extends FrameLayout {
    private HomepageListView mListView;
    private HomepageTopLayout mTopLayout;

    private boolean mIsInitViews = false;

    public HomepageFrameLayout(Context context) {
        super(context);
    }

    public HomepageFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomepageFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        initViews();
        boolean isTouchSexy = isTouchTopLayoutView(event);
        boolean disableTouchEvent = isListViewDisableTouchEvent(event);
        if (isTouchSexy && disableTouchEvent && mTopLayout != null) {
            return mTopLayout.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isListViewDisableTouchEvent(MotionEvent event) {
        if (mListView != null) {
            return mListView.shouldDisableTouchEvent(event);
        }
        return false;
    }

    private boolean isTouchTopLayoutView(MotionEvent event) {
        if (mTopLayout != null) {
            Rect rect = new Rect();
            mTopLayout.getHitRect(rect);
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (rect.contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    private void initViews() {
        if (mIsInitViews) {
            return;
        }
        mIsInitViews = true;
        mTopLayout = (HomepageTopLayout) findViewById(R.id.homepage_toplayout);
        mListView = (HomepageListView) findViewById(R.id.homepage_listview);
    }


}
