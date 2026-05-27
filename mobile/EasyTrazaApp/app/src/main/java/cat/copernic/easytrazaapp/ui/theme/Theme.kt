package cat.copernic.easytrazaapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


// 1. Configuramos el Tema Claro con los colores de tu Web
private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,               // Color principal (Botones, TopAppBar)
    onPrimary = TextWhite,                // Texto sobre el color principal

    secondary = BrownSecondary,           // Botones secundarios / Editar
    onSecondary = TextWhite,

    tertiary = BrownWarning,              // Detalles o alertas (Warning)
    onTertiary = TextDarkBrown,

    background = WebBackground,           // El fondo de TODA la aplicación (#efebe9)
    onBackground = TextDarkBrown,         // Texto sobre el fondo

    surface = WebSurface,                 // El fondo de las tarjetas (Cards) (#fff3e0)
    onSurface = TextDarkBrown,            // Texto dentro de las tarjetas

    error = BrownDelete,                  // Botones o textos de eliminar/error (#8d5a4a)
    onError = TextWhite
)

// Opcional: Puedes hacer un esquema oscuro,
// o simplemente usar el mismo para que la app no cambie de color si el móvil está en Modo Oscuro.
private val DarkColorScheme = darkColorScheme(
    primary = BrownPrimaryDarkTheme,       // Botones principales color café con leche
    onPrimary = TextDarkBrown,             // Texto marrón oscuro sobre los botones principales claros

    secondary = BrownSecondaryDarkTheme,   // Acciones secundarias
    onSecondary = TextDarkBrown,

    background = DarkBackground,           // Fondo de la app (#15100E)
    onBackground = TextLightBeige,         // Texto general en color crema suave

    surface = DarkSurface,                 // Tarjetas oscuras (#231A18)
    onSurface = TextLightBeige,            // Texto dentro de las tarjetas

    error = BrownDelete,
    onError = TextWhite
)

@Composable
fun EasyTrazaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Lo dejamos en false para obligar a usar tus colores web
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}