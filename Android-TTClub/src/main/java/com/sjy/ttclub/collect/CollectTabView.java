package com.sjy.ttclub.collect;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.collect.CollectArticleBean;
import com.sjy.ttclub.bean.collect.CollectPostBean;
import com.sjy.ttclub.bean.collect.CollectProductBean;
import com.sjy.ttclub.collect.CollectListRequest.CollectItemInfo;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.homepage.HomepageConst;
import com.sjy.ttclub.homepage.feeddetail.HomepageFeedDetailInfo;
import com.sjy.ttclub.knowledge.KnowledgeDetailInfo;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.LoadingDialog;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by linhz on 2015/12/23.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class CollectTabView implements ITabView {
    private final boolean ENABLE_PULL_REFRESH = false;
    private int mType = CommonConst.COLLECT_TYPE_ARTICLE;
    private Context mContext;

    private View mRootView;
    private PtrClassicFrameLayout mPrtLayout;
    private LoadMoreListViewContainer mLoadMoreLayout;
    private LoadingLayout mLoadingLayout;
    private ListView mListView;
    private View mNoDataView;
    private View mFootRootView;
    private CheckBox mSelectAllCheckBox;
    private CollectListViewAdapter mListViewAdapter;

    private CollectListRequest mRequestHandler;

    private ArrayList<CollectItemInfo> mDataList = new ArrayList<CollectItemInfo>();

    private boolean mIsEditMode = false;

    public CollectTabView(Context context, int type) {
        mContext = context;
        mType = type;
        initViews();
        mRequestHandler = new CollectListRequest(context, mType);

    }

    @Override
    public String getTitle() {
        int titleRes;
        if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
            titleRes = R.string.collect_article;
        } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
            titleRes = R.string.collect_product;
        } else {
            titleRes = R.string.collect_post;
        }
        return ResourceHelper.getString(titleRes);
    }

    @Override
    public View getTabView() {
        return mRootView;
    }

    @Override
    public void onPrepareContentView() {
        startRequestCollectData(false);
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    private void initViews() {
        mRootView = View.inflate(mContext, R.layout.collect_article_post, null);
        mPrtLayout = (PtrClassicFrameLayout) mRootView.findViewById(R.id.collect_article_post_prt);
        mLoadMoreLayout = (LoadMoreListViewContainer) mRootView.findViewById(R.id.collect_article_post_load_more);
        mListView = (ListView) mRootView.findViewById(R.id.collect_article_post_listview);
        mLoadingLayout = (LoadingLayout) mRootView.findViewById(R.id.collect_article_post_loading);
        mNoDataView = mRootView.findViewById(R.id.collect_nocollect);
        mFootRootView = mRootView.findViewById(R.id.collect_article_post_foot);

        initPrtLayout();
        initLoadMoreLayout();
        initListView();
        initLoadingView();
        initNoDataView();
        initFootView();
    }

    private void initPrtLayout() {
        final TTRefreshHeader header = new TTRefreshHeader(mContext);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ResourceHelper.getDimen(R.dimen.space_15), 0, ResourceHelper.getDimen(R.dimen.space_10));
        mPrtLayout.setVisibility(View.VISIBLE);
        mPrtLayout.setLoadingMinTime(1000);
        mPrtLayout.setDurationToCloseHeader(800);
        mPrtLayout.setHeaderView(header);
        mPrtLayout.addPtrUIHandler(header);
        mPrtLayout.setPullToRefresh(false);
        mPrtLayout.isKeepHeaderWhenRefresh();
        mPrtLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!ENABLE_PULL_REFRESH) {
                    frame.refreshComplete();
                    return;
                }
                if (mRequestHandler.isRequesting()) {
                    frame.refreshComplete();
                    return;
                }
                if (mRequestHandler.isEmpty()) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading),
                            false);
                }
                startRequestCollectData(false);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });
    }

    private void initLoadMoreLayout() {
        mLoadMoreLayout.useDefaultFooter();
        mLoadMoreLayout.setAutoLoadMore(true);
        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                startRequestCollectData(true);
            }
        });
    }

    private void initListView() {
        mListViewAdapter = new CollectListViewAdapter(mContext, mDataList);
        mListViewAdapter.setSelectStateListener(new CollectBaseItemView.ItemSelectStateListener() {
            @Override
            public void onItemSelectStateChanged(boolean selected) {
                if (!selected) {
                    mSelectAllCheckBox.setChecked(false);
                }
            }
        });
        mListViewAdapter.setType(mType);
        mListViewAdapter.setEditMode(mIsEditMode);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleItemClick(mListViewAdapter.getItem(position));
            }
        });
    }

    private void initLoadingView() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                startRequestCollectData(false);
            }
        });
    }

    private void initFootView() {
        mSelectAllCheckBox = (CheckBox) mFootRootView.findViewById(R.id.collect_select_all_checkbox);
        mSelectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestHandler.setSelectAll(mSelectAllCheckBox.isChecked());
                mListViewAdapter.notifyDataSetChanged();
            }
        });

        mFootRootView.findViewById(R.id.collect_select_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = mRequestHandler.getSelectedCount();
                if (count > 0) {
                    showDialogDelete(count);
                }
            }
        });
    }

    private void initNoDataView() {
        int resId = R.string.collect_no_article;
        if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
            resId = R.string.collect_no_article;
        } else if (mType == CommonConst.COLLECT_TYPE_POST) {
            resId = R.string.collect_no_post;
        } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
            resId = R.string.collect_no_product;
        }
        TextView textView = (TextView) mNoDataView.findViewById(R.id.collect_nocollect_text);
        textView.setText(resId);
    }

    public void setType(int type) {
        mType = type;
    }

    public boolean isInEditMode() {
        return mIsEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
        if (!isEditMode) {
            mRequestHandler.setSelectAll(isEditMode);
        }
        if (mListViewAdapter != null) {
            mListViewAdapter.setEditMode(isEditMode);
            mListViewAdapter.notifyDataSetChanged();
        }
        mFootRootView.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    private void handleItemClick(CollectItemInfo info) {
        if (mType == CommonConst.COLLECT_TYPE_ARTICLE) {
            CollectArticleBean.ArticleInfo articleInfo = (CollectArticleBean.ArticleInfo) info.data;

            int type = articleInfo.getType();
            if (type == HomepageConst.FEED_TYPE_KNOWLEDGE) {
                KnowledgeDetailInfo detailInfo = new KnowledgeDetailInfo();
                detailInfo.articleId = StringUtils.parseInt(articleInfo.getId());
                Message msg = Message.obtain();
                msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
                msg.obj = detailInfo;
                MsgDispatcher.getInstance().sendMessage(msg);
            } else {
                HomepageFeedDetailInfo detailInfo = new HomepageFeedDetailInfo();
                detailInfo.articleId = articleInfo.getId();
                detailInfo.type = type;
                Message msg = Message.obtain();
                msg.what = MsgDef.MSG_HOMEPAGE_SHOW_ARTICLE_DETAIL;
                msg.obj = detailInfo;
                MsgDispatcher.getInstance().sendMessage(msg);
            }

        } else if (mType == CommonConst.COLLECT_TYPE_POST) {
            CollectPostBean.PostInfo postInfo = (CollectPostBean.PostInfo) info.data;
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
            message.arg1 = StringUtils.parseInt(postInfo.getId());
            MsgDispatcher.getInstance().sendMessage(message);
        } else if (mType == CommonConst.COLLECT_TYPE_PRODUCT) {
            CollectProductBean.ProductInfo productInfo = (CollectProductBean.ProductInfo) info.data;
            Message msg = Message.obtain();
            msg.what = MsgDef.MSG_SHOW_PRODUCT_DETAIL_WINDOW;
            msg.obj = productInfo.getId();
            MsgDispatcher.getInstance().sendMessage(msg);
        }
    }

    private void handleRequestFailed(int errorType) {
        mPrtLayout.refreshComplete();
        mNoDataView.setVisibility(View.GONE);
        if (errorType == CommonConst.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (mRequestHandler.isEmpty()) {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                    .homepage_network_error_retry), true);
        } else {
            mLoadingLayout.setVisibility(View.GONE);
            if (errorType == CommonConst.ERROR_TYPE_DATA) {
                toastDataError();
            } else {
                toastNetworkError();
            }
            mLoadMoreLayout.loadMoreError(0, ResourceHelper.getString(R.string.homepage_network_error_retry));
        }
    }

    private void handleRequestSuccess(ArrayList allList, ArrayList newList) {
        mPrtLayout.refreshComplete();
        if (allList.size() == 0 && !mRequestHandler.hasMore()) {
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mNoDataView.setVisibility(View.GONE);
        }
        mLoadMoreLayout.loadMoreFinish(false, mRequestHandler.hasMore());
        mLoadingLayout.setVisibility(View.GONE);
        mDataList.clear();
        mDataList.addAll(allList);
        mListViewAdapter.notifyDataSetChanged();
    }

    private CollectListRequest.RequestResultCallback mResultCallback = new CollectListRequest.RequestResultCallback() {
        @Override
        public void onResultFail(int errorType) {
            handleRequestFailed(errorType);
        }

        @Override
        public void onResultSuccess(ArrayList allList, ArrayList newList) {
            handleRequestSuccess(allList, newList);
        }
    };

    private void startRequestCollectData(boolean isLoadMore) {
        mRequestHandler.startRequest(isLoadMore, mResultCallback);
    }


    private void showDialogDelete(int count) {
        String confirmText = ResourceHelper.getString(R.string.collect_delete_confirm_text);
        confirmText = confirmText.replaceAll("#del_count#", String.valueOf(count));
        SimpleTextDialog dialog = new SimpleTextDialog(mContext);
        dialog.setText(confirmText);
        dialog.addYesNoButton();
        dialog.setRecommendButton(GenericDialog.ID_BUTTON_NO);
        dialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    startDeleteCollectRequest();
                }
                return false;
            }
        });
        dialog.show();
    }

    private void startDeleteCollectRequest() {
        final LoadingDialog dialog = new LoadingDialog(mContext);
        dialog.setMessage(R.string.deletting);
        dialog.show();

        mRequestHandler.delSelectedCollects(new CollectListRequest.RequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                dimissProgressDialog(dialog);
                ToastHelper.showToast(mContext, R.string.collect_delete_failt);
            }

            @Override
            public void onResultSuccess(ArrayList<CollectItemInfo> allList, ArrayList<CollectItemInfo> newList) {
                dimissProgressDialog(dialog);
                if (allList.size() == 0 && !mRequestHandler.hasMore()) {
                    mNoDataView.setVisibility(View.VISIBLE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                }
                mLoadMoreLayout.loadMoreFinish(false, mRequestHandler.hasMore());
                mLoadingLayout.setVisibility(View.GONE);
                mDataList.clear();
                mDataList.addAll(allList);
                mListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void dimissProgressDialog(final LoadingDialog dialog) {
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 500);
    }

    private void toastDataError() {
        ToastHelper.showToast(mContext, R.string.homepage_data_error);
    }

    private void toastNetworkError() {
        ToastHelper.showToast(mContext, R.string.homepage_network_error);
    }
}
