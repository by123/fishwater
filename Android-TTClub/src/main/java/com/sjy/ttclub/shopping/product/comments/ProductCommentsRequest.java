package com.sjy.ttclub.shopping.product.comments;

import android.content.Context;

import com.sjy.ttclub.bean.shop.ShoppingGoodsReviewBean;
import com.sjy.ttclub.bean.shop.ShoppingReviewBean;
import com.sjy.ttclub.bean.shop.json2bean.JTBShoppingReviewsList;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.shopping.ShoppingConstant;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiawei on 2015/12/31.
 */
public class ProductCommentsRequest {
    private int mPageIndex = CommonConst.START_PAGE_INDEX;
    private int mPageLoadingIndex = CommonConst.START_PAGE_INDEX;
    private int mEndId = CommonConst.START_END_ID;
    private int mPageSize = 20;

    private boolean mHasMore = false;

    private boolean mIsRequesting = false;

    private Context mContext;
    private ArrayList<ShoppingReviewBean> mCommentList = new ArrayList<ShoppingReviewBean>();

    private String mGoodsId;

    private String mType = ShoppingConstant.COMMENT_TYPE_ALL;

    public ProductCommentsRequest(Context context, String goodsId) {
        this.mContext = context;
        mGoodsId = goodsId;
    }

    public void startRequest(boolean isLoadMore, String type, final RequestResultCallback callback) {
        if (callback == null) {
            return;
        }
        boolean typeChanged = false;
        if (!mType.equals(type)) {
            typeChanged = true;
        }
        if (mIsRequesting && !typeChanged) {
            callback.onResultFail(CommonConst.ERROR_TYPE_REQUESTING);
            return;
        }
        if (typeChanged) {
            mCommentList.clear();
        }
        if (mCommentList.isEmpty() || !isLoadMore || typeChanged) {
            mEndId = HomepageConst.START_END_ID;
            mPageLoadingIndex = HomepageConst.START_PAGE_INDEX;
        } else {
            mPageLoadingIndex = mPageIndex + 1;
        }

        mIsRequesting = true;
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", "goodsReviews");
        httpManager.addParams("goodsId", mGoodsId);
        httpManager.addParams("endId", String.valueOf(mEndId));
        httpManager.addParams("pageSize", String.valueOf(mPageSize));
        httpManager.addParams("type", type);

        httpManager.request(HttpUrls.URL_SHOP, HttpMethod.POST, JTBShoppingReviewsList.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj == null) {
                    handleGetCommentFailed(CommonConst.ERROR_TYPE_DATA, callback);
                    return;
                }
                JTBShoppingReviewsList reviewsList = (JTBShoppingReviewsList) obj;
                handleGetCommentSuccess(reviewsList.getData(), callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCommentFailed(code, callback);
            }
        });
    }

    private void handleGetCommentSuccess(ShoppingGoodsReviewBean reviewBean, RequestResultCallback callback) {
        List<ShoppingReviewBean> reviewList = reviewBean.getReviews();
        if (reviewList == null || reviewList.isEmpty()) {
            mHasMore = false;
        } else {
            boolean firstPage = mPageLoadingIndex == HomepageConst.START_PAGE_INDEX;
            if (firstPage) {
                mCommentList.clear();
            }
            mEndId = StringUtils.parseInt(reviewBean.getEndId());
            mPageIndex = mPageLoadingIndex;
            int size = reviewList.size();
            if (size < mPageSize) {
                mHasMore = false;
            } else {
                mHasMore = true;
            }
            mCommentList.addAll(reviewList);
        }
        callback.onResultSuccess(reviewBean, mCommentList);
    }

    private void handleGetCommentFailed(int errorType, RequestResultCallback callback) {
        callback.onResultFail(errorType);
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public boolean isEmpty() {
        return mCommentList.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ShoppingGoodsReviewBean reviewBean, List<ShoppingReviewBean> reviewList);
    }
}
