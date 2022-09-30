package com.ujjman.course.courseswap.helper


import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("ConflictingOnColor")
private val LightThemeColors = lightColors(
    primary = Blue300,
    primaryVariant = Blue400,
    onPrimary = Black2,
    secondary = Color.White,
    secondaryVariant = Yellow100,
    onSecondary = Color.Black,
    error = RedErrorDark,
    onError = RedErrorLight,
    background = Grey1,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Blue700,
)

@SuppressLint("ConflictingOnColor")
private val DarkThemeColors = darkColors(
    primary = Blue900,
    primaryVariant = Color.White,
    onPrimary = Color.White,
    secondary = Black1,
    secondaryVariant = Yellow200,
    onSecondary = Color.White,
    error = RedErrorLight,
    background = Color.Black,
    onBackground = Color.White,
    surface = Black1,
    onSurface = Blue500,
)
val BottomSheetShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
    ){
        content()
    }
}




























