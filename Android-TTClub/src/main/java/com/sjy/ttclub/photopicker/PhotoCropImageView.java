package com.sjy.ttclub.photopicker;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.ResourceHelper;

/**
 * Created by linhz on 2015/12/28.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoCropImageView extends View implements OnScaleGestureListener, OnTouchListener {

    private static final float SCALE_MAX = 3.0f;
    private static final float SCLAE_MIN = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private GestureDetector mGestureDetector;
    private float mInitScale;

    private Bitmap mBitmap;
    private Bitmap mOptBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private final Paint mDimPaint = new Paint();
    private boolean mIsInit = false;
    private boolean mIsCircle = true;

    private RectF mCropRect = new RectF();
    private Path mPath = new Path();
    private Matrix mDrawMatrix = new Matrix();
    private PaintFlagsDrawFilter mDrawFilter;

    private boolean mToDrawHighlight = true;
    private int mBottomMargin;

    public PhotoCropImageView(Context context) {
        this(context, null);
    }

    public PhotoCropImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
        initialize();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        setOnTouchListener(this);
    }

    private void initialize() {

        HardwareUtil.setLayerType(this, HardwareUtil.LAYER_TYPE_SOFTWARE);
        int strokeWidth = ResourceHelper.getDimen(R.dimen.space_1);
        mDimPaint.setColor(ResourceHelper.getColor(R.color.photo_crop_view_dim_color));

        mDimPaint.setStrokeWidth(strokeWidth);
        mDimPaint.setStyle(Paint.Style.STROKE);
        mDimPaint.setAntiAlias(true);
        mDimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    }

    private void initCropRect(int windowWidth, int windowHeight) {
        boolean isOritentalVertical = windowHeight > windowWidth;
        int margin = ResourceHelper.getDimen(R.dimen.photo_crop_rect_horizontal_marin);
        mBottomMargin = ResourceHelper.getDimen(R.dimen.photo_crop_rect_bottom_margin);
        int squareWidth = isOritentalVertical ? windowWidth : (windowHeight - mBottomMargin);
        squareWidth = squareWidth - margin * 2;
        int x, y;
        if (isOritentalVertical) {
            x = margin;
            y = (windowHeight - squareWidth) / 2;
        } else {
            x = (windowWidth - squareWidth) / 2;
            y = margin;
        }
        mCropRect = new RectF(x, y, x + squareWidth, y + squareWidth);
    }

    private Bitmap getProperBitmap(Bitmap bitmap, float requiredSize) {
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleX = requiredSize / width;
            float scaleY = requiredSize / height;
            mInitScale = scaleX > scaleY ? scaleX : scaleY;
            matrix.postScale(mInitScale, mInitScale);
            Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return bm;
        }
        return null;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsInit = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        if (!mIsInit) {
            initCropRect(getWidth(), getHeight());
            mOptBitmap = getProperBitmap(mBitmap, mCropRect.width());
            if (mOptBitmap == null) {
                return;
            }
            mBitmapWidth = mOptBitmap.getWidth();
            mBitmapHeight = mOptBitmap.getHeight();
            int deltaX = getWidth() > mBitmapWidth ? (getWidth() - mBitmapWidth) / 2 : 0;
            int deltaY = 0;
            if (getWidth() < getHeight()) {
                deltaY = getHeight() > mBitmapHeight ? (getHeight() - mBitmapHeight) / 2 : 0;
            } else {
                deltaY = getHeight() - mBottomMargin > mBitmapHeight ? (getHeight() - mBottomMargin - mBitmapHeight)
                        / 2 : 0;
            }
            mDrawMatrix.reset();
            mDrawMatrix.postTranslate(deltaX, deltaY);
            mIsInit = true;
        }
        canvas.setDrawFilter(mDrawFilter);
        canvas.save();
        canvas.concat(mDrawMatrix);
        canvas.drawBitmap(mOptBitmap, 0, 0, null);
        canvas.restore();

        if (mToDrawHighlight) {
            canvas.save();
            mPath.reset();
            if (mIsCircle) {
                float radius = mCropRect.width() / 2;
                mPath.addCircle(mCropRect.left + radius, mCropRect.top + radius, radius, Path.Direction.CW);
            } else {
                mPath.addRect(mCropRect, Path.Direction.CW);
            }
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.drawPaint(mDimPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsInit) {
            return true;
        }
        if (mInitScale > SCALE_MAX) {
            return true;
        }
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        invalidate();
        return true;
    }

    private RectF getMatrixRectF() {
        Matrix matrix = mDrawMatrix;
        RectF rect = new RectF();
        rect.set(0, 0, mBitmapWidth, mBitmapHeight);
        matrix.mapRect(rect);
        return rect;
    }

    private class MyGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mDrawMatrix.postTranslate(-distanceX, -distanceY);
            checkBorderWhenTranslate();
            return true;
        }
    }

    private void checkBorderWhenTranslate() {
        RectF rect = getMatrixRectF();
        float deltaX = 0f;
        float deltaY = 0f;
        if (rect.top > mCropRect.top) {
            deltaY = mCropRect.top - rect.top;
        }
        if (rect.left > mCropRect.left) {
            deltaX = mCropRect.left - rect.left;
        }
        if (rect.bottom < mCropRect.bottom) {
            deltaY = mCropRect.bottom - rect.bottom;
        }
        if (rect.right < mCropRect.right) {
            deltaX = mCropRect.right - rect.right;
        }
        mDrawMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        float[] values = new float[9];
        mDrawMatrix.getValues(values);
        float curScale = values[Matrix.MSCALE_X];
        if ((curScale < SCALE_MAX && scaleFactor > 1.0f) || (curScale > SCLAE_MIN && scaleFactor < 1.0f)) {
            if (scaleFactor * curScale < SCLAE_MIN) {
                scaleFactor = SCLAE_MIN / curScale;
            }
            if (scaleFactor * curScale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / curScale;
            }
            mDrawMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
        }

        return true;
    }

    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mDrawMatrix.postTranslate(deltaX, deltaY);

        float scaleX = 0f;
        float scaleY = 0f;
        if (rect.width() < mCropRect.width()) {
            scaleX = mCropRect.width() / rect.width();
        }
        if (rect.height() < mCropRect.height()) {
            scaleY = mCropRect.height() / rect.height();
        }
        if (scaleX > 0 || scaleY > 0) {
            float scale = scaleX < scaleY ? scaleY : scaleX;
            mDrawMatrix.postScale(scale, scale);
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // Donothing
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    public Bitmap getClipBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            prepareToClipImage();
            draw(canvas);
            restoreAfterClipImage();
            return Bitmap.createBitmap(bitmap, (int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.width(),
                    (int) mCropRect.height());
        }
        return null;
    }

    private void prepareToClipImage() {
        mToDrawHighlight = false;
        invalidate();
    }

    private void restoreAfterClipImage() {
        mToDrawHighlight = true;
        invalidate();
    }
}
