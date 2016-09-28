package com.sjy.ttclub.record;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lsym.ttclub.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.BaseBean;
import com.sjy.ttclub.bean.record.RecordDataDay;
import com.sjy.ttclub.bean.record.RecordDataMonth;
import com.sjy.ttclub.bean.record.RecordDay;
import com.sjy.ttclub.bean.record.RecordMonth;
import com.sjy.ttclub.bean.record.RecordPeepRuleBean;
import com.sjy.ttclub.community.AnimotionDao;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.network.HttpCallbackAdapter;
import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.record.datepicker.views.DatePicker;
import com.sjy.ttclub.record.datepicker.views.MonthView;
import com.sjy.ttclub.record.model.RecordPeepRequest;
import com.sjy.ttclub.record.model.RecordRequest;
import com.sjy.ttclub.record.peep.RecordPeepRuleInfo;
import com.sjy.ttclub.record.widget.RecordCollapseFrameLayout;
import com.sjy.ttclub.record.widget.RecordFloatView;
import com.sjy.ttclub.record.widget.RecordNewListView;
import com.sjy.ttclub.record.widget.RecordRemindPanel;
import com.sjy.ttclub.record.widget.RecordWindowListView;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.SettingFlags;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaImageView;
import com.sjy.ttclub.widget.LoadingLayout;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhxu on 2015/12/1.
 * Email:357599859@qq.com
 */
public class RecordWindow extends DefaultWindow implements View.OnClickListener, RecordFloatView.OnFloatItemClickListener, CheckBox.OnCheckedChangeListener, DatePicker.OnDatePickedListener, MonthView.OnDateChangeListener {

    public final static String TAG = "RecordActivity";

    public static final int RECORD_ITEM_ID_PAPA = 1;
    public static final int RECORD_ITEM_ID_MYSELF = 2;
    public static final int RECORD_ITEM_ID_NOTHING = 3;

    private static final int ACTION_PEEP = 1;
    private static final int ACTION_MY_RECORD = 2;
    private String mPapaTime;
    private String mPapaTimeRange;

    //统计
    private boolean isNoHappen = true;
    private String mGender = "man";

    private DatePicker mDatePicker;
    private RecordCollapseFrameLayout mRecordTopView;
    private RecordNewListView mListView;
    private RecordFloatView mFloatView;
    private LinearLayout mEditLayout, mNoLayout, mNoLayoutTop;
    private AlphaImageView mButtonOK;
    private ImageView mTopImage;
    private CheckBox mCheckBox;
    private SimpleDraweeView mNothingBg;

    private LoadingDialog mLoadingDialog;
    private RecordWindowListView mRecordWindowListView;
    private RecordPanel mRecordPanel;

    private LoadingLayout mLoadingLayout;

    private List<RecordDataMonth> mListMonthData = new ArrayList<>();
    private List<RecordDataDay> mListData = new ArrayList<>();
    private RecordRequest mRecordRequest;
    private RecordPeepRequest mPeepRequest;

    private RecordConfig mRecordConfig;

    private boolean mIsInitPanelShowed = false;
    private RecordPeepRuleInfo mRecordPeepRuleInfo;
    private ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
    private static final String RECORD_KEY_INIT = "isInitShowRecordWindow";

    private int mYear, mMonth, mDay;

    public RecordWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setEnableSwipeGesture(false);
        mRecordConfig = RecordConfig.getInstance();
        setTitle(R.string.record_title);
        setTitleBarBackground(ResourceHelper.getColor(R.color.record_top_bg));
        setTitleIcon(ResourceHelper.getDrawable(R.drawable.title_back));
        setTitleColor(ResourceHelper.getColor(R.color.white));

        initActionBar();

        View view = View.inflate(getContext(), R.layout.record_main, null);
        getBaseLayer().addView(view, getContentLPForBaseLayer());
        initUI(view);
        //统计RECORD_VIEW
        StatsModel.stats(StatsKeyDef.RECORD_VIEW);
        mGender = AccountManager.getInstance().isManSex() ? "man" : "woman";

