package com.sjy.ttclub.guide;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.util.ApplicationExitHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhangwulin on 2016/1/5.
 * email 1501448275@qq.com
 */
public class GuideNewUserWindow extends DefaultWindow {
    private SimpleDraweeView mUserHeadIcon;
    private TextView mUserName;
    private AlphaTextView mOkButton;
    private TextView mManSexButton;
    private TextView mWomanSexButton;
    private TextView mSingleSateButton;
    private TextView mLoveStateButton;
    private TextView mMarriageStateButton;
    private int mUserSex;
    private int mUserMarriageState;
    private ApplicationExitHelper mExitHelper;

    public GuideNewUserWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        createView();
        StatsModel.stats(StatsKeyDef.GUIDE_VIEW);
        mExitHelper = new ApplicationExitHelper(context);
    }

    @Override
    protected View onCreateTitleBar() {
        return null;
    }

    @Override
    public boolean onWindowBackKeyEvent() {
        return true;
    }

    private void createView() {
        View view = View.inflate(getContext(), R.layout.guide_new_user_layout, null);
        getBaseLayer().addView(view, getBaseLayerLP());
        mManSexButton = (TextView) view.findViewById(R.id.guide_man_sex_button);
        mManSexButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnManSexClick();
            }
        });
        mWomanSexButton = (TextView) view.findViewById(R.id.guide_woman_sex_button);
        mWomanSexButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnWoManSexClick();
            }
        });
        mSingleSateButton = (TextView) view.findViewById(R.id.guide_single_state_button);
        mSingleSateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnSingleStateClick();
            }
        });
        mLoveStateButton = (TextView) view.findViewById(R.id.guide_love_state_button);
        mLoveStateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnLoveStateClick();
            }
        });
        mMarriageStateButton = (TextView) view.findViewById(R.id.guide_merried_state_button);
        mMarriageStateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnMerriedStateClick();
            }
        });
        mUserHeadIcon = (SimpleDraweeView) view.findViewById(R.id.guide_user_head_icon);
        mUserName = (TextView) view.findViewById(R.id.guide_user_name);
        mOkButton = (AlphaTextView) view.findViewById(R.id.guide_over_button);
        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnOkButtonClick();
            }
        });
        initUserData();
    }

    private void initUserData() {
        AccountManager.getInstance().setHeadImage(mUserHeadIcon);
        if(!AccountManager.getInstance().isLogin()){
            return;
        }
        AccountInfo info = AccountManager.getInstance().getAccountInfo();
        if (StringUtils.isNotEmpty(info.getNickname())) {
            mUserName.setText(ResourceHelper.getString(R.string.community_hello_new_user) + info.getNickname());
        } else {
            mUserName.setText(ResourceHelper.getString(R.string.community_hello_new_user));
        }
    }

    private void handlerOnManSexClick() {
        if (mManSexButton.isSelected() || mWomanSexButton.isSelected()) {
            return;
        }
        final SimpleTextDialog simpleTextDialog = new SimpleTextDialog(getContext());
        simpleTextDialog.setText(ResourceHelper.getString(R.string.community_guide_select_sex_tip));
        simpleTextDialog.addYesNoButton(ResourceHelper.getString(R.string.community_guide_select_sex_sure),
                ResourceHelper.getString(R
                        .string.cancel));
        simpleTextDialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    mManSexButton.setSelected(true);
                    mWomanSexButton.setSelected(false);
                } else if (viewId == GenericDialog.ID_BUTTON_NO) {
                    mManSexButton.setSelected(false);
                    mWomanSexButton.setSelected(false);
                }
                return false;
            }
        });
        simpleTextDialog.setRecommendButton(GenericDialog.ID_BUTTON_YES);
        simpleTextDialog.setCanceledOnTouchOutside(false);
        simpleTextDialog.show();
    }

    private void handlerOnWoManSexClick() {
        if (mManSexButton.isSelected() || mWomanSexButton.isSelected()) {
            return;
        }
        final SimpleTextDialog simpleTextDialog = new SimpleTextDialog(getContext());
        simpleTextDialog.setText(ResourceHelper.getString(R.string.community_guide_select_sex_tip));
        simpleTextDialog.addYesNoButton(ResourceHelper.getString(R.string.community_guide_select_sex_sure),
                ResourceHelper.getString(R
                        .string.cancel));
        simpleTextDialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    mManSexButton.setSelected(false);
                    mWomanSexButton.setSelected(true);
                } else if (viewId == GenericDialog.ID_BUTTON_NO) {
                    mManSexButton.setSelected(false);
                    mWomanSexButton.setSelected(false);
                }
                return false;
            }
        });
        simpleTextDialog.setRecommendButton(GenericDialog.ID_BUTTON_YES);
        simpleTextDialog.setCanceledOnTouchOutside(false);
        simpleTextDialog.show();
    }

    private void handlerOnSingleStateClick() {
        mSingleSateButton.setSelected(true);
        mLoveStateButton.setSelected(false);
        mMarriageStateButton.setSelected(false);
    }

    private void handlerOnLoveStateClick() {
        mSingleSateButton.setSelected(false);
        mLoveStateButton.setSelected(true);
        mMarriageStateButton.setSelected(false);
    }

    private void handlerOnMerriedStateClick() {
        mSingleSateButton.setSelected(false);
        mLoveStateButton.setSelected(false);
        mMarriageStateButton.setSelected(true);
    }

    /*  性别。男：1；女：2；
        婚姻状态。单身：1；恋爱：2；已婚：4。
        性生活程度。无：1；生涩：2；一般：4；娴熟：8；默认为8*/
    private void trySaveUserSexData() {
        if (mManSexButton.isSelected()) {
            mUserSex = CommonConst.SEX_MAN;
            return;
        }
        if (mWomanSexButton.isSelected()) {
            mUserSex = CommonConst.SEX_WOMAN;
            return;
        }
    }

    private void trySaveMarriageStateData() {
        if (mSingleSateButton.isSelected()) {
            mUserMarriageState = CommonConst.MARRIAGE_STATE_SINGLE;
            return;
        }
        if (mLoveStateButton.isSelected()) {
            mUserMarriageState = CommonConst.MARRIAGE_STATE_INLOVE;
            return;
        }
        if (mMarriageStateButton.isSelected()) {
            mUserMarriageState = CommonConst.MARRIAGE_STATE_MARRIAGED;
            return;
        }
    }

    private void handlerOnOkButtonClick() {
        if (!mManSexButton.isSelected() && !mWomanSexButton.isSelected()) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_guide_no_select_sex));
            return;
        }
        if (!mSingleSateButton.isSelected() && !mLoveStateButton.isSelected() && !mMarriageStateButton.isSelected()) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_guide_no_select_marry_state));
            return;
        }
        trySaveUserSexData();
        trySaveMarriageStateData();
        AccountManager.getInstance().saveSexMarriageSexyLife(mUserSex, mUserMarriageState, CommonConst.DEFAULT_SEX_SKILL);
        SettingFlags.setFlag(CommonConst.ISFIRST_LOGIN, false);
        tryUploadUserData();
        NotificationCenter.getInstance().notify(Notification.obtain(NotificationDef.N_HOMEPAGE_IS_INIT_SHOW));

        //统计
        String statsSexValue="";
        String statsMerriedStateValue="";
        if(mUserSex==CommonConst.SEX_MAN){
            statsSexValue=StatsKeyDef.VALUE_MAN;
        }else if(mUserSex==CommonConst.SEX_WOMAN){
            statsSexValue=StatsKeyDef.VALUE_WOMAN;
        }
        if(mUserMarriageState==CommonConst.MARRIAGE_STATE_SINGLE){
            statsMerriedStateValue=StatsKeyDef.GUIDE_MERRIED_VALUE_SINGLE;
        }if(mUserMarriageState==CommonConst.MARRIAGE_STATE_MARRIAGED){
            statsMerriedStateValue=StatsKeyDef.GUIDE_MERRIED_VALUE_MARRIED;
        }if(mUserMarriageState==CommonConst.MARRIAGE_STATE_INLOVE){
            statsMerriedStateValue=StatsKeyDef.GUIDE_MERRIED_VALUE_INVOLVED;
        }
        HashMap<String, String> map = new HashMap<String, String>(2);
        map.put(StatsKeyDef.GENDER_KEY, statsSexValue);
        map.put(StatsKeyDef.GUIDE_MERRIED_KEY, statsMerriedStateValue);
        StatsModel.stats(StatsKeyDef.GUIDE_FINISH, map);
    }

    private void tryUploadUserData() {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "uploadUserinfo");
        httpManager.addParams("sex", String.valueOf(mUserSex));
        httpManager.addParams("marriage", String.valueOf(mUserMarriageState));
        httpManager.addParams("sexyLife", String.valueOf(CommonConst.DEFAULT_SEX_SKILL));
        httpManager.request(HttpUrls.HOMEPAGE_URL, HttpMethod.POST, null, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                handlerOnUpLoadSuccess(result);
            }

            @Override
            public void onError(String errorStr, int code) {
                handlerOnUpLoadFail(code);
            }
        });
    }

    private void handlerOnUpLoadFail(int errorCode) {
        mCallBacks.onWindowExitEvent(this, true);
    }

    private void handlerOnUpLoadSuccess(String result) {

        if (StringUtils.isEmpty(result)) {
            mCallBacks.onWindowExitEvent(this, true);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("status");
            if (HttpCode.SUCCESS_CODE == code) {
                JSONObject data = jsonObject.getJSONObject("data");
                String uuId = data.getString("uuid");
                SettingFlags.setFlag(SystemHelper.KEY_UUID, uuId);
                AccountManager.getInstance().notifyAccountDataChanged();
                mCallBacks.onWindowExitEvent(this, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onWindowKeyEvent(int keyCode, KeyEvent event) {
        mExitHelper.exitApplication();
        return true;
    }
}
