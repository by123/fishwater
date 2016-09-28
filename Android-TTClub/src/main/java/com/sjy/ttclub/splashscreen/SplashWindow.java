/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */


package com.sjy.ttclub.splashscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lsym.ttclub.R;
import com.sjy.ttclub.InitEventInfo;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;


public class SplashWindow extends DefaultWindow {
    private ISplashWindowListener mSplashWindowListener;
    private ImageView mWelcomeView;
    private boolean mIsInit = true;
    private SplashHelper mSplashHelper;

    public SplashWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        setEnableSwipeGesture(false);
        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(ResourceHelper
                .getColor(R.color.splash_window_bg_color));
        getBaseLayer().addView(rootView, getBaseLayerLP());
        createSplashHelper();
        createWelcomeView(rootView);
        initSplashData();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void initSplashData() {
        mIsInit = SettingFlags.getBooleanFlag(SplashConstant.SPLASH_KEY_ISINIT, true);
        if (mIsInit) {
            mSplashHelper.tryGetSplashData();
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_ISINIT, false);
            return;
        }
    }

    private void createWelcomeView(FrameLayout rootView) {
        mWelcomeView = new ImageView(getContext());
        mWelcomeView.setScaleType(ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        rootView.addView(mWelcomeView, flp);
        setWelcomeImage();
    }

    private void setWelcomeImage() {
        if (!SplashUtil.isShowSplash()) {
            mWelcomeView.setImageResource(R.drawable.welcome);
            return;
        }
        if (!SettingFlags.getBooleanFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_SUCCESS, false)) {
            mWelcomeView.setImageResource(R.drawable.welcome);
            mSplashHelper.tryGetSplashData();
            return;
        }
        Bitmap bitmap = tryGetSplashImageFromLocal();
        if (bitmap == null) {
            mWelcomeView.setImageResource(R.drawable.welcome);
            return;
        }
        mWelcomeView.setImageBitmap(bitmap);
        mWelcomeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnWelcomeViewClick();
            }
        });
    }

    private void handlerOnWelcomeViewClick() {
        if (!SplashUtil.isShowSplash()) {
            return;
        }
        if (SplashUtil.isImageSplash()) {
            return;
        }
        Banner banner = new Banner();
        banner.setAdAttr(SettingFlags.getIntFlag(SplashConstant.SPLASH_KEY_ADATTR));
        banner.setAdAttrValue(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_ADATTRVALUE));
        banner.setTitle(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_TITLE));
        banner.setImageUrl(SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_IMAGEURL));

        InitEventInfo eventInfo = new InitEventInfo();
        eventInfo.type = InitEventInfo.TYPE_BANNER;
        eventInfo.banner = banner;
        Message message = Message.obtain();
        message.obj = eventInfo;
        message.what = MsgDef.MSG_INIT_ACTION_EVENTS;
        MsgDispatcher.getInstance().sendMessageSync(message);

        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_CLOSE_SPLASH_WINDOW);
    }

    private void createSplashHelper() {
        mSplashHelper = new SplashHelper();
    }

    private Bitmap tryGetSplashImageFromLocal() {
        String path = SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_PATH);
        if (path != null) {
            return BitmapUtil.createBitmapThumbnail(path, HardwareUtil.screenWidth, HardwareUtil.screenHeight);
        } else {
            return null;
        }
    }

    public void setSplashWindowListener(ISplashWindowListener listener) {
        mSplashWindowListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public interface ISplashWindowListener {
        public void onSplashWindowError();
    }

}
