package com.sjy.ttclub.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.ProgressWheel;

/**
 * Created by linhz on 2015/12/24.
 * Email: linhaizhong@ta2she.com
 */
public class ProgressDialog extends GenericDialog {
    private ProgressWheel mProgressWheel;
    private TextView mTextView;

    private int mMaxProgress = 100;

    public ProgressDialog(Context context) {
        super(context);
        mRootView.setMinimumWidth((int) (HardwareUtil.screenWidth * 0.4f));
        initProgressLayout();
    }

    private void initProgressLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams contentLp = createContentViewLp();
        contentLp.topMargin = ResourceHelper.getDimen(R.dimen.space_8);
        contentLp.bottomMargin = contentLp.topMargin;
        addContentView(layout, contentLp);

        mProgressWheel = new ProgressWheel(getContext());
        mProgressWheel.spin();
        int size = ResourceHelper.getDimen(R.dimen.space_50);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.gravity = Gravity.CENTER_VERTICAL;
        layout.addView(mProgressWheel, lp);

        mTextView = new TextView(getContext());
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_16));
        mTextView.setTextColor(ResourceHelper.getColor(R.color.dialog_text_color));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams
                .WRAP_CONTENT);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.space_16);
        lp.gravity = Gravity.CENTER_VERTICAL;
        layout.addView(mTextView, lp);
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        int current = 100 * progress / mMaxProgress;
        if (current > 100) {
            current = 100;
        }
        mTextView.setText(current + "%");
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setText(int textId) {
        mTextView.setText(ResourceHelper.getString(textId));
    }

    @Override
    public void dismiss() {
        mProgressWheel.stopSpinning();
        super.dismiss();
    }
}
