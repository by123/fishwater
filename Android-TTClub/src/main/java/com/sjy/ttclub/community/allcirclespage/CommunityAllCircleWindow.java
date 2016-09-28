package com.sjy.ttclub.community.allcirclespage;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwl on 2015/11/26.
 * Email: 1501448275@qq.com
 */
public class CommunityAllCircleWindow extends DefaultWindow {
    private ListView mAllCirclesListView;
    private LoadingLayout mAllCirclesLoadingLayout;
    private CommunityCirclesAdapter mAllCircleAdapter;
    private List<CommunityCircleBean> mCircles=new ArrayList<>();
    private CommunityAllCircleRequest mAllCirclesRequest;
    public CommunityAllCircleWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(ResourceHelper.getString(R.string.community_all_circle));
        initView();
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag){
            case STATE_AFTER_PUSH_IN:
                tryGetAllCircles();
                break;

        }
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.community_all_circle_layout, null);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        mAllCirclesListView = (ListView) parentView.findViewById(R.id.all_circles_listview);
        mAllCirclesLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.loading_layout);
        mAllCircleAdapter=new CommunityCirclesAdapter(getContext(),mCircles);
        mAllCirclesListView.setAdapter(mAllCircleAdapter);
        mAllCirclesLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetAllCircles();
            }
        });
        initRequest();
    }
    private void initRequest(){
        mAllCirclesRequest=new CommunityAllCircleRequest(getContext());
    }
    private void tryGetAllCircles(){
        mAllCirclesLoadingLayout.setVisibility(View.VISIBLE);
        mAllCirclesListView.setVisibility(View.GONE);
        mAllCirclesLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mAllCirclesRequest.startRequest(new CommunityAllCircleRequest.RequestResultCallback() {
            @Override
            public void onResultFailed(int errorType) {
                handleGetDateFailed(errorType);
            }

            @Override
            public void onResultSuccess(List<CommunityCircleBean> datas) {
                handleGetDataSuccess(datas);
            }
        });
    }
    private void handleGetDateFailed(int errorCode) {
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            setDataFailedTips(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), false);
            return;
        }
        if (errorCode == HttpCode.ERROR_NETWORK) {
            setDataFailedTips(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
            return;
        }
    }

    private void handleGetDataSuccess(List<CommunityCircleBean> datas) {
        mCircles.addAll(datas);
        if (mCircles.size() > 0) {
            mAllCirclesListView.setVisibility(View.VISIBLE);
            mAllCirclesLoadingLayout.setVisibility(View.GONE);
            mAllCircleAdapter.notifyDataSetChanged();
        } else {
            setDataFailedTips(R.drawable.no_data_bg, ResourceHelper.getString(R.string.homepage_data_empty), false);
        }
    }

    private void setDataFailedTips(int imageSoruceId, String tip, boolean isShowBtn) {
        mAllCirclesListView.setVisibility(View.GONE);
        mAllCirclesLoadingLayout.setVisibility(View.VISIBLE);
        mAllCirclesLoadingLayout.setBgContent(imageSoruceId, tip, isShowBtn);
    }
}
