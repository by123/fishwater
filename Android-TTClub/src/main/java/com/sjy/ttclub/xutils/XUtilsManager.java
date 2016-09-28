
package com.sjy.ttclub.xutils;

import android.util.Log;

import com.google.gson.Gson;
import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.IDownLoadFileCallBack;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpLoadingCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.body.BodyItemWrapper;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhxu on 2015/11/23.
 * Email:357599859@qq.com
 */
public class XUtilsManager implements IHttpManager {
    private final static String TAG = "XUtilsManager";

    private RequestParams mParams;

    private LinkedHashMap<String, String> mHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, String> mHeaderMap = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> mFileMap = new LinkedHashMap<>();

    private static final String DEFAULT_ERROR_CALLBACK_STRING = "";
    private String mBodyContent;

    @Override
    public void addParams(String key, String val) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "addParams " + key + " " + val);
        }
        mHashMap.put(key, val);
    }

    @Override
    public void addParams(String name, Object value, String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            mFileMap.put(name, value);
        } else {
            mFileMap.put(name, new BodyItemWrapper(value, contentType));
        }
    }

    @Override
    public void addHeadParams(String key, String val) {
        mHeaderMap.put(key, val);
    }

    @Override
    public void updateHeadParams(String key, String val) {
        if (mParams != null && mParams.getHeaders() != null) {
            mParams.addHeader(key, val);
        }
    }

    @Override
    public void setHeadParams(HashMap<String, String> headParams) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "setHeadParams " + headParams);
        }
        mHeaderMap.putAll(headParams);
    }

    @Override
    public <T> T requestSync(String url, com.sjy.ttclub.network.HttpMethod httpMethod, Class<T> obj) {
        setParams(url, httpMethod);
        T _class = null;
        try {
            switch (httpMethod) {
                case GET:
                    _class = x.http().requestSync(HttpMethod.GET, mParams, obj);

                    break;
                case POST:
                    _class = x.http().requestSync(HttpMethod.POST, mParams, obj);
                    break;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return _class;
    }

    @Override
    public void request(String url, com.sjy.ttclub.network.HttpMethod httpMethod, IHttpCallBack callBack) {
        request(url, httpMethod, null, callBack);
    }

    @Override
    public void requestWithLoading(String url, com.sjy.ttclub.network.HttpMethod httpMethod, final
    IHttpLoadingCallBack callBack) {
        requestWithLoading(url, httpMethod, null, callBack);
    }

    @Override
    public <T> void requestWithLoading(String url, com.sjy.ttclub.network.HttpMethod httpMethod, final Class<T> _obj,
                                       final IHttpLoadingCallBack callBack) {

        setParams(url, httpMethod);

        HttpMethod _httpMethod = HttpMethod.GET;
        switch (httpMethod) {
            case GET:
                _httpMethod = HttpMethod.GET;
                break;
            case POST:
                _httpMethod = HttpMethod.POST;
                break;
        }

        x.http().request(_httpMethod, mParams, new Callback.ProgressCallback<String>() {
            @Override
            public void onLoading(long totle, long current, boolean isUpdating) {
                if (callBack != null) {
                    callBack.onLoading(totle, current, isUpdating);
                }
            }

            @Override
            public void onStarted() {
                if (callBack != null) {
                    callBack.onStart();
                }
            }

            @Override
            public void onWaiting() {
                if (callBack != null) {
                    callBack.onWaiting();
                }
            }

            @Override
            public void onSuccess(String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onSuccess " + result);
                }
                if (StringUtils.isEmpty(result)) {
                    callBack.onError(result, HttpCode.JSON_IS_EMPTY);
                    return;
                }
                int state;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                    if (state == HttpCode.SUCCESS_CODE) {
                        Gson gson;
                        T obj = null;
                        if (callBack != null) {
                            if (_obj != null) {
                                try {
                                    gson = new Gson();
                                    obj = gson.fromJson(result, _obj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    obj = null;
                                }
                            }
                            callBack.onSuccess(obj, result);
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onError(result, state);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onSuccess(null, result);
                    }
                }
                clear();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onError", ex);
                }

                if (callBack != null) {
                    if (ex != null) {
                        ex.printStackTrace();
                        callBack.onError(ex.toString(), HttpCode.ERROR_NETWORK);
                    } else {
                        callBack.onError(DEFAULT_ERROR_CALLBACK_STRING, HttpCode.ERROR_NETWORK);
                    }
                }
                clear();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onCancelled");
                }
                if (callBack != null) {
                    if (cex != null) {
                        callBack.onError(cex.toString(), HttpCode.ERROR_CANCEL);
                    } else {
                        callBack.onError(DEFAULT_ERROR_CALLBACK_STRING, HttpCode.ERROR_CANCEL);
                    }

                }
                clear();
            }

            @Override
            public void onFinished() {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onFinished");
                }
                clear();
            }
        });
    }

    @Override
    public <T> void request(String url, com.sjy.ttclub.network.HttpMethod httpMethod, final Class<T> _class, final
    IHttpCallBack callBack) {

        setParams(url, httpMethod);

        HttpMethod _httpMethod = HttpMethod.GET;
        switch (httpMethod) {
            case GET:
                _httpMethod = HttpMethod.GET;
                break;
            case POST:
                _httpMethod = HttpMethod.POST;
                break;
        }
        x.http().request(_httpMethod, mParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onSuccess " + result);
                }
                if (StringUtils.isEmpty(result)) {
                    callBack.onError(result, HttpCode.JSON_IS_EMPTY);
                    return;
                }
                int state;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                    if (state == HttpCode.SUCCESS_CODE) {
                        Gson gson;
                        T obj = null;
                        if (callBack != null) {
                            if (_class != null) {
                                try {
                                    gson = new Gson();
                                    obj = gson.fromJson(result, _class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    obj = null;
                                }
                            }
                            callBack.onSuccess(obj, result);
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onError(result, state);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onSuccess(null, result);
                    }
                }
                clear();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onError", ex);
                }
                if (callBack != null) {
                    if (ex != null) {
                        callBack.onError(ex.toString(), HttpCode.ERROR_NETWORK);
                    } else {
                        callBack.onError(DEFAULT_ERROR_CALLBACK_STRING, HttpCode.ERROR_NETWORK);
                    }
                }
                clear();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onCancelled");
                }
                if (callBack != null) {
                    if (cex != null) {
                        callBack.onError(cex.toString(), HttpCode.ERROR_CANCEL);
                    } else {
                        callBack.onError(DEFAULT_ERROR_CALLBACK_STRING, HttpCode.ERROR_CANCEL);
                    }
                }
                clear();
            }

            @Override
            public void onFinished() {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "onFinished");
                }
                clear();
            }
        });
    }

    @Override
    public void setParams(String url, com.sjy.ttclub.network.HttpMethod httpMethod) {

        if (mParams == null) {
            mParams = new RequestParams(url);
        }

        if (!StringUtils.isEmpty(mBodyContent)) {
            mParams.setBodyContent(mBodyContent);
        }

        if (!mHeaderMap.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = mHeaderMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                mParams.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (!mHashMap.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = mHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                switch (httpMethod) {
                    case GET:
                        mParams.addQueryStringParameter(entry.getKey(), entry.getValue());
                        break;
                    case POST:
                        mParams.addBodyParameter(entry.getKey(), entry.getValue());
                        break;
                }
            }
        }

        if (!mFileMap.isEmpty()) {
            mParams.setMultipart(true);
            Iterator<Map.Entry<String, Object>> iterator = mFileMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                Object value = entry.getValue();
                if (value instanceof BodyItemWrapper) {
                    BodyItemWrapper entityWrapper = (BodyItemWrapper) value;
                    mParams.addBodyParameter(entry.getKey(), entityWrapper.getValue(), entityWrapper.getContentType());
                } else {
                    mParams.addBodyParameter(entry.getKey(), value, null);
                }
            }
        }
    }

    @Override
    public void downLoadFile(String url, String saveFileUrl, final IDownLoadFileCallBack callBack) {

        if (StringUtils.isEmpty(url)) {
            callBack.onError(CommonConst.ERROR_TYPE_URL_FAULT);
            return;
        }

        RequestParams params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(saveFileUrl);
        x.http().get(params, new Callback.CommonCallback<File>() {
            @Override
            public void onSuccess(File file) {
                callBack.onSuccess(file);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                callBack.onError(HttpCode.ERROR_NETWORK);
            }

            @Override
            public void onCancelled(CancelledException e) {
                callBack.onError(HttpCode.ERROR_CANCEL);
            }

            @Override
            public void onFinished() {
                callBack.onFinish();
            }
        });
    }

    @Override
    public void setBodyContent(String content) {
        mBodyContent = content;
    }

    private void clear() {
        mParams.clearParams();
        mHashMap.clear();
        mFileMap.clear();
    }
}
