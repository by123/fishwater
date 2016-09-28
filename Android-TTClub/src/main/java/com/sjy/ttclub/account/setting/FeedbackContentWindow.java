package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.FeedbackRequest;
import com.sjy.ttclub.account.setting.adapter.FeedbackContentAdapter;
import com.sjy.ttclub.bean.account.FeedbackBean;
import com.sjy.ttclub.emoji.EmotionKeyBoard;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by gangqing on 2016/1/4.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackContentWindow extends DefaultWindow implements FeedbackRequest.FeedbackContentCallBackOnSuccess {
    private FeedbackBean.FeedbackInfo mFeedbackInfo;
    private PtrClassicFrameLayout mRefresh;
    private ListView mListView;
    private LoadingLayout mNoConnect;
    private EmotionKeyBoard mEmotionKeyBoard;
    private List<FeedbackBean.FeedbackInfo> mMessageArray = new ArrayList<FeedbackBean.FeedbackInfo>();
    private FeedbackContentAdapter mAdapter;
    private FeedbackRequest mRequest;
    private boolean mIsRefreshing = true;
    private boolean isFirst = true;

    public FeedbackContentWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_message_feedback_content_title);
        View view = View.inflate(getContext(), R.layout.account_message_letter_chat_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        initView(view);
        setNoConnect();
        initEmojiKeyBord();
        initRefresh();
    }

    private void initView(View view) {
        mRefresh = (PtrClassicFrameLayout) view.findViewById(R.id.account_message_chat_refresh);
        mListView = (ListView) view.findViewById(R.id.account_message_chat_details);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
        mEmotionKeyBoard = (EmotionKeyBoard) view.findViewById(R.id.emoji_keyboard);
    }

    public void setData(FeedbackBean.FeedbackInfo feedbackInfo) {
        mFeedbackInfo = feedbackInfo;
        mMessageArray.add(0, feedbackInfo);
        initData();
    }

    private void initData() {
        if (NetworkUtil.isNetworkConnected()) {
            mNoConnect.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
        } else {
            mNoConnect.setVisibility(View.VISIBLE);
            mRefresh.setVisibility(View.GONE);
            return;
        }
        mAdapter = new FeedbackContentAdapter(getContext(), mMessageArray);
        mListView.setAdapter(mAdapter);
        mRequest = new FeedbackRequest(getContext());
        mRequest.setFeedbackContentCallBackOnSuccess(this);
        mRequest.feedbackContentRequest(mFeedbackInfo.contentId);
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

    private void initEmojiKeyBord() {
        //表情键盘
        mEmotionKeyBoard.setVisibility(View.VISIBLE);
        mEmotionKeyBoard.setEmotionButtonVisible(true);
        mEmotionKeyBoard.setOnPostButtonOnClickListener(new EmotionKeyBoard.OnPostButtonOnClickListener() {
            @Override
            public void onPostButtonClick() {
                handlerPostChatButtonOnClick();
            }
        });
    }

    private void initRefresh() {
        final TTRefreshHeader header = new TTRefreshHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 15, 0, 15);
        mRefresh.setLoadingMinTime(800);
        mRefresh.setDurationToCloseHeader(800);
        mRefresh.setHeaderView(header);
        mRefresh.addPtrUIHandler(header);
        mRefresh.setPullToRefresh(false);
        mRefresh.isKeepHeaderWhenRefresh();
        mRefresh.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mIsRefreshing) {
                    isFirst = false;
                    mRequest.feedbackContentRequest(mFeedbackInfo.contentId);
                } else {
                    ToastHelper.showToast(getContext(), R.string.account_message_chat_have_no_more, Toast.LENGTH_SHORT);
                }
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });
    }

    private void handlerPostChatButtonOnClick() {
        String content = mEmotionKeyBoard.getEmotionEditContent().toString();
        if (StringUtils.isEmpty(content)) {
            ToastHelper.showToast(getContext(), R.string.community_post_no_content_tip);
            return;
        }
        mRequest.sendFeedbackRequest(mFeedbackInfo.contentId, content);
        mEmotionKeyBoard.setEmotionEditContent(null);
    }

    @Override
    public void onRequestSuccess(List<FeedbackBean.FeedbackInfo> feedbackInfoList, boolean isRequesting) {
        //区别发送和加载更多
        if (isFirst) {
            mMessageArray.clear();
            mMessageArray.add(0, mFeedbackInfo);
            mMessageArray.addAll(feedbackInfoList);
        } else {
            mMessageArray.remove(0);
            mMessageArray.addAll(0, feedbackInfoList);
            mMessageArray.add(0, mFeedbackInfo);
        }
        mIsRefreshing = isRequesting;
        mAdapter.notifyDataSetChanged();
        //显示当前行
        if (isFirst) {
            mListView.setSelection(mMessageArray.size() - 1);
        } else {
            isFirst = true;
            mListView.setSelection(0);
        }
    }

    @Override
    public void onSendSuccess() {
        initData();
    }
}
