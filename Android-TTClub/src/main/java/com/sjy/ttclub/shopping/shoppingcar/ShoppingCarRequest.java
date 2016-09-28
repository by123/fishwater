package com.sjy.ttclub.shopping.shoppingcar;

import com.google.gson.Gson;
import com.sjy.ttclub.bean.shop.ShoppingCarGoodsState;
import com.sjy.ttclub.bean.shop.ShoppingCarParamBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingCarGoods;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chenjiawei on 2016/1/4.
 */
public class ShoppingCarRequest {
    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private boolean mIsRequesting = false;
    private boolean mHasMore = false;
    private ShoppingCarParamBean mShoppingCarParamBean;
    private List<ShoppingCarParamBean> mShoppingCarParamBeans;

    public ShoppingCarRequest() {
    }

    public ShoppingCarRequest(ShoppingCarParamBean entity) {
        this.mShoppingCarParamBean = entity;
    }

    public ShoppingCarRequest(List<ShoppingCarParamBean> entitys) {
        this.mShoppingCarParamBeans = entitys;
    }

    public void startRequest(final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        mIsRequesting = true;
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartList");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, JTBShoppingCarGoods.class, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj == null) {
                    handleGetShoppingCarDataFailed(CommonConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                JTBShoppingCarGoods shoppingCarGoods = (JTBShoppingCarGoods) obj;
                handleGetShoppingCarDataSuccess(shoppingCarGoods.getData(), callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                handleGetShoppingCarDataFailed(code, callback);
            }
        });
    }

    public void startShoppingCarAddRequest(final ShoppingCarRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        mIsRequesting = true;
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartAdd");
        httpManager.addParams("goodsId", mShoppingCarParamBean.getGoodsId());
        httpManager.addParams("specId", mShoppingCarParamBean.getSpecId());
        httpManager.addParams("goodsCount", mShoppingCarParamBean.getGoodsCount());
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                int state = 1;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (state == 0) {
                    handleShoppingCarSuccess(callback);
                } else {
                    handleShoppingCarFailed(CommonConst.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                handleShoppingCarFailed(code, callback);
            }
        });
    }

    public void startShoppingCarUpdateRequest(final ShoppingCarRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        mIsRequesting = true;
        Gson gson = new Gson();
        String json = gson.toJson(mShoppingCarParamBeans);
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartUpdate");
        httpManager.addParams("data", json);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                int state = 1;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (state == 0) {
                    handleShoppingCarSuccess(callback);
                } else {
                    handleShoppingCarFailed(CommonConst.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
//                super.onError(errorStr, code);
                handleShoppingCarFailed(code, callback);
            }
        });
    }


    public void startShoppingCarDeleteRequest(final ShoppingCarRequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        mIsRequesting = true;
        Gson gson = new Gson();
        String json = gson.toJson(mShoppingCarParamBeans);
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartDelete");
        httpManager.addParams("data", json);
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                int state = 1;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (state == 0) {
                    handleShoppingCarSuccess(callback);
                } else {
                    handleShoppingCarFailed(CommonConst.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                handleShoppingCarFailed(code, callback);
            }
        });
    }

    public void startGetShoppingCarCountRequest(final ShoppingCarCountResultCallback callback) {
        if (callback == null) {
            return;
        }
        mIsRequesting = true;
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "cartCount");
        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                int state = 1;
                int count = 0;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    state = jsonObject.getInt("status");
                    JSONObject jsonObject2=new JSONObject(jsonObject.getString("data"));
                    count = jsonObject2.getInt("cartCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (state == 0) {
                    handleShoppingCarCountSuccess(count, callback);
                } else {
                    handleGetShoppingCarCountFailed(CommonConst.ERROR_TYPE_DATA, callback);
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                super.onError(errorStr, code);
                handleGetShoppingCarCountFailed(code, callback);
            }
        });
    }

    private void handleGetShoppingCarDataSuccess(ShoppingCarGoodsState shoppingCarGoodsBeans, RequestResultCallback callback) {
        callback.onResultSuccess(shoppingCarGoodsBeans);
    }

    private void handleShoppingCarSuccess(ShoppingCarRequestResultCallback callback) {
        callback.onResultSuccess();
    }

    private void handleShoppingCarCountSuccess(int count, ShoppingCarCountResultCallback callback) {
        callback.onResultSuccess(count);
    }

    private void handleGetShoppingCarCountFailed(int errorType, ShoppingCarCountResultCallback callback) {
        callback.onResultFail(errorType);
    }

    private void handleGetShoppingCarDataFailed(int errorType, RequestResultCallback callback) {
        callback.onResultFail(errorType);
    }

    private void handleShoppingCarFailed(int errorType, ShoppingCarRequestResultCallback callback) {
        callback.onResultFail(errorType);
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ShoppingCarGoodsState shoppingCarGoodsData);
    }


    public interface ShoppingCarRequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess();
    }

    public interface ShoppingCarCountResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(int count);
    }
}
