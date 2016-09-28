package com.sjy.ttclub.homepage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.homepage.FeedInfo;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.common.PraiseHelper;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.util.ACache;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class HomepageListViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AbsListView
        .OnScrollListener {
    private static final int ITEM_TYPE_TRANSPARENT = 0;
    private static final int ITEM_TYPE_IMAGE_TEXT = 1;
    private static final int ITEM_TYPE_DATE = 2;
    private static final int ITEM_TYPE_IMAGE = 3;
    private static final int ITEM_TYPE_LEFT_IMAGE_TEXT = 4;
    private static final int ITEM_TYPE_LITTLE_IMAGE_TEXT_ONE = 5;
    private static final int ITEM_TYPE_LITTLE_IMAGE_TEXT_TWO = 6;
    private static final int ITEM_TYPE_RIGHT_IMAGE_TEXT = 7;
    private static final int MAX_ITEM_TYPE = 8;

    private static final int POS_TRANSPARENT_VIEW = 0;

    private Context mContext;

    private ArrayList<ItemInfo> mItemList = new ArrayList<ItemInfo>();
    private ArrayList<FeedInfo> mFeedInfos = new ArrayList<FeedInfo>();

    private View mTransparentView;

    private int mFirstVisibleItem;

    private HomepageListViewCallback mCallback;

    public HomepageListViewAdapter(Context context) {
        this.mContext = context;
    }

    public void setListViewCallback(HomepageListViewCallback callback) {
        mCallback = callback;
    }

    @Override
    public int getCount() {
        return mItemList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemInfo info = mItemList.get(position);
        if (convertView == null) {
            convertView = createViewByItemInfo(info);
        }
        setupViewByItemInfo(convertView, info);
        return convertView;
    }

    private View createViewByItemInfo(ItemInfo info) {
        View view = null;
        if (info.type == ITEM_TYPE_TRANSPARENT) {
            if (mTransparentView == null) {
                mTransparentView = createTransparentView();
            }
            view = mTransparentView;
        } else if (info.type == ITEM_TYPE_IMAGE_TEXT) {
            view = View.inflate(mContext, R.layout.homepage_item_image_text, null);
        } else if (info.type == ITEM_TYPE_LEFT_IMAGE_TEXT) {
            view = View.inflate(mContext, R.layout.homepage_item_left_pic, null);
        } else if (info.type == ITEM_TYPE_LITTLE_IMAGE_TEXT_ONE) {
            view = View.inflate(mContext, R.layout.homepage_item_little_image_text_one, null);
        } else if (info.type == ITEM_TYPE_DATE) {
            view = View.inflate(mContext, R.layout.homepage_item_date, null);
        } else if (info.type == ITEM_TYPE_LITTLE_IMAGE_TEXT_TWO) {
            view = View.inflate(mContext, R.layout.homepage_item_little_image_text_two, null);
        } else if (info.type == ITEM_TYPE_RIGHT_IMAGE_TEXT) {
            view = View.inflate(mContext, R.layout.homepage_item_right_pic, null);
        } else if (info.type == ITEM_TYPE_IMAGE) {
            view = View.inflate(mContext, R.layout.homepage_item_pic, null);
        }

        return view;
    }

    private void setupViewByItemInfo(View view, ItemInfo info) {
        if (info.type == ITEM_TYPE_IMAGE_TEXT) {
            setupImageTextView(view, (FeedInfo) info.object);
        } else if (info.type == ITEM_TYPE_LEFT_IMAGE_TEXT) {
            setupLeftImageTextView(view, (FeedInfo) info.object);
        } else if (info.type == ITEM_TYPE_LITTLE_IMAGE_TEXT_ONE) {
            setupLittleImageOneView(view, (FeedInfo) info.object);
        } else if (info.type == ITEM_TYPE_LITTLE_IMAGE_TEXT_TWO) {
            setupLittleImageTwoView(view, (FeedInfo) info.object);
        } else if (info.type == ITEM_TYPE_DATE) {
            setupDateView(view, (String) info.object);
        } else if (info.type == ITEM_TYPE_RIGHT_IMAGE_TEXT) {
            setupRightImageTextView(view, (FeedInfo) info.object);
        } else if (info.type == ITEM_TYPE_IMAGE) {
            setupImageView(view, (FeedInfo) info.object);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_ITEM_TYPE;
    }

    public void updateFeedList(ArrayList<FeedInfo> list) {
        mItemList.clear();

        ItemInfo info = new ItemInfo();
        info.type = ITEM_TYPE_TRANSPARENT;
        mItemList.add(info);

        mFeedInfos.clear();
        mFeedInfos.addAll(list);

        String date = null;
        String tempDate = null;
        if (!mFeedInfos.isEmpty()) {
            for (FeedInfo feedInfo : mFeedInfos) {
                tempDate = feedInfo.getPublishtime();
                if (shouldCreateTimeView(date, tempDate)) {
                    info = new ItemInfo();
                    info.object = tempDate;
                    info.type = ITEM_TYPE_DATE;
                    mItemList.add(info);
                }
                date = tempDate;
                info = new ItemInfo();
                int type = StringUtils.parseInt(feedInfo.getStyleType());
                if (type == ITEM_TYPE_IMAGE_TEXT) {
                    info.type = ITEM_TYPE_IMAGE_TEXT;
                    info.object = feedInfo;
                } else if (type == ITEM_TYPE_LEFT_IMAGE_TEXT) {
                    info.type = ITEM_TYPE_LEFT_IMAGE_TEXT;
                    info.object = feedInfo;
                } else if (type == ITEM_TYPE_LITTLE_IMAGE_TEXT_ONE) {
                    info.type = ITEM_TYPE_LITTLE_IMAGE_TEXT_ONE;
                    info.object = feedInfo;
                } else if (type == ITEM_TYPE_LITTLE_IMAGE_TEXT_TWO) {
                    info.type = ITEM_TYPE_LITTLE_IMAGE_TEXT_TWO;
                    info.object = feedInfo;
                } else if (type == ITEM_TYPE_IMAGE) {
                    info.type = ITEM_TYPE_IMAGE;
                    info.object = feedInfo;
                } else if (type == ITEM_TYPE_RIGHT_IMAGE_TEXT) {
                    info.type = ITEM_TYPE_RIGHT_IMAGE_TEXT;
                    info.object = feedInfo;
                } else {
                    info.type = ITEM_TYPE_DATE;
                    info.object = feedInfo.getPublishtime();
                }
                mItemList.add(info);
            }
        }
        notifyDataSetChanged();
    }

    private boolean shouldCreateTimeView(String date, String nextDate) {
        if (date == null && nextDate != null) {
            //判断是否为今天，如果是今天，不显示日期
            date = nextDate;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = df.format(new Date());
            if (StringUtils.isNotEmpty(date) && currentDate != null) {
                currentDate = currentDate.trim();
                date = new String(date).trim();
                if (date.compareTo(currentDate) != 0) {
                    return true;
                }
            }
        } else if (date != null && nextDate != null) {
            if (!date.equalsIgnoreCase(nextDate)) {
                return true;
            }
        }

        return false;
    }

    private View createTransparentView() {
        View view = new View(mContext);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setEnabled(false);
        view.setMinimumHeight(ResourceHelper.getDimen(R.dimen.homepage_toplayout_height));
        return view;
    }


    public boolean isTransparentViewVisible() {
        if (mFirstVisibleItem == POS_TRANSPARENT_VIEW) {
            return true;
        }
        return false;
    }

    public Rect getTransparentViewHitRect() {
        Rect rect = new Rect();
        if (mTransparentView != null) {
            mTransparentView.getHitRect(rect);
        }
        return rect;
    }

    private void setupImageTextView(View feedView, final FeedInfo feedInfo) {
        final boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());

        SimpleDraweeView contentImage = (SimpleDraweeView) feedView.findViewById(R.id
                .image_text_image);
        TextView title = (TextView) feedView.findViewById(R.id.image_text_title);
        TextView content = (TextView) feedView.findViewById(R.id.image_text_content);
        final TextView likeBtn = (TextView) feedView.findViewById(R.id.image_text_like);
        TextView operationBtn = (TextView) feedView.findViewById(R.id.image_text_btn);
        TextView commentBtn = (TextView) feedView.findViewById(R.id.image_text_comment);

        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.TEXT_TYPE_TEST) {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_test));
        } else {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_article));
        }
        title.setText(feedInfo.getTitle());
        String brief = feedInfo.getBrief().replaceAll("\r\n", "\n");
        content.setText(brief);
        String imageUrl = feedInfo.getImageUrl();
        if (StringUtils.isNotEmpty(imageUrl)) {
            Uri uri = Uri.parse(imageUrl);
            contentImage.setAspectRatio(1 / 0.58f);
            contentImage.setImageURI(uri);

        }

        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_CIRCLE) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_TOPIC) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            if (isPraise) {
                likeBtn.setSelected(true);
            } else {
                likeBtn.setSelected(false);
            }
            likeBtn.setVisibility(View.VISIBLE);
            likeBtn.setText(feedInfo.getPraiseCount());
        }

        final FeedInfo feed = feedInfo;
        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        operationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
                setPraise(isPraise, feed);
                int praiseCount = StringUtils.parseInt(likeBtn.getText().toString());
                if (isPraise) {
                    likeBtn.setText((praiseCount - 1) + "");
                    likeBtn.setSelected(false);
                } else {
                    likeBtn.setText((praiseCount + 1) + "");
                    likeBtn.setSelected(true);
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
    }

    private void setupLeftImageTextView(View feedView, final FeedInfo feedInfo) {
        final boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());

        SimpleDraweeView contentImage = (SimpleDraweeView) feedView.findViewById(R.id
                .left_pic_image);
        TextView title = (TextView) feedView.findViewById(R.id.left_pic_title);
        LinearLayout userInfo = (LinearLayout) feedView.findViewById(R.id.left_pic_user_info);
        TextView userName = (TextView) feedView.findViewById(R.id.left_pic_user_name);
        SimpleDraweeView userImage = (SimpleDraweeView) feedView.findViewById(R.id.left_pic_user_image);
        TextView content = (TextView) feedView.findViewById(R.id.left_pic_content);
        final TextView likeBtn = (TextView) feedView.findViewById(R.id.left_pic_like);
        TextView commentBtn = (TextView) feedView.findViewById(R.id.left_pic_comment);

        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_CIRCLE) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_TOPIC) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            if (isPraise) {
                likeBtn.setSelected(true);
            } else {
                likeBtn.setSelected(false);
            }
            likeBtn.setVisibility(View.VISIBLE);
            likeBtn.setText(feedInfo.getPraiseCount());
        }

        String imageUrl = feedInfo.getImageUrl();
        if (StringUtils.isNotEmpty(imageUrl)) {
            Uri uri = Uri.parse(imageUrl);
            contentImage.setImageURI(uri);
            contentImage.setAspectRatio(0.68f);
        }
        title.setText(feedInfo.getTitle());
        String brief = feedInfo.getBrief().replaceAll("\r\n", "\n");
        content.setText(brief);
        userName.setText(feedInfo.getNickName());
        String iconUrl = feedInfo.getIconUrl();
        if (StringUtils.isNotEmpty(iconUrl)) {
            userImage.setImageURI(Uri.parse(iconUrl));
        }


        final FeedInfo feed = feedInfo;
        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_PERSONAL);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
                setPraise(isPraise, feed);
                int praiseCount = StringUtils.parseInt(likeBtn.getText().toString());
                if (isPraise) {
                    likeBtn.setText((praiseCount - 1) + "");
                    likeBtn.setSelected(false);
                } else {
                    likeBtn.setText((praiseCount + 1) + "");
                    likeBtn.setSelected(true);
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
    }

    private void setupLittleImageOneView(View feedView, final FeedInfo feedInfo) {
        final boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
        SimpleDraweeView contentImage = (SimpleDraweeView) feedView.findViewById(R.id.little_image_text_one_image);
        TextView userName = (TextView) feedView.findViewById(R.id.little_image_text_one_user_name);
        TextView content = (TextView) feedView.findViewById(R.id.little_image_text_one_content);
        final TextView likeBtn = (TextView) feedView.findViewById(R.id.little_image_text_one_like);
        TextView commentBtn = (TextView) feedView.findViewById(R.id.little_image_text_one_comment);
        TextView operationBtn = (TextView) feedView.findViewById(R.id.little_image_text_one_btn);
        TextView title = (TextView) feedView.findViewById(R.id.little_image_text_one_title);

        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.TEXT_TYPE_TEST) {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_test));
        } else {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_article));
        }

        title.setText(feedInfo.getTitle());
        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_CIRCLE) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_TOPIC) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            if (isPraise) {
                likeBtn.setSelected(true);
            } else {
                likeBtn.setSelected(false);
            }
            likeBtn.setVisibility(View.VISIBLE);
            likeBtn.setText(feedInfo.getPraiseCount());
        }
        String imageUrl = feedInfo.getIconUrl();
        if (StringUtils.isNotEmpty(imageUrl)) {
            Uri uri = Uri.parse(imageUrl);
            contentImage.setImageURI(uri);
        }
        userName.setText(ResourceHelper.getString(R.string.homepage_item_title_prefix) + " " + feedInfo.getNickName());
        String brief = feedInfo.getBrief().replaceAll("\r\n", "\n");
        content.setText(brief);

        final FeedInfo feed = feedInfo;
        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_PERSONAL);
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_PERSONAL);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
                setPraise(isPraise, feed);
                int praiseCount = StringUtils.parseInt(likeBtn.getText().toString());
                if (isPraise) {
                    likeBtn.setText((praiseCount - 1) + "");
                    likeBtn.setSelected(false);
                } else {
                    likeBtn.setText((praiseCount + 1) + "");
                    likeBtn.setSelected(true);
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        operationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
    }

    private void setupLittleImageTwoView(View feedView, final FeedInfo feedInfo) {
        final boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
        SimpleDraweeView contentImage = (SimpleDraweeView) feedView.findViewById(R.id.little_image_text_two_image);
        TextView title = (TextView) feedView.findViewById(R.id.little_image_text_two_title);
        LinearLayout userInfo = (LinearLayout) feedView.findViewById(R.id.little_image_text_two_user_info);
        SimpleDraweeView userImage = (SimpleDraweeView) feedView.findViewById(R.id.little_image_text_two_user_image);
        TextView userName = (TextView) feedView.findViewById(R.id.little_image_text_two_user_name);
        TextView content = (TextView) feedView.findViewById(R.id.little_image_text_two_content);
        final TextView likeBtn = (TextView) feedView.findViewById(R.id.little_image_text_two_like);
        TextView commentBtn = (TextView) feedView.findViewById(R.id.little_image_text_two_comment);
        TextView operationBtn = (TextView) feedView.findViewById(R.id.little_image_text_two_btn);


        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.TEXT_TYPE_TEST) {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_test));
        } else {
            operationBtn.setText(ResourceHelper.getString(R.string.homepage_item_btn_text_article));
        }

        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_CIRCLE) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_TOPIC) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            if (isPraise) {
                likeBtn.setSelected(true);
            } else {
                likeBtn.setSelected(false);
            }
            likeBtn.setVisibility(View.VISIBLE);
            likeBtn.setText(feedInfo.getPraiseCount());
        }
        String imageUrl = feedInfo.getImageUrl();
        if (StringUtils.isNotEmpty(imageUrl)) {
            Uri uri = Uri.parse(imageUrl);
            contentImage.setAspectRatio(1 / 0.58f);
            contentImage.setImageURI(uri);
        }
        title.setText(feedInfo.getTitle());
        userName.setText(feedInfo.getNickName());
        String iconUrl = feedInfo.getIconUrl();
        if (StringUtils.isNotEmpty(iconUrl)) {
            userImage.setImageURI(Uri.parse(iconUrl));
        }
        String brief = feedInfo.getBrief().replaceAll("\r\n", "\n");
        content.setText(brief);

        final FeedInfo feed = feedInfo;
        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_PERSONAL);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
                setPraise(isPraise, feed);
                int praiseCount = StringUtils.parseInt(likeBtn.getText().toString());
                if (isPraise) {
                    likeBtn.setText((praiseCount - 1) + "");
                    likeBtn.setSelected(false);
                } else {
                    likeBtn.setText((praiseCount + 1) + "");
                    likeBtn.setSelected(true);
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        operationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
    }

    private void setupRightImageTextView(View feedView, final FeedInfo feedInfo) {
        final boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
        TextView title = (TextView) feedView.findViewById(R.id.right_pic_title);
        TextView content = (TextView) feedView.findViewById(R.id.right_pic_content);
        final TextView likeBtn = (TextView) feedView.findViewById(R.id.right_pic_like);
        TextView commentBtn = (TextView) feedView.findViewById(R.id.right_pic_comment);
        SimpleDraweeView userImage = (SimpleDraweeView) feedView.findViewById(R.id.right_pic_image);
        TextView userName = (TextView) feedView.findViewById(R.id.right_pic_user_name);
        LinearLayout userInfoArea = (LinearLayout) feedView.findViewById(R.id.right_pic_user_info_area);


        if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_ASK_ANSWER) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_CIRCLE) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT_DETAIL) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_TOPIC) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_PRODUCT) {
            commentBtn.setVisibility(View.GONE);
            likeBtn.setVisibility(View.GONE);
        } else {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setText(feedInfo.getCommentCount());
            if (isPraise) {
                likeBtn.setSelected(true);
            } else {
                likeBtn.setSelected(false);
            }
            likeBtn.setVisibility(View.VISIBLE);
            likeBtn.setText(feedInfo.getPraiseCount());
        }

        title.setText(feedInfo.getTitle());
        content.setText(feedInfo.getBrief());
        String userImageUrl = feedInfo.getIconUrl();
        if (StringUtils.isNotEmpty(userImageUrl)) {
            userImage.setImageURI(Uri.parse(userImageUrl));
        }
        userName.setText("by " + feedInfo.getNickName());

        final FeedInfo feed = feedInfo;
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = PraiseHelper.isSavePraiseStateInLocal(mContext, feedInfo.getAttrValue());
                setPraise(isPraise, feed);
                int praiseCount = StringUtils.parseInt(likeBtn.getText().toString());
                if (isPraise) {
                    likeBtn.setText((praiseCount - 1) + "");
                    likeBtn.setSelected(false);
                } else {
                    likeBtn.setText((praiseCount + 1) + "");
                    likeBtn.setSelected(true);
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
        userInfoArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_PERSONAL);
            }
        });
    }

    private void setupImageView(View feedView, final FeedInfo feedInfo) {
        SimpleDraweeView image = (SimpleDraweeView) feedView.findViewById(R.id.homepage_pic_image);
        TextView title = (TextView) feedView.findViewById(R.id.homepage_pic_title);
        TextView imageNum = (TextView) feedView.findViewById(R.id.homepage_pic_image_num);
        FrameLayout allArea = (FrameLayout) feedView.findViewById(R.id.homepage_pic_area);

        String imageUrl = feedInfo.getImageUrl();
        if (StringUtils.isNotEmpty(imageUrl)) {
            image.setImageURI(Uri.parse(imageUrl));
        }
        final FeedInfo feed = feedInfo;
        title.setText(feedInfo.getTitle());
        int imageCount = 0;
        if (StringUtils.isNotEmpty(feedInfo.getImageCount())) {
            imageNum.setText(feedInfo.getImageCount() + " photos");
        } else {
            imageNum.setText(imageCount + " photos");
        }

        allArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFeedClick(feed, HomepageConst.TURN_TYPE_DETAILE);
            }
        });
    }

    private void setupDateView(View dateView, String dateString) {
        dateView.setClickable(true);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateString;
        String postfix = "";
        try {
            Calendar current = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            today.set(Calendar.YEAR, current.get(Calendar.YEAR));
            today.set(Calendar.MONTH, current.get(Calendar.MONTH));
            today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
            yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
            yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);

            Calendar tommorrow = Calendar.getInstance();
            tommorrow.set(Calendar.YEAR, current.get(Calendar.YEAR));
            tommorrow.set(Calendar.MONTH, current.get(Calendar.MONTH));
            tommorrow.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) + 1);
            tommorrow.set(Calendar.HOUR_OF_DAY, 0);
            tommorrow.set(Calendar.MINUTE, 0);
            tommorrow.set(Calendar.SECOND, 0);

            Date date = fmt.parse(dateString);
            Calendar articleDate = Calendar.getInstance();
            articleDate.setTime(date);
            articleDate.set(Calendar.SECOND, 10);

            if (articleDate.before(today) && articleDate.after(yesterday)) {
                postfix = mContext.getResources().getString(R.string.yesterday);
            } else {
                int index = articleDate.get(Calendar.DAY_OF_WEEK) - 1;
                if (index < 0 || index >= CommonConst.WEEK_STRINGS.length) {
                    index = 0;
                }
                postfix = CommonConst.WEEK_STRINGS[index];
            }
        } catch (Exception e) {
            e.printStackTrace();
            postfix = "";
            result = dateString;
        }
        result = result + " " + postfix;
        TextView titleView = (TextView) dateView.findViewById(R.id.homepage_date_title);
        titleView.setText(result);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //do something??
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mItemList.size()) {
            return;
        }
        ItemInfo itemInfo = mItemList.get(position);
        if (!(itemInfo.object instanceof FeedInfo)) {
            return;
        }
        FeedInfo feedInfo = (FeedInfo) itemInfo.object;

        if (mCallback != null) {
            mCallback.onFeedClick(feedInfo, HomepageConst.TURN_TYPE_DETAILE);
        }

    }


    private class ItemInfo {
        public int type;
        public Object object;
    }

    public interface HomepageListViewCallback {
        void onFeedClick(FeedInfo info, int turnType);
    }

    private void setPraise(boolean isPraise, FeedInfo feedInfo) {
        String title = feedInfo.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = feedInfo.getFid();
        }
        String typeValue = "post";
        if (isPraise) {
            if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_POST) {
                typeValue = "post";
                PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_POST, StringUtils.parseInt(feedInfo.getAttrValue()), 0);
            } else {
                typeValue = "article";
                PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_ARTICLE, StringUtils.parseInt(feedInfo.getAttrValue()
                ), 0);
            }
            PraiseHelper.removePraiseState(ACache.get(mContext), feedInfo.getAttrValue());
        } else {
            if (StringUtils.parseInt(feedInfo.getAttrType()) == HomepageConst.FEED_TYPE_POST) {
                typeValue = "post";
                PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_POST, StringUtils.parseInt(feedInfo.getAttrValue()), 1);
            } else {
                typeValue = "article";
                PraiseHelper.sendPraise(CommonConst.PRAISE_TYPE_ARTICLE, StringUtils.parseInt(feedInfo.getAttrValue()
                ), 1);
            }
            PraiseHelper.savePraiseState(ACache.get(mContext), feedInfo.getAttrValue());
        }
        HashMap<String, String> map = new HashMap<>(2);
        map.put("type", typeValue);
        map.put("spec", title);
        StatsModel.stats(StatsKeyDef.HOMEPAGE_LIKE, map);
    }
}
