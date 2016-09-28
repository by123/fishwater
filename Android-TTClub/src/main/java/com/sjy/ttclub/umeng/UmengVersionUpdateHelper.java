package com.sjy.ttclub.umeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.system.SystemHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.umeng.update.UpdateResponse;

/**
 * Created by gangqing on 2015/12/22.
 * Email:denggangqing@ta2she.com
 */
public class UmengVersionUpdateHelper {
    private static final long UPGRADE_PERIOD = 2 * 24 * 60 * 60 * 1000;
    private static final String PREFERENCE_NAME = "check_version_update_time";
    private static final String NEW_VERSION = "new_version";
    private static final String UPGRADE_TIME = "upgrade";

    private Context mContext;
    private SharedPreferences mSharePreference;

    public UmengVersionUpdateHelper(Context context) {
        this.mContext = context;
        mSharePreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @return true:有新版本,false:当前版本是最新版本
     */
    public boolean haveNewVersion() {
        String newVersion = getNewVersion();
        String currentVersion = SystemHelper.getAppInfo().versionName;
        int haveNew = StringUtils.compareVersion(currentVersion, newVersion);
        if (haveNew == 0 || haveNew > 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getNewVersion() {
        return mSharePreference.getString(NEW_VERSION, null);
    }

    /**
     * 自动检查传false，手动检查传true
     *
     * @param isManual
     */
    public void checkUpdate(boolean isManual) {
        CheckVersionUpdateCallBack callBack = new CheckVersionUpdateCallBack();
        if (isManual) {
            UmengManager.getInstance().startCheckUpdate(isManual, callBack);
        } else {
            //上次的日期
            long time = mSharePreference.getLong(UPGRADE_TIME, 0);
            //获取当前的日期
            long currentTime = System.currentTimeMillis();
            if (currentTime - time >= UPGRADE_PERIOD) {
                UmengManager.getInstance().startCheckUpdate(isManual, callBack);
            } else {
                sendMessageVersionCheckFinish();
            }
        }
    }

    private void sendMessageVersionCheckFinish() {
        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_VERSION_CHECK_FINISH);
    }

    public class CheckVersionUpdateCallBack {
        public void callBackYes(UpdateResponse updateInfo) {
            SharedPreferences.Editor editor = mSharePreference.edit();
            //保存本次检测的日期
            editor.putLong(UPGRADE_TIME, System.currentTimeMillis());
            //保存新的版本
            editor.putString(NEW_VERSION, updateInfo.version);
            editor.commit();
            sendMessageVersionCheckFinish();
        }

        public void callBackTimeOut() {
            ToastHelper.showToast(mContext, R.string.account_version_error, Toast.LENGTH_SHORT);
        }

        public void callBackNo() {
            ToastHelper.showToast(mContext, R.string.account_version_alert, Toast.LENGTH_SHORT);
        }

        public void callBackAuto() {
            sendMessageVersionCheckFinish();
        }
    }
}
