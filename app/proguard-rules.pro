# Reglas para el APK release (R8/minify).
# Retrofit, OkHttp y kotlinx-serialization ya traen consumer rules; estas cubren
# los DTO propios y evitan que R8 elimine lo que se usa por reflexión/serialización.

# DTOs de la API (@Serializable): conservar nombres de campos para el JSON.
-keep class com.injuv.voluntariado.data.api.** { *; }

# Interfaces de Retrofit: los métodos se invocan por proxy en runtime.
-keep interface com.injuv.voluntariado.data.api.InjuvApi { *; }

# Modelos de dominio usados con Gson/serialización implícita o reflexión.
-keep class com.injuv.voluntariado.data.** { *; }

# kotlinx.serialization: conservar anotaciones y clases generadas.
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class com.injuv.voluntariado.** {
    *** Companion;
}
-dontwarn kotlinx.serialization.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