        mPeepRequest = new RecordPeepRequest();
        tryGetPeepRuleInfo(true);
    }

    private void initActionBar() {
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.record_action_bar_me));
        item.setItemId(ACTION_MY_RECORD);
        actionList.add(item);

        item = new TitleBarActionItem(getContext());
        item.setDrawable(ResourceHelper.getDrawable(R.drawable.record_action_bar_peep));
        item.setItemId(ACTION_PEEP);
        actionList.add(item);
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    private void tryStartPeepAnimation() {
        for (final TitleBarActionItem actionItem : actionList) {
            if (actionItem.getItemId() == ACTION_PEEP) {
                ThreadManager.postDelayed(ThreadManager.THREAD_UI, new Runnable() {
                    @Override
                    public void run() {
                        actionItem.startAnimation(AnimotionDao.getPeepScaleAnimation(false));
                    }
                }, 500);
            }
        }
    }

    private void initUI(View view) {

        mRecordTopView = (RecordCollapseFrameLayout) findViewById(R.id.record_top_view);
        mRecordTopView.init();

        Calendar c = Calendar.getInstance();
        mDatePicker = (DatePicker) findViewById(R.id.record_dp);
        mDatePicker.setOnDatePickedListener(this);
        mDatePicker.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
        mDatePicker.setOnDateChangeListener(this);

        mListView = (RecordNewListView) findViewById(R.id.record_list);
        mListView.stepListView(mListData);
        mListView.setCollapseFrameLayout(mRecordTopView);

//        mCalendarView = (CollapseCalendarView) view.findViewById(R.id.calendar);
//        mRecordListView = (RecordNewListView) view.findViewById(R.id.record_list);
//        mCalendarView.setListener(this);

        mRecordPanel = new RecordPanel(getContext());
        View panelContentView = mRecordPanel.getContentView();
        panelContentView.setOnClickListener(this);

        mCheckBox = (CheckBox) panelContentView.findViewById(R.id.record_window_check);
        mTopImage = (ImageView) panelContentView.findViewById(R.id.record_window_top_view);
        mEditLayout = (LinearLayout) panelContentView.findViewById(R.id.record_window_edit);
        mNoLayout = (LinearLayout) panelContentView.findViewById(R.id.record_window_no);
        mNoLayoutTop = (LinearLayout) panelContentView.findViewById(R.id.record_window_no_top);
        mRecordWindowListView = (RecordWindowListView) panelContentView.findViewById(R.id.record_window_list);
        mNothingBg = (SimpleDraweeView) panelContentView.findViewById(R.id.record_window_nothing_bg);
        mButtonOK = (AlphaImageView) panelContentView.findViewById(R.id.record_float_view);
        mButtonOK.setOnClickListener(this);
        mCheckBox.setOnCheckedChangeListener(this);

        mLoadingDialog = new LoadingDialog(getContext());
        mRecordRequest = new RecordRequest(getContext());

        if (!AccountManager.getInstance().isManSex()) {
            mNoLayoutTop.setVisibility(View.VISIBLE);
        }

//        stepCalendarView();
//        stepRecordListView();
        stepRecordWindowListView();

        mFloatView = (RecordFloatView) view.findViewById(R.id.record_float_view);
        mFloatView.setFloatItemClickListener(this);
        mFloatView.setupFloatItemView(createFloatItems());

        mLoadingLayout = (LoadingLayout) view.findViewById(R.id.record_loading_layout);
        mLoadingLayout.setDefaultLoading();
    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        boolean isLoadFinished = mLoadingLayout.getVisibility() != View.VISIBLE;
        if (!isLoadFinished) {
            return;
        }

        if (itemId == ACTION_MY_RECORD) {
            isNoHappen = false;
            MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_RECORD_SELF_WINDOW);
            //统计-我的记录
            StatsModel.stats(StatsKeyDef.RECORD_MINE, "gender", AccountManager.getInstance().switchSex());
        } else if (itemId == ACTION_PEEP) {
            handlePeepClick();
        }
    }

    private void handlePeepClick() {
        isNoHappen = false;
        if (mRecordPeepRuleInfo == null) {
            tryGetPeepRuleInfo(false);
            return;
        }
        String type;
        if (!mRecordPeepRuleInfo.hasPeepPermission()) {
            RecordRemindPanel panel = new RecordRemindPanel(getContext());
            panel.setIcon(R.drawable.record_panel_peep_lock);
            panel.setKnowShow();
            panel.showPanel();
            //统计-偷窥-没有权限
            type = "unauthorized";
            StatsModel.stats(StatsKeyDef.RECORD_SNOOP, "type", type);
            return;
        }
        //统计偷窥-有权限
        type = "authorized";
        StatsModel.stats(StatsKeyDef.RECORD_SNOOP, "type", type);
        Message msg = Message.obtain();
        msg.obj = mRecordPeepRuleInfo;
        msg.what = MsgDef.MSG_SHOW_RECORD_PEEP_WINDOW;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            loadDataByMonth();
        } else if (stateFlag == STATE_ON_DETACH) {
            destroy();
        }
    }

