package com.sjy.ttclub.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.fresco.FrescoHelper;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by linhz on 2016/1/25.
 * Email: linhaizhong@ta2she.com
 */
public class WechatHandler extends BaseHandler {

    private Context mContext;
    private IWXAPI mWxApi;

    private ThirdpartyLoginCallback mCallback;

    public WechatHandler() {

    }

    @Override
    public void setActivity(Activity activity) {
        mContext = activity;
        mWxApi = WXAPIFactory.createWXAPI(activity, null);
        mWxApi.registerApp(CommonConst.APPID_WECHAT);
    }

    public IWXAPI getWxApi() {
        return mWxApi;
    }

    @Override
    public void startLogin(final ThirdpartyLoginCallback callback) {
        mCallback = callback;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = generateAuthState();
        if (mWxApi == null) {
            handleLoginFailed();
            return;
        }
        if (!mWxApi.isWXAppInstalled()) {
            toastAppNotInstall();
            return;
        }

        mWxApi.sendReq(req);
    }

    public void onResp(BaseResp resp) {
        int type = resp.getType();
        switch (type) {
            case 1:
                onAuthCallback((SendAuth.Resp) resp);
                break;
            case 2:
                onShareCallback(resp);
                break;
        }

    }

    private void onAuthCallback(SendAuth.Resp resp) {
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            this.getAuthWithCode(resp.code);
        } else {
            handleLoginFailed();
        }
    }

    private void getAuthWithCode(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        IHttpManager httpManager = HttpManager.getHttpManger();
        httpManager.addParams("appid", CommonConst.APPID_WECHAT);
        httpManager.addParams("secret", CommonConst.APPSECRET_WECHAT);
        httpManager.addParams("code", code);
        httpManager.addParams("grant_type", "authorization_code");
        httpManager.request(url, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleLoginFailed();
                } else {
                    handleGetAuthWithCodeSuccess(result);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                handleLoginFailed();
            }
        });
    }

    private void handleGetAuthWithCodeSuccess(String result) {
        final HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject e = new JSONObject(result);
            Iterator iterator = e.keys();
            String key = "";
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                map.put(key, e.optString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String token = map.get("access_token");
        String uid = map.get("openid");
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(uid)) {
            handleLoginFailed();
            return;
        }

        getUserInfo(token, uid);
    }

    private void getUserInfo(final String token, final String uid) {
        String url = "https://api.weixin.qq.com/sns/userinfo";
        IHttpManager httpManager = HttpManager.getHttpManger();
        httpManager.addParams("access_token", token);
        httpManager.addParams("openid", uid);
        httpManager.addParams("lang", "zh_CN");
        httpManager.request(url, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isEmpty(result)) {
                    handleLoginFailed();
                } else {
                    ThirdpartyUserInfo userInfo = handleGetUserInfoSuccess(result);
                    if (userInfo == null) {
                        handleLoginFailed();
                    } else {
                        userInfo.token = token;
                        userInfo.uid = uid;
                        userInfo.media = LoginMedia.WECHAT;
                        handleLoginSuccess(userInfo);
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                if (mCallback != null) {
                    mCallback.onLoginFailed(LoginMedia.WECHAT);
                }
            }
        });
    }

    private ThirdpartyUserInfo handleGetUserInfoSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean error = jsonObject.has("errcode");
            if (error) {
                return null;
            } else {
                ThirdpartyUserInfo userInfo = new ThirdpartyUserInfo();
                userInfo.nickName = jsonObject.optString("nickname");
                userInfo.iconUrl = jsonObject.optString("headimgurl");
                userInfo.sex = jsonObject.optString("sex");
                userInfo.uid = jsonObject.optString("openid");
                return userInfo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleLoginFailed() {
        if (mCallback != null) {
            mCallback.onLoginFailed(LoginMedia.WECHAT);
        }
        mCallback = null;
    }

    private void handleLoginSuccess(ThirdpartyUserInfo userInfo) {
        if (mCallback != null) {
            mCallback.onLoginSuccess(LoginMedia.WECHAT, userInfo);
        }
        mCallback = null;
    }


    private String generateAuthState() {
        String state = "wx" + "_" + System.currentTimeMillis();
        return state;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void startShare(Intent shareIntent) {

    }

    public void share2WechatTimeline(Intent shareIntent) {
        doSend(shareIntent, true);
    }

    public void share2WechatFriends(Intent shareIntent) {
        doSend(shareIntent, false);
    }


    private void doSend(Intent shareIntent, boolean isTimeline) {
        if (mWxApi == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        if (!mWxApi.isWXAppInstalled()) {
            toastAppNotInstall();
            return;
        }

        shareIntent = ShareIntentBuilder.createStandardShareIntent(shareIntent);
        String content = ShareIntentBuilder.parseShareContent(shareIntent);
        String imageUrl = ShareIntentBuilder.parseImageUrl(shareIntent);
        String title = ShareIntentBuilder.parseShareTitle(shareIntent);
        String shareUrl = ShareIntentBuilder.parseShareUrl(shareIntent);
        int sourceType = ShareIntentBuilder.parseSourceType(shareIntent);


        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(isTimeline);
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        WXMediaMessage msg = new WXMediaMessage();
        req.message = msg;
        msg.description = content;
        msg.title = title;

        WXMediaMessage.IMediaObject shareObject = null;
        if (imageUrl != null) {
            String path = imageUrl;
            boolean isLocalImage = !imageUrl.startsWith("http");
            if (!isLocalImage) {
                File file = FrescoHelper.getCachedImageOnDisk(imageUrl);
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            }
            msg.thumbData = getImageThumbnail(path);
        }

        if (StringUtils.isNotEmpty(shareUrl) && needWebpageObject(sourceType)) {
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = shareUrl;
            shareObject = webpageObject;
        } else if (needImageObject(sourceType) && StringUtils.isNotEmpty(imageUrl)) {
            WXImageObject imageObject = new WXImageObject();
            boolean useWebUrl = imageUrl.startsWith("http");
            if (useWebUrl && shareUrl != null) {
                imageObject.imageUrl = imageUrl;
            } else {
                imageObject.imagePath = imageUrl;
            }
            shareObject = imageObject;
        } else {
            WXTextObject textObject = new WXTextObject();
            textObject.text = content;
            shareObject = textObject;
        }
        msg.mediaObject = shareObject;
        mWxApi.sendReq(req);
    }

    private boolean needWebpageObject(int sourceType) {
        return (sourceType == ShareIntentBuilder.SOURCE_TYPE_SHARE_PAGE)
                || (sourceType == ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);
    }

    private boolean needImageObject(int sourceType) {
        return (sourceType == ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE);
    }

    private void onShareCallback(BaseResp baseResp) {
        boolean success = false;
        boolean cancel = false;
        boolean unsupport = false;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                success = true;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                unsupport = true;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_SENT_FAILED:
            case BaseResp.ErrCode.ERR_COMM:
                success = false;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                cancel = true;
                break;
        }
        if (success) {
            ToastHelper.showToast(R.string.share_success);
        } else if (unsupport) {
            ToastHelper.showToast(R.string.share_app_verion_low);
        } else if (cancel) {
            ToastHelper.showToast(R.string.share_cancel);
        } else {
            ToastHelper.showToast(R.string.share_error);
        }
    }


    public static final String TIMELINE_TRASCTION = "timeline";
    public static final String FRIENDS_TRASCTION = "friends";

    private String buildTransaction(boolean isTimeline) {
        String prefix = isTimeline ? TIMELINE_TRASCTION : FRIENDS_TRASCTION;
        return prefix + (System.currentTimeMillis());
    }
}
