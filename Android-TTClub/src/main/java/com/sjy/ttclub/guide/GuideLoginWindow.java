package com.sjy.ttclub.guide;

import android.content.Context;
import android.os.Message;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ApplicationExitHelper;
import com.sjy.ttclub.widget.AlphaTextView;

import java.util.ArrayList;

/**
 * Created by zhangwulin on 2016/1/5.
 * email 1501448275@qq.com
 */
public class GuideLoginWindow extends DefaultWindow {
    private AlphaTextView mReigsterButton;
    private AlphaTextView mLoginButton;
    private GuideImagesView mGiudeImagesView;
    private ApplicationExitHelper mExitHelper;

    public GuideLoginWindow(Context context, IDefaultWindowCallBacks callBacks, boolean isLogout) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);

        if (isLogout) {
            setPopBackWindowAfterPush(isLogout);
        }
        mExitHelper = new ApplicationExitHelper(context);
        createView();
    }

    private void createView() {
        View parentView = View.inflate(getContext(), R.layout.guide_login_layout, null);
        getBaseLayer().addView(parentView, getBaseLayerLP());
        mGiudeImagesView = (GuideImagesView) findViewById(R.id.guide_images_view);
        mGiudeImagesView.setImageResources(createInitData());
        //统计
        addUmengStats();
        mReigsterButton = (AlphaTextView) parentView.findViewById(R.id.guide_register_button);
        mReigsterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnRegisterButtonClick();
            }
        });
        mLoginButton = (AlphaTextView) parentView.findViewById(R.id.guide_login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnLoginButtonClick();
            }
        });
    }

    private void addUmengStats() {
        StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_1);
        mGiudeImagesView.setOnGuidePageChangeListener(new GuideImagesView.OnGuidePageChangeListener() {
            @Override
            public void onPageChange(int index) {
                if (index == 0) {
                    StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_1);
                } else if (index == 1) {
                    StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_2);
                } else if (index == 2) {
                    StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_3);
                }
            }
        });
    }

    private void handlerOnRegisterButtonClick() {
        StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_REGISTER);
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_SIGNUP_WINDOW;
        message.arg1 = Constant.OPEN_FROM_TYPE_GUIDE;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private void handlerOnLoginButtonClick() {
        StatsModel.stats(StatsKeyDef.LOGIN_INDUCTION_LOGIN);
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_LOGIN_WINDOW;
        message.arg1 = Constant.OPEN_FROM_TYPE_GUIDE;
        MsgDispatcher.getInstance().sendMessage(message);
    }

    private ArrayList<GuideImagesView.ImageInfo> createInitData() {
        ArrayList<GuideImagesView.ImageInfo> infoList = new ArrayList<>();
        GuideImagesView.ImageInfo info;
        info = new GuideImagesView.ImageInfo();
        info.sourceId = R.drawable.guide_image_1;
        infoList.add(info);

        info = new GuideImagesView.ImageInfo();
        info.sourceId = R.drawable.guide_image_2;
        infoList.add(info);

        info = new GuideImagesView.ImageInfo();
        info.sourceId = R.drawable.guide_image_3;
        infoList.add(info);
        return infoList;
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
    }

    @Override
    public boolean onWindowBackKeyEvent() {
        mExitHelper.exitApplication();
        return true;
    }
}
