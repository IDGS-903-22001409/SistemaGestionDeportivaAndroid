package navegacion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Surface,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Surface,

    secondary = Secondary,
    onSecondary = Surface,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = Surface,

    tertiary = Info,
    onTertiary = Surface,

    error = Error,
    onError = Surface,
    errorContainer = Color(0xFFFED7D7),
    onErrorContainer = Color(0xFF742A2A),

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = CardBorder,
    outlineVariant = DividerColor,

    scrim = Color(0x80000000)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = SecondaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Surface,

    secondary = Color(0xFF4A5568),
    onSecondary = Surface,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = Surface,

    tertiary = Info,
    onTertiary = SecondaryDark,

    error = Error,
    onError = SecondaryDark,

    background = SecondaryDark,
    onBackground = Surface,

    surface = Secondary,
    onSurface = Surface,
    surfaceVariant = Color(0xFF4A5568),
    onSurfaceVariant = Color(0xFFCBD5E0),

    outline = Color(0xFF4A5568),
    outlineVariant = Color(0xFF2D3748)
)

@Composable
fun TorneoManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
