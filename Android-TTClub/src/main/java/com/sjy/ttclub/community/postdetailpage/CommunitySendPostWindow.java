package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.setting.PhotoGridAdapter;
import com.sjy.ttclub.account.widget.NoScrollGridView;
import com.sjy.ttclub.common.RespondCodeHelper;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.bean.community.CommunitySendPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.model.GetPostRuleRequest;
import com.sjy.ttclub.emoji.EmoticonsEditText;
import com.sjy.ttclub.emoji.EmoticonsKeyBoardPopWindow;
import com.sjy.ttclub.emoji.EmoticonsUtils;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.framework.ui.TitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.photopicker.PhotoInfo;
import com.sjy.ttclub.photopicker.PhotoPickerCallback;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.PhotoCacheHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;
import com.sjy.ttclub.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by zhangwulin on 2015/12/28.
 * email 1501448275@qq.com
 */
public class CommunitySendPostWindow extends DefaultWindow implements PhotoGridAdapter.IPhotoAdapterListener {
    private static final int MAX_SIZE = 9;
    private CommunityCircleBean mCircle;
    private EmoticonsEditText mContentEditText;
    private EditText mThemeEditText;
    private NoScrollGridView mImageGridView;
    private AlphaImageView mSendRuleButton;
    private PhotoCacheHelper mCacheHelper;
    private PhotoGridAdapter mAdapter;
    private PhotoGridAdapter.PhotoItem mAddPhotoItem;
    private ArrayList<PhotoGridAdapter.PhotoItem> mPhotoItemList = new ArrayList<>();
    private EmoticonsKeyBoardPopWindow mKeyBoardPopWindow;
    private TitleBar mTitleBar;
    private TitleBarActionItem mSendTitleBarActionItem;
    private static final int ACTION_ITEM_SEND = 0;
    private List<TitleBarActionItem> mTitleBarItems = new ArrayList<>();
    private CommunitySendPostRequest mSendRequest;
    private GetPostRuleRequest mPostRuleRequest;
    private RelativeLayout mThemeEditLayout;
    private FrameLayout mEmojiLayout;
    private ImageView mAnnoyButton;
    private int isAnnoy = CommunityConstant.POST_CONTENT_NOT_ANNOY;

