package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.FeedbackRequest;
import com.sjy.ttclub.bean.account.FeedbackBean;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by gangqing on 2015/12/24.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackRecordTabView implements ITabView {
    private Context mContext;
    private View mRootView;

    private RecordAdapter mAdapter;
    private ListView mListView;
    private LoadMoreListViewContainer mLoadMoreLayout;
    private PtrClassicFrameLayout mPtrLayout;

    private FeedbackRequest mRequest;

    private ArrayList<FeedbackBean.FeedbackInfo> mRecordList = new ArrayList<>();

    public FeedbackRecordTabView(Context context) {
        this.mContext = context;
        mRequest = new FeedbackRequest(context);
        initView();
    }

    private View initView() {
        View view = View.inflate(mContext, R.layout.account_feedback_feedback_record, null);
        mRootView = view;
        mAdapter = new RecordAdapter(mContext, mRecordList);
        mListView = (ListView) view.findViewById(R.id.account_feedback_record_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = Message.obtain();
                msg.obj = mRecordList.get(position);
                msg.what = MsgDef.MSG_SHOW_FEEDBACK_CONTENT_WINDOW;
                MsgDispatcher.getInstance().sendMessage(msg);
            }
        });

        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.account_feedback_record_ptr);
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPtrLayout.setVisibility(View.VISIBLE);
        mPtrLayout.setLoadingMinTime(1000);
        mPtrLayout.setDurationToCloseHeader(800);
        mPtrLayout.setHeaderView(header);
        mPtrLayout.addPtrUIHandler(header);
        mPtrLayout.setPullToRefresh(false);
        mPtrLayout.isKeepHeaderWhenRefresh();
        mPtrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mRequest.isRequesting()) {
                    frame.refreshComplete();
                    return;
                }
                tryRequestData(false);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });

        mLoadMoreLayout = (LoadMoreListViewContainer) view.findViewById(R.id
                .account_feedback_record_loadmore_container);
        mLoadMoreLayout.useDefaultFooter();
        mLoadMoreLayout.setAutoLoadMore(true);
        mLoadMoreLayout.loadMoreFinish(false, true);
        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                tryRequestData(true);
            }
        });
        return view;
    }

    @Override
    public String getTitle() {
        return ResourceHelper.getString(R.string.account_feedback_record);
    }

    @Override
    public View getTabView() {
        return mRootView;
    }

    @Override
    public void onPrepareContentView() {

    }

    @Override
    public void onTabChanged(int tabChangedFlag) {
        if (tabChangedFlag == ITabView.TAB_TO_SHOW) {
            tryRequestData(false);
        }
    }


    public void tryRequestData(boolean isLoadMore) {
        mRequest.startRecordRequest(isLoadMore, new FeedbackRequest.FeedbackRecordCallback() {
            @Override
            public void onRequestSuccess(ArrayList<FeedbackBean.FeedbackInfo> list) {
                mRecordList.clear();
                mRecordList.addAll(list);
                mAdapter.notifyDataSetChanged();
                mPtrLayout.refreshComplete();
                mLoadMoreLayout.loadMoreFinish(false, mRequest.hasMore());
            }

            @Override
            public void onRequestFailed(int errorType) {
                mPtrLayout.refreshComplete();
                mLoadMoreLayout.loadMoreFinish(false, mRequest.hasMore());
            }
        });
    }

    private class RecordAdapter extends ArrayAdapter<FeedbackBean.FeedbackInfo> {

        RecordAdapter(Context context, ArrayList<FeedbackBean.FeedbackInfo> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.account_feedback_feedback_record_item, null);
            }
            setupRecordItemView(convertView, getItem(position));
            return convertView;
        }

        private void setupRecordItemView(View view, FeedbackBean.FeedbackInfo info) {
            TextView questionView = (TextView) view.findViewById(R.id.account_feedback_record_question);
            TextView timeView = (TextView) view.findViewById(R.id.account_feedback_record_time);
            TextView dialogueCount = (TextView) view.findViewById(R.id.account_feedback_record_dialog_count);
            questionView.setText(info.content);
            timeView.setText(info.createTime);
            dialogueCount.setText(info.dialogueCount + "对话");

        }
    }
}
