package com.bestapp.zipbab.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dagger.hilt.android.internal.managers.ViewComponentManager


private val DarkColorScheme = darkColorScheme(
    primary = MainColor,
    secondary = MainColorLight,
    tertiary = MainColorTransparent20,
    background = Black,
)

private val LightColorScheme = lightColorScheme(
    primary = MainColor,
    secondary = MainColorLight,
    tertiary = MainColorTransparent20,
    background = White,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val OnLightCustomColorsPalette = CustomColorsPalette(
    black = Black,
    white = White,
    defaultBackgroundColor = White,
    defaultForegroundColor = Black,
    drawableDefaultColor = DrawableDefaultColorDay,
    mainColor = MainColor,
    mainColorLight = MainColorLight,
    mainColorTransparent20 = MainColorTransparent20,
    mainColorTransparent50 = MainColorTransparent50,
    lightGray = LightGray,
    clr151718 = Clr151718,
    clr2E3034 = Clr2E3034,
    clr8A8A8A = Clr8A8A8A,
    temperatureMin0 = TemperatureMin0Day,
    temperatureMin30 = TemperatureMin30,
    temperatureMin40 = TemperatureMin40,
    temperatureMin60 = TemperatureMin60,
    temperatureMin80 = TemperatureMin80,
    temperatureTrack = TemperatureTrack,
    guidanceColor = GuidanceColor,
    modalBackgroundColor = ModalBackgroundColor,
    transparent = Transparent,
    gray = Gray,
    dividerColor = DividerColor,
    disabledColor = DisabledColorDay,
    clrD3D3D3 = ClrD3D3D3,
    clrC4E4F1 = ClrC4E4F1,
    viewLineGray = ViewLineGray,
    cardviewStroke = CardviewStroke,
    backgroundRightGray = BackgroundRightGray,
    clrEDF1F3 = ClrEDF1F3,
    backgroundRightBasic = BackgroundRightBasic,
)

val OnDarkCustomColorsPalette = CustomColorsPalette(
    black = Black,
    white = White,
    defaultBackgroundColor = Black,
    defaultForegroundColor = White,
    drawableDefaultColor = DrawableDefaultColorNight,
    mainColor = MainColor,
    mainColorLight = MainColorLight,
    mainColorTransparent20 = MainColorTransparent20,
    mainColorTransparent50 = MainColorTransparent50,
    lightGray = LightGray,
    clr151718 = Clr151718,
    clr2E3034 = Clr2E3034,
    clr8A8A8A = Clr8A8A8A,
    temperatureMin0 = TemperatureMin0Night,
    temperatureMin30 = TemperatureMin30,
    temperatureMin40 = TemperatureMin40,
    temperatureMin60 = TemperatureMin60,
    temperatureMin80 = TemperatureMin80,
    temperatureTrack = TemperatureTrack,
    guidanceColor = GuidanceColor,
    modalBackgroundColor = ModalBackgroundColor,
    transparent = Transparent,
    gray = Gray,
    dividerColor = DividerColor,
    disabledColor = DisabledColorNight,
    clrD3D3D3 = ClrD3D3D3,
    clrC4E4F1 = ClrC4E4F1,
    viewLineGray = ViewLineGray,
    cardviewStroke = CardviewStroke,
    backgroundRightGray = BackgroundRightGray,
    clrEDF1F3 = ClrEDF1F3,
    backgroundRightBasic = BackgroundRightBasic,
)

@Composable
fun ZipbabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val customColorsPalette = if (darkTheme) OnDarkCustomColorsPalette else OnLightCustomColorsPalette

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = if (view.context is ViewComponentManager.FragmentContextWrapper) {
//                (view.context as ViewComponentManager.FragmentContextWrapper).baseContext.
//            } else {
//                (view.context as Activity).window
//            }
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
//        }
//    }
    CompositionLocalProvider(LocalCustomColorsPalette provides customColorsPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = zipbabTypography,
            content = content
        )
    }
}