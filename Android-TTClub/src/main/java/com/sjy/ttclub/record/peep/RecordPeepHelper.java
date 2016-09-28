package com.sjy.ttclub.record.peep;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.data.RecordFeel;
import com.sjy.ttclub.bean.record.data.RecordMorePapa;
import com.sjy.ttclub.bean.record.data.RecordNoPapa;
import com.sjy.ttclub.bean.record.data.RecordOnePapa;
import com.sjy.ttclub.bean.record.data.RecordOrgasm;
import com.sjy.ttclub.bean.record.data.RecordPeepAverageData;
import com.sjy.ttclub.bean.record.data.RecordPeepData;
import com.sjy.ttclub.bean.record.data.RecordPeepSelfData;
import com.sjy.ttclub.bean.record.data.RecordPlace;
import com.sjy.ttclub.bean.record.data.RecordPosition;
import com.sjy.ttclub.bean.record.data.RecordTime;
import com.sjy.ttclub.bean.record.data.RecordTool;
import com.sjy.ttclub.bean.record.data.RecordToy;
import com.sjy.ttclub.record.RecordConst;
import com.sjy.ttclub.record.widget.BaseChartInfo;
import com.sjy.ttclub.record.widget.RecordChartViewInfo;
import com.sjy.ttclub.record.widget.RecordDataViewInfo;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.ToastHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/9.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPeepHelper {
    private static final String HOLDER_TIME = "#time#";
    private static final String HOLDER_SEX = "#sex#";
    private static final String HOLDER_ITEM = "#item#";

    private static final String RECORD_DAYS_SEX = "5";
    private static final String RECORD_DAYS_MARRIAGE_LEVEL_2 = "7";
    private static final String RECORD_DAYS_MARRIAGE_LEVEL_3 = "9";
    private static final String RECORD_DAYS_SHOW_ME = "10";
    private static final String RECORD_DAYS_LAST_WEEK = "15";
    private static final String RECORD_DAYS_LAST_MOUTH = "30";

    public static void toastShowMeLimit() {
        String text = ResourceHelper.getString(R.string.record_peep_permission_limit);
        String showMe = ResourceHelper.getString(R.string.record_peep_show_me);
        text = text.replace(HOLDER_ITEM, showMe);
        text = text.replace(HOLDER_TIME, RECORD_DAYS_SHOW_ME);
        ToastHelper.showToast(text);
    }

    public static void toastShowMeSexLimit(int sex) {
        String text = ResourceHelper.getString(R.string.record_peep_permission_show_me);
        String sexString = getPeepSex(sex);
        text = text.replace(HOLDER_SEX, sexString);
        ToastHelper.showToast(text);
    }

    public static void toastSexLimit(String sex) {
        String text = ResourceHelper.getString(R.string.record_peep_permission_limit);
        text = text.replace(HOLDER_ITEM, sex);
        text = text.replace(HOLDER_TIME, RECORD_DAYS_SEX);
        ToastHelper.showToast(text);
    }

    public static void toastMarriageLimit(int marriage, ArrayList<String> permissionList) {
        String text = ResourceHelper.getString(R.string.record_peep_permission_limit);
        if (marriage == RecordConst.PEEP_MARRIAGE_SINGLE) {
            String marriageString = ResourceHelper.getString(R.string.record_peep_marriage_single);
            text = text.replace(HOLDER_ITEM, marriageString);
            text = text.replace(HOLDER_TIME, RECORD_DAYS_MARRIAGE_LEVEL_2);
        } else if (marriage == RecordConst.PEEP_MARRIAGE_INLOVE) {
            String marriageString = ResourceHelper.getString(R.string.record_peep_marriage_inlove);
            text = text.replace(HOLDER_ITEM, marriageString);
            if (permissionList.contains(String.valueOf(RecordConst.PEEP_MARRIAGE_SINGLE)) &&
                    permissionList.contains(String.valueOf(RecordConst.PEEP_MARRIAGE_MARRIAGED))) {
                text = text.replace(HOLDER_TIME, RECORD_DAYS_MARRIAGE_LEVEL_3);
            } else if (permissionList.contains(String.valueOf(RecordConst.PEEP_MARRIAGE_SINGLE)) && !permissionList
                    .contains(String.valueOf(RecordConst.PEEP_MARRIAGE_MARRIAGED))) {
                text = text.replace(HOLDER_TIME, RECORD_DAYS_MARRIAGE_LEVEL_2);
            } else {
                text = text.replace(HOLDER_TIME, RECORD_DAYS_MARRIAGE_LEVEL_3);
            }
        } else {
            String marriageString = ResourceHelper.getString(R.string.record_peep_marriage_marriaged);
            text = text.replace(HOLDER_ITEM, marriageString);
            text = text.replace(HOLDER_TIME, RECORD_DAYS_MARRIAGE_LEVEL_3);
        }
        ToastHelper.showToast(text);
    }

    public static void toastTimeLimit(int time) {
        String text = ResourceHelper.getString(R.string.record_peep_permission_limit);
        if (time == RecordConst.PEEP_TIME_LAST_WEEK) {
            String itemText = ResourceHelper.getString(R.string.record_peep_time_last_week);
            text = text.replace(HOLDER_ITEM, itemText);
            text = text.replace(HOLDER_TIME, RECORD_DAYS_LAST_WEEK);
        } else if (time == RecordConst.PEEP_TIME_LAST_MONTH) {
            String itemText = ResourceHelper.getString(R.string.record_peep_time_last_month);
            text = text.replace(HOLDER_ITEM, itemText);
            text = text.replace(HOLDER_TIME, RECORD_DAYS_LAST_MOUTH);
        }
        ToastHelper.showToast(text);
    }

    public static String getPeepSex(int sex) {
        if (sex == RecordConst.PEEP_SEX_MAN) {
            return ResourceHelper.getString(R.string.record_peep_sex_boy);
        }
        return ResourceHelper.getString(R.string.record_peep_sex_girl);
    }

    public static String getPeepTime(int time, String specTime) {
        String timeString = specTime;
        if (time == RecordConst.PEEP_TIME_YESTODAY) {
            timeString = ResourceHelper.getString(R.string.record_peep_time_yesterday);
        } else if (time == RecordConst.PEEP_TIME_LAST_WEEK) {
            timeString = ResourceHelper.getString(R.string.record_peep_time_last_week);
        } else if (time == RecordConst.PEEP_TIME_LAST_MONTH) {
            timeString = ResourceHelper.getString(R.string.record_peep_time_last_month);
        }
        return timeString;
    }

    public static ArrayList<RecordDataViewInfo> prepareMorePapaDataViewInfoList(RecordPeepData data, String sex,
                                                                                String time, boolean showMe) {

        RecordPeepAverageData averageData = data.getPeepData();
        RecordPeepSelfData selfData = data.getMyData();

        RecordMorePapa morePapa = averageData.getMorePapa();
        RecordMorePapa selfPapa = selfData.getMorePapa();
        ArrayList<RecordDataViewInfo> list = new ArrayList<RecordDataViewInfo>();
        RecordDataViewInfo viewInfo = null;
        RecordFeel feeling = morePapa.getFeel();
        if (feeling != null) {
            RecordFeel myFeeling = selfPapa.getFeel();
            viewInfo = createMorePapaFeelViewInfo(feeling, myFeeling, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordOrgasm orgasm = morePapa.getOrgasm();
        if (orgasm != null) {
            RecordOrgasm myOrgasm = selfPapa.getOrgasm();
            viewInfo = createMorePapaOrgasmViewInfo(orgasm, myOrgasm, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordTime recordTime = morePapa.getTime();
        if (recordTime != null) {
            RecordTime myTime = selfPapa.getTime();
            viewInfo = createMorePapaTimeViewInfo(recordTime, myTime, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordPosition position = morePapa.getPosition();
        if (position != null) {
            RecordPosition myPosition = selfPapa.getPosition();
            viewInfo = createMorePapaPositionViewInfo(position, myPosition, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordPlace place = morePapa.getPlace();
        if (place != null) {
            RecordPlace myPlace = selfPapa.getPlace();
            viewInfo = createMorePapaPlaceViewInfo(place, myPlace, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordToy toy = morePapa.getToy();
        if (toy != null) {
            RecordToy myToy = selfPapa.getToy();
            viewInfo = createMorePapaToyViewInfo(toy, myToy, sex, time, showMe);
            list.add(viewInfo);
        }

        return list;
    }

    private static RecordDataViewInfo createMorePapaFeelViewInfo(RecordFeel feelling, RecordFeel myFeel, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_papa_feel);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_happy_v_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartInfo.percent = feelling.getFeelGreat();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelGreat();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_happy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelGood();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelGood();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_v_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelNotbad();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelNotbad();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelBad();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelBad();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaOrgasmViewInfo(RecordOrgasm orgasm, RecordOrgasm myOrgasm, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        String title = ResourceHelper.getString(R.string.record_peep_papa_orgasm);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_heat_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        BaseChartInfo.ChartType type = showMe ? BaseChartInfo.ChartType.CIRCLE_AVERAGE : BaseChartInfo.ChartType
                .CIRCLE_SELF;
        chartInfo.chartType = type;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_heat);
        chartInfo.percent = orgasm.getOrgasmYes();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
            chartInfo.percent = myOrgasm.getOrgasmYes();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaTimeViewInfo(RecordTime recordTime, RecordTime myTime, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_papa_time);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;
        boolean isMan = ResourceHelper.getString(R.string.record_peep_sex_boy).equals(sex);

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_5min;
        } else {
            chartViewInfo.textResId = R.string.time_15min;
        }
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeFiveMin();
        } else {
            chartInfo.percent = recordTime.getTimeFiftyMin();
        }
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeFiveMin();
            } else {
                chartInfo.percent = myTime.getTimeFiftyMin();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_10min;
        } else {
            chartViewInfo.textResId = R.string.time_30min;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeTenMin();
        } else {
            chartInfo.percent = recordTime.getTimeThirtyMin();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeTenMin();
            } else {
                chartInfo.percent = myTime.getTimeThirtyMin();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_15min;
        } else {
            chartViewInfo.textResId = R.string.time_1hour;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeFiftyMin();
        } else {
            chartInfo.percent = recordTime.getTimeOneHour();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeFiftyMin();
            } else {
                chartInfo.percent = myTime.getTimeOneHour();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_20min;
        } else {
            chartViewInfo.textResId = R.string.time_2hour;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeTwentyMin();
        } else {
            chartInfo.percent = recordTime.getTimeTwoHour();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeTwentyMin();
            } else {
                chartInfo.percent = myTime.getTimeTwoHour();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaPositionViewInfo(RecordPosition position, RecordPosition myPosition,
                                                                     String sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_papa_position);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_ms_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_DOWN;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartInfo.percent = position.getPosMaleUpper();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPosition.getPosMaleUpper();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_ws_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosFemaleUpper();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPosition.getPosFemaleUpper();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_hr_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosBehind();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPosition.getPosBehind();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_cr_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosSideLying();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPosition.getPosSideLying();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_other_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosOther();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPosition.getPosOther();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaPlaceViewInfo(RecordPlace place, RecordPlace myPlace, String sex,
                                                                  String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_POSITION;
        String title = ResourceHelper.getString(R.string.record_peep_papa_place);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.chartType = BaseChartInfo.ChartType.PLACE_AVERAGE;
        chartInfo.percent = place.getPlaceHome();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceHome();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceHotel();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceHotel();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceCar();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo();
            chartInfo.percent = myPlace.getPlaceCar();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceOutdoor();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceOutdoor();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaToyViewInfo(RecordToy toy, RecordToy myToy, String sex, String
            time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        String title = ResourceHelper.getString(R.string.record_peep_papa_toy);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_heat_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        BaseChartInfo.ChartType type = showMe ? BaseChartInfo.ChartType.CIRCLE_AVERAGE : BaseChartInfo.ChartType
                .CIRCLE_SELF;
        chartInfo.chartType = type;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartInfo.percent = toy.getToyUse();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myToy.getToyUse();
            chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    public static ArrayList<RecordDataViewInfo> prepareOnePapaDataViewInfoList(RecordPeepData data, String sex,
                                                                               String time, boolean showMe) {

        RecordPeepAverageData averageData = data.getPeepData();
        RecordPeepSelfData selfData = data.getMyData();

        RecordOnePapa averagePapa = averageData.getOnePapa();
        RecordOnePapa selfPapa = selfData.getOnePapa();
        ArrayList<RecordDataViewInfo> list = new ArrayList<RecordDataViewInfo>();
        RecordDataViewInfo viewInfo = null;
        RecordFeel feeling = averagePapa.getFeel();
        if (feeling != null) {
            RecordFeel myFeeling = selfPapa.getFeel();
            viewInfo = createOnePapaFeelViewInfo(feeling, myFeeling, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordOrgasm orgasm = averagePapa.getOrgasm();
        if (orgasm != null) {
            RecordOrgasm myOrgasm = selfPapa.getOrgasm();
            viewInfo = createOnePapaOrgasmViewInfo(orgasm, myOrgasm, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordTool tool = averagePapa.getTool();
        if (tool != null) {
            RecordTool myTool = selfPapa.getTool();
            viewInfo = createOnePapaToolViewInfo(tool, myTool, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordToy toy = averagePapa.getToy();
        if (toy != null) {
            RecordToy myTol = selfPapa.getToy();
            viewInfo = createOnePapaToyViewInfo(toy, myTol, sex, time, showMe);
            list.add(viewInfo);
        }

        RecordTime recordTime = averagePapa.getTime();
        if (recordTime != null) {
            RecordTime myTime = selfPapa.getTime();
            viewInfo = createOnePapaTimeViewInfo(recordTime, myTime, sex, time, showMe);
            list.add(viewInfo);
        }


        RecordPlace place = averagePapa.getPlace();
        if (place != null) {
            RecordPlace myPlace = selfPapa.getPlace();
            viewInfo = createOnePapaPlaceViewInfo(place, myPlace, sex, time, showMe);
            list.add(viewInfo);
        }

        return list;
    }

    private static RecordDataViewInfo createOnePapaFeelViewInfo(RecordFeel feel, RecordFeel myFeel, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_feel);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_happy_v_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartInfo.percent = feel.getFeelGreat();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelGreat();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_happy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feel.getFeelGood();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelGood();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_v_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feel.getFeelNotbad();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelNotbad();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feel.getFeelBad();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_felling);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myFeel.getFeelBad();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaOrgasmViewInfo(RecordOrgasm orgasm, RecordOrgasm myOrgasm, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_orgasm);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_heat_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        BaseChartInfo.ChartType type = showMe ? BaseChartInfo.ChartType.CIRCLE_AVERAGE : BaseChartInfo.ChartType
                .CIRCLE_SELF;
        chartInfo.chartType = type;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_heat);
        chartInfo.percent = orgasm.getOrgasmYes();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartInfo.percent = myOrgasm.getOrgasmYes();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaToolViewInfo(RecordTool tool, RecordTool myTool, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_tool);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;


        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_video;
        chartViewInfo.textResId = R.string.record_self_papa_tool_av;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_DOWN;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartInfo.percent = tool.getToolAv();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myTool.getToolAv();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_music;
        chartViewInfo.textResId = R.string.record_self_papa_tool_music;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolVoice();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myTool.getToolVoice();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_book;
        chartViewInfo.textResId = R.string.record_self_papa_tool_book;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolNovel();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myTool.getToolNovel();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_photo;
        chartViewInfo.textResId = R.string.record_self_papa_tool_image;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolImage();
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_liven);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myTool.getToolImage();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }


    private static RecordDataViewInfo createOnePapaToyViewInfo(RecordToy toy, RecordToy myToy, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_toy);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        boolean isMan = ResourceHelper.getString(R.string.record_peep_sex_boy).equals(sex);
        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_plane;
            chartViewInfo.textResId = R.string.record_self_papa_toy_plane;
        } else {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_bang;
            chartViewInfo.textResId = R.string.record_self_papa_toy_bang;
        }
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        if (isMan) {
            chartInfo.percent = toy.getToyPlaneCup();
        } else {
            chartInfo.percent = toy.getToyRockStick();
        }
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myToy.getToyPlaneCup();
            } else {
                chartInfo.percent = myToy.getToyRockStick();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_runhuaji;
            chartViewInfo.textResId = R.string.record_self_papa_toy_runhuaji;
        } else {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_dan;
            chartViewInfo.textResId = R.string.record_self_papa_toy_dan;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        if (isMan) {
            chartInfo.percent = toy.getToyLubricant();
        } else {
            chartInfo.percent = toy.getToyTiaoDan();
        }
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myToy.getToyLubricant();
            } else {
                chartInfo.percent = myToy.getToyTiaoDan();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }


        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_baby;
            chartViewInfo.textResId = R.string.record_self_papa_toy_bady;
        } else {
            chartViewInfo.iconResId = R.drawable.record_one_papa_toy_runhuaji;
            chartViewInfo.textResId = R.string.record_self_papa_toy_runhuaji;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = toy.getToyDoll();
        } else {
            chartInfo.percent = toy.getToyLubricant();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myToy.getToyDoll();
            } else {
                chartInfo.percent = myToy.getToyLubricant();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_other;
        chartViewInfo.textResId = R.string.record_self_papa_toy_other;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartInfo.percent = toy.getToyOther();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myToy.getToyOther();
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaPlaceViewInfo(RecordPlace place, RecordPlace myPlace, String sex,
                                                                 String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_POSITION;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_place);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.chartType = BaseChartInfo.ChartType.PLACE_AVERAGE;
        chartInfo.percent = place.getPlaceHome();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceHome();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceHotel();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceHotel();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceCar();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceCar();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceOutdoor();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.percent = myPlace.getPlaceOutdoor();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaTimeViewInfo(RecordTime recordTime, RecordTime myTime, String
            sex, String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        String title = ResourceHelper.getString(R.string.record_peep_one_papa_time);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        boolean isMan = ResourceHelper.getString(R.string.record_peep_sex_boy).equals(sex);
        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_5min;
        } else {
            chartViewInfo.textResId = R.string.time_15min;
        }
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeFiveMin();
        } else {
            chartInfo.percent = recordTime.getTimeFiftyMin();
        }
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeFiveMin();
            } else {
                chartInfo.percent = myTime.getTimeFiftyMin();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_10min;
        } else {
            chartViewInfo.textResId = R.string.time_30min;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeTenMin();
        } else {
            chartInfo.percent = recordTime.getTimeThirtyMin();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeTenMin();
            } else {
                chartInfo.percent = myTime.getTimeThirtyMin();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_15min;
        } else {
            chartViewInfo.textResId = R.string.time_1hour;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeFiftyMin();
        } else {
            chartInfo.percent = recordTime.getTimeOneHour();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeFiftyMin();
            } else {
                chartInfo.percent = myTime.getTimeOneHour();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        if (isMan) {
            chartViewInfo.textResId = R.string.time_20min;
        } else {
            chartViewInfo.textResId = R.string.time_2hour;
        }
        chartInfo = new BaseChartInfo(chartInfo);
        if (isMan) {
            chartInfo.percent = recordTime.getTimeTwentyMin();
        } else {
            chartInfo.percent = recordTime.getTimeTwoHour();
        }
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            if (isMan) {
                chartInfo.percent = myTime.getTimeTwentyMin();
            } else {
                chartInfo.percent = myTime.getTimeTwoHour();
            }
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

    public static ArrayList<RecordDataViewInfo> prepareNoPapaDataViewInfoList(RecordPeepData data, String sex,
                                                                              String time, boolean showMe) {
        RecordPeepAverageData averageData = data.getPeepData();
        RecordPeepSelfData selfData = data.getMyData();

        ArrayList<RecordDataViewInfo> list = new ArrayList<RecordDataViewInfo>();
        RecordNoPapa averagePapa = averageData.getNoPapa();
        RecordNoPapa selfPapa = selfData.getNoPapa();
        RecordDataViewInfo viewInfo = createNoPapaViewInfo(averagePapa, selfPapa, sex, time, showMe);
        list.add(viewInfo);
        return list;
    }

    private static RecordDataViewInfo createNoPapaViewInfo(RecordNoPapa noPapa, RecordNoPapa myNoPapa, String sex,
                                                           String time, boolean showMe) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        String title = ResourceHelper.getString(R.string.record_peep_data_no_papa);
        title = title.replace(HOLDER_SEX, sex);
        title = title.replace(HOLDER_TIME, time);
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_peep_data_no_papa;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        chartInfo.chartType = showMe ? BaseChartInfo.ChartType.CIRCLE_AVERAGE : BaseChartInfo.ChartType.CIRCLE_SELF;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_no_papa);
        chartInfo.percent = noPapa.getPercent();
        chartViewInfo.chartInfoList.add(chartInfo);
        if (showMe) {
            chartInfo = new BaseChartInfo(chartInfo);
            chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
            chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_self);
            chartInfo.percent = myNoPapa.getPercent();
            chartViewInfo.chartInfoList.add(chartInfo);
        }

        return viewInfo;
    }

}
