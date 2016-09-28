package com.sjy.ttclub.pay.wechat;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.lsym.ttclub.BuildConfig;
import com.lsym.ttclub.R;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.network.IHttpCallBack;
import com.sjy.ttclub.pay.wechat.model.MicroPayInfo;
import com.sjy.ttclub.pay.wechat.model.MicroPayRequest;
import com.sjy.ttclub.shopping.order.model.OrderRequest;
import com.sjy.ttclub.util.Md5Utils;
import com.sjy.ttclub.widget.dialog.LoadingDialog;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by zhxu on 2016/1/5.
 * Email:357599859@qq.com
 */
public class MicroPay {

    private final static String TAG = "MicroPay";

    private Context mContext;
    private LoadingDialog mDialog;

    private final IWXAPI mApi;

    private List<MicroPayInfo> mList = new ArrayList<>();
    private List<MicroPayInfo> mReqList = new ArrayList<>();

    String[] mValues, mKeys = {"appid", "body", "mch_id", "nonce_str", "notify_url", "out_trade_no", "spbill_create_ip", "total_fee", "trade_type"};

    String[] mReqValues, mReqKeys = {"appid", "noncestr", "package", "partnerid", "prepayid", "timestamp"};

    public MicroPay(Context context) {
        mApi = WXAPIFactory.createWXAPI(context, null);
        mApi.registerApp(CommonConst.getWechatAPPID(context));
        mContext = context;
        mDialog = new LoadingDialog(mContext);
        mDialog.setMessage(R.string.order_pay_go);
    }

    public void sendPayReq() {
        mDialog.show();
        mValues = new String[mKeys.length];
        mValues[0] = CommonConst.getWechatAPPID(mContext);
        mValues[1] = CommonConst.getGoodsTitle(mContext);
        mValues[2] = CommonConst.getWechatMCHID(mContext);
        mValues[3] = genNonceStr();
        mValues[4] = CommonConst.getWeChatUrl(mContext);
        mValues[5] = CommonConst.ORDERNUM;
        mValues[6] = CommonConst.SPBILL_CREATE_IP_WECHAT;
        mValues[7] = ((int) (CommonConst.TOTALPRICE * 100)) + "";
        mValues[8] = "APP";

        for (int i = 0; i < mKeys.length; i++) {
            MicroPayInfo microPayInfo = new MicroPayInfo();
            microPayInfo.key = mKeys[i];
            microPayInfo.val = mValues[i];
            mList.add(microPayInfo);
        }
        MicroPayInfo microPayInfo = new MicroPayInfo();
        microPayInfo.key = "sign";
        microPayInfo.val = genAppSign(mList);
        mList.add(microPayInfo);

        MicroPayRequest microPayRequest = new MicroPayRequest();
        microPayRequest.getPrepayId(toXml(), new IHttpCallBack() {
            @Override
            public <T> void onSuccess(T obj, String result) {
                sendPayReq(decodeXml(result).get("prepay_id"));
            }

            @Override
            public void onError(String errorStr, int code) {
            }
        });

        OrderRequest.getInstance().updateOrder(2);//更新订单支付类型
    }

    private void sendPayReq(String prepayId) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "prepayId " + prepayId);
        }
        PayReq req = new PayReq();
        req.appId = CommonConst.getWechatAPPID(mContext);
        req.partnerId = CommonConst.getWechatMCHID(mContext);
        req.prepayId = prepayId;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        mReqValues = new String[mReqKeys.length];
        mReqValues[0] = req.appId;
        mReqValues[1] = req.nonceStr;
        mReqValues[2] = req.packageValue;
        mReqValues[3] = req.partnerId;
        mReqValues[4] = req.prepayId;
        mReqValues[5] = req.timeStamp;
        for (int i = 0; i < mReqKeys.length; i++) {
            MicroPayInfo microPayInfo = new MicroPayInfo();
            microPayInfo.key = mReqKeys[i];
            microPayInfo.val = mReqValues[i];
            mReqList.add(microPayInfo);
        }
        req.sign = genAppSign(mReqList);

        mApi.sendReq(req);
        mDialog.dismiss();
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genNonceStr() {
        Random random = new Random();
        return Md5Utils.getMD5(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genAppSign(List<MicroPayInfo> list) {
        StringBuilder sb = new StringBuilder();
        for (MicroPayInfo microPayInfo : list) {
            sb.append(microPayInfo.key);
            sb.append('=');
            sb.append(microPayInfo.val);
            sb.append('&');
        }
        sb.append("key=");
        sb.append(CommonConst.getWechatKey(mContext));
        String appSign = Md5Utils.getMD5(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    private String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (MicroPayInfo microPayInfo : mList) {
            sb.append("<" + microPayInfo.key + ">");
            sb.append(microPayInfo.val);
            sb.append("</" + microPayInfo.key + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
