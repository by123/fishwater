package com.sjy.ttclub.record.datepicker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.RecordDataMonth;
import com.sjy.ttclub.record.datepicker.bizs.calendars.DPCManager;
import com.sjy.ttclub.record.datepicker.bizs.decors.DPDecor;
import com.sjy.ttclub.record.datepicker.bizs.languages.DPLManager;
import com.sjy.ttclub.record.datepicker.bizs.themes.DPTManager;
import com.sjy.ttclub.record.datepicker.cons.DPMode;
import com.sjy.ttclub.record.datepicker.utils.MeasureUtil;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * DatePicker
 *
 * @author AigeStudio 2015-06-29
 */
public class DatePicker extends FrameLayout {
    private DPTManager mTManager;// 主题管理器
    private DPLManager mLManager;// 语言管理器

    public MonthView monthView;// 月视图
    public TextView tvYear, tvMonth;// 年份 月份显示

    private OnDateSelectedListener onDateSelectedListener;// 日期多选后监听

    private final String TYPE_PAPA = "1", TYPE_MYSELF = "2", TYPE_NOTHING = "3", TYPE_DYM = "4";

    private MonthView.OnDateChangeListener mDateChangeListener;

    /**
     * 日期单选监听器
     */
    public interface OnDatePickedListener {
        void onDatePicked(String date);
    }

    /**
     * 日期多选监听器
     */
    public interface OnDateSelectedListener {
        void onDateSelected(List<String> date);
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTManager = DPTManager.getInstance();
        mLManager = DPLManager.getInstance();

        // 设置排列方向为竖向
//        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.record_date_view_bg));

        int height = MeasureUtil.dp2px(getContext(), 75);
        LayoutParams llParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        LayoutParams monthParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams commonParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        monthParams.setMargins(0, height, 0, 0);

        // ------------------------------------------------------------------------------------月视图
        monthView = new MonthView(context);
        monthView.setOnDateChangeListener(new MonthView.OnDateChangeListener() {
            @Override
            public void onMonthChange(int month) {
                tvMonth.setText(mLManager.titleMonth()[month - 1]);
                if (mDateChangeListener != null) {
                    mDateChangeListener.onMonthChange(month);
                }
            }

            @Override
            public void onYearChange(int year) {
                String tmp = String.valueOf(year);
                if (tmp.startsWith("-")) {
                    tmp = tmp.replace("-", mLManager.titleBC());
                }
                tvYear.setText(tmp + "年");
                if (mDateChangeListener != null) {
                    mDateChangeListener.onYearChange(year);
                }
            }
        });
        addView(monthView, monthParams);

        LinearLayout rlTop = new LinearLayout(context);
        rlTop.setOrientation(LinearLayout.VERTICAL);

        // 标题栏根布局
        LinearLayout rlTitle = new LinearLayout(context);
        rlTitle.setOrientation(LinearLayout.HORIZONTAL);
        rlTitle.setBackgroundColor(getResources().getColor(R.color.record_date_view_bg));
        int rlTitlePadding = MeasureUtil.dp2px(context, 10);
        rlTitle.setPadding(rlTitlePadding, rlTitlePadding, rlTitlePadding, rlTitlePadding);

        // 周视图根布局
        LinearLayout llWeek = new LinearLayout(context);
        llWeek.setBackgroundColor(getResources().getColor(R.color.record_date_view_bg));//record_date_view_bg
        llWeek.setOrientation(LinearLayout.HORIZONTAL);
        int llWeekPadding = MeasureUtil.dp2px(context, 5);
        llWeek.setPadding(0, llWeekPadding * 2, 0, 0);
        LinearLayout.LayoutParams lpWeek = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;

        // 标题栏子元素布局参数
        LinearLayout.LayoutParams lpYear = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f);
        LinearLayout.LayoutParams lpMonth = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f);

        // --------------------------------------------------------------------------------标题栏
        // 年份显示
        tvYear = new TextView(context);
//        tvYear.setText("2015");
        tvYear.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tvYear.setGravity(Gravity.RIGHT | Gravity.CENTER);
        tvYear.setTextColor(getResources().getColor(R.color.record_new_date_view_text_normal));

        // 月份显示
        tvMonth = new TextView(context);
