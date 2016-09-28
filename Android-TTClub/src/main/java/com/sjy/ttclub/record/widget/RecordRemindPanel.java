package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by gangqing on 2016/1/6.
 * Email:denggangqing@ta2she.com
 */
public class RecordRemindPanel extends BasePanel implements View.OnClickListener {
    private ImageView mIcon;
    private ImageView mButton;
    private PanelButtonClickListener mListener;
    public RecordRemindPanel(Context context) {
        super(context);
        initView();
        setAnimation(R.style.SexDataPanelAnim);
        setDimValue(0.8f);
    }

    @Override
    protected View onCreateContentView() {
        View view = View.inflate(mContext, R.layout.record_panel, null);
        return view;
    }

    @Override
    protected FrameLayout.LayoutParams getLayoutParams() {
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.MATCH_PARENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        return lp;
    }

    private void initView() {
        mIcon = (ImageView) mContentView.findViewById(R.id.record_panel_ico);
        mButton = (ImageView) mContentView.findViewById(R.id.record_panel_btn);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_panel_btn:
                this.hidePanel();
                clickBtn();
                break;
        }
    }

    public void clickBtn() {
        if(mListener!=null){
            mListener.onButtonClick();
        }
    }

    public void setOnPanelButtonClickListener(PanelButtonClickListener listener){
        this.mListener=listener;
    }
    public interface PanelButtonClickListener {
        void onButtonClick();
    }
    /**
     * 设置提示图文
     *
     * @param resId
     */
    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
    }

    /**
     * 设置显示"知道啦"
     */
    public void setKnowShow() {
        mButton.setImageResource(R.drawable.record_panel_know);
    }

    /**
     * 设置显示“好好好”
     */
    public void setGoodShow() {
        mButton.setImageResource(R.drawable.record_panel_good);
    }
}
