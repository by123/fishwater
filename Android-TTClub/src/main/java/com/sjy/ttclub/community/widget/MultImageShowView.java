package com.sjy.ttclub.community.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * @author 张武林
 *         没有数据和网络的背景布局
 */
public class MultImageShowView extends LinearLayout implements OnClickListener {
    private Context context;
    private SimpleDraweeView draweeView1;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams lp;
    private CommunityPostBean card;
    private static int mScreenWidth;

    /**
     * @param context
     */
    public MultImageShowView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public MultImageShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public MultImageShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    public void setDatas(CommunityPostBean card) {
        this.card = card;
    }

    private void initView() {
        mScreenWidth = HardwareUtil.getDeviceWidth();
        setOrientation(HORIZONTAL);
    }

    public void setType(int value) {
        createImages(value);
    }

    private void createImages(int type) {
        if (type == 0) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        switch (type) {
            case 1:
                createOneImage();
                break;
            case 2:
                createTwoImage();
                break;
            default:
                createThreeImage();
                break;
        }
    }

    private void createOneImage() {
        removeAllViews();
        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(0).getImageUrl());
        addView(draweeView1, createOneImageLp());
    }

    private void createTwoImage() {
        removeAllViews();
        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(0).getImageUrl());
        addView(draweeView1, createOneToThreeImageLp());

        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(1).getImageUrl());
        addView(draweeView1, createOneToThreeImageLp());

    }

    private void createThreeImage() {
        removeAllViews();

        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(0).getImageUrl());
        addView(draweeView1, createOneToThreeImageLp());

        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(1).getImageUrl());
        addView(draweeView1, createOneToThreeImageLp());


        draweeView1 = new SimpleDraweeView(getContext());
        setImage(draweeView1, card.getImages().get(2).getImageUrl());
        addView(draweeView1, createOneToThreeImageLp());
    }

    public LinearLayout.LayoutParams createOneImageLp() {
        lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.width = mScreenWidth - ResourceHelper.getDimen(R.dimen.space_69);
        lp.height = lp.width * 1 / 3;
        return lp;
    }

    public LinearLayout.LayoutParams createOneToThreeImageLp() {
        lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = (mScreenWidth - ResourceHelper.getDimen(R.dimen.space_69)) / 3;
        lp.height = lp.width;
        return lp;
    }


    /**
     * 设置图片
     *
     * @param drawee
     * @param path
     */
    private void setImage(SimpleDraweeView drawee, String path) {
        drawee.setImageURI(Uri.parse(path));
    }

    @Override
    public void onClick(View v) {
    }
}
