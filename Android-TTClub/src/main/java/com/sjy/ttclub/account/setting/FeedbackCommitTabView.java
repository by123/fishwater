package com.sjy.ttclub.account.setting;

import android.content.Context;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.FeedbackRequest;
import com.sjy.ttclub.account.setting.PhotoGridAdapter.PhotoItem;
import com.sjy.ttclub.account.widget.NoScrollGridView;
import com.sjy.ttclub.web.WebViewBrowserParams;
import com.sjy.ttclub.framework.DeviceManager;
import com.sjy.ttclub.framework.ITabView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.photopicker.PhotoInfo;
import com.sjy.ttclub.photopicker.PhotoPickerCallback;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.util.BitmapUtil;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.PhotoCacheHelper;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Created by gangqing on 2015/12/24.
 * Email:denggangqing@ta2she.com
 */
public class FeedbackCommitTabView implements ITabView, PhotoGridAdapter.IPhotoAdapterListener {
    private static final int MAX_SIZE = 9;

    private Context mContext;
    private View mRootView;
    private EditText mContentEditText;
    private NoScrollGridView mPhotoGridView;
    private PhotoGridAdapter mAdapter;
    private PhotoItem mAddPhotoItem;
    private ArrayList<PhotoItem> mPhotoItemList = new ArrayList<PhotoItem>();
    private PhotoCacheHelper mCacheHelper;

    public FeedbackCommitTabView(Context context) {
        mContext = context;
        mCacheHelper = new PhotoCacheHelper(context);
        mRootView = initView();
    }

    private View initView() {
        View view = View.inflate(mContext, R.layout.account_feedback_want_feedback, null);
        view.findViewById(R.id.feedback_common_problems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebView();
            }
        });
        mContentEditText = (EditText) view.findViewById(R.id.account_want_feedback_content);
        mPhotoGridView = (NoScrollGridView) view.findViewById(R.id.account_want_feedback_grid_view);
        mContentEditText.setLongClickable(false);
        //关闭系统表情
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
        mAddPhotoItem = createAddPhotoItem();
        mPhotoItemList.add(mAddPhotoItem);
        mAdapter = new PhotoGridAdapter(mContext, mPhotoItemList);
        mAdapter.setCacheHelper(mCacheHelper);
        mAdapter.setAdapterListener(this);
        mPhotoGridView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public String getTitle() {
        return ResourceHelper.getString(R.string.account_feedback_want_feedback);
    }

    @Override
    public View getTabView() {
        return mRootView;
    }

    @Override
    public void onTabChanged(int tabChangedFlag) {

    }

    @Override
    public void onPrepareContentView() {

    }

    public void destroy() {
        mCacheHelper.clearCache();
    }

    private PhotoItem createAddPhotoItem() {
        PhotoItem item = new PhotoItem();
        item.type = PhotoItem.TYPE_ADD;
        return item;
    }

    private void addAddPhotoItem() {
        if (mPhotoItemList.size() < MAX_SIZE) {
            if (!mPhotoItemList.contains(mAddPhotoItem)) {
                mPhotoItemList.add(mAddPhotoItem);
            }
        }
    }

    /**
     * 常见问题的网页
     */
    private void goToWebView() {
        Message msg = new Message();
        WebViewBrowserParams params = new WebViewBrowserParams();
        params.title = mContext.getResources().getString(R.string.account_setting_help_center);
        params.url = HttpUrls.HELP_URL;
        msg.obj = params;
        msg.what = MsgDef.MSG_SHOW_WEB_VIEW_BROWSER;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    /**
     * 提交
     */
    public void commit() {
        DeviceManager.getInstance().hideInputMethod();
        String content = mContentEditText.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastHelper.showToast(mContext, R.string.account_feedback_content_empty_hint);
            return;
        }
        FeedbackRequest feedbackRequest = new FeedbackRequest(mContext);
        ArrayList<String> photoList = new ArrayList<String>();
        for (PhotoItem item : mPhotoItemList) {
            if (item.type != PhotoItem.TYPE_ADD && item.path != null) {
                photoList.add(item.path);
            }
        }
        final LoadingDialog loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();
        feedbackRequest.commitWantFeedback(content, photoList, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                loadingDialog.dismiss();
                if (StringUtils.isNotEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        if (HttpCode.SUCCESS_CODE == code) {
                            ToastHelper.showToast(mContext, R.string.account_feedback_success);
                            mContentEditText.setText("");
                            mPhotoItemList.clear();
                            addAddPhotoItem();
                            mAdapter.notifyDataSetChanged();

                        } else {
                            ToastHelper.showToast(mContext, R.string.account_feedback_failed);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastHelper.showToast(mContext, R.string.account_feedback_failed);
                    }
                }
                BitmapUtil.deleteTempImages();
            }

            @Override
            public void onError(String errorStr, int code) {
                loadingDialog.dismiss();
                ToastHelper.showToast(mContext, R.string.account_feedback_failed);
                BitmapUtil.deleteTempImages();
            }
        });
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
                for (PhotoItem item : mPhotoItemList) {
                    if (item.type == PhotoItem.TYPE_GALLERY) {
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
        PhotoItem item;
        for (PhotoInfo info : resultList) {
            item = new PhotoItem();
            item.type = PhotoItem.TYPE_GALLERY;
            item.path = info.path;
            mPhotoItemList.add(item);
        }
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
    }


    private void handlePhotoClick(PhotoItem item) {
        int position = mPhotoItemList.indexOf(item);
        if (position < 0) {
            position = 0;
        }
        ArrayList<String> pathList = new ArrayList<>(mPhotoItemList.size());
        for (PhotoItem temp : mPhotoItemList) {
            if (temp.type != PhotoItem.TYPE_ADD) {
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
    public void onPhotoClicked(PhotoItem item) {
        if (item.type == PhotoItem.TYPE_ADD) {
            handleAddClick();
        } else {
            handlePhotoClick(item);
        }
    }

    @Override
    public void onPhotoDelete(PhotoItem item) {
        boolean success = mPhotoItemList.remove(item);
        if (!success) {
            for (PhotoItem temp : mPhotoItemList) {
                if (item.path.equals(temp.path)) {
                    mPhotoItemList.remove(temp);
                    break;
                }
            }
        }
        addAddPhotoItem();
        mAdapter.notifyDataSetChanged();
    }

}
