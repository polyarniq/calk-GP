-dontwarn javax.annotation.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keep class com.example.calkgp.model.** { *; }
-keep class com.example.calkgp.calculator.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
