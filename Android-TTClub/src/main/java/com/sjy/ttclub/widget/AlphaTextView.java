package com.sjy.ttclub.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * @author: zhangwl
 */
public class AlphaTextView extends TextView {

    private static final float DEFAULT_ALPHA = 1f;
    private static final float DISABLED_ALPHA = 0f;
    private static final float PR_ALPHA = 0.7f;

    public AlphaTextView(Context context) {
        super(context);
    }

    public AlphaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        changeImageStateDrawable(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        final boolean rt = super.onTouchEvent(event);
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    changeImageStateDrawable(true);
                    postDelayed(mStateWatchdog, 200);
                    break;
                case MotionEvent.ACTION_MOVE:
                    removeCallbacks(mStateWatchdog);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    removeCallbacks(mStateWatchdog);
                    /**
                     * post the change requestWithLoading to avoid the down and up drawable
                     * state changing happen at the same frame
                     */
                    post(new Runnable() {

                        @Override
                        public void run() {
                            changeImageStateDrawable(false);
                        }

                    });
                    break;
            }
        }
        return rt;
    }

    private Runnable mStateWatchdog = new Runnable() {
        @Override
        public void run() {
            changeImageStateDrawable(false);
        }
    };

    private void changeImageStateDrawable(boolean isPressed) {
        if (!isEnabled()) {
            setAlpha(DISABLED_ALPHA);
        } else {
            if (isPressed) {
                setAlpha(PR_ALPHA);
            } else {
                setAlpha(DEFAULT_ALPHA);
            }
        }
    }
}
