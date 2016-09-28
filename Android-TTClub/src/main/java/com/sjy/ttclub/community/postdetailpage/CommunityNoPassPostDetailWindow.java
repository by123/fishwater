package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.TitleButtonBean;
import com.sjy.ttclub.collect.CollectRequest;
import com.sjy.ttclub.comment.TopCommentsRequest;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.CommunityReportHelper;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;

/**
 * Created by zhangwulin on 2015/12/30.
 * email 1501448275@qq.com
 */
public class CommunityNoPassPostDetailWindow extends DefaultWindow {
    private ListView mPostDetailListView;
    private LoadingLayout mPostDetailLoadingLayout;
    private CommunityPostDetailAdapter mPostDetailAdapter;
    private LoadMoreListViewContainer mPostDetailLoadMoreLayout;
    private CommunityPostBean mPostBean;
    private CommunityPostRequest mPostRequest;
    private int INIT_POST = 0;
    private int mPostId;

    public CommunityNoPassPostDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        createView();
    }

    private void createView() {
        View parentView = View.inflate(getContext(), R.layout.community_post_detail_layout, null);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        mPostDetailListView = (ListView) parentView.findViewById(R.id.post_detail_list_view);
        mPostDetailLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.loading_layout);
        mPostDetailLoadMoreLayout = (LoadMoreListViewContainer) parentView.findViewById(R.id.list_view_container);
        mPostDetailLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mPostDetailLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetPostDetailContent(INIT_POST);
            }
        });
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                tryGetPostDetailContent(INIT_POST);
                break;
            case STATE_AFTER_POP_OUT:
                break;
        }
    }

    public void setPostId(int postId) {
        this.mPostId = postId;
        initRequest();
    }

    private void initRequest() {
        mPostRequest = new CommunityPostRequest(getContext(), mPostId);
    }

    private void setBarTitle() {
        if (mPostBean.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            setTitle(ResourceHelper.getString(R.string.community_qa_detail));
        } else {
            setTitle(ResourceHelper.getString(R.string.community_post_detail));
        }
    }

    private void initUI() {
        if (mPostBean == null) {
            return;
        }
        setBarTitle();
        mPostDetailAdapter = new CommunityPostDetailAdapter(getContext(), mPostBean);
        mPostDetailListView.setAdapter(mPostDetailAdapter);
        mPostDetailAdapter.updateItemList();
    }

    private void tryGetPostDetailContent(final int initRequestflag) {
        if (mPostRequest == null) {
            return;
        }
        if (initRequestflag == INIT_POST) {
            mPostDetailLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
            mPostDetailLoadingLayout.setVisibility(VISIBLE);
            mPostDetailLoadMoreLayout.setVisibility(GONE);
        }
        mPostRequest.startRequest(new CommunityPostRequest.RequestPostResultCallback() {
            @Override
            public void onResultFailed(int errorCode) {
                handleGetCardFailed(errorCode, initRequestflag);
            }

            @Override
            public void onResultSuccess(CommunityPostBean postBean) {
                handleGetCardSuccess(postBean, initRequestflag);
            }
        });
    }

    private void handleGetCardFailed(int errorCode, int flag) {
        if (flag == INIT_POST) {
            mPostDetailLoadingLayout.setVisibility(View.VISIBLE);
            mPostDetailLoadMoreLayout.setVisibility(GONE);
        } else {
            mPostDetailLoadingLayout.setVisibility(View.GONE);
            mPostDetailLoadMoreLayout.setVisibility(VISIBLE);
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_DATA) {
            if (flag == INIT_POST) {
                mPostDetailLoadingLayout.setBgContent(R.drawable.data_error, ResourceHelper.getString(R.string.homepage_data_error), false);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_NETWORK) {
            if (flag == INIT_POST) {
                mPostDetailLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry),
                        true);
            } else {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_network_error_retry), Toast
                        .LENGTH_SHORT);
            }
            return;
        }
        if (errorCode == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
    }

    private void handleGetCardSuccess(CommunityPostBean postBean, int flag) {
        if (flag == INIT_POST) {
            mPostDetailLoadingLayout.startAnimation(AnimotionDao.getAlphaAnimationHide());
            mPostDetailLoadingLayout.setVisibility(View.GONE);
            mPostDetailLoadMoreLayout.setVisibility(VISIBLE);
            mPostDetailLoadMoreLayout.startAnimation(AnimotionDao.getAlphaAnimationShow());
        }
        mPostBean = postBean;
        initUI();
    }
}
