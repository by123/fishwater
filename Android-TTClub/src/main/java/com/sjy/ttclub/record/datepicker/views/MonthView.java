package com.sjy.ttclub.record.datepicker.views;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.lsym.ttclub.R;
import com.sjy.ttclub.record.datepicker.bizs.calendars.DPCManager;
import com.sjy.ttclub.record.datepicker.bizs.decors.DPDecor;
import com.sjy.ttclub.record.datepicker.bizs.themes.DPTManager;
import com.sjy.ttclub.record.datepicker.cons.DPMode;
import com.sjy.ttclub.record.datepicker.entities.DPInfo;
import com.sjy.ttclub.record.datepicker.utils.MeasureUtil;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MonthView
 *
 * @author AigeStudio 2015-06-29
 */
public class MonthView extends View {
    private final Region[][] MONTH_REGIONS_4 = new Region[4][7];
    private final Region[][] MONTH_REGIONS_5 = new Region[5][7];
    private final Region[][] MONTH_REGIONS_6 = new Region[6][7];

    private final DPInfo[][] INFO_4 = new DPInfo[4][7];
    private final DPInfo[][] INFO_5 = new DPInfo[5][7];
    private final DPInfo[][] INFO_6 = new DPInfo[6][7];

    private final Map<String, List<Region>> REGION_SELECTED = new HashMap<>();

    private DPCManager mCManager = DPCManager.getInstance();
    private DPTManager mTManager = DPTManager.getInstance();

    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
    private Scroller mScroller;
    private OnDateChangeListener onDateChangeListener;
    private DatePicker.OnDatePickedListener onDatePickedListener;
    private ScaleAnimationListener scaleAnimationListener;

    private DPMode mDPMode = DPMode.MULTIPLE;
    private SlideMode mSlideMode;
    private DPDecor mDPDecor;

    private static float mInitY = 0;
    private boolean mCurrState = true;
    private int mCurrRow = 0;

    private int circleRadius;
    private int indexYear, indexMonth;
    private int centerYear, centerMonth;
    private int leftYear, leftMonth;
    private int rightYear, rightMonth;
    private int topYear, topMonth;
    private int bottomYear, bottomMonth;
    private int width, height;
    private int sizeDecor, sizeDecor2x, sizeDecor3x;
    private int lastPointX, lastPointY;
    private int lastMoveX, lastMoveY;
    private int criticalWidth, criticalHeight;
    private Boolean hasSelected = false;

    private float sizeTextGregorian, sizeTextFestival;
    private float offsetYFestival1, offsetYFestival2;

    private boolean isNewEvent,
            isFestivalDisplay = false,
            isHolidayDisplay = false,
            isTodayDisplay = true,
            isDeferredDisplay = false;

    private List<String> dateSelected = new ArrayList<>();

