package com.sjy.ttclub.framework;

import android.view.MotionEvent;

public interface OnTouchEventInterceptor {
    boolean onInterceptTouchEvent(MotionEvent ev);
    boolean onTouchEvent(MotionEvent event);
}
