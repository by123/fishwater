package com.sjy.ttclub.account.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountBaseIHttpCallBack;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.account.model.PersonalDataChangeRequest;
import com.sjy.ttclub.framework.ActivityResult;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.ActivityRequestCode;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.photopicker.PhotoCropCallback;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.ActionSheetPanel;
import com.sjy.ttclub.widget.dialog.GenericDialog;
import com.sjy.ttclub.widget.dialog.IDialogOnClickListener;
import com.sjy.ttclub.widget.dialog.SimpleTextDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gangqing on 2015/12/17.
 * Email:denggangqing@ta2she.com
 */
public class PersonalWindow extends DefaultWindow implements View.OnClickListener {
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final String CHOOSE_1 = "1";
    private static final String CHOOSE_2 = "2";
    private static final String CHOOSE_4 = "4";
    private static final String CHOOSE_8 = "8";
    private int mSelectAge = -1;
    private int mSelectMarriage = -1;
    private int mSelectSexExp = -1;
    private boolean mIsChange = false;
    private String mImagePath;

    private SimpleDraweeView mHeadImage;
    private TextView mNickname, mSex, mAge, mMarriage, mExperience, mLevel;

    private String mPhotoPath;
    private AccountInfo mAccountInfo;


    public PersonalWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.account_personal_title);
        View view = View.inflate(getContext(), R.layout.account_personal_layout, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACCOUNT_STATE_CHANGED);
        NotificationCenter.getInstance().register(this, NotificationDef.N_ACTIVITY_RESULT);

        initView(view);
        updatePersonalWindow();
    }

    private void initView(View view) {
        mHeadImage = (SimpleDraweeView) view.findViewById(R.id.account_personal_head_image);
        mHeadImage.setOnClickListener(this);
        mNickname = (TextView) view.findViewById(R.id.account_nickname);
        mSex = (TextView) view.findViewById(R.id.account_sex);
        mAge = (TextView) view.findViewById(R.id.account_age);
        mMarriage = (TextView) view.findViewById(R.id.account_marriage);
        mExperience = (TextView) view.findViewById(R.id.account_experience);
        mLevel = (TextView) view.findViewById(R.id.account_level);
        view.findViewById(R.id.account_personal_nickname).setOnClickListener(this);
        view.findViewById(R.id.account_personal_age).setOnClickListener(this);
        view.findViewById(R.id.account_personal_marriage).setOnClickListener(this);
        view.findViewById(R.id.account_personal_experience).setOnClickListener(this);
        view.findViewById(R.id.account_personal_level).setOnClickListener(this);
        view.findViewById(R.id.account_personal_change_password).setOnClickListener(this);
        view.findViewById(R.id.account_personal_logout).setOnClickListener(this);
        view.findViewById(R.id.account_personal_pic_image).setOnClickListener(this);
        view.findViewById(R.id.account_personal_blacklist).setOnClickListener(this);
    }

    private void updatePersonalWindow() {
        AccountManager accountManager = AccountManager.getInstance();
        mAccountInfo = accountManager.getAccountInfo();
        if (mAccountInfo == null) {
            return;
        }

        accountManager.setHeadImage(mHeadImage);
        mNickname.setText(mAccountInfo.getNickname());
        mLevel.setText(mAccountInfo.getLevel());
        mAge.setText(mAccountInfo.getAge());
        setSex(mAccountInfo.getSex());
        setMarriage(mAccountInfo.getMarriage());
        setExperience(mAccountInfo.getSexyLife());
    }

    private void setSex(String sex) {
        switch (sex) {
            case CHOOSE_1:
                mSex.setText(R.string.account_personal_sex_man);
                break;
            case CHOOSE_2:
                mSex.setText(R.string.account_personal_sex_woman);
                break;
        }
    }

    private void setMarriage(String marriage) {
        switch (marriage) {
            case CHOOSE_1:
                mMarriage.setText(R.string.account_personal_marriage_bachelor_dom);
                break;
            case CHOOSE_2:
                mMarriage.setText(R.string.account_personal_marriage_love);
                break;
            case CHOOSE_4:
                mMarriage.setText(R.string.account_personal_marriage_married);
                break;
        }
    }

    private void setExperience(String experience) {
        switch (experience) {
            case CHOOSE_1:
                mExperience.setText(R.string.account_personal_experience_zero);
                break;
            case CHOOSE_2:
                mExperience.setText(R.string.account_personal_experience_introduction);
                break;
            case CHOOSE_4:
                mExperience.setText(R.string.account_personal_experience_general);
                break;
            case CHOOSE_8:
                mExperience.setText(R.string.account_personal_experience_superior);
                break;
            default:
                mExperience.setText(R.string.account_personal_experience_general);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_personal_head_image:
            case R.id.account_personal_pic_image:  //头像
                chooseHeadImage();
                break;
            case R.id.account_personal_nickname:    //昵称
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_CHANGE_NICKNAME_WINDOW);
                break;
            case R.id.account_personal_age: //年龄
                showSelectAgePanel();
                break;
            case R.id.account_personal_marriage:    //婚恋状况
                showSelectMarriagePanel();
                break;
            case R.id.account_personal_experience:  //性经验
                showSelectSexExpPanel();
                break;
            case R.id.account_personal_level:   //等级
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LEVEL_WINDOW);
                break;
            case R.id.account_personal_blacklist:   //私信黑名单
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_LETTERS_BLACKLIST_WINDOW);
                break;
            case R.id.account_personal_change_password: //修改密码
                MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_CHANGE_PASSWORD_WINDOW);
                break;
            case R.id.account_personal_logout:  //退出
                logout();
                break;
        }
    }

    private void showSelectAgePanel() {
        if(mAccountInfo == null){
            return;
        }
        final int minAge = 18;
        int maxAge = 70;
        int count = maxAge - minAge;
        final ArrayList<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(String.valueOf(i + minAge));
        }
        final PersonalPanel panel = new PersonalPanel(getContext());
        panel.setPanelCallback(new PersonalPanel.IPersonalPanelCallback() {
            @Override
            public void onPersonalItemSelected(int position) {
            }

            @Override
            public void onPersonalChangedFinish(int position) {
                mSelectAge = position + minAge;
                if (mSelectAge != StringUtils.parseInt(mAccountInfo.getAge())) {
                    mAge.setText(String.valueOf(position + minAge));
                    mIsChange = true;
                }
            }
        });
        int userAge = StringUtils.parseInt(mAccountInfo.getAge());
        panel.setupPanel(list, userAge - minAge);
        panel.showPanel();
    }

    private void showSelectMarriagePanel() {
        if(mAccountInfo == null){
            return;
        }
        final ArrayList<String> list = new ArrayList<>(3);
        list.add(ResourceHelper.getString(R.string.account_personal_marriage_bachelor_dom));
        list.add(ResourceHelper.getString(R.string.account_personal_marriage_love));
        list.add(ResourceHelper.getString(R.string.account_personal_marriage_married));
        final PersonalPanel panel = new PersonalPanel(getContext());
        panel.setPanelCallback(new PersonalPanel.IPersonalPanelCallback() {
            @Override
            public void onPersonalItemSelected(int position) {
            }

            @Override
            public void onPersonalChangedFinish(int position) {
                mSelectMarriage = (int) Math.pow(2, position);
                if (mSelectMarriage != StringUtils.parseInt(mAccountInfo.getMarriage())) {
                    setMarriage(String.valueOf(mSelectMarriage));
                    mIsChange = true;
                }
            }
        });
        panel.setupPanel(list, changePosition(mAccountInfo.getMarriage()));
        panel.showPanel();
    }

    private void showSelectSexExpPanel() {
        if(mAccountInfo == null){
            return;
        }
        final ArrayList<String> list = new ArrayList<>(4);
        list.add(ResourceHelper.getString(R.string.account_personal_experience_zero));
        list.add(ResourceHelper.getString(R.string.account_personal_experience_introduction));
        list.add(ResourceHelper.getString(R.string.account_personal_experience_general));
        list.add(ResourceHelper.getString(R.string.account_personal_experience_superior));
        final PersonalPanel panel = new PersonalPanel(getContext());
        panel.setPanelCallback(new PersonalPanel.IPersonalPanelCallback() {
            @Override
            public void onPersonalItemSelected(int position) {
            }

            @Override
            public void onPersonalChangedFinish(int position) {
                mSelectSexExp = (int) Math.pow(2, position);
                if (mSelectSexExp != StringUtils.parseInt(mAccountInfo.getSexyLife())) {
                    setExperience(String.valueOf(mSelectSexExp));
                    mIsChange = true;
                }
            }
        });
        panel.setupPanel(list, changePosition(mAccountInfo.getSexyLife()));
        panel.showPanel();
    }

    private void chooseHeadImage() {
        ActionSheetPanel panel = new ActionSheetPanel(getContext());

        ActionSheetPanel.ActionSheetItem item = new ActionSheetPanel.ActionSheetItem();
        item.id = String.valueOf(PHOTO_REQUEST_GALLERY);
        item.title = ResourceHelper.getString(R.string.photo_pick_panel_from_gallery);
        panel.addSheetItem(item);

        item = new ActionSheetPanel.ActionSheetItem();
        item.id = String.valueOf(PHOTO_REQUEST_TAKEPHOTO);
        item.title = ResourceHelper.getString(R.string.photo_pick_panel_from_camera);
        panel.addSheetItem(item);

        panel.setSheetItemClickListener(new ActionSheetPanel.OnActionSheetClickListener() {
            @Override
            public void onActionSheetItemClick(String id) {
                if (String.valueOf(PHOTO_REQUEST_GALLERY).equals(id)) {
                    chooseFromGallery();
                } else if (String.valueOf(PHOTO_REQUEST_TAKEPHOTO).equals(id)) {
                    chooseFromCamera();
                }
            }
        });

        panel.showPanel();
    }

    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        try {
            ((Activity) getContext()).startActivityForResult(intent, ActivityRequestCode.ACCOUNT_PHOTO_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseFromCamera() {
        mPhotoPath = CommonUtils.getSnapshotPath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoPath)));
        try {
            ((Activity) getContext()).startActivityForResult(intent, ActivityRequestCode.ACCOUNT_PHOTO_TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        SimpleTextDialog dialog = new SimpleTextDialog(getContext());
        dialog.addTitle(R.string.account_setting_alert_logout_title);
        dialog.setText(R.string.account_setting_alert_logout_message_sure);
        dialog.addYesNoButton();
        dialog.setRecommendButton(GenericDialog.ID_BUTTON_NO);
        dialog.setOnClickListener(new IDialogOnClickListener() {
            @Override
            public boolean onDialogClick(GenericDialog dialog, int viewId, Object extra) {
                if (viewId == GenericDialog.ID_BUTTON_YES) {
                    mCallBacks.onWindowExitEvent(PersonalWindow.this, true);
                    AccountManager.getInstance().logout();
                }
                return false;
            }
        });
        dialog.show();
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationDef.N_ACCOUNT_STATE_CHANGED) {
            updatePersonalWindow();
        } else if (notification.id == NotificationDef.N_ACTIVITY_RESULT) {
            if (notification.extObj instanceof ActivityResult) {
                handleActivityResult((ActivityResult) notification.extObj);
            }
        }
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.resultCode != Activity.RESULT_OK) {
            return;
        }
        if (result.requestCode == ActivityRequestCode.ACCOUNT_PHOTO_GALLERY) {
            Uri uri = result.data.getData();
            String path = CommonUtils.getMediaAbsolutePath(getContext(), uri);
            showCropWindow(path);
        } else if (result.requestCode == ActivityRequestCode.ACCOUNT_PHOTO_TAKE_PHOTO) {
            showCropWindow(mPhotoPath);
        }
    }

    private void showCropWindow(final String srcPath) {
        PhotoCropCallback cropCallback = new PhotoCropCallback() {
            @Override
            public void onPhotoCropResult(String path) {
                if (path == null) {
                    return;
                }
                mImagePath = path;
                PersonalDataChangeRequest personalDataChangeRequest = new PersonalDataChangeRequest(getContext());
                personalDataChangeRequest.changeUserPicImage(path, new ChangeUserPicImageIHttpCallBack());
            }

            @Override
            public String getOrigPhotoPath() {
                return srcPath;
            }
        };
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PHOTO_CROP_WINDOW;
        msg.obj = cropCallback;
        msg.arg1 = 1;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_ON_DETACH) {
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACCOUNT_STATE_CHANGED);
            NotificationCenter.getInstance().unregister(this, NotificationDef.N_ACTIVITY_RESULT);
            commitData();
        }
    }

    private void commitData() {
        if (!mIsChange) {
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        if (mSelectAge != -1) {
            map.put("age", String.valueOf(mSelectAge));
        }
        if (mSelectMarriage != -1) {
            map.put("marriage", String.valueOf(mSelectMarriage));
        }
        if (mSelectSexExp != -1) {
            map.put("sexyLife", String.valueOf(mSelectSexExp));
        }
        PersonalDataChangeRequest personalDataChangeRequest = new PersonalDataChangeRequest(getContext());
        personalDataChangeRequest.changeUUI(map, new AccountBaseIHttpCallBack() {
            @Override
            public void onFail(String errorStr, int code) {

            }

            @Override
            public <T> void onSuccess(T obj, String result) {
                AccountManager.getInstance().notifyAccountDataChanged();
            }
        });
    }

    private int changePosition(String position) {
        int i = 0;
        switch ((position)) {
            case CHOOSE_1:
                i = 0;
                break;
            case CHOOSE_2:
                i = 1;
                break;
            case CHOOSE_4:
                i = 2;
                break;
            case CHOOSE_8:
                i = 3;
                break;
        }
        return i;
    }

    private class ChangeUserPicImageIHttpCallBack extends AccountBaseIHttpCallBack {
        @Override
        public <T> void onSuccess(T obj, String result) {
            mHeadImage.setImageURI(Uri.fromFile(new File(mImagePath)));
            ToastHelper.showToast(getContext(), R.string.account_personal_change_user_pic_image_success, Toast.LENGTH_SHORT);
            AccountManager.getInstance().notifyAccountDataChanged();
        }

        @Override
        public void onFail(String errorStr, int code) {
            ToastHelper.showToast(getContext(), R.string.account_personal_change_user_pic_image_error, Toast.LENGTH_SHORT);
        }
    }
}
