package com.sjy.ttclub.common;

import android.content.Context;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by zwl on 2015/11/19.
 * Email: 1501448275@qq.com
 */
public class RespondCodeHelper {

    public static void handlerResultStatus(Context context, int status) {
        switch (status) {
            case HttpCode.ERROR_NETWORK:
                ToastHelper.showToast(context, R.string.homepage_network_error);
                break;
            case HttpCode.FAIL_CODE:
                ToastHelper.showToast(context, context.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
                break;
            case HttpCode.LEVEL_NOT_ENOUGH_CODE:
                ToastHelper.showToast(context, context.getString(R.string.community_level_not_enough), Toast
                        .LENGTH_SHORT);
                break;
            case HttpCode.BANNED_CODE:
                ToastHelper.showToast(context, context.getString(R.string.community_limit_to_post), Toast
                        .LENGTH_SHORT);
                break;
            case HttpCode.INVALID_TOKEN_CODE:
                AccountManager.getInstance().logout();
                AccountManager.getInstance().tryOpenGuideLoginWindow();
                ToastHelper.showToast(context, context.getString(R.string.community_invalided_token),
                        Toast.LENGTH_SHORT);
                break;
            case HttpCode.MAN_LIMIT:
                ToastHelper.showToast(context, context.getString(R.string.community_limit_man_comment), Toast
                        .LENGTH_SHORT);
                break;
            case HttpCode.CONTENT_LIMIT:
                ToastHelper.showToast(context, context.getString(R.string.community_limit_comment_content), Toast
                        .LENGTH_SHORT);
                break;
            case HttpCode.ERROR_CODE_CONTENT_TOO_LONG:
                ToastHelper.showToast(context, context.getString(R.string.community_post_content_to_long_tip),
                        Toast.LENGTH_SHORT);
                break;
            case HttpCode.ERROR_CODE_CONTENT_FREQUENCY:
                ToastHelper.showToast(context, context.getString(R.string.community_to_do_more), Toast
                        .LENGTH_SHORT);
                break;
            default:
                ToastHelper.showToast(context, context.getString(R.string.community_post_failed), Toast
                        .LENGTH_SHORT);
                break;
        }
    }
}
