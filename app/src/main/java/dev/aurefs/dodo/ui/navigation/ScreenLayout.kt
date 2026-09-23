package dev.aurefs.dodo.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope

data class RouteChrome(val route: Routes, val onBack: () -> Unit)

val LocalRouteChrome = staticCompositionLocalOf<RouteChrome> {
    error("ScreenLayout used outside of a routeEntry")
}

// Registers an entry whose content can use ScreenLayout, which reads the route's layout config
inline fun <reified K : Routes> EntryProviderScope<Routes>.routeEntry(
    noinline onBack: () -> Unit,
    noinline content: @Composable (K) -> Unit,
) {
    entry<K> { key ->
        CompositionLocalProvider(LocalRouteChrome provides RouteChrome(key, onBack)) {
            content(key)
        }
    }
}

// The chrome (top bar, back button) is decided by the current route's RouteLayoutConfig;
// screens only fill in screen-specific slots
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLayout(
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val (route, onBack) = LocalRouteChrome.current

    Scaffold(
        topBar = {
            if (route !is Routes.Immersive) {
                TopAppBar(
                    title = { Text(route.title) },
                    navigationIcon = {
                        if (route.hasBackButton) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = actions,
                )
            }
        },
        floatingActionButton = floatingActionButton,
    ) { padding ->
        content(padding)
    }
}
