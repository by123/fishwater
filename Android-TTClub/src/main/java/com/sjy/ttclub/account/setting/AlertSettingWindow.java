package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.account.model.SettingRequest;
import com.sjy.ttclub.bean.account.RemindSetting;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.push.PushManager;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.LoadingLayout;

/**
 * Created by gangqing on 2016/1/12.
 * Email:denggangqing@ta2she.com
 */
public class AlertSettingWindow extends DefaultWindow implements View.OnClickListener {
    //关
    private static final String SWITCH_OFF = "0";
    //开
    private static final String SWITCH_ON = "1";
    private ImageView mMessageSwitch;
    private ImageView mCarSwitch;
    private String mIsMessageAlert;
    private String mIsCardAlert;
    private String mCurrentMessageAlert = SWITCH_ON;
    private String mCurrentCardAlert = SWITCH_ON;
    private SettingRequest mRequest;
    private LoadingLayout mLoadingLayout;

    public AlertSettingWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_alert_setting_title);
        View view = View.inflate(getContext(), R.layout.account_setting_alert_setting_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mRequest = new SettingRequest();

        initView(view);
    }

    private void initView(View view) {
        mMessageSwitch = (ImageView) view.findViewById(R.id.alert_setting_message_switch_off);
        mCarSwitch = (ImageView) view.findViewById(R.id.alert_setting_card_switch_off);
        mMessageSwitch.setOnClickListener(this);
        mCarSwitch.setOnClickListener(this);
        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.setting_alert_loading_layout);
        mLoadingLayout.setDefaultLoading();
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                getRemindSetting();
            }
        });
    }

    private void initData() {
        if (SWITCH_ON.equals(mCurrentMessageAlert)) {
            mMessageSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
        } else {
            mMessageSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
        }
        if (SWITCH_ON.equals(mCurrentCardAlert)) {
            mCarSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
        } else {
            mCarSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alert_setting_message_switch_off:    //消息与回复
                if (SWITCH_ON.equals(mIsMessageAlert)) {
                    mIsMessageAlert = SWITCH_OFF;
                    mMessageSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
                } else {
                    mIsMessageAlert = SWITCH_ON;
                    mMessageSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
                }
                break;
            case R.id.alert_setting_card_switch_off: //打卡
                if (SWITCH_ON.equals(mIsCardAlert)) {
                    mIsCardAlert = SWITCH_OFF;
                    mCarSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
                } else {
                    mIsCardAlert = SWITCH_ON;
                    mCarSwitch.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
                }
                break;
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_BEFORE_POP_OUT) {
            commit();
        } else if (STATE_AFTER_PUSH_IN == stateFlag) {
            getRemindSetting();
        }
    }

    private void getRemindSetting() {
        mRequest.getRemindSettingRequest(new AccountBaseIHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj instanceof RemindSetting) {
                    RemindSetting remindSetting = (RemindSetting) obj;
                    RemindSetting.Data data = remindSetting.getData();
                    mLoadingLayout.setVisibility(View.GONE);
                    if (data != null) {
                        mCurrentMessageAlert = data.getPrivateMsgReply();
                        //打卡记录以本地记录为准，不取服务器的数据
                        mCurrentCardAlert = PushManager.getInstance().isEnableRecordPush() ? SWITCH_ON : SWITCH_OFF;
                        mIsMessageAlert = mCurrentMessageAlert;
                        mIsCardAlert = mCurrentCardAlert;
                        initData();
                    }
                }
            }

            @Override
            public void onFail(String errorStr, int code) {
                mLoadingLayout.setDefaultNetworkError(true);
            }
        });
    }

    private void commit() {
        if (SWITCH_ON.equals(mIsCardAlert)) {
            PushManager.getInstance().addRecordTag();
            PushManager.getInstance().setEnableRecordPush(true);
        } else {
            PushManager.getInstance().removeRecordTag();
            PushManager.getInstance().setEnableRecordPush(false);
        }

        if (StringUtils.isNotEmpty(mCurrentMessageAlert) && StringUtils.isNotEmpty(mCurrentCardAlert)) {
            if (!mCurrentMessageAlert.equals(mIsMessageAlert) || !mCurrentCardAlert.equals(mIsCardAlert)) {
                mRequest.updateRemindSettingRequest(mIsMessageAlert, mIsCardAlert, new HttpCallbackAdapter() {
                    @Override
                    public <T> void onSuccess(T obj, String result) {

                    }

                    @Override
                    public void onError(String errorStr, int code) {
                        super.onError(errorStr, code);
                    }
                });
            }
        }
    }
}
