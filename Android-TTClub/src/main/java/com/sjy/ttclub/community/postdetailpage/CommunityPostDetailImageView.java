package com.sjy.ttclub.community.postdetailpage;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.bean.community.ImageCard;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.photopreview.PhotoPreviewInfo;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwulin on 2015/12/17.
 * email 1501448275@qq.com
 */
public class CommunityPostDetailImageView extends LinearLayout {
    private Context mContext;
    private SimpleDraweeView mImageView;
    private ImageCard mImageCard;
    private CommunityPostBean mPost;
    private boolean ENABLE_LOG =false;
    public CommunityPostDetailImageView(Context context) {
        this(context, null);
    }

    public CommunityPostDetailImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityPostDetailImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mImageView = new SimpleDraweeView(mContext);
        mImageView.setPadding(ResourceHelper.getDimen(R.dimen.space_20),ResourceHelper.getDimen(R.dimen.space_10),ResourceHelper.getDimen(R.dimen.space_20),ResourceHelper.getDimen(R.dimen.space_0));
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(mImageView, lp);
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleImageViewOnClick();
            }
        });
    }

    public void setCardDetailImageView(ImageCard imageCard) {
        this.mImageCard = imageCard;
        setmImageView(imageCard);
    }

    public void setPost(CommunityPostBean post) {
        this.mPost = post;
    }

    private void handleImageViewOnClick() {
        if (mPost == null) {
            return;
        }
        if (mImageCard == null) {
            return;
        }

        int position = mPost.getImages().indexOf(mImageCard);
        if (position < 0) {
            position = 0;
        }

        ArrayList<String> imageList = new ArrayList<String>();
        List<ImageCard> list = mPost.getImages();
        for (ImageCard imageCard : list) {
            imageList.add(imageCard.getImageUrl());
        }

        PhotoPreviewInfo info = new PhotoPreviewInfo();
        info.photoList = imageList;
        info.position = position;

        Message msg = Message.obtain();
        msg.what = MsgDef.MSG_SHOW_PHOTO_PREVIEW_WINDOW;
        msg.obj = info;
        MsgDispatcher.getInstance().sendMessage(msg);
    }

    private void setmImageView(ImageCard imageCard) {
        GenericDraweeHierarchy hierarchy = mImageView.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
        mImageView.setHierarchy(hierarchy);
        if(ENABLE_LOG) {
            Log.i("getImageWidth()", imageCard.getImageWidth() + "");
            Log.i("getImageHeight()", imageCard.getImageHeight() + "");
            Log.i("setAspectRatio", imageCard.getImageWidth() / (float) (imageCard.getImageHeight()) + "");
        }
        if (imageCard.getImageWidth() == 0 || imageCard.getImageHeight() == 0) {
            mImageView.setAspectRatio(1.0f);
        } else {
            float temp = ((float) imageCard.getImageWidth()) / imageCard.getImageHeight();
            mImageView.setAspectRatio(temp);
        }
        if (StringUtils.isEmpty(mImageCard.getImageUrl())) {
            return;
        }
        mImageView.setImageURI(Uri.parse(imageCard.getImageUrl()));
    }
}
