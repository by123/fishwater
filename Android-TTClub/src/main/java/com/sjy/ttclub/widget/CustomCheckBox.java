package com.sjy.ttclub.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by linhz on 2016/1/7.
 * Email: linhaizhong@ta2she.com
 */
public class CustomCheckBox extends CheckBox {

    private boolean mEnableToggle = false;

    public CustomCheckBox(Context context) {
        super(context);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEnableToggle(boolean enable) {
        mEnableToggle = enable;
    }

    @Override
    public void toggle() {
        if (mEnableToggle) {
            super.toggle();
        }

    }
}
