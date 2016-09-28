package com.sjy.ttclub.community.widget;

import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.GetPostRuleRequest;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by zhangwulin on 2015/12/30.
 * email 1501448275@qq.com
 */
public class CommunityCommentRuleTipPanel extends BasePanel implements View.OnClickListener{

    private TextView mHaveKnowButton;
    private TextView mReadRuleButton;
    private static final String SETTING_KEY="mIsFirstShowRuleTip";
    private int mCircleId;
    private GetPostRuleRequest mRuleRequest;
    public CommunityCommentRuleTipPanel(Context context,int circleId) {
        super(context);
        this.mCircleId=circleId;
        initView();
        setAnimation(R.style.TipPanelAnim);
    }

    @Override
    protected View onCreateContentView() {
        View view = View.inflate(mContext, R.layout.community_comment_rule_panel, null);
        return view;
    }

    private void initView() {
        mHaveKnowButton = (TextView) mContentView.findViewById(R.id.btn_known);
        mHaveKnowButton.setOnClickListener(this);
        mReadRuleButton = (TextView) mContentView.findViewById(R.id.btn_read_rule);
        mReadRuleButton.setOnClickListener(this);
        mRuleRequest=new GetPostRuleRequest(mContext,mCircleId);
    }

    @Override
    protected FrameLayout.LayoutParams getLayoutParams() {
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.TOP;
        return lp;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_known:
                handlerOnKnowButtonClick();
                break;
            case R.id.btn_read_rule:
                handlerOnReadButtonClick();
                break;
        }
    }

    private void handlerOnKnowButtonClick(){
        SettingFlags.setFlag(SETTING_KEY,2);
        hidePanel();
    }

    private void handlerOnReadButtonClick(){
        tryGetRuleData();
    }

    private void tryGetRuleData(){
        mRuleRequest.startRequest(new GetPostRuleRequest.GetRuleRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                handlerRequestFail(errorType);
            }

            @Override
            public void onResultSuccess(String url) {
                SettingFlags.setFlag(SETTING_KEY,2);
                Message message = Message.obtain();
                WebViewBrowserParams params = new WebViewBrowserParams();
                params.title = ResourceHelper.getString(R.string.community_post_have_to_read);
                params.url = url;
                message.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
                message.obj = params;
                MsgDispatcher.getInstance().sendMessage(message);
                hidePanel();
            }
        });
    }
    private void handlerRequestFail(int errorType){
        if (errorType == CommunityConstant.ERROR_TYPE_REQUESTING) {
            return;
        }
        if (errorType == CommunityConstant.ERROR_TYPE_DATA) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorType == CommunityConstant.ERROR_TYPE_NETWORK) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_network_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorType == CommunityConstant.ERROR_TYPE_CARD_NOT_EXIST) {
            ToastHelper.showToast(mContext, ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            return;
        }
        if (errorType == CommunityConstant.ERROR_TYPE_INVALID_TOKEN) {
            AccountManager.getInstance().tryOpenGuideLoginWindow();
            return;
        }
    }
}
