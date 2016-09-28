package com.sjy.ttclub.account.personal;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.MessageChatDetailsRequestHelper;
import com.sjy.ttclub.account.personal.adapter.LettersBlacklistAdapter;
import com.sjy.ttclub.bean.account.BlacklistBean;
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
 * Created by gangqing on 2015/12/31.
 * Email:denggangqing@ta2she.com
 */
public class LettersBlacklistWindow extends DefaultWindow implements MessageChatDetailsRequestHelper.BlacklistCallBackSuccess {
    private ListView mListView;
    private LoadMoreListViewContainer mLoadMore;
    private LoadingLayout mNoConnect;
    private MessageChatDetailsRequestHelper mHelper;
    private List<BlacklistBean.BlacklistObj.Blacklists> mBlacklist = new ArrayList<BlacklistBean.BlacklistObj.Blacklists>();
    private LettersBlacklistAdapter mBlacklistAdapter;

    public LettersBlacklistWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_personal_blacklist_title);
        View view = View.inflate(getContext(), R.layout.account_personal_blacklist_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());

        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);

        initView(view);
        setNoConnect();
        initData();
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            initData();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_BEFORE_POP_OUT == stateFlag) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        }
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.account_blacklist_list_view);
        mLoadMore = (LoadMoreListViewContainer) view.findViewById(R.id.account_blacklist_list_view_container);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
    }

    private void setNoConnect() {
        mNoConnect.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
        mNoConnect.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mNoConnect.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                initData();
            }
        });
    }

    private void initData() {
        if (NetworkUtil.isNetworkConnected()) {
            mNoConnect.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.VISIBLE);
        } else {
            mNoConnect.setVisibility(View.VISIBLE);
            mLoadMore.setVisibility(View.GONE);
            return;
        }
        mHelper = new MessageChatDetailsRequestHelper(getContext());
        mHelper.setBlacklistCallBack(this);
        mHelper.blacklist();

        mLoadMore.useDefaultFooter();
        mLoadMore.setAutoLoadMore(true);
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mHelper.blacklist();
            }
        });

        mBlacklistAdapter = new LettersBlacklistAdapter(getContext(), mBlacklist);
        mListView.setAdapter(mBlacklistAdapter);
    }

    @Override
    public void onSuccessForBlacklist(List<BlacklistBean.BlacklistObj.Blacklists> blacklist, boolean isHaveMore) {
        mBlacklist.addAll(0, blacklist);
        if (isHaveMore) {
            mLoadMore.loadMoreFinish(false, true);
        } else {
            mLoadMore.loadMoreFinish(false, false);
        }
        mBlacklistAdapter.notifyDataSetChanged();
    }
}