//    private void stepCalendarView() {
//        CalendarManager manager = new CalendarManager(LocalDate.now(), CalendarManager.State.MONTH, LocalDate.now()   .plusMonths(-6), LocalDate.now());
//        mCalendarView.setBackgroundColor(getResources().getColor(R.color.record_date_view_bg));
//        mCalendarView.init(manager, this);
//    }

//    private void stepRecordListView() {
//        mRecordListView.stepListView(mCalendarView, mListDayData);
//    }

    private void stepRecordWindowListView() {
        mRecordWindowListView.stepListView();
    }

    public ArrayList<RecordFloatView.FloatItemInfo> createFloatItems() {
        ArrayList<RecordFloatView.FloatItemInfo> itemList = new ArrayList<>(3);
        addItemList(itemList, RECORD_ITEM_ID_PAPA, ResourceHelper.getString(R.string.record_btn_papa));
        addItemList(itemList, RECORD_ITEM_ID_MYSELF, ResourceHelper.getString(R.string.record_btn_myself));
        addItemList(itemList, RECORD_ITEM_ID_NOTHING, ResourceHelper.getString(R.string.record_btn_nothing));
        return itemList;
    }

    private void addItemList(ArrayList<RecordFloatView.FloatItemInfo> itemList, int id, String iconText) {
        Resources res = getResources();
        RecordFloatView.FloatItemInfo info = new RecordFloatView.FloatItemInfo();
        info.id = id;
        info.icon = res.getDrawable(R.drawable.record_float_view_btn);
        info.topIconText = iconText;
        itemList.add(info);
    }

    private void showRecordPanel() {
        if (!mRecordPanel.isShowing()) {
            mRecordPanel.showPanel();
        }
    }

    private boolean windowConfig(int type) {
        clearList(type);

//        LocalDate selectDate = mCalendarView.getManager().getSelectedDay();
//        String date = String.format(ResourceHelper.getString(R.string.record_title_date), selectDate.getMonthOfYear(), selectDate.getDayOfMonth());

        if (type == RECORD_ITEM_ID_PAPA && isAfterToday()) {
            //统计RECORD_PAPA
            StatsModel.stats(StatsKeyDef.RECORD_PAPA, "gender", mGender);
            showNoLayout(false, R.drawable.record_window_top_icon_red);
            return true;
        } else if (type == RECORD_ITEM_ID_MYSELF && isAfterToday()) {
            //统计RECORD_ZIHAI
            StatsModel.stats(StatsKeyDef.RECORD_ZIHAI, "gender", mGender);
            showNoLayout(false, R.drawable.record_window_top_icon_blue);
            return true;
        } else if (isAfterToday()) {
            //统计RECORD_BUZUO
            StatsModel.stats(StatsKeyDef.RECORD_BUZUO, "gender", mGender);
            showNoLayout(true, R.drawable.record_window_top_icon_yellow);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("res://drawable-xxhdpi/" + R.drawable.record_window_nothing_bg)).build();
            DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setAutoPlayAnimations(true).build();
            mNothingBg.setController(controller);
            return true;
        }
        return false;
    }

    private boolean isAfterToday() {
        Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date d;
        try {
            d = sdf.parse(mYear + "-" + mMonth + "-" + mDay);
            int flag = d.compareTo(now);
            if (flag >= 0) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_error3), Toast.LENGTH_SHORT);
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void clearList(int type) {
        mCheckBox.setChecked(false);
        mRecordConfig.clearListRecord();
        switch (type) {
            case RECORD_ITEM_ID_PAPA:
                mRecordConfig.put(RecordConfig.TYPE_HEAT, RecordConfig.UN_HEAT);
                mRecordConfig.put(RecordConfig.TYPE_IS_TOOLS, RecordConfig.UN_TOOLS);
                break;
            case RECORD_ITEM_ID_MYSELF:
                mRecordConfig.put(RecordConfig.TYPE_HEAT, RecordConfig.UN_HEAT);
                break;
            case RECORD_ITEM_ID_NOTHING:
                break;
        }
    }

    private void showNoLayout(boolean _visibility, int resId) {
        if (_visibility) {
            mNoLayout.setVisibility(View.VISIBLE);
            mEditLayout.setVisibility(View.GONE);
        } else {
            mNoLayout.setVisibility(View.GONE);
            mEditLayout.setVisibility(View.VISIBLE);
        }
        mTopImage.setImageResource(resId);
    }

    /**
     * 提交前判断逻辑
     *
     * @param type
     * @param json
     * @return
     */
    private boolean exist(int type, String json, String chooseTime) {
        if (type == RECORD_ITEM_ID_PAPA) {
            //统计RECORD_PAPA_SUBMIT
            StatsModel.stats(StatsKeyDef.RECORD_PAPA_SUBMIT, "gender", mGender);
            if (StringUtils.isEmpty(chooseTime)) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_date));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_FEEL_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_feel));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_TIME_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_time));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_POSTURE_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_posture));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_SCENE_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_scene));
                return false;
            }
        } else if (type == RECORD_ITEM_ID_MYSELF) {
            //统计RECORD_ZIHAI_SUBMIT
            StatsModel.stats(StatsKeyDef.RECORD_ZIHAI_SUBMIT, "gender", mGender);
            if (json.indexOf(RecordConfig.TYPE_FEEL_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_feel));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_LIVEN_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_liven));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_TOOLS_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_tools));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_SCENE_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_scene));
                return false;
            }
            if (json.indexOf(RecordConfig.TYPE_TIME_EXIST) == -1) {
                ToastHelper.showToast(getContext(), ResourceHelper.getString(R.string.record_msg_time));
                return false;
            }
        } else {
            //统计RECORD_BUZUO_SUBMIT
            StatsModel.stats(StatsKeyDef.RECORD_BUZUO_SUBMIT, "gender", mGender);
        }
        return true;
    }

    private void showDialog(String msg) {
        mLoadingDialog.setMessage(msg);
        mLoadingDialog.show();
    }

    private void closeDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record_window_bg) {
            mRecordPanel.hidePanel();
        } else {
            String json = mRecordConfig.getJsonData();
            String chooseTime = mRecordConfig.getChooseTime();
            if (exist(RecordConfig.mCate, json, chooseTime)) {
                commitData(mRecordConfig.getJsonData(), mPapaTime, chooseTime);
            }
        }
    }

    @Override
    public void onItemClick(RecordFloatView.FloatItemInfo info) {
        if (windowConfig(info.id)) {
            mRecordWindowListView.updateData(info.id);
            showRecordPanel();
        }
        isNoHappen = false;
    }

    @Override
    public void onMonthChange(int month) {
        mMonth = month;
        loadDataByMonth();
        isNoHappen = false;
    }

    @Override
    public void onYearChange(int year) {
        mYear = year;
        loadDataByMonth();
        isNoHappen = false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int category = isChecked ? RecordConfig.RECORD_TYPE_DYM : RecordConfig.RECORD_TYPE_NOTHING;
        mRecordConfig.updateCate(category);
    }

