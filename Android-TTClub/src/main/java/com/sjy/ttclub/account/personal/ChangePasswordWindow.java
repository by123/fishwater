package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.account.model.PersonalDataChangeRequest;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;

import java.util.ArrayList;

/**
 * Created by gangqing on 2015/12/17.
 * Email:denggangqing@ta2she.com
 */
public class ChangePasswordWindow extends DefaultWindow implements View.OnClickListener {
    private EditText mOriginalPassword;
    private EditText mNewPassword;
    private EditText mNewPasswordAgain;

    public ChangePasswordWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_personal_password_change_title);
        initActionBar();
        View view = View.inflate(getContext(), R.layout.account_personal_password_change, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        mOriginalPassword = (EditText) view.findViewById(R.id.account_personal_password_change_original);
        mNewPassword = (EditText) view.findViewById(R.id.account_personal_password_change_new);
        mNewPasswordAgain = (EditText) view.findViewById(R.id.account_personal_password_change_new_again);
        view.findViewById(R.id.account_personal_password_change_original_image).setOnClickListener(this);
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.yes));
        item.setItemId(MenuItemIdDef.TITLEBAR_PASSWORD_CHANGE_CONFIRM);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        String password = mOriginalPassword.getText().toString();
        String newPassword = mNewPassword.getText().toString();
        String newPasswordAgain = mNewPasswordAgain.getText().toString();
        if (password.length() < 6) {
            ToastHelper.showToast(getContext(), R.string.account_personal_password_change_alert_1, Toast.LENGTH_SHORT);
        } else if (newPassword.length() < 6) {
            ToastHelper.showToast(getContext(), R.string.account_personal_password_change_alert_2, Toast.LENGTH_SHORT);
        } else if (newPasswordAgain.length() < 6 || !newPasswordAgain.equals(newPassword)) {
            ToastHelper.showToast(getContext(), R.string.account_personal_password_change_alert_3, Toast.LENGTH_SHORT);
        } else if(password.equals(newPassword)){
            ToastHelper.showToast(getContext(), R.string.account_personal_password_change_alert_4, Toast.LENGTH_SHORT);
        } else{
            PersonalDataChangeRequest personalDataChangeRequest = new PersonalDataChangeRequest(getContext());
            personalDataChangeRequest.changePassword(password, newPassword, new AccountBaseIHttpCallBack() {
                @Override
                public <T> void onSuccess(T obj, String result) {
                    ToastHelper.showToast(getContext(), "修改密码成功", Toast.LENGTH_SHORT);
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_PASSWORD_CHANGE_WINDOW);
                }

                @Override
                public void onFail(String errorStr, int code) {
                    if (code == HttpCode.ERROR_PASSWORD) {
                        ToastHelper.showToast(getContext(), "密码错误", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_personal_password_change_original_image:
                mOriginalPassword.setText(null);
                break;
        }
    }
}