    public CommunitySendPostWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setEnableSwipeGesture(false);
        initTitleBarItem();
        createPhotoCacheHelper();
        createView();
        initKeyBoardPopWindow();
        initRegisterNotify();
        StatsModel.stats(StatsKeyDef.POST_SEND_VIEW);
    }

    public void setCircleInfo(CommunityCircleBean circleInfo) {
        this.mCircle = circleInfo;
        initTitleView();
        initRequest();
    }

    private void createPhotoCacheHelper() {
        mCacheHelper = new PhotoCacheHelper(getContext());
    }

    private void createView() {
        View parentView = View.inflate(getContext(), R.layout.community_send_post_layout, null);
        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        mThemeEditLayout = (RelativeLayout) parentView.findViewById(R.id.post_theme_edit_layout);
        mAnnoyButton = (ImageView) parentView.findViewById(R.id.post_annoy_switch);
        mAnnoyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerOnAnnoyButtonClick();
            }
        });
        mSendRuleButton = (AlphaImageView) parentView.findViewById(R.id.community_post_card_rule);
        mSendRuleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnSendButtonRuleClick();
            }
        });
        mEmojiLayout = (FrameLayout) findViewById(R.id.rl_btn_emoji);
        mEmojiLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnEmojiLayoutClick();
            }
        });
        mContentEditText = (EmoticonsEditText) parentView.findViewById(R.id.community_post_card_content);
        mThemeEditText = (EditText) parentView.findViewById(R.id.community_post_card_theme);
        mImageGridView = (NoScrollGridView) parentView.findViewById(R.id.add_pic_icon);
        //关闭系统表情
        mThemeEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int
                            dEnd) {
                        Matcher emojiMatcher = CommonUtils.emoji.matcher(source);
                        if (emojiMatcher.find()) {
                            return "";
                        }
                        return null;
                    }
                }});
        mContentEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int
                            dEnd) {
                        Matcher emojiMatcher = CommonUtils.emoji.matcher(source);
                        if (emojiMatcher.find()) {
                            return "";
                        }
                        return null;
                    }
                }});
        //内容变化监听
        mThemeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handlerOnThemeTextChange(s);
            }
        });
        mContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handlerOnContentTextChange(s);
            }
        });
        mContentEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!DeviceManager.getInstance().isInputMethodShow()) {
                        mContentEditText.getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        DeviceManager.getInstance().hideInputMethod();
                    }
                }
                return false;
            }
        });
        mContentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String text = String.valueOf(textView.getText() + "\n" + "\n");
                    mContentEditText.setText(text);
                    mContentEditText.setSelection(text.length());
                    return true;
                }
                return false;
            }
        });
        mAddPhotoItem = createAddPhotoItem();
        mPhotoItemList.add(mAddPhotoItem);
        mAdapter = new PhotoGridAdapter(getContext(), mPhotoItemList);
        mAdapter.setCacheHelper(mCacheHelper);
        mAdapter.setAdapterListener(this);
        mImageGridView.setAdapter(mAdapter);
    }

    private void handlerOnAnnoyButtonClick() {
        if (isAnnoy == CommunityConstant.POST_CONTENT_ANNOY) {
            mAnnoyButton.setImageResource(R.drawable.switch_off);
            isAnnoy = CommunityConstant.POST_CONTENT_NOT_ANNOY;
        } else {
            mAnnoyButton.setImageResource(R.drawable.switch_on);
            isAnnoy = CommunityConstant.POST_CONTENT_ANNOY;
        }
    }

    private void initAnnoyButtonState(int isAnnoy) {
        if (isAnnoy == CommunityConstant.POST_CONTENT_ANNOY) {
            mAnnoyButton.setImageResource(R.drawable.switch_on);
        } else {
            mAnnoyButton.setImageResource(R.drawable.switch_off);
        }
    }

    private void initRegisterNotify() {
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().register(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
    }

    private void unRegisterNotify() {
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_SHOW);
        NotificationCenter.getInstance().unregister(this, NotificationDef.N_MESSAGE_SOFTWARE_HIDE);
    }

    private void initRequest() {
        if (mCircle == null) {
            return;
        }
        mSendRequest = new CommunitySendPostRequest(getContext());
        mPostRuleRequest = new GetPostRuleRequest(getContext(), mCircle.getCircleId());
    }

    public void initKeyBoardPopWindow() {
        mKeyBoardPopWindow = new EmoticonsKeyBoardPopWindow(getContext());
        mKeyBoardPopWindow.setBuilder(EmoticonsUtils.getSimpleBuilder(getContext()));
        mKeyBoardPopWindow.setEditText(mContentEditText);
    }

    private void initTitleView() {
        if (mCircle == null) {
            setTitle(ResourceHelper.getString(R.string.community_send_post_title));
        } else {
            setTitle(mCircle.getCircleName());
        }

        if (mCircle.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            initQaSendPostLayout();
        } else {
            initSendPostLayout();
        }
    }

    private void initQaSendPostLayout() {
        if (mThemeEditText == null) {
            return;
        }
        mThemeEditText.setHint(ResourceHelper.getString(R.string.community_post_edit_hint_theme_qa));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mThemeEditLayout.getLayoutParams();
        layoutParams.height = ResourceHelper.getDimen(R.dimen.space_100);
        mThemeEditLayout.setLayoutParams(layoutParams);
        if (mContentEditText == null) {
            return;
        }
        mContentEditText.setHint(ResourceHelper.getString(R.string.community_post_qa_edit_hint_content));
    }

    private void initSendPostLayout() {
        if (mThemeEditText == null) {
            return;
        }
        if(mCircle.getContentType()==CommunityConstant.POST_CONTENT_TYPE_TEXT) {
            mThemeEditText.setHint(ResourceHelper.getString(R.string.community_post_edit_hint_theme));
        }else {
            mThemeEditText.setHint(ResourceHelper.getString(R.string.community_post_edit_hint_theme_select));
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mThemeEditLayout.getLayoutParams();
        layoutParams.height = ResourceHelper.getDimen(R.dimen.space_50);
        mThemeEditLayout.setLayoutParams(layoutParams);
        if (mContentEditText == null) {
            return;
        }
        mContentEditText.setHint(ResourceHelper.getString(R.string.community_post_edit_hint_content));
    }

    private void initTitleBarItem() {
        mTitleBar = (TitleBar) getTitleBar();
        mSendTitleBarActionItem = new TitleBarActionItem(getContext());
        mSendTitleBarActionItem.setEnabled(true);
        mSendTitleBarActionItem.setItemId(ACTION_ITEM_SEND);
        mSendTitleBarActionItem.setText(ResourceHelper.getString(R.string.community_send_button));
        mSendTitleBarActionItem.getTextView().setTextColor(ResourceHelper.getColor(R.color.community_llgray));
        mTitleBarItems.add(mSendTitleBarActionItem);
        mTitleBar.setActionItems(mTitleBarItems);
    }

    private void handlerOnThemeTextChange(Editable s) {
        int nSelStart = 0;
        int nSelEnd = 0;
        boolean nOverMaxLength = false;
        nSelStart = mThemeEditText.getSelectionStart();
        nSelEnd = mThemeEditText.getSelectionEnd();
        nOverMaxLength = (s.length() > 30) ? true : false;
        if (nOverMaxLength) {
            ToastHelper.showToast(getContext(), "已达最大字数", Toast.LENGTH_SHORT);
            s.delete(nSelStart - 1, nSelEnd);
            mThemeEditText.setTextKeepState(s);
        }
        if (s.toString().length() > 0) {
            mSendTitleBarActionItem.setTextColor(ResourceHelper.getColor(R.color.community_pink));
        } else {
            mSendTitleBarActionItem.setTextColor(ResourceHelper.getColor(R.color.llgray));
        }
    }

    private void handlerOnContentTextChange(Editable s) {
        if (s.toString().length() > 0) {
            mSendTitleBarActionItem.setTextColor(ResourceHelper.getColor(R.color.community_pink));
        } else {
            mSendTitleBarActionItem.setTextColor(ResourceHelper.getColor(R.color.llgray));
        }
    }

    private void handlerOnEmojiLayoutClick() {
        if (!mThemeEditText.hasFocus()) {
            if (mKeyBoardPopWindow != null && !mKeyBoardPopWindow.isShowing()) {
                mKeyBoardPopWindow.showPopupWindow();
                DeviceManager.getInstance().hideInputMethod();
            }
        } else {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_limit_emoji), Toast.LENGTH_SHORT);
        }
    }

    private void handlerOnSendButtonRuleClick() {
        tryGetSendRuleUrl();
    }

    private void tryGetSendRuleUrl() {
        if (mPostRuleRequest == null) {
            return;
        }
        mPostRuleRequest.startRequest(new GetPostRuleRequest.GetRuleRequestResultCallback() {
            @Override
            public void onResultFail(int errorType) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.homepage_data_error), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResultSuccess(String url) {
                Message message = Message.obtain();
                WebViewBrowserParams params = new WebViewBrowserParams();
                params.title = ResourceHelper.getString(R.string.community_post_have_to_read);
                params.url = url;
                message.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
                message.obj = params;
                MsgDispatcher.getInstance().sendMessage(message);
            }
        });
    }

    @Override
    public boolean onWindowBackKeyEvent() {
        handleExitPageToToastSaveTip();
        return true;
    }

    @Override
    public void onBackActionButtonClick() {
        handleExitPageToToastSaveTip();
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        if (itemId == ACTION_ITEM_SEND) {
            handlerSendPostEvent();
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        switch (stateFlag) {
            case STATE_AFTER_PUSH_IN:
                tryGetPostDraftFromLocal();
                break;
            case STATE_BEFORE_POP_OUT:
                unRegisterNotify();
                break;
        }
    }

    @Override
    public void notify(Notification notification) {
        super.notify(notification);
        if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_SHOW) {
        } else if (notification.id == NotificationDef.N_MESSAGE_SOFTWARE_HIDE) {
        }
    }

    private void handlerSendPostEvent() {
        //统计post_send_release
        StatsModel.stats(StatsKeyDef.POST_SEND_RELEASE);
        if (mKeyBoardPopWindow != null && mKeyBoardPopWindow.isShowing()) {
            DeviceManager.getInstance().hideInputMethod();
            mKeyBoardPopWindow.closePopupWindow();
        }
        if (mCircle.getCircleType() == CommunityConstant.CIRCLE_TYPE_QA_POST) {
            handlerOnQaPostSend();
            return;
        }
        if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_TEXT) {
            handlerOnTextPostSend();
        } else if (mCircle.getContentType() == CommunityConstant.POST_CONTENT_TYPE_IMAGE) {
            handlerOnImagePostSend();
        }
    }

    /**
     * 问答发问
     */
    private void handlerOnQaPostSend() {
        if (mThemeEditText.getText().toString().length() == 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_no_theme_tip), Toast.LENGTH_SHORT);
            return;
        }
        if (mContentEditText.getText().toString().length() > CommunityConstant.POST_CONTENT_MAX_LENGTH) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_content_to_long_tip), Toast.LENGTH_SHORT);
            return;
        }
        handlerSendPostRequest();
    }

    /**
     * 以文为主发帖
     */
    private void handlerOnTextPostSend() {
        if (mThemeEditText.getText().length() == 0) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_no_theme_tip), Toast.LENGTH_SHORT);
            return;
        }
        if (mContentEditText.getText().toString().length() > CommunityConstant.POST_CONTENT_MAX_LENGTH) {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_content_to_long_tip), Toast.LENGTH_SHORT);
            return;
        }
        handlerSendPostRequest();
    }

    /**
     * 以图为主发帖
     */
    private void handlerOnImagePostSend() {
        if (mPhotoItemList.size() > 0 && mPhotoItemList.get(0).type != PhotoGridAdapter.PhotoItem.TYPE_ADD) {
            handlerSendPostRequest();
        } else {
            ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string
                    .community_post_no_image_tip), Toast.LENGTH_SHORT);
        }
    }

    private CommunitySendPostBean createSendPostBean() {
        CommunitySendPostBean postBean = new CommunitySendPostBean();
        postBean.setCirlceId(mCircle.getCircleId());
        postBean.setIsAnony(isAnnoy);
        postBean.setTopicId(0);
        postBean.setTheme(mThemeEditText.getText().toString());
        if (postBean.getTheme().length() > 0) {
            StatsModel.stats(StatsKeyDef.POST_SEND_TITLE);
        }
        postBean.setContent(mContentEditText.getText().toString());
        if (postBean.getContent().length() > 0) {
            StatsModel.stats(StatsKeyDef.POST_SEND_NOTE);
        }
        postBean.setPhotoItems(mPhotoItemList);
        if (postBean.getPhotoItems().size() > 0) {
            if (postBean.getPhotoItems().get(0).type != PhotoGridAdapter.PhotoItem.TYPE_ADD) {
                StatsModel.stats(StatsKeyDef.POST_SEND_IMAGE);
            }
        }
        return postBean;
    }

    private void handlerSendPostRequest() {
        mSendRequest.startRequest(createSendPostBean(), new CommunitySendPostRequest.SendPostRequestCallback() {
            @Override
            public void onResultFail(int errorCode) {
                handlerOnSendPostFail(errorCode);
            }

            @Override
            public void onResultSuccess(BaseBean baseBean) {
                handlerOnSendPostSuccess();
            }
        });
    }

    private void handlerOnSendPostFail(int errorCode) {
        BitmapUtil.deleteTempImages();
        RespondCodeHelper.handlerResultStatus(getContext(), errorCode);
    }

    private void handlerOnSendPostSuccess() {
        //统计发帖成功
        StatsModel.stats(StatsKeyDef.GROUP_DELIEVER_POST);

        BitmapUtil.deleteTempImages();
        mThemeEditText.setText("");
        mContentEditText.setText("");
        mPhotoItemList.clear();
        SettingFlags.setFlag("isSaveDraft", false);
        DeviceManager.getInstance().hideInputMethod();
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
        handlerCloseCurrentWindow();
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                Message message=Message.obtain();
                message.what=MsgDef.MSG_SHOW_COMMUNITY_MY_POST_WINDOW;
                message.arg1=mCircle.getCircleType();
                MsgDispatcher.getInstance().sendMessage(message);
            }
        }, 250);
    }

    private void handleExitPageToToastSaveTip() {
        //统计post_send_cancel
        StatsModel.stats(StatsKeyDef.POST_SEND_CANCEL);
        if (mThemeEditText.getText().toString().length() > 0 || mContentEditText.getText().toString().length() > 0 || isHavePhotos()) {
            final SimpleTextDialog simpleTextDialog = new SimpleTextDialog(getContext());
            simpleTextDialog.setText(ResourceHelper.getString(R.string.community_is_save_send_draft));
            simpleTextDialog.addYesNoButton(ResourceHelper.getString(R.string.save), ResourceHelper.getString(R
                    .string.cancel));
            simpleTextDialog.setOnClickListener(new IDialogOnClickListener() {
                @Override
                public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                    if (viewId == GenericDialog.ID_BUTTON_YES) {
                        handlerOnYesButtonClick();
                    } else if (viewId == GenericDialog.ID_BUTTON_NO) {
                        handlerOnCancelButtonClick();
                    }
                    return false;
                }
            });
            simpleTextDialog.setRecommendButton(GenericDialog.ID_BUTTON_YES);
            simpleTextDialog.setCanceledOnTouchOutside(false);
            simpleTextDialog.show();
        } else {
            SettingFlags.setFlag("isSaveDraft", false);
            handlerCloseCurrentWindow();
        }
    }

    private boolean isHavePhotos() {
        boolean isHavePhotos = false;
        if (mPhotoItemList.size() > 0) {
            if (mPhotoItemList.get(0).type == PhotoGridAdapter.PhotoItem.TYPE_ADD) {
                isHavePhotos = false;
            } else {
                isHavePhotos = true;
            }
        }
        return isHavePhotos;
    }

    private void handlerOnCancelButtonClick() {
        SettingFlags.setFlag("isSaveDraft", false);
        handlerCloseCurrentWindow();
    }

    private void handlerOnYesButtonClick() {
        trySaveCurrentPostDraft();
    }

    private void trySaveCurrentPostDraft() {
        for (int i = 0; i < mPhotoItemList.size(); i++) {
            if (mPhotoItemList.get(i).type == PhotoGridAdapter.PhotoItem.TYPE_ADD) {
                mPhotoItemList.remove(i);
            }
        }
        String content = mContentEditText.getText().toString();
        String theme = mThemeEditText.getText().toString();
        String urlStr = "";
        if (mPhotoItemList.size() > 0) {
            for (int i = 0; i < mPhotoItemList.size(); i++) {
                if (i == 0) {
                    urlStr = mPhotoItemList.get(i).path;
                } else {
                    urlStr += "<>" + mPhotoItemList.get(i).path;
                }
            }
        } else {
            content = mContentEditText.getText().toString();
        }
        SettingFlags.setFlag("isSaveDraft", true);
        SettingFlags.setFlag("isAnnoy", isAnnoy);
        SettingFlags.setFlag("theme", theme);
        SettingFlags.setFlag("content", content);
        SettingFlags.setFlag("imagesUrl", urlStr);
        SettingFlags.setFlag("circleType", mCircle.getCircleType());
        ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.community_have_save_draft), Toast.LENGTH_SHORT);
        handlerCloseCurrentWindow();
    }

    private void tryGetPostDraftFromLocal() {
        if (!SettingFlags.getBooleanFlag("isSaveDraft", false)) {
            return;
        }
        if (mPhotoItemList == null) {
            return;
        }
        if (mAdapter == null) {
            return;
        }
        CommunitySendPostBean draft = new CommunitySendPostBean();
        draft.setCircleType(SettingFlags.getIntFlag("circleType", CommunityConstant.CIRCLE_TYPE_POST));
        draft.setTheme(SettingFlags.getStringFlag("theme", ""));
        draft.setContent(SettingFlags.getStringFlag("content", ""));
        draft.setIsAnony(SettingFlags.getIntFlag("isAnnoy", CommunityConstant.POST_CONTENT_NOT_ANNOY));
        String[] urls;
        urls = StringUtils.split(SettingFlags.getStringFlag("imagesUrl", ""), "<>");
        if (draft.getCircleType() != mCircle.getCircleType()) {
            return;
        }
        mPhotoItemList.clear();
        PhotoGridAdapter.PhotoItem item;
        for (String urlStr : urls) {
            item = new PhotoGridAdapter.PhotoItem();
            item.type = PhotoGridAdapter.PhotoItem.TYPE_GALLERY;
            item.path = urlStr;
            mPhotoItemList.add(item);
        }
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
        if (mContentEditText != null) {
            mContentEditText.setText(draft.getContent());
        }
        if (mThemeEditText != null) {
            mThemeEditText.setText(draft.getTheme());
        }
        initAnnoyButtonState(draft.getIsAnony());
    }

    private PhotoGridAdapter.PhotoItem createAddPhotoItem() {
        PhotoGridAdapter.PhotoItem item = new PhotoGridAdapter.PhotoItem();
        item.type = PhotoGridAdapter.PhotoItem.TYPE_ADD;
        return item;
    }

    private void addAddPhotoItem() {
        if (mPhotoItemList.size() < MAX_SIZE) {
            if (!mPhotoItemList.contains(mAddPhotoItem)) {
                mPhotoItemList.add(mAddPhotoItem);
            }
        }
    }

    private void handleAddClick() {
        PhotoPickerCallback callback = new PhotoPickerCallback() {
            @Override
            public void onPhotoPickResult(ArrayList<PhotoInfo> resultList) {
                handlePhotoPickFinished(resultList);
            }

            @Override
            public int getMaxPhotoNum() {
                return MAX_SIZE;
            }

            @Override
            public ArrayList<String> getPickedPhotos() {
                ArrayList<String> pickedList = new ArrayList<String>();
                for (PhotoGridAdapter.PhotoItem item : mPhotoItemList) {
                    if (item.type == PhotoGridAdapter.PhotoItem.TYPE_GALLERY) {
                        pickedList.add(item.path);
                    }
                }
                return pickedList;
            }
        };
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PHOTO_PICKER_WINDOW;
        msg.obj = callback;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void handlePhotoPickFinished(ArrayList<PhotoInfo> resultList) {
        mPhotoItemList.clear();
        if (resultList == null || resultList.isEmpty()) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        PhotoGridAdapter.PhotoItem item;
        for (PhotoInfo info : resultList) {
            item = new PhotoGridAdapter.PhotoItem();
            item.type = PhotoGridAdapter.PhotoItem.TYPE_GALLERY;
            item.path = info.path;
            mPhotoItemList.add(item);
        }
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
    }


    private void handlePhotoClick(PhotoGridAdapter.PhotoItem item) {
        int position = mPhotoItemList.indexOf(item);
        if (position < 0) {
            position = 0;
        }
        ArrayList<String> pathList = new ArrayList<>(mPhotoItemList.size());
        for (PhotoGridAdapter.PhotoItem temp : mPhotoItemList) {
            if (temp.type != PhotoGridAdapter.PhotoItem.TYPE_ADD) {
                pathList.add(temp.path);
            }
        }
        PhotoPreviewInfo info = new PhotoPreviewInfo();
        info.photoList = pathList;
        info.position = position;

        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW;
        msg.obj = info;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    @Override
    public void onPhotoClicked(PhotoGridAdapter.PhotoItem item) {
        if (item.type == PhotoGridAdapter.PhotoItem.TYPE_ADD) {
            handleAddClick();
        } else {
            handlePhotoClick(item);
        }
    }

    @Override
    public void onPhotoDelete(PhotoGridAdapter.PhotoItem item) {
        boolean success = mPhotoItemList.remove(item);
        if (!success) {
            for (PhotoGridAdapter.PhotoItem temp : mPhotoItemList) {
                if (item.path.equals(temp.path)) {
                    mPhotoItemList.remove(temp);
                    break;
                }
            }
        }
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
    }

    private void handlerCloseCurrentWindow() {
        mCacheHelper.clearCache();
        if (mKeyBoardPopWindow != null && mKeyBoardPopWindow.isShowing()) {
            mKeyBoardPopWindow.closePopupWindow();
        }
        if (DeviceManager.getInstance().isInputMethodShow()) {
            DeviceManager.getInstance().hideInputMethod();
        }
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                closeWindow();
            }
        }, 200);
    }

    private void closeWindow() {
        mCallBacks.onWindowExitEvent(this, true);
    }
}
