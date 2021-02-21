package eltech.semoevm.notes.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable


//private val DarkColorPalette = darkColorPalette(
//    primary = backgroundPrimaryDark, // цвет нижнего уровня
//    secondary = backgroundPrimaryElevatedDark, // цвет 2го уровня
//    onPrimary = textPrimaryDark, // цвет сновного текста
//    onSecondary = textSecondaryDark, // цвет второстепенного текста
//    surface = backgroundPrimaryDark
//)
//
//private val LightColorPalette = lightColorPalette(
//    primary = backgroundPrimaryLight, // цвет нижнего уровня
//    secondary = backgroundPrimaryElevatedLight, // цвет 2го уровня
//    onPrimary = textPrimaryLight, // цвет сновного текста
//    onSecondary = textSecondaryLight // цвет второстепенного текста
//)

@Composable
fun NotesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}