package com.sjy.ttclub.widget.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

/**
 * Created by linhz on 2015/12/24.
 * Email: linhaizhong@ta2she.com
 */
public class SimpleTextDialog extends GenericDialog {
    private LinearLayout mTextContainer;
    private TextView mTextView;
    private TextView mSubTextView;


    public SimpleTextDialog(Context context) {
        super(context);
    }

    public void setText(int textRes) {
        setText(ResourceHelper.getString(textRes));
    }

    public void setText(String text) {
        initContainer();
        mTextView.setText(text);
        if (StringUtils.isEmpty(text)) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setSubText(int textRes) {
        setSubText(ResourceHelper.getString(textRes));
    }

    public void setSubText(String text) {
        initContainer();
        mSubTextView.setText(text);
        if (StringUtils.isEmpty(text)) {
            mSubTextView.setVisibility(View.GONE);
        } else {
            mSubTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initContainer() {
        if (mTextContainer == null) {
            mTextContainer = new LinearLayout(getContext());
            mTextContainer.setOrientation(LinearLayout.VERTICAL);
            addContentView(mTextContainer);

            mTextView = new TextView(getContext());
            mTextView.setVisibility(View.GONE);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_16));
            mTextView.setTextColor(ResourceHelper.getColor(R.color.dialog_text_color));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mTextContainer.addView(mTextView, lp);

            mSubTextView = new TextView(getContext());
            mSubTextView.setVisibility(View.GONE);
            mSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_14));
            mSubTextView.setTextColor(ResourceHelper.getColor(R.color.dialog_text_color));
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mTextContainer.addView(mSubTextView, lp);
        }
    }

}
