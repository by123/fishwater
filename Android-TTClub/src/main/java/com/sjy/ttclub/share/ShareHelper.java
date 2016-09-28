package com.sjy.ttclub.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

/**
 * Created by linhz on 2015/11/12.
 * Email: linhaizhong@ta2she.com
 */
public class ShareHelper implements SharePanel.ISharePanelListener, ShareLocalPickerDialog
        .ShareLocalPlatformListener {
    private Context mContext;
    private Intent mShareIntent;
    private SharePanel mSharePanel;

    public ShareHelper(Context context, Intent shareIntent) {
        mContext = context;
        mShareIntent = shareIntent;
        mSharePanel = new SharePanel(context);
        mSharePanel.setListener(this);
        mSharePanel.setupPlatformWindow(shareIntent);
    }

    /*package*/ void showSharePanel() {
        mSharePanel.showPanel();
    }

    /*package*/ void hideSharePanel() {
        mSharePanel.hidePanel();
    }

    private void showLocalPickerDialog() {
        ShareLocalPickerDialog dialog = new ShareLocalPickerDialog(mContext);
        dialog.setListener(this);
        dialog.setShareIntent(mShareIntent);
        dialog.show();
    }

    @Override
    public void onSharePlatformClick(SharePlatformInfo info) {
        hideSharePanel();
        if (info.type == SharePlatformInfo.TYPE_MORE) {
            showLocalPickerDialog();
        } else {
            ShareSender.getInstance().sendShare(info.id, mShareIntent);
        }
    }

    @Override
    public void onLocalPlatformSelected(Intent platformIntent, Intent shareIntent) {
        ShareSender.getInstance().send2LocalApp(platformIntent, shareIntent);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            return info != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String encodeUrl(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        Set<String> keySet = bundle.keySet();
        if (keySet == null || keySet.size() == 0) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        int j = 0;

        for (String key : keySet) {
            Object object = bundle.get(key);
            if (!(object instanceof String) && !(object instanceof String[])) {
                continue;
            }

            if (object instanceof String) {
                String value = (String) object;
                if (j != 0) {
                    buf.append("&");
                }
                try {
                    buf.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException neverHappen) {
                    neverHappen.printStackTrace();
                }
            } else {
                if (j != 0) {
                    buf.append("&");
                }
                buf.append(URLEncoder.encode(key)).append("=");
                String[] values = (String[]) object;
                try {
                    for (int i = 0; i < values.length; i++) {
                        String value = values[i];
                        if (i == 0) {
                            buf.append(URLEncoder.encode(value, "UTF-8"));
                        } else {
                            buf.append(URLEncoder.encode("," + value, "UTF-8"));
                        }
                    }
                } catch (UnsupportedEncodingException neverHappen) {
                    neverHappen.printStackTrace();
                }
            }
            j++;
        }

        return buf.toString();

    }

}
