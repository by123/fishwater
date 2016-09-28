package com.sjy.ttclub.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.fresco.FrescoHelper;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.share.ShareSender;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.util.file.FileUtils;

import java.io.File;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public class SinaHandler extends BaseHandler {
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read," +
            "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    private Activity mActivity;
    private SsoHandler mSsoHandler;
    private IWeiboShareAPI mShareApi;

    private ThirdpartyLoginCallback mCallback;

    public SinaHandler() {

    }

    @Override
    public void setActivity(Activity activity) {
        mActivity = activity;
        AuthInfo authInfo = new AuthInfo(activity, CommonConst.APPID_SINA, CommonConst.REDIRECT_URL_SINA, SCOPE);
        mSsoHandler = new SsoHandler(activity, authInfo);
        if (BuildConfig.DEBUG) {
            LogUtil.enableLog();
        }
        mShareApi = WeiboShareSDK.createWeiboAPI(activity, CommonConst.APPID_SINA);
        mShareApi.registerApp();
    }

    @Override
    public void startLogin(ThirdpartyLoginCallback callback) {
        if (mSsoHandler == null) {
            handleLoginFailed();
            return;
        }
        mCallback = callback;
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
                if (accessToken == null) {
                    handleLoginFailed();
                    return;
                }
                if (accessToken.isSessionValid()) {
                    SinaTokenKeeper.writeAccessToken(mActivity, accessToken);
                    tryGetUserInfo(accessToken);
                } else {
                    handleLoginFailed();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                handleLoginFailed();
            }

            @Override
            public void onCancel() {
                handleLoginFailed();
            }
        });
    }

    private void tryGetUserInfo(final Oauth2AccessToken accessToken) {
        SinaUsersAPI usersAPI = new SinaUsersAPI(mActivity, CommonConst.APPID_SINA, accessToken);
        final long uid = StringUtils.parseLong(accessToken.getUid());
        if (uid == 0) {
            handleLoginFailed();
            return;
        }
        usersAPI.show(uid, new RequestListener() {
            @Override
            public void onComplete(String result) {
                if (StringUtils.isEmpty(result)) {
                    handleLoginFailed();
                    return;
                }
                SinaUser user = SinaUser.parse(result);
                if (user == null) {
                    handleLoginFailed();
                } else {
                    ThirdpartyUserInfo userInfo = new ThirdpartyUserInfo();
                    userInfo.uid = accessToken.getUid();
                    userInfo.token = accessToken.getToken();
                    userInfo.nickName = user.screen_name;
                    userInfo.sex = user.gender;
                    userInfo.media = LoginMedia.SINA;
                    userInfo.iconUrl = user.profile_image_url;
                    handleLoginSuccess(userInfo);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                handleLoginFailed();
                e.printStackTrace();
            }
        });
    }


    private void handleLoginFailed() {
        if (mCallback != null) {
            mCallback.onLoginFailed(LoginMedia.SINA);
        }
        mCallback = null;
    }

    private void handleLoginSuccess(ThirdpartyUserInfo userInfo) {
        if (mCallback != null) {
            mCallback.onLoginSuccess(LoginMedia.SINA, userInfo);
        }
        mCallback = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public SsoHandler getSsoHandler() {
        return mSsoHandler;
    }

    public IWeiboShareAPI getShareApi() {
        return mShareApi;
    }

    @Override
    public void startShare(Intent shareIntent) {
        if (mShareApi == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        mShareApi.registerApp();

        AuthInfo authInfo = new AuthInfo(mActivity, CommonConst.APPID_SINA, CommonConst.REDIRECT_URL_SINA, SCOPE);
        Oauth2AccessToken accessToken = SinaTokenKeeper.readAccessToken(mActivity);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        shareIntent = ShareIntentBuilder.createStandardShareIntent(shareIntent);
        String content = ShareIntentBuilder.parseShareContent(shareIntent);
        String imageUrl = ShareIntentBuilder.parseImageUrl(shareIntent);
        String title = ShareIntentBuilder.parseShareTitle(shareIntent);
        String shareUrl = ShareIntentBuilder.parseShareUrl(shareIntent);

        if (StringUtils.isEmpty(title)) {
            title = ShareSender.getInstance().getDefaultShareTitle();
        }
        if (title.length() >= 512) {
            title = title.substring(0, 500);
        }

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (StringUtils.isNotEmpty(content)) {
            TextObject textObject = new TextObject();
            textObject.title = title;
            if (content.length() >= 1024) {
                content = content.substring(0, 1000);
            }
            textObject.text = content;
            weiboMessage.textObject = textObject;
        }

        String path = imageUrl;
        if (StringUtils.isNotEmpty(imageUrl)) {
            boolean isLocalImage = !imageUrl.startsWith("http");
            if (!isLocalImage) {
                File file = FrescoHelper.getCachedImageOnDisk(imageUrl);
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            }
            if (FileUtils.isFileExists(path)) {
                ImageObject imageObject = new ImageObject();
                imageObject.imagePath = path;
                imageObject.imageData = getImageThumbnail(path);
                weiboMessage.imageObject = imageObject;
            }
        }

        if (StringUtils.isNotEmpty(shareUrl)) {
            WebpageObject mediaObject = new WebpageObject();
            mediaObject.actionUrl = shareUrl;
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = title;
            mediaObject.description = content;
            mediaObject.setThumbImage(getImageThumbnailBitmap(path));
            mediaObject.defaultText = ShareSender.getInstance().getDefaultShareTitle();
            weiboMessage.mediaObject = mediaObject;
        }

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.multiMessage = weiboMessage;
        request.transaction = String.valueOf(System.currentTimeMillis());
        boolean success = mShareApi.sendRequest(mActivity, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onCancel() {
                ToastHelper.showToast(R.string.share_cancel);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                ToastHelper.showToast(R.string.share_error);
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                SinaTokenKeeper.writeAccessToken(mActivity, newToken);
            }
        });
        if (!success) {
            ToastHelper.showToast(R.string.share_error);
        }
    }

    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ToastHelper.showToast(R.string.share_success);
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToastHelper.showToast(R.string.share_cancel);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ToastHelper.showToast(R.string.share_error);
                break;
        }
    }
}
