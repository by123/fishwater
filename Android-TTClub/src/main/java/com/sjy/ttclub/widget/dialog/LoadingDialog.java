package com.sjy.ttclub.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by linhz on 2015/12/24.
 * Email: linhaizhong@ta2she.com
 */
public class LoadingDialog extends Dialog {

    private ImageView mImageView;
    private TextView mTextView;

    public LoadingDialog(Context context) {
        super(context, R.style.CommonDialogTheme);
        setContentView(initContentView());

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.6f;
        lp.gravity = Gravity.CENTER;
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                AnimationDrawable drawable = (AnimationDrawable) mImageView.getDrawable();
                drawable.start();
            }
        });
    }

    private View initContentView() {
        LinearLayout rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        mImageView = new ImageView(getContext());
        mImageView.setImageDrawable(ResourceHelper.getDrawable(R.anim.loading));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        rootView.addView(mImageView, lp);

        mTextView = new TextView(getContext());
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_12));
        mTextView.setTextColor(ResourceHelper.getColor(R.color.white));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = ResourceHelper.getDimen(R.dimen.space_10);

        rootView.addView(mTextView, lp);

        return rootView;
    }

    public void setMessage(int msgRes) {
        setMessage(ResourceHelper.getString(msgRes));
    }

    public void setMessage(String msg) {
        mTextView.setText(msg);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            AnimationDrawable drawable = (AnimationDrawable) mImageView.getDrawable();
            drawable.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
