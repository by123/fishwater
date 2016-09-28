package com.sjy.ttclub.emoji;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils
 *
 * @author zhongdaxia 2014-9-2 12:05:55
 */

public class EmoticonsRexgexUtils {

    public static ArrayList<EmoticonBean> emoticonBeanList = null;

    private static int getFontHeight(TextView tv) {
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    public static boolean setTextFace(final Context context, TextView tv, String content, Object spannable, int
            width, int height) {
        boolean isEmoticonMatcher = false;
        int fontHeight = height;
        if (width == EmoticonsEditText.WRAP_FONT && tv != null) {
            fontHeight = getFontHeight(tv);
        }

        Pattern p = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");
        Matcher m = p.matcher(content);
        if (m != null) {
            while (m.find()) {
                if (emoticonBeanList == null) {
                    DBHelper dbHelper = new DBHelper(context);
                    emoticonBeanList = dbHelper.queryAllEmoticonBeans();
                    dbHelper.cleanup();
                    if (emoticonBeanList == null) {
                        return isEmoticonMatcher;
                    }
                }

                int start = m.start();
                int end = m.end();
                String key = content.substring(start, end).toString();
                for (EmoticonBean bean : emoticonBeanList) {
                    if (!TextUtils.isEmpty(bean.getContent()) && bean.getContent().equals(key)) {
                        Drawable drawable = ImageLoader.getInstance(context).getDrawable(bean.getIconUri());
                        if (drawable != null) {
                            int itemHeight;
                            if (height == EmoticonsEditText.WRAP_DRAWABLE) {
                                itemHeight = drawable.getIntrinsicHeight();
                            } else if (height == EmoticonsEditText.WRAP_FONT) {
                                itemHeight = fontHeight;
                            } else {
                                itemHeight = height;
                            }
                            int itemWidth;
                            if (width == EmoticonsEditText.WRAP_DRAWABLE) {
                                itemWidth = drawable.getIntrinsicWidth();
                            } else if (width == EmoticonsEditText.WRAP_FONT) {
                                itemWidth = fontHeight;
                            } else {
                                itemWidth = width;
                            }

                            drawable.setBounds(0, 0, itemHeight, itemWidth);

                            VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                            if (spannable instanceof SpannableString) {
                                ((SpannableString) spannable).setSpan(imageSpan, start, end, Spannable
                                        .SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (spannable instanceof SpannableStringBuilder) {
                                ((SpannableStringBuilder) spannable).setSpan(imageSpan, start, end, Spannable
                                        .SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            isEmoticonMatcher = true;
                        }
                    }
                }
            }
        }
        return isEmoticonMatcher;
    }

    public static boolean setTextFace2(final Context context, TextView tv, final String userName, final String content, int width, int height) {
        boolean isEmoticonMatcher = false;
        int fontHeight = height;
        if (width == EmoticonsEditText.WRAP_FONT && tv != null) {
            fontHeight = getFontHeight(tv);
        }

        String str = "";
        str = userName + ":" + content;
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.clearSpans();
         /*int bstart = str.indexOf("楼主");
        int bend = bstart + "楼主".length();
        if (reply.getIsHost()== CommunityConstant.IS_HOST) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.community_host_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            try {
                style.setSpan(span, bstart, bend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            } catch (Exception e) {
            }
        }*/
        style.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                // TODO Auto-generated method stub

            }
        }, 0, userName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        if (StringUtils.isNotEmpty(userName)) {
            style.setSpan(new ForegroundColorSpan(context
                    .getResources()
                    .getColor(R.color.community_pink)), 0, userName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        Pattern p = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");
        Matcher m = p.matcher(str);
        if (m != null) {
            while (m.find()) {
                if (emoticonBeanList == null) {
                    DBHelper dbHelper = new DBHelper(context);
                    emoticonBeanList = dbHelper.queryAllEmoticonBeans();
                    dbHelper.cleanup();
                    if (emoticonBeanList == null) {
                        return isEmoticonMatcher;
                    }
                }
                int start = m.start();
                int end = m.end();
                String key = str.substring(start, end).toString();
                for (EmoticonBean bean : emoticonBeanList) {
                    if (!TextUtils.isEmpty(bean.getContent()) && bean.getContent().equals(key)) {
                        Drawable drawable = ImageLoader.getInstance(context).getDrawable(bean.getIconUri());
                        if (drawable != null) {
                            int itemHeight;
                            if (height == EmoticonsEditText.WRAP_DRAWABLE) {
                                itemHeight = drawable.getIntrinsicHeight();
                            } else if (height == EmoticonsEditText.WRAP_FONT) {
                                itemHeight = fontHeight;
                            } else {
                                itemHeight = height;
                            }
                            int itemWidth;
                            if (width == EmoticonsEditText.WRAP_DRAWABLE) {
                                itemWidth = drawable.getIntrinsicWidth();
                            } else if (width == EmoticonsEditText.WRAP_FONT) {
                                itemWidth = fontHeight;
                            } else {
                                itemWidth = width;
                            }

                            drawable.setBounds(0, 0, itemHeight, itemWidth);

                            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                            try {
                                style.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            isEmoticonMatcher = true;
                        }
                    }
                }
            }
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(style);
        return isEmoticonMatcher;
    }
}
