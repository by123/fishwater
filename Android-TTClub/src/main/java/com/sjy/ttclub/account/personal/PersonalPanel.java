package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.BasePanel;
import com.sjy.ttclub.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/5.
 * Email: linhaizhong@ta2she.com
 */
public class PersonalPanel extends BasePanel implements WheelView.OnWheelViewListener {
    private WheelView mWheelView;
    private IPersonalPanelCallback mCallback;

    public PersonalPanel(Context context) {
        super(context);
    }

    @Override
    protected View onCreateContentView() {
        View view = View.inflate(mContext, R.layout.account_personal_attr_panel, null);
        mWheelView = (WheelView) view.findViewById(R.id.account_personal_attr_wheel_view);
        mWheelView.setOnWheelViewListener(this);
        view.findViewById(R.id.account_personal_attr_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onPersonalChangedFinish(mWheelView.getCurrentPosition());
                }

                hidePanel();
            }
        });

        return view;
    }

    public void setupPanel(ArrayList<String> list, int initPosition) {
        mWheelView.setItemList(list);
        mWheelView.setCurrentPosition(initPosition);
    }

    public void setPanelCallback(IPersonalPanelCallback callback) {
        mCallback = callback;
    }

    public int getSelectedPosition() {
        return mWheelView.getCurrentPosition();
    }

    @Override
    public void onSelected(WheelView wView, int selectedIndex, String item) {
        if (mCallback != null) {
            mCallback.onPersonalItemSelected(selectedIndex);
        }
    }

    public interface IPersonalPanelCallback {
        void onPersonalItemSelected(int position);

        void onPersonalChangedFinish(int position);
    }
}
