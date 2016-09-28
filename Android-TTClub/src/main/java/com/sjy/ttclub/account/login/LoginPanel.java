package com.sjy.ttclub.account.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.LoginMedia;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.BasePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 陈嘉伟 on 2015/12/1.
 */
public class LoginPanel extends BasePanel {
    private GridView mGridView;
    private HashMap<LoginMedia, Integer> mDataResource;
    private HashMap<LoginMedia, String> mDataContent;
    private ArrayList<LoginMedia> mEntranceList = new ArrayList<LoginMedia>();
    private LinearLayout mLoginPanel;
    private TextView mNativeLogin;

    private LoginPanelGridViewViewAdapter mAdapter;

    public LoginPanel(Context context) {
        super(context);
        initData();
        initEntrance();
        mAdapter = new LoginPanelGridViewViewAdapter(mContext, mEntranceList);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initEntrance() {
        mEntranceList.add(LoginMedia.QQ);
        mEntranceList.add(LoginMedia.WECHAT);
        mEntranceList.add(LoginMedia.SINA);
    }

    @Override
    protected View onCreateContentView() {
        mLoginPanel = (LinearLayout) View.inflate(mContext, R.layout.account_login_panel_main, null);
        mGridView = (GridView) mLoginPanel.findViewById(R.id.lv_contents);
        mGridView.setSelector(mContext.getResources().getDrawable(R.drawable.share_platform_item_bg));
        mNativeLogin = (TextView) mLoginPanel.findViewById(R.id.tv_native_login);
        mNativeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanel();
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LOGIN_WINDOW);
            }
        });

        mGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        hidePanel();
                        switch (mEntranceList.get(position)) {
                            case QQ:
                                AccountManager.getInstance().loginThirdparty(LoginMedia.QQ);
                                break;
                            case WECHAT:
                                AccountManager.getInstance().loginThirdparty(LoginMedia.WECHAT);
                                break;
                            case SINA:
                                AccountManager.getInstance().loginThirdparty(LoginMedia.SINA);
                                break;
                        }
                    }
                });
        return mLoginPanel;
    }

    public class LoginPanelGridViewViewAdapter extends ArrayAdapter {

        public LoginPanelGridViewViewAdapter(Context context, List<LoginMedia> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LoginMedia key = mEntranceList.get(position);
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.account_login_panel_item, null);
            }
            TextView textView = (TextView) convertView;
            textView.setSingleLine();
            if (key != null) {
                Drawable drawable = mContext.getResources().getDrawable(mDataResource.get(key));
                int right = (int) mContext.getResources().getDimension(R.dimen.space_50);
                int bottom = (int) mContext.getResources().getDimension(R.dimen.space_50);
                drawable.setBounds(0, 0, right, bottom);
                textView.setText(mDataContent.get(key));
                textView.setCompoundDrawables(null, drawable, null, null);
            }
            return convertView;
        }
    }

    public void initData() {
        mDataResource = new HashMap<>();
        mDataContent = new HashMap<>();
        mDataResource.put(LoginMedia.QQ, R.drawable.account_login_qq);
        mDataResource.put(LoginMedia.SINA, R.drawable.account_login_sina);
        mDataResource.put(LoginMedia.WECHAT, R.drawable.account_login_wechat);
        mDataContent.put(LoginMedia.QQ, ResourceHelper.getString(R.string.account_login_panel_qq_title));
        mDataContent.put(LoginMedia.SINA, ResourceHelper.getString(R.string.account_login_panel_sina_title));
        mDataContent.put(LoginMedia.WECHAT, ResourceHelper.getString(R.string.account_login_panel_webchat_title));
    }
}
