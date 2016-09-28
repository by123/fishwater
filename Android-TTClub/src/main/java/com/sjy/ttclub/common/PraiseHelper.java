package com.sjy.ttclub.common;

import android.content.Context;

import com.sjy.ttclub.network.HttpCode;
import com.sjy.ttclub.network.HttpManager;
import com.sjy.ttclub.network.HttpMethod;
import com.sjy.ttclub.network.HttpUrls;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.network.IHttpManager;
import com.sjy.ttclub.util.ACache;
import com.sjy.ttclub.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

/**
 * Created by linhz on 2015/12/23.
 * Email: linhaizhong@ta2she.com
 */
public class PraiseHelper {

    public static void sendPraise(int praiseType, int id, int isPraise) {
        sendPraise(praiseType, String.valueOf(id), isPraise);
    }

    public static void sendPraise(int praiseType, String id, int isPraise) {
        if (praiseType == CommonConst.PRAISE_TYPE_POST) {
            //帖子点赞
            sendPraise("postId", id, "postType", CommonConst.PRAISE_TYPE_POST, "praisePost", isPraise);
        } else if (praiseType == CommonConst.PRAISE_TYPE_ARTICLE) {
            //文章点赞
            sendPraise("postId", id, "postType", CommonConst.PRAISE_TYPE_ARTICLE, "praisePost", isPraise);
        } else if (praiseType == CommonConst.PRAISE_TYPE_POST_COMMENTS) {
            //帖子评论点赞
            sendPraise("commentId", id, "cmtType", CommonConst.PRAISE_TYPE_POST_COMMENTS, "praiseComment",
                    isPraise);
        } else if (praiseType == CommonConst.PRAISE_TYPE_ARTICLE_COMMENTS) {
            //文章评论点赞
            sendPraise("commentId", id, "cmtType", CommonConst.PRAISE_TYPE_ARTICLE_COMMENTS,
                    "praiseComment", isPraise);
        }
    }

    public static void sendPraise(String praiseKey, String id, String type, int typeValue, String
            action, int isPraise) {
        IHttpManager httpManager = HttpManager.getBusinessHttpManger();
        httpManager.addParams("a", action);
        httpManager.addParams(praiseKey, id);
        httpManager.addParams(type, String.valueOf(typeValue));
        httpManager.addParams("praiseFlag", String.valueOf(isPraise));
        httpManager.request(HttpUrls.COMMUNITY_URL, HttpMethod.POST, new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                if (StringUtils.isNotEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("status");
                        if (HttpCode.SUCCESS_CODE == code) {
                            //点赞成功
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String errorStr, int code) {

            }
        });
    }

    /**
     * 保存点赞状态在本地
     *
     * @param cache
     * @param id
     */
    public static void savePraiseState(final ACache cache, int id) {
        savePraiseState(cache, String.valueOf(id));
    }

    public static void savePraiseState(final ACache cache, String id) {
        try {
            cache.put(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePraiseState(final Context context, String id) {
        if (context == null) {
            return;
        }
        try {
            ACache.get(context).put(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePraiseState(final Context context, int id) {
        savePraiseState(context, String.valueOf(id));
    }

    /**
     * 移除本地点赞状态
     *
     * @param cache
     * @param id
     */
    public static void removePraiseState(final ACache cache, int id) {
        removePraiseState(cache, String.valueOf(id));
    }

    public static void removePraiseState(final ACache cache, String id) {
        try {
            cache.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePraiseState(final Context context, int id) {
        removePraiseState(context, String.valueOf(id));
    }

    public static void removePraiseState(final Context context, String id) {
        if (context == null) {
            return;
        }
        try {
            ACache.get(context).remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断点赞是否保存在本地
     *
     * @param cache
     * @param id
     * @return
     */
    public static boolean isSavePraiseStateInLocal(final ACache cache, int id) {
        return isSavePraiseStateInLocal(cache, String.valueOf(id));
    }

    public static boolean isSavePraiseStateInLocal(final ACache cache, String id) {
        boolean isSave;
        try {
            if (cache.get(id) != null) {
                isSave = true;
            } else {
                isSave = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isSave = false;
        }
        return isSave;
    }

    public static boolean isSavePraiseStateInLocal(final Context context, int id) {
        return isSavePraiseStateInLocal(context, String.valueOf(id));
    }

    public static boolean isSavePraiseStateInLocal(final Context context, String id) {
        boolean isSave;
        if (context == null) {
            return false;
        }
        try {
            if (ACache.get(context).get(id) != null) {
                isSave = true;
            } else {
                isSave = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isSave = false;
        }
        return isSave;
    }
}
