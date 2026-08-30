# Add project specific ProGuard rules here.
# Keep Retrofit models & serialization
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.jobradar.app.**$$serializer { *; }
-keepclassmembers class com.jobradar.app.** { *** Companion; }
-keepclasseswithmembers class com.jobradar.app.** { kotlinx.serialization.KSerializer serializer(...); }

# Retrofit
-keepattributes Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * { @retrofit2.http.* <methods>; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
