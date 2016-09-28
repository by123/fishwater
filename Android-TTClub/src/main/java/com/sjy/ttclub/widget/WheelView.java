package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by linhz on 2015/12/24.
 * Email: linhaizhong@ta2she.com
 */
public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();
    public static final int OFF_SET_DEFAULT = 1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    private int mInitialY;
    private Runnable mScrollerTask;
    private int mNewCheck = 50;
    private int mOffset = OFF_SET_DEFAULT;
    private int mDisplayItemCount; // 每页显示的数量
    private int mSelectedIndex = 1;

    private ArrayList<String> mItemList = new ArrayList<String>();
    private LinearLayout mViewsContainer;

    private int mScrollDirection = -1;
    private Paint mPaint;
    private int mViewWidth;
    private int mItemHeight = 0;
    private int[] mSelectedAreaBorder;

    private boolean mIsBorderFillWidth = false;

    private int mTextSize = ResourceHelper.getDimen(R.dimen.space_16);
    private int mSelectedColor = ResourceHelper.getColor(R.color.wheel_view_selected_color);
    private int mDefaultColor = ResourceHelper.getColor(R.color.wheel_view_default_color);
    private int mBorderColor = ResourceHelper.getColor(R.color.wheel_view_border_color);


    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setVerticalScrollBarEnabled(false);
        CommonUtils.disableEdgeEffect(this);

        mViewsContainer = new LinearLayout(context);
        mViewsContainer.setOrientation(LinearLayout.VERTICAL);
        this.addView(mViewsContainer);

        mScrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                if (mInitialY - newY == 0) {
                    final int remainder = mInitialY % mItemHeight;
                    final int divided = mInitialY / mItemHeight;
                    if (remainder == 0) {
                        mSelectedIndex = divided + mOffset;
                        onSelectedCallBack();
                    } else {
                        if (remainder > mItemHeight / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, mInitialY - remainder + mItemHeight);
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, mInitialY - remainder);
                                }
                            });
                        }


                    }


                } else {
                    mInitialY = getScrollY();
                    WheelView.this.postDelayed(mScrollerTask, mNewCheck);
                }
            }
        };
    }

    public void setSelectedTextColor(int color) {
        mSelectedColor = color;
    }

    public void setDefaultTextColor(int color) {
        mDefaultColor = color;
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
    }

    public void setTextSize(int size) {
        mTextSize = size;
    }

    public void setBorderFillWidth(boolean fill) {
        mIsBorderFillWidth = fill;
    }

    private List<String> getItemList() {
        return mItemList;
    }

    public void setDisplayItemCount(int count) {
        if (count % 2 == 0) {
            mOffset = count / 2 + 1;
        } else {
            mOffset = count / 2;
            if (mOffset == 0) {
                mOffset = 1;
            }
        }
        mDisplayItemCount = mOffset * 2 + 1;
    }

    public void setItemList(List<String> list) {
        mViewsContainer.removeAllViewsInLayout();
        mItemList.clear();
        mItemList.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < mOffset; i++) {
            mItemList.add(0, "");
            mItemList.add("");
        }
        mDisplayItemCount = mOffset * 2 + 1;

        for (String item : mItemList) {
            mViewsContainer.addView(createView(item));
        }

        refreshItemView(0);
    }


    private void startScrollerTask() {
        mInitialY = getScrollY();
        this.postDelayed(mScrollerTask, mNewCheck);
    }

    private TextView createView(String item) {
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = ResourceHelper.getDimen(R.dimen.space_15);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == mItemHeight) {
            mItemHeight = getViewMeasuredHeight(tv);
            mViewsContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight *
                    mDisplayItemCount));
            ViewGroup.LayoutParams lp = this.getLayoutParams();
            lp.height = mItemHeight * mDisplayItemCount;
            this.setLayoutParams(lp);
        }
        return tv;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
        if (t > oldt) {
            mScrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            mScrollDirection = SCROLL_DIRECTION_UP;
        }

    }

    private void refreshItemView(int y) {
        int position = y / mItemHeight + mOffset;
        int remainder = y % mItemHeight;
        int divided = y / mItemHeight;

        if (remainder == 0) {
            position = divided + mOffset;
        } else {
            if (remainder > mItemHeight / 2) {
                position = divided + mOffset + 1;
            }
        }

        if (mSelectedIndex != position) {
            mSelectedIndex = position;
            onSelectedCallBack();
        }
        mSelectedIndex = position;

        int childSize = mViewsContainer.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) mViewsContainer.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(mSelectedColor);
            } else {
                itemView.setTextColor(mDefaultColor);
            }
        }
    }

    private int[] obtainSelectedAreaBorder() {
        if (null == mSelectedAreaBorder) {
            mSelectedAreaBorder = new int[2];
            mSelectedAreaBorder[0] = mItemHeight * mOffset;
            mSelectedAreaBorder[1] = mItemHeight * (mOffset + 1);
        }
        return mSelectedAreaBorder;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawBorder(canvas);
    }

    private void drawBorder(Canvas canvas) {
        if (mViewWidth == 0) {
            mViewWidth = getWidth();
            if (mViewWidth == 0) {
                mViewWidth = HardwareUtil.windowWidth;
            }
        }

        if (null == mPaint) {
            mPaint = new Paint();
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(ResourceHelper.getDimen(R.dimen.space_1));
        }
        canvas.save();
        canvas.translate(0, getScrollY());
        int startPos = mIsBorderFillWidth ? 0 : mViewWidth * 1 / 6;
        int endPos = mIsBorderFillWidth ? mViewWidth : mViewWidth * 5 / 6;
        canvas.drawLine(startPos, obtainSelectedAreaBorder()[0], endPos,
                obtainSelectedAreaBorder()[0], mPaint);
        canvas.drawLine(startPos, obtainSelectedAreaBorder()[1], endPos,
                obtainSelectedAreaBorder()[1], mPaint);
        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != onWheelViewListener) {
            int index = mSelectedIndex - mOffset;
            onWheelViewListener.onSelected(this, index, mItemList.get(mSelectedIndex));
        }
    }

    public void setCurrentPosition(int position) {
        setCurrentPosition(position, false);
    }

    private void setCurrentPosition(int position, boolean anim) {
        final int p = position;
        mSelectedIndex = p + mOffset;
        onSelectedCallBack();
        if (anim) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    WheelView.this.smoothScrollTo(0, p * mItemHeight);
                }
            });
        } else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    WheelView.this.scrollTo(0, p * mItemHeight);
                }
            });
        }
    }

    public String getSeletedItem() {
        return mItemList.get(mSelectedIndex);
    }

    public int getCurrentPosition() {
        return mSelectedIndex - mOffset;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }


    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }


    public interface OnWheelViewListener {
        void onSelected(WheelView wView, int selectedIndex, String item);
    }

}
