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
public class KnowledgeMainWindow extends DefaultWindow {
    private ListView mListView;
    private LoadingLayout mLoadingLayout;
    private KnowledgeAdapter mAdapter;
    private List<KnowledgeArticleBean.ArticleInfo> mDataList;
    private KnowledgeDataHelper mDataHelper;

    public KnowledgeMainWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.knowledge_title);
        initActionBar();
        onCreateContent();
        StatsModel.stats(StatsKeyDef.KNOWLEDGE_VIEW);

        mDataHelper = new KnowledgeDataHelper(getContext());

        tryGetData();
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
        initView(parentView);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        return parentView;
    }

    private void initView(View parentView) {
        mDataList = new ArrayList<>();
        initDataList();

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
                msg.what = MsgDef.MSG_SHOW_KNOWLEDGE_LIST_WINDOW;
                msg.obj = detailInfo;
                MsgDispatcher.getInstance().sendMessage(msg);

                statsItemClicked(info.getAid());
            }
        });
    }

    private void initDataList() {
        mDataList.clear();
        KnowledgeArticleBean.ArticleInfo articleInfo = new KnowledgeArticleBean.ArticleInfo();
        articleInfo.aid = KnowledgeConst.TYPE_MAN;
        articleInfo.url = "http://img.ta2she.com/flow/posture/male.jpg";
        mDataList.add(articleInfo);

        articleInfo = new KnowledgeArticleBean.ArticleInfo();
        articleInfo.aid = KnowledgeConst.TYPE_WOMAN;
        articleInfo.url = "http://img.ta2she.com/flow/posture/female.jpg";
        mDataList.add(articleInfo);

        articleInfo = new KnowledgeArticleBean.ArticleInfo();
        articleInfo.aid = KnowledgeConst.TYPE_MAN_WOMAN;
        articleInfo.url = "http://img.ta2she.com/flow/posture/mixed_sex.jpg";
        mDataList.add(articleInfo);
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
        mLoadingLayout.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                tryGetData();
            }
        });
    }

    private void handleShare() {
        String shareUrl = BuildConfig.SERVER_URL + "/h5.php?a=knowledge&apiver="+ CommonConst.API_VERSION;
        String shareTitle = getResources().getString(R.string.knowledge_share_title);
        String shareContent = getResources().getString(R.string.knowledge_share_content);
        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareUrl(shareUrl);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_TEXT);
        builder.setShareTitle(shareTitle);
        builder.setShareContent(shareContent);
        builder.setShareImageUrl("http://img.ta2she.com/flow/posture/male.jpg");
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_LINK);

        Message msg = Message.obtain();
        msg.obj = builder.create();
        msg.what = MsgDef.MSG_SHARE;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void statsItemClicked(int articleId) {
        if (articleId == KnowledgeConst.TYPE_MAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_SEX_MAN);
        } else if (articleId == KnowledgeConst.TYPE_WOMAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_SEX_WOMAN);
        } else if (articleId == KnowledgeConst.TYPE_MAN_WOMAN) {
            StatsModel.stats(StatsKeyDef.KNOWLEDGE_SEX_MAN_WOMAN);
        }
    }
}
