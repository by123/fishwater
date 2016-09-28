package com.sjy.ttclub.system;

/**
 * Created by linhz on 2015/11/29.
 * Email: linhaizhong@ta2she.com
 */
public class DeviceInfo {

    public String sdkVersionName;
    public int sdkVersionCode;
    //客户端唯一身份标识（我们生成，36位字符串）。在app生命周期内不发生变化（升级也不变），重新安装应该变化。
    public String uuid;
    //手机型号
    public String deviceName;
    //android设备id
    public String androidId;
    //设备唯一ID。尽可能的保证在一个设备上此id的唯一，不管应用是重新安装还是升级。
    public String deviceId;
    //生产商
    public String manufacturer;
    public String imei;
    public String simNo;
    public String macAdr;
    public String devToken;

    /*package*/DeviceInfo() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("sdkVersionName : ").append(sdkVersionName).append("\n");
        builder.append("deviceName : ").append(deviceName).append("\n");
        builder.append("androidId : ").append(androidId).append("\n");
        builder.append("manufacturer : ").append(manufacturer).append("\n");
        builder.append("imei : ").append(imei).append("\n");
        builder.append("simNo : ").append(simNo).append("\n");
        builder.append("macAdr : ").append(macAdr).append("\n");
        builder.append("UUID : ").append(uuid).append("\n");
        builder.append("deviceId : ").append(deviceId).append("\n");

        return builder.toString();
    }
}
