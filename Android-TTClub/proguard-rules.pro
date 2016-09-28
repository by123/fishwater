-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#-dontoptimize
#-dontobfuscate

-keep class org.apache.http.** {*; }

-keep class com.sjy.ttclub.BuildConfig

-keep class com.facebook.** { *; }
-dontwarn com.facebook.**
-keep class android.webkit.** { *; }

-keep class in.srain.cube.views.ptr.** { *; }
-dontwarn in.srain.cube.views.ptr.**

-keep class com.sjy.ttclub.bean.** { *; }

-keep class com.sjy.ttclub.widget.** { *; }
-dontwarn com.sjy.ttclub.widget.**


-keep class com.sjy.ttclub.loadmore.** { *; }
-dontwarn com.sjy.ttclub.loadmore.**

-dontwarn java.lang.**
-dontwarn org.codehaus.**
-dontwarn org.apache.commons.**
-dontwarn android.webkit.WebView

#UMENG
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class com.idea.fifaalarmclock.app.R$*{
    public static final int *;
}

-keep public class com.umeng.fb.ui.ThreadView {
}

-keep public class * extends com.umeng.**
#UMENG
#umeng socialize start
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
#umeng socialize end

##umeng push start
-keep class com.umeng.message.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.protobuffer.* {
        public <fields>;
        public <methods>;
}

-keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.local.* {
        public <fields>;
        public <methods>;
}
-keep class org.android.agoo.impl.*{
        public <fields>;
        public <methods>;
}

-dontwarn com.xiaomi.**

-dontwarn com.ut.mini.**

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}

-keep public class com.lsym.ttclub.R$*{
    public static final int *;
}
##umeng push end

#GSON
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; } #解决JSON解析问题
-dontwarn sun.misc.Unsafe
#-keep class com.google.gson.stream.** { *; }

-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *; }
#GSON

#ALIPAY
-keep class com.alipay.android.app.IALiPay{*;}
-keep class com.alipay.android.app.IALixPay{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}

-dontwarn com.alipay.android.app.**
#ALIPAY

#SHARE FOR QQ&WECHAT&WEIBO
-keep class com.tencent.** { *; }
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
#SHARE FOR QQ&WECHAT&WEIBO

#SINAWEIBO
-dontwarn com.weibo.sdk.android.WeiboDialog
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-keep public class android.net.http.SslError{
     *;
}

-keep public class android.webkit.WebViewClient{
    *;
}

-keep public class android.webkit.WebChromeClient{
    *;
}

-keep public interface android.webkit.WebChromeClient$CustomViewCallback {
    *;
}

-keep public interface android.webkit.ValueCallback {
    *;
}

-keep class * implements android.webkit.WebChromeClient {
    *;
}
#SINAWEIBO

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclasseswithmembers class * { @android.webkit.JavascriptInterface <methods>; }

-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
}