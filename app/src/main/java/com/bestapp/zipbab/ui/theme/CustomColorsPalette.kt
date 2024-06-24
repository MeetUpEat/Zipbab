package com.bestapp.zipbab.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
    val black: Color = Color.Unspecified,
    val white: Color = Color.Unspecified,
    val defaultBackgroundColor: Color = Color.Unspecified,
    val defaultForegroundColor: Color = Color.Unspecified,
    val drawableDefaultColor: Color = Color.Unspecified,
    val mainColor: Color = Color.Unspecified,
    val mainColorLight: Color = Color.Unspecified,
    val mainColorTransparent20: Color = Color.Unspecified,
    val mainColorTransparent50: Color = Color.Unspecified,
    val lightGray: Color = Color.Unspecified,
    val clr151718: Color = Color.Unspecified,
    val clr2E3034: Color = Color.Unspecified,
    val clr8A8A8A: Color = Color.Unspecified,
    val temperatureMin0: Color = Color.Unspecified,
    val temperatureMin30: Color = Color.Unspecified,
    val temperatureMin40: Color = Color.Unspecified,
    val temperatureMin60: Color = Color.Unspecified,
    val temperatureMin80: Color = Color.Unspecified,
    val temperatureTrack: Color = Color.Unspecified,
    val guidanceColor: Color = Color.Unspecified,
    val modalBackgroundColor: Color = Color.Unspecified,
    val transparent: Color = Color.Unspecified,
    val gray: Color = Color.Unspecified,
    val dividerColor: Color = Color.Unspecified,
    val disabledColor: Color = Color.Unspecified,
    val clrD3D3D3: Color = Color.Unspecified,
    val clrC4E4F1: Color = Color.Unspecified,
    val viewLineGray: Color = Color.Unspecified,
    val cardviewStroke: Color = Color.Unspecified,
    val backgroundRightGray: Color = Color.Unspecified,
    val clrEDF1F3: Color = Color.Unspecified,
    val backgroundRightBasic: Color = Color.Unspecified,
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }