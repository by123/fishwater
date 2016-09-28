
package com.sjy.ttclub.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * @author 张武林
 *         没有数据和网络的背景布局
 */
public class LoadingLayout extends LinearLayout implements OnClickListener {
    private TextView mSignView;
    private ImageView mIconView;
    private ProgressBar mPBLoading;
    private TextView mLoadAgainButton;
    private OnBtnClickListener mListener;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loading_layout, this);
        mIconView = (ImageView) view.findViewById(R.id.bg_icon);
        mSignView = (TextView) view.findViewById(R.id.bg_sign);
        mLoadAgainButton = (TextView) view.findViewById(R.id.btn_load_again);
        mPBLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
    }

    public void hide() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
        }
    }

    public void setBgContent(int resouceId, String tips, boolean isShowBtn) {
        if (resouceId == 0) {
            mIconView.setImageDrawable(null);
//            mPBLoading.setIndeterminateDrawable(null);
        } else {
            if (R.anim.loading == resouceId) {
                Log.v("setBgContent", resouceId + "");
                mIconView.setImageDrawable(null);
                mIconView.setVisibility(INVISIBLE);
            } else {
//                mPBLoading.setVisibility(INVISIBLE);
                mPBLoading.setIndeterminateDrawable(null);
                mIconView.setImageResource(resouceId);
                mIconView.setVisibility(VISIBLE);
            }
        }
        mSignView.setText(tips);
        if (isShowBtn) {
            mLoadAgainButton.setVisibility(View.VISIBLE);
            mLoadAgainButton.setOnClickListener(this);
        } else {
            mLoadAgainButton.setVisibility(View.GONE);
        }
    }

    public void setDefaultLoading() {
        setVisibility(VISIBLE);
        setBgContent(R.anim.loading, ResourceHelper.getString(R.string.loading), false);
    }

    public void setDefaultNetworkError(boolean isShowBtn) {
        setVisibility(VISIBLE);
        int resId = isShowBtn ? R.string.network_error_retry : R.string.no_network;
        setBgContent(R.drawable.no_network, ResourceHelper.getString(resId), isShowBtn);
    }

    public void setBtnOnClickListener(OnBtnClickListener listener) {
        this.mListener = listener;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_again:
                if (mListener != null) {
                    mListener.OnClick();
                }
                break;
            default:
                break;
        }
    }

    public interface OnBtnClickListener {
        void OnClick();
    }

    public void setBtnText(String msg) {
        mLoadAgainButton.setText(msg);
    }
}
