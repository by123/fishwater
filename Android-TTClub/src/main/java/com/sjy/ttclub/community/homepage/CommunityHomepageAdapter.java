/**
 *
 */
package com.sjy.ttclub.community.homepage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.Banner;
import com.sjy.ttclub.bean.community.CommunityBannerBean;
import com.sjy.ttclub.bean.community.CommunityCircleBean;
import com.sjy.ttclub.bean.community.CommunityListItemInfo;
import com.sjy.ttclub.bean.community.CommunityQABean;
import com.sjy.ttclub.common.BannerHelper;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.community.CommunityItemType;
import com.sjy.ttclub.community.allcirclespage.CommunityCircleView;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.ImageCycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class CommunityHomepageAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityListItemInfo> mItems = new ArrayList<>();
    private ImageCycleView mBannerView;

    public CommunityHomepageAdapter(Context con, List<CommunityListItemInfo> items) {
        this.mContext = con;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = mItems.get(position).getItemType();
        if (convertView == null) {
            convertView = createViewByItemType(type);
        }
        setViewDataByItemType(convertView, mItems.get(position));
        return convertView;
    }

    private View createViewByItemType(int type) {
        View view = null;
        if (type == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_BANNER) {
            view = View.inflate(mContext, R.layout.community_item_banner, null);
        } else if (type == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_HOT_CIRCLE) {
            view = new CommunityCircleView(mContext);
        } else if (type == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_ALL_CIRCLE_TITLE) {
            view = View.inflate(mContext, R.layout.community_home_item_all_circle_title, null);
        } else if (type == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_QA) {
            view = View.inflate(mContext, R.layout.community_item_qa_layout, null);
        }
        return view;
    }

    private void setViewDataByItemType(View view, CommunityListItemInfo itemInfo) {
        if (itemInfo.getItemType() == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_BANNER) {
            setBannerData(view, ((CommunityBannerBean) itemInfo.getData()).getBanners());
        } else if (itemInfo.getItemType() == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_HOT_CIRCLE) {
            setHotCircleViewData((CommunityCircleView) view, (CommunityCircleBean) itemInfo.getData());
        } else if (itemInfo.getItemType() == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_ALL_CIRCLE_TITLE) {
            setTitleViewData(view);
        } else if (itemInfo.getItemType() == CommunityItemType.COMMUNITY_HOME_ITEM_TYPE_QA) {
            setQaViewData(view, (CommunityQABean) itemInfo.getData());
        }
    }

    private void setHotCircleViewData(CommunityCircleView circleView, CommunityCircleBean circle) {
        circleView.setCircleView(circle);
        circleView.setOnCircleItemClickListener(new CommunityCircleView.OnCircleItemClickListener() {
            @Override
            public void onClick() {
                StatsModel.stats(StatsKeyDef.CONMMUNITY_GROUP_REC);
            }
        });
    }

    private void setBannerData(View view, final List<Banner> banners) {
        mBannerView = (ImageCycleView) view.findViewById(R.id.myvp);
        if (banners.size() > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBannerView.getLayoutParams();
            params.width = HardwareUtil.screenWidth;
            params.height = ((HardwareUtil.screenWidth / 5) * 2);
            mBannerView.setLayoutParams(params);
            mBannerView.setImageResources(createBannerImageInfoList(banners), new ImageCycleView.ImageCycleListener() {

                @Override
                public void onImageClick(int position, View imageView) {
                    String val = "banner" + banners.get(position).getTitle();
                    StatsModel.stats(StatsKeyDef.CONMMUNITY_DISCOVER_CLICK);
                    StatsModel.stats(StatsKeyDef.CONMMUNITY_BANNER, StatsKeyDef.SPEC_KEY, val);

                    BannerHelper.handleBannerClick(mContext, banners.get(position));
                }

            });
            mBannerView.startImageCycle();
        } else {
            mBannerView.setVisibility(View.GONE);
        }
    }

    public ImageCycleView getBanner() {
        return mBannerView;
    }

    private void setTitleViewData(View view) {
        RelativeLayout title = (RelativeLayout) view.findViewById(R.id.rl_btn_see_all);
        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tryOpenAllCircleWindow();
                //统计community_discovery_circle_all
                StatsModel.stats(StatsKeyDef.CONMMUNITY_DISCOVER_CLICK);
                StatsModel.stats(StatsKeyDef.CONMMUNITY_GROUP_MORE);
            }
        });
    }

    private void tryOpenAllCircleWindow() {
        MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_COMMUNITY_ALL_CIRCLES_WINDOW);
    }

    private void setQaViewData(View view, final CommunityQABean qa) {
        RelativeLayout mQATitleRelative = (RelativeLayout) view.findViewById(R.id.rlBtnSeeMore);
        TextView mQuestionContentTextView = (TextView) view.findViewById(R.id.textQuestionTheme);
        TextView mAnswerContentTextView = (TextView) view.findViewById(R.id.textAnswerContent);
        SimpleDraweeView mAnswererHeadIcon = (SimpleDraweeView) view.findViewById(R.id.answererHeadIcon);
        LinearLayout mQAContentLinelayout = (LinearLayout) view.findViewById(R.id.ll_qa_content);
        mQATitleRelative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_CIRCLES_DETAIL_WINDOW;
                message.arg1 = qa.getCircleId();
                message.arg2 = CommunityConstant.CIRCLE_TYPE_QA_POST;
                MsgDispatcher.getInstance().sendMessage(message);
                //统计community_discovery_qa_more
                StatsModel.stats(StatsKeyDef.CONMMUNITY_DISCOVER_CLICK);
                StatsModel.stats(StatsKeyDef.CONMMUNITY_QUESITON_MORE);
            }
        });
        mQuestionContentTextView.setText(qa.getQuestion());
        if (qa.getQuestion().length() > 0) {
            mQuestionContentTextView.setVisibility(View.VISIBLE);
        } else {
            mQuestionContentTextView.setVisibility(View.GONE);
        }
        RoundingParams roundingParams = mAnswererHeadIcon.getHierarchy().getRoundingParams();
        if (qa.getUserSex() == CommonConst.SEX_MAN) {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_man), ResourceHelper.getDimen(R.dimen.space_1));
        } else {
            roundingParams.setBorder(ResourceHelper.getColor(R.color.community_color_woman), ResourceHelper.getDimen(R.dimen.space_1));
        }
        mAnswererHeadIcon.getHierarchy().setRoundingParams(roundingParams);
        if (StringUtils.isNotEmpty(qa.getHeadimageUrl())) {
            mAnswererHeadIcon.setImageURI(Uri.parse(qa.getHeadimageUrl()));
        } else {
            mAnswererHeadIcon.setImageURI(Uri.parse("res://drawable/" + R.drawable.community_default_head_icon));
        }
        mAnswerContentTextView.setText(qa.getAnswer());
        mQAContentLinelayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = MsgDef.MSG_SHOW_COMMUNITY_POST_DETAIL_WINDOW;
                message.arg1 = qa.getPostId();
                MsgDispatcher.getInstance().sendMessage(message);
                //统计COMMUNITY_DISCOVERY_QA_RECOMMEND
                StatsModel.stats(StatsKeyDef.CONMMUNITY_DISCOVER_CLICK);
                StatsModel.stats(StatsKeyDef.CONMMUNITY_QUESITON_REC);
            }
        });
    }


    /**
     * banner开始自动滑动
     */
    public void startImageBanner() {
        if (mBannerView != null) {
            mBannerView.startImageCycle();
        }
    }

    /**
     * 关闭banner自动循环
     */
    public void stopImageBanner() {
        if (mBannerView != null) {
            mBannerView.stopImageCycle();
        }
    }

    private ArrayList<ImageCycleView.ImageInfo> createBannerImageInfoList(List<Banner> banners) {
        ArrayList<ImageCycleView.ImageInfo> infoList = new ArrayList<ImageCycleView.ImageInfo>();
        ImageCycleView.ImageInfo info;
        for (Banner banner : banners) {
            info = new ImageCycleView.ImageInfo();
            info.title = banner.getTitle();
            info.url = banner.getImageUrl();
            infoList.add(info);
        }
        return infoList;
    }

}
