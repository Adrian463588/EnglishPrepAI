# DevSecOps R8 / ProGuard Hardening Rules — English Prep AI
-repackageclasses 'o'
-allowaccessmodification
-renamesourcefileattribute "App"
-keepattributes Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations

# Strip verbose/debug/info logs from release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# JNI / Native edge inference runtime boundaries
-keep class com.google.ai.edge.litertlm.** { *; }
-keep class ai.onnxruntime.** { *; }

# Keep @Serializable DTOs & generated serializer companions without exposing pure domain/scoring logic
-keepattributes *Annotation*
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.englishprep.ai.ui.navigation.AppRoute** { *; }
-keepclasseswithmembers class com.englishprep.ai.data.room.**Dto* { *; }
-keepclasseswithmembers class com.englishprep.ai.data.repository.Backup** { *; }
-keepclasseswithmembers class com.englishprep.ai.domain.models.QuestionItem { *; }
-keepclasseswithmembers class com.englishprep.ai.domain.models.PassageItem { *; }
-keepclasseswithmembers class com.englishprep.ai.domain.models.MediaAssetItem { *; }
-keepclasseswithmembers class com.englishprep.ai.domain.models.MatchingPairSpec { *; }

-dontwarn java.awt.**
