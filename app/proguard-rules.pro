# Keep line numbers for readable crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Firestore data model classes ──────────────────────────────────────────────
# toObjects(Entry::class.java) uses reflection; R8 must not strip these fields.
-keep class com.elliewonderland.achtsamkeit.model.** { *; }
-keepclassmembers class com.elliewonderland.achtsamkeit.model.** { *; }

# ── kotlinx.serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.elliewonderland.achtsamkeit.**$$serializer { *; }
-keepclassmembers class com.elliewonderland.achtsamkeit.** {
    *** Companion;
}
-keepclasseswithmembers class com.elliewonderland.achtsamkeit.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Firebase / Google ─────────────────────────────────────────────────────────
# Firebase ships its own consumer rules; these cover any remaining gaps.
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ── RevenueCat ────────────────────────────────────────────────────────────────
# RevenueCat ships consumer ProGuard rules via the AAR; nothing extra needed.
-dontwarn com.revenuecat.**

# ── Vico charts ───────────────────────────────────────────────────────────────
-dontwarn com.patrykandpatrick.vico.**
