package com.sjy.ttclub.praise;

import android.content.Context;

import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.bean.community.CommunityPostBean;
import com.sjy.ttclub.community.CommunityConstant;
import com.sjy.ttclub.util.ACache;

import java.io.FileNotFoundException;

public class PraiseUtil {
    public static void sendPraise(final Context context, int praiseType, int postId, int isPraise) {
    }


    /**
     * 保存点赞状态在本地
     *
     * @param mCache
     * @param postId
     */
    public static void savePraiseState(final ACache mCache, int postId) {
        try {
            mCache.put(String.valueOf(postId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePraiseState(final Context context, int postId) {
        if (context == null) {
            return;
        }
        try {
            ACache.get(context).put(String.valueOf(postId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除本地点赞状态
     *
     * @param mCache
     * @param postId
     */
    public static void removePraiseState(final ACache mCache, int postId) {
        try {
            mCache.remove(String.valueOf(postId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePraiseState(final Context context, int postId) {
        if (context == null) {
            return;
        }
        try {
            ACache.get(context).remove(String.valueOf(postId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断点赞是否保存在本地
     *
     * @param mCache
     * @param postId
     * @return
     */
    public static boolean isSavePraiseStateInLocal(final ACache mCache, int postId) {
        boolean isSave;
        try {
            if (mCache.get(String.valueOf(postId)) != null) {
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

    public static boolean isSavePraiseStateInLocal(final Context context, int postId) {
        boolean isSave;
        if (context == null) {
            return false;
        }
        try {
            if (ACache.get(context).get(String.valueOf(postId)) != null) {
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

    public static boolean isPraised(final Context context, final CommunityPostBean card) {
        boolean isPraised;
        if (AccountManager.getInstance().isLogin()) {
            if (card.getIsPraise() == CommunityConstant.NO_PRAISE) {
                isPraised = false;
            } else {
                isPraised = true;
            }
        } else {
            if (isSavePraiseStateInLocal(context, card.getPostId())) {
                isPraised = true;
            } else {
                isPraised = false;
            }
        }
        return isPraised;
    }
}
