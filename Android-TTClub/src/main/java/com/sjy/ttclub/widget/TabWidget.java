
package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResourceType")
public class TabWidget extends RelativeLayout implements OnClickListener,
        TabPagerListener, INotify {

    public static final String TAG = "TabWidget";

    public final static int TABBG_DEFAULT = 0;
    public final static int TABBG_SELECTED = 1;

    public final static int TAB_BAR_INDEX = 0;
    // 起始ID
    protected final static int TAB_ITEM_ID_START = 0x08ff0000; // TabBar Item
    protected final static int ID_TABBAR_CONTAINER = 0x08fe0001;
    protected final static int ID_TABBAR = 0x08fe0000;
    protected OnTabChangedListener mListener;
    protected List<TabItem> mTabItems;

    protected RelativeLayout mTabbarContainer;
    protected LinearLayout mTabbar;
    protected TabCursor mCursor;

    protected TabPager mTabPager;

    private int mCursorOffset = 0;
    private int mCursorWidth = 0;
    private int mCursorHeight = 4;
    private int mCursorPadding = 10;
    private int mCursorColor = 0xfc1255;
    private int mTabItemTextSize = 20;
    private int mSelectedIndex = -1;

    private Drawable[] mTabItemBackground = new Drawable[2];
    private int[] mTabItemTextColor = new int[2];

    private boolean mEnableSnapAnimOnTitleClick = true;

    public TabWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, false);
    }

    public TabWidget(Context context) {
        super(context);
        init(context, false);
    }

    public TabWidget(Context context, boolean bottomTabbar) {
        super(context);
        init(context, bottomTabbar);
    }

    protected void init(Context context, boolean bottomTabbar) {
        mTabItems = new ArrayList<TabItem>();
        initTabbar(bottomTabbar);
        initTabPager(bottomTabbar);
        initDefaultStyle();

        setTabbarBg(null);
        setTabbarContainerBg(new ColorDrawable(TitleBar.getBgColor()));
        setTabBackgroundDrawable(new ColorDrawable(AbstractWindow.getBgColor()));
        setTabItemTextColor(TabWidget.TABBG_DEFAULT, ResourceHelper.getColor(R.color
                .tab_text_default_color));
        setTabItemTextColor(TabWidget.TABBG_SELECTED, ResourceHelper.getColor(R.color
                .tab_text_selected_color));
        setOverScrolledSytle(TabPager.OVERSCROLLED_STYLE_EDGEGLOW);
        setTabItemTextSize(ResourceHelper.getDimen(R.dimen.tabbar_textsize));
        setCursorHeight(ResourceHelper.getDimen(R.dimen.tabbar_cursor_height));
        mCursor.setCursorColor(ResourceHelper.getColor(R.color.tab_cursor_color));
    }

    private void initTabbar(boolean bottomTabbar) {
        Context context = getContext();
        mTabbarContainer = new RelativeLayout(context);
        mTabbarContainer.setId(ID_TABBAR_CONTAINER);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        if (bottomTabbar) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        this.addView(mTabbarContainer, lp);

        mTabbar = new LinearLayout(context);
        mTabbar.setId(ID_TABBAR);
        float tabBarHeight = ResourceHelper.getDimen(R.dimen.tabbar_height);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, (int) tabBarHeight);
        mTabbarContainer.addView(mTabbar, rlp);

        mCursor = new TabCursor(context);
        rlp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, mCursorHeight);
        rlp.addRule(RelativeLayout.BELOW, ID_TABBAR);
        mTabbarContainer.addView(mCursor, rlp);

    }

    private void initTabPager(boolean bottomTabbar) {
        mTabPager = new TabPager(getContext());
        mTabPager.setListener(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        if (bottomTabbar) {
            lp.addRule(RelativeLayout.ABOVE, ID_TABBAR_CONTAINER);
        } else {
            lp.addRule(RelativeLayout.BELOW, ID_TABBAR_CONTAINER);
        }
        this.addView(mTabPager, lp);
    }

    public void setEnableSnapAnimOnTitleClick(boolean enable) {
        mEnableSnapAnimOnTitleClick = enable;
    }

    public int getTabItemTextSize() {
        return mTabItemTextSize;
    }

    public View getTabbar() {
        return mTabbar;
    }

    public View peelingTabbarOff() {
        if (mTabbarContainer.getParent() != null) {
            ViewParent viewParent = mTabbarContainer.getParent();
            ((ViewGroup) viewParent).removeView(mTabbarContainer);
        }
        return mTabbarContainer;
    }

    public void pullTabbarIn() {
        if (mTabbarContainer.getParent() != null) {
            ViewParent viewParent = mTabbarContainer.getParent();
            ((ViewGroup) viewParent).removeView(mTabbarContainer);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        this.addView(mTabbarContainer, TAB_BAR_INDEX, lp);
    }

    public void initDefaultStyle() {
        this.setTabbarContainerBg(ResourceHelper.DEFAULT_BG_DRAWABLE);
        this.setTabItemTextColor(TabWidget.TABBG_DEFAULT,
                ResourceHelper.DEFAULT_TEXT_COLOR);
        this.setTabItemTextColor(TabWidget.TABBG_SELECTED,
                ResourceHelper.DEFAULT_TEXT_PRESSED_COLOR);
        this.setTabItemBg(TabWidget.TABBG_DEFAULT, null);
        this.setTabItemBg(TabWidget.TABBG_SELECTED, null);
        mCursor.initialize(mCursorWidth, mCursorHeight, mCursorPadding,
                mCursorColor, false);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshTabBar();
        refreshTabCursor();
    }

    @Override
    public void onClick(View v) {
        snapToTab(v.getId() - TAB_ITEM_ID_START, mEnableSnapAnimOnTitleClick);
        if (mListener != null) {
            mListener.onTabChangedByTitle(v.getId() - TAB_ITEM_ID_START);
        }
    }

    @Override
    public void onTabChangeStart(int newTabIndex, int oldTabIndex) {
        mSelectedIndex = newTabIndex;
        refreshTabBar(true, false, false);
        if (mListener != null) {
            mListener.onTabChangeStart(newTabIndex, oldTabIndex);
        }
    }

    @Override
    public void onTabChanged(int newTabIndex, int oldTabIndex) {
        if (mSelectedIndex != newTabIndex) {
            mSelectedIndex = newTabIndex;
            refreshTabBar();
        } else {
            refreshTabBar(false, true, false);
        }
        if (mListener != null) {
            mListener.onTabChanged(newTabIndex, oldTabIndex);
        }

    }

    @Override
    public void onTabSliding(int x, int y) {
        float currentX = x;
        float percentageOfX = currentX
                / ((mTabPager.getWidth() + mTabPager.getTabMargin()) * mTabItems
                .size());
        float scaledX = (mTabbarContainer.getWidth()
                - mTabbarContainer.getPaddingLeft() - mTabbarContainer
                .getPaddingRight()) * percentageOfX;
        mCursorOffset = (int) scaledX;
        mCursor.moveTo(mCursorOffset);
    }

    @Override
    public boolean onTabSlideOut() {
        return false;
    }

    public void setOnTabChangedListener(OnTabChangedListener listener) {
        mListener = listener;
    }

    public void lock() {
        mTabPager.lock();
        for (TabItem tabItem : mTabItems) {
            tabItem.mTitleView.setEnabled(false);
        }
    }

    public void unlock() {
        mTabPager.unlock();
        for (TabItem tabItem : mTabItems) {
            tabItem.mTitleView.setEnabled(true);
        }
    }

    public void setDragSwitchEnable(boolean aEnable) {
        mTabPager.setDragSwitchEnable(aEnable);
    }

    public void releaseDragging() {
        mTabPager.releaseDragging();
    }

    /**
     * Carefully call this method! It will remove all views! Anyway, the
     * style(appearance) will NOT be reset.
     */
    public void reset() {
        mSelectedIndex = -1;
        mTabItems.clear();
        mTabbar.removeAllViews();
        mTabPager.removeAllViews();
    }

    public void refreshTabBar() {
        refreshTabBar(true, true, false);
    }

    public void refreshTabBar(boolean refreshTextColor, boolean refreshTextBg,
                              boolean forceRefreshTextBg) {
        if (mSelectedIndex >= 0 && mTabItems != null
                && mSelectedIndex < mTabItems.size()) {
            int count = mTabItems.size();
            for (int i = 0; i < count; i++) {
                int selectedStatus = (i == mSelectedIndex ? 1 : 0);
                View titleView = mTabbar.getChildAt(i);
                if (refreshTextColor) {
                    if (titleView instanceof TextView) {
                        ((TextView) titleView)
                                .setTextColor(mTabItemTextColor[TABBG_DEFAULT
                                        + selectedStatus]);
                    }
                }
                titleView.setSelected(selectedStatus == 1);
                if (refreshTextBg) {
                    if (forceRefreshTextBg
                            || mTabItemBackground[TABBG_DEFAULT] != null
                            || mTabItemBackground[TABBG_SELECTED] != null) {
                        titleView
                                .setBackgroundDrawable(mTabItemBackground[TABBG_DEFAULT
                                        + selectedStatus]);
                    }
                }
            }
        }
    }

    public void setTabPagerAnimationTime(int duration) {
        if (mTabPager != null) {
            mTabPager.setScrollDuration(duration);
        }
    }

    public void refreshTabCursor() {
        int tabCount = mTabItems.size();
        if (tabCount > 0) {
            int width = mTabbarContainer.getMeasuredWidth()
                    - mTabbarContainer.getPaddingLeft()
                    - mTabbarContainer.getPaddingRight();
            int newCursorWidth = width / tabCount;
            float currentX = mSelectedIndex * width;
            float scaleInX = currentX / (width * tabCount);
            float scaledX = width * scaleInX;
            mCursorOffset = (int) scaledX;
            mCursorWidth = newCursorWidth;
            mCursor.setCursorWidth(mCursorWidth);
            mCursor.invalidate();
        }

    }

    public void addTab(View contextView, String title) {
        TextView titleView = new TextView(this.getContext());
        titleView.setText(title);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabItemTextSize);

        addTab(contextView, titleView);
    }

    public void addTab(View contentView, View titleView) {
        titleView.setId(TAB_ITEM_ID_START + mTabItems.size());
        titleView.setOnClickListener(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        mTabbar.addView(titleView, lp);
        mTabPager.addView(contentView);
        TabItem tabItem = new TabItem(contentView, titleView);
        mTabItems.add(tabItem);
        int cursorPadding = ResourceHelper.getDimen(R.dimen.tabbar_cursor_padding);
        if (mTabItems.size() >= 3) {
            cursorPadding = ResourceHelper.getDimen(R.dimen.tabbar_cursor_padding_threetab);
        }
        setCursorPadding(cursorPadding);
    }

    public void addTabBarDecorView(View decorView,
                                   RelativeLayout.LayoutParams lp) {
        if (decorView != null && decorView.getParent() == null) {
            mTabbarContainer.addView(decorView, lp);
        }
    }

    public void removeTabBarDecorView(View decorView) {
        if (decorView != null) {
            mTabbarContainer.removeView(decorView);
        }
    }

    /**
     * Scroll to tab at index with animation.
     *
     * @param index
     */
    public void snapToTab(int index) {
        snapToTab(index, true);
    }

    public void snapToTab(int index, boolean withAnimation) {
        if (index >= 0 && mTabItems != null && index < mTabItems.size()) {
            mTabPager.snapToTab(index, withAnimation);
            mSelectedIndex = index;
        }
        refreshTabBar();
    }

    public View getTabTitleView(int index) {
        if (index >= 0 && mTabItems != null && index < mTabItems.size()) {
            return mTabItems.get(index).mTitleView;
        }
        return null;
    }

    public View getTabContentView(int index) {
        if (index >= 0 && mTabItems != null && index < mTabItems.size()) {
            return mTabItems.get(index).mContentView;
        }
        return null;
    }

    public void showTabbar() {
        mTabbar.setVisibility(View.VISIBLE);
    }

    public void hideTabbar() {
        mTabbar.setVisibility(View.GONE);
    }

    public void showCursor() {
        mCursor.setVisibility(View.VISIBLE);
    }

    public void hideCursor() {
        mCursor.setVisibility(View.GONE);
    }

    public void showTabbarAndCursor() {
        mTabbarContainer.setVisibility(View.VISIBLE);
    }

    public void hideTabbarAndCursor() {
        mTabbarContainer.setVisibility(View.GONE);
    }

    public void setTabbarBg(Drawable bg) {
        if (mTabbar != null) {
            mTabbar.setBackgroundDrawable(bg);
        }
    }

    public void setTabbarContainerBg(Drawable bg) {
        if (mTabbarContainer != null) {
            mTabbarContainer.setBackgroundDrawable(bg);
        }
    }

    public void setTabbarContainerPadding(int l, int t, int r, int b) {
        mTabbarContainer.setPadding(l, t, r, b);
    }

    public void setTabbarHeight(int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTabbar
                .getLayoutParams();
        lp.height = height;
    }

    public void setTabItemTextSize(int textSize) {
        this.mTabItemTextSize = textSize;
        int count = mTabItems.size();
        for (int i = 0; i < count; i++) {
            TextView title = (TextView) mTabbar.getChildAt(i);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabItemTextSize);
        }
    }

    public void setTabItemTextColor(int index, int color) {
        if (index <= 1 && index >= 0) {
            mTabItemTextColor[index] = color;
            refreshTabBar();
        }
    }

    public void setTabItemBg(int index, Drawable drawable) {
        if (index <= 1 && index >= 0) {
            mTabItemBackground[index] = drawable;
            refreshTabBar(false, true, true);
        }
    }

    public void setCursorHeight(int height) {
        mCursor.setCursorHeight(height);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mCursor
                .getLayoutParams();
        lp.height = height;
        mCursor.setLayoutParams(lp);
    }

    public void setCursorPadding(int padding) {
        mCursor.setCursorPadding(padding);
    }

    public void setCursorColor(int color) {
        mCursor.setCursorColor(color);
    }

    public void setCursorDrawable(Drawable drawable) {
        mCursor.setCursorDrawable(drawable);
    }

    public void setCursorBackgroundDrawable(Drawable drawable) {
        mCursor.setBackgroundDrawable(drawable);
    }

    public void setCursorStyle(int style) {
        mCursor.setCursorStyle(style);
    }

    public void setCursorFadeOutDelay(int milliseconds) {
        mCursor.setFadeOutDelay(milliseconds);
    }

    public void setCursorFadeOutDuration(int milliseconds) {
        mCursor.setFadeOutDuration(milliseconds);
    }


    public int getScrollDuration() {
        return mTabPager.getScrollDuration();
    }

    public View getTabByIndex(int index) {
        return this.getTabContentView(index);
    }

    public int getCurrentTabIndex() {
        return mTabPager.getCurrentTabIndex();
    }

    public View getCurrentTab() {
        return mTabPager.getCurrentTab();
    }

    public int getOverScrolledStyle() {
        return mTabPager.getOverScrolledStyle();
    }

    public int getTabMargin() {
        return mTabPager.getTabMargin();
    }

    public void setContentDrawingCacheEnabled(boolean enabled) {
        mTabPager.setDrawingCacheEnabled(enabled);
    }

    public void setDynamicDurationEnabled(boolean enabled) {
        mTabPager.setDynamicDurationEnabled(enabled);
    }

    public void clearScrollableViews() {
        mTabPager.clearScrollableViews();
    }

    public void setScrollDuration(int duration) {
        mTabPager.setScrollDuration(duration);
    }

    public void setOverScrolledSytle(int style) {
        mTabPager.setOverScrolledStyle(style);
    }

    public void setEdgeEffect(EdgeEffect effect, boolean left) {
        mTabPager.setEdgeEffect(effect, left);
    }

    public void setEdgeEffect(Drawable leftDrawable, Drawable rightDrawable) {
        mTabPager.setEdgeEffect(leftDrawable, rightDrawable);
    }

    public void setEdgeBouceDragger(int dragger) {
        mTabPager.setEdgeBouceDragger(dragger);
    }

    public void setTabMargin(int margin) {
        mTabPager.setTabMargin(margin);
    }

    public void setTabBackgroundDrawable(Drawable drawable) {
        mTabPager.setBackgroundDrawable(drawable);
    }

    public void setContentPadding(int l, int t, int r, int b) {
        mTabPager.setPadding(l, t, r, b);
    }

    protected class TabItem {
        View mContentView;
        View mTitleView;

        public TabItem(View contentView, View titleView) {
            this.mContentView = contentView;
            this.mTitleView = titleView;
        }
    }

    private Bitmap mCache;
    private boolean mEnableCache = false;
    private boolean mRefresh = true;
    private boolean mBlockMeasureLayout = false;


    @Override
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        super.setChildrenDrawingCacheEnabled(false);
    }

    @Override
    protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        super.setChildrenDrawnWithCacheEnabled(false);
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
    }

    public void setEanbleCache(boolean enable) {
        mEnableCache = enable;
        mRefresh = enable;
        if (!enable) {
            mBlockMeasureLayout = false;
        }
    }

    public void destroyCache() {
        if (mCache != null && !mCache.isRecycled()) {
            mCache.recycle();
            mCache = null;
        }
    }

    private Canvas mCacheCanvas = new Canvas();

    private boolean judged = false;
    private boolean isHardwareAccelerated = false;

    @Override
    public void draw(Canvas canvas) {
        if (!judged) {
            judged = true;
            isHardwareAccelerated = SystemUtil.isHardwareAcceleratedJudgeEveryTime(canvas);
        }
        if (mEnableCache && !isHardwareAccelerated) {
            mBlockMeasureLayout = true;
            if (null == mCache) {
                // mStartTime = System.currentTimeMillis();
                mCache = Bitmap.createBitmap(getWidth(), getHeight(),
                        Bitmap.Config.ARGB_8888);
                if (mCache == null) {
                    // oom error
                    mEnableCache = false;
                    mBlockMeasureLayout = false;
                    super.draw(canvas);
                    return;
                }
                mCacheCanvas.setBitmap(mCache);
                // Log.d(TAG_CACHE, getClass().getSimpleName() +
                // ",buildCacheTimeConsume=" + (System.currentTimeMillis() -
                // mStartTime));
            }
            if (mRefresh) {
                mCache.eraseColor(Color.TRANSPARENT);
                super.draw(mCacheCanvas);
                mRefresh = false;
            }
            canvas.drawBitmap(mCache, 0, 0, null);
            // canvas.drawColor(Color.argb(64, 255, 0, 0));
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBlockMeasureLayout && getMeasuredWidth() != 0
                && getMeasuredHeight() != 0) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (mBlockMeasureLayout)
            return;
        super.onLayout(changed, left, top, right, bottom);
    }

    public void resetPrivateFlagDrawn() {
        mTabPager.resetPrivateFlagDrawn();
    }

    private void updateTitleBarShadow() {
        this.setWillNotDraw(false);
        this.invalidate();
    }

    @Override
    public void notify(Notification aNotification) {

    }

    public View getTabPagerRef() {
        return this.mTabPager;
    }

    public void replaceContent(int index, View view) {
        mTabPager.replaceView(index, view);
    }

    public void recoverContent(int index) {
        TabItem tabItem = mTabItems.get(index);
        mTabPager.replaceView(index, tabItem.mContentView);
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }
}