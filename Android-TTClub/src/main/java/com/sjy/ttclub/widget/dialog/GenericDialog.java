
package com.sjy.ttclub.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;

@SuppressWarnings("ResourceType")
public class GenericDialog extends Dialog implements View.OnClickListener {

    public static final int CMD_SHOW = 0x911114;
    public static final int CMD_HIDE = CMD_SHOW + 1;
    public static final int CMD_DISMISS = CMD_SHOW + 2;
    public static final int CMD_BACK = CMD_SHOW + 1001;

    public static final int ID_BUTTON_YES = 0x23456;
    public static final int ID_BUTTON_NO = 0x23457;

    protected LinearLayout mRootView;
    protected IDialogCmdListener mCmdListener;
    protected IDialogOnClickListener mClickListener;

    protected TextView mTitleView;

    protected boolean mHasBackKeyDown = false;

    public GenericDialog(Context context) {
        super(context, R.style.CommonDialogTheme);
        setCanceledOnTouchOutside(false);

        mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        mRootView.setBackgroundResource(R.drawable.material_background);
        mRootView.setMinimumWidth((int) (HardwareUtil.screenWidth * 0.7f));
        super.setContentView(mRootView);
    }

    public void addTitle(int titleResId) {
        String title = getContext().getString(titleResId);
        addTitle(title);
    }

    public void addTitle(String title) {
        if (mTitleView == null) {
            mTitleView = new TextView(getContext());
            mTitleView.setTextColor(ResourceHelper.getColor(R.color.dialog_title_color));
            mTitleView.setGravity(Gravity.LEFT | Gravity.TOP);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen
                    .dialog_title_text_size));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.leftMargin = ResourceHelper.getDimen(R.dimen.dialog_divider_marginHorizontal);
            lp.topMargin = ResourceHelper.getDimen(R.dimen.dialog_marginVertical);
            lp.rightMargin = lp.leftMargin;
            mRootView.addView(mTitleView, lp);
        }
        mTitleView.setText(title);
    }

    public void setTitle(String title) {
        if (mTitleView == null) {
            addTitle(title);
        } else {
            mTitleView.setText(title);
        }
    }

    public void addContentView(View contentView) {
        if (contentView == null) {
            return;
        }
        LinearLayout.LayoutParams lp = createContentViewLp();
        addContentView(contentView, lp);
    }

    public void addContentView(View contentView, LinearLayout.LayoutParams lp) {
        if (contentView == null) {
            return;
        }
        mRootView.addView(contentView, lp);
    }

    public LinearLayout.LayoutParams createContentViewLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 0);
        lp.leftMargin = ResourceHelper.getDimen(R.dimen.dialog_divider_marginHorizontal);
        lp.rightMargin = lp.leftMargin;
        lp.topMargin = ResourceHelper.getDimen(R.dimen.dialog_marginVertical);
        lp.bottomMargin = ResourceHelper.getDimen(R.dimen.dialog_marginVertical);

        lp.weight = 1;
        return lp;
    }

    public void addSingleButton(int id, String text) {
        DialogButton button = createButton(text, id);
        mRootView.addView(button, createButtonLP());
    }

    public void addYesNoButton() {
        Resources res = getContext().getResources();
        String yesStr = res.getString(R.string.yes);
        String cancelStr = res.getString(R.string.cancel);
        addYesNoButton(yesStr, cancelStr);
    }


    public void addYesNoButton(String yes, String no) {
        LinearLayout parentView = new LinearLayout(getContext());
        parentView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = createButtonLP();
        mRootView.addView(parentView, lp);

        DialogButton buttonNo = createButton(no, ID_BUTTON_NO);
        lp = createButtonLP();
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.topMargin = 0;
        lp.bottomMargin = 0;
        parentView.addView(buttonNo, lp);

        DialogButton buttonYes = createButton(yes, ID_BUTTON_YES);
        lp = createButtonLP();
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.topMargin = 0;
        lp.bottomMargin = 0;
        parentView.addView(buttonYes, lp);
    }

    protected DialogButton createButton(String text, int id) {
        DialogButton b = new DialogButton(getContext());
        b.setId(id);
        b.setText(text);
        b.setOnClickListener(this);
        return b;
    }

    protected LinearLayout.LayoutParams createButtonLP() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT | Gravity.TOP;
        lp.rightMargin = ResourceHelper.getDimen(R.dimen.space_6);
        lp.bottomMargin = ResourceHelper.getDimen(R.dimen.space_14);
        return lp;
    }

    public void setRecommendButton(int buttonId) {
        View view = mRootView.findViewById(buttonId);
        if (view instanceof DialogButton) {
            ((DialogButton) view).setTextHighlightEnabled(true);
        }
    }


    public void setOnCmdListener(IDialogCmdListener listener) {
        mCmdListener = listener;
    }

    public void setOnClickListener(IDialogOnClickListener listener) {
        mClickListener = listener;
    }


    @Override
    public void onClick(View v) {
        boolean handle = false;
        if (mClickListener != null) {
            handle = mClickListener.onDialogClick(GenericDialog.this,
                    v.getId(), null);
        }
        if (!handle) {
            boolean isYes = v.getId() == ID_BUTTON_YES;
            boolean isNo = v.getId() == ID_BUTTON_NO;
            if (isYes || isNo) {
                this.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCmdListener != null) {
            mCmdListener.onDialogCmd(GenericDialog.this, 0, CMD_SHOW, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCmdListener != null) {
            mCmdListener.onDialogCmd(GenericDialog.this, 0, CMD_DISMISS, null);
        }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hide() {
        if (mCmdListener != null) {
            mCmdListener.onDialogCmd(GenericDialog.this, 0, CMD_HIDE, null);
        }
        try {
            super.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN)
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mHasBackKeyDown = true;
        }
        if (mHasBackKeyDown && (event.getAction() == KeyEvent.ACTION_UP)
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mHasBackKeyDown = false;
            if (mCmdListener != null) {
                mCmdListener.onDialogCmd(GenericDialog.this, 0, CMD_BACK, null);
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
