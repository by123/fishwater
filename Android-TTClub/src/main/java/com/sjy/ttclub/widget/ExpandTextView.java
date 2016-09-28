package com.sjy.ttclub.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.util.ResourceHelper;

public class ExpandTextView extends LinearLayout {
    protected TextView contentView;
    protected ImageView expandView;
    protected int textColor;
    protected float textSize;
    protected int maxLine;
    protected String text = "";
    private static final int DURATIONMILLIS = 350;
    public int defaultTextColor = Color.BLACK;
    public int defaultTextSize = 12;
    public int defaultLine = 3;
    private static final int SHRINK_UP_STATE = 0;// 收起状态
    private static final int SPREAD_STATE = 1;// 展开状态
    private int mState = SHRINK_UP_STATE;//默认收起状态
    private OnTextExpandListener mListener;
    private OnTextExpandClickListener mClickListener;

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(context, attrs);
        initView();
        initListener();
    }

    protected void initWithAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ExpandTextStyle);
        textColor = a.getColor(R.styleable.ExpandTextStyle_expandTextColor,
                defaultTextColor);
        textSize = a.getDimensionPixelSize(R.styleable.ExpandTextStyle_expandTextSize, defaultTextSize);
        maxLine = a.getInt(R.styleable.ExpandTextStyle_expandMaxLine, defaultLine);
        text = a.getString(R.styleable.ExpandTextStyle_expandText);
        a.recycle();
    }

    protected void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        contentView = new TextView(getContext());
        contentView.setLineSpacing(10, 1.0f);
        contentView.setGravity(Gravity.LEFT);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(
                ResourceHelper.getDimen(R.dimen.space_15),
                ResourceHelper.getDimen(R.dimen.space_10),
                ResourceHelper.getDimen(R.dimen.space_10),
                ResourceHelper.getDimen(R.dimen.space_10)
        );
        addView(contentView, layoutParams);
        expandView = new ImageView(getContext());
        expandView.setVisibility(GONE);
        expandView.setPadding(
                ResourceHelper.getDimen(R.dimen.space_10),
                ResourceHelper.getDimen(R.dimen.space_7),
                ResourceHelper.getDimen(R.dimen.space_10),
                ResourceHelper.getDimen(R.dimen.space_8)
        );

        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        addView(expandView, layoutParams);

        initTextView(textColor, textSize, text);
    }

    public void setExpandViewState(int state) {
        mState = state;
    }

    public int getExpandViewState() {
        return mState;
    }


    protected void initTextView(int color, float size, String text) {
        contentView.setTextColor(color);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//        setText(SHRINK_UP_STATE, "",text);
    }

    protected void initListener() {
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onTextExpandClick();
                }
            }
        });
        expandView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.clearAnimation();
                if (contentView.getLineCount() <= maxLine) {
                    return;
                }
                final int deltaValue;
                final int startValue = contentView.getHeight() + 3;
                if (mState == SHRINK_UP_STATE) {
                    deltaValue = contentView.getLineHeight() * contentView.getLineCount() - startValue;
                    mState = SPREAD_STATE;
                } else {
                    deltaValue = contentView.getLineHeight() * maxLine - startValue;
                    mState = SHRINK_UP_STATE;
                }
                Animation animation = new Animation() {
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        contentView.setHeight((int) (startValue + deltaValue * interpolatedTime));
                    }
                };
                animation.setDuration(DURATIONMILLIS);
                contentView.startAnimation(animation);
                refreshExpandViewState(mState);
                if (mListener != null) {
                    mListener.onTextExpand(mState);
                }
            }
        });
    }

    public TextView getTextView() {
        return contentView;
    }

    public String getTextViewContent() {
        return contentView.getText().toString();
    }

    private void refreshExpandViewState(int state) {
        if (state == SHRINK_UP_STATE) {
            expandView.setImageResource(R.drawable.comment_expand_icon);
        } else {
            expandView.setImageResource(R.drawable.comment_stop_icon);
        }
        refreshDrawableState();
    }

    private void isExpandViewVisiable() {
        expandView.setVisibility(contentView.getLineCount() > maxLine ? View.VISIBLE : View.GONE);
    }

    private void refreshExpandTextHeight(int state) {
        int height = contentView.getLineHeight() + 2;
        if (state == SHRINK_UP_STATE) {
            if (contentView.getLineCount() > maxLine) {
                contentView.setHeight(height * maxLine);
            } else {
                contentView.setHeight(height * contentView.getLineCount());
            }
        } else {
            contentView.setHeight(height * contentView.getLineCount());
        }
    }

    public void setText(int state, CharSequence userName, CharSequence charSequence) {
        mState = state;
        EmoticonsUtils.setContent2(getContext(), contentView, userName.toString(), charSequence.toString());
        new expandAsyncTask(state).execute();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setOnTextExpandClickListener(OnTextExpandClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnTextExpandClickListener {
        void onTextExpandClick();
    }

    public void setOnTextExpandListener(OnTextExpandListener listener) {
        this.mListener = listener;
    }

    public interface OnTextExpandListener {
        void onTextExpand(int state);
    }

    private class expandAsyncTask extends AsyncTask<Void, Void, Void> {
        private int mState;

        public expandAsyncTask(int state) {
            this.mState = state;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            isExpandViewVisiable();
            refreshExpandTextHeight(mState);
            refreshExpandViewState(mState);
            requestLayout();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

    }
}
