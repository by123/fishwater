/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class ResourceHelper {
    private static Context sContext;

    public static final Drawable DEFAULT_BG_DRAWABLE = new ColorDrawable(
            Color.RED);
    public static final Drawable DEFAULT_ITEM_DRAEABLE = new ColorDrawable(
            Color.BLUE);
    public static final int DEFAULT_TEXT_COLOR = Color.GREEN;
    public static final int DEFAULT_TEXT_PRESSED_COLOR = Color.WHITE;
    public static final int DEFAULT_TEXT_DISABLE_COLOR = Color.GRAY;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static int getInteger(int resId){
        if (resId <= 0) {
            return 0;
        }
        int value = sContext.getResources().getInteger(resId);
        return value;
    }
    public static int getDimen(int resId) {
        if (resId <= 0) {
            return 0;
        }
        int dimen = sContext.getResources().getDimensionPixelSize(resId);
        return dimen;
    }

    public static Bitmap getBitmap(int resId) {
        if (resId <= 0) {
            return null;
        }
        Drawable drawable = sContext.getResources().getDrawable(resId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public static Drawable getDrawable(int resId) {
        if (resId <= 0) {
            return null;
        }

        Drawable drawable = sContext.getResources().getDrawable(resId);
        return drawable;
    }

    public static int getColor(int resId) {
        if (resId <= 0) {
            return 0;
        }
        return sContext.getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(int resId) {
        if (resId <= 0) {
            return null;
        }
        return sContext.getResources().getColorStateList(resId);
    }

    public static String getString(int resId) {
        if (resId <= 0) {
            return null;
        }
        return sContext.getResources().getString(resId);
    }

    public static StateListDrawable getColorDrawableStateList(
            int normalColorResId, int pressedColorResId) {
        int normalColor = getColor(normalColorResId);
        int pressedColor = getColor(pressedColorResId);
        return createColorDrawableStateList(normalColor, pressedColor);
    }

    public static StateListDrawable createColorDrawableStateList(
            int normalColor, int pressedColor) {
        StateListDrawable dr = new StateListDrawable();
        dr.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        dr.addState(new int[]{android.R.attr.state_checked},
                new ColorDrawable(pressedColor));
        dr.addState(new int[]{}, new ColorDrawable(normalColor));
        return dr;
    }

    public static StateListDrawable getDrawableStateList(int normalResId,
                                                         int pressedResId) {
        Drawable normal = getDrawable(normalResId);
        Drawable pressed = getDrawable(pressedResId);

        StateListDrawable dr = new StateListDrawable();
        dr.addState(new int[]{android.R.attr.state_pressed}, pressed);
        dr.addState(new int[]{android.R.attr.state_checked}, pressed);
        dr.addState(new int[]{}, normal);
        return dr;
    }

    public static Drawable createGradientDrawable(int startColor, int midColor,
                                                  int endColor) {

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                startColor, midColor, endColor});
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        return gradientDrawable;
    }

    public static int interpolateColor(int colorA, int colorB, float bias) {
        float[] hsvColorA = new float[3];
        Color.colorToHSV(colorA, hsvColorA);

        float[] hsvColorB = new float[3];
        Color.colorToHSV(colorB, hsvColorB);

        hsvColorB[0] = interpolate(hsvColorA[0], hsvColorB[0], bias);
        hsvColorB[1] = interpolate(hsvColorA[1], hsvColorB[1], bias);
        hsvColorB[2] = interpolate(hsvColorA[2], hsvColorB[2], bias);

        return Color.HSVToColor(hsvColorB);
    }

    private static float interpolate(float a, float b, float bias) {
        return (a + ((b - a) * bias));
    }

}
