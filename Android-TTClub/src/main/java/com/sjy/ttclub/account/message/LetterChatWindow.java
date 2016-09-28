package com.sjy.ttclub.account.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.message.adapter.MessageChatDetailsAdapter;
import com.sjy.ttclub.account.model.MessageChatDetailsRequestHelper;
import com.sjy.ttclub.bean.account.LetterChatParamBean;
import com.sjy.ttclub.bean.account.MessageDialogDetails;
import com.sjy.ttclub.bean.account.MessageDialogs;
import com.sjy.ttclub.emoji.EmotionKeyBoard;
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
import com.sjy.ttclub.util.NetworkUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.AlphaTextView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.TTRefreshHeader;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by gangqing on 2015/12/28.
 * Email:denggangqing@ta2she.com
 */
public class LetterChatWindow extends DefaultWindow implements View.OnClickListener, MessageChatDetailsRequestHelper.ChatCallBack {
    private static final int TYPE_LETTER_FIRST = 1;
    private static final int TYPE_LETTER_OLD = 2;
    private static final int FIRST_REQUEST = 0;
    private static final int LOAD_MORE = 1;
    private static final int LOAD_SEND = 2;
    private static final int TYPE_ORDER = 12;
    private static final String ITEM_ID = "0";
    private MessageDialogs mDialogs;
    private TitleBarActionItem mItem;
    private String mUserRoleId;
    private String mUserId;
    private LetterChatParamBean mParamBean;
    private LinearLayout mTa2SheSecretaryGood;
    private TextView mTa2SheSecretaryGoodTitle, mTa2SheSecretaryGoodPrice;
    private SimpleDraweeView mTa2SheSecretaryGoodImage;
    private ListView mListView;
    private List<MessageDialogDetails.LetterObj.Letters> mDetailsList = new ArrayList<MessageDialogDetails.LetterObj.Letters>();
    private MessageChatDetailsAdapter mChatAdapter;
    private MessageChatDetailsRequestHelper mRequestHelper;
    private PtrClassicFrameLayout mRefresh;
    private boolean mIsRefreshing = true;
    private LoadingLayout mNoConnect;
    private EmotionKeyBoard mEmotionKeyBoard;
    private AlphaTextView mGoodSend;

