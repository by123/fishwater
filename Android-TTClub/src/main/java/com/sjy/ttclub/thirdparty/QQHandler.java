package com.sjy.ttclub.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.share.ShareSender;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public class QQHandler extends BaseHandler {
    private Activity mActivity;
    private ThirdpartyLoginCallback mCallback;
    private Tencent mTencent;

    public QQHandler() {

    }

    @Override
    public void setActivity(Activity activity) {
        mActivity = activity;
        mTencent = Tencent.createInstance(CommonConst.APPID_QQ, activity);
    }

    private IUiListener mLoginListener = new IUiListener() {
        @Override
        public void onComplete(Object result) {
            if (result == null) {
                handleLoginFailed();
                return;
            }
            setupOpenIdAndToken(result.toString());
            QQToken token = mTencent.getQQToken();
            if (token == null || token.getAccessToken() == null) {
                handleLoginFailed();
                return;
            }
            tryGetUserInfo(token);
        }

        @Override
        public void onError(UiError uiError) {
            handleLoginFailed();
        }

        @Override
        public void onCancel() {
            handleLoginFailed();
        }
    };

    private void setupOpenIdAndToken(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String token = jsonObject.optString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.optString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.optString(Constants.PARAM_OPEN_ID);
            if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startLogin(ThirdpartyLoginCallback callback) {
        if (mActivity == null) {
            handleLoginFailed();
            return;
        }
        if (!mTencent.isSupportSSOLogin(mActivity)) {
            toastAppVersionLow();
            return;
        }
        mCallback = callback;
        mTencent.login(mActivity, "get_user_info", mLoginListener);
    }

    private void tryGetUserInfo(final QQToken token) {
        final UserInfo userInfo = new UserInfo(mActivity, token);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object result) {
                if (result == null) {
                    handleLoginFailed();
                    return;
                }
                ThirdpartyUserInfo loginInfo = parseUserInfo(result.toString());
                if (loginInfo != null) {
                    loginInfo.token = token.getAccessToken();
                    loginInfo.uid = token.getOpenId();
                    loginInfo.media = LoginMedia.QQ;
                    handleLoginSuccess(loginInfo);
                } else {
                    handleLoginFailed();
                }
            }

            @Override
            public void onError(UiError uiError) {
                handleLoginFailed();
            }

            @Override
            public void onCancel() {
                handleLoginFailed();
            }
        });
    }

    private ThirdpartyUserInfo parseUserInfo(String result) {
        ThirdpartyUserInfo userInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            userInfo = new ThirdpartyUserInfo();
            userInfo.nickName = jsonObject.optString("nickname");
            userInfo.iconUrl = jsonObject.optString("figureurl_qq_2");
            userInfo.sex = jsonObject.optString("gender");
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;

    }

    private void handleLoginFailed() {
        if (mCallback != null) {
            mCallback.onLoginFailed(LoginMedia.QQ);
        }
        mCallback = null;
    }

    private void handleLoginSuccess(ThirdpartyUserInfo userInfo) {
        if (mCallback != null) {
            mCallback.onLoginSuccess(LoginMedia.QQ, userInfo);
        }
        mCallback = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mLoginListener);
        } else if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mShareListener);
        }
    }

    private IUiListener mShareListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            ToastHelper.showToast(R.string.share_success);
        }

        @Override
        public void onError(UiError uiError) {
            ToastHelper.showToast(R.string.share_error);
        }

        @Override
        public void onCancel() {
            ToastHelper.showToast(R.string.share_cancel);
        }
    };

    @Override
    public void startShare(Intent shareIntent) {
        if (mTencent == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        shareIntent = ShareIntentBuilder.createStandardShareIntent(shareIntent);
        String content = ShareIntentBuilder.parseShareContent(shareIntent);
        String imageUrl = ShareIntentBuilder.parseImageUrl(shareIntent);
        String title = ShareIntentBuilder.parseShareTitle(shareIntent);
        String shareUrl = ShareIntentBuilder.parseShareUrl(shareIntent);
        int sourceType = ShareIntentBuilder.parseSourceType(shareIntent);
        boolean isImageType = (sourceType == ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE) && StringUtils.isNotEmpty
                (imageUrl);

        int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
        Bundle bundle = new Bundle();
        //如果是图片类型，只支持本地图片，所以需要判断下
        if (isImageType) {
            boolean isLocalImage = !imageUrl.startsWith("http://");
            if (isLocalImage) {
                shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
            }
        }
        if (StringUtils.isEmpty(imageUrl)) {
            imageUrl = ShareSender.getInstance().getDefaultImageUrl();
        }
        if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        } else {
            if (StringUtils.isEmpty(shareUrl)) {
                shareUrl = ShareSender.DEFAULT_URL;
            }
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            if (StringUtils.isEmpty(title)) {
                title = ShareSender.getInstance().getDefaultShareTitle();
            }
            if (StringUtils.isEmpty(content)) {
                content = title;
            }
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        }
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, ResourceHelper.getString(R.string.app_name));
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
        mTencent.shareToQQ(mActivity, bundle, mShareListener);
    }

    public void startShare2Qzone(Intent shareIntent) {
        if (mTencent == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        shareIntent = ShareIntentBuilder.createStandardShareIntent(shareIntent);
        String content = ShareIntentBuilder.parseShareContent(shareIntent);
        String imageUrl = ShareIntentBuilder.parseImageUrl(shareIntent);
        String title = ShareIntentBuilder.parseShareTitle(shareIntent);
        String shareUrl = ShareIntentBuilder.parseShareUrl(shareIntent);
        int sourceType = ShareIntentBuilder.parseSourceType(shareIntent);
        boolean isImageType = (sourceType == ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE) && StringUtils.isNotEmpty
                (imageUrl);

        int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
        Bundle bundle = new Bundle();

        if (StringUtils.isEmpty(shareUrl)) {
            shareUrl = ShareSender.DEFAULT_URL;
        }
        if (StringUtils.isEmpty(title)) {
            title = ShareSender.getInstance().getDefaultShareTitle();
        }
        if (StringUtils.isEmpty(content)) {
            content = title;
        }
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
        if (StringUtils.isNotEmpty(imageUrl)) {
            ArrayList<String> imageUrls = new ArrayList<String>(1);
            imageUrls.add(imageUrl);
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        }

        bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, ResourceHelper.getString(R.string.app_name));
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        mTencent.shareToQzone(mActivity, bundle, mShareListener);
    }
}
