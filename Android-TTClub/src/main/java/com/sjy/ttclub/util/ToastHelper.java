package com.sjy.ttclub.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.ContextManager;

/**
 * Created by gangqing on 2015/11/26.
 * Email:denggangqing@ta2she.com
 */
public class ToastHelper {
    private static Toast sToast;

    public static void showToast(String text) {
        showToast(ContextManager.getAppContext(), text, Toast.LENGTH_LONG);
    }

    public static void showToast(int resId) {
        showToast(ContextManager.getAppContext(), resId, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, int resId, int duration) {
        String message = context.getResources().getString(resId);
        showToast(context, message, duration);
    }

    public static void showToast(Context context, String text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        } else {
            sToast.setText(text);
            sToast.setDuration(duration);
        }
        sToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        ThreadManager.post(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                sToast.show();
            }
        });
    }

    public static void showToast(Context context, String title, String content) {
        Toast result = new Toast(context);

        View layout = View.inflate(context, R.layout.toast_layout, null);

        TextView textView = (TextView) layout.findViewById(R.id.toast_title);
        TextView textView2 = (TextView) layout.findViewById(R.id.toast_detail);
        textView2.setVisibility(View.VISIBLE);
        textView.setText(title);
        textView2.setText(content);
        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(Toast.LENGTH_LONG);
        result.show();
    }
}
