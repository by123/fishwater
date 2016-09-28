package com.sjy.ttclub.splashscreen;
import com.sjy.ttclub.bean.splash.SplashDataBean;
import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.IDownLoadFileCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;

import java.io.File;

/**
 * Created by zhangwulin on 2016/1/11.
 * email 1501448275@qq.com
 */
public class SplashHelper {
    private String mImageUrl;
    private SplashRequest mSplashRequest;
    public SplashHelper() {
        mSplashRequest=new SplashRequest();
    }
    public void tryGetSplashData() {

        mSplashRequest.startSplashDataRequest(new SplashRequest.SplashDataRequestCallback() {
            @Override
            public void onResultFail() {
                handlerOnGetSplashDataFail();
            }

            @Override
            public void onResultSuccess(SplashDataBean.SplashData splashData) {
                handlerOnGetRequestSplashDataSuccess(splashData);
            }
        });
    }

    private void handlerOnGetSplashDataFail() {
        handlerDownLoadImageFail();
    }

    private void handlerOnGetRequestSplashDataSuccess(SplashDataBean.SplashData splashData) {
        String oldImageUrl = SettingFlags.getStringFlag(SplashConstant.SPLASH_KEY_IMAGEURL, "");
        String newImageUrl = splashData.getImageUrl();
        boolean isSaveSuccess = SettingFlags.getBooleanFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_SUCCESS);
        if (!isSaveSuccess) {
            trySaveSplashData(splashData);
            tryDownloadAdImage(splashData);
            return;
        }
        if (oldImageUrl != null && newImageUrl != null && oldImageUrl.equals(newImageUrl)) {
            trySaveSplashData(splashData);
            return;
        }
        trySaveSplashData(splashData);
        tryDownloadAdImage(splashData);
    }

    private void trySaveSplashData(SplashDataBean.SplashData splashData) {
        SettingFlags.setFlag(SplashConstant.SPLASH_KEY_BANNERID, splashData.getBannerId());
        SettingFlags.setFlag(SplashConstant.SPLASH_KEY_ADATTR, splashData.getAdAttr());
        if (StringUtils.isNotEmpty(splashData.getTitle())) {
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_TITLE, splashData.getTitle());
        }
        if (StringUtils.isNotEmpty(splashData.getImageUrl())) {
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_IMAGEURL, splashData.getImageUrl());
        }
        if (StringUtils.isNotEmpty(splashData.getAdAttrValue())) {
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_ADATTRVALUE, splashData.getAdAttrValue());
        }
        if (StringUtils.isNotEmpty(splashData.getStartTime())) {
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_STARTTIME, splashData.getStartTime());
        }
        if (StringUtils.isNotEmpty(splashData.getEndTime())) {
            SettingFlags.setFlag(SplashConstant.SPLASH_KEY_ENDTIME, splashData.getEndTime());
        }
    }

    private void tryDownloadAdImage(SplashDataBean.SplashData splashData) {
        mImageUrl = splashData.getImageUrl();
        if (StringUtils.isEmpty(mImageUrl)) {
            handlerDownLoadImageFail();
            return;
        }
        final String fileName = SplashConstant.SPLASH_IMAGE_PATH;
        String folderPath = PathManager.getInstance().getSplashPath();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final String finalPath = folderPath + fileName;
        IHttpManager iHttpManager = HttpManager.getBusinessHttpManger();
        iHttpManager.downLoadFile(mImageUrl, finalPath, new IDownLoadFileCallBack() {
            @Override
            public void onSuccess(File file) {
                SettingFlags.setFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_SUCCESS, true);
                SettingFlags.setFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_PATH, finalPath);
            }

            @Override
            public void onError(int errorCode) {
                handlerDownLoadImageFail();
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void handlerDownLoadImageFail() {
        SettingFlags.setFlag(SplashConstant.SPLASH_KEY_DOWNLOAD_IMAGE_SUCCESS, false);
    }
}
