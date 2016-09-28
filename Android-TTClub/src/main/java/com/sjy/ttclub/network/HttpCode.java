package com.sjy.ttclub.network;

/**
 * Created by zwl on 2015/11/26.
 * Email: 1501448275@qq.com
 */
public class HttpCode {
    /**
     * 接口状态返回码
     */
    public static final int ERROR_NETWORK = -1000;
    public static final int ERROR_CANCEL = -1001;
    public static final int SUCCESS_CODE = 0;//请求成功
    public static final int FAIL_CODE = 100;//操作失败
    public static final int ILLEGAL_CODE = 101;//非法请求
    public static final int INVALID_PLATFORM_CODE = 102;//无效平台
    public static final int INVALID_VERSON_CODE = 103;//无效版本
    public static final int INVALID_TOKEN_CODE = 104;//无效token
    public static final int LEVEL_NOT_ENOUGH_CODE = 105;//等级不足
    public static final int FROZEN_USER_CODE = 106;//用户被冻结
    public static final int BANNED_CODE = 107;//用户被禁言
    public static final int FAIL_ANALYSIS_CODE = -1;//JSON解析失败
    public static final int JSON_IS_EMPTY = -2;//JSON串为空

    public static final int MAN_LIMIT = 306; //限制男生
    public static final int HAVE_BLACK = 303; //限制男生
    public static final int CONTENT_LIMIT = 308; //限制内容

    public static final int ERR_VICODE = 401;//验证码错误
    public static final int ERR_HAS_REGISTER = 402;//用户已注册
    public static final int ERR_NOT_REGISTER = 403;//用户未注册
    public static final int ERROR_PASSWORD = 404;//密码错误
    public static final int ERROR_PROHIBIT_LOGIN = 405;//用户禁止登录
    public static final int ERR_TTP_TOKEN = 406;//第三方登录access_token失效

    public static final int ERROR_CODE_601 = 601;//商品不存在
    public static final int ERROR_CODE_602 = 602;//已经评论过了
    public static final int ERROR_CODE_604 = 604;//商品不足，请重新购买
    public static final int ERROR_CODE_605 = 605;//商品失效,请刷新购物车
    public static final int ERROR_CODE_606 = 606;//结算价格错误
    public static final int ERROR_CODE_607 = 607;//服务器繁忙，请稍后再试
    public static final int ERROR_CODE_608 = 608;//支付类型错误
    public static final int ERROR_CODE_CONTENT_TOO_LONG = 628;//内容过多
    public static final int ERROR_CODE_CONTENT_FREQUENCY = 638;//操作过频繁

}
