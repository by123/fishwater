package com.sjy.ttclub.account.message;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.message.adapter.ReplyMeAdapter;
import com.sjy.ttclub.account.model.ReplyMeRequest;
import com.sjy.ttclub.bean.account.ExternalLinksHelperParam;
import com.sjy.ttclub.bean.account.ReplyMeMsgArray;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.LetterExternalLinksHelper;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ReplyMeExternalLinksHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/29.
 * Email:denggangqing@ta2she.com
 */
public class ReplyMeWindow extends DefaultWindow implements ReplyMeRequest.ReplyMeCallBack, AdapterView.OnItemClickListener {
    private ListView mListView;
    private ReplyMeRequest mReplyMeRequest;
    private List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> mArrayList = new ArrayList<ReplyMeMsgArray.MsgArrayObj.MsgArrays>();
    private ReplyMeAdapter mReplyMeAdapter;
    private LoadMoreListViewContainer mLoadMoreListViewContainer;
    private ImageView mNoData;
    private LoadingLayout mNoConnect;
    private int mType;

    public ReplyMeWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        View view = View.inflate(getContext(), R.layout.account_relationship_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);

        mReplyMeRequest = new ReplyMeRequest();
        mReplyMeRequest.setReplyMeCallBack(this);

        initView(view);
        //统计
        StatsModel.stats(StatsKeyDef.MYSELF_REPLY);
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            initData();
            request();
        }
    }

    public void reStart() {
        mReplyMeRequest = new ReplyMeRequest();
        mReplyMeRequest.setReplyMeCallBack(this);
        mArrayList.clear();
        request();
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.account_relationship_list_view);
        mLoadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.account_relationship_list_view_container);
        mNoData = (ImageView) view.findViewById(R.id.account_relationship_no_data);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
    }

    public void setType(int type) {
        mType = type;
        initData();
    }

    private void initData() {
        initPublicData();
        if (NetworkUtil.isNetworkConnected()) {
            mNoConnect.setVisibility(View.GONE);
            mLoadMoreListViewContainer.setVisibility(View.VISIBLE);
        } else {
            mNoConnect.setVisibility(View.VISIBLE);
            mLoadMoreListViewContainer.setVisibility(View.GONE);
            return;
        }
        if (Constant.TYPE_REPLY_ME == mType) {   //回复我的
            setTitle(R.string.account_message_reply_me);
            initReplyMeData();
        } else {  //赞、同问
            setTitle(R.string.account_message_agree);
            initPraiseMeData();
        }
    }

    /**
     * 回复我的
     */
    private void initReplyMeData() {
        mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mReplyMeRequest.msgReplyMeRequest();
            }
        });
    }

    /**
     * 赞、同问
     */
    private void initPraiseMeData() {
        mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mReplyMeRequest.msgPraiseMeRequest();
            }
        });
    }

    /**
     * 公共样式
     */
    private void initPublicData() {
        setNoConnect();
        mListView.setOnItemClickListener(this);
        mLoadMoreListViewContainer.useDefaultFooter();
        mLoadMoreListViewContainer.setAutoLoadMore(true);
        mReplyMeAdapter = new ReplyMeAdapter(getContext(), mArrayList);
        mListView.setAdapter(mReplyMeAdapter);
    }

    private void setNoConnect() {
        mNoConnect.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
        mNoConnect.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mNoConnect.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                initData();
                request();
            }
        });
    }

    @Override
    public void msgReplyMeRequestOnSuccess(List<ReplyMeMsgArray.MsgArrayObj.MsgArrays> arrayList, boolean isHaveMore) {
        mArrayList.addAll(arrayList);
        if (isHaveMore) {
            mLoadMoreListViewContainer.loadMoreFinish(false, true);
        } else {
            mLoadMoreListViewContainer.loadMoreFinish(false, false);
        }
        if (mArrayList.size() == 0) {
            mNoData.setVisibility(View.VISIBLE);
            mLoadMoreListViewContainer.setVisibility(View.GONE);
        } else {
            mNoData.setVisibility(View.GONE);
            mLoadMoreListViewContainer.setVisibility(View.VISIBLE);
            mReplyMeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReplyMeMsgArray.MsgArrayObj.MsgArrays msg = mArrayList.get(position);
        ExternalLinksHelperParam param = new ExternalLinksHelperParam();
        param.setMsgObj(msg);
        param.setId(msg.getToPostId());
        param.setType(msg.getType());
        ReplyMeExternalLinksHelper externalLinksHelper = new ReplyMeExternalLinksHelper();
        externalLinksHelper.goExternalLinks(param);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (STATE_AFTER_PUSH_IN == stateFlag) {
            request();
        }
    }

    private void request(){
        if (Constant.TYPE_REPLY_ME == mType) {   //回复我的
            mReplyMeRequest.msgReplyMeRequest();
        } else {  //赞、同问
            mReplyMeRequest.msgPraiseMeRequest();
        }
    }
}
