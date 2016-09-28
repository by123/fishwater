package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.util.Log;

import com.sjy.ttclub.account.setting.PhotoGridAdapter;
import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.community.CommunitySendPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpLoadingCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.widget.dialog.ProgressDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhangwulin on 2016/1/4.
 * email 1501448275@qq.com
 */
public class CommunitySendPostRequest {
    private Context mContext;
    private boolean mIsRequesting;
    private ProgressDialog loadingDialog;
    public CommunitySendPostRequest(Context context) {
        this.mContext = context;
    }

    public void startRequest(CommunitySendPostBean sendPostBean, final SendPostRequestCallback callback) {
        if(mIsRequesting){
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        loadingDialog=new ProgressDialog(mContext);
        loadingDialog.show();
        IHttpManager mIHttpManager = HttpManager.getBusinessHttpManger();
        mIHttpManager.addParams("a", "submitPost");
        mIHttpManager.addParams("circleId", String.valueOf(sendPostBean.getCirlceId()));
        mIHttpManager.addParams("postTitle", sendPostBean.getTheme());
        mIHttpManager.addParams("content", sendPostBean.getContent());
        mIHttpManager.addParams("topicId", String.valueOf(sendPostBean.getTopicId()));
        mIHttpManager.addParams("isAnony", String.valueOf(sendPostBean.getIsAnony()));
        tryAddImageToRequest(sendPostBean, mIHttpManager,callback);
    }
    private void tryAddImageToRequest(CommunitySendPostBean sendPostBean,final IHttpManager mIHttpManager,final SendPostRequestCallback callback){
        if(sendPostBean.getPhotoItems()==null){
            return;
        }
        ArrayList<String> photoList = new ArrayList<>();
        for (PhotoGridAdapter.PhotoItem item : sendPostBean.getPhotoItems()) {
            if (item.type != PhotoGridAdapter.PhotoItem.TYPE_ADD && item.path != null) {
                photoList.add(item.path);
            }
        }
        if(photoList.size()==0){
            tryStartRequest(mIHttpManager,callback);
            return;
        }
        BitmapUtil.compressBitmaps(photoList, new BitmapUtil.CompressBitmapCallback() {
            @Override
            public void onCompressFinished(ArrayList<String> desList) {
                if (desList != null && !desList.isEmpty()) {
                    for (int i = 0; i < desList.size(); i++) {
                        String fileName = "file" + i;
                        mIHttpManager.addParams(fileName, new File(desList.get(i)), null);
                    }
                }
                tryStartRequest(mIHttpManager,callback);
            }
        });
    }

    private void tryStartRequest(final IHttpManager mIHttpManager,final SendPostRequestCallback callback){
        mIHttpManager.requestWithLoading(HttpUrls.COMMUNITY_URL, HttpMethod.POST, BaseBean.class, new IHttpLoadingCallBack() {

            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                loadingDialog.setMaxProgress((int) total);
                if (!isUploading) {
                    loadingDialog.setProgress((int) current);
                }
            }

            @Override
            public <T> void onSuccess(T obj, String result) {
                BaseBean baseBean = (BaseBean) obj;
                handlerSendPostSuccess(baseBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handlerSendPostFail(code, callback);
            }
        });
    }
    private void handlerSendPostFail(int errorCode, SendPostRequestCallback callback) {
        loadingDialog.dismiss();
        mIsRequesting = false;
        callback.onResultFail(errorCode);
    }

    private void handlerSendPostSuccess(BaseBean baseBean, SendPostRequestCallback callback) {
        loadingDialog.dismiss();
        mIsRequesting = false;
        if(baseBean.getStatus()== HttpCode.SUCCESS_CODE) {
            callback.onResultSuccess(baseBean);
        }else {
            callback.onResultFail(baseBean.getStatus());
        }
    }

    public interface SendPostRequestCallback {
        void onResultFail(int errorCode);

        void onResultSuccess(BaseBean baseBean);
    }
}
