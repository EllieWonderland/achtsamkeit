package com.elliewonderland.achtsamkeit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

private val GoogleProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = androidx.compose.ui.text.googlefonts.R.array.com_google_android_gms_fonts_certs,
)

private val InstrumentSerif = FontFamily(
    Font(
        googleFont = GoogleFont("Instrument Serif"),
        fontProvider = GoogleProvider,
        weight = FontWeight.Normal,
    ),
    Font(
        googleFont = GoogleFont("Instrument Serif"),
        fontProvider = GoogleProvider,
        weight = FontWeight.Normal,
        style  = FontStyle.Italic,
    ),
)

private val GeistSans = FontFamily(
    Font(googleFont = GoogleFont("Geist"), fontProvider = GoogleProvider, weight = FontWeight.Light),
    Font(googleFont = GoogleFont("Geist"), fontProvider = GoogleProvider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Geist"), fontProvider = GoogleProvider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Geist"), fontProvider = GoogleProvider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Geist"), fontProvider = GoogleProvider, weight = FontWeight.Bold),
)

val AppTypography = Typography(
    // ─── Display (Instrument Serif)
    displayLarge  = TextStyle(fontFamily = InstrumentSerif, fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = InstrumentSerif, fontSize = 36.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    displaySmall  = TextStyle(fontFamily = InstrumentSerif, fontSize = 32.sp, lineHeight = 36.sp, letterSpacing = (-0.4).sp),

    // ─── Headlines (Instrument Serif)
    headlineLarge  = TextStyle(fontFamily = InstrumentSerif, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = InstrumentSerif, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall  = TextStyle(fontFamily = InstrumentSerif, fontSize = 22.sp, lineHeight = 28.sp),

    // ─── Titles (Geist)
    titleLarge   = TextStyle(fontFamily = GeistSans, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleMedium  = TextStyle(fontFamily = GeistSans, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    titleSmall   = TextStyle(fontFamily = GeistSans, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium),

    // ─── Body (Geist)
    bodyLarge    = TextStyle(fontFamily = GeistSans, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium   = TextStyle(fontFamily = GeistSans, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall    = TextStyle(fontFamily = GeistSans, fontSize = 12.sp, lineHeight = 16.sp),

    // ─── Labels (Geist)
    labelLarge   = TextStyle(fontFamily = GeistSans, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    labelMedium  = TextStyle(fontFamily = GeistSans, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    labelSmall   = TextStyle(fontFamily = GeistSans, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp),
)

val SerifItalic = TextStyle(
    fontFamily = InstrumentSerif,
    fontStyle  = FontStyle.Italic,
    fontSize   = 24.sp,
    lineHeight = 32.sp,
)
