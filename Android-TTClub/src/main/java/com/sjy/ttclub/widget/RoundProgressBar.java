/*
 * Copyright (C) 2015-2015 SJY.JIANGSU Corporation. All rights reserved
 */
package com.sjy.ttclub.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;

/**
 * Created by chenwj on 2015/10/29.
 * Email: chenweijia@ta2she.com
 */
public class RoundProgressBar extends RelativeLayout {
    public static final int STROKE = 0;
    public static final int FILL = 1;

    private static final int ID_PROGRESS_VIEW = 0x2022;

    private Paint mPaint;
    private int mRoundColor;
    private int mRoundProgressColor;
    private int mTextColor;
    private float mTextSize;
    private float mRoundWidth;
    private int mMaxProgress;
    private int mCurProgress;
    private int mStyle;
    private Drawable mIconDrawble;
    private int mDetailTextColor;
    private float mDetailTextSize;
    private String mDetailText;

    private ImageView mIconView;
    private TextView mProgressView;
    private TextView mDetailView;


    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        mRoundColor = typedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        mRoundProgressColor = typedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        mTextColor = typedArray.getColor(R.styleable.RoundProgressBar_rtextColor, Color.WHITE);
        mTextSize = typedArray.getDimension(R.styleable.RoundProgressBar_rtextSize, 15);
        mRoundWidth = typedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMaxProgress = typedArray.getInteger(R.styleable.RoundProgressBar_max, 9999);
        mStyle = typedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mDetailTextColor = typedArray.getColor(R.styleable.RoundProgressBar_rdtextColor, Color.WHITE);
        mDetailTextSize = typedArray.getDimension(R.styleable.RoundProgressBar_rdtextSize, 12);
        mDetailText = typedArray.getString(R.styleable.RoundProgressBar_rdtext);

        mIconDrawble = typedArray.getDrawable(R.styleable.RoundProgressBar_rIcon);

        typedArray.recycle();

        setWillNotDraw(false);
        initViews();
    }

    @SuppressWarnings("ResourceType")
    private void initViews() {
        mProgressView = new TextView(getContext());
        mProgressView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mProgressView.setTextColor(mTextColor);
        mProgressView.setId(ID_PROGRESS_VIEW);
        mProgressView.setText("0");
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rlp.addRule(CENTER_IN_PARENT);
        this.addView(mProgressView, rlp);

        mIconView = new ImageView(getContext());
        mIconView.setImageDrawable(mIconDrawble);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rlp.addRule(CENTER_HORIZONTAL);
        rlp.addRule(ABOVE, ID_PROGRESS_VIEW);
        this.addView(mIconView, rlp);

        mDetailView = new TextView(getContext());
        mDetailView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailTextSize);
        mDetailView.setTextColor(mDetailTextColor);
        mDetailView.setText(mDetailText);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rlp.addRule(CENTER_HORIZONTAL);
        rlp.addRule(BELOW, ID_PROGRESS_VIEW);
        this.addView(mDetailView, rlp);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2; //获取圆心的x坐标
        int centerY = getHeight() / 2;
        int radius = (int) (centerX - mRoundWidth / 2);
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        //设置进度是实心还是空心
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setColor(mRoundProgressColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        RectF oval = new RectF(centerX - radius, centerX - radius, centerX
                + radius, centerX + radius);

        switch (mStyle) {
            case STROKE: {
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, 360 * mCurProgress / mMaxProgress, false, mPaint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (mCurProgress != 0)
                    canvas.drawArc(oval, 270, 360 * mCurProgress / mMaxProgress, true, mPaint);  //根据进度画圆弧
                break;
            }
        }


    }


    public synchronized int getMaxProgress() {
        return mMaxProgress;
    }

    /**
     * 设置进度的最大值
     *
     * @param mMaxProgress
     */
    public void setMaxProgress(int mMaxProgress) {
        if (mMaxProgress < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.mMaxProgress = mMaxProgress;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public int getCurProgress() {
        return mCurProgress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param curProgress
     */
    public void setCurProgress(int curProgress) {
        if (curProgress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (curProgress > mMaxProgress) {
            curProgress = mMaxProgress;
        }
        if (curProgress <= mMaxProgress) {
            this.mCurProgress = curProgress;
            postInvalidate();
        }

        mProgressView.setText(String.valueOf(curProgress));

    }

    public void setDetailText(String text) {
        mDetailView.setText(text);
    }

    public void setIconDrawble(Drawable drawble) {
        mIconView.setImageDrawable(drawble);
    }

    public int getCricleColor() {
        return mRoundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.mRoundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return mRoundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.mRoundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public float getRoundWidth() {
        return mRoundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.mRoundWidth = roundWidth;
    }


}