    public MonthView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            scaleAnimationListener = new ScaleAnimationListener();
        }
        mScroller = new Scroller(context);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mSlideMode = null;
                isNewEvent = true;
                lastPointX = (int) event.getX();
                lastPointY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isNewEvent) {
                    if (Math.abs(lastPointX - event.getX()) > 100) {
                        mSlideMode = SlideMode.HOR;
                        isNewEvent = false;
                    } else if (Math.abs(lastPointY - event.getY()) > 50) {
                        mSlideMode = SlideMode.VER;
                        isNewEvent = false;
                    }
                }
                if (mSlideMode == SlideMode.HOR) {
                    int totalMoveX = (int) (lastPointX - event.getX()) + lastMoveX;
                    smoothScrollTo(totalMoveX, indexYear * height);
                } else if (mSlideMode == SlideMode.VER) {
                    int totalMoveY = (int) (lastPointY - event.getY()) + lastMoveY;
                    smoothScrollTo(width * indexMonth, totalMoveY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mSlideMode == SlideMode.VER) {
                    if (Math.abs(lastPointY - event.getY()) > 25) {
                        if (lastPointY < event.getY()) {
                            if (Math.abs(lastPointY - event.getY()) >= criticalHeight) {
                                indexYear--;
                                centerYear = centerYear - 1;
                            }
                        } else if (lastPointY > event.getY()) {
                            if (Math.abs(lastPointY - event.getY()) >= criticalHeight) {
                                indexYear++;
                                centerYear = centerYear + 1;
                            }
                        }
                        buildRegion();
                        computeDate();
                        smoothScrollTo(width * indexMonth, height * indexYear);
                        lastMoveY = height * indexYear;
                    }
                } else if (mSlideMode == SlideMode.HOR) {
                    if (Math.abs(lastPointX - event.getX()) > 25) {
                        if (lastPointX > event.getX() && Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                            indexMonth++;
                            centerMonth = (centerMonth + 1) % 13;
                            if (centerMonth == 0) {
                                centerMonth = 1;
                                centerYear++;
                            }
                        } else if (lastPointX < event.getX() && Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                            indexMonth--;
                            centerMonth = (centerMonth - 1) % 12;
                            if (centerMonth == 0) {
                                centerMonth = 12;
                                centerYear--;
                            }
                        }
                        buildRegion();
                        computeDate();
                        smoothScrollTo(width * indexMonth, indexYear * height);
                        lastMoveX = width * indexMonth;
                    }
                } else {
//                    defineRegion((int) event.getX(), (int) event.getY());
                    defineRegion(lastPointX, lastPointY);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureUtil.dp2px(getContext(), 345);
        setMeasuredDimension(measureWidth, height);
    }

    private int mCurrCellH = 0, mCurrCellW = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;

        criticalWidth = (int) (1F / 5F * width);
        criticalHeight = (int) (1F / 5F * height);

        int cellW = (int) (w / 7F);
        int cellH4 = (int) (h / 6F);
        int cellH5 = (int) (h / 7F);
        int cellH6 = (int) (h / 8F);
        mCurrCellH = (int) (h / 8F);
        mCurrCellW = cellW;
        circleRadius = cellW;

        sizeDecor = (int) (cellW / 3F);
        sizeDecor2x = sizeDecor * 2;
        sizeDecor3x = sizeDecor * 3;

        sizeTextGregorian = width / 25F;
        mPaint.setTextSize(sizeTextGregorian);

        float heightGregorian = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        sizeTextFestival = width / 40F;
        mPaint.setTextSize(sizeTextFestival);

        float heightFestival = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        offsetYFestival1 = (((Math.abs(mPaint.ascent() + mPaint.descent())) / 2F) + heightFestival / 2F + heightGregorian / 2F) / 2F;
        offsetYFestival2 = offsetYFestival1 * 2F;

        for (int i = 0; i < MONTH_REGIONS_4.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_4[i].length; j++) {
                Region region = new Region();
                region.set((j * cellW), (i * cellH6), cellW + (j * cellW), cellW + (i * cellH6));
                MONTH_REGIONS_4[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_REGIONS_5.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_5[i].length; j++) {
                Region region = new Region();
                region.set((j * cellW), (i * cellH6), cellW + (j * cellW), cellW + (i * cellH6));
                MONTH_REGIONS_5[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_REGIONS_6.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_6[i].length; j++) {
                Region region = new Region();
                region.set((j * cellW), (i * cellH6), cellW + (j * cellW), cellW + (i * cellH6));
                MONTH_REGIONS_6[i][j] = region;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.record_date_view_bg));
        draw(canvas, width * indexMonth, (indexYear - 1) * height, topYear, topMonth);
        draw(canvas, width * (indexMonth - 1), height * indexYear, leftYear, leftMonth);
        draw(canvas, width * indexMonth, indexYear * height, centerYear, centerMonth);
        draw(canvas, width * (indexMonth + 1), height * indexYear, rightYear, rightMonth);
        draw(canvas, width * indexMonth, (indexYear + 1) * height, bottomYear, bottomMonth);
    }

    private void draw(Canvas canvas, int x, int y, int year, int month) {
        canvas.save();
        canvas.translate(x, y);
        DPInfo[][] info = mCManager.obtainDPInfo(year, month);
        DPInfo[][] result;
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            tmp = MONTH_REGIONS_4;
            arrayClear(INFO_4);
            result = arrayCopy(info, INFO_4);
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            tmp = MONTH_REGIONS_5;
            arrayClear(INFO_5);
            result = arrayCopy(info, INFO_5);
        } else {
            tmp = MONTH_REGIONS_6;
            arrayClear(INFO_6);
            result = arrayCopy(info, INFO_6);
        }
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                draw(canvas, tmp[i][j].getBounds(), info[i][j]);
            }
        }
        canvas.restore();
    }

    public void onAnimate(final boolean isUp) {
        if (mCurrState == isUp) {
            return;
        }
        mCurrState = isUp;

        if (mInitY == 0) {
            mInitY = getY();
        }
        ValueAnimator anim = !isUp ? ValueAnimator.ofFloat(0, 1) : ValueAnimator.ofFloat(1, 0);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float changeTop = (mCurrCellH * mCurrRow) * (float) animation.getAnimatedValue();
                setY(mInitY - changeTop);
            }
        });
        anim.start();
    }

    private void draw(Canvas canvas, Rect rect, DPInfo info) {
        drawBG(canvas, rect, info);
        drawDecor(canvas, rect, info);
        drawGregorian(canvas, rect, info.strG, info);
        if (isFestivalDisplay) drawFestival(canvas, rect, info.strF, info.isFestival);
    }

    private void drawBG(Canvas canvas, Rect rect, DPInfo info) {
//        if (info.isToday && isTodayDisplay) {
//            drawBGToday(canvas, rect, info.strG);
//        } else {
        if (isHolidayDisplay) {
            drawBGHoliday(canvas, rect, info.isHoliday);
        }
        if (isDeferredDisplay) {
            drawBGDeferred(canvas, rect, info.isDeferred);
        }
//        }
    }

