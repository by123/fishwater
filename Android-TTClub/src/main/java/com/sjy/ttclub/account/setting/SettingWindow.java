package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;

/**
 * Created by gangqing on 2015/11/25.
 * Email:denggangqing@ta2she.com
 */
public class SettingWindow extends DefaultWindow implements View.OnClickListener {
    private TextView mCleanCache;
    private Button mLogout;
    private String totalCacheSize;

    public SettingWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_setting_title);
        View view = View.inflate(getContext(), R.layout.account_setting_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        view.findViewById(R.id.account_setting_service).setOnClickListener(this);
        view.findViewById(R.id.account_setting_help).setOnClickListener(this);
        view.findViewById(R.id.account_setting_about_me).setOnClickListener(this);
        view.findViewById(R.id.account_setting_grade).setOnClickListener(this);
        view.findViewById(R.id.account_setting_feedback).setOnClickListener(this);
        view.findViewById(R.id.account_setting_alert).setOnClickListener(this);
        mCleanCache = (TextView) view.findViewById(R.id.account_setting_clean_cache);
        mCleanCache.setOnClickListener(this);
        mLogout = (Button) view.findViewById(R.id.account_setting_logout);
        mLogout.setOnClickListener(this);

        initView();
    }

    private void initView() {
        setLogout();
        setCleanCache();
    }

    /**
     * 判断退出按钮是否显示
     */
    private void setLogout() {
        if (AccountManager.getInstance().isLogin()) {
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示缓存的大小
     */
    private void setCleanCache() {
        ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {
            @Override
            public void run() {
                totalCacheSize = CommonUtils.getTotalCacheSize(getContext());
            }
        }, new Runnable() {
            @Override
            public void run() {
                mCleanCache.setText("清除缓存 " + totalCacheSize);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_setting_alert:    //提醒设置
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_ALERT_SETTING_WINDOW);
                break;
            case R.id.account_setting_service:  //客服
                callService();
                break;
            case R.id.account_setting_feedback: //意见反馈
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_FEEDBACK_WINDOW);
                break;
            case R.id.account_setting_help: //帮助
                goToWebView(getContext().getResources().getString(R.string.account_setting_help_center), HttpUrls
                        .HELP_URL);
                break;
            case R.id.account_setting_about_me: //关于她他社
                goToWebView(getContext().getResources().getString(R.string.account_setting_about_me), HttpUrls
                        .ABOUT_URL + SystemHelper.getAppInfo().versionName);
                break;
            case R.id.account_setting_grade:    //应用评分
                goToGrade();
                break;
            case R.id.account_setting_clean_cache:  //清理缓存
                startCleanCache();
                break;
            case R.id.account_setting_logout:   //退出登录
                logout();
                break;
        }
    }

    private void callService() {
        SimpleTextDialog dialog = new SimpleTextDialog(getContext());
        dialog.addTitle(R.string.account_alert_dialog_title);
        dialog.setText(R.string.account_alert_dialog_message);
        dialog.addYesNoButton(ResourceHelper.getString(R.string.account_alert_dialog_negative),
                ResourceHelper.getString(R.string.account_alert_dialog_positive));
        dialog.setRecommendButton(GenericDialog.ID_BUTTON_YES);
        dialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    Uri uri = Uri.parse(ResourceHelper.getString(R.string.account_alert_dialog_uri));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(uri);
                    try {
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        dialog.show();
    }

    private void goToWebView(String title, String url) {
        Message msg = new Message();
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = title;
        params.url = url;
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void goToGrade() {
        Uri uri = Uri.parse(HttpUrls.APPLICATION_GRADE);
        String title = ResourceHelper.getString(R.string.account_setting_grade_title);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getContext().startActivity(Intent.createChooser(intent, title));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCleanCache() {
        SimpleTextDialog dialog = new SimpleTextDialog(getContext());
        dialog.addTitle(R.string.account_setting_alert_clean_cache_title);
        dialog.setText(R.string.account_setting_alert_clean_cache_message);
        dialog.addYesNoButton();
        dialog.setRecommendButton(GenericDialog.ID_BUTTON_NO);
        dialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    CommonUtils.clearAllCache(getContext(), new Runnable() {
                        @Override
                        public void run() {
                            ToastHelper.showToast(getContext(), R.string.account_setting_alert_clean_cache_message_success,
                                    Toast.LENGTH_SHORT);
                            setCleanCache();
                        }
                    });
                }
                return false;
            }
        });
        dialog.show();
    }

    private void logout() {
        SimpleTextDialog dialog = new SimpleTextDialog(getContext());
        dialog.addTitle(R.string.account_setting_alert_logout_title);
        dialog.setText(R.string.account_setting_alert_logout_message_sure);
        dialog.addYesNoButton();
        dialog.setRecommendButton(GenericDialog.ID_BUTTON_NO);
        dialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    AccountManager.getInstance().logout();
//                    mCallBacks.onWindowExitEvent(SettingWindow.this, false);
                    AccountManager.getInstance().tryOpenGuideLoginWindow(true);
                }
                return false;
            }
        });
        dialog.show();

    }
}
