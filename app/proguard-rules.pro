# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in H:\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keeppackagenames com.adpdigital.push.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep class android.os.Bundle { *; }

-keep class com.adpdigital.push.AdpPushClient { *; }
-keep class com.adpdigital.push.ForegroundManager { *; }
-keep class com.adpdigital.push.AppListener { *; }
-keep class com.adpdigital.push.PushMessage { *; }
-keep class com.adpdigital.push.ChabokNotification { *; }
-keep class com.adpdigital.push.DeliveryMessage { *; }
-keep class com.adpdigital.push.EventMessage { *; }
-keep class com.adpdigital.push.MessageSent { *; }
-keep class com.adpdigital.push.BadgeUpdate { *; }
-keep class com.adpdigital.push.Callback { *; }
-keep class com.adpdigital.push.NotificationHandler { *; }
-keep class com.adpdigital.push.ConnectionStatus { *; }