
package com.sjy.ttclub.widget.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.widget.Button;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

public class DialogButton extends Button {

    public DialogButton(Context context) {
        super(context);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_16));
        setTextHighlightEnabled(false);
        setMinHeight(ResourceHelper.getDimen(R.dimen.space_34));
        setBackgroundResource(R.drawable.material_button);
        int paddingHorizontal = ResourceHelper.getDimen(R.dimen.space_10);
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
    }


    public void setTextHighlightEnabled(boolean aBool) {
        int resId = R.color.dialog_button_default_text_color;
        if (aBool) {
            resId = R.color.dialog_button_highlight_text_color;
        }
        setTextColor(ResourceHelper.getColor(resId));
    }

}
