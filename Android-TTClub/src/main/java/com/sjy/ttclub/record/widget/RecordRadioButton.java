package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RadioButton;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordRadioButton extends RadioButton {
    private boolean mEnableToggle = true;

    public RecordRadioButton(Context context) {
        super(context);
        init();
    }

    public RecordRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setButtonDrawable(android.R.color.transparent);
        setTextColor(ResourceHelper.getColorStateList(R.color.record_item_text_color));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_12));
        setGravity(Gravity.CENTER);
        setPadding(0, 0, 0, 0);
        setBackgroundDrawable(ResourceHelper.getDrawable(R.drawable.record_window_list_items_btn));
    }

    public void setHolidayView() {
        setGravity(Gravity.CENTER);
        setButtonDrawable(android.R.color.transparent);
        setTextColor(ResourceHelper.getColorStateList(R.color.record_item_holiday_text_color));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_12));
        setBackgroundDrawable(ResourceHelper.getDrawable(R.drawable.record_window_list_item_holiday_btn));
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
