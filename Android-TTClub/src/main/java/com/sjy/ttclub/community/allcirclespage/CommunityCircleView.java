package com.sjy.ttclub.community.allcirclespage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ToastHelper;

/**
 * Created by zwl on 2015/11/26.
 * Email: 1501448275@qq.com
 */
public class CommunityCircleView extends LinearLayout {

    private LinearLayout mRootLayout;
    private SimpleDraweeView mCircleIcon;
    private TextView mCircleName;
    private TextView mCircleTheme;
    private TextView mCircleHeatCount;
    private TextView mCircleHotSign;
    private ImageView mCircleHeatIcon;
    private CommunityCircleBean mCircle;
    private static final int PERMISSION_ANYONE = 0;
    private OnCircleItemClickListener mListener;

    public CommunityCircleView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View parentView = View.inflate(getContext(), R.layout.community_circle_item_layout, null);
        addView(parentView);
        mRootLayout = (LinearLayout) parentView.findViewById(R.id.ll_layout);
        mCircleIcon = (SimpleDraweeView) parentView.findViewById(R.id.community_icon);
        mCircleName = (TextView) parentView.findViewById(R.id.community_name);
        mCircleTheme = (TextView) parentView.findViewById(R.id.community_theme);
        mCircleHeatCount = (TextView) parentView.findViewById(R.id.community_person_count);
        mCircleHotSign = (TextView) parentView.findViewById(R.id.hot_sign);
        mCircleHeatIcon = (ImageView) parentView.findViewById(R.id.heat_icon);
        mRootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerRootLayoutOnClick();
            }
        });
    }

    private void handlerRootLayoutOnClick() {
        if (mCircle == null) {
            return;
        }
        //统计community_discovery_circle
        StatsModel.stats(StatsKeyDef.CONMMUNITY_DISCOVER_CLICK);
        if (StringUtils.isEmpty(mCircle.getCircleName())) {
            StatsModel.stats(StatsKeyDef.GROUPLIST_CLICK, StatsKeyDef.SPEC_KEY, mCircle.getCircleId());
        } else {
            StatsModel.stats(StatsKeyDef.GROUPLIST_CLICK, StatsKeyDef.SPEC_KEY, mCircle.getCircleName());
        }
        if (mListener != null) {
            mListener.onClick();
        }
        if (mCircle.getLimitEnterSex() == PERMISSION_ANYONE) {
            Message message = Message.obtain();
            message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
            message.arg1 = mCircle.getCircleId();
            message.arg2 = mCircle.getCircleType();
            MsgDispatcher.getInstance().sendMessage(message);
            return;
        }
        int userSex = AccountManager.getInstance().getSex();
        if (mCircle.getLimitEnterSex() == userSex) {
            if (userSex == CommonConst.SEX_MAN) {
                ToastHelper.showToast(getContext(), getContext().getString(R.string.community_limit_man_enter), Toast.LENGTH_SHORT);
            } else {
                ToastHelper.showToast(getContext(), getContext().getString(R.string.community_limit_woman_enter), Toast.LENGTH_SHORT);
            }
            return;
        }
        Message message = Message.obtain();
        message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
        message.arg1 = mCircle.getCircleId();
        message.arg2 = mCircle.getCircleType();
        MsgDispatcher.getInstance().sendMessage(message);
    }

    public void setCircleView(CommunityCircleBean circleBean) {
        mCircle = circleBean;
        setmCircleName(circleBean);
        setmCircleTheme(circleBean);
        setmCircleIcon(circleBean);
        setCircleHeatIcon(circleBean);
        setmCircleHeatCount(circleBean);
        setmCircleHotSign(circleBean);
    }

    private void setCircleHeatIcon(CommunityCircleBean circleBean) {
        if (circleBean.getIsLimitMale() == CommunityConstant.CIRCLE_LIMIT_MAN) {
            mCircleHeatIcon.setImageResource(R.drawable.community_hot_woman_icon);
        } else {
            if (AccountManager.getInstance().getSex() == CommonConst.SEX_MAN) {
                mCircleHeatIcon.setImageResource(R.drawable.community_hot_man_icon);
            } else {
                mCircleHeatIcon.setImageResource(R.drawable.community_hot_woman_icon);
            }
        }
    }

    private void setmCircleHeatCount(CommunityCircleBean circleBean) {
        if (circleBean.getHeat() > CommunityConstant.MAX_COUNTS) {
            mCircleHeatCount.setText(CommunityConstant.MAX_SHOW_VALUE + "℃");
        } else {
            mCircleHeatCount.setText(String.valueOf(circleBean.getHeat() + "℃"));
        }
    }

    private void setmCircleIcon(CommunityCircleBean circleBean) {
        mCircleIcon.setImageURI(Uri.parse(circleBean.getIconUrl()));
    }

    private void setmCircleName(CommunityCircleBean circleBean) {
        mCircleName.setText(circleBean.getCircleName());
    }

    private void setmCircleTheme(CommunityCircleBean circleBean) {
        mCircleTheme.setText(circleBean.getTheme());
    }

    private void setmCircleHotSign(CommunityCircleBean circleBean) {
        if (circleBean.getIsHot() == 0) {
            mCircleHotSign.setVisibility(View.GONE);
        } else {
            mCircleHotSign.setVisibility(View.VISIBLE);
        }
    }

    public interface OnCircleItemClickListener {
        void onClick();
    }

    public void setOnCircleItemClickListener(OnCircleItemClickListener listener) {
        this.mListener = listener;
    }
}
