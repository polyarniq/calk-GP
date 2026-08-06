-dontwarn javax.annotation.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keep class com.polyarniq.calkgp.model.** { *; }
-keep class com.polyarniq.calkgp.calculator.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
