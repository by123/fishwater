package com.sjy.ttclub.community.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by chenjiawei on 2015/12/21.
 */
public class CircleSexLimitRemidPanel extends BasePanel {
    private LinearLayout mRootPanel;
    private int mSexEntrance;
    private ImageView mButtom;


    public CircleSexLimitRemidPanel(Context context, int sexEntrance) {
        super(context, false);
        this.mSexEntrance = sexEntrance;
        setAlpha(0.8f);
        initPanel();
    }

    @Override
    protected View onCreateContentView() {
        if (mSexEntrance == CommonConst.SEX_MAN) {
            mRootPanel = (LinearLayout) View.inflate(mContext, R.layout.circle_man_remid, null);
            mButtom = (ImageView) mRootPanel.findViewById(R.id.iv_buttom);
        } else if (mSexEntrance == CommonConst.SEX_WOMAN) {
            mRootPanel = (LinearLayout) View.inflate(mContext, R.layout.circle_woman_remid, null);
            mButtom = (ImageView) mRootPanel.findViewById(R.id.iv_buttom);
        }
        mButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePanel();
            }
        });
        return mRootPanel;
    }

    @Override
    protected FrameLayout.LayoutParams getLayoutParams() {
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        return lp;
    }
}
