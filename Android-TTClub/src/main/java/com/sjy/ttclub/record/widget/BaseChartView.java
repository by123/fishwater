package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class BaseChartView extends FrameLayout {
    protected ArrayList<BaseChartInfo> mChartList = new ArrayList<BaseChartInfo>();

    protected Paint mPaint = new Paint();

    protected long mStartTime;
    protected boolean mIsAnim = false;

    protected long mDuration = 1000;

    protected boolean mBlockDrawOnAttach = false;

    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mPaint.setAntiAlias(true);
    }

    public void addChartInfo(BaseChartInfo info) {
        mChartList.add(info);
    }

    public void addChartInfo(ArrayList<BaseChartInfo> list) {
        mChartList.addAll(list);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void startAnim() {
        mBlockDrawOnAttach = false;
        mIsAnim = true;
        mStartTime = SystemClock.uptimeMillis();
        invalidate();
    }

    public void stopAnim() {
        mIsAnim = false;
    }

    public void setBlockDrawOnAttach(boolean blockDrawOnAttach) {
        mBlockDrawOnAttach = blockDrawOnAttach;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