    public LetterChatWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setLaunchMode(LAUNCH_MODE_SINGLE_INSTANCE);
        View view = View.inflate(getContext(), R.layout.account_message_letter_chat_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_LOGIN_SUCCESS);
        initActionBar();
        initView(view);
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_ACCOUNT_LOGIN_SUCCESS) {
            //登录成功后，需重新获取数据
            initData();
            request();
        }
    }

    private void initView(View view) {
        mTa2SheSecretaryGood = (LinearLayout) view.findViewById(R.id.ta_she_secretary_good);
        mTa2SheSecretaryGoodImage = (SimpleDraweeView) view.findViewById(R.id.ta_she_secretary_good_image);
        mTa2SheSecretaryGoodTitle = (TextView) view.findViewById(R.id.ta_she_secretary_good_title);
        mTa2SheSecretaryGoodPrice = (TextView) view.findViewById(R.id.ta_she_secretary_good_price);
        mListView = (ListView) view.findViewById(R.id.account_message_chat_details);
        mRefresh = (PtrClassicFrameLayout) view.findViewById(R.id.account_message_chat_refresh);
        mNoConnect = (LoadingLayout) view.findViewById(R.id.account_loading_layout);
        mEmotionKeyBoard = (EmotionKeyBoard) view.findViewById(R.id.emoji_keyboard);
        mGoodSend = (AlphaTextView) view.findViewById(R.id.ta_she_secretary_good_send);
        mGoodSend.setOnClickListener(this);
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

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        mItem = new TitleBarActionItem(getContext());
        Drawable drawable = ResourceHelper.getDrawable(R.drawable.account_message_chat_details_more);
        mItem.setDrawable(drawable);
        actionList.add(mItem);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        chooseHeadImage();
    }

    private void chooseHeadImage() {
        ActionSheetPanel panel = new ActionSheetPanel(getContext());

        ActionSheetPanel.ActionSheetItem item = new ActionSheetPanel.ActionSheetItem();
        item.id = ITEM_ID;
        if (Constant.CANCEL_BLACK.equals(mDialogs.getPullBlackFlag())) {//当前是没拉黑
            item.title = ResourceHelper.getString(R.string.account_message_black);
        } else {  //当前是拉黑
            item.title = ResourceHelper.getString(R.string.account_message_cancel_black);
        }
        panel.addSheetItem(item);

        panel.setSheetItemClickListener(new ActionSheetPanel.OnActionSheetClickListener() {
            @Override
            public void onActionSheetItemClick(String id) {
                if (ITEM_ID.equals(id)) {
                    if (Constant.CANCEL_BLACK.equals(mDialogs.getPullBlackFlag())) {
                        mRequestHelper.shieldingRequest(mDialogs.getUserId(), Constant.BLACK, null);
                        mDialogs.setPullBlackFlag(Constant.BLACK);
                    } else {
                        mRequestHelper.shieldingRequest(mDialogs.getUserId(), Constant.CANCEL_BLACK, null);
                        mDialogs.setPullBlackFlag(Constant.CANCEL_BLACK);
                    }
                }
            }
        });

        panel.showPanel();
    }

    public void setData(LetterChatParamBean paramBean) {
        mParamBean = paramBean;
        mDialogs = paramBean.getMessageDialogs();
        if (mDialogs == null) {
            return;
        }
        mUserRoleId = mDialogs.getUserRoleId();
        mUserId = mDialogs.getUserId();
        setNoConnect();
        mRequestHelper = new MessageChatDetailsRequestHelper(getContext());
        mRequestHelper.setCallBack(this);
        initData();
    }

    private void setNoConnect() {
        mNoConnect.setBgContent(R.drawable.no_network, ResourceHelper.getString(R.string.homepage_network_error_retry), true);
        mNoConnect.setBtnOnClickListener(new LoadingLayout.OnBtnClickListener() {
            @Override
            public void OnClick() {
                mNoConnect.setBgContent(R.anim.loading, ResourceHelper.getString(R.string.homepage_loading), false);
                initData();
                request();
            }
        });
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
        initEmojiKeyBord();
        //设置title
        String title = mDialogs.getNickname();
        if (StringUtils.isEmpty(title)) {
            setTitle(R.string.account_message_chat_default_title);
        } else {
            setTitle(title);
        }
        setPublicPattern();
        //判断对话对象
        if (Constant.TA_SHE_SECRETARY.equals(mUserRoleId)) {  //她他小秘
            setTaSheSecretary();
        } else if (!TextUtils.isEmpty(mUserId)) {  //普通私信
            setNormalLetter();
        } else {  //官方消息
            mEmotionKeyBoard.setVisibility(View.GONE);
            setOfficialNews();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            request();
        } else if (stateFlag == STATE_AFTER_POP_OUT || stateFlag == STATE_ON_HIDE) {
            DeviceManager.getInstance().hideInputMethod();
        }
    }

    private void request() {
        //判断对话对象
        if (Constant.TA_SHE_SECRETARY.equals(mUserRoleId)) {  //她他小秘
            mRequestHelper.personalChatDetailsRequest(mDialogs.getDialogId(), TYPE_LETTER_FIRST, FIRST_REQUEST);
        } else if (!TextUtils.isEmpty(mUserId)) {  //普通私信
            mRequestHelper.personalChatDetailsRequest(mDialogs.getDialogId(), TYPE_LETTER_FIRST, FIRST_REQUEST);
        } else {  //官方消息
            mRequestHelper.officialNewsDetailsRequest();
        }
    }

    /**
     * 设置她他小秘的样式
     */
    private void setTaSheSecretary() {
        mItem.setDrawable(null);
        String imageUrl = mParamBean.getImageUrl();
        String goodTitle = mParamBean.getTitle();
        String price = mParamBean.getPrice();
        if (StringUtils.isEmpty(imageUrl) || StringUtils.isEmpty(goodTitle) || StringUtils.isEmpty(price)) {
            mTa2SheSecretaryGood.setVisibility(View.GONE);
        } else {
            mTa2SheSecretaryGood.setVisibility(View.VISIBLE);
            mTa2SheSecretaryGoodImage.setImageURI(Uri.parse(imageUrl));
            mTa2SheSecretaryGoodTitle.setText(goodTitle);
            mTa2SheSecretaryGoodPrice.setText("￥" + price);
            if (mParamBean.getType() == TYPE_ORDER) {
                mGoodSend.setText(ResourceHelper.getString(R.string.account_message_send_order));
            }
        }
    }

    /**
     * 设置普通私信的样式
     */
    private void setNormalLetter() {
        mTa2SheSecretaryGood.setVisibility(View.GONE);
    }

    /**
     * 设置官方消息的样式
     */
    private void setOfficialNews() {
        mItem.setDrawable(null);
        mTa2SheSecretaryGood.setVisibility(View.GONE);
    }

    /**
     * 设置公共样式
     */
    private void setPublicPattern() {
        mChatAdapter = new MessageChatDetailsAdapter(getContext(), mDetailsList);
        mListView.setAdapter(mChatAdapter);

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
                    if (Constant.TA_SHE_SECRETARY.equals(mUserRoleId) || !TextUtils.isEmpty(mUserId)) {
                        mRequestHelper.personalChatDetailsRequest(mDialogs.getDialogId(), TYPE_LETTER_OLD, LOAD_MORE);
                    } else {
                        mRequestHelper.officialNewsDetailsRequest();
                    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_she_secretary_good_send:   //商品发送
                if (mParamBean.getType() == TYPE_ORDER) {  //订单详情
                    sendContent(getOrderContent());
                    mTa2SheSecretaryGood.setVisibility(View.GONE);
                } else {  //商品详情
                    mRequestHelper.send(mDialogs.getDialogId(), mDialogs.getUserId(), mParamBean.getTitle(), Constant.EXTERNAL_GOOD, "查看详情", mParamBean.getSpId());
                    mTa2SheSecretaryGood.setVisibility(View.GONE);
                }
                break;
        }
    }

    private String getOrderContent() {
        StringBuffer content = new StringBuffer();
        content.append(ResourceHelper.getString(R.string.account_message_order_id));
        content.append(mParamBean.getSpId() + "\n");
        content.append(" ");
        content.append(ResourceHelper.getString(R.string.account_message_order_price));
        content.append(mParamBean.getPrice() + "\n");
        content.append(" ");
        content.append(ResourceHelper.getString(R.string.account_message_order_time));
        content.append(mParamBean.getTime());
        return String.valueOf(content);
    }

    @Override
    public void onSuccess(List<MessageDialogDetails.LetterObj.Letters> letters, boolean isHaveMore, int requestType) {
        switch (requestType) {
            case FIRST_REQUEST:
                mDetailsList.addAll(0, letters);
                break;
            case LOAD_MORE:
                mDetailsList.addAll(0, letters);
                break;
            case LOAD_SEND:
                mDetailsList.addAll(letters);
                break;
        }
        mIsRefreshing = isHaveMore;
        mChatAdapter.notifyDataSetChanged();
        switch (requestType) {
            case FIRST_REQUEST:
                mListView.setSelection(mDetailsList.size() - 1);
                break;
            case LOAD_MORE:
                mListView.setSelection(0);
                break;
            case LOAD_SEND:
                mListView.setSelection(mDetailsList.size() - 1);
                break;
        }
    }

    @Override
    public void onSuccessForSend() {
        mRequestHelper.personalChatDetailsRequest(mDialogs.getDialogId(), TYPE_LETTER_FIRST, LOAD_SEND);
    }

    @Override
    public void onSuccessForBlack() {
        if (Constant.CANCEL_BLACK.equals(mDialogs.getPullBlackFlag())) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.account_personal_blacklist_cancel), Toast.LENGTH_SHORT);
        } else {
            ToastHelper.showToast(getContext(), R.string.account_message_blacked, Toast.LENGTH_SHORT);
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_MESSAGE_HAVE_BLACK_CHANGE);
            mCallBacks.onWindowExitEvent(this, true);
        }
    }

    private void handlerPostChatButtonOnClick() {
        String content = mEmotionKeyBoard.getEmotionEditContent().toString();
        if (StringUtils.isEmpty(content)) {
            ToastHelper.showToast(getContext(), R.string.community_post_no_content_tip);
            return;
        }
        sendContent(content);
    }

    private void sendContent(String content) {
        mRequestHelper.send(mDialogs.getDialogId(), mDialogs.getUserId(), content, null, null, null);
        mEmotionKeyBoard.setEmotionEditContent(null);
    }
}
