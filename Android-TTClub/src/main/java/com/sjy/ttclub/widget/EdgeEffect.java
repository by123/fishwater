
package com.sjy.ttclub.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class EdgeEffect {

    public final static String TAG = "EdgeEffect";
    private final static float EPSILON = 0.001f;

    private final static int RECEDE_TIME = 1000; // 回退时间 ms
    private final static int PULL_TIME = 167; // 自动回退前的等待时间 ms
    private final static int PULL_DECAY_TIME = 1000; // 自动回退时间 ms

    private final static float MAX_ALPHA = 1.f; // 最大透明度
    private final static float MAX_GLOW_HEIGHT = 1.5f; // 最大高度为图片高度的多少倍
    private final static int PULL_DISTANCE_GLOW_FACTOR = 7;
    private final static float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 1.1f;
    private final static float PULL_GLOW_BEGIN = 1.f; // 光晕在回退时onPull的起始大小

    private final static int STATE_IDLE = 0;
    private final static int STATE_PULL = 1;
    private final static int STATE_RECEDE = 2;
    private final static int STATE_PULL_DECAY = 3;

    private Drawable mGlow = null;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mState = STATE_IDLE;
    private float mPullDistance = 0;

    private float mGlowAlpha = 0;
    private float mGlowScale = 0;
    private float mGlowAlphaStart = 0;
    private float mGlowAlphaFinish = 0;
    private float mGlowScaleStart = 0;
    private float mGlowScaleFinish = 0;

    private long mStartTime;
    private float mDuration;
    private Interpolator mInterpolator;
    private boolean mAllowRecedeWhileTouching = false;

    public EdgeEffect() {
        this(null);
    }

    public EdgeEffect(Drawable drawable) {
        mInterpolator = new DecelerateInterpolator();
        mGlow = drawable;
        if (mGlow != null) {
            mWidth = mGlow.getIntrinsicWidth();
            mHeight = mGlow.getIntrinsicHeight();
        }
    }

    public boolean draw(Canvas canvas) {
        if (mGlow != null) {
            update();
            mGlow.setAlpha((int) (Math.max(0, Math.min(mGlowAlpha, 1)) * 255));
            int glowWidth = (int) (mWidth * mGlowScale);
            mGlow.setBounds(0, 0, glowWidth, mHeight);
            mGlow.draw(canvas);
        } else {
            mState = STATE_IDLE;
        }
        return mState != STATE_IDLE;
    }

    private void update() {

        final long time = AnimationUtils.currentAnimationTimeMillis();
        final float t = Math.min((time - mStartTime) / mDuration, 1.f);

        final float interp = mInterpolator.getInterpolation(t);
        mGlowAlpha = mGlowAlphaStart + (mGlowAlphaFinish - mGlowAlphaStart)
                * interp;
        mGlowScale = mGlowScaleStart + (mGlowScaleFinish - mGlowScaleStart)
                * interp;

        if (t >= 1.f - EPSILON) {
            switch (mState) {
            case STATE_PULL:
                // 是否允许当用户还按住时的自动消退
                if (mAllowRecedeWhileTouching) {
                    mState = STATE_PULL_DECAY;
                    mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    mDuration = PULL_DECAY_TIME;

                    mGlowAlphaStart = mGlowAlpha;
                    mGlowScaleStart = mGlowScale;
                    mGlowAlphaFinish = 0.f;
                    mGlowScaleFinish = 0.f;
                }
                break;
            case STATE_PULL_DECAY:
                mState = STATE_RECEDE;
                break;
            case STATE_RECEDE:
                mState = STATE_IDLE;
                break;
            }
        }
    }

    public void onPull(float deltaDistance) {

        if (mGlow == null) {
            mState = STATE_IDLE;
            return;
        }

        final long now = AnimationUtils.currentAnimationTimeMillis();
        if (mState == STATE_PULL_DECAY && now - mStartTime < mDuration) {
            return;
        }
        if (mState != STATE_PULL) {
            mGlowScale = PULL_GLOW_BEGIN;
        }
        mState = STATE_PULL;
        mStartTime = now;
        mDuration = PULL_TIME;

        mPullDistance += deltaDistance;
        float glowChange = Math.abs(deltaDistance);

        // 支持逆向滑动消退
        if (deltaDistance > 0 && mPullDistance < 0) {
            glowChange = -glowChange;
        } else if (deltaDistance < 0 && mPullDistance > 0) {
            glowChange = -glowChange;
        }

        // 计算新的透明度，最大为 MAX_ALPHA
        mGlowAlpha = mGlowAlphaStart = Math.min(
                MAX_ALPHA,
                Math.max(0, mGlowAlpha + glowChange
                        * PULL_DISTANCE_ALPHA_GLOW_FACTOR));

        // 计算新的放大尺度，最大为 MAX_GLOW_HEIGHT
        mGlowScale = mGlowScaleStart = Math.min(MAX_GLOW_HEIGHT, Math.max(0,
                mGlowScale + glowChange * PULL_DISTANCE_GLOW_FACTOR));

        mGlowAlphaFinish = mGlowAlpha;
        mGlowScaleFinish = mGlowScale;
    }

    public void onRelease() {

        if (mGlow == null) {
            mState = STATE_IDLE;
            return;
        }

        if (mState != STATE_PULL && mState != STATE_PULL_DECAY) {
            return;
        }
        mPullDistance = 0;
        mState = STATE_RECEDE;
        mDuration = RECEDE_TIME;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();

        mGlowAlphaStart = mGlowAlpha;
        mGlowScaleStart = mGlowScale;
        mGlowAlphaFinish = 0.f;
        mGlowScaleFinish = 0.f;
    }

    public void finish() {
        mState = STATE_IDLE;
    }

    public boolean isFinished() {
        return mState == STATE_IDLE;
    }

    public boolean isAllowRecedeWhileTouching() {
        return mAllowRecedeWhileTouching;
    }

    public void allowRecedeWhileTouching(boolean flag) {
        this.mAllowRecedeWhileTouching = flag;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setDrawable(Drawable drawable) {
        mGlow = drawable;
        if (mGlow != null) {
            mWidth = mGlow.getIntrinsicWidth();
            mHeight = mGlow.getIntrinsicHeight();
        }
    }

}
