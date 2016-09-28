package com.sjy.ttclub.record;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.record.Record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RecordConfig {

    public static final int RECORD_TYPE_PAPA = 1;
    public static final int RECORD_TYPE_MYSELF = 2;
    public static final int RECORD_TYPE_NOTHING = 3;
    public static final int RECORD_TYPE_DYM = 4;
    public static final int RECORD_TYPE_NO = 5;

    public static final int TYPE_FEEL = 1;
    public static final int TYPE_POSTURE = 2;
    public static final int TYPE_SCENE = 3;
    public static final int TYPE_LIVEN = 4;
    public static final int TYPE_TOOLS = 5;
    public static final int TYPE_TIME = 6;
    public static final int TYPE_HEAT = 7;
    public static final int TYPE_IS_TOOLS = 8;
    public static final int TYPE_MYSELF_WHEN = 9;
    public static final int TYPE_PAPA_WHEN = 10;

    public static final String TYPE_FEEL_EXIST = "\"11\":";
    public static final String TYPE_POSTURE_EXIST = "\"1\":";
    public static final String TYPE_SCENE_EXIST = "\"12\":";
    public static final String TYPE_LIVEN_EXIST = "\"5\":";
    public static final String TYPE_TOOLS_EXIST = "\"4\":";
    public static final String TYPE_TIME_EXIST = "\"2\":";
    public static final String TYPE_HEAT_EXIST = "\"3\":";

    public final static int mMan = 1;
    public final static int mWoman = 2;

    public final static String HEAT = "301";
    public final static String UN_HEAT = "304";
    public final static String TOOLS = "405";
    public final static String UN_TOOLS = "406";

    public static int mCurrSex, mCate, mTodayState;

    private List<Record> mListRecord = new ArrayList<>();
    private List<Record> mListRecordArray = new ArrayList<>();
    private String mChooseTime;

    //TODO 代号
    private String[] mCategory = {"11", "1", "12", "5", "4", "2", "3", "8", "9"};

    public int[] btnIcon = {R.drawable.record_window_list_items_btn};

    private static RecordConfig mRecordConfig;

    public static RecordConfig getInstance() {
        if (mRecordConfig == null) {
            mRecordConfig = new RecordConfig();
        }
        return mRecordConfig;
    }

    public void init(int sex, int cate) {
        mCate = cate;
        mCurrSex = sex;
    }

    public void updateCate(int cate) {
        mCate = cate;
    }

    public int[] getFrameWork() {
        switch (mCate) {
            case RECORD_TYPE_PAPA:
                return new int[]{TYPE_PAPA_WHEN, TYPE_FEEL, TYPE_HEAT, TYPE_TIME, TYPE_POSTURE, TYPE_SCENE, TYPE_IS_TOOLS};
            case RECORD_TYPE_MYSELF:
                return new int[]{TYPE_MYSELF_WHEN, TYPE_FEEL, TYPE_HEAT, TYPE_LIVEN, TYPE_TOOLS, TYPE_SCENE, TYPE_TIME};
            default:
                return new int[]{mCate};
        }
    }

    //助兴
    public HashMap<String, RecordBean> mLivenMap = new HashMap<>();
    public String liven[] = {"AV", "声音", "小说", "图片"};
    public String livenVal[] = {"501", "502", "503", "504"};

    public List<RecordBean> getLiven() {
        return setData(mLivenMap, livenVal);
    }

    public void initLiven() {
        setData(mLivenMap, TYPE_LIVEN, btnIcon, livenVal, liven);
    }

    //玩具
    public HashMap<String, RecordBean> mToolsMap = new HashMap<>();
    public String manTools[] = {"飞机杯", "润滑剂", "娃娃", "其他"};
    public String manToolsVal[] = {"411", "412", "413", "414"};
    public String womanTools[] = {"振动棒", "跳蛋", "润滑剂", "其他"};
    public String womanToolsVal[] = {"421", "422", "423", "424"};

    public List<RecordBean> getTools() {
        if (mCurrSex == mMan) {
            return setData(mToolsMap, manToolsVal);
        } else {
            return setData(mToolsMap, womanToolsVal);
        }
    }

    public void initTools() {
        setData(mToolsMap, TYPE_TOOLS, btnIcon, manToolsVal, manTools);
        setData(mToolsMap, TYPE_TOOLS, btnIcon, womanToolsVal, womanTools);
    }

    //时间
    public HashMap<String, RecordBean> mTimeMap = new HashMap<>();
    public String manTime[] = {"5分钟", "10分钟", "15分钟", "20分钟"};
    public String manTimeVal[] = {"204", "205", "201", "206"};
    public String womanTime[] = {"15分钟", "30分钟", "1小时", "2小时"};
    public String womanTimeVal[] = {"201", "202", "203", "207"};

    public RecordBean getTime(String value) {
        return mTimeMap.get(value);
    }

    public List<RecordBean> getTime() {
        if (mCurrSex == mMan) {
            return setData(mTimeMap, manTimeVal);
        } else {
            return setData(mTimeMap, womanTimeVal);
        }
    }

    public void initTime() {
        setData(mTimeMap, TYPE_TIME, btnIcon, manTimeVal, manTime);
        setData(mTimeMap, TYPE_TIME, btnIcon, womanTimeVal, womanTime);
    }

    //时间段
    public HashMap<String, RecordBean> mWhenMap = new HashMap<>();
    public String time[] = {"早上", "下午", "晚上"};
    //TODO 代号码
    public String timeVal[] = {"1", "2", "3"};

    public List<RecordBean> getWhen() {
        return setData(mWhenMap, timeVal);
    }

    public void initWhen() {
        setData(mWhenMap, TYPE_MYSELF_WHEN, btnIcon, timeVal, time);
    }

    //场景
    public HashMap<String, RecordBean> mSceneMap = new HashMap<>();
    public String scene[] = {"家里", "酒店", "户外", "车里"};
    public String sceneVal[] = {"1201", "1202", "1203", "1204"};

    public List<RecordBean> getScene() {
        return setData(mSceneMap, sceneVal);
    }

    public void initScene() {
        setData(mSceneMap, TYPE_SCENE, btnIcon, sceneVal, scene);
    }

    //姿势icon
    public HashMap<String, RecordBean> mPostureMap = new HashMap<>();
    public int posture[] = {R.drawable.record_window_list_items_ms, R.drawable.record_window_list_items_ws, R
            .drawable.record_window_list_items_hr, R.drawable.record_window_list_items_cr, R.drawable
            .record_window_list_items_other};
    public String postureVal[] = {"121", "122", "123", "124", "125"};
    public String postureText[] = {"", "", "", "", ""};

    public List<RecordBean> getPosture() {
        return setData(mPostureMap, postureVal);
    }

    public void initPosture() {
        setData(mPostureMap, TYPE_POSTURE, posture, postureVal, postureText);
    }

    //感觉icon
    public HashMap<String, RecordBean> mFeelMap = new HashMap<>();
    public int feel[] = {R.drawable.record_window_list_items_v_happy, R.drawable.record_window_list_items_happy, R
            .drawable.record_window_list_items_v_unhappy, R.drawable.record_window_list_items_unhappy};
    public String feelVal[] = {"1101", "1102", "1103", "1104"};
    public String feelValText[] = {"", "", "", "", ""};

    public RecordBean getFeel(String value) {
        return mFeelMap.get(value);
    }

    public List<RecordBean> getFeel() {
        return setData(mFeelMap, feelVal);
    }

    public void initFeel() {
        setData(mFeelMap, TYPE_FEEL, feel, feelVal, feelValText);
    }

    /**
     * @param hashMap
     * @param value   排序用
     * @return
     */
    private List<RecordBean> setData(HashMap<String, RecordBean> hashMap, String[] value) {
        List<RecordBean> list = new ArrayList<>();
        for (int i = 0; i < value.length; i++) {
            Iterator iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                if (value[i].equals(key)) {
                    list.add(i, hashMap.get(key));
                }
            }
        }
        return list;
    }

    private void setData(HashMap<String, RecordBean> hashMaps, int category, int[] icon, String[] value, String[]
            text) {
        RecordBean recordBean;
        for (int i = 0; i < value.length; i++) {
            recordBean = new RecordBean();
            if (icon.length > 1) {
                recordBean.setIcon(icon[i]);
            } else {
                recordBean.setIcon(icon[0]);
            }
            recordBean.setSex(mCurrSex);
            recordBean.setText(text[i]);
            recordBean.setValue(value[i]);
            recordBean.setCategory(category);
            hashMaps.put(value[i], recordBean);
        }
    }

    public void putArray(int key, String val) {
        Record record = new Record();
        record.setCate(mCategory[key - 1]);
        record.setVal(val);
        mListRecordArray.add(record);
        put(key, val);
    }

    public void removeArray(int key, String val) {
        for (int i = 0; i < mListRecordArray.size(); i++) {
            Record record = mListRecordArray.get(i);
            if (record.getCate().equals(mCategory[key - 1]) && record.getVal().equals(val)) {
                mListRecordArray.remove(i);
            }
        }
        remove(key, val);
    }

    public void put(int key, String val) {
        Record record = new Record();
        record.setCate(mCategory[key - 1]);
        record.setVal(val);
        mListRecord.add(record);
    }

    public void remove(int key, String val) {
        for (int i = 0; i < mListRecord.size(); i++) {
            Record record = mListRecord.get(i);
            if (record.getCate().equals(mCategory[key - 1]) && record.getVal().equals(val)) {
                mListRecord.remove(i);
            }
        }
    }

    public String getChooseTime() {
        return mChooseTime;
    }

    public String getJsonData() {
        JSONObject json = new JSONObject();
        JSONObject jsonRecord = new JSONObject();
        try {
            json.put("sex", mCurrSex);
            json.put("cate", mCate);
            for (int i = 0; i < mListRecord.size(); i++) {
                Record record = mListRecord.get(i);
                if (record.getCate().equals(mCategory[TYPE_POSTURE - 1]) || record.getCate().equals
                        (mCategory[TYPE_TOOLS - 1]) || record.getCate().equals(mCategory[TYPE_LIVEN - 1])) {
                    JSONArray jsonArray = new JSONArray();
                    for (int j = 0; j < mListRecordArray.size(); j++) {
                        if (record.getCate().equals(mListRecordArray.get(j).getCate())) {
                            jsonArray.put(mListRecordArray.get(j).getVal());
                        }
                    }
                    jsonRecord.put(record.getCate(), jsonArray);
                } else if (record.getCate().equals(mCategory[TYPE_MYSELF_WHEN - 1])) {
                    mChooseTime = record.getVal();
                } else {
                    if (record.getCate().equals(mCategory[TYPE_IS_TOOLS - 1])) {
                        jsonRecord.put(mCategory[TYPE_TOOLS - 1], record.getVal());//玩具多选、是否玩具合并
                    } else {
                        jsonRecord.put(record.getCate(), record.getVal());
                    }
                }
            }
            json.put("record", jsonRecord);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void clearListRecord() {
        mListRecord.clear();
        mListRecordArray.clear();
    }

    public void destroy() {
        mWhenMap.clear();
        mListRecord.clear();
        mListRecordArray.clear();
        mFeelMap.clear();
        mLivenMap.clear();
        mPostureMap.clear();
        mSceneMap.clear();
        mTimeMap.clear();
        mToolsMap.clear();
    }

    public class RecordBean {
        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        private int icon;
        private int sex;
        private String text;
        private String value;
        private int category;
    }
}
