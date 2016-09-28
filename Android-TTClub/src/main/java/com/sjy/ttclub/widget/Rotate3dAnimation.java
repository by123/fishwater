package com.sjy.ttclub.widget;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by zhangwulin on 2016/1/11.
 * email 1501448275@qq.com
 */
public class Rotate3dAnimation extends Animation{

    // 3d rotate
    private float mFromDegrees;
    private float mToDegrees;
    private float mCenterX;
    private float mCenterY;

    private Camera mCamera;

    public Rotate3dAnimation(float fromDegrees, float toDegrees)
    {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
        mCenterX = width / 2;
        mCenterY = height / 2;
        mCamera = new Camera();
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + (mToDegrees - mFromDegrees) * interpolatedTime;
        Matrix matrix = t.getMatrix();
        Camera camera = new Camera();
        camera.save();
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }
}
