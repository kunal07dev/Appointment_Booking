package com.dr.booking_client.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp),
    titleLarge   = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium  = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyLarge    = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyMedium   = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
    labelLarge   = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall   = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)