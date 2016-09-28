/*
 *  * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.util.StringUtils;

public class CommunityHomepageListView extends ListView implements OnScrollListener {

    Scroller scroller;
    View foot;// 底部布局文件；
    int footHeight;// 顶部布局文件的高度；
    private boolean isBottom = false;// 是否滚动到最后一行
    private boolean isFullScreen = false;
    int scrollState;// listview 当前滚动状态；
    boolean isRemark = false, isLoad = false;// 标记，当前是在listview最顶端摁下的；
    private int startY;// 摁下时的Y值；
    private int currentY;//当前y的值
    private int startX;
    boolean isRelese = false;
    private int distence;

    int state;// 当前的状态；
    final int NONE = 0;// 正常状态；
    final int PULL = 1;// 提示上拉状态；
    final int RELESE = 2;// 提示释放状态；
    OnLoadingListener onLoadingListener;//刷新数据的接口
    private ImageView foot_icon;
    private ProgressBar foot_load;
    private int sexTag = 0;//0女 ，1男
    private int mShowFootHeight = 0;

    public CommunityHomepageListView(Context context) {
        super(context);
        initView(context);
    }

    public CommunityHomepageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CommunityHomepageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化界面，添加顶部布局文件到 listview
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        foot = inflater.inflate(R.layout.foot_view_layout, null);

        foot_icon = (ImageView) foot.findViewById(R.id.foot_icon);
        initSexImg();//男女加载

        foot_load = (ProgressBar) foot.findViewById(R.id.foot_load);
        closeLoad();//底部Load

        scroller = new Scroller(context);
        measureView(foot);
        footHeight = foot.getMeasuredHeight();
        mShowFootHeight = -(footHeight * 3 / 4);
        Log.i("tag", "headerHeight = " + footHeight);
        footPadding(mShowFootHeight);
        this.addFooterView(foot);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 根据用户性别判断图片展示（未完）
     */
    public void initSexImg() {

        if (AccountManager.getInstance().getSex() == CommonConst.SEX_MAN) {
            foot_icon.setImageResource(R.drawable.community_home_girl_icon);
            return;
        }
        if(AccountManager.getInstance().getSex() ==CommonConst.SEX_WOMAN){
            foot_icon.setImageResource(R.drawable.community_home_man_icon);
            return;
        }
        foot_icon.setImageResource(R.drawable.community_home_girl_icon);
    }

    /**
     * 设置header 布局 上边距:
     *
     * @param footPadding
     */
    private void footPadding(final int footPadding) {
        foot.setPadding(foot.getPaddingLeft(), foot.getPaddingTop(), foot.getPaddingRight(), footPadding);
        foot.invalidate();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            isRemark = true;
            isBottom = true;
        } else {
            isRemark = false;
            isBottom = false;
        }
        if (totalItemCount > visibleItemCount) {
            isFullScreen = true;
        } else {
            isFullScreen = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        this.scrollState = scrollState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = (int) ev.getRawY();
                startX = (int) ev.getRawX();
                if (state == NONE) {
                    startY = (int) ev.getRawY();
                }
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                distence = (int) (ev.getRawY() - startY);
                if (state == RELESE) {
                    // 添加监听；
                    if (onLoadingListener != null) {
                        onLoadingListener.onJumpTo();
                    }

                } else if (state == PULL) {
                    state = NONE;
                    isRelese = true;

                    if (!isBottom)
                        isRemark = false;
                    reflashViewByState(distence);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程操作；
     *
     * @param ev
     */
    int space;

    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getRawY();
        space = tempY - startY;
        int footPadding = -space - (footHeight * 3 / 4);
        switch (state) {
            case NONE:
                state = PULL;
                foot_load.setVisibility(View.GONE);
                reflashViewByState(space);
                break;
            case PULL:
                footPadding(footPadding);
                if (space < -((footHeight * 1 / 4) + 100) && isBottom && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    foot_load.setVisibility(View.VISIBLE);

                    state = RELESE;
                    reflashViewByState(space);
                }
                break;
            case RELESE:
                distence = (int) (ev.getRawY() - startY);
                footPadding(footPadding);
                if (space > -((footHeight * 1 / 4) + 100)) {
                    state = PULL;
                    reflashViewByState(distence);
                } else if (space >= 0) {
                    state = NONE;
                    isRelese = true;
                    if (!isBottom)
                        isRemark = false;
                    reflashViewByState(distence);
                }
                break;
        }
    }

    /**
     * 根据当前状态，改变界面显示；
     */
    private void reflashViewByState(int distance) {
        TextView tip = (TextView) foot.findViewById(R.id.foot_text_01);
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);
        Log.v("zhxu_test-state", state + " ");
        switch (state) {
            case NONE:
                foot_load.setVisibility(View.GONE);
                footPadding(mShowFootHeight);
                break;
            case PULL:
                break;
            case RELESE:
                foot_load.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 获取完数据；
     */
    public void reflashComplete() {
        closeLoad();
        state = NONE;
        isRemark = false;
        isBottom = false;
        isLoad = true;
        reflashViewByState(distence);
    }

    public void closeLoad() {
        foot_load.setVisibility(View.GONE);
    }

    public void setInterface(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    /**
     * 刷新数据接口
     *
     * @author Administrator
     */
    public interface OnLoadingListener {
        void onJumpTo();
    }
}
