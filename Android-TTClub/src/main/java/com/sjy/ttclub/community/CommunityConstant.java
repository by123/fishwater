package com.sjy.ttclub.community;

import com.sjy.ttclub.network.HttpCode;

/**
 * @author: 张武林
 * @类 说   明:
 * @创建时间：${date}
 */
public class CommunityConstant {

    public static final int DEFAULT_DELAY_TIME=300;
    public static final int COMMENT_TOP_REPLY_COUNT = 2;
    public static final int ONLY_HOST = 1;
    public static final int NO_ONLY_HOST = 0;
    public static final int ASCEND_ORDER = 1;
    public static final int DROP_ORDER = 2;
    //上传的图片最大尺寸
    public static final int POST_IMAGE_MAX_SIZE = 8 * 1024 * 0124;
    //需要压缩的图片最小尺寸
    public static final int POST_IMAGE_MIN_COMPRESS_SIZE = 2 * 1024 * 1024;
    //可显示的最大点赞数
    public static final int MAX_COUNTS = 9999;
    //超过最大点赞数显示的数值
    public static final String MAX_SHOW_VALUE = "10K";
    //Ta问ta答
    public static final int TYPE_QA_POST = 2;
    //普通帖子
    public static final int TYPE_POST = 1;
    //不确定性别
    public static final int SEX_UNDEFINED=0;
    //未点赞
    public static final int NO_PRAISE = 0;
    //已点赞
    public static final int PRAISED = 1;
    //未问
    public static final int NO_ASK = 0;
    //已问
    public static final int ASKED = 1;
    //未关注
    public static final int NO_ATTETION = 0;
    //已关注
    public static final int HAVE_ATTETION = 1;
    //加关注
    public static final int ADD_ATTETION = 1;
    //取消关注
    public static final int CANCEL_ATTETION = 0;
    //点赞
    public static final int PRAISE_CARD = 1;//帖子
    public static final int PRAISE_ARTICLE = 2;//文章
    public static final int PRAISE_CARD_COMMENTS = 3;//帖子评论
    public static final int PRAISE_ARTICLE_COMMENTS = 4;//文章评论
    public static final int PRAISE_ADD = 1;//添加点赞
    public static final int PRAISE_CANCEL = 0;//取消点赞

    /*文章帖子点赞*/
    public static final int PRAISE_TYPE_POST = 1;//帖子
    public static final int PRAISE_TYPE_ARTICLE = 2;//文章
    /*评论点赞*/
    public static final int COMMENT_PRAISE_TYPE_POST = 1;//帖子评论
    public static final int COMMENTS_PRAISE_TYPE_ARTICLE = 2;//文章评论
    public static final int COMMENTS_TYPE_POST = 1;//帖子评论
    public static final int COMMENTS_TYPE_ARTICLE = 2;//文章评论

    /**
     * 获取帖子接口类型
     */
    public static final int GET_POST_LIST_HOTEST = 1;//获取最热帖子
    public static final int GET_POST_LIST_CREAM = 2;//获取精华帖子
    public static final int GET_POST_LIST_NEWEST = 3;//获取最新帖子
     /**
     * 获取数据相关
     */
    public static final int START_PAGE_INDEX = 1;
    public static final int START_END_ID = 0;

    public static final int INVALIDE_TYPE = -1000;

    public static final int PAGE_SIZE_CARD = 20;
    public static final int PAGE_SIZE_TEST = 8;

    public static final int ERROR_TYPE_DATA = 1;
    public static final int ERROR_TYPE_NETWORK = HttpCode.ERROR_NETWORK;
    public static final int ERROR_TYPE_REQUESTING = 3;
    public static final int ERROR_TYPE_EMPTY_DATA = 4;
    public static final int ERROR_TYPE_INVALID_TOKEN = 104;
    public static final int ERROR_TYPE_CARD_NOT_EXIST = 21;
    public static final int ERROR_TYPE_COMMENT_NOT_EXIST = 108;
    public static final int ERROR_TYPE_CIRCEL_NOT_EXIST = 108;
    /**
     * 发帖
     */
    public static final int POST_CONTENT_MAX_LENGTH = 10000;
    public static final int POST_CONTENT_TYPE_TEXT = 2;//以文为主
    public static final int POST_CONTENT_TYPE_IMAGE = 1;//以图为主
    public static final int POST_CONTENT_ANNOY=1;//匿名发帖
    public static final int POST_CONTENT_NOT_ANNOY=0;//公開发帖
    /**
     * 圈子类型
     */
    public static final int CIRCLE_TYPE_QA_POST = 2;
    public static final int CIRCLE_TYPE_POST = 1;
    /**
     * 收藏
     */
    public static final int COLLECT_CARD_TYPE = 1;//收藏帖子
    public static final int COLLECT_ARTICLE_TYPE = 2;//收藏文章
    public static final int ADD_COLLECT_TYPE = 1;//添加收藏
    public static final int CANCEL_COLLECT_TYPE = 0;//取消收藏
    public static final int COLLECT_ERROR_TYPE_FAIL = 0;//收藏失败
    public static final int HAVE_COLLECT_FLAG = 1;
    public static final int NO_COLLECT_FLAG = 0;

    /**
     * 帖子
     */
    public static final int IS_HOST = 1;
    public static final int NOT_HOST = 0;

    /**
     * 发送评论
     */
    public static final int POST_TYPE_COMMENT = 1;
    public static final int POST_TYPE_REPLY = 2;
    public static final int CONTENT_TYPE_CARD = 1;
    public static final int CONTENT_TYPE_ARTICLE = 2;
    public static final int POST_COMMENT_NO_COMMENTID = 0;
    public static final int COMMENT_MAX_LINE = 3;

    /**
     * 用户角色
     */
    public static final int USER_ROLE_GENERAL = 1;
    public static final int USER_ROLE_GIRL_GODDESS = 2;
    public static final int USER_ROLE_EDITOR = 3;
    public static final int USER_ROLE_TESTING_EXPRESS = 4;

    /**
     * 私信对象
     */
    public static final int TYPE_CHAT_OBJECT_GENERAL = 1;
    public static final int TYPE_CHAT_OBJECT_OFFICIAL = 2;
    public static final int CHAT_DEFAULT_ID = 0;

    /**
     * 私信默认等级
     */
    public static final int PRIVATE_LETTER_DEFAULT_LEVEL = 5;

    /**
     * 帖子内容分类
     */
    public static final int COMMUNITY_CONTENT_TYPE_HOME = 1;
    public static final int COMMUNITY_CONTENT_TYPE_HOT = 2;
    public static final int COMMUNITY_CONTENT_TYPE_ALL_CARD = 3;
    public static final int COMMUNITY_CONTENT_TYPE_CREAM_CARD = 4;
    public static final int COMMUNITY_CONTENT_TYPE_NEW_QA = 5;
    public static final int COMMUNITY_CONTENT_TYPE_CREAM_QA = 6;

    /**
     * 圈子tab
     */
    public static final int COMMUNITY_TAB_INDEX_HOTEST = 0;
    public static final int COMMUNITY_TAB_INDEX_NEWEST = 1;
    public static final int COMMUNITY_TAB_INDEX_CREAM = 2;

    /**
     * 发评论
     */
    public static final int COMMENT_LIMIT_MAN = 1;
    public static final int COMMENT_NOT_LIMIT_MAN =0;
    /**
     * 圈子属性
     */
    public static final int CIRCLE_LIMIT_MAN =1;
}
