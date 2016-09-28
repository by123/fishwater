package com.sjy.ttclub.record.self;

import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.record.data.RecordFeel;
import com.sjy.ttclub.bean.record.data.RecordMorePapa;
import com.sjy.ttclub.bean.record.data.RecordOnePapa;
import com.sjy.ttclub.bean.record.data.RecordOrgasm;
import com.sjy.ttclub.bean.record.data.RecordPlace;
import com.sjy.ttclub.bean.record.data.RecordPosition;
import com.sjy.ttclub.bean.record.data.RecordTime;
import com.sjy.ttclub.bean.record.data.RecordTool;
import com.sjy.ttclub.bean.record.data.RecordToy;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.record.widget.BaseChartInfo;
import com.sjy.ttclub.record.widget.RecordChartViewInfo;
import com.sjy.ttclub.record.widget.RecordDataViewInfo;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by gangqing on 2016/1/11.
 * Email:denggangqing@ta2she.com
 */
public class RecordSelfHelper {
    public static ArrayList<RecordDataViewInfo> prepareMorePapaDataViewInfoList(RecordMorePapa data) {
        ArrayList<RecordDataViewInfo> list = new ArrayList<RecordDataViewInfo>();

        RecordFeel feelling = data.getFeel();
        RecordDataViewInfo viewInfo = createMorePapaFeelViewInfo(feelling, ResourceHelper.getString(R.string.record_self_papa_feel));
        list.add(viewInfo);

        RecordOrgasm orgasm = data.getOrgasm();
        viewInfo = createMorePapaOrgasmViewInfo(orgasm, ResourceHelper.getString(R.string.record_self_papa_heat));
        list.add(viewInfo);

        RecordTime recordTime = data.getTime();
        int userSex = AccountManager.getInstance().getSex();
        viewInfo = createMorePapaTimeViewInfo(recordTime, ResourceHelper.getString(R.string.record_self_papa_time), userSex);
        list.add(viewInfo);

        RecordPosition position = data.getPosition();
        viewInfo = createMorePapaPositionViewInfo(position);
        list.add(viewInfo);

        RecordPlace place = data.getPlace();
        viewInfo = createMorePapaPlaceViewInfo(place, ResourceHelper.getString(R.string.record_self_papa_where));
        list.add(viewInfo);

        RecordToy toy = data.getToy();
        viewInfo = createMorePapaToyViewInfo(toy);
        list.add(viewInfo);

        return list;
    }

    public static ArrayList<RecordDataViewInfo> prepareOnePapaDataViewInfoList(RecordOnePapa data) {
        ArrayList<RecordDataViewInfo> list = new ArrayList<RecordDataViewInfo>();

        RecordFeel feelling = data.getFeel();
        RecordDataViewInfo viewInfo = createMorePapaFeelViewInfo(feelling, ResourceHelper.getString(R.string.record_self_one_papa_feel));
        list.add(viewInfo);

        RecordOrgasm orgasm = data.getOrgasm();
        viewInfo = createMorePapaOrgasmViewInfo(orgasm, ResourceHelper.getString(R.string.record_self_one_papa_heat));
        list.add(viewInfo);

        RecordTool tool = data.getTool();
        viewInfo = createOnePapaToolViewInfo(tool);
        list.add(viewInfo);

        int userSex = AccountManager.getInstance().getSex();
        RecordToy toy = data.getToy();
        if (CommonConst.SEX_MAN == userSex) {   //男
            viewInfo = createOnePapaToyManViewInfo(toy);
        } else {  //女
            viewInfo = createOnePapaToyWoManViewInfo(toy);
        }
        list.add(viewInfo);

        RecordPlace place = data.getPlace();
        viewInfo = createMorePapaPlaceViewInfo(place, ResourceHelper.getString(R.string.record_self_one_papa_place));
        list.add(viewInfo);

        RecordTime recordTime = data.getTime();
        int sex = AccountManager.getInstance().getSex();
        viewInfo = createMorePapaTimeViewInfo(recordTime, ResourceHelper.getString(R.string.record_self_one_papa_time), sex);
        list.add(viewInfo);

        return list;
    }

