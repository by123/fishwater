package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by zwl on 2015/11/19.
 * Email: 1501448275@qq.com
 */
public class CheckResultCodeUtil {

    public static void handlerResultStatus(Context context,int status){
        switch (status){
            case 100:
                ToastHelper.showToast(context, context.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
                break;
            case 105:
                ToastHelper.showToast(context,context.getString(R.string.community_level_not_enough), Toast.LENGTH_SHORT);
                break;
            case 107:
                ToastHelper.showToast(context, context.getString(R.string.community_limit_to_post), Toast.LENGTH_SHORT);
                break;
            case 104:
                AccountManager.getInstance().tryOpenGuideLoginWindow();
                break;
            case 306:
                ToastHelper.showToast(context, context.getString(R.string.community_limit_man_comment), Toast.LENGTH_SHORT);
                break;
            case 308:
                ToastHelper.showToast(context,context.getString(R.string.community_limit_comment_content), Toast.LENGTH_SHORT);
                break;
            case 628:
                ToastHelper.showToast(context, context.getString(R.string.community_post_content_to_long_tip), Toast.LENGTH_SHORT);
                break;
            case 638:
                ToastHelper.showToast(context, context.getString(R.string.community_to_do_more), Toast.LENGTH_SHORT);
                break;
            default:
                ToastHelper.showToast(context, context.getString(R.string.community_psot_failed), Toast.LENGTH_SHORT);
                break;
        }
    }
}
