package com.sjy.ttclub.community.userinfopage;

import android.content.Context;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.IMainTabView;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.TabWindow;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.TabPager;

/**
 * Created by zhangwulin on 2015/12/29.
 * email 1501448275@qq.com
 */
public class CommunityMyPostWindow extends TabWindow implements IMainTabView, TabPager.IScrollable {
    private final static int TYPE_POST = 1;
    private final static int TYPE_ASK = 2;

    private CommunityMyPostListTabView mMyPostView;
    private CommunityMyPostListTabView mMyPostAskView;
    private CommunityMyCommentListTabView mMyCommentView;
    private CommunityMyCommentListTabView mMyCommentAskView;
    private int mType=TYPE_POST;
    public CommunityMyPostWindow(Context context, IDefaultWindowCallBacks callBacks,int type) {
        super(context, callBacks);
        setTitle(ResourceHelper.getString(R.string.account_title_me));
        setTabbarInTitlebar(false);
        setType(type);
    }

    public void setType(int type){
        this.mType=type;
        createTabView();
    }

    @Override
    public void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                break;
            case STATE_BEFORE_POP_OUT:
                break;
        }
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public boolean canScroll(boolean scrollLeft) {
        return false;
    }

    private void createTabView() {
        mMyPostView = new CommunityMyPostListTabView(getContext(), ResourceHelper.getString(R.string.community_my_post_title), TYPE_POST);
        addTab(mMyPostView);

        mMyPostAskView = new CommunityMyPostListTabView(getContext(), ResourceHelper.getString(R.string.community_my_post_question), TYPE_ASK);
        addTab(mMyPostAskView);

        mMyCommentView = new CommunityMyCommentListTabView(getContext(), ResourceHelper.getString(R.string.community_my_comment_title), TYPE_POST);
        addTab(mMyCommentView);

        mMyCommentAskView = new CommunityMyCommentListTabView(getContext(), ResourceHelper.getString(R.string.community_my_comment_answer), TYPE_ASK);
        addTab(mMyCommentAskView);

        if(mType==TYPE_ASK){
            setCurrentTab(1);
        }
    }
}
