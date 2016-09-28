package com.sjy.ttclub.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;


public class PriceView extends View {

    private String value = null;

    private int moneySize = -1;
    private int intSize = -1;
    private int decimalSize = -1;

    private String money = "￥";
    private String decimalPart = "";
    private String intPart = "";

    private int moneyStart = 0;
    private int intStart = 0;
    private int decimalStart = 0;

    private int textColor = 0;
    private boolean strike = false;
    private boolean withEndZero = true;

    private Paint mPaint;
    private Rect mTextBound = new Rect();
    private int totalWidth = 0;
    private int maxHeight = 0;


    public PriceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PriceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceView(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        getProperties(context, attrs);
        calcTextDimens();
    }

    private void calcTextDimens() {

        totalWidth = 0;
        maxHeight = 0;

        if (value == null || value.length() <= 0) {
            return;
        }
        String arr[] = value.split("\\.");

        intPart = arr[0];
        if (intPart.length() > 0 && intPart.charAt(0) == '￥') {
            intPart = intPart.substring(1);
        }

        decimalPart = arr.length > 1 ? arr[1] : "";
        if (decimalPart != null) {
            if (!withEndZero) {
                decimalPart = decimalPart.replaceAll("0{1,}$", "");
            }
            if (decimalPart != null && decimalPart.length() > 0) {
                decimalPart = "." + decimalPart;
            }
        }

        //处理￥
        int moneyWidth = process(money, moneySize);
        moneyStart = getPaddingLeft();

        //处理整数部分
        int intWidth = process(intPart, intSize);
        intStart = moneyStart + moneyWidth;

        //处理小数部分
        process(decimalPart, decimalSize);
        decimalStart = intStart + intWidth;

        totalWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

    }

    private int process(String text, int textSize) {
        if (text == null || text.length() <= 0) {
            return 0;
        }
        mPaint.setTextSize(textSize);
        int textWidth = (int) mPaint.measureText(text);
        mPaint.getTextBounds(text, 0, text.length(), mTextBound);
        totalWidth += textWidth;
        maxHeight = mTextBound.height() > maxHeight ? mTextBound.height() : maxHeight;
        return textWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mPaint.setColor(textColor);
        //画中间的删除线
        if (strike) {
            float startX = getPaddingLeft();
            float startY = (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2 + getPaddingTop();
            float stopX = getMeasuredWidth() - getPaddingRight();
            float stopY = startY;
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }
        //画￥
        mPaint.setTextSize(moneySize);
        float commonY = getMeasuredHeight() - getPaddingBottom() - ResourceHelper.getDimen(R.dimen.space_1);
        canvas.drawText(money, moneyStart, commonY, mPaint);
        //画整数部分
        mPaint.setTextSize(intSize);
        canvas.drawText(intPart, intStart, commonY, mPaint);
        //画小数部分
        mPaint.setTextSize(decimalSize);
        canvas.drawText(decimalPart, decimalStart, commonY, mPaint);
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = totalWidth;
                break;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = maxHeight;
                break;
        }
        return result + ResourceHelper.getDimen(R.dimen.space_1);
    }

    private void getProperties(Context context, AttributeSet attrs) {
        //自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CartPriceValue);
        int textSize = a.getDimensionPixelSize(R.styleable.CartPriceValue_textSize, 14);
        String value = a.getString(R.styleable.CartPriceValue_value);
        int textColor = a.getColor(R.styleable.CartPriceValue_textColor, 0xffffff);
        int moneySize = a.getDimensionPixelSize(R.styleable.CartPriceValue_moneySize, textSize);
        int intSize = a.getDimensionPixelSize(R.styleable.CartPriceValue_intSize, textSize);
        int decimalSize = a.getDimensionPixelSize(R.styleable.CartPriceValue_decimalSize, textSize);
        boolean strike = a.getBoolean(R.styleable.CartPriceValue_strike, false);
        boolean withEndZero = a.getBoolean(R.styleable.CartPriceValue_withEndZero, true);
        this.value = value;
        this.textColor = textColor;
        this.moneySize = moneySize;
        this.intSize = intSize;
        this.decimalSize = decimalSize;
        this.strike = strike;
        this.withEndZero = withEndZero;
        a.recycle();
    }

    public void setText(String text) {
        this.value = text;
        calcTextDimens();
        requestLayout();
    }
}