//    @Override
//    public void onDateSelected(LocalDate date) {
//        mPapaTime = getPapaTime(date);
//        loadDataByDay();
//    }

    @Override
    public void onDatePicked(String date) {
        setTime(date);
        loadDataByDay();
    }

    private void setTime(String date) {
        mPapaTime = date;
        mYear = StringUtils.parseInt(date.substring(0, date.indexOf("-")));
        mMonth = StringUtils.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
        mDay = StringUtils.parseInt(date.substring(date.lastIndexOf("-") + 1));
    }

    private void destroy() {
        if (isNoHappen) {
            //统计RECORD_VIEW_LEAVE
            StatsModel.stats(StatsKeyDef.RECORD_VIEW_LEAVE, "gender", mGender);
        }
        mRecordConfig.destroy();
//        DPCManager.getInstance().recycle();
    }

    private void handleLoadMonthDataSuccess(String result) {
        if (StringUtils.isNotEmpty(result)) {
            try {
                Gson gson = new Gson();
                RecordMonth recordMonth = gson.fromJson(result, RecordMonth.class);
                if (recordMonth.getStatus() == HttpCode.SUCCESS_CODE) {
                    mListMonthData.addAll(recordMonth.getData().getDataMonth());
                    mDatePicker.updateData(mListMonthData);
                } else {
                    toastRequestError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                toastRequestError();
            }
        } else {
            toastRequestError();
        }
    }

    private void handleLoadDayDataSuccess(String result) {
        if (StringUtils.isNotEmpty(result)) {
            try {
                Gson gson = new Gson();
                RecordDay recordDay = gson.fromJson(result, RecordDay.class);
                if (recordDay.getStatus() == HttpCode.SUCCESS_CODE) {
                    mListData.clear();
                    List<RecordDataDay> list = recordDay.getData().getDataDay();
                    if (list.size() == 0) {
                        RecordDataDay recordDataDay = new RecordDataDay();
                        recordDataDay.setCategory(RecordConfig.RECORD_TYPE_NO + "");
                        mListData.add(recordDataDay);
                    } else {
                        mListData.addAll(list);
                    }
                    mListView.updateData(mListData);
                } else {
                    toastRequestError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                toastRequestError();
            }
        } else {
            toastRequestError();
        }
    }

    /**
     * 加载某月数据
     */
    private void loadDataByMonth() {
        mListMonthData.clear();
//        LocalDate localDate = mCalendarView.getManager().getActiveMonth();
        mRecordRequest.getRecordByMonth(mYear, mMonth, new HttpCallbackAdapter() {
            @Override
            public void onError(String errorStr, int code) {
                if (code != HttpCode.INVALID_TOKEN_CODE) {
                    toastRequestError();
                } else {
                    super.onError(errorStr, code);
                }
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public <T> void onSuccess(T obj, String result) {
                mLoadingLayout.setVisibility(View.GONE);
                handleLoadMonthDataSuccess(result);
            }
        });
    }

    /**
     * 加载某日数据
     */
    private void loadDataByDay() {
//        LocalDate localDate = mCalendarView.getManager().getSelectedDay();
        mRecordRequest.getRecordByDay(mYear, mMonth, mDay, new
                HttpCallbackAdapter() {
                    @Override
                    public void onError(String errorStr, int code) {
                        if (code != HttpCode.INVALID_TOKEN_CODE) {
                            toastRequestError();
                        } else {
                            super.onError(errorStr, code);
                        }
                    }

                    @Override
                    public <T> void onSuccess(T obj, String result) {
                        handleLoadDayDataSuccess(result);
                    }
                });
    }


    private void toastRequestError() {
        ToastHelper.showToast(getContext(), R.string.record_request_exception);
    }

    /**
     * 提交打卡记录
     *
     * @param json
     */
    private void commitData(String json, String papaTime, String papaTimeRange) {
        showDialog(ResourceHelper.getString(R.string.record_commit_msg));
        mRecordRequest.commitData(json, papaTime, papaTimeRange, new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                closeDialog();
                if (StringUtils.isNotEmpty(result)) {
                    try {
                        Gson gson = new Gson();
                        BaseBean resultsBean = gson.fromJson(result, BaseBean.class);
                        if (resultsBean.getStatus() == HttpCode.SUCCESS_CODE) {
                            ToastHelper.showToast(getContext(), R.string.record_request_success);
                            if (mRecordPanel != null) {
                                mRecordPanel.hidePanel();
                            }
                            loadDataByMonth();
                            loadDataByDay();
                        } else {
                            toastRequestError();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastRequestError();
                    }
                } else {
                    toastRequestError();
                }
            }

            @Override
            public void onError(String errorStr, int code) {
                if (HttpCode.FAIL_CODE == code && StringUtils.isNotEmpty(errorStr)) {
                    Gson gson = new Gson();
                    BaseBean resultsBean = gson.fromJson(errorStr, BaseBean.class);
                    ToastHelper.showToast(getContext(), resultsBean.getMsg(), Toast.LENGTH_SHORT);
                } else {
                    toastRequestError();
                }
                closeDialog();
                super.onError(errorStr, code);
            }
        });
    }

    private void tryGetPeepRuleInfo(final boolean isFirst) {
        mPeepRequest.getPeepRule(new HttpCallbackAdapter() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (obj instanceof RecordPeepRuleBean) {
                    RecordPeepRuleBean ruleBean = (RecordPeepRuleBean) obj;
                    if (ruleBean.getStatus() == HttpCode.SUCCESS_CODE) {
                        mRecordPeepRuleInfo = new RecordPeepRuleInfo((RecordPeepRuleBean) obj);
                        if (isFirst) {
                            tryShowInitPanel();
                        }
                        //TODO 用户升级后首次进入啪啪记录页时出现一次偷窥按钮动效果
                    }
                }
            }
        });
    }

    private static final String FLAG_PANEL_PAST_RECORD = "flag_record_panel_past_record";
    private static final String FLAG_PANEL_PEEP_UNLOCK = "flag_record_panel_peep_unlock";
    private static final String FLAG_PANEL_PEEP_LOCK = "flag_record_panel_peep_lock";

    private void tryShowInitPanel() {
        if (mIsInitPanelShowed) {
            return;
        }
        mIsInitPanelShowed = true;
        boolean unlockShowed = SettingFlags.getBooleanFlag(FLAG_PANEL_PEEP_UNLOCK, false);
        if (!unlockShowed) {
            if (mRecordPeepRuleInfo != null && mRecordPeepRuleInfo.hasPeepPermission()) {
                showPanel(FLAG_PANEL_PEEP_UNLOCK);
                return;
            }
        }
        boolean pastRecordShowed = SettingFlags.getBooleanFlag(FLAG_PANEL_PAST_RECORD, false);
        if (!pastRecordShowed) {
            showPanel(FLAG_PANEL_PAST_RECORD);
        }
    }

    private void showPanel(String type) {
        boolean hasShowed = SettingFlags.getBooleanFlag(type, false);
        if (hasShowed) {
            return;
        }
        RecordRemindPanel panel = new RecordRemindPanel(getContext());
        if (FLAG_PANEL_PAST_RECORD.equals(type)) {
            panel.setIcon(R.drawable.record_panel_jilu_past);
            panel.setKnowShow();
        } else if (FLAG_PANEL_PEEP_UNLOCK.equals(type)) {
            panel.setIcon(R.drawable.record_panel_peep_unlock);
            panel.setGoodShow();
            //关闭panel后开始偷窥按钮动画
            panel.setOnPanelButtonClickListener(new RecordRemindPanel.PanelButtonClickListener() {
                @Override
                public void onButtonClick() {
                    tryStartPeepAnimation();
                }
            });
        } else if (FLAG_PANEL_PEEP_LOCK.equals(type)) {
            panel.setIcon(R.drawable.record_panel_peep_lock);
            panel.setKnowShow();
        }
        panel.showPanel();
        SettingFlags.setFlag(type, true);
    }
}
