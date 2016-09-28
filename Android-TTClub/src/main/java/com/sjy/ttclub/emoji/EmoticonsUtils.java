package com.sjy.ttclub.emoji;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

import com.sjy.ttclub.bean.community.CommentBean;
import com.sjy.ttclub.bean.community.CommentReplyBean;
import com.sjy.ttclub.util.ThreadManager;

import java.util.ArrayList;


public class EmoticonsUtils {

    /**
     * 初始化表情数据库
     *
     * @param context
     */
    public static void initEmoticonsDB(final Context context) {
        if (!Utils.isInitDb()) {
            ThreadManager.execute(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(context);

                    /**
                     * FROM ASSETS
                     */
                    ArrayList<EmoticonBean> xhsfaceArray = ParseData(feelingsArray, EmoticonBean.FACE_TYPE_NOMAL,
                            ImageBase.Scheme.ASSETS);
                    EmoticonSetBean xhsEmoticonSetBean = new EmoticonSetBean("feelings", 3, 7);
                    xhsEmoticonSetBean.setIconUri("assets://emoji/emoji_icon.png");
                    xhsEmoticonSetBean.setItemPadding(15);
                    xhsEmoticonSetBean.setVerticalSpacing(7);
                    xhsEmoticonSetBean.setShowDelBtn(true);
                    xhsEmoticonSetBean.setEmoticonList(xhsfaceArray);
                    dbHelper.insertEmoticonSet(xhsEmoticonSetBean);


                    dbHelper.cleanup();
                    Utils.setIsInitDb(true);
                }
            });

        }
    }

    public static EmoticonsKeyboardBuilder getSimpleBuilder(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryEmoticonSet("feelings", "feelings");
        dbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet();
        dbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static ArrayList<EmoticonBean> ParseData(String[] arry, long eventType, ImageBase.Scheme scheme) {
        try {
            ArrayList<EmoticonBean> emojis = new ArrayList<EmoticonBean>();
            for (int i = 0; i < arry.length; i++) {
                if (!TextUtils.isEmpty(arry[i])) {
                    String temp = arry[i].trim().toString();
                    String[] text = temp.split(",");
                    if (text != null && text.length == 2) {
                        String fileName = null;
                        if (scheme == ImageBase.Scheme.DRAWABLE) {
                            if (text[0].contains(".")) {
                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
                            } else {
                                fileName = scheme.toUri(text[0]);
                            }
                        } else {
                            fileName = scheme.toUri(text[0]);
                        }
                        String content = text[1];
                        EmoticonBean bean = new EmoticonBean(eventType, fileName, content);
                        emojis.add(bean);
                    }
                }
            }
            return emojis;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setContent(Context con, TextView tv_content, String content) {
        tv_content.setText(content);
        CharSequence text = tv_content.getText();
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.clearSpans();
        EmoticonsRexgexUtils.setTextFace(con, tv_content, content, style, EmoticonsEditText.WRAP_FONT,
                EmoticonsEditText.WRAP_FONT);
        tv_content.setText(style);
    }
    public static void setContent2(Context con,TextView tv_content, String userName,String content) {
        EmoticonsRexgexUtils.setTextFace2(con, tv_content, userName,content, EmoticonsEditText.WRAP_FONT, EmoticonsEditText.WRAP_FONT);
    }
    /**
     * 自定义表情
     */
    public static String[] feelingsArray = {
            "emoji/feelings_01.png,[微笑]",
            "emoji/feelings_02.png,[撇嘴]",
            "emoji/feelings_03.png,[色]",
            "emoji/feelings_04.png,[发呆]",
            "emoji/feelings_05.png,[得意]",
            "emoji/feelings_06.png,[流泪]",
            "emoji/feelings_07.png,[害羞]",
            "emoji/feelings_08.png,[闭嘴]",
            "emoji/feelings_09.png,[睡]",
            "emoji/feelings_10.png,[大哭]",
            "emoji/feelings_11.png,[尴尬]",
            "emoji/feelings_12.png,[发怒]",
            "emoji/feelings_13.png,[调皮]",
            "emoji/feelings_14.png,[呲牙]",
            "emoji/feelings_15.png,[惊讶]",
            "emoji/feelings_16.png,[难过]",
            "emoji/feelings_17.png,[酷]",
            "emoji/feelings_18.png,[冷汗]",
            "emoji/feelings_19.png,[抓狂]",
            "emoji/feelings_20.png,[吐]",
            "emoji/feelings_21.png,[偷笑]",
            "emoji/feelings_22.png,[可爱]",
            "emoji/feelings_23.png,[白眼]",
            "emoji/feelings_24.png,[饥饿]",
            "emoji/feelings_25.png,[困]",
            "emoji/feelings_26.png,[惊恐]",
            "emoji/feelings_27.png,[憨笑]",
            "emoji/feelings_28.png,[拍砖]",
            "emoji/feelings_29.png,[嘘]",
            "emoji/feelings_30.png,[晕]",
            "emoji/feelings_31.png,[囧]",
            "emoji/feelings_32.png,[衰]",
            "emoji/feelings_33.png,[再见]",
            "emoji/feelings_34.png,[恶魔喜]",
            "emoji/feelings_35.png,[恶魔怒]",
            "emoji/feelings_36.png,[柴犬笑]",
            "emoji/feelings_37.png,[柴犬色]",
            "emoji/feelings_38.png,[坏笑]",
            "emoji/feelings_39.png,[抠鼻]",
            "emoji/feelings_40.png,[委屈]",
            "emoji/feelings_41.png,[亲亲]",
            "emoji/feelings_42.png,[无语]",
            "emoji/feelings_43.png,[贱笑]",
            "emoji/feelings_44.png,[鄙视]",
            "emoji/feelings_45.png,[可怜]",
            "emoji/feelings_46.png,[惊吓]",
            "emoji/feelings_47.png,[猪]",
            "emoji/feelings_48.png,[心碎]",
            "emoji/feelings_49.png,[爱心]",
            "emoji/feelings_50.png,[礼物]",
            "emoji/feelings_51.png,[骨头]",
            "emoji/feelings_52.png,[刀]",
            "emoji/feelings_53.png,[好]",
            "emoji/feelings_54.png,[顶]",
            "emoji/feelings_55.png,[踩]",
            "emoji/feelings_56.png,[玫瑰]",
            "emoji/feelings_57.png,[嘴唇]",
            "emoji/feelings_58.png,[便便]",
            "emoji/feelings_59.png,[抱抱]",
            "emoji/feelings_60.png,[茶]",
            "emoji/feelings_61.png,[月亮]",
            "emoji/feelings_62.png,[太阳]",
            "emoji/feelings_63.png,[闪电]",
            "emoji/feelings_64.png,[饮品]",
            "emoji/feelings_65.png,[蛋糕]",
            "emoji/feelings_66.png,[棒棒糖]",
    };

}
