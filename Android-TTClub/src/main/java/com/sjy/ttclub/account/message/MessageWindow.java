package com.sjy.ttclub.account.message;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.message.adapter.MessageListAdapter;
import com.sjy.ttclub.account.model.MessageRequestHelper;
import com.sjy.ttclub.account.model.MessageUnreadCountRequestHelper;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.bean.account.MessageUnreadCountBean;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.loadmore.LoadMoreContainer;
import com.sjy.ttclub.loadmore.LoadMoreHandler;
import com.sjy.ttclub.loadmore.LoadMoreListViewContainer;
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.widget.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangqing on 2015/12/25.
 * Email:denggangqing@ta2she.com
 */
public class MessageWindow extends DefaultWindow implements MessageRequestHelper.CallBackOnSuccess, View
        .OnClickListener, AdapterView.OnItemClickListener {
    private ListView mListView;
    private List<MessageDialogs> mLetterList = new ArrayList<MessageDialogs>(); //所有会话
    private List<MessageDialogs> mDialogList;   //被选择的会话
    private MessageListAdapter mListAdapter;
    private LoadMoreListViewContainer mLoadMore;
    private int mPage = 1;
    private MessageRequestHelper mMessageRequestHelper;
    private LinearLayout mEdit;
    private TitleBarActionItem mItem;
    private LoadingLayout mNoConnect;
    private TextView mReplyMeUnreadCount;
    private TextView mAgreeUnreadCount;
    private MessageUnreadCountBean.Data mUnreadCount;

    public MessageWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_message_title);
        View view = View.inflate(getContext(), R.layout.account_message_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED);

        mMessageRequestHelper = new MessageRequestHelper(getContext());
        mMessageRequestHelper.setCallBack(this);

        initActionBar();
        initView(view);
        setNoConnect();
        initData();
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            //登录成功后，需重新获取数据
            initData();
            mMessageRequestHelper.messageListRequest(1, true);
        } else if (notification.id == NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED) {
            setUnreadCount();
        }
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.account_message_list_view);
        mLoadMore = (LoadMoreListViewContainer) view.findViewById(R.id.account_message_load_more);
        mEdit = (LinearLayout) view.findViewById(R.id.account_message_edit);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
        mReplyMeUnreadCount = (TextView) view.findViewById(R.id.account_message_reply_count);
        mAgreeUnreadCount = (TextView) view.findViewById(R.id.account_message_agree_count);
        view.findViewById(R.id.account_message_all_check).setOnClickListener(this);
        view.findViewById(R.id.account_message_delete).setOnClickListener(this);
        view.findViewById(R.id.account_message_reply).setOnClickListener(this);
        view.findViewById(R.id.account_message_agree).setOnClickListener(this);
    }

    public void initData() {
        if (NetworkUtil.isNetworkConnected()) {
            mNoConnect.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.VISIBLE);
        } else {
            mNoConnect.setVisibility(View.VISIBLE);
            mLoadMore.setVisibility(View.GONE);
            return;
        }
        mListAdapter = new MessageListAdapter(getContext(), mLetterList);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        mLoadMore.useDefaultFooter();
        mLoadMore.setAutoLoadMore(true);
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mPage++;
                mMessageRequestHelper.messageListRequest(mPage, false);
            }
        });
    }

    private void setNoConnect() {
        mNoConnect.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string
                .homepage_network_error_retry), true);
        mNoConnect.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mNoConnect.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                initData();
                mMessageRequestHelper.messageListRequest(1, true);
            }
        });
    }

    private void setUnreadCount() {
        mUnreadCount = MessageUnreadCountRequestHelper.getBean();
        if (mUnreadCount == null) {
            return;
        }
        //回复我的
        if (mUnreadCount.replyMeCount != 0) {
            mReplyMeUnreadCount.setVisibility(View.VISIBLE);
            mReplyMeUnreadCount.setText(String.valueOf(mUnreadCount.replyMeCount));
        } else {
            mReplyMeUnreadCount.setVisibility(View.GONE);
        }
        //赞、同问
        if (mUnreadCount.praiseMeCount != 0) {
            mAgreeUnreadCount.setVisibility(View.VISIBLE);
            mAgreeUnreadCount.setText(String.valueOf(mUnreadCount.praiseMeCount));
        } else {
            mAgreeUnreadCount.setVisibility(View.GONE);
        }
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        mItem = new TitleBarActionItem(getContext());
        mItem.setText(ResourceHelper.getString(R.string.account_message_edit));
        actionList.add(mItem);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        if (mLetterList.size() > 0) {
            isShowCheck();
        }
    }

    @Override
    public void onSuccess(List<MessageDialogs> messageLest, boolean haveLoadMore) {
        mLetterList.clear();
        mLetterList.addAll(messageLest);
        if (haveLoadMore) {
            mLoadMore.loadMoreFinish(false, true);
        } else {
            mLoadMore.loadMoreFinish(false, false);
        }
        //如果当前是编辑模式，则显示选择按钮
        if (mEdit.isShown()) {
            setShow(true);
        } else {
            setShow(false);
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessForDelete() {
        mLetterList.removeAll(mDialogList);
        mListAdapter.notifyDataSetChanged();
    }

    private void isShowCheck() {
        setCheck(false);
        if (mEdit.isShown()) {
            setShow(false);
            hiddenEditLayout();
        } else {
            setShow(true);
            showEditLayout();
        }
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * 显示编辑布局
     */
    private void showEditLayout() {
        mItem.setText(ResourceHelper.getString(R.string.account_message_cancel));
        mEdit.startAnimation(AnimotionDao.getTranslateUpVisible());
        mEdit.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏编辑布局
     */
    private void hiddenEditLayout() {
        mItem.setText(ResourceHelper.getString(R.string.account_message_edit));
        mEdit.startAnimation(AnimotionDao.getTranslateDownHidden());
        mEdit.setVisibility(View.GONE);
    }

    /**
     * 遍历所有显示状态
     *
     * @param state
     */
    private void setShow(boolean state) {
        for (MessageDialogs dialogs : mLetterList) {
            dialogs.setShow(state);
        }
    }

    /**
     * 设置所有Check属性（官方消息除外）
     *
     * @param check
     */
    private void setCheck(boolean check) {
        MessageDialogs dialogs = null;
        if (isAllCheck()) {
            check = false;
        }
        for (int i = 1; i < mLetterList.size(); i++) {
            dialogs = mLetterList.get(i);
            dialogs.setCheck(check);
        }
    }

    /**
     * 判断是否全选了
     *
     * @return
     */
    private boolean isAllCheck() {
        if (mEdit.isShown()) {
            MessageDialogs dialogs = null;
            for (int i = 1; i < mLetterList.size(); i++) {
                dialogs = mLetterList.get(i);
                boolean checked = dialogs.isCheck();
                if (!checked) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        Message msg;
        switch (v.getId()) {
            case R.id.account_message_all_check:    //选择全部
                setCheck(true);
                mListAdapter.notifyDataSetChanged();
                break;
            case R.id.account_message_delete:   //删除
                deleteDialog();
                break;
            case R.id.account_message_reply:    //回复我的
                if (mUnreadCount != null) {
                    mUnreadCount.replyMeCount = 0;
                }
                msg = Message.obtain();
                msg.what = MsgDef.MSG_SHOW_REPLY_ME_WINDOW;
                msg.arg1 = Constant.TYPE_REPLY_ME;
                MsgDispatcher.getInstance().sendMessage(msg);
                break;
            case R.id.account_message_agree:    //赞
                if (mUnreadCount != null) {
                    mUnreadCount.praiseMeCount = 0;
                }
                msg = Message.obtain();
                msg.what = MsgDef.MSG_SHOW_REPLY_ME_WINDOW;
                msg.arg1 = Constant.TYPE_SAME;
                MsgDispatcher.getInstance().sendMessage(msg);
                break;
        }
    }

    /**
     * 删除会话
     */
    private void deleteDialog() {
        mDialogList = new ArrayList<MessageDialogs>();
        for (MessageDialogs dialogs : mLetterList) {
            if (dialogs.isCheck()) {
                mDialogList.add(dialogs);
            }
        }
        mMessageRequestHelper.deletePersonalLetter(mDialogList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //如果当前是编辑模式，并且点击的不是官方消息，则不会跳进会话界面
        if (mEdit.isShown() && position != 0) {
            boolean checked = mLetterList.get(position).isCheck();
            CheckBox ck = (CheckBox) view.findViewById(R.id.check);
            ck.setChecked(!checked);
        } else {
            if (position == 0 && mUnreadCount != null) {
                mUnreadCount.officialMsgCount = 0;
            } else {
                mLetterList.get(position).setNotreadCount("0");
            }
            Message msg = Message.obtain();
            LetterChatParamBean paramBean = new LetterChatParamBean();
            paramBean.setLetter(mLetterList.get(position));
            msg.obj = paramBean;
            msg.what = MsgDef.MSG_SHOW_LETTER_CHAT_WINDOW;
            MsgDispatcher.getInstance().sendMessage(msg);
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_UNREAD_COUNT_CHANGED);
            new MessageUnreadCountRequestHelper().getMessageUnreadCount();
        } else if (STATE_AFTER_PUSH_IN == stateFlag) {
            setUnreadCount();
            mMessageRequestHelper.messageListRequest(1, true);
        } else if (STATE_ON_SHOW == stateFlag) {
            setUnreadCount();
            mListAdapter.notifyDataSetChanged();
        }
    }
}
