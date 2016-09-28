package com.sjy.ttclub.pay.alipay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.pay.alipay.utils.Base64;
import com.sjy.ttclub.pay.alipay.utils.Result;
import com.sjy.ttclub.shopping.order.model.OrderRequest;
import com.sjy.ttclub.stats.StatsKeyDef;
import com.sjy.ttclub.stats.StatsModel;
import com.sjy.ttclub.widget.dialog.LoadingDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class AliPay {

    private Context mContext;

    private LoadingDialog mDialog;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private static final String ALGORITHM = "RSA";

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    public AliPay(Context context) {
        mContext = context;
        mDialog = new LoadingDialog(context);
        mDialog.setMessage(R.string.order_pay_go);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mDialog.dismiss();
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    Message message = Message.obtain();
                    if (TextUtils.equals(resultStatus, "9000")) {

                        //统计PRODUCT_PAYMENT
                        StatsModel.stats(StatsKeyDef.PRODUCT_PAYMENT, "payment", "alipay");

                        message.what = MsgDef.MSG_SHOW_ORDER_SUCCESS_WINDOW;
                    } else {
                        message.what = MsgDef.MSG_SHOW_ORDER_FAILED_WINDOW;
                    }
                    MsgDispatcher.getInstance().sendMessage(message);
                    break;
                }
                case SDK_CHECK_FLAG: {
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {

        //统计PRODUCT_PAYMENT
        StatsModel.stats(StatsKeyDef.PRODUCT_PAYMENT, "payment", "alipay");

        mDialog.show();
        String orderInfo = getOrderInfo(CommonConst.getGoodsTitle(mContext), CommonConst.getGoodsDescription(mContext), CommonConst.TOTALPRICE + "", CommonConst.ORDERNUM, CommonConst.getAliPayUrl(mContext));
        String sign = sign(orderInfo, CommonConst.getRSAPrivate(mContext));
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask((Activity) mContext);
                String result = payTask.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();

        OrderRequest.getInstance().updateOrder(3);//更新订单支付类型
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    private void check() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask((Activity) mContext);
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price, String orderNumber, String url) {
        // 合作者身份ID
        String orderInfo = "partner=" + "\"" + CommonConst.getAliPayPartner(mContext) + "\"";

        // 卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + CommonConst.getAliPaySeller(mContext) + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNumber + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // orderInfo += "&total_fee=" + "\"" + 0.01 + "\"";

        // 服务器异步通知页面路径"http://120.24.210.171:8081/washcar/payment/aliPaymentRespond"
        orderInfo += "&notify_url=" + "\"" + url + "\"";

        // 接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    public String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(encodedKeySpec);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
