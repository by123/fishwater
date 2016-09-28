package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.RelationshipRequest;
import com.sjy.ttclub.account.personal.adapter.FansAdapter;
import com.sjy.ttclub.account.personal.adapter.FollowersAdapter;
import com.sjy.ttclub.bean.account.RelationshipBean;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/22.
 * Email:denggangqing@ta2she.com
 */
public class RelationshipWindow extends DefaultWindow {
    private ListView mListView;
    private LoadMoreListViewContainer mLoadMoreListViewContainer;
    private int mType;
    private List<RelationshipBean.Data.Follow> mRelationshipList;
    private ImageView mNoData;
    private FollowersAdapter mFollowersAdapter;
    private FansAdapter mFansAdapter;
    private boolean mAccountDataChange = false;
    private LoadingLayout mNoConnect;

    public RelationshipWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        View view = View.inflate(getContext(), R.layout.account_relationship_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        mRelationshipList = new ArrayList<RelationshipBean.Data.Follow>();
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);

        initView(view);
        setNoConnect();
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.account_relationship_list_view);
        mNoData = (ImageView) view.findViewById(R.id.account_relationship_no_data);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
        mLoadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.account_relationship_list_view_container);
        mLoadMoreListViewContainer.useDefaultFooter();
        mLoadMoreListViewContainer.setAutoLoadMore(true);
        mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //获取最后一条数据的时间
                String timestamp = mRelationshipList.get(mRelationshipList.size() - 1).timestamp;
                getRelationship(timestamp);
            }
        });
    }

    public void setType(int type) {
        mType = type;
        if (Constant.FOLLOW == type) {
            setTitle(R.string.account_relationship_follow_title);
            mFollowersAdapter = new FollowersAdapter(getContext(), mRelationshipList);
            mListView.setAdapter(mFollowersAdapter);
        } else {
            setTitle(R.string.account_relationship_fans_title);
            mFansAdapter = new FansAdapter(getContext(), mRelationshipList);
            mListView.setAdapter(mFansAdapter);
        }
        if (NetworkUtil.isNetworkConnected()) {
            mNoConnect.setVisibility(View.GONE);
            mLoadMoreListViewContainer.setVisibility(View.VISIBLE);
        } else {
            mNoConnect.setVisibility(View.VISIBLE);
            mLoadMoreListViewContainer.setVisibility(View.GONE);
            return;
        }
    }

    private void setNoConnect() {
        mNoConnect.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
        mNoConnect.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mNoConnect.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                setType(mType);
                getRelationship(null);
            }
        });
    }

    private void getRelationship(String timestamp) {
        RelationshipRequest relationshipRequest = new RelationshipRequest();
        relationshipRequest.startRelationshipRequest(String.valueOf(mType), timestamp, new RelationshipRequestIHttpCallBack());
    }

    private void setNoData() {
        mLoadMoreListViewContainer.setVisibility(View.GONE);
        mNoData.setVisibility(View.VISIBLE);
        //如果当前是关注页面，设置没有数据时的背景图
        if (Constant.FOLLOW == mType) {
            mNoData.setImageResource(R.drawable.account_relationship_follow_no_data);
        } else {  //如果当前是粉丝页面，设置没有数据时的背景图
            mNoData.setImageResource(R.drawable.account_relationship_fans_no_data);
        }
    }

    private void setData(List<RelationshipBean.Data.Follow> followList) {
        mLoadMoreListViewContainer.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.GONE);
        //判断是否还有数据，如果新加载的数据小于每页面加载的数据，则没有更多数据了
        if (followList.size() < Constant.RELATIONSHIP_PAGER_SIZE) {
            mLoadMoreListViewContainer.loadMoreFinish(false, false);
        } else {
            mLoadMoreListViewContainer.loadMoreFinish(false, true);
        }
        //刷新
        if (Constant.FOLLOW == mType) {
            mFollowersAdapter.notifyDataSetChanged();
        } else {
            mFansAdapter.notifyDataSetChanged();
        }
    }

    private class RelationshipRequestIHttpCallBack extends AccountBaseIHttpCallBack {

        @Override
        public <T> void onSuccess(T obj, String result) {
            if (obj instanceof RelationshipBean) {
                RelationshipBean relationshipBean = (RelationshipBean) obj;
                List<RelationshipBean.Data.Follow> followList = relationshipBean.data.followArray;
                mRelationshipList.addAll(followList);
                if (mRelationshipList.size() == 0) {
                    setNoData();
                } else {
                    setData(followList);
                }
            }
        }

        @Override
        public void onFail(String errorStr, int code) {

        }
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            getRelationship(null);
        }
    }

    public void setAccountDataChange() {
        if (!mAccountDataChange) {
            mAccountDataChange = true;
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_BEFORE_POP_OUT == stateFlag || STATE_ON_HIDE == stateFlag) {
            if (mAccountDataChange) {
                AccountManager.getInstance().notifyAccountDataChanged();
            }
        }else if (STATE_AFTER_PUSH_IN == stateFlag || STATE_ON_SHOW == stateFlag) {
            mRelationshipList.clear();
            getRelationship(null);
        }
    }
}