//    private void drawBGToday(Canvas canvas, Rect rect, String str) {
//        mPaint.setTextSize(sizeTextGregorian);
//        mPaint.setColor(mTManager.colorWeekend());
//        float y = rect.centerY() + Math.abs(mPaint.ascent()) - (mPaint.descent() - mPaint.ascent()) / 2F;
//        canvas.drawText(str, rect.centerX() + 2, rect.centerY(), mPaint);
//    }

    private void drawBGHoliday(Canvas canvas, Rect rect, boolean isHoliday) {
        mPaint.setColor(mTManager.colorTitle());
        if (isHoliday) canvas.drawCircle(rect.centerX(), rect.centerY(), circleRadius / 2F, mPaint);
    }

    private void drawBGDeferred(Canvas canvas, Rect rect, boolean isDeferred) {
        mPaint.setColor(mTManager.colorDeferred());
        if (isDeferred)
            canvas.drawCircle(rect.centerX(), rect.centerY(), circleRadius / 2F, mPaint);
    }

    private boolean isInit = true;

    private void drawGregorian(Canvas canvas, Rect rect, String str, DPInfo info) {
        mPaint.setTextSize(sizeTextGregorian);
        if (info.isToday && isTodayDisplay) {
//            mPaint.setColor(mTManager.colorWeekend());
//            mPaint.setColor(mTManager.colorG());
            mPaint.setColor(getResources().getColor(R.color.record_date_view_text_super_light));
            if (null != onDatePickedListener && isInit) {
                isInit = false;
                String date = centerYear + "-" + centerMonth + "-" + str;
                select(date);
                date = getTime(date);
                onDatePickedListener.onDatePicked(date);
            }
        } else {
            mPaint.setColor(mTManager.colorG());
        }
        float y = rect.centerY();
        if (!isFestivalDisplay)
            y = rect.centerY() + Math.abs(mPaint.ascent()) - (mPaint.descent() - mPaint.ascent()) / 2F;
        canvas.drawText(str, rect.centerX() + 2, y, mPaint);
    }

    private void drawFestival(Canvas canvas, Rect rect, String str, boolean isFestival) {
        mPaint.setTextSize(sizeTextFestival);
        if (isFestival) {
            mPaint.setColor(mTManager.colorF());
        } else {
            mPaint.setColor(mTManager.colorL());
        }
        if (str.contains("&")) {
            String[] s = str.split("&");
            String str1 = s[0];
            if (mPaint.measureText(str1) > rect.width()) {
                float ch = mPaint.measureText(str1, 0, 1);
                int length = (int) (rect.width() / ch);
                canvas.drawText(str1.substring(0, length), rect.centerX(), rect.centerY() + offsetYFestival1, mPaint);
                canvas.drawText(str1.substring(length), rect.centerX(), rect.centerY() + offsetYFestival2, mPaint);
            } else {
                canvas.drawText(str1, rect.centerX(),
                        rect.centerY() + offsetYFestival1, mPaint);
                String str2 = s[1];
                if (mPaint.measureText(str2) < rect.width()) {
                    canvas.drawText(str2, rect.centerX(), rect.centerY() + offsetYFestival2, mPaint);
                }
            }
        } else {
            if (mPaint.measureText(str) > rect.width()) {
                float ch = 0.0F;
                for (char c : str.toCharArray()) {
                    float tmp = mPaint.measureText(String.valueOf(c));
                    if (tmp > ch) {
                        ch = tmp;
                    }
                }
                int length = (int) (rect.width() / ch);
                canvas.drawText(str.substring(0, length), rect.centerX(), rect.centerY() + offsetYFestival1, mPaint);
                canvas.drawText(str.substring(length), rect.centerX(), rect.centerY() + offsetYFestival2, mPaint);
            } else {
                canvas.drawText(str, rect.centerX(), rect.centerY() + offsetYFestival1, mPaint);
            }
        }
    }

    private void drawDecor(Canvas canvas, Rect rect, DPInfo info) {
        if (!TextUtils.isEmpty(info.strG)) {
            String data = centerYear + "-" + centerMonth + "-" + info.strG;
            if (null != mDPDecor && info.isDecorTL) {
                mDPDecor.drawDecorTL(canvas, rect, mPaint, data);
            }
            if (null != mDPDecor && info.isDecorBG) {
                mDPDecor.drawDecorBG(canvas, rect, mPaint, centerYear + "-" + centerMonth + "-" + info.strG);
            } else if (null != mDPDecor && info.isDecorT) {
                mDPDecor.drawDecorT(canvas, rect, mPaint, data);
            }
            if (null != mDPDecor && info.isDecorTR) {
                mDPDecor.drawDecorTR(canvas, rect, mPaint, data);
            }
            if (null != mDPDecor && info.isDecorL) {
                mDPDecor.drawDecorL(canvas, rect, mPaint, data);
            }
            if (null != mDPDecor && info.isDecorR) {
                mDPDecor.drawDecorR(canvas, rect, mPaint, data);
//                canvas.save();
//                canvas.clipRect(rect.left + sizeDecor2x, rect.top + sizeDecor, rect.left + sizeDecor3x, rect.top + sizeDecor2x);
//                mDPDecor.drawDecorR(canvas, canvas.getClipBounds(), mPaint, data);
//                canvas.restore();
            }
            if (null != mDPDecor && info.isDecorSelected) {
                mDPDecor.drawDecorSelected(canvas, rect, mPaint, data);
            }
        }
    }

    List<String> getDateSelected() {
        return dateSelected;
    }

    void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public void setOnDatePickedListener(DatePicker.OnDatePickedListener onDatePickedListener) {
        this.onDatePickedListener = onDatePickedListener;
    }

    void setDPMode(DPMode mode) {
        this.mDPMode = mode;
    }

    void setDPDecor(DPDecor decor) {
        this.mDPDecor = decor;
    }

    DPMode getDPMode() {
        return mDPMode;
    }

    void setDate(int year, int month) {
        centerYear = year;
        centerMonth = month;
        indexYear = 0;
        indexMonth = 0;
        buildRegion();
        computeDate();
        requestLayout();
        invalidate();
    }

    void setFestivalDisplay(boolean isFestivalDisplay) {
        this.isFestivalDisplay = isFestivalDisplay;
    }

    void setTodayDisplay(boolean isTodayDisplay) {
        this.isTodayDisplay = isTodayDisplay;
    }

    void setHolidayDisplay(boolean isHolidayDisplay) {
        this.isHolidayDisplay = isHolidayDisplay;
    }

    void setDeferredDisplay(boolean isDeferredDisplay) {
        this.isDeferredDisplay = isDeferredDisplay;
    }

    private void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();
    }

    private void buildRegion() {
        String key = indexYear + ":" + indexMonth;
        if (!REGION_SELECTED.containsKey(key)) {
            REGION_SELECTED.put(key, new ArrayList<Region>());
        }
    }

    private void arrayClear(DPInfo[][] info) {
        for (DPInfo[] anInfo : info) {
            Arrays.fill(anInfo, null);
        }
    }

    private DPInfo[][] arrayCopy(DPInfo[][] src, DPInfo[][] dst) {
        for (int i = 0; i < dst.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, dst[i].length);
        }
        return dst;
    }

    private void defineRegion(int x, int y) {
        DPInfo[][] info = mCManager.obtainDPInfo(centerYear, centerMonth);
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            tmp = MONTH_REGIONS_4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            tmp = MONTH_REGIONS_5;
        } else {
            tmp = MONTH_REGIONS_6;
        }
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[i].length; j++) {
                Region region = tmp[i][j];
                if (TextUtils.isEmpty(info[i][j].strG)) {
                    continue;
                }
                info[i][j].isDecorSelected = false;
                if (region.contains(x, y) && !hasSelected) {
                    hasSelected = true;
                    mCurrRow = i;
                    info[i][j].isDecorSelected = true;
                    String date = centerYear + "-" + centerMonth + "-" + info[i][j].strG;
                    select(date);
                    if (null != onDatePickedListener) {
                        date = getTime(date);
                        onDatePickedListener.onDatePicked(date);
                    }
                    hasSelected();
                }
            }
        }
    }

    private void select(String date) {
        List<String> tmpSelect = new ArrayList<>();
        tmpSelect.add(date);
        DPCManager.getInstance().setDecorSelected(tmpSelect);
        setDPDecor(dpDecor);
        invalidate();
    }

    private String getTime(String date) {
        String year = date.substring(0, date.indexOf("-"));
        String month = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
        String day = date.substring(date.lastIndexOf("-") + 1);
        if (StringUtils.parseInt(day) < 10) {
            day = "0" + day;
        }
        if (StringUtils.parseInt(month) < 10) {
            month = "0" + month;
        }
        StringBuffer time = new StringBuffer();
        time.append(year);
        time.append("-");
        time.append(month);
        time.append("-");
        time.append(day);
        return String.valueOf(time);
    }

    final int paddingTop = 0;

    public DPDecor dpDecor = new DPDecor() {
        @Override
        public void drawDecorBG(Canvas canvas, Rect rect, Paint paint, String data) {
            super.drawDecorBG(canvas, rect, paint, data);
            Drawable drawable = getResources().getDrawable(R.drawable.record_day_state_papa);
            canvas.drawBitmap(drawableToBitmap(drawable), rect.centerX() - drawable.getIntrinsicWidth() / 2, rect.centerY() - drawable.getIntrinsicHeight() / 2 + paddingTop, paint);
        }

        @Override
        public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
            super.drawDecorTR(canvas, rect, paint, data);
            Drawable drawable = getResources().getDrawable(R.drawable.record_day_state_dym);
            canvas.drawBitmap(drawableToBitmap(drawable), rect.centerX() - drawable.getIntrinsicWidth() / 2, rect.centerY() - drawable.getIntrinsicHeight() / 2 + paddingTop, paint);
        }

        @Override
        public void drawDecorR(Canvas canvas, Rect rect, Paint paint, String data) {
            super.drawDecorR(canvas, rect, paint, data);
            Drawable drawable = getResources().getDrawable(R.drawable.record_day_state_nothing);
            canvas.drawBitmap(drawableToBitmap(drawable), rect.centerX() - drawable.getIntrinsicWidth() / 2, rect.centerY() - drawable.getIntrinsicHeight() / 2 + paddingTop, paint);
        }

        @Override
        public void drawDecorT(Canvas canvas, Rect rect, Paint paint, String data) {
            super.drawDecorT(canvas, rect, paint, data);
            Drawable drawable = getResources().getDrawable(R.drawable.record_day_state_myself);
            canvas.drawBitmap(drawableToBitmap(drawable), rect.centerX() - drawable.getIntrinsicWidth() / 2, rect.centerY() - drawable.getIntrinsicHeight() / 2 + paddingTop, paint);
        }

        @Override
        public void drawDecorSelected(Canvas canvas, Rect rect, Paint paint, String data) {
            super.drawDecorSelected(canvas, rect, paint, data);
            Drawable drawable = getResources().getDrawable(R.drawable.record_day_state_focus);
            canvas.drawBitmap(drawableToBitmap(drawable), rect.centerX() - drawable.getIntrinsicWidth() / 2, rect.centerY() - drawable.getIntrinsicHeight() / 2, paint);
        }
    };

    public void hasSelected() {
        ThreadManager.postDelayed(ThreadManager.THREAD_WORK, new Runnable() {
            @Override
            public void run() {
                hasSelected = false; //防止多点
            }
        }, 300);
    }

    Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private void computeDate() {
        rightYear = leftYear = centerYear;
        topYear = centerYear - 1;
        bottomYear = centerYear + 1;

        topMonth = centerMonth;
        bottomMonth = centerMonth;

        rightMonth = centerMonth + 1;
        leftMonth = centerMonth - 1;

        if (centerMonth == 12) {
            rightYear++;
            rightMonth = 1;
        }
        if (centerMonth == 1) {
            leftYear--;
            leftMonth = 12;
        }
        if (null != onDateChangeListener) {
            onDateChangeListener.onYearChange(centerYear);
            onDateChangeListener.onMonthChange(centerMonth);
        }
    }

    public interface OnDateChangeListener {
        void onMonthChange(int year);

        void onYearChange(int month);
    }

    private enum SlideMode {
        VER,
        HOR
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class ScaleAnimationListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            MonthView.this.invalidate();
        }
    }
}
