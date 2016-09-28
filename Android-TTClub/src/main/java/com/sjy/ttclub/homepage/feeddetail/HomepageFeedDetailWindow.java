package com.sjy.ttclub.homepage.feeddetail;

import android.content.Context;

import com.sjy.ttclub.framework.AbstractWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;

/**
 * Created by linhz on 2015/10/31.
 * Email: linhaizhong@ta2she.com
 */
public class HomepageFeedDetailWindow extends AbstractWindow implements HomepageFeedDetailView.IFeedDetailListener {

    private HomepageFeedDetailView mDetailView;

    private IFeedDetailCallback mFeedDetailCallback;

    private HomepageFeedDetailInfo mDetailInfo;

    private boolean mIsPushIn = false;

    public HomepageFeedDetailWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks, true);
        initWindow();
    }

    private void initWindow() {
        mDetailView = new HomepageFeedDetailView(getContext());
        mDetailView.setDetailViewListener(this);
        getBaseLayer().addView(mDetailView, getBaseLayerLP());
    }


    public void setupDetailWindow(HomepageFeedDetailInfo info) {
        mDetailInfo = info;
        if (mIsPushIn) {
            mDetailView.setupDetailView(mDetailInfo.articleId, mDetailInfo.type, mDetailInfo.childType, mDetailInfo
                    .sourceType);
        }
    }

    public void setFeedDetailCallback(IFeedDetailCallback callback) {
        mFeedDetailCallback = callback;
    }

    @Override
    public void onBackClick() {
        mCallBacks.onWindowExitEvent(this, true);
    }

    @Override
    public void onShareClick() {

    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_SHOW) {
            mDetailView.onResume();
        } else if (stateFlag == STATE_AFTER_PUSH_IN) {
            mIsPushIn = true;
            mDetailView.setupDetailView(mDetailInfo.articleId, mDetailInfo.type, mDetailInfo.childType, mDetailInfo
                    .sourceType);
        }
    }

    public interface IFeedDetailCallback {
        void onCommentsClick();
    }
}
