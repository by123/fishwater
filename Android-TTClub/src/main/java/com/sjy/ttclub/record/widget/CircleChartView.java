package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsym.ttclub.R;


/**
 * Created by linhz on 2016/1/6.
 * Email: linhaizhong@ta2she.com
 */
public class CircleChartView extends BaseChartView {

    private ImageView mIconView;
    private TextView mAverageView;
    private TextView mSelfView;
    private View mDividerView;

    private RectF mDrawRect = new RectF();

    public CircleChartView(Context context) {
        this(context, null);
    }

    public CircleChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View view = View.inflate(context, R.layout.record_circle_chart_inner_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        this.addView(view, lp);

        mIconView = (ImageView) view.findViewById(R.id.record_circle_chart_inner_image);

        mAverageView = (TextView) view.findViewById(R.id.record_circle_chart_inner_average);
        mAverageView.setVisibility(View.GONE);

        mDividerView = view.findViewById(R.id.record_circle_chart_inner_divider);
        mDividerView.setVisibility(View.GONE);

        mSelfView = (TextView) view.findViewById(R.id.record_circle_chart_inner_self);

    }

    @Override
    public void addChartInfo(BaseChartInfo info) {
        super.addChartInfo(info);
        if (info.chartType == BaseChartInfo.ChartType.CIRCLE_AVERAGE) {
            mAverageView.setTextColor(info.color);
            if (info.textSize > 0) {
                mAverageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, info.textSize);
            }
            mAverageView.setText(info.percent + "%");
            mAverageView.setVisibility(View.VISIBLE);
            mDividerView.setVisibility(View.VISIBLE);
        } else if (info.chartType == BaseChartInfo.ChartType.CIRCLE_SELF) {
            mSelfView.setTextColor(info.color);
            mSelfView.setText(info.percent + "%");
            if (info.textSize > 0) {
                mSelfView.setTextSize(TypedValue.COMPLEX_UNIT_PX, info.textSize);
            }
        }

    }

    public void setIcon(int resId) {
        mIconView.setImageResource(resId);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawCircles(canvas);
    }

    private void drawCircles(Canvas canvas) {
        int radius = getWidth() / 2;
        for (BaseChartInfo info : mChartList) {
            radius -= info.width;
            drawCircle(canvas, info, radius);
        }
    }

    private void drawCircle(Canvas canvas, BaseChartInfo info, int radius) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        mPaint.setColor(info.color);
        mPaint.setAlpha(40);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(info.width);
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        mPaint.setAlpha(255);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mDrawRect.set(centerX - radius, centerX - radius, centerX
                + radius, centerX + radius);
        canvas.drawArc(mDrawRect, 270, 360 * info.percent / 100, false, mPaint);
    }
}