    private static RecordDataViewInfo createMorePapaFeelViewInfo(RecordFeel feelling, String title) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
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

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_happy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelGood();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_v_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelNotbad();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_unhappy_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = feelling.getFeelBad();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaOrgasmViewInfo(RecordOrgasm orgasm, String title) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_heat_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_heat);
        chartInfo.percent = orgasm.getOrgasmYes();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaTimeViewInfo(RecordTime recordTime, String title, int sex) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        viewInfo.title = title;
        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_time);
        if (CommonConst.SEX_MAN == sex) {
            chartViewInfo.textResId = R.string.time_5min;
            chartInfo.percent = recordTime.getTimeFiveMin();
        } else {
            chartViewInfo.textResId = R.string.time_15min;
            chartInfo.percent = recordTime.getTimeFiftyMin();
        }
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        if (CommonConst.SEX_MAN == sex) {
            chartViewInfo.textResId = R.string.time_10min;
            chartInfo.percent = recordTime.getTimeTenMin();
        } else {
            chartViewInfo.textResId = R.string.time_30min;
            chartInfo.percent = recordTime.getTimeThirtyMin();
        }
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        if (CommonConst.SEX_MAN == sex) {
            chartViewInfo.textResId = R.string.time_15min;
            chartInfo.percent = recordTime.getTimeFiftyMin();
        } else {
            chartViewInfo.textResId = R.string.time_1hour;
            chartInfo.percent = recordTime.getTimeOneHour();
        }
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        if (CommonConst.SEX_MAN == sex) {
            chartViewInfo.textResId = R.string.time_20min;
            chartInfo.percent = recordTime.getTimeTwentyMin();
        } else {
            chartViewInfo.textResId = R.string.time_2hour;
            chartInfo.percent = recordTime.getTimeTwoHour();
        }
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaToolViewInfo(RecordTool tool) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        viewInfo.title = ResourceHelper.getString(R.string.record_self_papa_tool);

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

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_music;
        chartViewInfo.textResId = R.string.record_self_papa_tool_music;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolVoice();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_book;
        chartViewInfo.textResId = R.string.record_self_papa_tool_book;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolNovel();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_toy_photo;
        chartViewInfo.textResId = R.string.record_self_papa_tool_image
        ;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = tool.getToolImage();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaPositionViewInfo(RecordPosition position) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        viewInfo.title = ResourceHelper.getString(R.string.record_self_papa_post);

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

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_ws_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosFemaleUpper();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_hr_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosBehind();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_cr_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosSideLying();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_other_normal;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = position.getPosOther();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaPlaceViewInfo(RecordPlace place, String title) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_POSITION;
        viewInfo.title = title;

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.chartType = BaseChartInfo.ChartType.PLACE_AVERAGE;
        chartInfo.percent = place.getPlaceHome();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceHotel();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceCar();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = place.getPlaceOutdoor();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createMorePapaToyViewInfo(RecordToy toy) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_CIRCLE;
        viewInfo.title = ResourceHelper.getString(R.string.record_self_papa_tool);

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_window_list_items_prop_normal;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_20);
        chartInfo.chartType = BaseChartInfo.ChartType.CIRCLE_SELF;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartInfo.percent = toy.getToyUse();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaToyManViewInfo(RecordToy toy) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        viewInfo.title = ResourceHelper.getString(R.string.record_self_one_papa_toy);

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_plane;
        chartViewInfo.textResId = R.string.record_self_papa_toy_plane;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartInfo.percent = toy.getToyPlaneCup();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_runhuaji;
        chartViewInfo.textResId = R.string.record_self_papa_toy_runhuaji;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyLubricant();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_baby;
        chartViewInfo.textResId = R.string.record_self_papa_toy_bady;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyDoll();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_other;
        chartViewInfo.textResId = R.string.record_self_papa_toy_other;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyOther();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

    private static RecordDataViewInfo createOnePapaToyWoManViewInfo(RecordToy toy) {
        RecordDataViewInfo viewInfo = new RecordDataViewInfo();
        viewInfo.type = RecordDataViewInfo.TYPE_BAR;
        viewInfo.title = ResourceHelper.getString(R.string.record_self_one_papa_tool);

        RecordChartViewInfo chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_bang;
        chartViewInfo.textResId = R.string.record_self_papa_toy_bang;
        BaseChartInfo chartInfo = new BaseChartInfo();
        chartInfo.width = ResourceHelper.getDimen(R.dimen.space_22);
        chartInfo.textSize = ResourceHelper.getDimen(R.dimen.space_10);
        chartInfo.chartType = BaseChartInfo.ChartType.BAR_UP;
        chartInfo.color = ResourceHelper.getColor(R.color.record_peep_chart_tools);
        chartInfo.percent = toy.getToyRockStick();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_dan;
        chartViewInfo.textResId = R.string.record_self_papa_toy_dan;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyTiaoDan();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_runhuaji;
        chartViewInfo.textResId = R.string.record_self_papa_toy_runhuaji;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyLubricant();
        chartViewInfo.chartInfoList.add(chartInfo);

        chartViewInfo = new RecordChartViewInfo();
        viewInfo.chartViewInfoList.add(chartViewInfo);
        chartViewInfo.iconResId = R.drawable.record_one_papa_toy_other;
        chartViewInfo.textResId = R.string.record_self_papa_toy_other;
        chartInfo = new BaseChartInfo(chartInfo);
        chartInfo.percent = toy.getToyOther();
        chartViewInfo.chartInfoList.add(chartInfo);

        return viewInfo;
    }

}
