package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by gangqing on 2015/12/17.
 * Email:denggangqing@ta2she.com
 */
public class LevelWindow extends DefaultWindow {
    private SimpleDraweeView mHeadImage;
    private TextView mUserName;
    private TextView mLevel;
    private ProgressBar mProgressBar;
    private TextView mLevelSign;

    public LevelWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_personal_level_title);
        initActionBar();
        View view = View.inflate(getContext(), R.layout.account_level_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        initView(view);
        setData();
    }

    private void initView(View view) {
        mHeadImage = (SimpleDraweeView) view.findViewById(R.id.account_personal_level_head_image);
        mUserName = (TextView) view.findViewById(R.id.account_personal_level_user_name);
        mLevel = (TextView) view.findViewById(R.id.account_personal_level_user_level);
        mProgressBar = (ProgressBar) view.findViewById(R.id.account_personal_level_progress);
        mLevelSign = (TextView) view.findViewById(R.id.account_personal_level_sign);
    }

    private void setData() {
        AccountInfo accountInfo = AccountManager.getInstance().getAccountInfo();
        AccountManager.getInstance().setHeadImage(mHeadImage);
        setNicknameAndLevel(accountInfo);
        setProgressBar(accountInfo);
    }

    /**
     * 设置昵称和等级
     * @param accountInfo
     */
    private void setNicknameAndLevel(AccountInfo accountInfo){
        //设置昵称和等级的字体颜色
        if ((CommonConst.SEX_MAN + "").equals(accountInfo.getSex())) {
            mUserName.setTextColor(ResourceHelper.getColor(R.color.account_level_color_man));
            mLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_man));
        } else {
            mUserName.setTextColor(ResourceHelper.getColor(R.color.account_level_color_woman));
            mLevel.setTextColor(ResourceHelper.getColor(R.color.account_level_color_woman));
        }
        mUserName.setText(accountInfo.getNickname());
        mLevel.setText("LV." + accountInfo.getLevel());
    }

    /**
     * 进度条及进度提示
     * @param accountInfo
     */
    private void setProgressBar(AccountInfo accountInfo){
        //经验进度条
        int maxProgress = StringUtils.parseInt(accountInfo.getProgressBar());
        mProgressBar.setMax(maxProgress);
        int curProgress = StringUtils.parseInt(accountInfo.getCurrentProgress());
        mProgressBar.setProgress(curProgress);
        //距离LV11还差870经验
        int cdExperience = maxProgress - curProgress;
        if ("1".equals(accountInfo.getIsMaxLevel())) {
            mLevelSign.setText("满级LV" + accountInfo.getLevel());
        } else {
            mLevelSign.setText("距离LV" + (StringUtils.parseInt(accountInfo.getLevel()) + 1) + "还差" + cdExperience + "经验");
        }
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.account_personal_level_action));
        item.setItemId(MenuItemIdDef.TITLEBAR_LEVEL_EXPLAIN);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        Message message = Message.obtain();
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = ResourceHelper.getString(R.string.account_personal_level_action_title);
        params.url = HttpUrls.LEVEL_EXPLAIN;
        message.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        message.obj = params;
        MsgDispatcher.getInstance().sendMessage(message);
    }
}
