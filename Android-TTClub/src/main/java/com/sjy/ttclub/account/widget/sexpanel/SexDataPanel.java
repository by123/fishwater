package com.sjy.ttclub.account.widget.sexpanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.share.ShareIntentBuilder;
import com.sjy.ttclub.share.SharePlatformInfo;
import com.sjy.ttclub.share.ShareSender;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;
import com.sjy.ttclub.widget.BasePanel;
import com.sjy.ttclub.widget.RoundProgressBar;

import java.text.DecimalFormat;

/**
 * Created by 陈嘉伟 on 2015/12/3
 */
public class SexDataPanel extends BasePanel implements View.OnClickListener {
    private RelativeLayout mRelativeLayout;
    //布局控制
    private RoundProgressBar mRoundProgressBar;
    private TextView mPapaData, mRecordData, mCommunityData, mSortData;
    private TextView mUserName;
    private SimpleDraweeView mUserIcon;
    private LinearLayout mShareIconBottom, mShareBottom;
    private TextView mFirstTitle, mSecondTitle;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.0");
    private DecimalFormat mDecimalFormatWhole = new DecimalFormat("0");

    public SexDataPanel(Context context) {
        super(context);
        setAnimation(R.style.SexDataPanelAnim);
        initUI();
        //统计
        StatsModel.stats(StatsKeyDef.SEXY_INDEX_VIEW);
    }

    @Override
    protected View onCreateContentView() {
        mRelativeLayout = (RelativeLayout) View.inflate(mContext, R.layout.account_sex_share_layout, null);
        return mRelativeLayout;
    }

    @Override
    protected FrameLayout.LayoutParams getLayoutParams() {
        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    private void initUI() {
        mRoundProgressBar = (RoundProgressBar) mRelativeLayout.findViewById(R.id.share_tasks_view);
        mPapaData = (TextView) mRelativeLayout.findViewById(R.id.tv_papa_data);
        mRecordData = (TextView) mRelativeLayout.findViewById(R.id.tv_record_data);
        mCommunityData = (TextView) mRelativeLayout.findViewById(R.id.tv_community_data);
        mSortData = (TextView) mRelativeLayout.findViewById(R.id.tv_sort_data);
        mUserName = (TextView) mRelativeLayout.findViewById(R.id.tv_share_username);
        mUserIcon = (SimpleDraweeView) mRelativeLayout.findViewById(R.id.sv_share_user_head);
        mRelativeLayout.findViewById(R.id.iv_share_wx).setOnClickListener(this);
        mRelativeLayout.findViewById(R.id.iv_share_wb).setOnClickListener(this);
        mRelativeLayout.findViewById(R.id.iv_share_qq).setOnClickListener(this);
        mRelativeLayout.findViewById(R.id.iv_share_wh).setOnClickListener(this);
        mRelativeLayout.findViewById(R.id.iv_share_qqzone).setOnClickListener(this);
        mShareIconBottom = (LinearLayout) mRelativeLayout.findViewById(R.id.ll_share_icon_bottom);
        mShareBottom = (LinearLayout) mRelativeLayout.findViewById(R.id.ll_share_bottom);
        mFirstTitle = (TextView) mRelativeLayout.findViewById(R.id.middle_first_title);
        mSecondTitle = (TextView) mRelativeLayout.findViewById(R.id.middle_second_title);
        AccountInfo userInfo = AccountManager.getInstance().getAccountInfo();
        if (userInfo != null) {
            String nickName = userInfo.getNickname();
            String sexPoint = userInfo.getSexPoint();
            String papaData = userInfo.getSexCount();
            String recordData = userInfo.getRecordCount();
            String communityData = userInfo.getUserExpers();
            String sortData = userInfo.getRanking();
            setFormatText(mPapaData, papaData);
            setFormatText(mRecordData, recordData);
            setFormatText(mCommunityData, communityData);
            setFormatText(mSortData, sortData);
            AccountManager.getInstance().setHeadImage(mUserIcon);
            mUserName.setText(nickName);
            mRoundProgressBar.setMaxProgress(20000);
            mRoundProgressBar.setCurProgress(StringUtils.parseInt(sexPoint));//
            if (String.valueOf(CommonConst.SEX_MAN).equals(userInfo.getSex())) {
                mFirstTitle.setText(mContext.getString(R.string.sex_panel_man_first_title));
                mSecondTitle.setText(mContext.getString(R.string.sex_panel_man_second_title));
            } else if (String.valueOf(CommonConst.SEX_WOMAN).equals(userInfo.getSex())) {
                mFirstTitle.setText(mContext.getString(R.string.sex_panel_woman_first_title));
                mSecondTitle.setText(mContext.getString(R.string.sex_panel_woman_second_title));
            }
        } else {
            ToastHelper.showToast(mContext, R.string.homepage_data_error, Toast.LENGTH_SHORT);
        }
    }

    private void setFormatText(TextView tv, String data) {
        double count = StringUtils.parseInt(data);
        if (count < 0) {
            tv.setText("0");
        } else if (count < 10000) {
            tv.setText(mDecimalFormatWhole.format(count) + "");
        } else if (count >= 10000 && count < 99500) {
            double tmp = count / 10000;
            tv.setText(mDecimalFormat.format(tmp) + "万");
        } else if (count >= 99500 && count < 100000000) {
            double tmp = count / 10000;
            tv.setText(mDecimalFormatWhole.format(tmp) + "万");
        } else if (count >= 100000000 && count < 995000000) {
            double tmp = count / 100000000;
            tv.setText(mDecimalFormat.format(tmp) + "亿");
        } else if (count >= 995000000) {
            double tmp = count / 100000000;
            tv.setText(mDecimalFormatWhole.format(tmp) + "亿");
        }
    }

    public String saveShareImage() {
        Bitmap bitmap = Bitmap.createBitmap(mRelativeLayout.getWidth(), mRelativeLayout.getHeight(), Bitmap.Config
                .RGB_565);
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        mRelativeLayout.draw(canvas);
        String path = CommonUtils.storePageSnapshot(bitmap);
        return path;
    }

    private void storeAfterShare() {
        mShareBottom.setVisibility(View.VISIBLE);
        mShareIconBottom.setVisibility(View.INVISIBLE);
    }

    private void prepareForShare() {
        mShareBottom.setVisibility(View.INVISIBLE);
        mShareIconBottom.setVisibility(View.VISIBLE);
    }

    private void shareAction(final String platformId) {
        prepareForShare();
        String path = saveShareImage();
        storeAfterShare();
        if (StringUtils.isNotEmpty(path)) {
            ShareIntentBuilder builder = ShareIntentBuilder.obtain();
            builder.setShareImageUrl(path);
            builder.setShareSourceType(ShareIntentBuilder.SOURCE_TYPE_SHARE_IMAGE);
            builder.setShareMineType(ShareIntentBuilder.MIME_TYPE_IMAGE);
            ShareSender.getInstance().sendShare(platformId, builder.create());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_wh:
                shareAction(SharePlatformInfo.ID_WECHAT_TIMELINE);
                break;
            case R.id.iv_share_qqzone:
                shareAction(SharePlatformInfo.ID_QZONE);
                break;
            case R.id.iv_share_qq:
                shareAction(SharePlatformInfo.ID_QQ);
                break;
            case R.id.iv_share_wb:
                shareAction(SharePlatformInfo.ID_WEIBO);
                break;
            case R.id.iv_share_wx:
                shareAction(SharePlatformInfo.ID_WECHAT_FRIENDS);
                break;
        }
    }
}
