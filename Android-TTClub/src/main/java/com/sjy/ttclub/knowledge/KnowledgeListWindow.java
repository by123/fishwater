package com.sjy.ttclub.knowledge;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.KnowledgeArticleBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linhz on 2015/12/21.
 * Email: linhaizhong@ta2she.com
 */
public class KnowledgeListWindow extends DefaultWindow {
    private ListView mListView;
    private LoadingLayout mLoadingLayout;
    private KnowledgeAdapter mAdapter;
    private List<KnowledgeArticleBean.ArticleInfo> mDataList;
    private KnowledgeDataHelper mDataHelper;

    private int mType;

    public KnowledgeListWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        initActionBar();
        onCreateContent();
        mDataHelper = new KnowledgeDataHelper(getContext());
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper
                .getDrawable(R.drawable.share_icon_gray));
        item.setItemId(MenuItemIdDef.TITLEBAR_SHARE);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);

    }

    private View onCreateContent() {
        View parentView = View.inflate(getContext(), R.layout.knowledge_list_layout, null);
        initViews(parentView);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        return parentView;
    }

    private void initViews(View parentView) {
        mDataList = new ArrayList<>();
        mLoadingLayout = (LoadingLayout) parentView.findViewById(R.id.knowledge_list_layout_loading);
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mLoadingLayout.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.loading), false);
                tryGetData();
            }
        });
        mListView = (ListView) parentView.findViewById(R.id.knowledge_list_layout_list);
        mAdapter = new KnowledgeAdapter(getContext(), mDataList, KnowledgeConst.TYPE_MAIN);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KnowledgeArticleBean.ArticleInfo info = mAdapter.getItem(position);
                KnowledgeDetailInfo detailInfo = new KnowledgeDetailInfo();
                detailInfo.articleId = info.aid;
                Message msg = Message.obtain();
                msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_DETAILE_WINDOW;
                msg.obj = detailInfo;
                MsgDispatcher.getInstance().sendMessage(msg);

                statsListItemClicked(info.aid);
            }
        });

    }

    public void setupListWindow(KnowledgeDetailInfo info) {
        mType = info.articleId;
        String title;
        if (mType == KnowledgeConst.TYPE_MAN) {
            title = ResourceHelper.getString(R.string.knowledge_men);
        } else if (mType == KnowledgeConst.TYPE_WOMAN) {
            title = ResourceHelper.getString(R.string.knowledge_women);
        } else {
            title = ResourceHelper.getString(R.string.knowledge_men_women);
        }
        setTitle(title);

        tryGetData();
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (itemId == MenuItemIdDef.TITLEBAR_SHARE) {
            handleShare();
        }
    }

    private void tryGetData() {
        mDataHelper.getKnowledgeData(new KnowledgeDataHelper.ResultCallback() {
            @Override
            public void onRequestFailed(int errorType) {
                if (errorType == KnowledgeDataHelper.ERROR_TYPE_DATA) {
                    handleDataError();
                } else {
                    handleNetworkError();
                }
            }

            @Override
            public void onRequestSuccess(KnowledgeArticleBean data) {
                if (data == null) {
                    handleDataError();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    List<List<KnowledgeArticleBean.ArticleInfo>> list = data.getData();
                    if (mType < list.size()) {
                        List<KnowledgeArticleBean.ArticleInfo> dataList = list.get(mType);
                        mDataList.clear();
                        mDataList.addAll(dataList);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    public void handleDataError() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.drawable.no_data_bg, ResourceHelper.getString(R.string.no_data), false);
    }

    public void handleNetworkError() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.network_error_retry),
                true);
    }

    private void handleShare() {
        String shareUrl = BuildConfig.SERVER_URL + "/h5.php?a=knowledge&apiver="+ CommonConst.API_VERSION+"&list=" + mType;
        String shareTitle = getResources().getString(R.string.knowledge_share_title);
        String shareContent = getResources().getString(R.string.knowledge_share_content);
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareUrl(shareUrl);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareTitle(shareTitle);
        builder.setShareContent(shareContent);
        builder.setShareImageUrl(mDataList.get(0).getUrl());
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);

        Message msg = Message.obtain();
        msg.obj = builder.create();
        msg.what = MsgDef.MSG_SHARE;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void statsListItemClicked(int articleId) {
        if (mType == KnowledgeConst.TYPE_MAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_MAN_DETAIL, StatsKeyDef.KNOWLEDGE_MAN_DETAIL, articleId);
        } else if (mType == KnowledgeConst.TYPE_WOMAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_WOMAN_DETAIL, StatsKeyDef.KNOWLEDGE_WOMAN_DETAIL, articleId);
        } else if (mType == KnowledgeConst.TYPE_MAN_WOMAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_MAN_AND_WOMAN_DETAIL, StatsKeyDef
                    .KNOWLEDGE_MAN_AND_WOMAN_DETAIL, articleId);
        }
    }
}
