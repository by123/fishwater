package com.sjy.ttclub.account.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;

/**
 * Created by gangqing on 2016/1/5.
 * Email:denggangqing@ta2she.com
 */
public class SecurityPasswordEditText extends LinearLayout implements TextWatcher, View.OnKeyListener {
    private static final int MAX_PASSWORD = 4;
    private EditText mEditText;
    private ImageView oneTextView;
    private ImageView twoTextView;
    private ImageView threeTextView;
    private ImageView fourTextView;
    private LayoutInflater inflater;
    private ImageView[] imageViews;
    private View contentView;
    private StringBuilder builder;
    private SecurityEditListener mListener;

    public SecurityPasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        builder = new StringBuilder();
        initView();
        initWidget();
    }

    private void initView() {
        contentView = inflater.inflate(R.layout.account_security_password_layout, null);
        mEditText = (EditText) contentView.findViewById(R.id.privacy_password);
        oneTextView = (ImageView) contentView.findViewById(R.id.point1);
        twoTextView = (ImageView) contentView.findViewById(R.id.point2);
        threeTextView = (ImageView) contentView.findViewById(R.id.point3);
        fourTextView = (ImageView) contentView.findViewById(R.id.point4);

        LinearLayout.LayoutParams lParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addView(contentView, lParams);
    }

    private void initWidget() {
        mEditText.addTextChangedListener(this);
        mEditText.setOnKeyListener(this);
        imageViews = new ImageView[]{oneTextView, twoTextView, threeTextView, fourTextView};
    }

    public void requestEditTextFocus(){
        mEditText.requestFocus();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == 0) {
            return;
        }
        if (builder.length() < MAX_PASSWORD) {
            builder.append(s.toString());
            setTextValue();
        }
        s.delete(0, s.length());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
            delTextValue();
            return true;
        }
        return false;
    }

    public interface SecurityEditListener {
        void onNumCompleted(String num);
    }

    public void setSecurityEditListener(SecurityEditListener listener) {
        mListener = listener;
    }

    /**
     * 保存value
     */
    private void setTextValue() {
        String value = builder.toString();
        int len = value.length();
        if (len <= MAX_PASSWORD) {
            imageViews[len - 1].setVisibility(View.VISIBLE);
        }

        if (len == MAX_PASSWORD) {
            //回调
            if (mListener != null) {
                mListener.onNumCompleted(value);
            }
            clearSecurityEdit();
        }
    }

    /**
     * 回退
     */
    private void delTextValue() {
        String str = builder.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len > 0 && len <= MAX_PASSWORD) {
            builder.delete(len - 1, len);
        }
        imageViews[len - 1].setVisibility(View.INVISIBLE);
    }

    /**
     * 清除
     */
    public void clearSecurityEdit() {
        if (builder != null) {
            if (builder.length() == MAX_PASSWORD) {
                builder.delete(0, MAX_PASSWORD);
            }
        }
        for (ImageView tv : imageViews) {
            tv.setVisibility(View.INVISIBLE);
        }
    }
}
