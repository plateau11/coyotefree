# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
# Keep all classes and methods from Apache Batik
-keep class org.apache.batik.** { *; }

# Keep all Apache POI classes
-keep class org.apache.poi.** { *; }

# Keep all OSGi framework classes
-keep class org.osgi.framework.** { *; }

-keep class java.** { *; }

-keep class javax.** { *; }

-keep class net.** { *; }

-keep class com.ahmadullahpk.alldocumentreader.** { *; }

-keep class org.bouncycastle.** { *; }
-keep class org.conscrypt.** { *; }
-keep class org.openjsse.** { *; }

-keep class com.shockwave.**

-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**


# Suppress warnings (prevents ProGuard from failing)
-dontwarn org.apache.batik.**
-dontwarn org.apache.poi.**
-dontwarn org.osgi.framework.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn net.**
