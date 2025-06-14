# Add project-specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JavaScript, uncomment the following
# line and specify the fully qualified class name of the JavaScript interface
# class. This prevents ProGuard from obfuscating the interface methods.
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment the following line to preserve line number information.
# This is useful for debugging stack traces, allowing you to trace errors
# back to the exact line in your source code.
#-keepattributes SourceFile,LineNumberTable

# If you choose to keep the line number information, uncomment the next line
# to hide the original source file name in the stack traces. This is helpful
# for obfuscation, but it may make debugging more difficult.
#-renamesourcefileattribute SourceFile

# Keep classes that are used for reflection. This prevents ProGuard from 
# removing them, which can lead to runtime exceptions.
#-keep class com.example.** { *; }

# If you are using Retrofit or similar libraries, keep the necessary classes
# to ensure they work correctly during runtime.
#-keep class retrofit2.** { *; }
#-keep class okhttp3.** { *; }

# Keep your application class to ensure it is not removed or renamed.
# This is important if you are using dependency injection or custom application logic.
#-keep public class * extends android.app.Application

# Keep any classes annotated with @Keep to preserve their names and members.
# This is useful when using libraries that rely on reflection.
#-keep @androidx.annotation.Keep class * { *; }

# Add any other necessary rules below as your project evolves.
