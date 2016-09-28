
package com.sjy.ttclub.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

public class TabCursor extends View {

    public final static String TAG = "TabCursor";

    public final static int STYLE_LINE = 0;
    public final static int STYLE_LINE_FADEOUT = 1;
    public final static int STYLE_DRAWABLE = 2;

    private int mStyle = STYLE_LINE;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mPadding = 0;
    private int mLineColor = 0xff85b9e7;
    private Drawable mDrawable = null;
    private boolean mDrawEnabled = true;

    private int mLeftOffset = 0;
    private Paint mPaint = new Paint();

    private FadeTimer mTimer;
    private int mFadeOutDelay = 500;
    private int mFadeOutDuration = 200;
    private int mMaxAlpha = 0xFF;
    private int mAlpha = mMaxAlpha;

    public TabCursor(Context context) {
        this(context, null);
    }

    public TabCursor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabCursor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize(int width, int height, int padding, int color,
                           boolean fadeOut) {
        this.mWidth = width;
        this.mHeight = height;
        this.mPadding = padding;
        this.mLineColor = color;
        this.mStyle = fadeOut ? STYLE_LINE_FADEOUT : STYLE_LINE;
    }

    public void initialize(int width, int height, int padding, Drawable drawable) {
        this.mWidth = width;
        this.mHeight = height;
        this.mPadding = padding;
        this.mDrawable = drawable;
        this.mStyle = STYLE_DRAWABLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mDrawEnabled)
            return;

        if (mStyle == STYLE_DRAWABLE) {
            if (mDrawable != null) {
                Rect bounds = new Rect(mLeftOffset + mPadding, getHeight()
                        - mHeight, mLeftOffset + mWidth - mPadding, getHeight());
                mDrawable.setBounds(bounds);
                mDrawable.draw(canvas);
            }
        } else {
            final int color = Color.argb(mAlpha, Color.red(mLineColor),
                    Color.green(mLineColor), Color.blue(mLineColor));
            mPaint.setColor(color);
            canvas.drawRect(mLeftOffset + mPadding, getHeight() - mHeight,
                    mLeftOffset + mWidth - mPadding, getHeight(), mPaint);
        }

    }

    public void moveTo(int x) {
        mLeftOffset = x;
        resetTimer();
        invalidate();
    }

    public int getCursorStyle() {
        return this.mStyle;
    }

    public void setCursorStyle(int style) {
        this.mStyle = style;
        this.mAlpha = mMaxAlpha;
        resetTimer();
        invalidate();
    }

    public int getCursorWidth() {
        return this.mWidth;
    }

    public void setCursorWidth(int width) {
        this.mWidth = width;
    }

    public int getCursorHeight() {
        return this.mHeight;
    }

    public void setCursorHeight(int height) {
        this.mHeight = height;
    }

    public int getCursorPadding() {
        return this.mPadding;
    }

    public void setCursorPadding(int padding) {
        this.mPadding = padding;
    }

    public int getCursorColor() {
        return this.mLineColor;
    }

    public void setCursorColor(int lineColor) {
        this.mLineColor = lineColor;
        invalidate();
    }

    public Drawable getCursorDrawable() {
        return this.mDrawable;
    }

    public void setCursorDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        invalidate();
    }

    public boolean isCursorDrawEnabled() {
        return mDrawEnabled;
    }

    public void setCursorDrawEnabled(boolean enabled) {
        this.mDrawEnabled = enabled;
    }

    public int getFadeOutDelay() {
        return this.mFadeOutDelay;
    }

    public void setFadeOutDelay(int milliseconds) {
        this.mFadeOutDelay = milliseconds;
        invalidate();
    }

    public int getFadeOutDuration() {
        return this.mFadeOutDuration;
    }

    public void setFadeOutDuration(int milliseconds) {
        this.mFadeOutDuration = milliseconds;
        invalidate();
    }

    private void setAlpha(int alpha) {
        this.mAlpha = alpha;
        invalidate();
    }

    private void resetTimer() {
        if (mStyle != STYLE_LINE_FADEOUT) {
            return;
        }

        if (mFadeOutDelay > 0) {
            if (mTimer == null || mTimer.isRunning == false) {
                mTimer = new FadeTimer();
                mTimer.execute();
            } else {
                mTimer.reset();
            }
            mAlpha = 0xFF;
        }

    }

    private class FadeTimer extends AsyncTask<Void, Integer, Void> {

        private int elapsed = 0;
        private boolean isRunning = true;

        public void reset() {
            elapsed = 0;
        }

        @Override
        protected Void doInBackground(Void... args) {
            while (isRunning) {
                try {
                    Thread.sleep(1);
                    elapsed++;
                    if (elapsed >= mFadeOutDelay
                            && elapsed < mFadeOutDelay + mFadeOutDuration) {
                        int newAlpha = mMaxAlpha - (elapsed - mFadeOutDelay)
                                * mMaxAlpha / mFadeOutDuration;
                        publishProgress(newAlpha);

                    } else if (elapsed >= mFadeOutDelay + mFadeOutDuration) {
                        isRunning = false;
                    }
                } catch (InterruptedException e) {

                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... alpha) {
            setAlpha(alpha[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            setAlpha(0x00);
        }
    }

}
