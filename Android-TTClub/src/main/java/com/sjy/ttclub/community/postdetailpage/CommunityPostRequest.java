package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import com.sjy.ttclub.bean.community.CommunityPostDetailJsonBean;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.ImageCard;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import java.util.ArrayList;

/**
 * Created by zwl on 2015/11/17.
 * Email: 1501448275@qq.com
 */
public class CommunityPostRequest {
    private Context mContext;
    private int mPostId;
    private boolean mIsRequesting = false;
    private IHttpManager mHttpManager;

    public CommunityPostRequest(Context mContext, int postId) {
        this.mContext = mContext;
        this.mPostId = postId;
    }

    public void startRequest(RequestPostResultCallback callback) {
        startRequestById(mPostId, callback);
    }

    private void startRequestById(int postId, final RequestPostResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFailed(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mHttpManager = HttpManager.getBusinessHttpManger();
        mHttpManager.addParams("a", "postDetail");
        mHttpManager.addParams("postId", String.valueOf(postId));
        mHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityPostDetailJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityPostDetailJsonBean cardJsonBean = (CommunityPostDetailJsonBean) obj;
                handleGetPostSuccess(cardJsonBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetPostFailed(code, callback);
            }
        });
    }

    private void handleGetPostFailed(int errorCode, RequestPostResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFailed(errorCode);
    }

    private void handleGetPostSuccess(CommunityPostDetailJsonBean result, RequestPostResultCallback callback) {
        mIsRequesting = false;
        if (HttpCode.SUCCESS_CODE == result.getStatus()) {
            //获取数据列表
            CommunityPostBean mPostCard = result.getData();
            ArrayList<ImageCard> images = new ArrayList<>();
            for (int i = 0; i < mPostCard.getImages().size(); i++) {
                ImageCard image = new ImageCard();
                image = mPostCard.getImages().get(i);
                image.setImageSortId(i);
                images.add(image);
            }
            mPostCard.setImages(images);
            callback.onResultSuccess(mPostCard);
        } else {
            handleGetPostFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }

    public interface RequestPostResultCallback {

        void onResultFailed(int errorCode);

        void onResultSuccess(CommunityPostBean postBean);
    }

}
