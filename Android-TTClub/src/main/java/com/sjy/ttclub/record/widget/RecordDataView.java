package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.AlphaTextView;

/**
 * Created by linhz on 2016/1/8.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataView extends LinearLayout {

    private TextView mTitleView;
    private FrameLayout mDataViewContainer;
    private AlphaTextView mShareView;
    private View mShareLogoView;

    private String mSampleText;
    private String mTitleText;

    public RecordDataView(Context context) {
        this(context, null);
    }

    public RecordDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(ResourceHelper.getColor(R.color.white));
        addTitleView();
        addDivider();
        addContainer();
        addShareView();
    }

    private void addTitleView() {
        mTitleView = new TextView(getContext());
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_16));
        mTitleView.setTextColor(ResourceHelper.getColor(R.color.black));
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setText("偷窥啪啪");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper
                .getDimen(R.dimen.space_64));
        this.addView(mTitleView, lp);
    }

    private void addContainer() {
        mDataViewContainer = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        lp.topMargin = ResourceHelper.getDimen(R.dimen.space_20);
        lp.bottomMargin = ResourceHelper.getDimen(R.dimen.space_8);
        this.addView(mDataViewContainer, lp);
    }

    private void addShareView() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper
                .getDimen(R.dimen.space_40));
        this.addView(frameLayout, lp);

        mShareView = new AlphaTextView(getContext());
        mShareView.setTextColor(ResourceHelper.getColor(R.color.black_dark));
        mShareView.setGravity(Gravity.CENTER_VERTICAL);
        mShareView.setText(R.string.record_data_share);
        mShareView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceHelper.getDimen(R.dimen.space_13));
        mShareView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.record_share, 0);
        mShareView.setCompoundDrawablePadding(ResourceHelper.getDimen(R.dimen.space_8));
        mShareView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareClicked();
            }
        });
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        flp.rightMargin = ResourceHelper.getDimen(R.dimen.space_16);
        flp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        frameLayout.addView(mShareView, flp);

        mShareLogoView = View.inflate(getContext(), R.layout.share_bottom_logo, null);
        mShareLogoView.setVisibility(View.INVISIBLE);
        flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        flp.rightMargin = ResourceHelper.getDimen(R.dimen.space_16);
        flp.gravity = Gravity.CENTER;
        frameLayout.addView(mShareLogoView, flp);
    }

    private void addDivider() {
        View view = new View(getContext());
        view.setBackgroundColor(ResourceHelper.getColor(R.color.divider_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper
                .getDimen(R.dimen.divider_height));
        lp.leftMargin = lp.rightMargin = ResourceHelper.getDimen(R.dimen.space_16);
        this.addView(view, lp);
    }

    public void setShareText(String text) {
        mShareView.setText(text);
    }

    public void setSampleText(String text) {
        mSampleText = text;
    }

    public void setupDataItemView(RecordDataViewInfo info) {
        mTitleView.setText(info.title);
        mTitleText = info.title;
        RecordDataChartView view = createChartViewByInfo(info);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout
                .LayoutParams.WRAP_CONTENT);
        mDataViewContainer.addView(view, lp);
    }

    private RecordDataChartView createChartViewByInfo(RecordDataViewInfo info) {
        RecordDataChartView view;
        if (info.type == RecordDataViewInfo.TYPE_CIRCLE) {
            view = new RecordDataCircleChartView(getContext());
        } else if (info.type == RecordDataViewInfo.TYPE_POSITION) {
            view = new RecordDataWherePapaView(getContext());
        } else {
            view = new RecordDataBarChartView(getContext());
        }
        view.setupChartView(info.chartViewInfoList);
        return view;
    }

    public void prepareGroupShare() {
        mShareView.setVisibility(View.INVISIBLE);
    }

    public void afterGroupShare() {
        mShareView.setVisibility(View.VISIBLE);
    }

    private void prepareShare() {
        mShareView.setVisibility(View.INVISIBLE);
        mShareLogoView.setVisibility(View.VISIBLE);
    }

    private void afterShare() {
        mShareView.setVisibility(View.VISIBLE);
        mShareLogoView.setVisibility(View.INVISIBLE);
    }

    private Bitmap startSnapshot() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    private void handleShareClicked() {
        prepareShare();
        Bitmap bitmap = startSnapshot();
        afterShare();
        if (bitmap == null) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }
        String path = CommonUtils.storePageSnapshot(bitmap);
        if (StringUtils.isEmpty(path)) {
            ToastHelper.showToast(R.string.share_error);
            return;
        }

        ShareIntentBuilder builder = ShareIntentBuilder.obtain();
        builder.setShareImageUrl(path);
        builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE);
        builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_IMAGE);
        builder.setShareContent(mSampleText);
        builder.setShareTitle(mTitleText);
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHARE;
        msg.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(msg);
    }

}
