/**
 * ****************************************************************************
 * Copyright (C) 2005-2013 UCWEB Corporation. All rights reserved
 * File        : 2013-11-30
 * <p/>
 * Description :
 * <p/>
 * Creation    : 2013-11-30
 * Author      : linhz@ucweb.com
 * History     : Creation, 2013-11-30, linhz, Create the file
 * ****************************************************************************
 */
package com.sjy.ttclub.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.sjy.ttclub.util.StringUtils;


public class ShareIntentBuilder {

    /**
     * 分享，弹出分享面板，用于其他activity调起分享功能，是通过broadcast进行的
     */
    public static final String ACTION_LOCAL_SHARE = "action_local_share";

    /**
     * 未知分享源
     */
    public static final int SOURCE_TYPE_UNKNOW = -1;
    /**
     * 分享自网页，即网页分享
     */
    public static final int SOURCE_TYPE_SHARE_PAGE = 0;
    /**
     * 分享链接
     */
    public static final int SOURCE_TYPE_SHARE_LINK = 1;
    /**
     * 分享图片
     */
    public static final int SOURCE_TYPE_SHARE_IMAGE = 2;
    /**
     * 分享文字
     */
    public static final int SOURCE_TYPE_SHARE_TEXT = 3;
    /**
     * 分享视频
     */
    public static final int SOURCE_TYPE_SHARE_VIDEO = 4;
    /**
     * 分享音乐
     */
    public static final int SOURCE_TYPE_SHARE_MUSIC = 5;

    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_IMAGE = "image/*";

    /**
     * 分享链接
     */
    public static final String SHARE_URL = "url";
    /**
     * 分享标题
     */
    public static final String SHARE_TITLE = "title";
    /**
     * 分享内容，注意最终的分享会拼接上链接，所以此内容里不应该再出现share url
     */
    public static final String SHARE_CONTENT = "content";
    /**
     * 摘要内容
     */
    public static final String SHARE_SUMMARY = "summary";
    /**
     * 分享类型设置，用于调起本地应用选择用
     */
    public static final String SHARE_MIME_TYPE = "mine_type";
    /**
     * 分享文件路径，目前只有图片路径
     */
    public static final String SHARE_FILE_PATH = "file";

    /**
     * 分享到的目标平台，直接走到目标平台，不经过平台选择
     */
    public static final String SHARE_TARGET_PLANFORM = "target";
    /**
     * 分享来源，比如分享网页，分享图片等
     */
    public static final String SHARE_SOURCE_TYPE = "source_type";

    public static final String STATS_KEY = "stats_key";


    private String mTitle;
    private String mContent;
    private String mMineType;
    private String mShareUrl;
    private String mImageUrl;
    private String mSummary;
    private int mSourceType;

    private String mTargetPlanform;

    private Bundle mStatsBundle;

    private ShareIntentBuilder() {

    }

    /**
     * 创建默认ShareIntentBuilder
     *
     * @return
     */
    public static ShareIntentBuilder obtain() {
        return new ShareIntentBuilder();
    }

    /**
     * 根据share intent创建ShareIntentBuilder
     *
     * @param intent
     * @return
     */
    public static ShareIntentBuilder obtain(Intent intent) {
        ShareIntentBuilder builder = new ShareIntentBuilder();
        builder.setShareContent(parseShareContent(intent));
        builder.setShareImageUrl(parseImageUrl(intent));
        builder.setShareMineType(parseShareMimeType(intent));
        builder.setShareSourceType(parseSourceType(intent));
        builder.setShareTitle(parseShareTitle(intent));
        builder.setShareUrl(parseShareUrl(intent));
        builder.setTargetPlatform(parseTargetPlatform(intent));
        builder.setStatsBundle(parseStatsBundle(intent));
        return builder;
    }

    public ShareIntentBuilder setStatsBundle(Bundle key) {
        mStatsBundle = key;
        return ShareIntentBuilder.this;
    }

    public Bundle getStatsBundle() {
        return mStatsBundle;
    }

    public static Bundle parseStatsBundle(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getBundleExtra(STATS_KEY);
    }

    /**
     * 设置分享title
     *
     * @param title
     * @return
     */
    public ShareIntentBuilder setShareTitle(String title) {
        mTitle = title;
        return ShareIntentBuilder.this;
    }

    public String getShareTitle() {
        return mTitle;
    }

