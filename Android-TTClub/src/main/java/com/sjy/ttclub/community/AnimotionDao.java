package com.sjy.ttclub.community;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimotionDao {

    //向上平移进入动画
    public static TranslateAnimation getTranslateUpVisible() {
        TranslateAnimation mShowAction1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                3.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction1.setDuration(500);
        return mShowAction1;
    }

    //向下移出消失动画
    public static TranslateAnimation getTranslateDownHidden() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                3.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }

    //点赞伸缩动画
    public static ScaleAnimation getScaleAnimation(boolean isfillafter) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setInterpolator(new DecelerateInterpolator());
        if (isfillafter) {
            scale.setFillAfter(true);
        } else {
            scale.setFillAfter(false);
        }
        scale.setDuration(300);
        return scale;
    }
    //偷窥抖动动画
    public static ScaleAnimation getPeepScaleAnimation(boolean isfillafter) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setInterpolator(new CycleInterpolator(1f));
        scale.setRepeatCount(5);
        if (isfillafter) {
            scale.setFillAfter(true);
        } else {
            scale.setFillAfter(false);
        }
        scale.setDuration(200);
        return scale;
    }
    public static TranslateAnimation getPeepShakeAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,0, 10);
        translateAnimation.setInterpolator(new CycleInterpolator(7f));
        translateAnimation.setDuration(300);
        return translateAnimation;
    }
    //点赞伸缩动画
    public static ScaleAnimation getScaleAnimationShow(boolean isfillafter) {
        ScaleAnimation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setInterpolator(new DecelerateInterpolator());
        if (isfillafter) {
            scale.setFillAfter(true);
        } else {
            scale.setFillAfter(false);
        }
        scale.setDuration(500);
        return scale;
    }

    //淡入动画
    public static AlphaAnimation getAlphaAnimationShow() {
        AlphaAnimation alphaShow = new AlphaAnimation(0, 1);
        alphaShow.setDuration(500);
        return alphaShow;
    }
    public static AlphaAnimation getAlphaAnimationShow(int time) {
        AlphaAnimation alphaShow = new AlphaAnimation(0, 1);
        alphaShow.setDuration(time);
        return alphaShow;
    }
    //淡出动画
    public static AlphaAnimation getAlphaAnimationHide() {
        AlphaAnimation alphaHide = new AlphaAnimation(1, 0);
        alphaHide.setDuration(100);
        return alphaHide;
    }
}
