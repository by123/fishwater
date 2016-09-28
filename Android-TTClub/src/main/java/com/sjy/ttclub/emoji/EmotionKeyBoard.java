package com.sjy.ttclub.emoji;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.widget.AlphaTextView;

import java.util.regex.Matcher;

/**
 * Created by Administrator on 2015/11/30.
 */
public class EmotionKeyBoard extends LinearLayout implements IEmoticonsKeyboard, EmoticonsToolBarView
        .OnToolBarItemClickListener {

    private EmoticonsEditText mEmoticonsEditText;
    private ImageView mEmotionButton;
    private AlphaTextView mPostButton;
    private LinearLayout mEmotionLayout;
    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private EmoticonsToolBarView mEmoticonsToolBarView;
    private boolean isShowSoftKeyBord = false;
    private boolean isShowEmojiKeyBord = false;
    private int mAutoViewHeight;
    private int mChildViewPosition = -1;
    private OnPostButtonOnClickListener mListener;

    public EmotionKeyBoard(Context context) {
        this(context, null);
    }

    public EmotionKeyBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionKeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.emoji_emotion_keyboard_layout, null);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(parentView, lp);
        mPostButton = (AlphaTextView) parentView.findViewById(R.id.btn_post);
        mEmotionButton = (ImageView) parentView.findViewById(R.id.btn_emotion_select);
        mEmoticonsEditText = (EmoticonsEditText) parentView.findViewById(R.id.content_edit);
        mEmoticonsPageView = (EmoticonsPageView) parentView.findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) parentView.findViewById(R.id.view_eiv);
        mEmoticonsToolBarView = (EmoticonsToolBarView) parentView.findViewById(R.id.view_etv);
        mEmotionLayout = (LinearLayout) parentView.findViewById(R.id.ly_foot_func);
        setAutoViewHeight(mEmotionLayout, Utils.dip2px(getContext(), 285));
        this.setBuilder(EmoticonsUtils.getSimpleBuilder(getContext()));
        setEmotionLayoutListener();
        initState();
        mEmoticonsEditText.cancelLongPress();
        mEmoticonsEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start,
                                               int end, Spanned dest, int dstart, int dend) {
                        Matcher emojiMatcher = CommonUtils.emoji.matcher(source);
                        if (emojiMatcher.find()) {
                            return "";
                        }
                        return null;
                    }
                }});
        mEmoticonsEditText.setOnTextChangedInterface(new EmoticonsEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0, int start, int lengthBefore, int after) {
                if (arg0.length() > 0) {
                    mPostButton.setTextColor(getResources().getColor(R.color.title_color));
                } else {
                    mPostButton.setTextColor(getResources().getColor(R.color.llgray));
                }
            }
        });
        mEmoticonsEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handerEmotionEditTextOnTouchListener();
                return false;
            }
        });
        mPostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerPostButtonOnClick();
            }
        });
        mEmotionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerEmotionButtonOnClick();
            }
        });
    }

    private void initState() {
        isShowSoftKeyBord = false;
        isShowEmojiKeyBord = false;
    }

    private void setEmotionLayoutListener() {
        mEmoticonsPageView.setOnIndicatorListener(new EmoticonsPageView.OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
            }
        });

        mEmoticonsPageView.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (mEmoticonsEditText != null) {
                    mEmoticonsEditText.setFocusable(true);
                    mEmoticonsEditText.setFocusableInTouchMode(true);
                    mEmoticonsEditText.requestFocus();
                    // 删除
                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        mEmoticonsEditText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }
                    // 用户自定义
                    else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    int index = mEmoticonsEditText.getSelectionStart();
                    Editable editable = mEmoticonsEditText.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getContent());
                    } else {
                        editable.insert(index, bean.getContent());
                    }
                }
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
                mEmoticonsToolBarView.setToolBtnSelect(position);
            }
        });

        mEmoticonsToolBarView.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                mEmoticonsPageView.setPageSelect(position);
            }
        });


    }

    private void handerEmotionEditTextOnTouchListener() {
        if (mEmoticonsEditText == null) {
            return;
        }
        if (isShowEmojiKeyBord) {
            mEmoticonsEditText.setFocusable(true);
            mEmoticonsEditText.setFocusableInTouchMode(true);
            closeEmotionBoard();
            openSoftBoard();
        }
        isShowSoftKeyBord = true;
        isShowEmojiKeyBord = false;
    }

    private void handlerPostButtonOnClick() {
        if (mListener != null) {
            this.mListener.onPostButtonClick();
        }
    }

    private void handlerEmotionButtonOnClick() {
        if (!isShowEmojiKeyBord && !isShowSoftKeyBord) {
            openEmotionBoard();
            isShowSoftKeyBord = false;
            show(0);
        } else if (isShowEmojiKeyBord && !isShowSoftKeyBord) {
            openSoftBoard();
            closeEmotionBoard();
        } else if (!isShowEmojiKeyBord && isShowSoftKeyBord) {
            closeSoftBoard();
            ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                @Override
                public void run() {
                    openEmotionBoard();
                }
            }, 200);
        }
    }

    public EmoticonsEditText getmEmoticonsEditText() {
        return mEmoticonsEditText;
    }

    /**
     * 获取编辑框内容
     *
     * @return
     */
    public String getEmotionEditContent() {
        if (mEmoticonsEditText == null) {
            return "";
        }
        return mEmoticonsEditText.getText().toString();
    }

    /**
     * 设置编辑款内容
     *
     * @param content
     */
    public void setEmotionEditContent(String content) {
        if (mEmoticonsEditText == null) {
            return;
        }
        mEmoticonsEditText.setText(content);
    }

    /**
     * 設置編輯提示
     *
     * @param hit
     */
    public void setEmotionEditHit(String hit) {
        if (mEmoticonsEditText == null) {
            return;
        }
        mEmoticonsEditText.setHint(hit);
    }

    /**
     * 设置表情按键是否可见
     *
     * @param isVisible
     */
    public void setEmotionButtonVisible(boolean isVisible) {
        if (isVisible) {
            mEmotionButton.setVisibility(VISIBLE);
        } else {
            mEmotionButton.setVisibility(GONE);
        }
    }

    /**
     * 关闭键盘
     */
    public void closeSoftware() {
        closeSoftBoard();
        closeEmotionBoard();
    }

    /**
     * 关闭系统软键盘
     */
    private void closeSoftBoard() {
        DeviceManager.getInstance().hideInputMethod();
        isShowSoftKeyBord = false;

    }

    /**
     * 关闭表情键盘
     */
    private void closeEmotionBoard() {
        if (mEmotionLayout != null && mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            mEmotionButton.setImageResource(R.drawable.btn_expression);
            isShowEmojiKeyBord = false;
        }
    }

    /**
     * 打开表情键盘
     */
    public void openEmotionBoard() {
        mEmotionLayout.setVisibility(View.VISIBLE);
        mEmotionButton.setImageResource(R.drawable.btn_writing);
        isShowEmojiKeyBord = true;
        isShowSoftKeyBord = false;
        NotificationCenter.getInstance().notify(Notification.obtain(NotificationDef.N_EMOJINKEYBOARD_SHOW));
    }

    /**
     * 打开软键盘
     */
    public void openSoftBoard() {
        ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
            @Override
            public void run() {
                DeviceManager.getInstance().showInputMethod();
            }
        }, 200);
        isShowSoftKeyBord = true;
    }

    public void tryOpenSoftBorad() {
        if (isShowEmojiKeyBord()) {
            closeEmotionBoard();
        }
        openSoftBoard();
    }

    /**
     * 表情键盘是否展开
     *
     * @return
     */
    public boolean isShowEmojiKeyBord() {
        return isShowEmojiKeyBord;
    }

    @Override
    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        if (mEmoticonsPageView != null) {
            mEmoticonsPageView.setBuilder(builder);
        }
        if (mEmoticonsToolBarView != null) {
            mEmoticonsToolBarView.setBuilder(builder);
        }
    }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public void show(int position) {
        int childCount = mEmotionLayout.getChildCount();
        if (position < childCount) {
            for (int i = 0; i < childCount; i++) {
                if (i == position) {
                    mEmotionLayout.getChildAt(i).setVisibility(View.VISIBLE);
                    mChildViewPosition = i;
                } else {
                    mEmotionLayout.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }
    }

    private void setAutoViewHeight(View view, int height) {
        int heightDp = height;
        if (heightDp > 0 && heightDp != mAutoViewHeight) {
            mAutoViewHeight = heightDp;
        }

        if (view != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.height = mAutoViewHeight;
            view.setLayoutParams(params);
        }
    }

    public void setOnPostButtonOnClickListener(OnPostButtonOnClickListener listener) {
        this.mListener = listener;
    }

    public interface OnPostButtonOnClickListener {
        void onPostButtonClick();
    }
}
