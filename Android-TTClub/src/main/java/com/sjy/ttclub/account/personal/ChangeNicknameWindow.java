package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.PersonalDataChangeRequest;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.TimeUtil;
import com.sjy.ttclub.util.ToastHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * Created by gangqing on 2015/12/17.
 * Email:denggangqing@ta2she.com
 */
public class ChangeNicknameWindow extends DefaultWindow implements View.OnClickListener {
    private static final long CD_TIME = 7 * 24 * 60 * 60 * 1000;
    private EditText mNewNickname;
    private String mNetWork;
    private String mFlagChangeNickNameLastTime;
    private AccountInfo mAccountInfo;

    public ChangeNicknameWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_personal_nickname_change_title);
        initActionBar();
        View view = View.inflate(getContext(), R.layout.account_nickname_change, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mAccountInfo = AccountManager.getInstance().getAccountInfo();
        mFlagChangeNickNameLastTime = "flag_change_nickname_last_time" + mAccountInfo.getUserId();
        mNewNickname = (EditText) view.findViewById(R.id.account_personal_change_nickname_new);
        //昵称不能使用系统表情和空格
        mNewNickname.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = CommonUtils.emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                }
                if (source.equals(" ")) {
                    return "";
                }
                return null;
            }
        }});
        mNewNickname.setText(mAccountInfo.getNickname());
        //获取当前的网络时间
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNetWork = TimeUtil.getTime();
            }
        }).start();

        view.findViewById(R.id.account_personal_change_nickname_delete).setOnClickListener(this);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.account_personal_nickname_change_keep));
        item.setItemId(MenuItemIdDef.TITLEBAR_PASSWORD_CHANGE_CONFIRM);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (StringUtils.isEmpty(mNetWork)) {
            ToastHelper.showToast(getContext(), R.string.account_version_error, Toast.LENGTH_SHORT);
            return;
        }
        if (!canChange()) {
            return;
        }

        final String newNickname = mNewNickname.getText().toString();
        String oldNickname = mAccountInfo.getNickname();
        if (newNickname.length() != 0) {
            if (newNickname.equals(oldNickname)) {
                ToastHelper.showToast(getContext(), R.string.account_personal_nickname_change_alert_1, Toast.LENGTH_SHORT);
            } else {
                PersonalDataChangeRequest personalDataChangeRequest = new PersonalDataChangeRequest(getContext());
                personalDataChangeRequest.changeNickname(newNickname, new AccountBaseIHttpCallBack() {
                    @Override
                    public <T> void onSuccess(T obj, String result) {
                        ToastHelper.showToast(getContext(), R.string.account_personal_nickname_change_success, Toast.LENGTH_SHORT);
                        mAccountInfo.setNickname(newNickname);
                        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_CHANGE_NICKNAME_WINDOW);
                        DeviceManager.getInstance().hideInputMethod();
                        notifyAccountStatus();
                        //保存当前修改昵称的时间
                        SettingFlags.setFlag(mFlagChangeNickNameLastTime, mNetWork);
                    }

                    @Override
                    public void onFail(String errorStr, int code) {
                        ToastHelper.showToast(getContext(), R.string.account_personal_nickname_change_error, Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    private boolean canChange() {
        //获取上次修改昵称的时间
        String lastTime = SettingFlags.getStringFlag(mFlagChangeNickNameLastTime, "");
        //判断是否在七天内修改昵称
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long edTime = 0;
        try {
            Date dt1 = df.parse(lastTime);
            Date dt2 = df.parse(mNetWork);
            edTime = dt2.getTime() - dt1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotEmpty(lastTime)) {
            if (edTime < CD_TIME) {
                ToastHelper.showToast(getContext(), R.string.account_personal_change_nickname_error, Toast.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    private void notifyAccountStatus() {
        Notification notification = Notification.obtain(NotificationDef.N_ACCOUNT_STATE_CHANGED);
        NotificationCenter.getInstance().notify(notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_personal_change_nickname_delete:
                mNewNickname.setText(null);
                break;
        }
    }
}
