package com.sjy.ttclub.community.homepage;

import android.content.Context;

import com.sjy.ttclub.bean.community.CommunityBannerBean;
import com.sjy.ttclub.bean.community.CommunityHomeJsonBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;

import java.util.ArrayList;

/**
 * Created by zwl on 2015/11/16.
 * Email: 1501448275@qq.com
 */
public class CommunityHomepageRequest {

    private boolean mIsRequesting = false;
    private Context mContext;
    private ArrayList<CommunityListItemInfo> mHomeItems = new ArrayList<>();
    private IHttpManager mIHttpManager;

    public CommunityHomepageRequest(Context context) {
        mContext = context;

    }

    public void startRequest(RequestResultCallback callback) {
        startRequestCardByType(callback);
    }

    private void startRequestCardByType(final RequestResultCallback
                                                callback) {

        if (callback == null) {
            return;
        }
        if (mIsRequesting) {
            callback.onResultFail(CommunityConstant.ERROR_TYPE_REQUESTING);
            return;
        }
        mIsRequesting = true;
        mIHttpManager = HttpManager.getBusinessHttpManger();
        mIHttpManager.addParams("a", "bbsHome");
        mIHttpManager.addParams("userSex", String.valueOf("1"));
        mIHttpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, CommunityHomeJsonBean.class, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                CommunityHomeJsonBean homeJsonBean = (CommunityHomeJsonBean) obj;
                handleGetCardsSuccess(homeJsonBean, callback);
            }

            @Override
            public void onError(String errorStr, int code) {
                handleGetCardsFailed(code, callback);
            }
        });
    }


    private void handleGetCardsFailed(int errorType, RequestResultCallback callback) {
        mIsRequesting = false;
        callback.onResultFail(errorType);
    }

    private void handleGetCardsSuccess(CommunityHomeJsonBean homeJsonBean, RequestResultCallback callback) {
        mIsRequesting = false;

        if (HttpCode.SUCCESS_CODE == homeJsonBean.getStatus()) {

            //获取数据列表
            ArrayList<CommunityListItemInfo> newList = new ArrayList<>();

            CommunityListItemInfo bannerItem = new CommunityListItemInfo();
            bannerItem.setItemType(CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_BANNER);
            CommunityBannerBean bannerBean=new CommunityBannerBean();
            bannerBean.setBanners(homeJsonBean.getData().getBanners());
            bannerItem.setData(bannerBean);
            newList.add(bannerItem);
            if (homeJsonBean.getData().getHotCircles().size() > 0) {
                CommunityListItemInfo allCircleTitleItem = new CommunityListItemInfo();
                allCircleTitleItem.setItemType(CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_ALL_CIRCLE_TITLE);
                newList.add(allCircleTitleItem);
                for (int i = 0; i < homeJsonBean.getData().getHotCircles().size(); i++) {
                    CommunityListItemInfo item = new CommunityListItemInfo();
                    item.setItemType(CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_HOT_CIRCLE);
                    item.setData(homeJsonBean.getData().getHotCircles().get(i));
                    newList.add(item);
                }
            }
            //判断对象是否为空
            if (homeJsonBean.getData().getQa() != null || homeJsonBean.getData().getQa().getQuestion() != null || homeJsonBean.getData().getQa().getPostId() != 0) {
                CommunityListItemInfo allQATitleItem = new CommunityListItemInfo();
                allQATitleItem.setItemType(CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_QA_TITLE);
//                    newList.add(allQATitleItem);
                CommunityListItemInfo qaItem = new CommunityListItemInfo();
                qaItem.setItemType(CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_QA);
                qaItem.setData(homeJsonBean.getData().getQa());
                newList.add(qaItem);
            }
            mHomeItems.clear();
            mHomeItems.addAll(newList);
            callback.onResultSuccess(mHomeItems, newList);
        } else {
            handleGetCardsFailed(CommunityConstant.ERROR_TYPE_DATA, callback);
        }
    }


    public void reset() {
        mHomeItems.clear();
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public boolean isEmpty() {
        return mHomeItems.isEmpty();
    }

    public interface RequestResultCallback {
        void onResultFail(int errorType);

        void onResultSuccess(ArrayList<CommunityListItemInfo> allList, ArrayList<CommunityListItemInfo> newList);
    }
}