    /**
     * 从intent获取分享title
     *
     * @param intent
     * @return
     */
    public static String parseShareTitle(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_TITLE);
    }

    /**
     * 设置分享内容, 注意：分享链接会被拼接到分享内容里的，所以不需要在分享内容添加<b>此链接</b>，否则会出现两个相同的链接
     *
     * @param content
     * @return
     */
    public ShareIntentBuilder setShareContent(String content) {
        mContent = content;
        return ShareIntentBuilder.this;
    }

    /**
     * 从intent获取分享内容
     *
     * @param intent
     * @return
     */
    public static String parseShareContent(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_CONTENT);
    }

    /**
     * 设置分享目标平台，如果设置了，不会出现分享平台选择面板
     *
     * @param targetPlatform
     * @return
     */
    public ShareIntentBuilder setTargetPlatform(String targetPlatform) {
        mTargetPlanform = targetPlatform;
        return ShareIntentBuilder.this;
    }

    /**
     * 获取分享目标平台
     *
     * @param intent
     * @return
     */
    public static String parseTargetPlatform(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_TARGET_PLANFORM);
    }

    /**
     * 设置分享链接，注意：分享链接会被拼接到分享内容里的，所以不需要在分享内容添加<b>此链接</b>，否则会出现两个相同的链接
     *
     * @param url
     * @return
     */
    public ShareIntentBuilder setShareUrl(String url) {
        mShareUrl = url;
        return ShareIntentBuilder.this;
    }

    /**
     * 从intent里获取分享链接
     *
     * @param intent
     * @return
     */
    public static String parseShareSummary(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_SUMMARY);
    }

    /**
     * 设置摘要内容
     *
     * @param summary
     * @return
     */
    public ShareIntentBuilder setShareSummary(String summary) {
        mSummary = summary;
        return ShareIntentBuilder.this;
    }

    /**
     * 从intent里获取分享链接
     *
     * @param intent
     * @return
     */
    public static String parseShareUrl(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_URL);
    }

    /**
     * 分享的mine type设置，一般用于调起本地应用分享时使用
     *
     * @return
     */
    public ShareIntentBuilder setShareMineType(String type) {
        mMineType = type;
        return ShareIntentBuilder.this;
    }

    /**
     * 获取分享mime type
     *
     * @param intent
     * @return
     */
    public static String parseShareMimeType(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_MIME_TYPE);
    }

    /**
     * 设置要分享的文件所在路径，目前只有图片路径
     *
     * @param path
     * @return
     */
    public ShareIntentBuilder setShareImageUrl(String path) {
        mImageUrl = path;
        return ShareIntentBuilder.this;
    }

    public String getFilePath() {
        return mImageUrl;
    }

    /**
     * 获取分享的文件所在路径，目前只有图片路径
     *
     * @param intent
     * @return
     */
    public static String parseImageUrl(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(SHARE_FILE_PATH);
    }

    /**
     * 设置分享源：比如分享网页?分享图片等
     *
     * @param type
     * @return
     */
    public ShareIntentBuilder setShareSourceType(int type) {
        mSourceType = type;
        return ShareIntentBuilder.this;
    }

    /**
     * 返回分享源：比如分享网页?分享图片等
     *
     * @return
     */
    public int getShareSourceType() {
        return mSourceType;
    }

    /**
     * 获取分享源：比如分享网页?分享图片等
     *
     * @param intent
     * @return
     */
    public static int parseSourceType(Intent intent) {
        if (intent == null) {
            return SOURCE_TYPE_UNKNOW;
        }
        return intent.getIntExtra(SHARE_SOURCE_TYPE, SOURCE_TYPE_UNKNOW);
    }

    /**
     * 创建分享流程使用的intent，不做真正的分享使用，最后的分享intent，请调用createStandardShareIntent
     *
     * @return
     */
    public Intent create() {
        Intent intent = new Intent();
        intent.setType(mMineType);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(SHARE_TITLE, mTitle);
        intent.putExtra(SHARE_URL, mShareUrl);
        intent.putExtra(SHARE_MIME_TYPE, mMineType);
        intent.putExtra(SHARE_CONTENT, mContent);
        intent.putExtra(SHARE_FILE_PATH, mImageUrl);
        intent.putExtra(SHARE_SOURCE_TYPE, mSourceType);
        intent.putExtra(SHARE_TARGET_PLANFORM, mTargetPlanform);
        intent.putExtra(SHARE_SUMMARY, mSummary);
        intent.putExtra(STATS_KEY, mStatsBundle);
        return intent;
    }

    /**
     * 创建系统标准分享inent，一般是走到最后进行分享的时候了
     *
     * @param shareIntent 待转换intent
     * @return
     */
    public static Intent createStandardShareIntent(Intent shareIntent) {
        String picPath = ShareIntentBuilder.parseImageUrl(shareIntent);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!StringUtils.isEmpty(picPath)) {
            intent.putExtra(ShareIntentBuilder.SHARE_FILE_PATH, picPath);
        }

        Uri uri = null;
        if (!StringUtils.isEmpty(picPath)) {
            if (!picPath.startsWith("http")) {
                if (!picPath.startsWith("file://") && !picPath.startsWith("content://")) {
                    picPath = "file://" + picPath;
                }
            }
            uri = Uri.parse(picPath);
        }

        intent.setComponent(shareIntent.getComponent());

        String type = ShareIntentBuilder.parseShareMimeType(shareIntent);
        intent.setType(type);

        if (uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        String content = ShareIntentBuilder.parseShareContent(shareIntent);
        String shareUrl = ShareIntentBuilder.parseShareUrl(shareIntent);

        String shareTitle = ShareIntentBuilder.parseShareTitle(shareIntent);
        intent.putExtra(ShareIntentBuilder.SHARE_TITLE, shareTitle);
        intent.putExtra(ShareIntentBuilder.SHARE_URL, shareUrl);
        intent.putExtra(ShareIntentBuilder.SHARE_CONTENT, content);

        int shareSourceType = ShareIntentBuilder.parseSourceType(shareIntent);
        intent.putExtra(ShareIntentBuilder.SHARE_SOURCE_TYPE, shareSourceType);
        String summary = ShareIntentBuilder.parseShareSummary(shareIntent);
        intent.putExtra(ShareIntentBuilder.SHARE_SUMMARY, summary);
        // mail title
        intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        //cat content and share url
        if (StringUtils.isNotEmpty(content)) {
            if (StringUtils.isNotEmpty(shareUrl)) {
                content = content + " " + shareUrl;
            }
        } else {
            if (StringUtils.isNotEmpty(shareUrl)) {
                content = shareUrl;
            }
        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        // for wechat
        intent.putExtra("Kdescription", content);
        intent.putExtra("hide_if_no_img", false);

        return intent;
    }

}
