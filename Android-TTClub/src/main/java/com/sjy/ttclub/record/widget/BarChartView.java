package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.HashMap;

/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class BarChartView extends BaseChartView {

    private int mTextPadding;
    private float[] mRadiis = new float[8];

    private HashMap<BaseChartInfo, GradientDrawable> mDrawableMap = new HashMap<BaseChartInfo, GradientDrawable>();

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mTextPadding = ResourceHelper.getDimen(R.dimen.space_10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int totalWidth = 0;
        for (BaseChartInfo info : mChartList) {
            totalWidth += info.width;
        }
        setMeasuredDimension(totalWidth, height);
    }


    @Override
    public void draw(Canvas canvas) {
        if (!mBlockDrawOnAttach) {
            super.draw(canvas);
            drawBarCharts(canvas);
        }
        if (mIsAnim) {
            long costTime = Math.abs(SystemClock.uptimeMillis() - mStartTime);
            if (costTime >= mDuration) {
                mIsAnim = false;
            }
            invalidate();
        }

    }

    private void drawBarCharts(Canvas canvas) {
        int offset = 0;
        int size = mChartList.size();
        BaseChartInfo info;
        GradientDrawable drawable;
        for (int i = 0; i < size; i++) {
            info = mChartList.get(i);
            canvas.save();
            canvas.translate(offset, 0);
            drawable = mDrawableMap.get(info);
            if (drawable == null) {
                drawable = createDrawable(info);
                mDrawableMap.put(info, drawable);
            }
            drawBarChart(canvas, info, drawable);
            canvas.restore();
            offset += info.width;
        }

    }

    private void drawBarChart(Canvas canvas, BaseChartInfo info, GradientDrawable drawable) {
        float ratio = 1.0f;
        if (mIsAnim) {
            long costTime = Math.abs(SystemClock.uptimeMillis() - mStartTime);
            if (costTime > mDuration) {
                costTime = mDuration;
            }
            ratio = 1.0f * costTime / mDuration;
        }

        mPaint.setColor(info.color);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(info.textSize);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        int diff = (int) (Math.abs(fm.ascent) - Math.abs(fm.descent));
        int viewHeight = getHeight();
        int maxHeight = viewHeight - diff - mTextPadding;
        final int height = maxHeight * info.percent / 100;
        final int curHeight = (int) (height * ratio);

        if (info.chartType == BaseChartInfo.ChartType.BAR_DOWN) {
            drawable.setBounds(0, 0, info.width, curHeight);
            drawable.draw(canvas);
            //draw Text
            String text = ((int) (info.percent * ratio)) + "%";
            int posY = curHeight + diff + mTextPadding;
            int posX = info.width / 2;
            canvas.drawText(text, posX, posY, mPaint);
        } else if (info.chartType == BaseChartInfo.ChartType.BAR_UP) {
            int translateHeight = viewHeight - curHeight;
            canvas.save();
            canvas.translate(0, translateHeight);
            drawable.setBounds(0, 0, info.width, curHeight);
            drawable.draw(canvas);
            canvas.restore();
            //draw Text
            String text = ((int) (info.percent * ratio)) + "%";
            int posY = translateHeight - diff;
            int posX = info.width / 2;
            canvas.drawText(text, posX, posY, mPaint);
        }


    }

    private GradientDrawable createDrawable(BaseChartInfo info) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(info.color);
        if (info.chartType == BaseChartInfo.ChartType.BAR_DOWN) {
            gradientDrawable.setCornerRadii(createCornerRadii(0, 0, info.width / 2, info.width / 2));
        } else if (info.chartType == BaseChartInfo.ChartType.BAR_UP) {
            gradientDrawable.setCornerRadii(createCornerRadii(info.width / 2, info.width / 2, 0, 0));
        }
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        return gradientDrawable;
    }

    private float[] createCornerRadii(float r0, float r1, float r2, float r3) {
        float[] radii = new float[8];
        radii[0] = radii[1] = r0;
        radii[2] = radii[3] = r1;
        radii[4] = radii[5] = r2;
        radii[6] = radii[7] = r3;

        return radii;
    }


}
