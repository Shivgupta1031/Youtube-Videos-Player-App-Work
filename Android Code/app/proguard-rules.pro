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

-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Dagger 2 / Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclassmembers class * {
    @dagger.hilt.* <fields>;
}
-keepclassmembers class * {
    @dagger.hilt.* <methods>;
}
-keepclassmembers class * {
    @javax.inject.* *;
}
-keepclassmembers class * {
    @dagger.* *;
}

# Coroutines
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {
    public <init>(**);
}

# Retrofit and Gson
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Lifecycle and LiveData
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.runtime.** { *; }

-keep class androidx.lifecycle.LiveData { *; }

# Navigation
-keep class androidx.navigation.** { *; }
-keep class androidx.hilt.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep class android.arch.persistence.room.** { *; }
-keepclassmembers class * implements androidx.room.RoomDatabase* {
    public static <methods>;
}
-keepclassmembers class * {
    @androidx.room.Dao *;
}
-keepclasseswithmembers class * {
    @androidx.room.* <fields>;
}
-keepclasseswithmembers class * {
    @androidx.room.* <methods>;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Additional rules for Hilt Navigation Compose
-keep class dagger.hilt.android.internal.builders.*

# Keep the classes that extend ViewModel or AndroidViewModel
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel

# Keep the Parcelable implementation classes
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep the Retrofit interfaces
-keep interface retrofit2.** { *; }
-keep class retrofit2.** { *; }

# Keep the Gson classes
-keep class com.google.gson.** { *; }

# Keep the Room database classes
-keep class androidx.room.** { *; }

# Keep the Dagger 2 classes
-keep class dagger.** { *; }

-keep class com.devshiv.ytchannel.model.** { *; }
-keepclassmembers class com.devshiv.ytchannel.model.** { *; }

-keep class com.devshiv.ytchannel.viewmodels.** { *; }
-keep class com.devshiv.ytchannel.repository.** { *; }