//        tvMonth.setText("六月");
        tvMonth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tvMonth.setGravity(Gravity.LEFT | Gravity.CENTER);
        tvMonth.setTextColor(getResources().getColor(R.color.record_new_date_view_text_normal));

        rlTitle.addView(tvYear, lpYear);
        rlTitle.addView(tvMonth, lpMonth);

        rlTop.addView(rlTitle, commonParams);

        LinearLayout.LayoutParams lines = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_height));
        View view = new View(context);
        int padding = getResources().getDimensionPixelSize(R.dimen.space_10);
        view.setPadding(padding, 0, padding, padding);
        view.setBackgroundColor(getResources().getColor(R.color.record_date_view_stroke));
        rlTop.addView(view, lines);

        // --------------------------------------------------------------------------------周视图
        for (int i = 0; i < mLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(context);
            tvWeek.setText(mLManager.titleWeek()[i]);
            tvWeek.setGravity(Gravity.CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvWeek.setTextColor(getResources().getColor(R.color.record_new_date_view_week));
            llWeek.addView(tvWeek, lpWeek);
        }
        rlTop.addView(llWeek, commonParams);

        addView(rlTop, llParams);
    }

    public void updateData(List<RecordDataMonth> monthList) {
        DPCManager.getInstance().recycle();
        List<String> tmpDYM = new ArrayList<>();
        List<String> tmpMyself = new ArrayList<>();
        List<String> tmpPAPA = new ArrayList<>();
        List<String> tmpNothing = new ArrayList<>();
        for (RecordDataMonth dataMonth : monthList) {
            if (dataMonth.getCategory().equals(TYPE_DYM)) {
                tmpDYM.add(getTime(dataMonth.getDay()));
                DPCManager.getInstance().setDecorTR(tmpDYM);
            } else if (dataMonth.getCategory().equals(TYPE_NOTHING)) {
                tmpNothing.add(getTime(dataMonth.getDay()));
                DPCManager.getInstance().setDecorR(tmpNothing);
            } else if (dataMonth.getCategory().equals(TYPE_MYSELF)) {
                tmpMyself.add(getTime(dataMonth.getDay()));
                DPCManager.getInstance().setDecorT(tmpMyself);
            } else if (dataMonth.getCategory().equals(TYPE_PAPA)) {
                tmpPAPA.add(getTime(dataMonth.getDay()));
                DPCManager.getInstance().setDecorBG(tmpPAPA);
            }
        }
        setDPDecor(monthView.dpDecor);
        invalidate();
    }

    private String getTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        if (StringUtils.parseInt(day) < 10) {
            day = day.substring(1);
        }
        if (StringUtils.parseInt(month) < 10) {
            month = month.substring(1);
        }
        StringBuffer time = new StringBuffer();
        time.append(year);
        time.append("-");
        time.append(month);
        time.append("-");
        time.append(day);
        return String.valueOf(time);
    }

    /**
     * 设置初始化年月日期
     *
     * @param year  ...
     * @param month ...
     */
    public void setDate(int year, int month) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        monthView.setDate(year, month);
    }

    public void setDPDecor(DPDecor decor) {
        monthView.setDPDecor(decor);
    }

    public void onAnimate(boolean isShow) {
        monthView.onAnimate(isShow);
    }

    /**
     * 设置日期选择模式
     *
     * @param mode ...
     */
    public void setMode(DPMode mode) {
        if (mode != DPMode.MULTIPLE) {
//            tvEnsure.setVisibility(GONE);
        }
        monthView.setDPMode(mode);
    }

    /**
     * 设置单选监听器
     *
     * @param onDatePickedListener ...
     */
    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
//        if (monthView.getDPMode() != DPMode.SINGLE) {
//            throw new RuntimeException(
//                    "Current DPMode does not SINGLE! Please call setMode set DPMode to SINGLE!");
//        }
        monthView.setOnDatePickedListener(onDatePickedListener);
    }

    public void setOnDateChangeListener(final MonthView.OnDateChangeListener dateChangeListener) {
        mDateChangeListener = dateChangeListener;
    }

    /**
     * 设置多选监听器
     *
     * @param onDateSelectedListener ...
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        if (monthView.getDPMode() != DPMode.MULTIPLE) {
            throw new RuntimeException(
                    "Current DPMode does not MULTIPLE! Please call setMode set DPMode to MULTIPLE!");
        }
        this.onDateSelectedListener = onDateSelectedListener;
    }
}
