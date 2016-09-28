package com.sjy.ttclub.record.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
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

import java.util.ArrayList;

/**
 * Created by linhz on 2016/1/9.
 * Email: linhaizhong@ta2she.com
 */
public class RecordDataGroupView extends LinearLayout {
    private TextView mShareView;
    private TextView mSampleView;
    private View mShareLogoView;

    private String mShareText;
    private String mSampleText;
    private boolean mIsSelf = false;
    private SelfShareClicked mSelfShareClicked;

    public RecordDataGroupView(Context context) {
        this(context, null);
    }

    public RecordDataGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordDataGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(ResourceHelper.getColor(R.color.record_date_view_bg));
    }

    public void setShareText(String text) {
        mShareText = text;
        if (mShareView != null) {
            mShareView.setText(text);
        }
    }

    public void setSampleText(String text) {
        mSampleText = text;
        if (mSampleView != null) {
            mSampleView.setText(text);
        }
    }

    public void setupGroupView(ArrayList<RecordDataViewInfo> list) {
        removeAllViewsInLayout();
        RecordDataViewInfo viewInfo;
        LinearLayout.LayoutParams lp;
        RecordDataView dataView;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                viewInfo = list.get(i);
                dataView = new RecordDataView(getContext());
                dataView.setShareText(mShareText);
                dataView.setSampleText(mSampleText);
                dataView.setupDataItemView(viewInfo);
                lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    lp.topMargin = ResourceHelper.getDimen(R.dimen.space_12);
                }
                this.addView(dataView, lp);
            }
        }

        addDivider();
        addShareBottomView();
    }

    private void addShareBottomView() {
        View view = View.inflate(getContext(), R.layout.record_data_bottom_layout, null);
        LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .WRAP_CONTENT);
        this.addView(view, lp);
        mShareView = (TextView) view.findViewById(R.id.record_bottom_share);
        mSampleView = (TextView) view.findViewById(R.id.record_specimen_count);
        mSampleView.setText(mSampleText);
        mShareView.setText(mShareText);
        mShareView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShareClicked();
            }
        });
        mShareLogoView = view.findViewById(R.id.share_bottom_logo);
        mShareLogoView.setVisibility(View.INVISIBLE);
    }

    private void addDivider() {
        View view = new View(getContext());
        view.setBackgroundColor(ResourceHelper.getColor(R.color.divider_color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ResourceHelper
                .getDimen(R.dimen.divider_height));
        lp.leftMargin = lp.rightMargin = ResourceHelper.getDimen(R.dimen.space_16);
        this.addView(view, lp);
    }

    public void prepareShare() {
        mShareView.setVisibility(View.INVISIBLE);
        mShareLogoView.setVisibility(View.VISIBLE);
        int childCount = getChildCount();
        RecordDataView dataView;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child instanceof RecordDataView) {
                dataView = (RecordDataView) child;
                dataView.prepareGroupShare();
            }
        }
    }

    public void afterShare() {
        mShareView.setVisibility(View.VISIBLE);
        mShareLogoView.setVisibility(View.INVISIBLE);
        int childCount = getChildCount();
        RecordDataView dataView;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child instanceof RecordDataView) {
                dataView = (RecordDataView) child;
                dataView.afterGroupShare();
            }
        }
    }

    public int getAllChildHeight(){
        int childCount = getChildCount();
        View child;
        int height = 0;
        LayoutParams lp;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            lp = (LayoutParams)child.getLayoutParams();
            height += lp.topMargin+lp.bottomMargin;
            height += child.getHeight();
        }
        return height;
    }

    private Bitmap startSnapshot() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), getAllChildHeight(), Bitmap.Config.RGB_565);
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public void setIsSelf(boolean isSelf){
        mIsSelf = isSelf;
    }

    private void handleShareClicked() {
        if (mIsSelf) {
            if (mSelfShareClicked != null) {
                mSelfShareClicked.shareClicked(mSampleText);
            }
            return;
        }
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
        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHARE;
        msg.obj = builder.create();
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    public interface SelfShareClicked {
        void shareClicked(String sampleText);
    }

    public void setSelfShareClicked(SelfShareClicked selfShareClicked) {
        mSelfShareClicked = selfShareClicked;
    }
}
